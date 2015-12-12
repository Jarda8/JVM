package jvm.values;

/**
 *
 * @author Jaroslav Ševčík
 */
public class ReferenceValue extends Value{
    //Tohle bude index někam, většinou (vždy?) asi na haldu.
    private int value;

    public ReferenceValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
}
