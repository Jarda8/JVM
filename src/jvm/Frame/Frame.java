package jvm.frame;

import jvm.values.Value;
import java.util.ArrayDeque;
import java.util.Deque;
import jvm.Heap;
import jvm.JVM;
import static jvm.JVM.unsignedToBytes;
import jvm.values.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.Type;

/**
 *
 * @author Jaroslav Ševčík
 */
public class Frame {

    private int pc = 0;
    private final Deque<Value> operandStack;
    private final Value[] localVariables;
    private final ConstantPool constantPool;
    private final byte[] code;
    private final Frame invoker;

    public Frame(Method method, Value[] arguments, ReferenceValue thisHeapIndex, Frame invoker) throws Exception {
        //System.out.println("Vytváří se frame metody: " + method.getName());
        this.operandStack = new ArrayDeque<>(method.getCode().getMaxStack());
        this.localVariables = new Value[method.getCode().getMaxLocals()];
        this.constantPool = method.getConstantPool();
        this.code = method.getCode().getCode();
        this.invoker = invoker;

        Type[] argumentTypes = method.getArgumentTypes();
        int i = 0, j = 0;
        if (!method.isStatic()) {
            localVariables[0] = thisHeapIndex;
            j++;
        }

        if (arguments != null) {
            for (; i < argumentTypes.length; i++, j++) {
                localVariables[j] = arguments[i];
            }
        }
        /*System.out.println("Vytvořil se frame metody: " + method.getName());

        System.out.println("Bytecode metody:");
        for (int k = 0; k < method.getCode().getCode().length; k++) {
            System.out.println(unsignedToBytes(method.getCode().getCode()[k]));
        }*/
    }

    public Value[] getLocalVariables() {
        return localVariables;
    }

    public Deque<Value> getOperandStack() {
        return operandStack;
    }

    public void pushOnStack(Value val) {
        operandStack.push(val);
    }

    public void popFromStack() {
        operandStack.pop();
    }

    public void start() throws Exception {
        //System.out.println("Provádí se kód metody.");

        //vykonávání bytecodu
        do {
            switch (code[pc]) {
                case 0x10:
                    bipush();
                    break;
                case 0x2:
                    iconst_m1();
                    break;
                case 0x3:
                    iconst_0();
                    break;
                case 0x4:
                    iconst_1();
                    break;
                case 0x5:
                    iconst_2();
                    break;
                case 0x6:
                    iconst_3();
                    break;
                case 0x7:
                    iconst_4();
                    break;
                case 0x8:
                    iconst_5();
                    break;
                case 0x3b:
                    istore_0();
                    break;
                case 0x3c:
                    istore_1();
                    break;
                case 0x3d:
                    istore_2();
                    break;
                case 0x3e:
                    istore_3();
                    break;
                case (byte) 0xb1:
                    ret();
                    break;
                case (byte) 0xac:
                    iret();
                    break;
                case (byte) 0xb0:
                    aret();
                    break;
                case (byte) 0xbb:
                    neww();
                    break;
                case 0x59:
                    dup();
                    break;
                case (byte) 0xb7:
                    invokespecial();
                    break;
                case (byte) 0xb8:
                    invokestatic();
                    break;
                case (byte) 0xb6:
                    invokevirtual();
                    break;   
                case 0x2a:
                    aload_0();
                    break;
                case 0x2b:
                    aload_1();
                    break;
                case 0x2c:
                    aload_2();
                    break;
                case 0x2d:
                    aload_3();
                    break;
                case 0x1a:
                    iload_0();
                    break;
                case 0x1b:
                    iload_1();
                    break;
                case 0x1c:
                    iload_2();
                    break;
                case 0x1d:
                    iload_3();
                    break;
                case (byte) 0xb5:
                    putfield();
                    break;
                case 0x4b:
                    astore_0();
                    break;
                case 0x4c:
                    astore_1();
                    break;
                case 0x4d:
                    astore_2();
                    break;
                case 0x4e:
                    astore_3();
                    break;
                case 0x11:
                    sipush();
                    break;
                case (byte) 0xbc:
                    newarray();
                    break;
                case (byte) 0x4f:
                    iastore();
                    break;
                case (byte) 0x53:
                    aastore();
                    break;
                case (byte) 0xb4:
                    getfield();
                    break;
                case (byte) 0x2e:
                    iaload();
                    break;
                case (byte) 0x60:
                    iadd();
                    break;
                case (byte) 0x64:
                    isub();
                    break;
                case (byte) 0x68:
                    imul();
                    break;
                case (byte) 0x6c:
                    idiv();
                    break;
                case (byte) 0xa7:
                    gotoo();
                    break;
                case (byte) 0x99:
                    ifeq();
                    break;
                case (byte) 0x9a:
                    ifne();
                    break;
                case (byte) 0x9b:
                    iflt();
                    break;
                case (byte) 0x9c:
                    ifge();
                    break;
                case (byte) 0x9d:
                    ifgt();
                    break;
                case (byte) 0x9e:
                    ifle();
                    break;
                case (byte) 0x9f:
                    if_icmpeq();
                    break;
                case (byte) 0xa0:
                    if_icmpne();
                    break;
                case (byte) 0xa1:
                    if_icmplt();
                    break;
                case (byte) 0xa2:
                    if_icmpge();
                    break;
                case (byte) 0xa3:
                    if_icmpgt();
                    break;
                case (byte) 0xa4:
                    if_icmple();
                    break;
                case (byte) 0x84:
                    iinc();
                    break;
                case (byte) 0xbd:
                    anewarray();
                    break;
                case (byte) 0x32:
                    aaload();
                    break;
                case (byte) 0x57:
                    pop();
                    break;
                case (byte) 0x1:
                    aconst_null();
                    break;
                case (byte) 0x3a:
                    astore();
                    break;
                case (byte) 0x36:
                    istore();
                    break;
                case (byte) 0x15:
                    iload();
                    break;
                case (byte) 0x19:
                    aload();
                    break;
                case (byte) 0xbe:
                    arraylength();
                    break;
                case (byte) 0x34:
                    caload();
                    break;
                case (byte) 0xc5:
                    multianewarray();
                    break;
                default:
                    throw new Exception("Neznámá instrukce " + JVM.unsignedToBytes(code[pc]));
            }
        } while (pc < code.length);
    }

