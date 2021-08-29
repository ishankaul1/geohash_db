package ishankaul.personal.proximitydb;

import java.util.*;

/**
 * The original version of GeoHash failed to properly encapsulate the representation
 * of the GeoHash from consumers of the hashes. This class shields users from the
 * underlying representation by properly encapsulating the underlying representation.
 * Please use your original GeoHash work to complete this class.
 *
 * After you complete this class, go and complete the GeoDBFactory, GeoHashFactory,
 * and DefaultGeoDBConfiguration.
 *
 * You will need to update your implementation of GeoDB to use a GeoHashFactory to
 * produce geohash objects when the insert, delete, nearby, contains, etc. methods
 * are called. You cannot directly call your old GeoHash class or assume that your
 *
 * There are some items marked with @Bonus that are not required. However, if you
 * want an additional challenge, knock these items out AFTER completing everything
 * else.
 *
 */

public class GeoHash_Impl implements GeoHash {

    //private double lat;
    //private double lon;
    private int bitsOfPrecision;
    private ArrayList<Boolean> geoHash;

    public static final double[] LATITUDE_RANGE = { -90, 90 };
    public static final double[] LONGITUDE_RANGE = { -180, 180 };

    //creates a geoHash implementation directly from a boolean[] array
    private GeoHash_Impl(ArrayList<Boolean> geoHash){
        this.bitsOfPrecision = geoHash.size();
        this.geoHash = geoHash;
    }
    private GeoHash_Impl(double lat, double lon, int bitsOfPrecision) {
        // @ToDo, fill this in
        // You are free to change the constructor parameters, but the
        // constructor must remain private and the static factory method "from" used
        // to create instances of this.

        this.bitsOfPrecision = bitsOfPrecision;

        //TODO: try to implement this section using streams and Guava library for fun/practice with java functional programming
        boolean[] geoHash_prim = geohash2D(lat, LATITUDE_RANGE, lon, LONGITUDE_RANGE, bitsOfPrecision);
        geoHash = new ArrayList<>();
        for (int i = 0; i < geoHash_prim.length; i++){
            geoHash.add(geoHash_prim[i]);
        }


    }

    public static GeoHash_Impl from(double lat, double lon, int bitsOfPrecision){
        return new GeoHash_Impl(lat, lon, bitsOfPrecision);
    }

    public static GeoHash_Impl fromBoolArray(ArrayList<Boolean> boolArray){
        return new GeoHash_Impl(boolArray);
    }



    /**
     * functionality for creation of the boolean array
     * @return boolean[] array
     */
    private boolean[] geohash2D(double v1, double[] v1range, double v2, double[] v2range, int bitsOfPrecision) {
        boolean[] geohash2d = new boolean[bitsOfPrecision];
        int bitsOfPrecision1 = (int) Math.ceil( (double) bitsOfPrecision / 2);
        int bitsOfPrecision2 = (int) Math.floor( (double) bitsOfPrecision / 2);
        boolean[] geohash1 = geohash1D(v1, v1range, bitsOfPrecision1);
        boolean[] geohash2 = geohash1D(v2, v2range, bitsOfPrecision2);
        for (int i = 0; i < bitsOfPrecision; i++){
            if (i % 2 == 0) {
                geohash2d[i] = geohash1[(int) i / 2];
            }else{
                geohash2d[i] = geohash2[(int) Math.floor( i / 2)];
            }
        }
        return geohash2d;
    }


    public static boolean[] geohash1D(double valueToHash, double[] valueRange, int bitsOfPrecision) {


        double valueRangeCopy[] = valueRange.clone(); //need deep copy here - otherwise later test cases fail

        return geoHash1DHelper(valueToHash, valueRangeCopy, bitsOfPrecision, bitsOfPrecision-1);


    }

    public static boolean[] geoHash1DHelper(double valueToHash, double[] valueRange, int bitsOfPrecision, int bitsLeft ){
        //base case - no bits left
        if (bitsLeft == -1){
            //create new array to fill
            return new boolean[bitsOfPrecision];
        }
        boolean[] hashCode = geoHash1DHelper(valueToHash, valueRange, bitsOfPrecision, bitsLeft-1);
        double midpoint = (valueRange[0] + valueRange[1]) / 2;
        if (valueToHash >= midpoint) {
            hashCode[bitsLeft] = true;
            valueRange[0] = midpoint;
        }else{
            hashCode[bitsLeft] = false;
            valueRange[1] = midpoint;
        }
        return hashCode;

    }

    public int bitsOfPrecision() {
        return this.bitsOfPrecision;
    }

    /**
     * Similar to "substring" on Strings. This method should
     * return the first n bits of the GeoHash as a new
     * GeoHash.
     *
     * @param n
     * @return
     */
    public GeoHash_Impl prefix(int n){
        return new GeoHash_Impl((ArrayList<Boolean>) geoHash.subList(0, n));
    }

