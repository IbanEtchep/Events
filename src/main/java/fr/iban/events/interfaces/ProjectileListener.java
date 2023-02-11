package fr.iban.events.interfaces;

import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public interface ProjectileListener {
    void onProjectileEvent(ProjectileHitEvent e);
    void onThrow(ProjectileLaunchEvent e);
}
