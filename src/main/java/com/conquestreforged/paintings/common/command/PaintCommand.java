package com.conquestreforged.paintings.common.command;

/**
 * @author dags <dags@dags.me>
 */
public class PaintCommand {
//
//    @Override
//    public String getName() {
//        return "paint";
//    }
//
//    @Override
//    public String getUsage(ICommandSender sender) {
//        return "paint <type> <art>";
//    }
//
//    @Override
//    public List<String> getAliases() {
//        return Collections.singletonList("paint");
//    }
//
//    @Override
//    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//        if (args.length != 2) {
//            throw new CommandException("Command requires 2 arguments");
//        }
//
//        String name = args[0];
//        PaintingVariant type = PaintingVariant.fromName(name);
//        if (!name.equalsIgnoreCase("Vanilla") && !type.isPresent()) {
//            throw new CommandException("Unknown painting type " + name);
//        }
//
//        if (type.isPresent()) {
//            name = type.getName();
//        }
//
//        String art = args[1];
//        if (!Art.valid(art, VanillaArt.ALL) && !Art.valid(art, ModArt.ALL)) {
//            throw new CommandException("Unknown art type " + art);
//        }
//
//        ItemStack stack = PaintingItem.createStack(name, art);
//        EntityPlayerMP player = (EntityPlayerMP) sender;
//        player.inventory.addItemStackToInventory(stack);
//    }
//
//    @Override
//    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
//        return sender.canUseCommand(0, getName());
//    }
//
//    @Override
//    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
//        if (args.length == 0) {
//            return suggestions(PaintingVariant.getIds(), "");
//        }
//        if (args.length == 1) {
//            return suggestions(PaintingVariant.getIds(), args[0]);
//        }
//        if (args.length == 2) {
//            return suggestions(PaintingArt.getNames(), args[1]);
//        }
//        return Collections.emptyList();
//    }
//
//    @Override
//    public boolean isUsernameIndex(String[] args, int index) {
//        return false;
//    }
//
//    @Override
//    public int compareTo(ICommand o) {
//        return getName().compareTo(o.getName());
//    }
//
//    private static List<String> suggestions(Stream<String> stream, String input) {
//        String arg = input.toLowerCase();
//        return stream.filter(s -> !s.equalsIgnoreCase(arg) && s.toLowerCase().startsWith(arg)).collect(Collectors.toList());
//    }
}
