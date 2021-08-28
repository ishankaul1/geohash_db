package ishankaul.personal.live7;


import ishankaul.personal.live6.CompositeTree;
import ishankaul.personal.live6.DataAndPosition;
import ishankaul.personal.live6.ProximityDB;
import ishankaul.personal.live7.ProximityDB_Operation;

public class Insert_Operation implements ProximityDB_Operation {
    private DataAndPosition dataAndPosition;

    public Insert_Operation( DataAndPosition dataAndPosition){
        this.dataAndPosition = dataAndPosition;
    }

    public void execute(ProximityDB proximityDB){
        proximityDB.insert(dataAndPosition);
    }
}
