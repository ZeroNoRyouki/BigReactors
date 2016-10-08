package erogenousbeef.bigreactors.common;

import erogenousbeef.bigreactors.client.ClientProxy;
import erogenousbeef.bigreactors.gui.BeefGuiIconManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum CircuitType {

    DISABLED(0),
    inputActive(-1), 				// Input: reactor on/off
    inputSetControlRod(-1), 		// Input: control rod insertion (0-100)
    inputEjectWaste(-1),			// Input: eject waste from the reactor

    outputFuelTemperature(+1),		// Output: Temperature of the reactor fuel
    outputCasingTemperature(+1),	// Output: Temperature of the reactor casing
    outputFuelMix(+1), 		        // Output: Fuel mix, % of contents that is fuel (0-100, 100 = 100% fuel)
    outputFuelAmount(+1), 	        // Output: Fuel amount in a control rod, raw value, (0-4*height)
    outputWasteAmount(+1), 	        // Output: Waste amount in a control rod, raw value, (0-4*height)
    outputEnergyAmount(+1);         // Output: Energy in the reactor's buffer, percentile (0-100, 100 = 100% full)

    public static final CircuitType[] TYPES = CircuitType.values();

    CircuitType(int flowDirection) {
        this._flow = (byte)flowDirection;
    }

    public boolean isInput() {
        return this._flow < 0;
    }

    public boolean isOutput() {
        return this._flow > 0;
    }

    public String getTooltip() {

        return s_grabbableTooltips[this.ordinal() - 1];
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getIcon() {

        if (DISABLED == this)
            return TextureMap.LOCATION_MISSING_TEXTURE;

        return ClientProxy.GuiIcons.getIcon(BeefGuiIconManager.REDNET_FIRST + this.ordinal() - 1);
    }

    public static boolean hasCoordinate(CircuitType circuitType) {
        return circuitType == CircuitType.inputSetControlRod;
    }

    public static boolean canBeToggledBetweenPulseAndNormal(CircuitType circuitType) {
        return circuitType == CircuitType.inputActive;
    }

    private final byte _flow;

    // TODO i18l
    private static final String[] s_grabbableTooltips = {
            "Input: Toggle reactor on/off",
            "Input: Change control rod insertion",
            "Input: Eject Waste",
            "Output: Fuel Temp (C)",
            "Output: Casing Temp (C)",
            "Output: Fuel mix (% fuel, 0-100)",
            "Output: Fuel amount",
            "Output: Waste amount",
            "Output: Energy amount (%)"
    };
}
