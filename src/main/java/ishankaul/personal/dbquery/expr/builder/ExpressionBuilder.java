package ishankaul.personal.dbquery.expr.builder;

import ishankaul.personal.dbquery.ast.LiteralNode;
import ishankaul.personal.dbquery.expr.Expression;

import java.util.ArrayList;
import java.util.LinkedList;

public interface ExpressionBuilder {

    public void buildWhole(LinkedList<Expression> expressionStack);


    public Expression getExpression();
}
