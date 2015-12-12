package jvm;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import jvm.frame.Frame;
import jvm.values.Value;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.JavaClass;

public class JVM {

    static final Deque<Frame> frameStack = new ArrayDeque<>();
    static final Heap heap = new Heap();
    //Slouží jako Method Area ve specifikaci
    static final HashMap<String, JavaClass> classTable = new HashMap<>();

        //přidat native methods stack
    public static void main(String[] args) throws Exception {

        //loading core classes
        classTable.put("initclasses.StringObj", (new ClassParser("initclasses\\StringObj.class")).parse());
        
        heap.allocateObject(classTable.get("initclasses.StringObj"));
        
        //loading main class
        JavaClass mainClass = (new ClassParser(args[0])).parse();
        classTable.put(mainClass.getClassName(), mainClass);

        //vytvoří se frame pro main funkci, hodí se na stack a spustí se
        //argumenty z příkazové řádky je třeba dát do našeho stringu
        frameStack.push(new Frame(mainClass.getMethods()[1], null, null, null));
        frameStack.peek().start();
        //program doběhl
        //uklidim stack
        frameStack.pop();
        //uklidim haldu a classTable?

//        System.out.println(classTable.get("testapp.TestApp").getMethods()[2]);
//        System.out.println(((ConstantInteger)classTable.get("testapp.TestApp").getConstantPool().getConstant(2)).getBytes());

    }
    //TODO predělat všechno na StringValue nebo ReferenceValue, až bude 
    public Value[] parseMainArguments(String[] args) {
        Value[] valArgs;
        if (args.length <= 1) {
            return null;
        }
        valArgs = new Value[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            
            //alokovat někam (na haldu) string
            //valArgs[i] = new StringValue(index začátku stringu)
        }
        return valArgs;
    }

}
