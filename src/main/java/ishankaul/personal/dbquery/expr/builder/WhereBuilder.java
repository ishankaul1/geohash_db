package ishankaul.personal.dbquery.expr.builder;

import ishankaul.personal.dbquery.expr.Expression;
import ishankaul.personal.dbquery.expr.WhereExpression;

public class WhereBuilder implements ExpressionBuilder{

    private WhereExpression expr;

    public WhereBuilder(){
        this.expr = new WhereExpression();
    }

    public void buildFilter(Expression expression){
        this.expr.setFilterExpression(expression);
    }

    @Override
    public Expression getExpression() {
        return expr;
    }
}
