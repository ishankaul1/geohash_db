package ishankaul.personal.live6;

import java.util.Collection;

/**
 * Create an implementation of the ProximityDB.
 *
 * The ProximityDB maps data items to coordinates and allows searching
 * for nearby data items using geohashes.
 * The type parameter T is the type of data stored in the database. For example,
 * to map Strings to coordinates, new ProximityDBImpl<String>(...)
 *
 */

public class ProximityDB_Impl<T> implements ProximityDB {

    protected int maxBitsOfPrecision;
    protected CompositeTree compositeTree;
    protected GeoHashFactory geoHashFactory;

    public ProximityDB_Impl( int maxBitsOfPrecision){
        if (maxBitsOfPrecision < 0)
            throw new IllegalArgumentException("Can't have bits of precision less than 0");
        this.maxBitsOfPrecision = maxBitsOfPrecision;
        this.geoHashFactory =  new GeoHashFactory_Impl();
        this.compositeTree = new CompositeTree(maxBitsOfPrecision, geoHashFactory);
    }

    public ProximityDB_Impl(int maxBitsOfPrecision, GeoHashFactory geoHashFactory){
        if (maxBitsOfPrecision < 0)
            throw new IllegalArgumentException("Can't have bits of precision less than 0");
        this.maxBitsOfPrecision = maxBitsOfPrecision;
        this.geoHashFactory = geoHashFactory;
        this.compositeTree = new CompositeTree(maxBitsOfPrecision, geoHashFactory);

    }

    @Override
    public void insert(DataAndPosition data) {
        compositeTree.insert(data);

    }

    @Override
    public Collection<DataAndPosition> delete(Position pos) {

        return compositeTree.delete(pos);
    }

    @Override
    public Collection<DataAndPosition> delete(Position pos, int bitsOfPrecision) {
        //guard against bad values
        if (bitsOfPrecision > this.maxBitsOfPrecision || bitsOfPrecision < 0){
            throw new IllegalArgumentException("Bits of precision must be 0 < x < maxBitsOfPrecision");
        }
        return compositeTree.deleteAll(pos, bitsOfPrecision);
    }

    @Override
    public boolean contains(Position pos, int bitsOfPrecision) {
        //guard against bad values
        if (bitsOfPrecision > this.maxBitsOfPrecision || bitsOfPrecision < 0){
            throw new IllegalArgumentException("Bits of precision must be 0 < x < maxBitsOfPrecision");
        }
        return compositeTree.contains(pos, bitsOfPrecision);
    }

    @Override
    public Collection<DataAndPosition> nearby(Position pos, int bitsOfPrecision) {
        //guard against bad values
        if (bitsOfPrecision > this.maxBitsOfPrecision || bitsOfPrecision < 0){
            throw new IllegalArgumentException("Bits of precision must be 0 < x < maxBitsOfPrecision");
        }
        return compositeTree.nearby(pos, bitsOfPrecision);
    }

    @Override
    public ProximityDB emptyClone() {
        return new ProximityDB_Impl(maxBitsOfPrecision);
    }
}
