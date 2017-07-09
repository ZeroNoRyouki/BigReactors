package erogenousbeef.bigreactors.gui.controls.grab;


import erogenousbeef.bigreactors.common.CircuitType;
import net.minecraft.util.ResourceLocation;

public class RedNetConfigGrabbable implements IBeefGuiGrabbable {

	protected String name;
	protected ResourceLocation icon;
	protected CircuitType circuitType;

	public RedNetConfigGrabbable(String name, ResourceLocation icon, CircuitType circuitType) {
		this.name = name;
		this.icon = icon;
		this.circuitType = circuitType;
	}
	
	@Override
	public ResourceLocation getIcon() {
		return icon;
	}
	
	public CircuitType getCircuitType() {
		return circuitType;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof RedNetConfigGrabbable) {
			return this.circuitType == ((RedNetConfigGrabbable)other).circuitType;
		}
		return false;
	}
	
	public String getName() {
		return name;
	}
}