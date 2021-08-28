package ishankaul.personal.live9.expr;

public class GreaterThanExpression<T> implements Expression<T,Boolean> {

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
    public Boolean evaluate(Context<T> ctx) {
        Number lhs = (Number)leftChild.evaluate(ctx);
        Number rhs = (Number)rightChild.evaluate(ctx);

        return lhs.doubleValue() > rhs.doubleValue();
    }
}
