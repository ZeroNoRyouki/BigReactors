package erogenousbeef.bigreactors.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class DebugCommand extends CommandBase {

    @Override
    public String getName() {
        return "erdebug";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "erdebug";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // TODO put your stupid-test-code here
        sender.sendMessage(new TextComponentString("THIS IS A TEST"));
    }
}
