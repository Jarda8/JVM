package nativemethods;

import jvm.frame.Frame;
import jvm.values.ReferenceValue;
import jvm.values.Value;

public interface NativeMethod {
    public void start(Value[] arguments, ReferenceValue thisHeapIndex, Frame invoker) throws Exception ;
}
