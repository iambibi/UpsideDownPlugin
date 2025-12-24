package fr.iambibi.upsidedown.commands;

import fr.iambibi.upsidedown.fixes.postprocesswarn.SculkPatchConfiguration;
import fr.iambibi.upsidedown.fixes.postprocesswarn.SculkPatchFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;

public class TestCommand {

    @Command("test")
    @Description("Permet de changer le spawn")
    public void setSpawn(Player player) {

        ServerLevel level = ((CraftWorld) player.getWorld()).getHandle();
        BlockPos pos = new BlockPos((int) player.getLocation().getX(), (int) player.getLocation().getY(), (int) player.getLocation().getZ());

        SculkPatchFeature.place(level, pos, level.random, new SculkPatchConfiguration());
    }
}
