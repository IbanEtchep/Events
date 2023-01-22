package fr.iban.events;

import fr.iban.bukkitcore.rewards.Reward;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameConfig implements ConfigurationSerializable {

    private String name;
    private Map<PotionEffectType, Integer> potionEffects = new HashMap<>();
    private Kit kit;
    private Reward winReward;
    private Reward participationReward;
    private boolean pvp = false;
    private boolean damage = false;

    public GameConfig(String name) {
        this.name = name;
    }

    public GameConfig(String name, Map<PotionEffectType, Integer> potionEffects, Kit kit) {
        this.name = name;
        this.potionEffects = potionEffects;
        this.kit = kit;
    }

    public GameConfig(Map<String, Object> map){
        this.name = (String) map.get("name");
        this.kit = (Kit) map.get("kit");
        if(map.containsKey("potionEffects")){
            List<String> potionStringList = (List<String>) map.get("potionEffects");
            for (String potionString : potionStringList) {
                String[] split = potionString.split(":");
                PotionEffectType type = PotionEffectType.getByName(split[0]);
                int level = Integer.parseInt(split[1]);
                if(type != null) {
                    potionEffects.put(type, level);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<PotionEffectType, Integer> getPotionEffects() {
        return potionEffects;
    }

    public void setPotionEffects(Map<PotionEffectType, Integer> potionEffects) {
        this.potionEffects = potionEffects;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public boolean isDamage() {
        return damage;
    }

    public void setDamage(boolean damage) {
        this.damage = damage;
    }

    public Reward getWinReward() {
        return winReward;
    }

    public void setWinReward(Reward winReward) {
        this.winReward = winReward;
    }

    public Reward getParticipationReward() {
        return participationReward;
    }

    public void setParticipationReward(Reward participationReward) {
        this.participationReward = participationReward;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        if(kit != null){
            map.put("kit", kit);
        }
        if(!potionEffects.isEmpty()){
            List<String> potions = potionEffects.entrySet()
                    .stream().map(entry -> entry.getKey().getName()+":"+entry.getValue()+"").toList();
            map.put("potionEffects", potions);
        }
        return map;
    }
}
