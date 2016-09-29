package src.datatype;

/**
 *
 * @author Lucas
 */
public class Var extends Data {

    private final String name;
    private final Data value;

    public Var(String name, Data value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public Data getData() {
        return this.value;
    }
}
