package fr.iban.events.options;

public class IntOption extends Option<Integer> {

    public IntOption(String name, int defaultVal) {
        super(name);
        this.value = defaultVal;
    }

}
