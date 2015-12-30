package initclasses;

public class File {
    private String fileName;

    public File(String fileName) {
        this.fileName = fileName;
    }
    
    public native int[] readInt(int number);
    
}
