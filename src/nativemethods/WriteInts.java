package nativemethods;

import java.io.PrintWriter;
import jvm.Heap;
import jvm.JVM;
import jvm.frame.Frame;
import jvm.values.*;

public class WriteInts implements NativeMethod {

    @Override
    public void start(Value[] arguments, ReferenceValue thisHeapIndex, Frame invoker) throws Exception {
        ReferenceValue stringFileName = JVM.heap.fetchRef(thisHeapIndex, Heap.OBJECT_HEAD_SIZE);
        ReferenceValue charArrayFileName = JVM.heap.fetchRef(stringFileName, Heap.OBJECT_HEAD_SIZE);
        IntValue fileNameLength = JVM.heap.getArrayLength(charArrayFileName);
        char[] charArray = new char[fileNameLength.getValue()];
        for (int i = 0; i < fileNameLength.getValue(); i++) {
            charArray[i] = JVM.heap.fetchCharFromArray(charArrayFileName, new IntValue(i)).getValue();
        }
        String fileName = new String(charArray);
        PrintWriter pw = new PrintWriter(fileName);
        
        ReferenceValue intArray = (ReferenceValue) arguments[0];
        int length = JVM.heap.getArrayLength(intArray).getValue();
        
        for (int i = 0; i < length; i++) {
//            JVM.heap.storeIntToArray(new IntValue(scanner.nextInt()), result, new IntValue(i));
            pw.print(JVM.heap.fetchIntFromArray(intArray, new IntValue(i)));
            pw.print(" ");
        }
    }
}
