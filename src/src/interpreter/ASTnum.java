/* Generated By:JJTree: Do not edit this line. ASTnum.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=BaseNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package src.interpreter;

public
class ASTnum extends SimpleNode {
  public ASTnum(int id) {
    super(id);
  }

  public ASTnum(ProgramParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ProgramParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=7ef044fb73ae57aa37d66366096e501e (do not edit this line) */
