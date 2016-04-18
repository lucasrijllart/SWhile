package src.datatype;

import java.util.LinkedList;

/**
 *
 * @author Lucas
 */
public class List extends Data {

    private LinkedList<Data> values;

    /**
     *
     * @param l
     */
    public List(LinkedList<Data> l) {
        values = l;
    }
    
    /**
     *
     * @param d
     */
    public void addElement(Data d) {
        this.values.add(d);
    }

    /**
     *
     * @return
     */
    public LinkedList<Data> getList() {
        return this.values;
    }

    /**
     *
     * @return
     */
    @Override
    public Data getData() {
        LinkedList<Data> list = new LinkedList<>();
        for (Data d : this.values) {
            list.add(d);
        }
        Data listData;
        int size = list.size();
        
        if (!list.isEmpty()) {
            listData = new Tree(list.removeLast().getData(), new Nil());
            if (size > 1) {
                    for (int i = 0; i < size-1; i++) {
                    listData = new Tree(list.removeLast().getData(), listData);
                    }
            }
        } else {
            return new Nil();
        }
        return listData;
    }
}
