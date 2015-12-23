package jvm.values;

/**
 *
 * @author Jaroslav Ševčík
 */
public class ReferenceValue extends Value{
    private int value;

    public ReferenceValue(int value) {
        super(Value.REFERENCE_TAG);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
}
