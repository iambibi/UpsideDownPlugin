package fr.iambibi.upsidedown.generation.mirror;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.*;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.WritableBookMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MirrorBlockStates {
    //todo: test all supported block states (BARREL, FURNACE, BEACON, SPAWNER, ect.)
    public static final List<Material> BLOCK_STATES_SUPPORTED = new ArrayList<>() {
        {
            addAll(Tag.ALL_SIGNS.getValues());
            addAll(Tag.ALL_HANGING_SIGNS.getValues());
            addAll(Tag.ITEMS_SKULLS.getValues());
            add(Material.CHISELED_BOOKSHELF);
            add(Material.LECTERN);
            add(Material.CHEST);
        }
    };
    public static void mirrorBlockState(BlockState sourceState, BlockState targetState) {
        if (sourceState instanceof Sign src && targetState instanceof Sign trg) {
            for (Side side : Side.values()) {

                SignSide srcSide = src.getSide(side);
                SignSide trgSide = trg.getSide(side);

                trgSide.setGlowingText(srcSide.isGlowingText());
                trgSide.setColor(srcSide.getColor());

                for (int i = 0; i < 4; i++) {
                    trgSide.line(i, transformText(srcSide.line(i)));
                }
            }
        } else if (sourceState instanceof Skull src && targetState instanceof Skull trg) {
            trg.setProfile(src.getProfile());
        } else if (sourceState instanceof ChiseledBookshelf src
                && targetState instanceof ChiseledBookshelf trg) {
            trg.getSnapshotInventory().clear();

            for (int i = 0; i < src.getSnapshotInventory().getSize(); i++) {
                ItemStack item = src.getSnapshotInventory().getItem(i);

               if (item == null) continue;

               trg.getSnapshotInventory().setItem(i, mirrorBook(item.clone()));
            }
        } else if (sourceState instanceof Lectern src
                && targetState instanceof Lectern trg) {
            trg.getSnapshotInventory().clear();

            ItemStack book = src.getSnapshotInventory().getItem(0);
            if (book != null) {
                trg.getSnapshotInventory().setItem(0, mirrorBook(book.clone()));
            }

            trg.setPage(src.getPage());
        }

        targetState.update(false, false);
    }

    private static ItemStack mirrorBook(ItemStack source) {
        ItemStack clone = source.clone();
        ItemMeta meta = clone.getItemMeta();
        if (meta == null) return clone;

        if (clone.getType() == Material.WRITTEN_BOOK && meta instanceof BookMeta book) {
            List<Component> pages = new ArrayList<>();
            for (Component page : book.pages()) {
                pages.add(transformText(page));
            }
            clone.editMeta(BookMeta.class, meta1 -> meta1.pages(pages));
        }

        else if (clone.getType() == Material.WRITABLE_BOOK && meta instanceof WritableBookMeta writable) {
            List<TextComponent> pages = writable.getPages()
                    .stream()
                    .map(Component::text).toList();
            List<String> newPages = new ArrayList<>();

            for (Component page : pages) {
                newPages.add(PlainTextComponentSerializer.plainText().serialize(transformText(page)));
            }

            clone.editMeta(WritableBookMeta.class, meta1 -> writable.setPages(newPages));
        }

        return clone;
    }

    /**
     * Transforms text by inverting case and adding obfuscation randomly
     * except for accented characters é, à, è.
     *
     * @param inputComponent The input Component to transform
     * @return The transformed Component
     */
    public static Component transformText(Component inputComponent) {
        StringBuilder out = new StringBuilder();

        String input = PlainTextComponentSerializer.plainText().serialize(inputComponent);

        for (char c : input.toCharArray()) {
            if (new Random().nextFloat() < 0.06) {
                out.append("§kO");
            } else if ("éàè".indexOf(c) >= 0) {
                out.append(c);
            } else if (Character.isUpperCase(c)) {
                out.append(Character.toLowerCase(c));
            } else if (Character.isLowerCase(c)) {
                out.append(Character.toUpperCase(c));
            } else {
                out.append(c);
            }
        }

        return Component.text(out.toString());
    }
}