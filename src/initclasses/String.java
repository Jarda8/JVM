package initclasses;

public class String {
    int length;
    char[] value;

    public String(java.lang.String s) {
        length = s.length();
        value = new char[length];
        for (int i = 0; i < length; i++) {
            value[i] = s.charAt(i);
        }
    }
}
