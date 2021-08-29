package ishankaul.personal.proximitystreamdb;


import ishankaul.personal.proximitydb.DataAndPosition;
import ishankaul.personal.proximitydb.ProximityDB;

/**
 * Represents an 'insert' operation on the DB. Used to 'rebuild' previous state of the DB history.
 */

public class Insert_Operation implements ProximityDB_Operation {
    private DataAndPosition dataAndPosition;

    public Insert_Operation( DataAndPosition dataAndPosition){
        this.dataAndPosition = dataAndPosition;
    }

    public void execute(ProximityDB proximityDB){
        proximityDB.insert(dataAndPosition);
    }
}
