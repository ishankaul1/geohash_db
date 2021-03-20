package edu.vanderbilt.cs.live7;


import edu.vanderbilt.cs.live6.CompositeTree;
import edu.vanderbilt.cs.live6.DataAndPosition;
import edu.vanderbilt.cs.live6.ProximityDB;
import edu.vanderbilt.cs.live7.ProximityDB_Operation;

public class Insert_Operation implements ProximityDB_Operation {
    private DataAndPosition dataAndPosition;

    public Insert_Operation( DataAndPosition dataAndPosition){
        this.dataAndPosition = dataAndPosition;
    }

    public void execute(ProximityDB proximityDB){
        proximityDB.insert(dataAndPosition);
    }
}
