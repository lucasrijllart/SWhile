/* Generated By:JJTree: Do not edit this line. ASToutput.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=BaseNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package src.interpreter;

public
class ASToutput extends SimpleNode {
  public ASToutput(int id) {
    super(id);
  }

  public ASToutput(ProgramParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ProgramParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=e215abc3053d9c6e8c57e7d393b1e804 (do not edit this line) */
