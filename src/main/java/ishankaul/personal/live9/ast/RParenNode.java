package ishankaul.personal.live9.ast;

public class RParenNode implements Node {
    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}