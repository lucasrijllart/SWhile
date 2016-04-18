package src.datatype;

/**
 *
 * @author Lucas
 */
public class Var extends Data {

    private final String name;
    private final Data value;

    /**
     *
     * @param name
     * @param value
     */
    public Var(String name, Data value) {
        this.name = name;
        this.value = value;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return
     */
    @Override
    public Data getData() {
        return this.value;
    }
}
