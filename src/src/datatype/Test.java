/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.datatype;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import src.inputparser.ParseException;
import src.interpreter.InterpreterException;
import src.interpreter.ProgramParser;

/**
 *
 * @author Lucas
 */
public class Test {
    public static void main(String[] args) {
        
        Data ex1 = new Num("1936310");
        Data ex2 = new Num("1936311");
        
        Test test = new Test(ex1, ex2);
        test.eq();
    }
    
    private final Data expr1;
    private final Data expr2;
    
    private Test(Data ex1, Data ex2) {
        this.expr1 = ex1;
        this.expr2 = ex2;
    }
    
    private void eq() {
        Data ex11 = expr1;
        Data ex12 = expr2;
        //eq1(ex11, ex12);
        Data ex21 = expr1;
        Data ex22 = expr2;
        //eq2(ex21, ex22);
        Data ex31 = expr1;
        Data ex32 = expr2;
        eq3(ex31, ex32);
    }
    
    private void eq1(Data expr1, Data expr2) {
        System.out.println("Eq1 ---------");
        long startTime = System.currentTimeMillis();
        System.out.println(expr1.equals1(expr2));
        System.out.println("Time:" + (System.currentTimeMillis()-startTime));
    }
    
    private void eq2(Data expr1, Data expr2) {
        System.out.println("Eq2 ---------");
        long startTime = System.currentTimeMillis();
        System.out.println(expr1.equals2(expr1, expr2));
        System.out.println("Time:" + (System.currentTimeMillis()-startTime));
    }
    
    private void eq3(Data expr1, Data expr2) {
        System.out.println("Eq3 ---------");
        long startTime = System.currentTimeMillis();
        System.out.println(expr1.equals3(expr2));
        System.out.println("Time:" + (System.currentTimeMillis()-startTime));
    }
}