    private void bipush() {
        //System.out.println("bipush");
        pc++;
        IntValue val = new IntValue(code[pc]);
        operandStack.push(val);
        pc++;
    }
    
    private void istore() {
        //System.out.println("istore");
        pc++;
        int index = code[pc];
        localVariables[index] = operandStack.pop();
        pc++;
    }

    private void istore_0() {
        //System.out.println("istore_0");
        pc++;
        localVariables[0] = operandStack.pop();
    }

    private void istore_1() {
        //System.out.println("istore_1");
        pc++;
        localVariables[1] = operandStack.pop();
    }

    private void istore_2() {
        //System.out.println("istore_2");
        pc++;
        localVariables[2] = operandStack.pop();
    }

    private void istore_3() {
        //System.out.println("istore_3");
        pc++;
        localVariables[3] = operandStack.pop();
    }

    private void ret() {
        //System.out.println("ret");
        pc = code.length;
    }

    private void iret() {
        //System.out.println("iret");
        invoker.operandStack.push(operandStack.pop());
        pc = code.length;
    }

    private void aret() {
        //System.out.println("aret");
        invoker.operandStack.push(operandStack.pop());
        pc = code.length;
    }

    private void neww() throws Exception {
        //System.out.println("new");
        pc++;
        int constPoolIndex = code[pc] << 8 | (code[pc + 1] & 0xFF);
        int nameIndex = ((ConstantClass) constantPool.getConstant(constPoolIndex)).getNameIndex();
        String className = ((ConstantUtf8) constantPool.getConstant(nameIndex)).getBytes();
        ReferenceValue classRef = JVM.getJavaClassRef(className);
        ReferenceValue objRef = JVM.heap.allocateObject(classRef);
        operandStack.push(objRef);
        pc += 2;
        //JVM.heap.dumbHeap();
    }

    private void newarray() throws Exception {
        //System.out.println("newarray");
        int sizeOfElement;
        pc++;
        byte atype = code[pc];
        sizeOfElement = Heap.getTypeSize(atype);
        ReferenceValue arrayRef = JVM.heap.allocateArray(((IntValue) operandStack.pop()).getValue(), sizeOfElement, atype);
        operandStack.push(arrayRef);
        pc++;
        //JVM.heap.dumbHeap();
    }

