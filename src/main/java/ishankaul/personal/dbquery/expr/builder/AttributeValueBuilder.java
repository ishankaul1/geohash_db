package ishankaul.personal.dbquery.expr.builder;

import ishankaul.personal.dbquery.expr.AttributeValueExpression;
import ishankaul.personal.dbquery.expr.Expression;

public class AttributeValueBuilder implements ExpressionBuilder{

    private AttributeValueExpression attributeValueExpression;

    public AttributeValueBuilder (String attr){
        this.attributeValueExpression = new AttributeValueExpression(attr);
    }


    @Override
    public AttributeValueExpression getExpression() {
        return this.attributeValueExpression;
    }
}
