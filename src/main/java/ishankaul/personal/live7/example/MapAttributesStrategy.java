package ishankaul.personal.live7.example;


import ishankaul.personal.live7.Attribute;
import ishankaul.personal.live7.AttributesStrategy;

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