    private void anewarray() throws Exception {
        //System.out.println("anewarray");
        pc++;
        int constPoolIndex = code[pc] << 8 | (code[pc + 1] & 0xFF);
        int nameIndex = ((ConstantClass) constantPool.getConstant(constPoolIndex)).getNameIndex();
        String className = ((ConstantUtf8) constantPool.getConstant(nameIndex)).getBytes();
        ReferenceValue classRef = JVM.getJavaClassRef(className);
        int sizeOfElement = 4;
        int type = 14;// reference
        ReferenceValue arrayRef = JVM.heap.allocateArray(((IntValue) operandStack.pop()).getValue(), sizeOfElement, type);
        operandStack.push(arrayRef);
        pc += 2;
    }
    
    private void multianewarray() throws Exception {
        //System.out.println("multianewarray");
        pc++;
        int constPoolIndex = code[pc] << 8 | (code[pc + 1] & 0xFF);
        int nameIndex = ((ConstantClass) constantPool.getConstant(constPoolIndex)).getNameIndex();
        String className = ((ConstantUtf8) constantPool.getConstant(nameIndex)).getBytes();
        //System.out.println(className);
        pc += 2;
        int dimensions = code[pc];
        IntValue[] dimensionsLengths = new IntValue[dimensions];
        for (int i = dimensions - 1; i >= 0; i--) {
            dimensionsLengths[i] = (IntValue) operandStack.pop();
        }
        int sizeOfElement = 4;
        int sizeOfLastElement = 4;
        int type = 13;// reference na pole
        ReferenceValue arrayRef = JVM.heap.allocateArray(dimensionsLengths[0].getValue(), sizeOfElement, type);
        ReferenceValue hlpArrayRef;
        for (int i = 0; i < dimensionsLengths[0].getValue(); i++) {
            hlpArrayRef = JVM.heap.allocateArray(dimensionsLengths[1].getValue(), sizeOfLastElement, 10);
            JVM.heap.storeRefToArray(hlpArrayRef, arrayRef, new IntValue(i));
            
        }
        operandStack.push(arrayRef);
        pc++;
    }

    private void dup() {
        //System.out.println("dup");
        pc++;
        Value topValue = operandStack.peek();
        operandStack.push(topValue);
    }

    private void iconst_m1() {
        //System.out.println("iconst_m1");
        pc++;
        operandStack.push(new IntValue(-1));
    }

    private void iconst_0() {
        //System.out.println("iconst_0");
        pc++;
        operandStack.push(new IntValue(0));
    }

    private void iconst_1() {
        //System.out.println("iconst_1");
        pc++;
        operandStack.push(new IntValue(1));
    }

    private void iconst_2() {
        //System.out.println("iconst_2");
        pc++;
        operandStack.push(new IntValue(2));
    }

    private void iconst_3() {
        //System.out.println("iconst_3");
        pc++;
        operandStack.push(new IntValue(3));
    }

    private void iconst_4() {
        //System.out.println("iconst_4");
        pc++;
        operandStack.push(new IntValue(4));
    }

    private void iconst_5() {
        //System.out.println("iconst_5");
        pc++;
        operandStack.push(new IntValue(5));
    }

