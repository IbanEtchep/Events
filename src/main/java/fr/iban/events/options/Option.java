package fr.iban.events.options;

import fr.iban.events.enums.OptionType;

public abstract class Option {


    protected OptionType type;
    private final String name;

    public Option(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public OptionType getType() {
        return type;
    }


}
