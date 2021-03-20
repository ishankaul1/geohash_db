package edu.vanderbilt.cs.live7;
import edu.vanderbilt.cs.live6.CompositeTree;
import edu.vanderbilt.cs.live6.DataAndPosition;
import edu.vanderbilt.cs.live6.Position;
import edu.vanderbilt.cs.live6.ProximityDB;

public class Delete_Operation implements ProximityDB_Operation{

    private Position position;
    private int bitsOfPrecision;

    public Delete_Operation(Position position){
        this.position = position;
        this.bitsOfPrecision = -1;
    }

    public Delete_Operation(Position position, int bitsOfPrecision){
        this.position = position;
        this.bitsOfPrecision = bitsOfPrecision;
    }

    @Override
    public void execute(ProximityDB proximityDB) {
        if (this.bitsOfPrecision >= 0){
            proximityDB.delete(position, bitsOfPrecision);
        }else{
            proximityDB.delete(position);
        }
    }
}