    private void invokespecial() throws Exception {
        //System.out.println("invokespecial");
        pc++;
        int constPoolIndex = code[pc] << 8 | (code[pc + 1] & 0xFF);
        ConstantMethodref methodRef = (ConstantMethodref) constantPool.getConstant(constPoolIndex);
        int classIndex = methodRef.getClassIndex();
        int classNameIndex = ((ConstantClass) constantPool.getConstant(classIndex)).getNameIndex();
        String className = ((ConstantUtf8) constantPool.getConstant(classNameIndex)).getBytes();

        int nameAndTypeIndex = methodRef.getNameAndTypeIndex();
        ConstantNameAndType nameAndType = (ConstantNameAndType) constantPool.getConstant(nameAndTypeIndex);
        int nameIndex = nameAndType.getNameIndex();
        String methodName = ((ConstantUtf8) constantPool.getConstant(nameIndex)).getBytes();

        //System.out.println("metoda " + methodName + " třídy " + className);

        Method m = null;
        JavaClass clazz = JVM.getJavaClass(className);
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                m = method;
                break;
            }
        }
        
        if (m==null) { //jedna se o metodu predka
            while(true) {
                String SuperClassName = ((ConstantUtf8) clazz.getConstantPool().getConstant(((ConstantClass) clazz.getConstantPool().getConstant(clazz.getSuperclassNameIndex())).getNameIndex())).getBytes();
                clazz = jvm.JVM.getJavaClass(SuperClassName);
                //System.out.println("Prohladavame metody tridy " + clazz.getClassName());
            
                for (Method method : clazz.getMethods()) {
                    if (method.getName().equals(methodName)) {
                        //System.out.println("\t HEUREKA, nasli jsme funkci " + methodName +" v tride "+ clazz.getClassName());
                        m = method;
                        break;
                    }
                }
                if(m!=null) {
                    break;
                } else {
                   continue;
                }        
            }
        }

        Value[] arguments = null;
        if (m.getArgumentTypes().length > 0) {
            arguments = new Value[m.getArgumentTypes().length];
            for (int i = m.getArgumentTypes().length - 1; i >= 0; i--) {
                arguments[i] = operandStack.pop();
            }
        }

        JVM.callMethod(m, arguments, (ReferenceValue) operandStack.pop(), this);
        //System.out.println("Doběhla metoda " + m.getName());
        pc += 2;
    }

    private void invokestatic() throws Exception {
        //System.out.println("invokestatic");
        pc++;
        int constPoolIndex = code[pc] << 8 | (code[pc + 1] & 0xFF);
        ConstantMethodref methodRef = (ConstantMethodref) constantPool.getConstant(constPoolIndex);
        int classIndex = methodRef.getClassIndex();
        int classNameIndex = ((ConstantClass) constantPool.getConstant(classIndex)).getNameIndex();
        String className = ((ConstantUtf8) constantPool.getConstant(classNameIndex)).getBytes();

        int nameAndTypeIndex = methodRef.getNameAndTypeIndex();
        ConstantNameAndType nameAndType = (ConstantNameAndType) constantPool.getConstant(nameAndTypeIndex);
        int nameIndex = nameAndType.getNameIndex();
        String methodName = ((ConstantUtf8) constantPool.getConstant(nameIndex)).getBytes();

        //System.out.println("metoda " + methodName + " třídy " + className);

        Method m = null;

        JavaClass clazz = JVM.getJavaClass(className);
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                m = method;
                break;
            }
        }

        Value[] arguments = null;

        if (m.getArgumentTypes().length > 0) {
            arguments = new Value[m.getArgumentTypes().length];
            for (int i = m.getArgumentTypes().length - 1; i >= 0; i--) {
                arguments[i] = operandStack.pop();
            }
        }

        //System.out.println("Volá se metoda: " + m.getName());
        JVM.callMethod(m, arguments, null, this);
        //System.out.println("Doběhla metoda " + m.getName());
        pc += 2;
    }
    
    private void aload() {
        //System.out.println("aload");
        pc++;
        int index = code[pc];
        operandStack.push(localVariables[index]);
        pc++;
    }

    private void invokevirtual() throws Exception {
        //System.out.println("invokevirtual");
        pc++;
        int constPoolIndex = code[pc] << 8 | (code[pc + 1] & 0xFF);
        ConstantMethodref methodRef = (ConstantMethodref) constantPool.getConstant(constPoolIndex);
        int classIndex = methodRef.getClassIndex();
        int classNameIndex = ((ConstantClass) constantPool.getConstant(classIndex)).getNameIndex();
        String className = ((ConstantUtf8) constantPool.getConstant(classNameIndex)).getBytes();
        int nameAndTypeIndex = methodRef.getNameAndTypeIndex();
        ConstantNameAndType nameAndType = (ConstantNameAndType) constantPool.getConstant(nameAndTypeIndex);
        int nameIndex = nameAndType.getNameIndex();
        String methodName = ((ConstantUtf8) constantPool.getConstant(nameIndex)).getBytes();
        
        //System.out.println("metoda " + methodName + " třídy " + className);
        
        Method m = null;
      
        JavaClass clazz = JVM.getJavaClass(className);
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                m = method;
                break;
            }
        }
        
        if (m==null) { //jedna se o metodu predka
            while(true) {
                String SuperClassName = ((ConstantUtf8) clazz.getConstantPool().getConstant(((ConstantClass) clazz.getConstantPool().getConstant(clazz.getSuperclassNameIndex())).getNameIndex())).getBytes();
                clazz = jvm.JVM.getJavaClass(SuperClassName);
                //System.out.println("Prohladavame metody tridy " + clazz.getClassName());
            
                for (Method method : clazz.getMethods()) {
                    if (method.getName().equals(methodName)) {
                        //System.out.println("\t HEUREKA, nasli jsme funkci " + methodName +" v tride "+ clazz.getClassName());
                        m = method;
                        break;
                    }
                }
                if(m!=null) {
                    break;
                } else {
                   continue;
                }        
            }
        }
        

        Value[] arguments = null;
        
        if (m.getArgumentTypes().length > 0) {
            arguments = new Value[m.getArgumentTypes().length];
//           // System.out.println("Argument tagy:");
            for (int i = m.getArgumentTypes().length - 1; i >= 0; i--) {
                arguments[i] = operandStack.pop();
//                //System.out.println(arguments[i].tag);
            }
        }
        
        int instanceClassIndex = JVM.heap.getClassIndex((ReferenceValue) operandStack.peek());
        JavaClass instanceClass =  JVM.getJavaClassByIndex(new ReferenceValue(instanceClassIndex));
        
        if(instanceClass.getClassName() == null ? clazz.getClassName() != null : !instanceClass.getClassName().equals(clazz.getClassName())) {
            //System.out.println("\t Prohledavame metody tridy " + instanceClass.getClassName());
            
            for (Method method : instanceClass.getMethods()) {
                if (method.getName().equals(methodName)) {
                    //System.out.println("Nasli jsme metodu v instanci!!!!");
                    m = method;
                    break;
                }
            }
        }
        
        if(JVM.isNative(m)) {
            //System.out.println("Volá se NATIVNI metoda: " + m.getName());
            JVM.callNativeMethod(m, arguments, (ReferenceValue) operandStack.pop(), this);
            //System.out.println("Doběhla NATIVNI metoda " + m.getName());
        } else {
           // System.out.println("Volá se metoda: " + m.getName());
            JVM.callMethod(m, arguments, (ReferenceValue) operandStack.pop(), this);
            //System.out.println("Doběhla metoda " + m.getName());
        }
        
        pc += 2;
    }
    
    private void aload_0() {
      //  System.out.println("aload_0");
        pc++;
        operandStack.push(localVariables[0]);
    }

    private void aload_1() {
       // System.out.println("aload_1");
        pc++;
        operandStack.push(localVariables[1]);
    }

    private void aload_2() {
        //System.out.println("aload_2");
        pc++;
        operandStack.push(localVariables[2]);
    }

    private void aload_3() {
        //System.out.println("aload_3");
        pc++;
        operandStack.push(localVariables[3]);
    }
    
    private void iload() {
        //System.out.println("iload");
        pc++;
        int index = code[pc];
        operandStack.push(localVariables[index]);
        pc++;
    }

    private void iload_0() {
        //System.out.println("iload_0");
        pc++;
        operandStack.push(localVariables[0]);
    }

    private void iload_1() {
        //System.out.println("iload_1");
        pc++;
        operandStack.push(localVariables[1]);
    }

    private void iload_2() {
        //System.out.println("iload_2");
        pc++;
        operandStack.push(localVariables[2]);
    }

    private void iload_3() {
        //System.out.println("iload_3");
        pc++;
        operandStack.push(localVariables[3]);
    }

    private void astore_0() {
        //System.out.println("astore_0");
        pc++;
        localVariables[0] = operandStack.pop();
    }

    private void astore_1() {
       //System.out.println("astore_1");
        pc++;
        localVariables[1] = operandStack.pop();
    }

    private void astore_2() {
        //System.out.println("astore_2");
        pc++;
        localVariables[2] = operandStack.pop();
    }

    private void astore_3() {
        //System.out.println("astore_3");
        pc++;
        localVariables[3] = operandStack.pop();
    }

    private void astore() {
        //System.out.println("astore");
        pc++;
        int index = code[pc];
        localVariables[index] = operandStack.pop();
        pc++;
    }

    private int getSuperclassesFieldsSize(JavaClass clazz) throws Exception {

        if (clazz.getClassName().equals("java.lang.Object")) {
            return 0;
        }
        String superClassName = ((ConstantUtf8) clazz.getConstantPool().getConstant(((ConstantClass) clazz.getConstantPool().getConstant(clazz.getSuperclassNameIndex())).getNameIndex())).getBytes();
        JavaClass superClass = JVM.getJavaClass(superClassName);
        int size = 0;
        for (Field field : superClass.getFields()) {
            size += Heap.getTypeSize(field.getType().getType());
        }
        return getSuperclassesFieldsSize(superClass) + size;
    }

    private int getFieldOffset(JavaClass clazz, String fieldName) throws Exception {
        int offset = 0;
        Field[] fields = clazz.getFields();
        Field field = null;
        for (Field field1 : fields) {
            if (field1.getName().equals(fieldName)) {
                field = field1;
                break;
            }
            offset += Heap.getTypeSize(field1.getType().getType());
        }
        if (field == null) {
            String superClassName = ((ConstantUtf8) clazz.getConstantPool().getConstant(((ConstantClass) clazz.getConstantPool().getConstant(clazz.getSuperclassNameIndex())).getNameIndex())).getBytes();
            JavaClass superClass = JVM.getJavaClass(superClassName);
            offset = getFieldOffset(superClass, fieldName);
        } else {
            offset += getSuperclassesFieldsSize(clazz);
        }

        return offset;
    }

    private void putfield() throws Exception {
        //System.out.println("putfield");
        pc++;
        int constPoolIndex = code[pc] << 8 | (code[pc + 1] & 0xFF);
        ConstantFieldref fieldRef = (ConstantFieldref) constantPool.getConstant(constPoolIndex);
        int classIndex = fieldRef.getClassIndex();
        int classNameIndex = ((ConstantClass) constantPool.getConstant(classIndex)).getNameIndex();
        String className = ((ConstantUtf8) constantPool.getConstant(classNameIndex)).getBytes();

        int nameAndTypeIndex = fieldRef.getNameAndTypeIndex();
        ConstantNameAndType nameAndType = (ConstantNameAndType) constantPool.getConstant(nameAndTypeIndex);
        String fieldName = nameAndType.getName(constantPool);
        String fieldType = nameAndType.getSignature(constantPool);

        JavaClass clazz = JVM.getJavaClass(className);
        int offset = jvm.Heap.OBJECT_HEAD_SIZE + getFieldOffset(clazz, fieldName);

        switch (fieldType.charAt(0)) {
            case 'I':
                JVM.heap.storeInt((IntValue) operandStack.pop(), (ReferenceValue) operandStack.pop(), offset);
                break;
            case 'C':
                JVM.heap.storeChar((CharValue) operandStack.pop(), (ReferenceValue) operandStack.pop(), offset);
                break;
            case 'L':
                JVM.heap.storeRef((ReferenceValue) operandStack.pop(), (ReferenceValue) operandStack.pop(), offset);
                break;
            case '[':
                JVM.heap.storeRef((ReferenceValue) operandStack.pop(), (ReferenceValue) operandStack.pop(), offset);
                break;
            default:
                throw new Exception("Neznámý typ při ukládání fieldu!");
        }
        pc += 2;
    }

    private void getfield() throws Exception {
        //System.out.println("getfield");
        pc++;
        int constPoolIndex = code[pc] << 8 | (code[pc + 1] & 0xFF);
        ConstantFieldref fieldRef = (ConstantFieldref) constantPool.getConstant(constPoolIndex);
        int classIndex = fieldRef.getClassIndex();
        int classNameIndex = ((ConstantClass) constantPool.getConstant(classIndex)).getNameIndex();

        String className = ((ConstantUtf8) constantPool.getConstant(classNameIndex)).getBytes();
        int nameAndTypeIndex = fieldRef.getNameAndTypeIndex();
        ConstantNameAndType nameAndType = (ConstantNameAndType) constantPool.getConstant(nameAndTypeIndex);
        String fieldName = nameAndType.getName(constantPool);
        String fieldType = nameAndType.getSignature(constantPool);

        JavaClass clazz = JVM.getJavaClass(className);
        int offset = jvm.Heap.OBJECT_HEAD_SIZE + getFieldOffset(clazz, fieldName);

        switch (fieldType.charAt(0)) {
            case 'I':
                operandStack.push(JVM.heap.fetchInt((ReferenceValue) operandStack.pop(), offset));
                break;
            case 'C':
                operandStack.push(JVM.heap.fetchChar((ReferenceValue) operandStack.pop(), offset));
                break;
            case 'L':
                operandStack.push(JVM.heap.fetchRef((ReferenceValue) operandStack.pop(), offset));
                break;
            case '[':
                operandStack.push(JVM.heap.fetchRef((ReferenceValue) operandStack.pop(), offset));
                break;
            default:
                throw new Exception("Neznámý typ při ukládání fieldu!");
        }
        pc += 2;
    }

    private void sipush() {
        //System.out.println("sipush");
        pc++;
        short s = (short) (code[pc] << 8 | (code[pc + 1] & 0xFF));
        int i = s;
        operandStack.push(new IntValue(i));
        pc += 2;
    }

    private void iastore() throws Exception {
        //System.out.println("iastore");
        pc++;
        IntValue value = (IntValue) operandStack.pop();
        IntValue index = (IntValue) operandStack.pop();
        ReferenceValue arrayRef = (ReferenceValue) operandStack.pop();
        JVM.heap.storeIntToArray(value, arrayRef, index);
    }

    private void aastore() throws Exception {
        //System.out.println("aastore");
        pc++;
        ReferenceValue value = (ReferenceValue) operandStack.pop();
        IntValue index = (IntValue) operandStack.pop();
        ReferenceValue arrayRef = (ReferenceValue) operandStack.pop();
        JVM.heap.storeRefToArray(value, arrayRef, index);
    }

    private void aaload() throws Exception {
        //System.out.println("aaload");
        pc++;
        IntValue index = (IntValue) operandStack.pop();
        ReferenceValue arrayRef = (ReferenceValue) operandStack.pop();
        ReferenceValue refVal = JVM.heap.fetchRefFromArray(arrayRef, index);
        operandStack.push(refVal);
    }

    private void iaload() throws Exception {
        //System.out.println("iaload");
        pc++;
        IntValue index = (IntValue) operandStack.pop();
        ReferenceValue arrayRef = (ReferenceValue) operandStack.pop();
        operandStack.push(JVM.heap.fetchIntFromArray(arrayRef, index));
    }

    private void iadd() {
        //System.out.println("iadd");
        pc++;
        IntValue result = ((IntValue) operandStack.pop()).add((IntValue) operandStack.pop());
        operandStack.push(result);
    }

    private void isub() {
        //System.out.println("isub");
        pc++;
        IntValue x = (IntValue) operandStack.pop();
        IntValue result = ((IntValue) operandStack.pop()).sub(x);
        operandStack.push(result);
    }

    private void imul() {
        //System.out.println("imul");
        pc++;
        IntValue result = ((IntValue) operandStack.pop()).mul((IntValue) operandStack.pop());
        operandStack.push(result);
    }

    private void idiv() {
        //System.out.println("idiv");
        pc++;
        IntValue x = (IntValue) operandStack.pop();
        IntValue result = ((IntValue) operandStack.pop()).div(x);
        operandStack.push(result);
    }

    private void gotoo() {
        //System.out.println("goto");
        short offset = (short) (code[pc + 1] << 8 | (code[pc + 2] & 0xFF));
        pc += offset;
    }

    private void ifeq() {
        //System.out.println("ifeq");
        if (((IntValue) operandStack.pop()).getValue() == 0) {
            short offset = (short) (code[pc + 1] << 8 | (code[pc + 2] & 0xFF));
            pc += offset;
        } else {
            pc += 3;
        }
    }

    private void ifne() {
        //System.out.println("ifne");
        if (((IntValue) operandStack.pop()).getValue() != 0) {
            short offset = (short) (code[pc + 1] << 8 | (code[pc + 2] & 0xFF));
            pc += offset;
        } else {
            pc += 3;
        }
    }

    private void iflt() {
        //System.out.println("iflt");
        if (((IntValue) operandStack.pop()).getValue() < 0) {
            short offset = (short) (code[pc + 1] << 8 | (code[pc + 2] & 0xFF));
            pc += offset;
        } else {
            pc += 3;
        }
    }

    private void ifgt() {
        //System.out.println("ifgt");
        if (((IntValue) operandStack.pop()).getValue() > 0) {
            short offset = (short) (code[pc + 1] << 8 | (code[pc + 2] & 0xFF));
            pc += offset;
        } else {
            pc += 3;
        }
    }

    private void ifle() {
        //System.out.println("ifle");
        if (((IntValue) operandStack.pop()).getValue() <= 0) {
            short offset = (short) (code[pc + 1] << 8 | (code[pc + 2] & 0xFF));
            pc += offset;
        } else {
            pc += 3;
        }
    }

    private void ifge() {
        //System.out.println("ifge");
        if (((IntValue) operandStack.pop()).getValue() >= 0) {
            short offset = (short) (code[pc + 1] << 8 | (code[pc + 2] & 0xFF));
            pc += offset;
        } else {
            pc += 3;
        }
    }

    private void if_icmpeq() {
        //System.out.println("if_icmpeq");
        IntValue value2 = (IntValue) operandStack.pop();
        IntValue value1 = (IntValue) operandStack.pop();
        if (value1.getValue() == value2.getValue()) {
            short offset = (short) (code[pc + 1] << 8 | (code[pc + 2] & 0xFF));
            pc += offset;
        } else {
            pc += 3;
        }
    }

    private void if_icmpne() {
        //System.out.println("if_icmpne");
        IntValue value2 = (IntValue) operandStack.pop();
        IntValue value1 = (IntValue) operandStack.pop();
        if (value1.getValue() != value2.getValue()) {
            short offset = (short) (code[pc + 1] << 8 | (code[pc + 2] & 0xFF));
            pc += offset;
        } else {
            pc += 3;
        }
    }

    private void if_icmplt() {
        //System.out.println("if_icmplt");
        IntValue value2 = (IntValue) operandStack.pop();
        IntValue value1 = (IntValue) operandStack.pop();
        if (value1.getValue() < value2.getValue()) {
            short offset = (short) (code[pc + 1] << 8 | (code[pc + 2] & 0xFF));
            pc += offset;
        } else {
            pc += 3;
        }
    }

    private void if_icmpge() {
        //System.out.println("if_icmpge");
        IntValue value2 = (IntValue) operandStack.pop();
        IntValue value1 = (IntValue) operandStack.pop();
        if (value1.getValue() >= value2.getValue()) {
            short offset = (short) (code[pc + 1] << 8 | (code[pc + 2] & 0xFF));
            pc += offset;
        } else {
            pc += 3;
        }
    }

    private void if_icmpgt() {
        //System.out.println("if_icmpgt");
        IntValue value2 = (IntValue) operandStack.pop();
        IntValue value1 = (IntValue) operandStack.pop();
        if (value1.getValue() > value2.getValue()) {
            short offset = (short) (code[pc + 1] << 8 | (code[pc + 2] & 0xFF));
            pc += offset;
        } else {
            pc += 3;
        }
    }

    private void if_icmple() {
        //System.out.println("if_icmple");
        IntValue value2 = (IntValue) operandStack.pop();
        IntValue value1 = (IntValue) operandStack.pop();
        if (value1.getValue() <= value2.getValue()) {
            short offset = (short) (code[pc + 1] << 8 | (code[pc + 2] & 0xFF));
            pc += offset;
        } else {
            pc += 3;
        }
    }

    private void iinc() {
        //System.out.println("iinc");
        pc++;
        short index = (short) code[pc];
        IntValue constant = new IntValue(code[pc + 1]);
        ((IntValue) localVariables[index]).inc(constant);
        pc += 2;
    }

    private void pop() {
        //System.out.println("pop");
        pc++;
        operandStack.pop();
    }

    private void aconst_null() {
        //System.out.println("aconst_null");
        pc++;
        operandStack.push(new ReferenceValue(0));
    }
    
    private void arraylength() {
        //System.out.println("arraylength");
        pc++;
        operandStack.push(JVM.heap.getArrayLength((ReferenceValue) operandStack.pop()));
    }
    
    private void caload() throws Exception {
        //System.out.println("caload");
        pc++;
        IntValue index = (IntValue) operandStack.pop();
        ReferenceValue arrayRef = (ReferenceValue) operandStack.pop();
        operandStack.push(JVM.heap.fetchCharFromArray(arrayRef, index));
    }

}
