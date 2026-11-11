package com.enderdevelopment.plugin;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private NamespacedKey ownerKey;

    @Override
    public void onEnable() {
        ownerKey = new NamespacedKey(this, "owner");
        MobClaimListener listener = new MobClaimListener(this, ownerKey);
        Bukkit.getPluginManager().registerEvents(listener, this);

        TameEveryMobCommand command = new TameEveryMobCommand();
        if (getCommand("tameeverymob") != null) {
            getCommand("tameeverymob").setExecutor(command);
        }
        getLogger().info("TameEveryMob enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("TameEveryMob disabled.");
    }
}
