/* DataToProgram.java */
/* Generated By:JavaCC: Do not edit this line. DataToProgram.java */
package datatoprogram;

import java.util.ArrayList;

public class DataToProgram implements DataToProgramConstants {

  private static DataToProgram DTPparser = null;
  private static long startTime = System.currentTimeMillis();

  public static void main(String[] args) {
    try {
      java.util.Scanner scanner = new java.util.Scanner(new java.io.File(args[0]));
      String text = scanner.useDelimiter("\u005c\u005cA").next();
      scanner.close();
      long startTime = System.currentTimeMillis();
      run(text);
      System.out.println((System.currentTimeMillis()-startTime) + " ");

    } catch (Throwable e) {
    }
  }

  public static String run(String input) throws ParseException {
    String dataAsProgram = "";
     //args[0]
      if (DTPparser == null) {
        DTPparser = new DataToProgram(new java.io.StringReader(input));
      } else {
        ReInit(new java.io.StringReader(input));
      }
      dataAsProgram = start();
      String indented = "";
      int indent = 0;
      String[] lines;
      try{
        lines = dataAsProgram.split(System.getProperty("line.separator"));
      } catch (Throwable e) {
        lines = dataAsProgram.split("\u005cn");
      }

      for(String s : lines) { //for every line
        if(s.contains(">")){ indent += 2; } //if > then +3
        if(s.contains("<")){ indent -= 2; } //if < then -3
        s = s.replaceAll(">", ""); //remove all > < signs
        s = s.replaceAll("<", "");
        indented += new String(new char[indent]).replace("\u005c0", " ") + s + "\u005cn";
      }

    return indented;
  }

  static final public String start() throws ParseException {Program p;
    p = program();
{if ("" != null) return p.print;}
    throw new Error("Missing return statement in function");
  }

  static final public Program program() throws ParseException {String in, out; int inputVar, outputVar; Block S;
    jj_consume_token(17);
    in = jj_consume_token(NUM).image;
inputVar = Integer.parseInt(in);
    jj_consume_token(18);
    S = block();
    jj_consume_token(18);
    out = jj_consume_token(NUM).image;
outputVar = Integer.parseInt(out);
    jj_consume_token(19);
{if ("" != null) return new Program(inputVar, S, outputVar);}
    throw new Error("Missing return statement in function");
  }

  static final public Block block() throws ParseException {Statements s = new Statements(new ArrayList<Cmd>());
    jj_consume_token(17);
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case 17:{
      s = statements();
      break;
      }
    default:
      jj_la1[0] = jj_gen;
      ;
    }
    jj_consume_token(19);
{if ("" != null) return new Block(s);}
    throw new Error("Missing return statement in function");
  }

  static final public Statements statements() throws ParseException {Cmd c;
  Statements s2 = new Statements(new ArrayList<Cmd>());
    c = command();
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case 18:{
      jj_consume_token(18);
      s2 = statements();
      break;
      }
    default:
      jj_la1[1] = jj_gen;
      ;
    }
