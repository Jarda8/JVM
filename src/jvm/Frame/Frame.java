package jvm.frame;

import java.io.IOException;
import jvm.values.Value;
import java.util.ArrayDeque;
import java.util.Deque;
import static jvm.JVM.unsignedToBytes;
import jvm.values.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.Type;

/**
 *
 * @author Jaroslav Ševčík
 */
public class Frame {

    //Možná by měl být jeden pc pro celou JVM (podle specifikace jeden pro vlákno) a při spuštění nové metody se někam ukládá původní hodnota?
    private int pc = 0;
    private final Deque<Value> operandStack;
    private final Value[] localVariables;
    //constant pool je jeden pro třídu
    private final ConstantPool constantPool;
    private final byte[] code;
    private final Frame invoker;

    //Odkaz na třídu? Pak bych nepotřeboval odkaz na constant pool. Přistupoval bych k němu přes třídu.
    //Argumenty se budou možná předávat jinak. Pushnou se rovnou na operand stack volajícím?
    public Frame(Method method, Value[] arguments, ReferenceValue thisHeapIndex, Frame invoker) throws Exception {
        System.out.println("Vytváří se frame metody: " + method.getName());
        this.operandStack = new ArrayDeque<>(method.getCode().getMaxStack());
        this.localVariables = new Value[method.getCode().getMaxLocals()];
        //constant pool je jeden pro třídu
        this.constantPool = method.getConstantPool();
        this.code = method.getCode().getCode();
        this.invoker = invoker;

        Type[] argumentTypes = method.getArgumentTypes();
        int i = 0, j = 0;
        if (!method.isStatic()) {
            localVariables[0] = thisHeapIndex;
            j++;
        }

//        System.out.println(method.getArgumentTypes()[0].getType());
        if (arguments != null) {
            for (; i < argumentTypes.length; i++, j++) {
                localVariables[j] = arguments[i];
            }
        }
        System.out.println("Vytvořil se frame metody: " + method.getName());

        System.out.println("Bytecode metody:");
        for (int k = 0; k < method.getCode().getCode().length; k++) {
            System.out.println(unsignedToBytes(method.getCode().getCode()[k]));
        }
    }

    public void pushOnStack(Value val) {
        operandStack.push(val);
    }

    public void popFromStack() {
        operandStack.pop();
    }

