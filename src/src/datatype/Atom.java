package src.datatype;

/**
 *
 * @author Lucas
 */
public class Atom extends Data {
    
    private final String value;
    private final Data rawData;
    
    public Atom(String s) {
        this.value = s;
        switch (s) {
            case "quote":
                rawData = makeTree(1);
                break;
                
            case "var":
            	rawData = makeTree(2);
            	break;
                
            case "cons":
                rawData = makeTree(3);
                break;
                
            case "hd":
                rawData = makeTree(4);
                break;
                
            case "tl":
                rawData = makeTree(5);
                break;
                
            case ":=":
                rawData = makeTree(6);
                break;
                
            case "while":
                rawData = makeTree(7);
                break;
                
            case "if":
                rawData = makeTree(8);
                break;
                
            default:
                throw new AssertionError("Atom not found");
        }
    }
    
    private Data makeTree(int generations) {
        Data returnTree = new Nil();
        for (int i = 0; i < generations; i++) {
            returnTree = new Tree(returnTree, new Nil());
        }
        return returnTree;
    }
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public Data getData() {
        return rawData;
    }
}
