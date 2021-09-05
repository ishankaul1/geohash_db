package ishankaul.personal.dbquery.ast;

import ishankaul.personal.dbquery.ast.visitor.QueryVisitor;
import ishankaul.personal.dbquery.expr.Expression;

import java.util.List;

public class ExpressionNode implements Node {

    private ExpressionFactory expressionFactory;

    private Node leftParen = new LParenNode();

    private Node operation;

    private List<Node> arguments;

    private Node rightParen = new RParenNode();

    public ExpressionNode(Node operation, List<Node> arguments) {
        this.operation = operation;
        this.arguments = arguments;
        this.expressionFactory = new ExpressionFactory();
    }

    public Node getOperation() {
        return operation;
    }

    public void setOperation(Node operation) {
        this.operation = operation;
    }

    public List<Node> getArguments() {
        return arguments;
    }

    public void setArguments(List<Node> arguments) {
        this.arguments = arguments;
    }

    public void accept(AstVisitor visitor){
        visitor.visit(this);
        //TODO: make this work by having each operation accept the visitor
        /*if (visitor instanceof QueryVisitor){
            return;
        }*/
        leftParen.accept(visitor);
        operation.accept(visitor);
        for(Node arg : arguments){
            arg.accept(visitor);
        }
        rightParen.accept(visitor);
    }
}
