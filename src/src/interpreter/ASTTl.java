/* Generated By:JJTree: Do not edit this line. ASTTl.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=BaseNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package src.interpreter;

public
class ASTTl extends SimpleNode {
  public ASTTl(int id) {
    super(id);
  }

  public ASTTl(ProgramParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ProgramParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=ea8d4ec5d86ed8a28188d57c3271fd30 (do not edit this line) */