    //Implementation: build a new ArrayList<Boolean> with binary value of one higher than the current
    //then use fromBoolArray() to form a new geoHashImpl and return that
    public GeoHash_Impl northNeighbor() {
        // @Bonus, this is not required, but is a nice challenge
        // for bonus points

        //create list of even objects
        ArrayList<Boolean> latitudeBits = new ArrayList<>();
        ArrayList<Boolean> longitudeBits = new ArrayList<>();
        int j = 0;
        for (Boolean bit : this.geoHash){
            if (j % 2 == 0){
                latitudeBits.add(bit);
            }else{
                longitudeBits.add(bit);
            }
            j +=1;
        }

        //corner case check
        //Predicate<Boolean> isTrue = b -> Boolean.TRUE.equals(b);
        if (latitudeBits.stream().allMatch(Boolean.TRUE::equals)){
            //hit a corner case - all one's. for now just gonna throw an exception
            throw new IllegalArgumentException("No north neighbor of maximum latitude geohash");
        }

        //now the fun part
        ArrayList<Boolean> northNeighbor = new ArrayList<>();
        boolean carryOver = false;
        for (int i = latitudeBits.size()-1; i >= 0; i--){
            Boolean thisBit = latitudeBits.get(i);
            if (i == latitudeBits.size()-1){
                //at the first character; have to flip the bit
                if (thisBit){
                    northNeighbor.add(Boolean.FALSE);
                    carryOver = true;
                }else{
                    northNeighbor.add(Boolean.TRUE);
                }
            }else{
                //not at the first character
                if (carryOver){
                    //carrOver - gotta flip the bit
                    if(thisBit){
                        northNeighbor.add(Boolean.FALSE);
                        //keep carryover true
                    }else{
                        northNeighbor.add(Boolean.TRUE);
                        carryOver = false;
                    }
                }else{
                    //keep the bit
                    northNeighbor.add(thisBit);
                }
            }

        }
        Collections.reverse(northNeighbor);

        //interleave longitude bits back into northNeighbor
        for (int i = 0; i < longitudeBits.size(); i++){
            northNeighbor.add(i * 2+1, longitudeBits.get(i));
        }

        return fromBoolArray(northNeighbor);

    }

    public GeoHash_Impl southNeighbor() {
        // @Bonus, this is not required, but is a nice challenge
        // for bonus points

        //create list of even objects
        ArrayList<Boolean> latitudeBits = new ArrayList<>();
        ArrayList<Boolean> longitudeBits = new ArrayList<>();
        int j = 0;
        for (Boolean bit : this.geoHash){
            if (j % 2 == 0){
                latitudeBits.add(bit);
            }else{
                longitudeBits.add(bit);
            }
            j +=1;
        }

        //corner case check
        if (latitudeBits.stream().allMatch(Boolean.FALSE::equals)){
            //hit a corner case - all one's. for now just gonna throw an exception
            throw new IllegalArgumentException("No south neighbor of minimum latitude geohash");
        }

        //now the fun part
        ArrayList<Boolean> southNeighbor = new ArrayList<>();
        boolean carryUnder = false;
        for (int i = latitudeBits.size()-1; i >= 0; i--){
            Boolean thisBit = latitudeBits.get(i);
            if (i == latitudeBits.size()-1){
                //at the first character; have to flip the bit
                if (thisBit){
                    southNeighbor.add(Boolean.FALSE);
                }else{
                    southNeighbor.add(Boolean.TRUE);
                    carryUnder = true;
                }
            }else{
                //not at the first character
                if (carryUnder){
                    //carryUnder - gotta flip the bit
                    if(thisBit){ //= 1
                        southNeighbor.add(Boolean.FALSE);
                        carryUnder = false;
                    }else{
                        southNeighbor.add(Boolean.TRUE);
                        //keep carryunder true
                    }
                }else{
                    //keep the bit
                    southNeighbor.add(thisBit);
                }
            }

        }
        Collections.reverse(southNeighbor);

        //interleave longitude bits back into southNeighbor
        for (int i = 0; i < longitudeBits.size(); i++){
            southNeighbor.add(i * 2+1, longitudeBits.get(i));
        }

        return fromBoolArray(southNeighbor);

    }

