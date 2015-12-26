package jvm;

import java.nio.ByteBuffer;
import jvm.frame.Frame;
import jvm.values.*;
import org.apache.bcel.classfile.*;

/**
 *
 * @author Jaroslav Ševčík
 */
public class Heap {

    private static final int HEAP_SIZE = (int) Math.pow(2, 20) + 1;
//    private static final int HEAP_SIZE = 129;
    public final ByteBuffer heap = ByteBuffer.allocate(HEAP_SIZE);
    public static final int OBJECT_HEAD_SIZE = 12;
    public static final int ARRAY_HEAD_SIZE = 16;
    public static final int LENGTH_OFFSET = 12;
    public static final int CLASS_REF_OFFSET = 8;
    public static final int GC_FLAG_OFFSET = 4;
    public static final int SIZE_OFFSET = 0;
    public int freeList = 1;// halda začíná z technických důvodů od jedničky

    public Heap() {
        heap.putInt(1, HEAP_SIZE - 1);// velikost volného bloku - na začátku celá halda
        heap.putInt(5, -1);// Druhá položka je index dalšího volného bloku. - -1 znamená, že je to konec free listu.
    }

    public ReferenceValue allocateObject(ReferenceValue classRef) throws Exception {
        int objRef;
        int freeBlockRef;
        int size = OBJECT_HEAD_SIZE;
        int[] freeBlock;
        JavaClass clazz = jvm.JVM.getJavaClassByIndex(classRef);

        if (clazz.getSuperclassName() != null) {
            String superClassName = ((ConstantUtf8) clazz.getConstantPool().getConstant(((ConstantClass) clazz.getConstantPool().getConstant(clazz.getSuperclassNameIndex())).getNameIndex())).getBytes();
            size += getSuperclassFieldsSize(jvm.JVM.getJavaClass(superClassName));
        }

        for (Field field : clazz.getFields()) {
            if (!field.isStatic()) {
                size += getTypeSize(field.getType().getType());
            }
        }
        size += (8 - size % 8) % 8;// zarovnání na 8 bytů(Potřebuji vždy aspoň 8 bytů velký free blok na zapsání infa o velikosti a odkaz na další free blok.)
        freeBlock = findFreeSpace(size);
        objRef = freeBlock[0];
        freeBlockRef = freeBlock[1];
        int sizeOfFreeBlock = heap.getInt(objRef);
        int nextFreeBlock = heap.getInt(objRef + 4);
        if (sizeOfFreeBlock >= size + 8) {
            int newFreeBlockIndex = objRef + size;
            heap.putInt(newFreeBlockIndex, sizeOfFreeBlock - size);
            if (freeBlockRef == -1) {
                freeList = newFreeBlockIndex;
            } else {
                heap.putInt(freeBlockRef, newFreeBlockIndex);
            }
            heap.putInt(newFreeBlockIndex + 4, nextFreeBlock);
        } else {
            if (freeBlockRef == -1) {
                freeList = nextFreeBlock;
            } else {
                heap.putInt(freeBlockRef, nextFreeBlock);
            }

        }
        heap.putInt(objRef, size);
        heap.putInt(objRef + 4, 0);// GC flag
        heap.putInt(objRef + 8, classRef.getValue());//odkaz na třídu
        for (int i = objRef + OBJECT_HEAD_SIZE; i < (objRef + size); i++) {
            heap.put(i, (byte) 0);
        }

        return new ReferenceValue(objRef);
    }

    private int getSuperclassFieldsSize(JavaClass superClass) throws Exception {
        int fieldsSize = 0;

//        if (superClass.getClassName().equals("initclasses.Object")) {
//            return 0;
//        }
        if (superClass.getClassName().equals("java.lang.Object")) {
            return 0;
        }

        if (superClass.getSuperclassName() != null) {
            String superClassName = ((ConstantUtf8) superClass.getConstantPool().getConstant(((ConstantClass) superClass.getConstantPool().getConstant(superClass.getSuperclassNameIndex())).getNameIndex())).getBytes();
            fieldsSize = getSuperclassFieldsSize(jvm.JVM.getJavaClass(superClassName));
        }

        for (Field field : superClass.getFields()) {
            if (!field.isStatic()) {
                fieldsSize += getTypeSize(field.getType().getType());
            }
        }
        return fieldsSize;
    }

