package ishankaul.personal.dbquery.expr;

import ishankaul.personal.proximitydb.DataAndPosition;

import java.util.Map;
import java.util.stream.Stream;

public class FindExpression<T> implements Expression<T, Stream<DataAndPosition<T>>> {

    private NearExpression near;
    private WhereExpression where;

    public FindExpression(){

    }

    public void setNear(NearExpression near) {
        this.near = near;
    }

    public void setWhere(WhereExpression where){
        this.where = where;
    }

    public FindExpression(NearExpression near, WhereExpression where) {
        this.near = near;
        this.where = where;
    }

    @Override
    public Stream<DataAndPosition<T>> evaluate(Context<T> ctx) {
        Stream<DataAndPosition<Map<String,?>>> nearby = near.evaluate(ctx);
        ctx.setTarget(nearby);
        return where.evaluate(ctx);
    }
}
