package ishankaul.personal.dbquery.expr;

import ishankaul.personal.proximitydb.DataAndPosition;
import ishankaul.personal.proximitystreamdb.Attribute;

import java.util.Collection;

public class AttributeValueExpression implements Expression {

    private final String attribute;

    public AttributeValueExpression(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public Object evaluate(Context ctx) {

        Object data = ((DataAndPosition)ctx.getTarget()).getData();

        Collection<Attribute> attrs = ctx.getAttributesStrategy()
                .getAttributes(data);

        return attrs.stream().filter(a -> a.getName().equals(this.attribute))
                .map(a -> a.getValue())
                .findFirst().orElse(null);
    }
}
