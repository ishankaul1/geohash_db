package ishankaul.personal.dbquery.expr;

abstract class ComparisonExpression implements Expression{
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

    public abstract Boolean evaluate(Context ctx);

}
