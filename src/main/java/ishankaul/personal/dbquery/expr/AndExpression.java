package ishankaul.personal.dbquery.expr;

public class AndExpression<T> implements Expression<T,Boolean>{


    //Eventually there should be no left and right child; should be able to 'and' any # of elements
    private Expression<T,Boolean> leftChild;
    private Expression<T,Boolean> rightChild;

    public Expression<T, Boolean> getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Expression<T, Boolean> leftChild) {
        this.leftChild = leftChild;
    }

    public Expression<T, Boolean> getRightChild() {
        return rightChild;
    }

    public void setRightChild(Expression<T, Boolean> rightChild) {
        this.rightChild = rightChild;
    }

    public Boolean evaluate(Context<T> ctx) {
        return leftChild.evaluate(ctx) && rightChild.evaluate(ctx);
    }
}
