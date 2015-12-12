package jvm.frame;

import jvm.values.Value;
import java.util.ArrayDeque;
import java.util.Deque;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

/**
 *
 * @author Jaroslav Ševčík
 */
public class Frame {
     //Možná by měl být jeden pc pro celou JVM (podle specifikace jeden pro vlákno) a při spuštění nové metody se někam ukládá původní hodnota?
    private int pc = 0;
    private Deque<Value> operandStack;
    private LocalVariable[] localVariables;
    //constant pool je jeden pro třídu
    private ConstantPool constantPool;
    private byte[] code;
    private Frame invoker;

    //Odkaz na třídu? Pak bych nepotřeboval odkaz na constant pool. Přistupoval bych k němu přes třídu.
    //Argumenty se budou možná předávat jinak. Pushnou se rovnou na operand stack volajícím?
    public Frame(Method method, Value[] arguments, Value thisHeapIndex, Frame invoker) throws Exception {
        this.operandStack = new ArrayDeque<>(method.getCode().getMaxStack());
        this.localVariables = new LocalVariable[method.getCode().getMaxLocals()];
        //constant pool je jeden pro třídu
        this.constantPool = method.getConstantPool();
        this.code = method.getCode().getCode();
        this.invoker = invoker;
        
        Type[] argumentTypes = method.getArgumentTypes();
        int i = 0, j = 0;
        if (!method.isStatic()) {
            localVariables[0] = new LocalVariable(thisHeapIndex);
            j++;
        }
        
//        System.out.println(method.getArgumentTypes()[0].getType());
        
        
        for (; i < argumentTypes.length; i++, j++) {
            localVariables[j] = new LocalVariable(arguments[i]);
        }
    }
    
    public void start () {
        //tady se začne vykonávat bytecode
        
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
    
}
