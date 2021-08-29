package ishankaul.personal.proximitydb;

public class GeoHashFactory_Impl implements GeoHashFactory {
    @Override
    public GeoHash with(double lat, double lon, int bitsOfPrecision) {
        return GeoHash_Impl.from(lat, lon, bitsOfPrecision);
    }
}
