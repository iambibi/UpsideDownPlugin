package fr.iambibi.upsidedown.commands;

import fr.iambibi.upsidedown.UpsideDown;
import fr.iambibi.upsidedown.additions.breach.BreachGenerator;
import fr.iambibi.upsidedown.additions.breach.BreachManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;

public class BreachCreateCommand {

    @Command("breach create")
    @Description("Permet de créer une brèche")
    public void createBreach(Player player) {


        new BreachGenerator().createBreach(player);
    }

    @Command("breach test")
    @Description("Permet de créer une brèche")
    public void tescreateBreach(Player player) {
        System.out.println(BreachManager.isInUpsideDownPortal(player.getTargetBlock(null, 10)));
    }
}
