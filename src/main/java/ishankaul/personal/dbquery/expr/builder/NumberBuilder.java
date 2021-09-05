package ishankaul.personal.dbquery.expr.builder;

import ishankaul.personal.dbquery.expr.Expression;
import ishankaul.personal.dbquery.expr.NumberExpression;

public class NumberBuilder implements ExpressionBuilder{

    private NumberExpression expr;

    public NumberBuilder(double number){
        this.expr = new NumberExpression(number);
    }

    @Override
    public Expression getExpression() {
        return this.expr;
    }
}
