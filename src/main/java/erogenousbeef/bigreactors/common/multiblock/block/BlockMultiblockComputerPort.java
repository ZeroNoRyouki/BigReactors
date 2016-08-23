package erogenousbeef.bigreactors.common.multiblock.block;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import erogenousbeef.bigreactors.common.multiblock.PartTier;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorComputerPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineComputerPort;
import erogenousbeef.bigreactors.init.BrBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Optional.InterfaceList({
        @Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = "ComputerCraft")
})
public class BlockMultiblockComputerPort extends BlockMultiblockDevice implements IPeripheralProvider {

    public BlockMultiblockComputerPort(PartType type, String blockName) {
        super(type, blockName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {

        switch (this._type) {

            case ReactorComputerPort:
                return new TileEntityReactorComputerPort();

            case TurbineComputerPort:
                return new TileEntityTurbineComputerPort();

            default:
                throw new IllegalArgumentException("Invalid part type");
        }
    }

    @Override
    public void registerRecipes() {

        if (!Loader.isModLoaded("ComputerCraft") && !Loader.isModLoaded("OpenComputers"))
            return;

        if (PartType.ReactorComputerPort == this._type) {

            if (PartTier.REACTOR_TIERS.contains(PartTier.Legacy))
                GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Legacy, 1), "CRC", "GPG", "CRC",
                    'C', BrBlocks.reactorCasing.createItemStack(PartTier.Legacy, 1), 'R', Items.REDSTONE,
                    'G', "ingotGold", 'P', Items.REPEATER));

            if (PartTier.REACTOR_TIERS.contains(PartTier.Basic))
                GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Basic, 1), "CRC", "GPG", "CRC",
                    'C', BrBlocks.reactorCasing.createItemStack(PartTier.Basic, 1), 'R', Items.REDSTONE,
                    'G', "ingotGold", 'P', Items.REPEATER));

        } else if (PartType.TurbineComputerPort == this._type) {

            if (PartTier.TURBINE_TIERS.contains(PartTier.Legacy))
                GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Legacy, 1), "HRH", "GPG", "HRH",
                    'H', BrBlocks.turbineHousing.createItemStack(PartTier.Legacy, 1), 'R', Items.REDSTONE,
                    'G', "ingotGold", 'P', Items.REPEATER));

            if (PartTier.TURBINE_TIERS.contains(PartTier.Basic))
                GameRegistry.addRecipe(new ShapedOreRecipe(this.createItemStack(PartTier.Basic, 1), "HRH", "GPG", "HRH",
                    'H', BrBlocks.turbineHousing.createItemStack(PartTier.Basic, 1), 'R', Items.REDSTONE,
                    'G', "ingotGold", 'P', Items.REPEATER));
        }
    }

    @Optional.Method(modid ="ComputerCraft")
    @Override
    public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {

        TileEntity tileEntity = world.getTileEntity(pos);

        return tileEntity instanceof IPeripheral ? (IPeripheral)tileEntity : null;
    }
}
