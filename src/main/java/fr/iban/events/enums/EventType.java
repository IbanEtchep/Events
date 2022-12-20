package fr.iban.events.enums;

import fr.iban.events.*;
import fr.iban.events.options.Option;

import java.util.List;

public enum EventType {

    SUMOTORI("Sumotori", "Tous les joueurs s'affrontent dans une arène, l'objectif et de faire valser les joueurs hors de celle-ci. Le dernier joueur à y subsister sera déclaré vainqueur.", LastToFallEvent.getArenaOptions()),
    JUMP("Jump", "L'objectif est de bondir d'obstacle en obstacle pour arriver en haut le premier !", JumpEvent.getArenaOptions()),
    SPEEF("Spleef", "Equipé d'une pelle, vous devrez faire tomber vos adversaires dans le vide !", LastToFallEvent.getArenaOptions()),
    TNTRUN("TNT-Run", "Les blocs sur lesquels vous marchez tombent, soyez le dernier à subsister sur la plateforme !", LastToFallEvent.getArenaOptions()),
    DROPPER("Dropper", "L'objectif est de sauter au coeur de la map et d'atteindre un espace restreint pour passer a la map suivante !", DropperEvent.getArenaOptions()),
    SNOWBATTLE("SnowBattle", "Lancez-vous des boules de neiges et soyez le premier a garder vos 5 vies pour gagner", SnowEvent.getArenaOptions());

    private final String name;
    private final String desc;
    private final List<Option> arenaOptions;

    EventType(String name, String desc, List<Option> arenaOptions) {
        this.name = name;
        this.desc = desc;
        this.arenaOptions = arenaOptions;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public List<Option> getArenaOptions() {
        return arenaOptions;
    }

}
