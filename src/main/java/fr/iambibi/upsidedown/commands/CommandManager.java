package fr.iambibi.upsidedown.commands;

import fr.iambibi.upsidedown.UpsideDown;
import lombok.Getter;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;

public class CommandManager {
    @Getter
    static Lamp handler;

    public static void init() {
        handler = BukkitLamp.builder(UpsideDown.getInstance())
                .build();

        registerCommands();
    }

    private static void registerCommands() {
        handler.register(
                new TestCommand()
        );
    }
}
