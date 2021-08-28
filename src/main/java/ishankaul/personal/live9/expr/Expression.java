package ishankaul.personal.live9.expr;

public interface Expression<T,R> {

    public R evaluate(Context<T> ctx);

}