    public int getTypeSize(byte type) throws Exception {
//        System.out.println(type);
        switch (type) {
            case 10:
                return 4;//int
            case 5:
                return 2;//char
            case 13:
                return 4;//array ref
            case 14:
                return 4;//object ref
            default:
                throw new Exception("Neznámý typ při alokaci fieldu: " + type);
        }
    }

    public int getClassIndex(ReferenceValue ref) {
        return heap.getInt(ref.getValue() + CLASS_REF_OFFSET);
    }

    public ReferenceValue allocateArray(int length, int sizeOfElement, int atype) throws Exception {
        int size = ARRAY_HEAD_SIZE + length * sizeOfElement;
        int[] freeBlock = findFreeSpace(size);
        int arrayRef = freeBlock[0];
        int freeBlockRef = freeBlock[1];
        int sizeOfFreeBlock = heap.getInt(arrayRef);
        int nextFreeBlock = heap.getInt(arrayRef + 4);

        size += (8 - size % 8) % 8;// zarovnání na 8 bytů(Potřebuji vždy aspoň 8 bytů velký free blok na zapsání infa o velikosti a odkaz na další free blok.)
        if (sizeOfFreeBlock >= size + 8) {
            int newFreeBlockIndex = arrayRef + size;
            heap.putInt(newFreeBlockIndex, sizeOfFreeBlock - size);
            if (freeBlockRef == -1) {
                freeList = newFreeBlockIndex;
            } else {
                heap.putInt(freeBlockRef, newFreeBlockIndex);
            }
            heap.putInt(newFreeBlockIndex + 4, nextFreeBlock);
        } else {
            if (freeBlockRef == -1) {
                freeList = nextFreeBlock;
            } else {
                heap.putInt(freeBlockRef, nextFreeBlock);
            }
        }

        heap.putInt(arrayRef, size);// size
        heap.putInt(arrayRef + 4, 0);// GC flag
        heap.putInt(arrayRef + 8, atype);// element type
        heap.putInt(arrayRef + 12, length);// length
        for (int i = arrayRef + ARRAY_HEAD_SIZE; i < (arrayRef + size); i++) {
            heap.put(i, (byte) 0);
        }

        return new ReferenceValue(arrayRef);
    }

    public IntValue fetchInt(ReferenceValue objRef, int offset) {
        int value = heap.getInt(objRef.getValue() + offset);
        return new IntValue(value);
    }

    public CharValue fetchChar(ReferenceValue objRef, int offset) {
        char value = heap.getChar(objRef.getValue() + offset);
        return new CharValue(value);
    }

    public ReferenceValue fetchRef(ReferenceValue objRef, int offset) {
        int value = heap.getInt(objRef.getValue() + offset);
        return new ReferenceValue(value);
    }

    public void storeInt(IntValue v, ReferenceValue objRef, int offset) {
        heap.putInt(objRef.getValue() + offset, v.getValue());
    }

    public void storeChar(CharValue v, ReferenceValue objRef, int offset) {
        heap.putChar(objRef.getValue() + offset, v.getValue());
    }

    public void storeRef(ReferenceValue v, ReferenceValue objRef, int offset) {
        heap.putInt(objRef.getValue() + offset, v.getValue());
    }

    public void storeIntToArray(IntValue v, ReferenceValue arrayRef, IntValue index) throws Exception {
        int length = heap.getInt(arrayRef.getValue() + LENGTH_OFFSET);
        if (index.getValue() >= length) {
            throw new Exception("Index je větší než velikost pole!");
        } else if (index.getValue() < 0) {
            throw new Exception("Index je záporný!");
        }
        heap.putInt(arrayRef.getValue() + ARRAY_HEAD_SIZE + index.getValue() * 4, v.getValue());
    }

    public void storeCharToArray(CharValue v, ReferenceValue arrayRef, IntValue index) throws Exception {
        int length = heap.getInt(arrayRef.getValue() + LENGTH_OFFSET);
        if (index.getValue() >= length) {
            throw new Exception("Index je větší než velikost pole!");
        } else if (index.getValue() < 0) {
            throw new Exception("Index je záporný!");
        }
        heap.putChar(arrayRef.getValue() + ARRAY_HEAD_SIZE + index.getValue() * 2, v.getValue());
    }

