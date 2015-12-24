package jvm;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
//import java.util.HashMap;
import java.util.List;
import jvm.frame.Frame;
import jvm.values.*;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class JVM {

    static final Deque<Frame> frameStack = new ArrayDeque<>();
    public static final Heap heap = new Heap();
    //Slouží jako Method Area ve specifikaci
//    static final HashMap<String, JavaClass> classTable = new HashMap<>();
    static final List<JavaClass> classTable = new ArrayList<>();
    //přidat native methods stack

    public static void main(String[] args) throws Exception {

        //loading core classes
//        classTable.put("initclasses/Sring", (new ClassParser("initclasses/String.class")).parse());
//        classTable.put("initclasses/Object", (new ClassParser("initclasses/Object.class")).parse());
        classTable.add((new ClassParser("initclasses/Object.class")).parse());
        classTable.add((new ClassParser("initclasses/String.class")).parse());

        //loading main class
        JavaClass mainClass = (new ClassParser(args[0])).parse();
//        classTable.put(mainClass.getClassName(), mainClass);
        classTable.add(mainClass);

        ReferenceValue args2 = null;//pole objektů typu initclasses.String
        ReferenceValue stringClassRef;
        if (args.length > 1) {
            stringClassRef = getJavaClassRef("initclasses/String");
            args2 = heap.allocateArray(args.length - 1, 4, stringClassRef.getValue());
            for (int i = 1; i < args.length; i++) {
                ReferenceValue stringRef = heap.allocateObject(stringClassRef);
                int stringLength = args[i].length();
                heap.storeInt(new IntValue(stringLength), stringRef, Heap.OBJECT_HEAD_SIZE);
                ReferenceValue charArrayRef = heap.allocateArray(args[i].length(), 2, 5);
                for (int j = 0; j < stringLength; j++) {
                    heap.storeCharToArray(new CharValue(args[i].charAt(j)), charArrayRef, new IntValue(j));
                }
                heap.storeRef(charArrayRef, stringRef, Heap.OBJECT_HEAD_SIZE + 4);
                heap.storeRefToArray(stringRef, args2, new IntValue(i - 1));
            }
        } else if (args.length == 0) {
            System.out.println("Nezadal jste název spouštěného programu!");
            return;
        }

        Value[] mainArgs = null;
        if (args2 != null) {
            mainArgs = new Value[1];
            mainArgs[0] = args2;
        }

        frameStack.push(new Frame(mainClass.getMethods()[1], mainArgs, null, null));
        frameStack.peek().start();
        //program doběhl
        //uklidim stack
        frameStack.pop();
        //uklidim haldu a classTable?
        heap.dumbHeap();

//        System.out.println(classTable.get("testapp.TestApp").getMethods()[0]);
//        System.out.println(((ConstantInteger)classTable.get("testapp.TestApp").getConstantPool().getConstant(2)).getBytes());
    }

    //TODO predělat všechno na StringValue nebo ReferenceValue, až bude 
    private static Value[] parseMainArguments(String[] args) {
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

    public static JavaClass getJavaClass(String className) throws IOException {
//        JavaClass result = classTable.get(className);
        if (className.equals("java/lang/Object")) {
            className = "initclasses/Object";
        }
        JavaClass result = null;
        for (JavaClass clazz : classTable) {
            if (clazz.getClassName().equals(className)) {
                result = clazz;
                break;
            }
        }
        if (result == null) {
            result = (new ClassParser(className + ".class")).parse();
            //chybí alokace a inicializace třídních proměnných
//            classTable.put(className, result);
            classTable.add(result);
        }
        return result;
    }

    public static ReferenceValue getJavaClassRef(String className) throws IOException {
        if (className.equals("java/lang/Object")) {
            className = "initclasses/Object";
        }
        int result = -1;
        for (int i = 0; i < classTable.size(); i++) {
            if (classTable.get(i).getClassName().equals(className)) {
                result = i;
                break;
            }
        }
        if (result == -1) {
            JavaClass newClass = (new ClassParser(className + ".class")).parse();
            classTable.add(newClass);
            //chybí alokace a inicializace třídních proměnných
//            classTable.put(className, result);
            result = classTable.indexOf(newClass);
        }
        return new ReferenceValue(result);
    }

    public static JavaClass getJavaClassByIndex(ReferenceValue classRef) {
        return classTable.get(classRef.getValue());
    }

    public static void callMethod(Method method, Value[] arguments, ReferenceValue thisHeapIndex, Frame invoker) throws Exception {
        frameStack.push(new Frame(method, arguments, thisHeapIndex, invoker));
        frameStack.peek().start();
        frameStack.pop();
    }

    //pomocná funkce na výpis unsigned bytů
    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }

}
