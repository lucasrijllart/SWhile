package src.datatype;

import java.util.LinkedList;

public abstract class Data {

    public abstract Data getData();

    /*
     Have 2 methods, one that does checks like here and one that converts the
     tree in a list and then compares if both list are the same. Check millisecs
     to see which one is faster.
    
     The first one has as checks:
     1: check if same class
     2: check if head is same class
     3: check if tail is same class
    
     Call recursively so that it checks the head of the head of the head
     until it's nil ?
    
     OR dataToTree(data).equals(dataToTree(data2))
     */
    public boolean equality(Data expr2) {
        return equals3(expr2);
    }

    public boolean equals1(Data ex2) {
    	Data expr1 = this;
        Data expr2 = ex2;
    	//get rid of variables
    	if (expr1 instanceof Var) expr1 = expr1.getData();
        if (expr2 instanceof Var) expr2 = expr2.getData();
        
        String d1 = dataToString(expr1.getData());
        String d2 = dataToString(expr2.getData());
        return d1.equals(d2);
    }
    
    public boolean equals2(Data expr1, Data expr2) {
        if (expr1 instanceof Var) expr1 = expr1.getData();
        if (expr2 instanceof Var) expr2 = expr2.getData();
        expr1 = expr1.getData();
        expr2 = expr2.getData();
        
        if (expr1 instanceof Nil && expr2 instanceof Nil) return true;
        
        if ((expr1 instanceof Nil && expr2 instanceof Tree)
                || expr1 instanceof Tree && expr2 instanceof Nil) {
            return false;
        }
        
        
        Data l1 = ((Tree)expr1).getLeft();
        Data r1 = ((Tree)expr1).getRight();
        Data l2 = ((Tree)expr2).getLeft();
        Data r2 = ((Tree)expr2).getRight();
        if (!equals2(l1, l2)) return false;
        return equals2(r1, r2);
    }
    
    public boolean equals3(Data exp2) {
        Data expr1 = this;
        Data expr2 = exp2;
        if (expr1 instanceof Var) expr1 = expr1.getData();
        if (expr2 instanceof Var) expr2 = expr2.getData();
        boolean EQUALS = true;
        Data X = expr1.getData();
        Data Y = expr2.getData();
        LinkedList<Data> STACK = new LinkedList<>();
        STACK.add(X);
        STACK.add(Y);
        //System.out.println("Stack:" + STACK.size());
        while (!STACK.isEmpty()) {
            Data B = STACK.pop().getData();
            Data A = STACK.pop().getData();
            if (A instanceof Tree) {
                if (B instanceof Tree) {
                    STACK.add(((Tree) A).getRight().getData());
                    STACK.add(((Tree) B).getRight().getData());
                    STACK.add(((Tree) A).getLeft().getData());
                    STACK.add(((Tree) B).getLeft().getData());
                } else {
                    EQUALS = false;
                    STACK.clear();
                }
            } else {
                if (B instanceof Tree) {
                    EQUALS = false;
                    STACK.clear();
                }
            }
        }
        return EQUALS;
    }
    
    //allows to get the representation of data into readable form
    public String dataToString(Data d) {
        System.out.println("Here 1: " + d);
        if (d instanceof Nil) {
            return "nil";
        } else if (d instanceof Num) {
            /*
             Num num = (Num) d;
             Integer numInt = num.getInt();
             String numStr = numInt.toString();
             return numStr;
             */
            return ((Num) d).getInt().toString();
        } else if (d instanceof Var) { //contains other data, recursive call
            /*
             Var var = (Var) d;
             Data varD = var.getData();
             return dataToTree(varD);
             */
            return dataToString(((Var) d).getData());
        } else if (d instanceof List) { //contains other data, recusive call
            String finalOutput = "[";
            System.out.println("Here 2");
            LinkedList<Data> list = ((List)d).getList();
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size()-1; i++) {
                    finalOutput += list.get(i).dataToString() + ",";
                }
                finalOutput += list.getLast().dataToString() + "]";
            } else {
                finalOutput += "]";
            }
            return finalOutput;
        } else if (d instanceof Bool) {
            if (((Bool)d).getValue()) {
                return "true";
            } else {
                return "false";
            }
        } else if (d instanceof Atom) {
            return ((Atom)d).getValue();
        } else { //is Tree
            Tree t = (Tree) d; //get D as a tree object
            String s = "<" + dataToString(t.getLeft()) + "." + dataToString(t.getRight()) + ">";
            return s;
        }
    }
    //allows to get the representation of data into readable form
    public String dataToString() {
        return dataToString(this);
    }
}

    