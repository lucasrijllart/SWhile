package src.datatype;

/**
 *
 * @author Lucas Rijllart
 */
public class Atom extends Data {
    
    private final String value;
    private final Data rawData;
    
    /**
     *
     * @param s holds the name of the atom, which is then created depending on
     *          its name
     */
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
    
    /**
     * 
     * @param generations   number of generations of nodes that need to be
     *                      created
     * @return              the constructed binary tree
     */
    private Data makeTree(int generations) {
        Data returnTree = new Nil();
        for (int i = 0; i < generations; i++) {
            returnTree = new Tree(returnTree, new Nil());
        }
        return returnTree;
    }
    
    /**
     *
     * @return string value of the atom
     */
    public String getValue() {
        return this.value;
    }
    
    /**
     *
     * @return data value of the atom
     */
    @Override
    public Data getData() {
        return rawData;
    }
}
