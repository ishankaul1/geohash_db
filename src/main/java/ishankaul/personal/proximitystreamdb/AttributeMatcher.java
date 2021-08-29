package ishankaul.personal.proximitystreamdb;

public interface AttributeMatcher<T> {

    public boolean matches(Attribute<T> attr);

}
