package ishankaul.personal.dbquery.expr.builder;

import ishankaul.personal.dbquery.expr.Expression;
import ishankaul.personal.dbquery.expr.FindExpression;
import ishankaul.personal.dbquery.expr.NearExpression;
import ishankaul.personal.dbquery.expr.WhereExpression;

public class FindBuilder implements ExpressionBuilder{
    public FindExpression expr;

    public FindBuilder(FindExpression expr){
        this.expr = expr;
    }

    public void buildNear(NearExpression near){
        this.expr.setNear(near);
    }

    public void buildWhere(WhereExpression where){
        this.expr.setWhere(where);
    }

    @Override
    public FindExpression getExpression() {
        return expr;
    }
}
