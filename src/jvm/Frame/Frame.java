package jvm.frame;

import java.io.IOException;
import jvm.values.Value;
import java.util.ArrayDeque;
import java.util.Deque;
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
    private Deque<Value> operandStack;
    private Value[] localVariables;
    //constant pool je jeden pro třídu
    private ConstantPool constantPool;
    private byte[] code;
    private Frame invoker;

    //Odkaz na třídu? Pak bych nepotřeboval odkaz na constant pool. Přistupoval bych k němu přes třídu.
    //Argumenty se budou možná předávat jinak. Pushnou se rovnou na operand stack volajícím?
    public Frame(Method method, Value[] arguments, ReferenceValue thisHeapIndex, Frame invoker) throws Exception {
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
    }

    public void start() throws Exception {
        //vykonávání bytecodu
        do {
            switch (code[pc]) {
                case 0x10: bipush(); break;
                case 0x3c: istore_1(); break;
                case (byte) 0xb1: ret(); break;
                case (byte) 0xbb: neww(); break;
                case 0x59: dup(); break;
                default: throw new Exception("Neznámá instrukce!");
            }
        } while (pc < code.length);
        

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
    }
    
    private void bipush () {
        pc++;
        IntValue val = new IntValue(code[pc]);
        operandStack.push(val);
        pc++;
    }
    
    private void istore_1 () {
        pc++;
        localVariables[1] = operandStack.pop();
        pc++;
    }
    
    private void ret () {
        pc = code.length;
    }
    
    private void neww () throws IOException {
        pc++;
        int constPoolIndex = code[pc] << 8 | (code[pc + 1] & 0xFF);
        int nameIndex = ((ConstantClass) constantPool.getConstant(constPoolIndex)).getNameIndex();
        String className = ((ConstantUtf8) constantPool.getConstant(nameIndex)).getBytes();
        className += ".class";
        JavaClass objClass = jvm.JVM.getJavaClass(className);
        ReferenceValue objRef = new ReferenceValue(jvm.JVM.heap.allocateObject(objClass));
        operandStack.push(objRef);
        pc += 2;
    }
    
    private void dup () {
        pc++;
        //mělká kopie - Měla by být hluboká?
        Value topValue = operandStack.peek();
        operandStack.push(topValue);
    }
    
    private void invokespecial () {
        pc++;
        int constPoolIndex = code[pc] << 8 | (code[pc + 1] & 0xFF);
        //volání metody (např. inicializace objektu)
        
        pc += 2;
    }

}
