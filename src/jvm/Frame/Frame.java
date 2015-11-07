package jvm.Frame;

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
    private int pc = 0;
    private Deque<Value> operandStack;
    private LocalVariable[] localVariables;
    private ConstantPool constantPool;
    private byte[] code;

    //asi ještě předat místo, kam se bude ukládat návratová hodnota metody
    
    public Frame(Method method, Value[] arguments, Value thisHeapIndex) throws Exception {
        this.operandStack = new ArrayDeque<>(method.getCode().getMaxStack());
        this.localVariables = new LocalVariable[method.getCode().getMaxLocals()];
        this.constantPool = method.getConstantPool();
        this.code = method.getCode().getCode();
        
        Type[] argumentTypes = method.getArgumentTypes();
        int i = 0, j = 0;
        if (!method.isStatic()) {
            j = 1;
            localVariables[0] = new LocalVariable(thisHeapIndex);
        }
        
//        System.out.println(method.getArgumentTypes()[0].getType());
        
        
        for (; i < argumentTypes.length; i++, j++) {
            switch (argumentTypes[i].getType()) {
                case 3: localVariables[j] = new LocalVariable(arguments[i]); break;
                case 6: localVariables[j] = new LocalVariable(arguments[i]); j++; break;//index je posunutý o jedna, protože double zabírá dvě položky
                default: throw new Exception("Neznámý typ argumentu při vytváření framu!");
                    //Dodělat ostatní typy. Nejsem si jistej, jaký to mohou být. Nejspíš jsou i tyhle špatně.
            }
        }
    }
    
    public void start () {
        //tady se začne vykonávat bytecode
    }
    
}
