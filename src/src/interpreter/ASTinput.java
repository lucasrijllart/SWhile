/* Generated By:JJTree: Do not edit this line. ASTinput.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=BaseNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package src.interpreter;

public
class ASTinput extends SimpleNode {
  public ASTinput(int id) {
    super(id);
  }

  public ASTinput(ProgramParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ProgramParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=7205e80c650c060ae8211cf7f19968ee (do not edit this line) */
