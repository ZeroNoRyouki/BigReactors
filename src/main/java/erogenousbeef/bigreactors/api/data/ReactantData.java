package erogenousbeef.bigreactors.api.data;

public class ReactantData {

	public enum ReactantType {
		Fuel,
		Waste,
	}

	public static final ReactantType[] TYPES = ReactantType.values();
	public static final int DEFAULT_FLUID_COLOR_FUEL = 0xbcba50;
	public static final int DEFAULT_FLUID_COLOR_WASTE = 0x4d92b5;

	private String name;
	private ReactantType type;
	private int color;
	
	public String getName() { return name; }
	public ReactantType getType() { return type; }
	public int getColor() { return color; }

	/**
	 * Declare a new set of reactant data with a default color.
	 * Waste will by Cyanite Cyan and fuel will be Yellorium Yellow.
	 * @param name The name of this reactant. Must be unique.
	 * @param type The type of this reactant. Fuel/Waste.
	 */
	public ReactantData(String name, ReactantType type) {
		this.name = name;
		this.type = type;
		this.color = type == ReactantType.Fuel ? DEFAULT_FLUID_COLOR_FUEL : DEFAULT_FLUID_COLOR_WASTE;
	}
	
	/**
	 * Declare a new set of reactant data.
	 * @param name The name of this reactant. Must be unique.
	 * @param type The type of this reactant. Fuel/Waste.
	 * @param color The color to use when rendering fuel rods with this reactant in it.
	 */
	public ReactantData(String name, ReactantType type, int color) {
		this.name = name;
		this.type = type;
		this.color = color;
	}
	
	/**
	 * @return True if this reactant is considered fuel.
	 */
	public boolean isFuel() {
		return type == ReactantType.Fuel;
	}
	
	/**
	 * @return True if this reactant is considered waste.
	 */
	public boolean isWaste() {
		return type == ReactantType.Waste;
	}
}
