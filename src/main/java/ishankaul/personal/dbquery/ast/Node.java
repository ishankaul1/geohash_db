package ishankaul.personal.dbquery.ast;

public interface Node {

    public void accept(AstVisitor visitor);
}
