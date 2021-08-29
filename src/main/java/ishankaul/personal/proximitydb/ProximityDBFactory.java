package ishankaul.personal.proximitydb;


public class ProximityDBFactory {

    /**
     * @ToDo:
     *
     * Fiil this in to create one of your implementations
     *
     * @param <T>
     * @return
     */
    public <T> ProximityDB<T> create( int bits){
        return new ProximityDB_Impl<T>(bits);
    }

}
