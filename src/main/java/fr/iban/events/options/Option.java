package fr.iban.events.options;

public abstract class Option<T> {


    private final String name;
    protected T value;

    public Option(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