ArrayList<Cmd> cmds = new ArrayList<Cmd>(); //List of commands
    cmds.add(c);
    for(Cmd cmd : s2.cmds) {
      cmds.add(cmd);
    }
    {if ("" != null) return new Statements(cmds);}
    throw new Error("Missing return statement in function");
  }

  static final public Cmd command() throws ParseException {String s;
                int v;
                Expr e;
                Block b;
                Block b2 = null;
    jj_consume_token(17);
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case ASSIGN:{
      jj_consume_token(ASSIGN);
      jj_consume_token(18);
      s = jj_consume_token(NUM).image;
v = Integer.parseInt(s);
      jj_consume_token(18);
      e = expr();
      jj_consume_token(19);
{if ("" != null) return new Assign(v, e);}
      break;
      }
    case WHILE:{
      jj_consume_token(WHILE);
      jj_consume_token(18);
      e = expr();
      jj_consume_token(18);
      b = block();
      jj_consume_token(19);
{if ("" != null) return new While(e, b);}
      break;
      }
    case IF:{
      jj_consume_token(IF);
      jj_consume_token(18);
      e = expr();
      jj_consume_token(18);
      b = block();
      jj_consume_token(18);
      b2 = block();
      jj_consume_token(19);
{if ("" != null) return new If(e, b, b2);}
      break;
      }
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public Expr expr() throws ParseException {Expr e; String s;
    jj_consume_token(17);
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case QUOTE:{
      jj_consume_token(QUOTE);
      jj_consume_token(18);
      jj_consume_token(NIL);
      jj_consume_token(19);
{if ("" != null) return new Nil();}
      break;
      }
    case CONS:{
      jj_consume_token(CONS);
      jj_consume_token(18);
      e = expr();
      jj_consume_token(18);
Expr e2;
      e2 = expr();
      jj_consume_token(19);
{if ("" != null) return new Cons(e, e2);}
      break;
      }
    case HD:{
      jj_consume_token(HD);
      jj_consume_token(18);
      e = expr();
      jj_consume_token(19);
{if ("" != null) return new Hd(e);}
      break;
      }
    case TL:{
      jj_consume_token(TL);
      jj_consume_token(18);
      e = expr();
      jj_consume_token(19);
{if ("" != null) return new Tl(e);}
      break;
      }
    case VAR:{
      jj_consume_token(VAR);
      jj_consume_token(18);
      s = jj_consume_token(NUM).image;
      jj_consume_token(19);
{if ("" != null) return new Var(Integer.parseInt(s));}
      break;
      }
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public DataToProgramTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[4];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x20000,0x40000,0x380,0xf400,};
   }

  /** Constructor with InputStream. */
  public DataToProgram(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public DataToProgram(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new DataToProgramTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public DataToProgram(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new DataToProgramTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public DataToProgram(DataToProgramTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(DataToProgramTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 4; i++) jj_la1[i] = -1;
  }

  static private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static private int jj_ntk_f() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List<int[]> jj_expentries = new ArrayList<int[]>();
  static private int[] jj_expentry;
  static private int jj_kind = -1;

  /** Generate ParseException. */
  static public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[20];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 4; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 20; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

}

class Program { //[progname, varnum1, "S", varnum2]
  String print;

  public Program(int inputvarnum, Block block, int outputvarnum) {
    //String indentation = new String(new char[indent]).replace("\0", " ");
    this.print = "progname read " + new Var(inputvarnum).print + block.print + "\u005cnwrite " + new Var(outputvarnum).print;
  }
}

class Block {
  String print;
  public Block(Statements statements) {
    this.print = " {\u005cn>" + statements.print + "\u005cn<}";
  }
}

class Statements {
  ArrayList<Cmd> cmds;
  String print;
  public Statements(ArrayList<Cmd> cmds) {
    this.cmds = cmds;
    String getAllCmds = ""; //cumulative string for commands
    if(cmds.isEmpty()) { //if empty statements
      this.print = "";
    } else { //if not empty statements
      for(int i = 0; i < cmds.size(); i++) { //for each cmd
        getAllCmds += cmds.get(i).print; //add cmd to string
        if(i != cmds.size()-1) { //if not last object
          getAllCmds += ";\u005cn"; //include comma
        }
      }
      this.print = getAllCmds; //add square brackets around
    }
  }
}

abstract class Cmd { String print; }
class Assign extends Cmd { //[:=, varnum, "E"]
  public Assign(int V, Expr E) {
    super.print = new Var(V).print + " := " + E.print;
  }
}
class While extends Cmd { //[while, "E", "B"]
  public While(Expr E, Block B) {
    super.print = "while " + E.print + B.print;
  }
}
class If extends Cmd { //[if, "E", "BT", "BE"]
  public If(Expr E, Block Bt, Block Be) {
    if(Be.print.equals(" {\u005cn>\u005cn<}")) {
      super.print = "if " + E.print + Bt.print;
    } else {
      super.print = "if " + E.print + Bt.print + " else" + Be.print;
    }
  }
}

abstract class Expr { String print; }
class Nil extends Expr { //[quote, nil]
  public Nil() { super.print = "nil"; }
}
class Var extends Expr { //[var, varnum]
  public Var(int num) { super.print = "var" + num; }
}
class Cons extends Expr { //[cons, "E", "F"]
  public Cons(Expr E, Expr F) { super.print = "cons " + E.print + " " + F.print; }
}
class Hd extends Expr { //[hd, "E"]
  public Hd(Expr E) { super.print = "hd " + E.print; }
}
class Tl extends Expr { //[tl, "E"]
  public Tl(Expr E) {super.print = "tl " + E.print; }
}
