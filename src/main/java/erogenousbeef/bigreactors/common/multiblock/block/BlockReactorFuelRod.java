package erogenousbeef.bigreactors.common.multiblock.block;

import erogenousbeef.bigreactors.common.Properties;
import erogenousbeef.bigreactors.common.multiblock.PartType;
import erogenousbeef.bigreactors.common.multiblock.helpers.FuelAssembly;
import erogenousbeef.bigreactors.common.multiblock.tileentity.TileEntityReactorFuelRod;
import it.zerono.mods.zerocore.api.multiblock.MultiblockTileEntityBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReactorFuelRod extends BlockTieredPart {

	public BlockReactorFuelRod(String blockName) {

		super(PartType.ReactorFuelRod, blockName, Material.IRON);
		this.setLightLevel(0.9f);
		this.setLightOpacity(1);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityReactorFuelRod();
	}


	/*
	 * TODO Have to make my own particle for this. :/
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random par5Random)
    {
    	TileEntity te = world.getBlockTileEntity(x, y, z);
    	if(te instanceof TileEntityReactorFuelRod) {
    		TileEntityReactorFuelRod fuelRod = (TileEntityReactorFuelRod)te;
    		MultiblockReactor reactor = fuelRod.getReactorController();
    		if(reactor != null && reactor.isActive() && reactor.getFuelConsumedLastTick() > 0) {
    			int numParticles = par5Random.nextInt(4) + 1;
    			while(numParticles > 0) {
                    world.spawnParticle(BigReactors.VALENTINES_DAY ? "heart" : "crit",
                    		fuelRod.xCoord + 0.5D,
                    		fuelRod.yCoord + 0.5D,
                    		fuelRod.zCoord + 0.5D,
                    		par5Random.nextFloat() * 3f - 1.5f,
                    		par5Random.nextFloat() * 3f - 1.5f,
                    		par5Random.nextFloat() * 3f - 1.5f);
    				numParticles--;
    			}
    		}
    	}
    }
     **/

	@Override
	protected void buildBlockState(BlockStateContainer.Builder builder) {

		super.buildBlockState(builder);
		builder.add(Properties.FUELRODSTATE);
	}

	@Override
	protected IBlockState buildDefaultState(IBlockState state) {

		return super.buildDefaultState(state).withProperty(Properties.FUELRODSTATE, FuelRodState.Disassembled);
	}

	@Override
	protected IBlockState buildActualState(IBlockState state, IBlockAccess world, BlockPos position,
										   MultiblockTileEntityBase part) {

		state = super.buildActualState(state, world, position, part);

		if (part instanceof TileEntityReactorFuelRod) {

			boolean assembled = part.isConnected() && part.getMultiblockController().isAssembled();
			TileEntityReactorFuelRod fuelRod = (TileEntityReactorFuelRod)part;
			FuelAssembly assembly = fuelRod.getFuelAssembly();
			FuelRodState rodState = FuelRodState.Disassembled;

			if (assembled && null != assembly) {

				switch (assembly.getAxis()) {
					case X:
						rodState = FuelRodState.AssembledEW;
						break;

					case Y:
						rodState = FuelRodState.AssembledUD;
						break;

					case Z:
						rodState = FuelRodState.AssembledSN;
						break;
				}
			}

			state = state.withProperty(Properties.FUELRODSTATE, rodState);
		}

		return state;
	}
}
