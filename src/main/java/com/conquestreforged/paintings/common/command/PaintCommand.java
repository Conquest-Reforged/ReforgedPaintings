package com.conquestreforged.paintings.common.command;

import com.conquestreforged.paintings.common.art.Art;
import com.conquestreforged.paintings.common.art.ModArt;
import com.conquestreforged.paintings.common.art.VanillaArt;
import com.conquestreforged.paintings.common.entity.PaintingArt;
import com.conquestreforged.paintings.common.entity.PaintingType;
import com.conquestreforged.paintings.common.item.PaintingItem;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dags <dags@dags.me>
 */
public class PaintCommand implements ICommand {

    @Override
    public String getName() {
        return "paint";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "paint <type> <art>";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("paint");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 2) {
            throw new CommandException("Command requires 2 arguments");
        }

        String name = args[0];
        PaintingType type = PaintingType.fromName(name);
        if (!name.equalsIgnoreCase("Vanilla") && !type.isPresent()) {
            throw new CommandException("Unknown painting type " + name);
        }

        if (type.isPresent()) {
            name = type.getName();
        }

        String art = args[1];
        if (!Art.valid(art, VanillaArt.ALL) && !Art.valid(art, ModArt.ALL)) {
            throw new CommandException("Unknown art type " + art);
        }

        ItemStack stack = PaintingItem.createStack(name, art);
        EntityPlayerMP player = (EntityPlayerMP) sender;
        player.inventory.addItemStackToInventory(stack);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(0, getName());
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 0) {
            return suggestions(PaintingType.getIds(), "");
        }
        if (args.length == 1) {
            return suggestions(PaintingType.getIds(), args[0]);
        }
        if (args.length == 2) {
            return suggestions(PaintingArt.getNames(), args[1]);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return getName().compareTo(o.getName());
    }

    private static List<String> suggestions(Stream<String> stream, String input) {
        String arg = input.toLowerCase();
        return stream.filter(s -> !s.equalsIgnoreCase(arg) && s.toLowerCase().startsWith(arg)).collect(Collectors.toList());
    }
}
