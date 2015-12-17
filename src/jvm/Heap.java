package jvm;

import java.nio.ByteBuffer;
import jvm.values.*;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author Jaroslav Ševčík
 */
public class Heap {

    private static final int HEAP_SIZE = (int) Math.pow(2, 20);
    public final ByteBuffer heap = ByteBuffer.allocate(HEAP_SIZE);
    public static final int OBJECT_HEAD_SIZE = 4;
    public static final int ARRAY_HEAD_SIZE = 8;
    //Alokuje se za sebe a adekvátně se posune index prvního volného místa.
    public int firstFree = 0;

    public ReferenceValue allocateObject(ReferenceValue classRef) throws Exception {
        int ptr = firstFree;
        JavaClass clazz = jvm.JVM.getJavaClassByIndex(classRef);
        //ještě tu budou nějaký flagy
        heap.putInt(firstFree, classRef.getValue());//odkaz na třídu
        firstFree += 4;
        
        if (clazz.getSuperclassName() != null) {
            String superClassName = ((ConstantUtf8) clazz.getConstantPool().getConstant(((ConstantClass) clazz.getConstantPool().getConstant(clazz.getSuperclassNameIndex())).getNameIndex())).getBytes();
            allocateSuperclassFields(jvm.JVM.getJavaClass(superClassName));
        }

        for (Field field : clazz.getFields()) {
            allocateType(field.getType().getType());
        }
        return new ReferenceValue(ptr);
    }

    private void allocateSuperclassFields(JavaClass superClass) throws Exception {
        
        if (superClass.getClassName().equals("initclasses.Object")) {
            return;
        }

        if (superClass.getSuperclassName() != null) {
            String superClassName = ((ConstantUtf8) superClass.getConstantPool().getConstant(((ConstantClass) superClass.getConstantPool().getConstant(superClass.getSuperclassNameIndex())).getNameIndex())).getBytes();
            allocateSuperclassFields(jvm.JVM.getJavaClass(superClassName));
        }

        for (Field field : superClass.getFields()) {
            allocateType(field.getType().getType());
        }
    }

    private void allocateType(byte type) throws Exception {
//        System.out.println(type);
        switch (type) {
            case 10:
                firstFree += 4;
                break;//int
            case 5:
                firstFree += 2;
                break;//char
            case 13:
                firstFree += 4;
                break;//array ref
            default:
                throw new Exception("Neznámý typ při alokaci fieldu");
        }
    }

    public ReferenceValue allocateArray(int length, int sizeOfElement, int atype) {
        int ptr = firstFree;
        heap.putInt(firstFree, atype);
        firstFree += 4;
        heap.putInt(firstFree, length);
        firstFree += length * sizeOfElement + 4;
        return new ReferenceValue(ptr);
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
    
    public void storeIntToArray (IntValue v, ReferenceValue arrayRef, IntValue index) throws Exception {
        int length = heap.getInt(arrayRef.getValue() + 4);
        if (index.getValue() >= length) {
            throw new Exception("Index je větší než velikost pole!");
        } else if (index.getValue() < 0) {
            throw new Exception("Index je záporný!");
        }
        heap.putInt(arrayRef.getValue() + ARRAY_HEAD_SIZE + index.getValue() * 4, v.getValue());
    }
    
    public IntValue fetchIntFromArray (ReferenceValue arrayRef, IntValue index) throws Exception {
        int length = heap.getInt(arrayRef.getValue() + 4);
        int value;
        if (index.getValue() >= length) {
            throw new Exception("Index je větší než velikost pole!");
        } else if (index.getValue() < 0) {
            throw new Exception("Index je záporný!");
        }
        value = heap.getInt(arrayRef.getValue() + ARRAY_HEAD_SIZE + index.getValue() * 4);
        return new IntValue(value);
    }

    public void dumbHeap() {
        System.out.println("\nheap dump:");
        for (int i = 0; i < firstFree; i++) {
            System.out.print(jvm.JVM.unsignedToBytes(heap.get(i)));
            if ((i + 1) % 4 == 0) {
                System.out.print(" ");
            }
        }
        System.out.println("\n\n\n\n\n\n");
    }
}
