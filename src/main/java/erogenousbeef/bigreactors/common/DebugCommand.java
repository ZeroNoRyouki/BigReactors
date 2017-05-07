package erogenousbeef.bigreactors.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class DebugCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "erdebug";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "erdebug";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // TODO put your stupid-test-code here
        sender.addChatMessage(new TextComponentString("THIS IS A TEST"));
    }
}