    public GeoHash_Impl westNeighbor() {
        // @Bonus, this is not required, but is a nice challenge
        // for bonus points

        //create list of even objects
        ArrayList<Boolean> latitudeBits = new ArrayList<>();
        ArrayList<Boolean> longitudeBits = new ArrayList<>();
        int j = 0;
        for (Boolean bit : this.geoHash){
            if (j % 2 == 0){
                latitudeBits.add(bit);
            }else{
                longitudeBits.add(bit);
            }
            j +=1;
        }

        //corner case check
        //Predicate<Boolean> isTrue = b -> Boolean.TRUE.equals(b);
        if (longitudeBits.stream().allMatch(Boolean.FALSE::equals)){
            //hit a corner case - all one's. for now just gonna throw an exception
            throw new IllegalArgumentException("No west neighbor of minimum longitude geohash");
        }

        //now the fun part
        ArrayList<Boolean> westNeighbor = new ArrayList<>();
        boolean carryUnder = false;
        for (int i = longitudeBits.size()-1; i >= 0; i--){
            Boolean thisBit = longitudeBits.get(i);
            if (i == longitudeBits.size()-1){
                //at the first character; have to flip the bit
                if (thisBit){
                    westNeighbor.add(Boolean.FALSE);
                    //carryOver = true;
                }else{
                    westNeighbor.add(Boolean.TRUE);
                    carryUnder = true;
                }
            }else{
                //not at the first character
                if (carryUnder){
                    //carrOver - gotta flip the bit
                    if(thisBit){
                        westNeighbor.add(Boolean.FALSE);
                        carryUnder = false;
                    }else{
                        westNeighbor.add(Boolean.TRUE);
                        //keep carryUnder true
                    }
                }else{
                    //keep the bit
                    westNeighbor.add(thisBit);
                }
            }

        }
        Collections.reverse(westNeighbor);

        //interleave latitude bits back into westNeighbor
        for (int i = 0; i < latitudeBits.size(); i++){
            westNeighbor.add(i * 2, latitudeBits.get(i));
        }

        return fromBoolArray(westNeighbor);

    }

    public GeoHash_Impl eastNeighbor() {
        // @Bonus, this is not required, but is a nice challenge
        // for bonus points
        ArrayList<Boolean> latitudeBits = new ArrayList<>();
        ArrayList<Boolean> longitudeBits = new ArrayList<>();
        int j = 0;
        for (Boolean bit : this.geoHash){
            if (j % 2 == 0){
                latitudeBits.add(bit);
            }else{
                longitudeBits.add(bit);
            }
            j +=1;
        }

        //corner case check
        if (longitudeBits.stream().allMatch(Boolean.TRUE::equals)){
            //hit a corner case - all one's. for now just gonna throw an exception
            throw new IllegalArgumentException("No east neighbor of maximum longitude geohash");
        }

        //now the fun part
        ArrayList<Boolean> eastNeighbor = new ArrayList<>();
        boolean carryOver = false;
        for (int i = longitudeBits.size()-1; i >= 0; i--){
            Boolean thisBit = longitudeBits.get(i);
            if (i == longitudeBits.size()-1){
                //at the first character; have to flip the bit
                if (thisBit){
                    eastNeighbor.add(Boolean.FALSE);
                    carryOver = true;
                }else{
                    eastNeighbor.add(Boolean.TRUE);
                }
            }else{
                //not at the first character
                if (carryOver){
                    //carrOver - gotta flip the bit
                    if(thisBit){
                        eastNeighbor.add(Boolean.FALSE);
                        //keep carryover true
                    }else{
                        eastNeighbor.add(Boolean.TRUE);
                        carryOver = false;
                    }
                }else{
                    //keep the bit
                    eastNeighbor.add(thisBit);
                }
            }

        }
        Collections.reverse(eastNeighbor);

        //interleave latitude bits back into westNeighbor
        for (int i = 0; i < latitudeBits.size(); i++){
            eastNeighbor.add(i * 2, latitudeBits.get(i));
        }

        return fromBoolArray(eastNeighbor);    }

    @Override
    public Iterator<Boolean> iterator() {
        // @ToDo, create an interator for the bits in the GeoHash
        return geoHash.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoHash_Impl booleans = (GeoHash_Impl) o;
        return bitsOfPrecision == booleans.bitsOfPrecision && Objects.equals(geoHash, booleans.geoHash);
    }

    /**@Override
    public boolean equals(Object o) {
    // @ToDo, fill this in
    // Always override equals and hashcode together
    // Your IDE can probably do this part for you
    return false;
    }*/


   /* @Override
    public int hashCode() {
        // @ToDo, fill this in
        // Always override equals and hashcode together
        // Your IDE can probably do this part for you
        return 0;
    }*/


    @Override
    public int hashCode() {
        return Objects.hash(bitsOfPrecision, geoHash);
    }

    /**public ArrayList<Boolean> getGeoHash()
     {
     return geoHash;
     }*/

    public String toString() {
        // @ToDo, fill this in
        StringBuilder stringBuilder = new StringBuilder();
        this.geoHash.forEach(geoHashVal -> {
            if (geoHashVal) {
                stringBuilder.append('1');
            } else {
                stringBuilder.append('0');
            }
        });

        return stringBuilder.toString();
    }
}