    public void start() throws Exception {
        System.out.println("Provádí se kód metody.");

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
                case (byte) 0xbb:
                    neww();
                    break;
                case 0x59:
                    dup();
                    break;
                case (byte) 0xb7:
                    invokespecial();
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
                default:
                    throw new Exception("Neznámá instrukce " + code[pc]);
            }
        } while (pc < code.length);
    }

    private void bipush() {
        System.out.println("bipush");
        pc++;
        IntValue val = new IntValue(code[pc]);
        operandStack.push(val);
        pc++;
    }

    private void istore_0() {
        System.out.println("istore_0");
        pc++;
        localVariables[0] = operandStack.pop();
    }

    private void istore_1() {
        System.out.println("istore_1");
        pc++;
        localVariables[1] = operandStack.pop();
    }

    private void istore_2() {
        System.out.println("istore_2");
        pc++;
        localVariables[2] = operandStack.pop();
    }

    private void istore_3() {
        System.out.println("istore_3");
        pc++;
        localVariables[3] = operandStack.pop();
    }

    private void ret() {
        System.out.println("ret");
        pc = code.length;
    }

    private void neww() throws Exception {
        System.out.println("new");
        pc++;
        int constPoolIndex = code[pc] << 8 | (code[pc + 1] & 0xFF);
        int nameIndex = ((ConstantClass) constantPool.getConstant(constPoolIndex)).getNameIndex();
        String className = ((ConstantUtf8) constantPool.getConstant(nameIndex)).getBytes();
        ReferenceValue classRef = jvm.JVM.getJavaClassRef(className);
        ReferenceValue objRef = jvm.JVM.heap.allocateObject(classRef);
        operandStack.push(objRef);
        pc += 2;
    }

    private void dup() {
        System.out.println("dup");
        pc++;
        //mělká kopie - Měla by být hluboká?
        Value topValue = operandStack.peek();
        operandStack.push(topValue);
    }

    private void iconst_m1() {
        System.out.println("iconst_m1");
        pc++;
        operandStack.push(new IntValue(-1));
    }

    private void iconst_0() {
        System.out.println("iconst_0");
        pc++;
        operandStack.push(new IntValue(0));
    }

    private void iconst_1() {
        System.out.println("iconst_1");
        pc++;
        operandStack.push(new IntValue(1));
    }

    private void iconst_2() {
        System.out.println("iconst_2");
        pc++;
        operandStack.push(new IntValue(2));
    }

    private void iconst_3() {
        System.out.println("iconst_3");
        pc++;
        operandStack.push(new IntValue(3));
    }

    private void iconst_4() {
        System.out.println("iconst_4");
        pc++;
        operandStack.push(new IntValue(4));
    }

    private void iconst_5() {
        System.out.println("iconst_5");
        pc++;
        operandStack.push(new IntValue(5));
    }

    private void invokespecial() throws Exception {
        System.out.println("invokespecial");
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

        System.out.println("metoda " + methodName + " třídy " + className);

        if (className.equals("java/lang/Object") && methodName.equals("<init>")) {
            pc += 2;
            System.out.println("Doběhla metoda " + methodName);
            return;
        }

        Method m = null;
        //TODO Takhle patrně zjistím třídu proměnné, na které byla metoda zavolána, nikoliv třídu konkrétní instance (Může to být podtřída.).
        //Třída se bude muset zjistit z instance na haldě. Index na haldu je v localVariables[0] (pokud jde o instanční metodu) a index do classTable je na prvním místě v instanci.
        //Tohle se možná nemusí řešit v invokespecial, ale v invokevirtual nebo jak se jmenuje ta instrukce.
        JavaClass clazz = jvm.JVM.getJavaClass(className);
        for (int i = 0; i < clazz.getMethods().length; i++) {
            if (clazz.getMethods()[i].getName().equals(methodName)) {
                m = clazz.getMethods()[i];
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

//        System.out.println("Volá se metoda: " + m.getName());
        jvm.JVM.callMethod(m, arguments, (ReferenceValue) operandStack.pop(), this);
        System.out.println("Doběhla metoda " + m.getName());
        pc += 2;
    }

    private void aload_0() {
        System.out.println("aload_0");
        pc++;
        operandStack.push(localVariables[0]);
    }

    private void aload_1() {
        System.out.println("aload_1");
        pc++;
        operandStack.push(localVariables[1]);
    }

    private void aload_2() {
        System.out.println("aload_2");
        pc++;
        operandStack.push(localVariables[2]);
    }

    private void aload_3() {
        System.out.println("aload_3");
        pc++;
        operandStack.push(localVariables[3]);
    }

    private void iload_0() {
        System.out.println("iload_0");
        pc++;
        operandStack.push(localVariables[0]);
    }

    private void iload_1() {
        System.out.println("iload_1");
        pc++;
        operandStack.push(localVariables[1]);
    }

    private void iload_2() {
        System.out.println("iload_2");
        pc++;
        operandStack.push(localVariables[2]);
    }

    private void iload_3() {
        System.out.println("iload_3");
        pc++;
        operandStack.push(localVariables[3]);
    }

    private void astore_0() {
        System.out.println("astore_0");
        pc++;
        localVariables[0] = operandStack.pop();
    }

    private void astore_1() {
        System.out.println("astore_1");
        pc++;
        localVariables[1] = operandStack.pop();
    }

    private void astore_2() {
        System.out.println("astore_2");
        pc++;
        localVariables[2] = operandStack.pop();
    }

    private void astore_3() {
        System.out.println("astore_3");
        pc++;
        localVariables[3] = operandStack.pop();
    }
    
    private int getTypeSize (byte type) throws Exception {
        int result;
        switch (type) {
                 case 10: result = 4; break;//int
                 case 5: result = 2; break;//char
                 default: throw new Exception("Neznámý typ při zjišťování velikosti typu: " + type);
             }
        return result;
    }

    private int getSuperclassesFieldsSize(JavaClass clazz) throws Exception {
        if (clazz.getClassName().equals("initclasses.Object")) {
            return 0;
        }
        String superClassName = ((ConstantUtf8) clazz.getConstantPool().getConstant(((ConstantClass) clazz.getConstantPool().getConstant(clazz.getSuperclassNameIndex())).getNameIndex())).getBytes();
        JavaClass superClass = jvm.JVM.getJavaClass(superClassName);
        int size = 0;
        for (Field field : superClass.getFields()) {
             size += getTypeSize(field.getType().getType());
        }
        return getSuperclassesFieldsSize(superClass) + size;
    }

    private void putfield() throws Exception {
        System.out.println("putfield");
        pc++;
        int constPoolIndex = code[pc] << 8 | (code[pc + 1] & 0xFF);
        ConstantFieldref fieldRef = (ConstantFieldref) constantPool.getConstant(constPoolIndex);
        int classIndex = fieldRef.getClassIndex();
        int classNameIndex = ((ConstantClass) constantPool.getConstant(classIndex)).getNameIndex();
        String className = ((ConstantUtf8) constantPool.getConstant(classNameIndex)).getBytes();

        int nameAndTypeIndex = fieldRef.getNameAndTypeIndex();
        ConstantNameAndType nameAndType = (ConstantNameAndType) constantPool.getConstant(nameAndTypeIndex);
        int nameIndex = nameAndType.getNameIndex();
        String fieldName = ((ConstantUtf8) constantPool.getConstant(nameIndex)).getBytes();

        JavaClass clazz = jvm.JVM.getJavaClass(className);
        Field[] fields = clazz.getFields();
        Field field = null;
        //TODO Až budou mít objekty flagy, tak se změní offset pravděpodobně
        int offset = jvm.Heap.OBJECT_HEAD_SIZE + getSuperclassesFieldsSize(clazz);
        for (Field field1 : fields) {
            if (field1.getName().equals(fieldName)) {
                field = field1;
                break;
            }
            offset += getTypeSize(field1.getType().getType());
        }
        switch (field.getType().getType()) {
            case 10:
                jvm.JVM.heap.storeInt((IntValue) operandStack.pop(), (ReferenceValue) operandStack.pop(), offset);
                break;
            case 5:
                jvm.JVM.heap.storeChar((CharValue) operandStack.pop(), (ReferenceValue) operandStack.pop(), offset);
                break;
            default:
                throw new Exception("Neznámý typ při ukládání fieldu!");
        }
        pc += 2;
    }

    private void sipush() {
        System.out.println("sipush");
        pc++;
        short s = (short) (code[pc] << 8 | (code[pc + 1] & 0xFF));
        int i = s;
        operandStack.push(new IntValue(i));
        pc += 2;
    }

}

//            Vrací to nějaký divný čísla. Měly by odpovídat tagům classfilu.
//            switch (argumentTypes[i].getType()) {
//                case 10: case 4: case 5: int); break;//10 int, 4 boolean, 5 char,
//                case 14: class/string; break;//14 class/string - Je divný, že to vrací stejný číslo. Class a String by měly být jiný
//                case 6: float; break;//6 float
//                case 7: double; break; break;//7 double
//                case 13: array; break; break;//13 array
//                default: throw new Exception("Neznámý typ argumentu při vytváření framu!");
//Asi bude stačit jenom pár základních typů - int, boolean, string/obj reference, array reference, char
//            }
//        V implementaci returnu se návratové hodnoty pushnou rovnou na operand stack volající metody (případně jinam, pokud jde o main) a posune se pc volajícího.
