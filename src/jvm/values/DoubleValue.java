package jvm.values;

/**
 *
 * @author Jaroslav Ševčík
 */
public class DoubleValue extends Value{
    private double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
}
