package ishankaul.personal.proximitydb;

import ishankaul.personal.proximitydb.example.ProximityTestBuilding;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProximityDBTest {

    private ProximityDBFactory factory = new ProximityDBFactory();

    @Test
    public void testSimpleInsert() {
        int bitsOfPrecision = 16;
        ProximityDB<ProximityTestBuilding> db = factory.create(bitsOfPrecision);
        db.insert(DataAndPosition.with(0,0, new ProximityTestBuilding("test")));

        for(int i = 0; i < bitsOfPrecision; i++) {
            assertTrue(db.contains(Position.with(0,0), i));
        }
    }

    @Test
    public void testSimpleDelete() {
        int bitsOfPrecision = 16;
        ProximityDB<ProximityTestBuilding> db = factory.create(bitsOfPrecision);
        db.insert(DataAndPosition.with(0,0, new ProximityTestBuilding("test")));
        db.delete(Position.with(0,0));

        for(int i = 0; i < bitsOfPrecision; i++) {
            assertTrue(!db.contains(Position.with(0,0), i));
        }
    }

    @Test
    public void testZeroBits(){
        ProximityDB<ProximityTestBuilding> db = factory.create(16);
        db.insert(DataAndPosition.with(0,0, new ProximityTestBuilding("test")));
        db.insert(DataAndPosition.with(90,180, new ProximityTestBuilding("test")));
        db.insert(DataAndPosition.with(-90,-180, new ProximityTestBuilding("test")));
        db.insert(DataAndPosition.with(-90, 180, new ProximityTestBuilding("test")));
        db.insert(DataAndPosition.with(90, -180, new ProximityTestBuilding("test")));

        assertEquals(5, db.nearby(Position.with(0,0), 0).size());
    }

    @Test
    public void testZeroBitsDelete(){
        ProximityDB<ProximityTestBuilding> db = factory.create(16);
        db.insert(DataAndPosition.with(0,0, new ProximityTestBuilding("test")));
        db.insert(DataAndPosition.with(90,180, new ProximityTestBuilding("test")));
        db.insert(DataAndPosition.with(-90,-180, new ProximityTestBuilding("test")));
        db.insert(DataAndPosition.with(-90, 180, new ProximityTestBuilding("test")));
        db.insert(DataAndPosition.with(90, -180, new ProximityTestBuilding("test")));

        db.delete(Position.with(0,0), 0);

        assertEquals(0, db.nearby(Position.with(0,0), 0).size());
    }

    @Test
    public void testInsertDeleteSeries(){

        ProximityDB<ProximityTestBuilding> db = factory.create(16);
        db.insert(DataAndPosition.with(0,0, new ProximityTestBuilding("test")));
        db.insert(DataAndPosition.with(90,180, new ProximityTestBuilding("test")));
        db.insert(DataAndPosition.with(-90,-180, new ProximityTestBuilding("test")));
        db.insert(DataAndPosition.with(-90, 180, new ProximityTestBuilding("test")));
        db.insert(DataAndPosition.with(90, -180, new ProximityTestBuilding("test")));

        assertTrue(db.contains(Position.with(0, 0), 16));
        assertTrue(db.contains(Position.with(90, 180), 16));
        assertTrue(db.contains(Position.with(-90, -180), 16));
        assertTrue(db.contains(Position.with(-90, 180), 16));
        assertTrue(db.contains(Position.with(90, -180), 16));
        assertTrue(db.contains(Position.with(90.5, -180.5), 16));
        assertTrue(!db.contains(Position.with(1, -1), 16));
        assertTrue(!db.contains(Position.with(45, -45), 16));

        db.delete(Position.with(90, -180));
        assertTrue(!db.contains(Position.with(90, -180), 16));

        db.delete(Position.with(1, 1), 1);
        assertTrue(db.contains(Position.with(-90, -180), 16));
        assertTrue(!db.contains(Position.with(90, 180), 16));
        db.insert(DataAndPosition.with(90, 180, new ProximityTestBuilding("test")));
        assertTrue(db.contains(Position.with(90, 180), 16));

    }

}
