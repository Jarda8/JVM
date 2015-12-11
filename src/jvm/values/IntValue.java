package jvm.values;

/**
 *
 * @author Jaroslav Ševčík
 */
public class IntValue extends Value{
    private int value;

    public IntValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
}
