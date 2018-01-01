package erogenousbeef.bigreactors.common;

import erogenousbeef.bigreactors.api.data.ReactorInteriorData;
import erogenousbeef.bigreactors.api.registry.ReactorInterior;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.List;

public class DebugCommand extends CommandBase {

    @Override
    public String getName() {
        return "erdebug";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "erdebug you know what to do ...";
    }

    /**
     * Callback for when the command is executed
     *
     * @param server
     * @param sender
     * @param args
     */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (!this.areArgumentsPresent(sender, args, 2)) {
            return;
        }

        // process sub-commands
        switch (args[0].toLowerCase()) {

            case "custom":
                this.executeCustom(server, sender, args);
                break;

            case "reactor":
                this.executeReactor(server, sender, args);
                break;

        }
    }

    private void executeCustom(MinecraftServer server, ICommandSender sender, String[] args) {
        // TODO put custom code here
    }

    private void executeReactor(MinecraftServer server, ICommandSender sender, String[] args) {

        switch (args[1].toLowerCase()) {

            // reactor dumpmoderator block|fluid name
            case "dumpmoderator": {

                if (!this.areArgumentsPresent(sender, args, 4)) {
                    return;
                }

                final boolean fluid = 0 == "fluid".compareTo(args[2]);
                final String name = args[3];
                final ReactorInteriorData data = this.getReactorInteriorData(fluid, name);

                this.printReactorInteriorData(sender, fluid, name, data);
                break;
            }

            // reactor changemoderator block|fluid name absorption heatEfficiency moderation heatConductivity
            case "changemoderator": {

                if (!this.areArgumentsPresent(sender, args, 4)) {
                    return;
                }

                final boolean fluid = 0 == "fluid".compareTo(args[2]);
                final String name = args[3];
                final ReactorInteriorData data = this.getReactorInteriorData(fluid, name);

                data.absorption = Float.parseFloat(args[4]);
                data.heatEfficiency = Float.parseFloat(args[5]);
                data.moderation = Float.parseFloat(args[6]);
                data.heatConductivity = Float.parseFloat(args[7]);

                this.printReactorInteriorData(sender, fluid, name, data);
                break;
            }
        }
    }

    private boolean areArgumentsPresent(ICommandSender sender, String[] args, int minimum) {

        boolean valid = args.length >= minimum;

        if (!valid)
            sender.sendMessage(new TextComponentString("Invalid arguments"));

        return valid;
    }

    private ReactorInteriorData getReactorInteriorData(boolean fluid, String name) {
        return fluid ? ReactorInterior.getFluidData(name) : ReactorInterior.getBlockData(name);
    }

    private void printReactorInteriorData(ICommandSender sender, boolean fluid, String name, ReactorInteriorData data) {

        sender.sendMessage(new TextComponentString(String.format("Reactor moderator data: %1$s (%2$s)",
                name, fluid ? "fluid" : "block")));

        if (null == data) {

            sender.sendMessage(new TextComponentString("Moderator not found!"));

        } else {

            sender.sendMessage(new TextComponentString(String.format(" - absorption %1$f", data.absorption)));
            sender.sendMessage(new TextComponentString(String.format(" - heatEfficiency %1$f", data.heatEfficiency)));
            sender.sendMessage(new TextComponentString(String.format(" - moderation %1$f", data.moderation)));
            sender.sendMessage(new TextComponentString(String.format(" - heatConductivity %1$f", data.heatConductivity)));
        }
    }
}