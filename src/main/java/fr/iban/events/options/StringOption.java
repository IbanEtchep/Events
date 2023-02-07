package fr.iban.events.options;

public class StringOption extends Option<String> {


    public StringOption(String name, String defaultValue) {
        super(name);
        value = defaultValue;
    }

    public StringOption(String name) {
        super(name);
        value = "nd";
    }

}
