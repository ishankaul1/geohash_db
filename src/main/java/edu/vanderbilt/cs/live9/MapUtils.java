package edu.vanderbilt.cs.live9;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {

    public static <K,V> Map<K,V> of(Object...items){
        Map<K,V> data = new HashMap<>();
        for(int i = 0; i < items.length; i += 2){
            data.put((K)items[i], (V)items[i+1]);
        }
        return data;
    }

}
