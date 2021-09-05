package ishankaul.personal.dbquery.expr.builder;

import ishankaul.personal.dbquery.ast.LiteralNode;
import ishankaul.personal.dbquery.expr.Expression;
import ishankaul.personal.dbquery.expr.LessThanExpression;

import java.util.ArrayList;

public class LessThanBuilder implements ExpressionBuilder{

    private LessThanExpression expr;

    public LessThanBuilder(){
        expr = new LessThanExpression();
    }

    public void buildLeft(Expression left){
        this.expr.setLeftChild(left);
    }

    public void buildRight(Expression right){
        this.expr.setRightChild(right);
    }

    @Override
    public void buildWhole(ArrayList<LiteralNode> literalStack, ArrayList<Expression> expressionStack) {

    }

    @Override
    public LessThanExpression getExpression() {
        return this.expr;
    }
}
