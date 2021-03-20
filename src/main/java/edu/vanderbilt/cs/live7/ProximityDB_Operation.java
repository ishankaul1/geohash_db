package edu.vanderbilt.cs.live7;

import edu.vanderbilt.cs.live6.ProximityDB;

@FunctionalInterface
public interface ProximityDB_Operation {
    void execute(ProximityDB proximityDB);
}
