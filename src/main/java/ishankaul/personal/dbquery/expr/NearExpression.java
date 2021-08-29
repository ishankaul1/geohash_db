package ishankaul.personal.dbquery.expr;

import ishankaul.personal.proximitydb.DataAndPosition;
import ishankaul.personal.proximitydb.Position;

import java.util.stream.Stream;

public class NearExpression<T> implements Expression<T, Stream<DataAndPosition<T>>> {

    private Expression<T,Double> leftChild;
    private Expression<T, Double> middleChild;
    private Expression<T, Double> rightChild;

    public Expression<T, Double> getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Expression<T, Double> leftChild) {
        this.leftChild = leftChild;
    }

    public Expression<T, Double> getMiddleChild() {
        return middleChild;
    }

    public void setMiddleChild(Expression<T, Double> middleChild) {
        this.middleChild = middleChild;
    }

    public Expression<T, Double> getRightChild() {
        return rightChild;
    }

    public void setRightChild(Expression<T, Double> rightChild) {
        this.rightChild = rightChild;
    }

    @Override
    public Stream<DataAndPosition<T>> evaluate(Context<T> ctx) {
        return ctx.getDb().nearby(
                Position.with(leftChild.evaluate(ctx),middleChild.evaluate(ctx)),
                rightChild.evaluate(ctx).intValue()).stream();
    }
}
