package src.datatype;

public class Bool extends Data {
    private final boolean value;
    
    public Bool(String s) {
        this.value = s.equals("true");
    }
    
    public boolean getValue() {return this.value;}

    @Override
    public Data getData() {
        if (this.value) {
            return new Tree(new Nil(), new Nil());
        } else {
            return new Nil();
        }
    }
}