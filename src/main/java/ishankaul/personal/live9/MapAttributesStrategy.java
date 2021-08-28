package ishankaul.personal.live9;

import ishankaul.personal.live7.Attribute;
import ishankaul.personal.live7.AttributesStrategy;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class MapAttributesStrategy implements AttributesStrategy<Map<String,?>> {

    @Override
    public Collection<Attribute> getAttributes(Map<String, ?> data) {

        return data.entrySet().stream().map(e ->
                new Attribute(e.getKey(), e.getValue().getClass(), e.getValue())).collect(Collectors.toList());
    }
}
