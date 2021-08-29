package ishankaul.personal.dbquery.expr;

public interface Expression<T,R> {

    public R evaluate(Context<T> ctx);

}
