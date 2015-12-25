package jvm.values;

/**
 *
 * @author Jaroslav Ševčík
 */
public class CharValue extends Value{
    private char value;

    public CharValue(char value) {
        super(Value.CHAR_TAG);
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }
    
}
