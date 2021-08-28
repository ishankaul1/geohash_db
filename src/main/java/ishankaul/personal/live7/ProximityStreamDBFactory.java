package ishankaul.personal.live7;

import ishankaul.personal.live6.GeoHashFactory;

public class ProximityStreamDBFactory {

    /**
     * @ToDo:
     *
     * Fill in with your ProximityStreamDB implementation
     *
     */
    public <T> ProximityStreamDB<T> create(AttributesStrategy<T> strat,
                                           GeoHashFactory hashFactory,
                                           int bits){
        return new ProximityStreamDB_Impl<T>(strat, hashFactory, bits);
    }

    /**
     * @ToDo:
     *
     * Fill in with your ProximityStreamDB implementation
     *
     * This version should provide a default GeoHashFactory that
     * you provide.
     *
     */
    public <T> ProximityStreamDB<T> create(AttributesStrategy<T> strat,
                                           int bits){
        return new ProximityStreamDB_Impl<T>(strat, bits);
    }

}
