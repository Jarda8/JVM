package jvm;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import jvm.Frame.Frame;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.JavaClass;

public class JVM {

        static final Deque<Frame> frameStack = new ArrayDeque<>();
        static final Heap heap = new Heap();
        static final HashMap<String, JavaClass> classTable = new HashMap<>();
        //native methods stack?

    public static void main(String[] args) throws IOException, Exception {
        
        JavaClass mainClass = (new ClassParser(args[0])).parse();
        classTable.put(mainClass.getClassName(), mainClass);
        
        //vytvoří se frame pro main funkci, hodí se na stack a spustí se
        //argumenty z příkazové řádky je třeba naparsovat do příslušné Value třídy
        frameStack.push(new Frame(mainClass.getMethods()[1], null, null));
        frameStack.peek().start();
        //program doběhl
        //uklidim stack
        frameStack.pop();
        //uklidim haldu a classTable?
        
        System.out.println(classTable.get("testapp.TestApp").getMethods()[2]);
//        System.out.println(((ConstantInteger)classTable.get("testapp.TestApp").getConstantPool().getConstant(2)).getBytes());

    }
    
    
}
