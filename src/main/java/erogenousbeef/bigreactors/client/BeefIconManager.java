package erogenousbeef.bigreactors.client;

import com.google.common.collect.Maps;
import erogenousbeef.bigreactors.common.BigReactors;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

/**
 * Manages Icons that are not registered via blocks or items.
 * Useful for fluid icons and GUI icons.
 * 
 * @author Erogenous Beef
 * 
 */
public abstract class BeefIconManager {
	
	private HashMap<String, Integer> nameToIdMap;
	private HashMap<Integer, /*TextureAtlasSprite*/ResourceLocation> idToIconMap;
	
	public String[] iconNames;

	protected abstract String[] getIconNames();
	protected abstract String getPath();
	
	public BeefIconManager() {
		nameToIdMap = Maps.newHashMap();
        idToIconMap = Maps.newHashMap();
        iconNames = getIconNames();
	}
	
	public void registerIcons(TextureMap textureMap) {
		if(iconNames == null) { return; }

		ResourceLocation location;
		String path = this.getPath();

		for(int i = 0; i < iconNames.length; i++) {

			location = BigReactors.createResourceLocation(path + iconNames[i]);

			nameToIdMap.put(iconNames[i], i);
			//idToIconMap.put(i, textureMap.registerSprite(location));

			textureMap.registerSprite(location);
			idToIconMap.put(i, location);
		}
	}

	public ResourceLocation getIcon(String name) {
		if(name == null || name.isEmpty()) { return null; }
		
		Integer id = nameToIdMap.get(name);
		if(id == null) {
			return null;
		}
		
		return idToIconMap.get(id);
	}
	
	public ResourceLocation getIcon(int id) {
		return idToIconMap.get(id);
	}
}