    public void storeRefToArray(ReferenceValue v, ReferenceValue arrayRef, IntValue index) throws Exception {
        int length = heap.getInt(arrayRef.getValue() + LENGTH_OFFSET);
        if (index.getValue() >= length) {
            throw new Exception("Index je větší než velikost pole!");
        } else if (index.getValue() < 0) {
            throw new Exception("Index je záporný!");
        }
        heap.putInt(arrayRef.getValue() + ARRAY_HEAD_SIZE + index.getValue() * 4, v.getValue());
    }

    public IntValue fetchIntFromArray(ReferenceValue arrayRef, IntValue index) throws Exception {
        int length = heap.getInt(arrayRef.getValue() + LENGTH_OFFSET);
        int value;
        if (index.getValue() >= length) {
            throw new Exception("Index je větší než velikost pole!");
        } else if (index.getValue() < 0) {
            throw new Exception("Index je záporný!");
        }
        value = heap.getInt(arrayRef.getValue() + ARRAY_HEAD_SIZE + index.getValue() * 4);
        return new IntValue(value);
    }

    public CharValue fetchCharFromArray(ReferenceValue arrayRef, IntValue index) throws Exception {
        int length = heap.getInt(arrayRef.getValue() + LENGTH_OFFSET);
        char value;
        if (index.getValue() >= length) {
            throw new Exception("Index je větší než velikost pole!");
        } else if (index.getValue() < 0) {
            throw new Exception("Index je záporný!");
        }
        value = heap.getChar(arrayRef.getValue() + ARRAY_HEAD_SIZE + index.getValue() * 2);
        return new CharValue(value);
    }

    public ReferenceValue fetchRefFromArray(ReferenceValue arrayRef, IntValue index) throws Exception {
        int length = heap.getInt(arrayRef.getValue() + LENGTH_OFFSET);
        int value;
        if (index.getValue() >= length) {
            throw new Exception("Index je větší než velikost pole!");
        } else if (index.getValue() < 0) {
            throw new Exception("Index je záporný!");
        }
        value = heap.getInt(arrayRef.getValue() + ARRAY_HEAD_SIZE + index.getValue() * 4);
        return new ReferenceValue(value);
    }

    public void dumbHeap() {
        System.out.println("\nheap dump:");
        for (int i = 1; i < 257; i++) {
            System.out.print(jvm.JVM.unsignedToBytes(heap.get(i)) + " ");
            if (i % 4 == 0) {
                System.out.print("  ");
            }
        }
        System.out.println("\n\n");
    }

    private int[] findFreeSpace(int size) throws Exception {
        int ptr = -1;
        int freeBlock;

        if (freeList == -1) {
            collectGarbage();
            if (freeList == -1) {
                throw new OutOfMemoryError("Došlo místo na haldě!");
            }
        }
        freeBlock = freeList;
        while (heap.getInt(freeBlock) < size && heap.getInt(freeBlock + 4) != -1) {
            ptr = freeBlock + 4;
            freeBlock = heap.getInt(ptr);
        }
        if (heap.getInt(freeBlock) < size) {
            collectGarbage();
            ptr = -1;
            freeBlock = freeList;
            while (heap.getInt(freeBlock) < size && heap.getInt(freeBlock + 4) != -1) {
                ptr = freeBlock + 4;
                freeBlock = heap.getInt(ptr);
            }
            if (heap.getInt(freeBlock) < size) {
                throw new OutOfMemoryError("Došlo místo na haldě!");
            }
        }
        int[] result = new int[2];
        result[0] = freeBlock;
        result[1] = ptr;
        return result;
    }

    private void collectGarbage() throws Exception {
        System.out.println("**********************************collect garbage**************************");
        for (Frame frame : JVM.frameStack) {
            for (Value val : frame.getLocalVariables()) {
                if (val != null && val.getTag() == Value.REFERENCE_TAG && ((ReferenceValue) val).getValue() != 0) {
                    mark(((ReferenceValue) val).getValue());
                }
            }
            for (Value val : frame.getOperandStack()) {
                if (val != null && val.getTag() == Value.REFERENCE_TAG && ((ReferenceValue) val).getValue() != 0) {
                    mark(((ReferenceValue) val).getValue());
                }
            }
        }
        sweep();
        joinAdjacentFreeBlocks();
        dumbHeap();
    }

