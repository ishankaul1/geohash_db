package ishankaul.personal.live9.ast;

import ishankaul.personal.live9.expr.Expression;
import ishankaul.personal.live9.expr.FindExpression;
import ishankaul.personal.live9.expr.NearExpression;
import ishankaul.personal.live9.expr.WhereExpression;
import sun.jvm.hotspot.code.Location;

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
