package nativemethods;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import jvm.Heap;
import jvm.JVM;
import jvm.frame.Frame;
import jvm.values.*;

public class ReadInts implements NativeMethod{
    @Override
    public void start(Value[] arguments, ReferenceValue thisHeapIndex, Frame invoker) throws Exception {
        ReferenceValue result = JVM.heap.allocateArray(((IntValue) arguments[0]).getValue(), 4, 10);
        ReferenceValue stringFileName = JVM.heap.fetchRef(thisHeapIndex, Heap.OBJECT_HEAD_SIZE);
        ReferenceValue charArrayFileName = JVM.heap.fetchRef(stringFileName, Heap.OBJECT_HEAD_SIZE);
        IntValue fileNameLength = JVM.heap.getArrayLength(charArrayFileName);
        char[] charArray = new char[fileNameLength.getValue()];
        for (int i = 0; i < fileNameLength.getValue(); i++) {
            charArray[i] = JVM.heap.fetchCharFromArray(charArrayFileName, new IntValue(i)).getValue();
        }
        String fileName = new String(charArray);
        Path path = Paths.get(fileName);
        Scanner scanner = new Scanner(path);
        for(int i = 0; i < ((IntValue) arguments[0]).getValue(); i++) {
            JVM.heap.storeIntToArray(new IntValue(scanner.nextInt()), result, new IntValue(i));
        }
        scanner.close();
        invoker.pushOnStack(result);
    }
}
