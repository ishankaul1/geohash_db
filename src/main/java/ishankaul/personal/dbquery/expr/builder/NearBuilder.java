package ishankaul.personal.dbquery.expr.builder;

import ishankaul.personal.dbquery.expr.Expression;
import ishankaul.personal.dbquery.expr.NearExpression;

public class NearBuilder implements ExpressionBuilder {

    private NearExpression expression;

    public NearBuilder(){
        this.expression = new NearExpression();
    }

    public void buildLeft(Expression expr){
        this.expression.setLeftChild(expr);
    }

    public void buildMiddle(Expression expr){
        this.expression.setMiddleChild(expr);
    }

    public void buildRigbt(Expression expr){
        this.expression.setRightChild(expr);
    }

    @Override
    public NearExpression getExpression() {
        return this.expression;
    }
}
