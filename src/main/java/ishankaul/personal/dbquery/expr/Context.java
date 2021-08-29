package ishankaul.personal.dbquery.expr;

import ishankaul.personal.proximitystreamdb.AttributesStrategy;
import ishankaul.personal.proximitystreamdb.ProximityStreamDB;

public class Context<T> {

    private Object target;

    private ProximityStreamDB<T> db;

    private AttributesStrategy attributesStrategy;

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public ProximityStreamDB<T> getDb() {
        return db;
    }

    public void setDb(ProximityStreamDB<T> db) {
        this.db = db;
    }

    public AttributesStrategy getAttributesStrategy() {
        return attributesStrategy;
    }

    public void setAttributesStrategy(AttributesStrategy attributesStrategy) {
        this.attributesStrategy = attributesStrategy;
    }
}
