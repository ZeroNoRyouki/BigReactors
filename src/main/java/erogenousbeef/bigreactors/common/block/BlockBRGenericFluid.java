package erogenousbeef.bigreactors.common.block;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import erogenousbeef.bigreactors.common.BigReactors;

public class BlockBRGenericFluid extends BlockFluidClassic {

	// TODO blockstate
	/*
	private IIcon _iconFlowing;
	private IIcon _iconStill;
	*/

	public BlockBRGenericFluid(Fluid fluid, String unlocalizedName) {
		super(fluid, Material.water);

		//setRegistryName(unlocalizedName);
		setUnlocalizedName("fluid." + unlocalizedName + ".still");
	}

	// TODO blockstate
	/*
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegistry) {
		_iconStill   = iconRegistry.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName());
		_iconFlowing = iconRegistry.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getUnlocalizedName().replace(".still", ".flowing"));

		this.stack.getFluid().setIcons(_iconStill, _iconFlowing);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata) {
		return side <= 1 ? _iconStill : _iconFlowing;
	}
	*/
}
