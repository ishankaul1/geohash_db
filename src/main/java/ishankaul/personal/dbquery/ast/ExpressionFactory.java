package ishankaul.personal.dbquery.ast;

import ishankaul.personal.dbquery.expr.Expression;
import ishankaul.personal.dbquery.expr.FindExpression;
import ishankaul.personal.dbquery.expr.NearExpression;
import ishankaul.personal.dbquery.expr.WhereExpression;

import java.util.HashMap;


public class ExpressionFactory {

    @FunctionalInterface
    private interface ExpressionFactoryCommand{
        Expression execute(String param);
    }

    private HashMap<String, ExpressionFactoryCommand> expressionMap = new HashMap<>();

    public ExpressionFactory(){
        expressionMap.put("near", param -> {return new NearExpression();});
        expressionMap.put("where", param -> {return new WhereExpression(); });
        expressionMap.put("find", param -> {
            NearExpression near = new NearExpression();
            WhereExpression where = new WhereExpression();
            return new FindExpression(near, where);
        });

    }



}
