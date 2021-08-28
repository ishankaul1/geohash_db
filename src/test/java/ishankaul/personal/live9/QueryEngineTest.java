package ishankaul.personal.live9;

import ishankaul.personal.live6.DataAndPosition;
import ishankaul.personal.live6.GeoHash;
import ishankaul.personal.live6.Position;
import ishankaul.personal.live7.AttributesStrategy;
import ishankaul.personal.live7.ProximityStreamDB;
import ishankaul.personal.live7.ProximityStreamDBFactory;
import ishankaul.personal.live7.example.Building;
import ishankaul.personal.live7.example.BuildingAttributesStrategy;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueryEngineTest {


    private class FakeGeoHash implements GeoHash {

        private List<Boolean> bits = new ArrayList<>();

        public FakeGeoHash(boolean[] bits){
            for(Boolean b : bits){this.bits.add(b);}
        }

        public FakeGeoHash(List<Boolean> bits){
            this.bits = bits;
        }

        @Override
        public int bitsOfPrecision() {
            return bits.size();
        }

        @Override
        public GeoHash prefix(int n) {
            return new FakeGeoHash(this.bits.subList(0,n));
        }

        @Override
        public GeoHash northNeighbor() {
            return null;
        }

        @Override
        public GeoHash southNeighbor() {
            return null;
        }

        @Override
        public GeoHash westNeighbor() {
            return null;
        }

        @Override
        public GeoHash eastNeighbor() {
            return null;
        }

        @Override
        public Iterator<Boolean> iterator() {
            return bits.iterator();
        }
    }

    private <T> ProximityStreamDB<T> newDB(AttributesStrategy<T> strat,
                                           Map<Position, boolean[]> hashLookup,
                                           int bits) {

        return new ProximityStreamDBFactory().create(
                strat,
                (lat,lon,bs) -> new FakeGeoHash(hashLookup.get(Position.with(lat,lon))).prefix(bs),
                bits);
    }

    private static boolean[] randomGeoHash(int bits){
        boolean[] hash = new boolean[bits];
        for(int i = 0; i < hash.length; i++){
            hash[i] = Math.random() > 0.5;
        }
        return hash;
    }

    public Position randomPosition(){
        double lat = -90.0 + (Math.random() * 180); // generate a random lat between -90/90
        double lon = -180 + (Math.random() * 360); // generate a random lon between -180/180
        return Position.with(lat, lon);
    }

    /**
     * This method randomly generates a set of unique Positions and maps them to a specified
     * number of geohashes. The geohashes are completely random and the mapping is random.
     *
     * For example, randomCoordinateHashMappings(16, 100, 12) would generate 100 unique Positions
     * and map them to 12 random geohashes of 16 bits each.
     *
     * @param bits
     * @param total
     * @param groups
     * @return
     */
    private Map<Position,boolean[]> randomCoordinateHashMappings(int bits, int total, int groups, int sharedPrefixLength){
        int avg = total/groups; // If it doesn't divide evenly, there is a remainder discarded

        Map<Position,boolean[]> mappings = new HashMap<>();
        Set<Position> positions = new HashSet<>();
        Set<String> hashes = new HashSet<>();

        for(int i = 0; i < groups; i++){

            // We generate random unique geohash prefixes of length
            // `sharedPrefixLength` so that we can synthesize groups
            // of positions that will match up to a certain number of
            // bits
            boolean[] hash = randomGeoHash(sharedPrefixLength);
            while(hashes.contains(toString(hash))){
                hash = randomGeoHash(sharedPrefixLength);
            }
            hashes.add(toString(hash));

            // Create `avg` Position objects that are unique
            // and map each one to the random hash
            for(int j = 0; j < avg; j++) {

                // We randomize every bit after the shared prefix
                boolean[] fullHash = Arrays.copyOf(hash, bits);
                for(int k = hash.length; k < fullHash.length; k++){
                    fullHash[k] = (Math.random() > 0.5);
                }

                Position pos = randomPosition();
                while (positions.contains(pos)) {
                    pos = randomPosition();
                }
                positions.add(pos);
                mappings.put(pos, fullHash);
            }

        }

        return mappings;
    }

    public String toString(boolean[] data) {
        String hashString = "";
        for (boolean b : data) {
            hashString += (b ? "1" : "0");
        }
        return hashString;
    }

    public static DataAndPosition<Map<String,?>> data(Map<String,?> m){
        return new DataAndPosition<Map<String, ?>>() {
            @Override
            public Map<String, ?> getData() {
                return m;
            }

            @Override
            public double getLatitude() {
                return (Double)m.get("lat");
            }

            @Override
            public double getLongitude() {
                return (Double)m.get("lon");
            }
        };
    }

    @Test
    public void testSimpleQuery(){

        DataAndPosition data = data(MapUtils.of(
                "height", 10.0,
                "age", 32.0,
                "lat", -90.0,
                "lon", -180.0
        ));

        DataAndPosition data2 = data(MapUtils.of(
                "age", 56.0,
                "height", 8.0,
                "lat", -90.0,
                "lon", -180.0
        ));

        ProximityStreamDB db = new ProximityStreamDBFactory().create(
                new MapAttributesStrategy(),
                32);
        db.insert(data);
        db.insert(data2);


        // This should produce the same result as the manually created
        // query above
        Stream<DataAndPosition<Map<String,?>>> result2 =
                QueryEngine.execute(db,
                        new MapAttributesStrategy(),
                        "(find " +
                                "     (near -45.0 -145.0 2) " +
                                "     (where " +
                                "          (> :height 8)" +
                                "     )" +
                                ")");

        assertEquals(1, result2.count());
    }

    public static int randomInt(int min, int max){
        return min + (int) Math.rint(Math.random() * (max - min));
    }

    public static String greaterThanQuery(double lat, double lon, int bits, String attrName, Object v){
        return
        "(find " +
                "     (near "+lat+" "+lon+" "+bits+") " +
                "     (where " +
                "          (> :"+attrName+" "+v+")" +
                "     )" +
                ")";
    }

    @Test
    public void testRandomQueriesWithFixedGeo(){

        // This test randomly generates a series of maps with a fixed
        // set of keys and random values for the keys. The test then
        // issues a series of queries near 0,0 to check if the query
        // accurately filters results based on randomly chosen keys and
        // values.

        int maxItems = 1000;
        int minItems = 10;
        int maxAttrs = 10;
        int minAttrs = 1;
        int totalItems =  randomInt(minItems,maxItems);
        int totalAttrs = randomInt(minAttrs,maxAttrs);

        List<String> attrNames = new ArrayList<>();
        for(int i = 0; i < totalAttrs; i++) {
            attrNames.add(UUID.randomUUID().toString());
        }

        List<Map<String,Object>> items = new ArrayList<>();
        for(int i = 0; i < totalItems; i++) {
            Map<String,Object> attrValues = new HashMap<>();

            attrValues.put("id", UUID.randomUUID().toString());
            attrValues.put("lat",0.0);
            attrValues.put("lon",0.0);
            attrNames.forEach(n -> attrValues.put(n, randomInt(Integer.MIN_VALUE, Integer.MAX_VALUE)));

            items.add(attrValues);
        }

        ProximityStreamDB db = new ProximityStreamDBFactory().create(
                new MapAttributesStrategy(),
                32);

        items.forEach(item -> {
            //System.out.println("item: " + item);
            db.insert(data(item));
        });

        int randomQueries = randomInt(0, 100);
        for(int i = 0; i < randomQueries; i++){
            String attrName = attrNames.get(randomInt(0, attrNames.size() - 1));
            int value = (int) items.stream()
                    .map(item-> item.get(attrName))
                    .skip(Math.max(0, randomInt(0, items.size() - 2)))
                    .findFirst().get();

            String query = greaterThanQuery(0,0, 1, attrName, value);
            Stream<DataAndPosition<Map<String,?>>> result =
                    QueryEngine.execute(db, new MapAttributesStrategy(), query);

            long total = items.stream()
                    .map(item -> item.get(attrName))
                    .filter(v -> ((Integer) v) > value)
                    .count();

            assertEquals(total, result.count());
        }

    }

    @Test
    public void testRandomQueries(){

        // This test randomly generates a set of positions that are
        // artificially assigned to geohashes. The geohashes are constructed
        // such that the positions are guaranteed to fall into N groups that
        // match K bits of their geohashes. The test generates random queries
        // and uses the advance knowledge of what other items are nearby to
        // calculate what the query results should be.
        //
        // A synthetic example:
        //
        // groups = 3;
        // sharedPrefixLength = 2;
        // bits = 4;
        // buildings = 6;
        //
        // randomMappings = {
        //      [(-88.01, 0) 1111]
        //      [(-48.01, 90) 1101]
        //      [(-88.01, 20) 1000]
        //      [(20.01, 0) 1001]
        //      [(118.01, -10) 0110]
        //      [(88.01, 10) 0101]
        // }
        //
        // There are three unique prefixes of length 2.
        // [11, 10, 01]
        //
        // Every position has been mapped to a random geohash that
        // starts with one of these prefixes.
        //
        // For any given prefix, we know in advance what positions will
        // map to it.
        //
        // For each position, we can check that all other locations with
        // a matching prefix are returned when we do a nearby search on
        // that position.
        //
        // Note: the hashes are completely random and unrelated to the
        // acutal positions on the earth -- it shouldn't matter to your
        // implementation how the position to geohash translation is done,
        // as long as it is consistent

        int maxGroups = 128;
        int maxBits = 256;

        int groups = 1 + (int) Math.rint(Math.random() * (maxGroups - 1));
        int sharedPrefixLength =
                Math.max(
                        (int) Math.log(groups) + 16, // We have to ensure that we have
                        // enough bits in the shared prefix
                        // to differentiate all the groups and
                        // not take forever to randomly generate
                        // the unique shared prefixes
                        (int) Math.rint(Math.random() * maxBits));
        int bits =  sharedPrefixLength +
                (int) Math.rint(Math.random() * (maxBits - sharedPrefixLength));
        int buildings = (int) Math.rint(Math.random() * 28 * groups);;


        System.out.println("Testing "+ buildings +" items with "
                + bits + " bit hashes and " + sharedPrefixLength +" shared bits in "
                + groups + " groups");

        Map<Position,boolean[]> randomMappings = randomCoordinateHashMappings(bits, buildings, groups, sharedPrefixLength);
        ProximityStreamDB<Building> db = newDB(new BuildingAttributesStrategy(), randomMappings, bits);

        Map<String,Set<Building>> hashToBuilding = new HashMap<>();
        Map<String,List<Double>> hashToSqft = new HashMap<>();
        Map<String,List<Double>> hashToClassrooms = new HashMap<>();

        for(Map.Entry<Position,boolean[]> entry : randomMappings.entrySet()){
            Position pos = entry.getKey();
            String hashstr = toString(entry.getValue()).substring(0, sharedPrefixLength);
            Building b = new Building(UUID.randomUUID().toString(),
                    Math.random() * 100000,
                    Math.rint(Math.random() * 25));
            db.insert(DataAndPosition.with(pos.getLatitude(), pos.getLongitude(), b));

            Set<Building> existing = hashToBuilding.getOrDefault(hashstr, new HashSet<>());
            existing.add(b);
            hashToBuilding.put(hashstr, existing);

            List<Double> curr = hashToSqft.getOrDefault(hashstr, new ArrayList<>());
            curr.add(b.getSizeInSquareFeet());
            hashToSqft.put(hashstr, curr);

            List<Double> rooms = hashToClassrooms.getOrDefault(hashstr, new ArrayList<>());
            rooms.add(b.getClassRooms());
            hashToClassrooms.put(hashstr, rooms);
        }

        for(Map.Entry<Position,boolean[]> entry : randomMappings.entrySet()){
            Position pos = entry.getKey();
            String hashstr = toString(entry.getValue()).substring(0, sharedPrefixLength);
            Set<Building> atGeo = hashToBuilding.getOrDefault(hashstr, new HashSet<>());

            double value = atGeo.stream()
                    .mapToDouble(b -> b.getSizeInSquareFeet())
                    .skip(Math.max(0, randomInt(0, atGeo.size() - 2)))
                    .findFirst().getAsDouble();

            String query = greaterThanQuery(pos.getLatitude(), pos.getLongitude(),
                    sharedPrefixLength, BuildingAttributesStrategy.SIZE_IN_SQUARE_FEET,
                    value);

            Set<Building> expected = atGeo.stream()
                    .filter(b -> b.getSizeInSquareFeet() > value)
                    .collect(Collectors.toSet());

            Set<Building> result =
                    QueryEngine.execute(db, new BuildingAttributesStrategy(), query)
                            .map(d -> d.getData())
                            .collect(Collectors.toSet());

            assertEquals(expected.size(), result.size());

            expected.stream().forEach(b -> assertTrue(result.contains(b)));
        }

        for(Map.Entry<Position,boolean[]> entry : randomMappings.entrySet()){
            Position pos = entry.getKey();
            String hashstr = toString(entry.getValue()).substring(0, sharedPrefixLength);
            Set<Building> atGeo = hashToBuilding.getOrDefault(hashstr, new HashSet<>());

            double value = atGeo.stream()
                    .mapToDouble(b -> b.getClassRooms())
                    .skip(Math.max(0, randomInt(0, atGeo.size() - 2)))
                    .findFirst().getAsDouble();

            String query = greaterThanQuery(pos.getLatitude(), pos.getLongitude(),
                    sharedPrefixLength, BuildingAttributesStrategy.CLASSROOMS,
                    value);

            Set<Building> expected = atGeo.stream()
                    .filter(b -> b.getClassRooms() > value)
                    .collect(Collectors.toSet());

            Set<Building> result =
                    QueryEngine.execute(db, new BuildingAttributesStrategy(), query)
                            .map(d -> d.getData())
                            .collect(Collectors.toSet());

            assertEquals(expected.size(), result.size());

            expected.stream().forEach(b -> assertTrue(result.contains(b)));
        }

    }

}
