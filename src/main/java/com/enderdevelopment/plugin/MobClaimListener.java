package com.enderdevelopment.plugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class MobClaimListener implements Listener {
    private final NamespacedKey ownerKey;
    private final Map<UUID, UUID> claimedMobs = new HashMap<>();

    public MobClaimListener(JavaPlugin plugin, NamespacedKey ownerKey) {
        this.ownerKey = ownerKey;
        Bukkit.getScheduler().runTaskTimer(plugin, this::followOwners, 10L, 10L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLethalAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity mob) || mob instanceof Player) {
            return;
        }

        Player player = findPlayerDamager(event.getDamager());
        if (player == null || !player.hasPermission("tameeverymob.use") || mob.isDead()) {
            return;
        }
        if (getOwner(mob) != null) {
            return;
        }

        double remainingHealth = mob.getHealth() - event.getFinalDamage();
        if (remainingHealth > 0.0) {
            return;
        }

        event.setCancelled(true);
        mob.setHealth(Math.max(1.0, Math.min(mob.getMaxHealth(), mob.getMaxHealth() * 0.25)));
        claim(mob, player);
        player.sendMessage(Component.text("You tamed " + mob.getType().name().toLowerCase().replace('_', ' ') + ". It will follow you like a wolf.")
                .color(NamedTextColor.GREEN));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onClaimedMobDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity mob)) {
            return;
        }
        UUID owner = getOwner(mob);
        if (owner == null) {
            return;
        }
        Player attacker = findPlayerDamager(event.getDamager());
        if (attacker != null && attacker.getUniqueId().equals(owner)) {
            event.setCancelled(true);
        }
    }

    private void claim(LivingEntity mob, Player player) {
        UUID ownerId = player.getUniqueId();
        mob.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, ownerId.toString());
        claimedMobs.put(mob.getUniqueId(), ownerId);
        if (mob instanceof Tameable tameable) {
            tameable.setTamed(true);
            tameable.setOwner(player);
        }
    }

    private UUID getOwner(LivingEntity mob) {
        String value = mob.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
        if (value == null) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private void followOwners() {
        Iterator<Map.Entry<UUID, UUID>> iterator = claimedMobs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, UUID> entry = iterator.next();
            Entity entity = Bukkit.getEntity(entry.getKey());
            Player owner = Bukkit.getPlayer(entry.getValue());
            if (!(entity instanceof LivingEntity mob) || mob.isDead() || owner == null || !owner.isOnline()) {
                iterator.remove();
                continue;
            }

            if (mob instanceof org.bukkit.entity.Mob normalMob) {
                normalMob.setTarget(null);
            }
            if (!mob.getWorld().equals(owner.getWorld())) {
                mob.teleport(safeFollowLocation(owner));
                continue;
            }

            double distanceSquared = mob.getLocation().distanceSquared(owner.getLocation());
            if (distanceSquared > 256.0) {
                mob.teleport(safeFollowLocation(owner));
            } else if (distanceSquared > 36.0) {
                moveToward(mob, owner.getLocation());
            }
        }
    }

    private Location safeFollowLocation(Player owner) {
        Location location = owner.getLocation().clone();
        Vector backward = location.getDirection().normalize().multiply(-2.0);
        location.add(backward);
        return location;
    }

    private void moveToward(LivingEntity mob, Location target) {
        Vector direction = target.toVector().subtract(mob.getLocation().toVector()).normalize();
        mob.setVelocity(direction.multiply(0.35).setY(Math.max(-0.2, Math.min(0.35, direction.getY()))));
    }

    private Player findPlayerDamager(Entity damager) {
        if (damager instanceof Player player) {
            return player;
        }
        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Player player) {
            return player;
        }
        return null;
    }
}
