package erogenousbeef.bigreactors.client;

import java.util.HashMap;
import net.minecraft.client.renderer.texture.TextureMap;
import com.google.common.collect.Maps;
import erogenousbeef.bigreactors.common.BigReactors;

/**
 * Manages Icons that are not registered via blocks or items.
 * Useful for fluid icons and GUI icons.
 * 
 * @author Erogenous Beef
 * 
 */
public abstract class BeefIconManager {

	public static final int TERRAIN_TEXTURE = 0;
	public static final int ITEM_TEXTURE = 1;
	
	private HashMap<String, Integer> nameToIdMap;
	// TODO Commented out IIcon stuff
	//private HashMap<Integer, IIcon> idToIconMap;
	
	public String[] iconNames = null;

	protected abstract String[] getIconNames();
	protected abstract String getPath();
	
	public BeefIconManager() {
		nameToIdMap = Maps.newHashMap();
		// TODO Commented out IIcon stuff
        //idToIconMap = Maps.newHashMap();
        iconNames = getIconNames();
	}
	
	public void registerIcons(TextureMap textureMap) {
		if(iconNames == null) { return; }

		for(int i = 0; i < iconNames.length; i++) {
			nameToIdMap.put(iconNames[i], i);
			// TODO Commented out IIcon stuff
			//idToIconMap.put(i, textureMap.registerIcon(BigReactors.TEXTURE_NAME_PREFIX + getPath() + iconNames[i]));
		}
	}

	// TODO Commented out IIcon stuff
	/*
	public IIcon getIcon(String name) {
		if(name == null || name.isEmpty()) { return null; }
		
		Integer id = nameToIdMap.get(name);
		if(id == null) {
			return null;
		}
		
		return idToIconMap.get(id);
	}
	
	public IIcon getIcon(int id) {
		return idToIconMap.get(id);
	}
	*/
	@Deprecated // TODO switch to blockstate / ResourceLocation
	public Object getIcon(String unused) {
		return null;
	}
	@Deprecated // TODO switch to blockstate / ResourceLocation
	public Object getIcon(int unused) {
		return null;
	}

	public int getTextureType() { return TERRAIN_TEXTURE; }
	
}