    private void mark(int objRef) throws Exception {
        int fieldOffset = 0;
        if (heap.getInt(objRef + GC_FLAG_OFFSET) == 1) {
            return;
        }
        heap.putInt(objRef + GC_FLAG_OFFSET, 1);
        JavaClass clazz = jvm.JVM.getJavaClassByIndex(new ReferenceValue(heap.getInt(objRef + CLASS_REF_OFFSET)));

        if (clazz.getSuperclassName() != null) {
            String superClassName = ((ConstantUtf8) clazz.getConstantPool().getConstant(((ConstantClass) clazz.getConstantPool().getConstant(clazz.getSuperclassNameIndex())).getNameIndex())).getBytes();
            fieldOffset += markSuperclassFields(jvm.JVM.getJavaClass(superClassName), objRef);
        }

        for (Field field : clazz.getFields()) {
            if (field.getType().getType() == 14/*object ref*/ && heap.getInt(objRef + OBJECT_HEAD_SIZE + fieldOffset) != 0) {
                mark(heap.getInt(objRef + OBJECT_HEAD_SIZE + fieldOffset));
            }
            fieldOffset += getTypeSize(field.getType().getType());
        }
    }

    private int markSuperclassFields(JavaClass superClass, int objRef) throws Exception {
        int fieldOffset = 0;

//        if (superClass.getClassName().equals("initclasses.Object")) {
//            return 0;
//        }
        if (superClass.getClassName().equals("java.lang.Object")) {
            return 0;
        }

        if (superClass.getSuperclassName() != null) {
            String superClassName = ((ConstantUtf8) superClass.getConstantPool().getConstant(((ConstantClass) superClass.getConstantPool().getConstant(superClass.getSuperclassNameIndex())).getNameIndex())).getBytes();
            fieldOffset = markSuperclassFields(jvm.JVM.getJavaClass(superClassName), objRef);
        }

        for (Field field : superClass.getFields()) {
            if (field.getType().getType() == 14/*object ref*/ && heap.getInt(objRef + fieldOffset) != 0) {
                mark(heap.getInt(objRef + fieldOffset));
            }
            fieldOffset += getTypeSize(field.getType().getType());
        }
        return fieldOffset;
    }

    private void sweep() {
        int objRef;
        int nextFreeBlock;
        int objSize;
        int previousFreeBlock = -1;

        if (freeList == 1) {
            objRef = heap.getInt(1) + 1;
            nextFreeBlock = heap.getInt(5);
        } else {
            objRef = 1;
            nextFreeBlock = freeList;
        }

        while (objRef < HEAP_SIZE) {
            objSize = heap.getInt(objRef);
            if (heap.getInt(objRef + GC_FLAG_OFFSET) == 0) {
                if (previousFreeBlock == -1) {
                    heap.putInt(objRef + 4, freeList);
                    freeList = objRef;
                } else {
                    heap.putInt(objRef + 4, heap.getInt(previousFreeBlock + 4));
                    heap.putInt(previousFreeBlock + 4, objRef);
                }
                previousFreeBlock = objRef;
                objRef += objSize;
            } else {
                heap.putInt(objRef + GC_FLAG_OFFSET, 0);
                objRef += objSize;
            }
            if (objRef == nextFreeBlock) {
                objRef += heap.getInt(objRef);// + velikost free bloku
                previousFreeBlock = nextFreeBlock;
                nextFreeBlock = heap.getInt(nextFreeBlock + 4);
            }
        }
    }

    private void joinAdjacentFreeBlocks() {
        int freeBlock = freeList;
        int freeBlockSize;
        int nextFreeBlock;

        while (freeBlock != -1) {
            freeBlockSize = heap.getInt(freeBlock);
            nextFreeBlock = heap.getInt(freeBlock + 4);
            if (freeBlock + freeBlockSize == nextFreeBlock) {
                heap.putInt(freeBlock, freeBlockSize + heap.getInt(nextFreeBlock));
                heap.putInt(freeBlock + 4, heap.getInt(nextFreeBlock + 4));
            } else {
                freeBlock = nextFreeBlock;
            }
        }
    }
    
    public IntValue getArrayLength(ReferenceValue arrayRef) {
        return new IntValue(heap.getInt(arrayRef.getValue() + LENGTH_OFFSET));
    }
}
