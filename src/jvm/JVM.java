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

    public static void main(String[] args) throws IOException {
        
        JavaClass clazz = (new ClassParser(args[0])).parse();
        classTable.put(clazz.getClassName(), clazz);
        
        
        
        
        System.out.println(classTable.get("testapp.TestApp").getMethods()[1].getCode());
        System.out.println(((ConstantInteger)classTable.get("testapp.TestApp").getConstantPool().getConstant(2)).getBytes());

    }
    
    
}
