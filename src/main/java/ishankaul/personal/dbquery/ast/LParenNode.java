package ishankaul.personal.dbquery.ast;

public class LParenNode implements Node{
    @Override
    public void accept(AstVisitor visitor) {
        visitor.visit(this);
    }
}
