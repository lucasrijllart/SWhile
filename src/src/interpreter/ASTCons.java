/* Generated By:JJTree: Do not edit this line. ASTCons.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=BaseNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package src.interpreter;

public
class ASTCons extends SimpleNode {
  public ASTCons(int id) {
    super(id);
  }

  public ASTCons(ProgramParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ProgramParserVisitor visitor, Object data) throws InterpreterException {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=acfec89b00810d2463fca9bce10cf45a (do not edit this line) */
