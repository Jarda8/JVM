package jvm.Frame;

/**
 *
 * @author Jaroslav Ševčík
 */
public class LocalVariable {
    //int nameIndex;
    private Value value;

    public LocalVariable(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
    
}
