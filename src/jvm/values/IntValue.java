package jvm.values;

/**
 *
 * @author Jaroslav Ševčík
 */
public class IntValue extends Value{
    private int value;

    public IntValue(int value) {
        super(Value.INT_TAG);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    public IntValue add(IntValue x) {
        return new IntValue(value + x.getValue());
    }
    
    public IntValue sub(IntValue x) {
        return new IntValue(value - x.getValue());
    }
    
    public IntValue mul(IntValue x) {
        return new IntValue(value * x.getValue());
    }
    
    public IntValue div(IntValue x) {
        return new IntValue(value / x.getValue());
    }
    
    public void inc(IntValue v) {
        value += v.getValue();
    }
    
}
