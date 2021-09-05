package ishankaul.personal.dbquery.expr.builder;

import ishankaul.personal.dbquery.ast.LiteralNode;
import ishankaul.personal.dbquery.expr.AndExpression;
import ishankaul.personal.dbquery.expr.Expression;

import java.util.ArrayList;
import java.util.LinkedList;

public class AndBuilder implements ExpressionBuilder{

    private AndExpression andExpression;

    public AndBuilder(){
        this.andExpression = new AndExpression();
    }

    public void buildLeft(Expression expr){
        this.andExpression.setLeftChild(expr);
    }

    public void buildRight(Expression expr){
        this.andExpression.setRightChild(expr);
    }


    @Override
    public void buildWhole(LinkedList<Expression> expressionStack) {
        buildRight(expressionStack.pop());
        buildLeft(expressionStack.pop());
    }

    @Override
    public AndExpression getExpression() {
        return this.andExpression;
    }
}
