package ishankaul.personal.proximitystreamdb;

import ishankaul.personal.proximitydb.ProximityDB;

@FunctionalInterface
public interface ProximityDB_Operation {
    void execute(ProximityDB proximityDB);
}
