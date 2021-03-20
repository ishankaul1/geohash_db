package edu.vanderbilt.cs.live7;

import edu.vanderbilt.cs.live6.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class ProximityStreamDB_Impl<T> extends ProximityDB_Impl implements ProximityStreamDB  {

    private AttributesStrategy strategy;
    private ArrayList<ProximityDB_Operation> history;

    public ProximityStreamDB_Impl(AttributesStrategy<T> strategy, GeoHashFactory geoHashFactory, int maxBitsOfPrecision){
        super(maxBitsOfPrecision, geoHashFactory);
        this.strategy = strategy;
        this.history = new ArrayList<>();
    }

    public ProximityStreamDB_Impl(AttributesStrategy<T> strategy, int maxBitsOfPrecision){
        super(maxBitsOfPrecision);
        this.strategy = strategy;
        this.history = new ArrayList<>();
    }

    @Override
    public void insert(DataAndPosition data) {
        history.add(new Insert_Operation(data));
        super.insert(data);

    }

    @Override
    public Collection<DataAndPosition> delete(Position pos){
        history.add(new Delete_Operation(pos));
        return super.delete(pos);
    }

    @Override
    public Collection<DataAndPosition> delete(Position pos, int bitsOfPrecision) {
        history.add(new Delete_Operation(pos, bitsOfPrecision));
        return super.delete(pos, bitsOfPrecision);
    }

    @Override
    public ProximityStreamDB databaseStateAtTime(int n) {
        if (n > history.size() || n < 0) {
            throw new IllegalArgumentException("History only available between 0 and time N=" + n);
        }

        ProximityStreamDB newDB = new ProximityStreamDB_Impl(strategy, geoHashFactory, maxBitsOfPrecision);

        if (n == 0){
            return newDB;
        }

        Iterator<ProximityDB_Operation> iter = history.iterator();
        int i  = 0;
        while (iter.hasNext() && i < n){
            ProximityDB_Operation operation = iter.next();
            operation.execute(newDB);
            i++;

        }
        return newDB;

    }

    @Override
    public Map histogramNearby(AttributeMatcher matcher, Position pos, int bitsOfPrecision) {
        //Map map = streamNearby(matcher, pos, bitsOfPrecision).collect(groupingBy)
        Map map = (Map) streamNearby(matcher, pos, bitsOfPrecision).collect(groupingBy(Function.identity(), counting()));
        return map;
    }

    @Override
    public OptionalDouble maxNearby(AttributeMatcher matcher, Position pos, int bitsOfPrecision) {
        return streamNearby(matcher, pos, bitsOfPrecision).mapToDouble(value -> (Double) value ).max();
    }

    @Override
    public OptionalDouble minNearby(AttributeMatcher matcher, Position pos, int bitsOfPrecision) {
        return streamNearby(matcher, pos, bitsOfPrecision).mapToDouble(value -> (Double) value ).min();
    }

    @Override
    public OptionalDouble averageNearby(AttributeMatcher matcher, Position pos, int bitsOfPrecision) {
        return streamNearby(matcher, pos, bitsOfPrecision).mapToDouble(value -> (Double) value ).average();
    }

    /**
     * double averageBuildingSqft = strmdb.averageNearby(
     *                 a -> BuildingAttributesStrategy.SIZE_IN_SQUARE_FEET.equals(a.getName()),
     *                 Position.with(36.145050, 86.803365),2 ).getAsDouble()
     * @param matcher - a predicate to determine which attributes should be included in the stream
     * @param pos
     * @param bitsOfPrecision
     * @return
     */
    @Override
    public Stream streamNearby(AttributeMatcher matcher, Position pos, int bitsOfPrecision) {
        Collection<DataAndPosition> nearbyDpos = nearby(pos, bitsOfPrecision);
        return nearbyDpos
                .stream()
                .flatMap(dpos -> strategy.getAttributes(dpos.getData()).stream())
                .filter(attr -> matcher.matches((Attribute) attr))
                .map(attr -> ((Attribute<?>) attr).getValue());

        /**.map(attrCollection -> attrCollection.stream().filter(attr -> matcher.matches(attr)))
         .filter(attr -> matcher.matches( attr)) //the need to type cast here makes me mad//
         .map(attr -> ((Attribute<?>) attr).getValue());*/


    }
}
