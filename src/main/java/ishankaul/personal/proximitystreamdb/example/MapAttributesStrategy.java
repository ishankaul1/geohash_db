package ishankaul.personal.proximitystreamdb.example;


import ishankaul.personal.proximitystreamdb.Attribute;
import ishankaul.personal.proximitystreamdb.AttributesStrategy;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is a sample strategy that converts all of the entries in a map
 * to attributes.
 *
 */
public class MapAttributesStrategy implements AttributesStrategy<Map<String,?>> {

    @Override
    public Collection<Attribute> getAttributes(Map<String, ?> data) {

        return data.entrySet()
                .stream()
                .map(e -> new Attribute(e.getKey(), e.getValue().getClass(), e.getValue()))
                .collect(Collectors.toList());
    }
}
