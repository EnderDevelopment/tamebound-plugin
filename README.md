# Minecraft Plugin: Tamebound

Tamebound is a Minecraft plugin that makes living mobs tameable through a lethal player attack, then gives claimed mobs wolf-like follow behavior. A permitted player can claim most living mobs by killing them, after which the mob follows its owner, stops targeting normally, and can be teleported back when it gets too far away.

## Install

1. Use a Paper Minecraft server running version 1.21.11 or a compatible 1.21 server.
2. Install Java 21 or newer.
3. Build the plugin with Maven:
   ```bash
   mvn package
   ```
4. Copy the generated `target/plugin.jar` file into the server's `plugins` folder.
5. Start or restart the server.
6. Confirm that `TameEveryMob` appears as enabled in the server log.

## Use

1. Give a player the `tameeverymob.use` permission. This permission is enabled by default.
2. Have the player attack a living mob with a direct attack or a projectile.
3. When the attack would reduce the mob to zero health, the attack is canceled and the mob is claimed instead.
4. The claimed mob is restored to 25% of its maximum health and follows the player like a wolf.
5. Claimed mobs stop targeting normally and move toward their owner when more than 6 blocks away.
6. If a claimed mob is more than 16 blocks away or the owner changes worlds, it is teleported near the owner.
7. The owning player cannot damage their claimed mob through direct or projectile attacks.
8. Run `/tameeverymob` or `/tem` to display the plugin help message.

The plugin claims living entities other than players. Entities that support Minecraft's built-in tameable system are also marked as tamed and assigned to the claiming player.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/2cb5c4fe-a61e-4123-95f7-061baf2e7f14?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=2cb5c4fe-a61e-4123-95f7-061baf2e7f14
