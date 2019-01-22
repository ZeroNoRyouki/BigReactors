package erogenousbeef.bigreactors.common.multiblock.block;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import erogenousbeef.bigreactors.common.compat.IdReference;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorComputerPort;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityTurbineComputerPort;
import it.zerono.mods.zerocore.lib.compat.computer.Connector;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@Optional.InterfaceList({
        @Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = IdReference.MODID_COMPUTERCRAFT)
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
    public void onRegisterRecipes(@Nonnull IForgeRegistry<IRecipe> registry) {
        //TODO fix recipe!
        /*
        if (!CompatManager.isModLoaded(IdReference.MODID_COMPUTERCRAFT) &&
                !CompatManager.isModLoaded(IdReference.MODID_OPENCOMPUTERS))
            return;

        if (PartType.ReactorComputerPort == this._type) {

            if (PartTier.REACTOR_TIERS.contains(PartTier.Legacy))
                RecipeHelper.addShapedOreDictRecipe(this.createItemStack(PartTier.Legacy, 1), "CRC", "GPG", "CRC",
                    'C', BrBlocks.reactorCasing.createItemStack(PartTier.Legacy, 1), 'R', Items.REDSTONE,
                    'G', "ingotGold", 'P', Items.REPEATER);

            if (PartTier.REACTOR_TIERS.contains(PartTier.Basic))
                RecipeHelper.addShapedOreDictRecipe(this.createItemStack(PartTier.Basic, 1), "CRC", "GPG", "CRC",
                    'C', BrBlocks.reactorCasing.createItemStack(PartTier.Basic, 1), 'R', Items.REDSTONE,
                    'G', "ingotGold", 'P', Items.REPEATER);

        } else if (PartType.TurbineComputerPort == this._type) {

            if (PartTier.TURBINE_TIERS.contains(PartTier.Legacy))
                RecipeHelper.addShapedOreDictRecipe(this.createItemStack(PartTier.Legacy, 1), "HRH", "GPG", "HRH",
                    'H', BrBlocks.turbineHousing.createItemStack(PartTier.Legacy, 1), 'R', Items.REDSTONE,
                    'G', "ingotGold", 'P', Items.REPEATER);

            if (PartTier.TURBINE_TIERS.contains(PartTier.Basic))
                RecipeHelper.addShapedOreDictRecipe(this.createItemStack(PartTier.Basic, 1), "HRH", "GPG", "HRH",
                    'H', BrBlocks.turbineHousing.createItemStack(PartTier.Basic, 1), 'R', Items.REDSTONE,
                    'G', "ingotGold", 'P', Items.REPEATER);
        }
        */
    }

    @Optional.Method(modid = IdReference.MODID_COMPUTERCRAFT)
    @Override
    public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {

        final TileEntity tileEntity = WorldHelper.getTile(world, pos);
        Connector computer = null;

        if (tileEntity instanceof TileEntityReactorComputerPort) {
            computer = ((TileEntityReactorComputerPort) tileEntity).getComputerCraftPeripheral();
        } else if (tileEntity instanceof TileEntityTurbineComputerPort) {
            computer = ((TileEntityTurbineComputerPort) tileEntity).getComputerCraftPeripheral();
        }

        return computer instanceof IPeripheral ? (IPeripheral)computer : null;
    }
}