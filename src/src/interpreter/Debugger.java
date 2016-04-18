package src.interpreter;

/*
 version 0.1.2
 */
import src.datatype.*;

import java.util.HashMap;
import java.util.LinkedList;

public class Debugger implements ProgramParserVisitor {

    //interpreting stack
    private LinkedList<Data> stack;
    //Variables hashmap
    public HashMap<String, Data> symbolTable;
    // makeshift log
    private Log log = new Log();
    // fine string
    public String fine;
    // finer string
    public String finer;
    //start of interpreting
    private long startTime;

    public boolean finished = false;
    private boolean inFirstBlockStatic = true;
    private boolean everyBlockFinished = true;

    private int shouldVisit; //for the amount of instructions to visit
    private int child = 0; //current child

    public Debugger(int shouldVisit) {
        this.shouldVisit = shouldVisit;
    }
    
    class Log {
        String fine;
        String finer;
        public Log() {
            this.fine = "";
            this.finer = "";
        }
        public void fine(String s) {
            fine += s + "\n";
            finer += s + "\n";
        }
        public void finer(String s) { finer += s + "\n"; }
        public String getFine() { return fine; }
        public String getFiner() { return finer; }
    }
    
    @Override
    public Object visit(SimpleNode node, Object data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //program
    @Override
    public Object visit(ASTProgram node, Object data) throws InterpreterException {
        startTime = System.currentTimeMillis();
        log.finer("Starting debugger");
        stack = new LinkedList<>();
        symbolTable = new HashMap<>();

        node.childrenAccept(this, data);

        //log.fine("\nFinished in " + (System.currentTimeMillis()-startTime) + "ms:\n" + "symbolTable:\t" + symbolTable + "\nstack:\t" + stack + ": " + stack.get(0).dataToString() + "\n ");

        //add logs
        fine = log.getFine();
        finer = log.getFiner();
        return null; //return object representation
    }

    //progname()
    @Override
    public Object visit(ASTprogname node, Object data) throws InterpreterException {
        String progname = node.data.get("progname").toString();
        log.finer("Program name: " + progname);
        return null;
    }

    //input()
    @Override
    public Object visit(ASTinput node, Object data) throws InterpreterException {
        Data getInput = (Data) node.data.get("input"); //get input
        String getInputVar = (String) node.data.get("inputVar"); //get var

        log.finer("Got input: " + getInput.getClass().getSimpleName() + ":" + getInput.dataToString());
        log.finer("Got inputVar: " + getInputVar);

        //add program name to symboltable
        symbolTable.put(getInputVar, getInput);
        log.fine("Input = " + getInputVar + ":" + getInput.dataToString());

        return null;
    }

    //output()
    @Override
    public Object visit(ASToutput node, Object data) throws InterpreterException {
        Object getVarName = node.data.get("outputVar");
        Data outputVarData = null;
        String varName = null;

        if (getVarName instanceof String) {
            varName = (String) getVarName;
            if (symbolTable.containsKey(varName)) { //if does exists
                outputVarData = symbolTable.get(varName);
                log.finer("Found var: " + getVarName);
            } else { //if it doesn't exist THROW WARNING!!!!!
                symbolTable.put(varName, new Nil()); //make Nil
                outputVarData = symbolTable.get(varName);
                log.fine("New var on output!");
            }
        } else {
            //throw exception var name not found (not string)
        }

        //push data of var on stack
        stack.push(outputVarData);
        log.fine("Output = " + varName + ":" + outputVarData.dataToString());
        return null;
    }

    //block visits a specified amount of children
    @Override
    public Object visit(ASTBlock node, Object data) throws InterpreterException {
        boolean inFirstBlock = false;
        int currentChild = 0;
        int currentShouldVisit = shouldVisit;
        if (inFirstBlockStatic) {
            inFirstBlock = true;
            inFirstBlockStatic = false;
        }
        System.out.println("In block, shouldVisit:" + currentShouldVisit + " |static:" + shouldVisit);
        System.out.println("Child:" + currentChild + " |total:" + node.jjtGetNumChildren());
        while (currentShouldVisit > 0 && currentChild < node.jjtGetNumChildren()) {
            System.out.println("- Visiting child:" + currentChild);
            node.jjtGetChild(currentChild).jjtAccept(this, data);
            currentShouldVisit -= 1;
            shouldVisit -=1;
            currentChild += 1;
            System.out.println("static after visit:" + shouldVisit);
            System.out.println("shouldVisit:" + currentShouldVisit + " > 0:" + (currentShouldVisit>0));
            System.out.println("child:" + currentChild + " total:" + node.jjtGetNumChildren() + " child<total:" + (currentChild < node.jjtGetNumChildren()) + "\n");
        }
        shouldVisit = currentShouldVisit;
        System.out.println("Out of enclosing block. shouldVisit:" + shouldVisit);

        if (!(currentChild == node.jjtGetNumChildren())) {
            System.out.println("Every block finished: false");
            everyBlockFinished = false;
        } else {
            System.out.println("Every block finished: still true");
        }

        System.out.println("inFirstBlock:" + inFirstBlock + " & everyBlockFinished:" + everyBlockFinished);
        if (inFirstBlock &&  everyBlockFinished) {
            System.out.println("FINISHED");
            finished = true;
        }

        return null;
    }

    //ASSIGN (<VAR> := <EXPR>) 2 children
    @Override
    public Object visit(ASTAssign node, Object data) throws InterpreterException {
        node.childrenAccept(this, data); //get <var> and <expr>

        Data valueToAssign = stack.pop(); //get <expr>
        //if it's a var (X:=Y) then it contains the var's value not the object
        if (valueToAssign instanceof Var || valueToAssign instanceof Atom)
        	valueToAssign = valueToAssign.getData();
        
        log.finer("Value: " + valueToAssign.dataToString());

        Var varToAssign = (Var) stack.pop(); //get <var>

        symbolTable.put(varToAssign.getName(), valueToAssign);
        log.fine("Assign: " + varToAssign.getName() + "=" + valueToAssign.dataToString());

        return null;
    }

    //WHILE (while <expr> <block>)
    @Override
    public Object visit(ASTWhile node, Object data) throws InterpreterException {
        //visit while expr condition
        node.jjtGetChild(0).jjtAccept(this, data);

        Data expr;

        expr = stack.pop();
        if (expr instanceof Var) expr = expr.getData();
        log.fine("While (" + expr.dataToString() + ")");

        System.out.println("Arrived in while, shouldVisit:" + shouldVisit + " expr:" + expr.dataToString());

        while (shouldVisit > 0 && expr.getData() instanceof Tree) {

            shouldVisit -= 1;

            //visit block
            node.jjtGetChild(1).jjtAccept(this, data);

            //update expr
            node.jjtGetChild(0).jjtAccept(this, data);
            expr = stack.pop();
            if (expr instanceof Var) expr = expr.getData();
            System.out.println("While expr:" + expr.dataToString());

            if (expr.getData() instanceof Nil) {
                everyBlockFinished = true;
            } else {
                everyBlockFinished = false;
            }

            System.out.println("End of WHILE");
        }
        System.out.println("OUT OF WHILE LLOOPP");

        return null;
    }

    //IF (if <expr> <block> <elseblock>)
    @Override
    public Object visit(ASTIf node, Object data) throws InterpreterException {
        node.jjtGetChild(0).jjtAccept(this, data); //visit var

        //condition
        Data expr = stack.pop();
        if (expr instanceof Var) expr = expr.getData();
        log.fine("If (" + expr.dataToString() + ")");

        //if Tree then visit block, else (nil) don't
        if (expr.getData() instanceof Tree) { //if condition met
            log.finer("If expr is tree: " + expr.dataToString());

            //visit THEN block
            node.jjtGetChild(1).jjtAccept(this, data);

        } else if (expr.getData() instanceof Nil) { //if condition not met
            log.finer("If nil");
            if (node.jjtGetNumChildren() == 3) { //if there is an else block
                log.fine("Else:");
                node.jjtGetChild(2).jjtAccept(this, data); //visit else block
            } //else do nothing
        } else { //exception
            throw new UnsupportedOperationException("If expr is not tree or nil");
        }

        return null;
    }

    //NIL
    @Override
    public Object visit(ASTnil node, Object data) throws InterpreterException {
        stack.push(new Nil());
        log.finer("new nil");
        return null;
    }

    //CONS (cons <expr> <expr>)
    @Override
    public Object visit(ASTCons node, Object data) throws InterpreterException {
        node.childrenAccept(this, data); //puts two exprs on the stack

        //get expr2 (first on stack)
        Data expr2 = stack.pop();
        //get expr1 (second)
        Data expr1 = stack.pop();
        //new data expr after cons
        Data newExpr;
        
        //get values if vars
        if (expr1 instanceof Var) {
            expr1 = expr1.getData();
        }
        if (expr1 instanceof Atom) {
            expr1 = expr1.getData();
        }
        if (expr2 instanceof Var) {
            expr2 = expr2.getData();
        }
        if (expr2 instanceof Atom) {
            expr2 = expr2.getData();
        }
        
        log.finer("expr1:" + expr1 + " & expr2:" + expr2);
        
        if (expr1 instanceof Nil) { //1: NIL
             if (expr2 instanceof Num) { //2: Num
                Num n = (Num) expr2;
                Integer value = n.getInt() + 1;
                newExpr = new Num(value.toString());
            } else if (expr2 instanceof Bool) { //2: Bool
                //if bool is false, true
                Bool b = (Bool) expr2;
                if (!b.getValue()) {
                    newExpr = new Bool("true");
                } else { //else <nil.true>
                    newExpr = new Tree(expr1.getData(), expr2.getData());
                }
            } else if (expr2 instanceof List) { //2: List
                List l = (List)expr2;
                l.addElement(expr1);
                newExpr = l;
            } else { //2: NIL, TREE
                newExpr = new Tree(expr1.getData(), expr2.getData());
            }
        } else if (expr1 instanceof Num) { //1: NUM
            if (expr2 instanceof List) { //2: LIST
                List l = (List)expr2;
                l.addElement(expr1);
                newExpr = l;
            } else { //2: NIL, TREE, NUM, BOOL
                newExpr = new Tree(expr1.getData(), expr2.getData());
            }
        } else if (expr1 instanceof Bool) { //1: BOOL
            if (expr2 instanceof Nil) { //2: NIL
                if (!((Bool)expr1).getValue()) {
                    newExpr = new Bool("true");
                } else {
                    newExpr = new Tree(expr1.getData(), expr2.getData());
                }
            } else if (expr2 instanceof List) { //2: LIST
                List l = (List)expr2;
                l.addElement(expr1);
                newExpr = l;
            } else { //2: NUM, TREE, BOOL
                newExpr = new Tree(expr1.getData(), expr2.getData());
            }
        } else if (expr1 instanceof List) { //1: LIST
            if (expr2 instanceof Nil) { //2: NIL
                LinkedList<Data> list = new LinkedList<>();
                list.add(expr1);
                newExpr = new List(list);
            } else if (expr2 instanceof List) { //2: LIST
                List l = (List) expr1;
                l.addElement(expr2);
                newExpr = l;
            } else { //2: NUM, TREE, BOOL
                newExpr = new Tree(expr1.getData(), expr2.getData());
            }
        } else if (expr1 instanceof Atom) { //1: ATOM
            if (expr2 instanceof List) { //2: LIST
                List l = (List) expr2;
                l.addElement(expr1.getData());
                newExpr = l;
            } else { //2: NIL, TREE, NUM, BOOL
                newExpr = new Tree(expr1.getData(), expr2.getData());
            }
        } else { //1: TREE
            newExpr = new Tree(expr1.getData(), expr2.getData());
        }
        
        log.finer("Cons:[" + expr1.dataToString() + ", " + expr2.dataToString() + "] = " + newExpr.dataToString());

        stack.push(newExpr);
        return null;
    }

    //HD (hd <expr>)
    @Override
    public Object visit(ASTHd node, Object data) throws InterpreterException {
         node.childrenAccept(this, data);

        //pop (can be: nil, tree, var, num, bool, list, atom)
        Data expr = stack.pop();
        Data newExpr;

        //if var then get what's in the var
        if (expr instanceof Var) {
            expr = expr.getData();
        }
        System.out.println("1:" + expr);
        if (expr instanceof Atom) {
            expr = expr.getData();
        }
        System.out.println("2:" + expr);
        
        //check what it is to make appropriate action
        if (expr instanceof Bool) { //BOOL
            newExpr = new Bool("false");
        } else if (expr instanceof List) { //LIST
            LinkedList<Data> list = ((List) expr).getList();
            newExpr = list.getFirst();
        } else if (expr instanceof Nil) { //NIL
            newExpr = expr;
        } else if (expr instanceof Num) {
            if (expr.getData() instanceof Nil) {
                newExpr = new Nil();
            } else {
                newExpr = ((Tree) expr.getData()).getLeft();
            }
        } else { //TREE
            newExpr = ((Tree) expr).getLeft();
        }
        log.finer("Hd:" + expr.dataToString() + " = " + newExpr.dataToString());
        stack.push(newExpr);
        return null;
    }

    //TL (tl <expr>)
    @Override
    public Object visit(ASTTl node, Object data) throws InterpreterException {
        node.childrenAccept(this, data);
        
        Data expr = stack.pop();
        Data newExpr;

        //if var then get what's in the var, same for atom
        if (expr instanceof Var) {
            expr = expr.getData();
        }
        if (expr instanceof Atom) {
            expr = expr.getData();
        }
        
        //check what it is to make appropriate action
        if (expr instanceof Nil) { //NIL
            newExpr = expr;
        } else if (expr instanceof Num) { //NUM
            newExpr = new Num(Integer.toString((((Num)expr).getInt())-1));
        } else if (expr instanceof Bool) { //BOOL
            newExpr = new Bool("false");
        } else if (expr instanceof List) { //LIST
            LinkedList<Data> list = ((List) expr).getList();
            list.pop(); //remove top element (head) to only have tail
            newExpr = new List(list);
        } else { //TREE, ATOM
            newExpr = ((Tree) expr).getRight();
        }
        log.finer("Tl:" + expr.dataToString() + " = " + newExpr.dataToString());
        
        stack.push(newExpr);
        return null;
    }

    //VAR
    @Override
    public Object visit(ASTvar node, Object data) throws InterpreterException {
        Object getVarName = node.data.get("varname");
        if (getVarName instanceof String) {
            String varName = (String) getVarName;
            if (symbolTable.containsKey(varName)) { //if does exists
                Data varData = symbolTable.get(varName);
                stack.push(new Var(varName, varData));
                log.finer("Found var: " + varName);
            } else { //if it doesn't exist
                symbolTable.put(varName, new Nil()); //make Nil
                stack.push(new Var(varName, new Nil()));
                log.finer("New var: " + varName);
            }
        } else {
            throw new UnsupportedOperationException("var name not string");
        }
        return null;
    }

    //NUM
    @Override
    public Object visit(ASTnum node, Object data) throws InterpreterException {
        Object getNum = node.data.get("num");
        if (getNum instanceof String) {
            String stringNum = (String) getNum;
            stack.push(new Num(stringNum));
            
        } else {
            throw new UnsupportedOperationException("num isn't string, can't parseInt()");
        }
        return null;
    }

    //BOOL
    @Override
    public Object visit(ASTbool node, Object data) throws InterpreterException {
        //push new bool(value)
        stack.push(new Bool((String) node.data.get("boolean")));

        return null;
    }

    //LIST
    @Override
    public Object visit(ASTExprList node, Object data) throws InterpreterException {
        //gather arguments to pass to List constructor
        LinkedList<Data> argList = new LinkedList<>();
        //get children first to last to construct the list
        for (int i = 1; i <= node.jjtGetNumChildren(); i++) {
            //visit child and put result in stack
            node.jjtGetChild(i-1).jjtAccept(this, data);
            //add child to argList
            argList.add(stack.pop());
        }
        List newList = new List(argList);
        log.finer("New list: " + newList.dataToString());

        stack.push(newList);
        return null;
    }

    //SWICH
    @Override
    public Object visit(ASTSwitch node, Object data) throws InterpreterException {
        //visit 1st child to get expr on stack
        node.jjtGetChild(0).jjtAccept(this, data);
        //Data expr = stack.pop().getData();

        //System.out.println("children:" + node.jjtGetNumChildren());
        //while a case doesn't match, goto next
        int previousSize = stack.size();
        boolean found = false;
        int i = 1; //child pointer
        while (!found && i < node.jjtGetNumChildren()) {
            //System.out.println("stack in while before:" + stack);
            node.jjtGetChild(i).jjtAccept(this, data);
            //System.out.println("stack in while after:" + stack + ":" + stack.size());
            if (stack.size() > previousSize) {
                log.finer("Switch interrupted because case matched");
                stack.pop();
                found = true;
            }
            i++; //increment pointer
        }
        return null;
    }

    //CASE
    @Override
    public Object visit(ASTCase node, Object data) throws InterpreterException {
        //get expr from switch (don't pop) to compare
        Data expr = stack.peek();

        //get case expr list on the stack
        node.jjtGetChild(0).jjtAccept(this, data);
        log.finer("Got " + node.jjtGetChild(0).jjtGetNumChildren() + " case(s)");

        //get each expr of the list
        Data list = stack.pop().getData();
        log.finer("Case exprList: " + list.dataToString());

        //while list != nil, check if head is equal to expr
        boolean found = false;
        while (!(list instanceof Nil) && !found) {
            Tree listAsTree = (Tree) list; //get list as tree
            //if (listAsTree.getLeft() == expr)
            if (listAsTree.getLeft().equality(expr)) {
                log.finer("case matches expr");
                //execute case code
                node.jjtGetChild(1).jjtAccept(this, data);
                //set found to true
                found = true;
            } else {
                log.finer("case doesn't match");
            }
            list = listAsTree.getRight(); //list = tail of list
        }
        if (!found) {
            log.finer("Case ignored");
        } else {
            stack.push(new Nil());
        }
        return null;
    }
    
    @Override
    public Object visit(ASTDefaultCase node, Object data) throws InterpreterException {
        node.childrenAccept(this, data);
        log.fine("Default case");
        return null;
    }
    
    //ATOM
    @Override
    public Object visit(ASTatom node, Object data) {
        Atom a = new Atom(node.data.get("atom").toString());
        
        log.finer("Found atom:" + a.getValue());
        
        stack.push(a);
        return null;
    }

    //EQUALITY
    @Override
    public Object visit(ASTEqual node, Object data) throws InterpreterException {
        node.childrenAccept(this, data);
        System.out.println("stack:" + stack);

        Data expr1 = stack.pop();
        Data expr2 = stack.pop();
        /*
         long start = System.currentTimeMillis();
         boolean equals = expr1.equals2(expr2);
         if (equals) {
         stack.push(new Bool("true"));
         } else {
         stack.push(new Bool("false"));
         }
         System.out.println("equals time: " + (System.currentTimeMillis() - start) + "ms");
         */

        //start = System.currentTimeMillis();
        boolean equals = expr1.equality(expr2);
        if (equals) {
            stack.push(new Bool("true"));
        } else {
            stack.push(new Bool("false"));
        }
        //System.out.println("equali time: " + (System.currentTimeMillis() - start) + "ms");

        //push result on stack
        return null;
    }
    
    //MACRO CALL
    @Override
    public Object visit(ASTMacro node, Object data) throws InterpreterException {
        //get progname
        node.jjtGetChild(0).jjtAccept(this, data);
        String progname = ((Var)stack.pop()).getName();
        log.finer("macro progname:" + progname);
        
        //get prog input
        node.jjtGetChild(1).jjtAccept(this, data);
        Data expr = (Data) stack.pop().getData();
        
        log.finer("macro input:" + expr.dataToString(expr));
        Data output = new Nil();
        try {
            log.fine("Macro: " + progname + " - input: " + expr.dataToString());
            String[] s = {progname, expr.dataToString()};
            ProgramParser programParser = new ProgramParser(s);
            try {
                output = programParser.runProgram();
            } catch (src.inputparser.ParseException ex) {
            }
        } catch (ParseException ex) {
            System.out.println("ParseEx:" + ex);
        } catch (InterpreterException ex) {
            System.out.println("Int ex:" + ex);
        }
        stack.push(output);
        return null;
    }
}
