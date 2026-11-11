package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class TameEveryMobCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Component.text("TameEveryMob makes living mobs follow the player who kills them.")
                .color(NamedTextColor.GOLD));
        sender.sendMessage(Component.text("Kill a mob while you have the tameeverymob.use permission.")
                .color(NamedTextColor.GRAY));
        return true;
    }
}
