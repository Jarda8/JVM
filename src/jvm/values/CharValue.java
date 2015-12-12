package jvm.values;

/**
 *
 * @author Jaroslav Ševčík
 */
public class CharValue extends Value{
    private char value;

    public CharValue(char value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }
    
}
