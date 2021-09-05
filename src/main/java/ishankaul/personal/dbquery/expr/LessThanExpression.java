package ishankaul.personal.dbquery.expr;

public class LessThanExpression<T> extends ComparisonExpression {

    private Expression leftChild;
    private Expression rightChild;

    public Expression getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Expression leftChild) {
        this.leftChild = leftChild;
    }

    public Expression getRightChild() {
        return rightChild;
    }

    public void setRightChild(Expression rightChild) {
        this.rightChild = rightChild;
    }

    @Override
    public Boolean evaluate(Context ctx) {
        Double lhs = (Double)leftChild.evaluate(ctx);
        Double rhs = (Double)rightChild.evaluate(ctx);

        return lhs < rhs;
    }


}
