package erogenousbeef.bigreactors.common.data;

import erogenousbeef.bigreactors.api.data.ReactantData;
import erogenousbeef.bigreactors.api.data.SourceProductMapping;
import erogenousbeef.bigreactors.api.registry.Reactants;
import erogenousbeef.bigreactors.common.BigReactors;

public class StandardReactants {

	public static final String yellorium = "yellorium";
	public static final String cyanite = "cyanite";
	public static final String blutonium = "blutonium";

	// These are used as fallbacks
	public static SourceProductMapping yelloriumMapping;
	public static SourceProductMapping cyaniteMapping;
	
	public static void register() {
		Reactants.registerReactant(yellorium, 0, ReactantData.DEFAULT_FLUID_COLOR_FUEL);
		Reactants.registerReactant(cyanite, 1, ReactantData.DEFAULT_FLUID_COLOR_WASTE);
		Reactants.registerReactant(blutonium, 0, ReactantData.DEFAULT_FLUID_COLOR_FUEL);
	}
	
}
