package ishankaul.personal.dbquery.ast;

import ishankaul.personal.dbquery.expr.*;

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
        expressionMap.put(">", param -> {return new GreaterThanExpression();});
        expressionMap.put("<", param -> {return new LessThanExpression();});

    }

    public Expression create(String param){
        return expressionMap.get(param).execute(param);
    }

    public static void main(String[] args) {
        //basic test for object creation
        ExpressionFactory factory = new ExpressionFactory();
        Expression expr = factory.create("near");

    }

}

