package fr.iban.events.enums;

import fr.iban.events.games.SnowBattleGame;
import fr.iban.events.games.*;
import fr.iban.events.options.Option;

import java.util.List;
import java.util.function.Supplier;

public enum GameType {

    SUMOTORI("Sumotori", "Tous les joueurs s'affrontent dans une arène, l'objectif et de faire valser les joueurs hors de celle-ci. Le dernier joueur à y subsister sera déclaré vainqueur.", LastToFallGame.getArenaOptions()),
    JUMP("Jump", "L'objectif est de bondir d'obstacle en obstacle pour arriver en haut le premier !", JumpGame.getArenaOptions()),
    SPEEF("Spleef", "Equipé d'une pelle, vous devrez faire tomber vos adversaires dans le vide !", LastToFallGame.getArenaOptions()),
    TNTRUN("TNT-Run", "Les blocs sur lesquels vous marchez tombent, soyez le dernier à subsister sur la plateforme !", LastToFallGame.getArenaOptions()),
    DROPPER("Dropper", "L'objectif est de sauter au coeur de la map et d'atteindre un espace restreint pour passer a la map suivante !", DropperGame.getArenaOptions()),
    ICERACE("IceRace", "Course de bateau sur glace !", IceRaceGame.getArenaOptions()),
    SNOWBATTLE("SnowBattle", "Lancez-vous des boules de neiges et soyez le premier a garder vos 5 vies pour gagner", SnowBattleGame.getArenaOptions()),
    PITCHOUT("PitchOut", "Affrontez vous dans une grande arène, et soyez le dernier a garder vos 3 vies pour gagner !", LastToFallGame.getArenaOptions());

    private final String name;
    private final String desc;
    private final List<Option<?>> arenaOptions;
    private Supplier<Game> gameSupplier;

    GameType(String name, String desc, List<Option<?>> arenaOptions) {
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

    public List<Option<?>> getArenaOptions() {
        return arenaOptions;
    }

    public void registerHandler(Supplier<Game> gameSupplier) {
        this.gameSupplier = gameSupplier;
    }

    public Game getNewHandler() {
        if(gameSupplier == null) {
            return null;
        }
        return gameSupplier.get();
    }

}
