package vazkii.ambience;

import java.util.HashMap;
import java.util.Map;

import javax.naming.ReferralException;

import com.google.common.reflect.Reflection;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent.BossInfo;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.common.MinecraftForge;

public final class SongPicker {

	public static final String EVENT_MAIN_MENU = "mainMenu";
	public static final String EVENT_BOSS = "boss";
	public static final String EVENT_IN_NETHER = "nether";
	public static final String EVENT_IN_END = "end";
	public static final String EVENT_HORDE = "horde";
	public static final String EVENT_NIGHT = "night";
	public static final String EVENT_RAIN = "rain";
	public static final String EVENT_UNDERWATER = "underwater";
	public static final String EVENT_UNDERGROUND = "underground";
	public static final String EVENT_DEEP_UNDEGROUND = "deepUnderground";
	public static final String EVENT_HIGH_UP = "highUp";
	public static final String EVENT_VILLAGE = "village";
	public static final String EVENT_MINECART = "minecart";
	public static final String EVENT_BOAT = "boat";
	public static final String EVENT_HORSE = "horse";
	public static final String EVENT_PIG = "pig";
	public static final String EVENT_FISHING = "fishing";
	public static final String EVENT_DYING = "dying";
	public static final String EVENT_PUMPKIN_HEAD = "pumpkinHead";
	public static final String EVENT_GENERIC = "generic";
	
	public static final Map<String, String> eventMap = new HashMap();
	public static final Map<Biome, String> biomeMap = new HashMap();
	public static final Map<BiomeDictionary.Type, String> primaryTagMap = new HashMap();
	public static final Map<BiomeDictionary.Type, String> secondaryTagMap = new HashMap();
	
	public static void reset() {
		eventMap.clear();
		biomeMap.clear();
		primaryTagMap.clear();
		secondaryTagMap.clear();
	}
	
	public static String getSong() {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.thePlayer;
		World world = mc.theWorld;

		if(player == null || world == null)
			return getSongForEvent(EVENT_MAIN_MENU);
		
		BlockPos pos = new BlockPos(player);

		AmbienceEventEvent event = new AmbienceEventEvent.Pre(world, pos);
		MinecraftForge.EVENT_BUS.post(event);
    	String eventr = getSongForEvent(event.event);
    	if(eventr != null)
    		return eventr;
		
    	GuiBossOverlay bossOverlay = mc.ingameGUI.getBossOverlay();
    	Map map = ReflectionHelper.getPrivateValue(GuiBossOverlay.class, bossOverlay, Ambience.OBF_MAP_BOSS_INFOS);
        if(!map.isEmpty()) {
        	String song = getSongForEvent(EVENT_BOSS);
        	if(song != null)
        		return song;
        }
        
        float hp = player.getHealth();
        if(hp < 7) {
        	String song = getSongForEvent(EVENT_DYING);
        	if(song != null)
        		return song;
        }

	        int monsterCount = world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(player.posX - 16, player.posY - 8, player.posZ - 16, player.posX + 16, player.posY + 8, player.posZ + 16)).size();
		if(monsterCount > 5) {
        	String song = getSongForEvent(EVENT_HORDE);
        	if(song != null)
        		return song;
		}
        
        if(player.fishEntity != null) {
        	String song = getSongForEvent(EVENT_FISHING);
        	if(song != null)
        		return song;
        }
        
        ItemStack headItem = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if(headItem != null && headItem.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN)) {
        	String song = getSongForEvent(EVENT_PUMPKIN_HEAD);
        	if(song != null)
        		return song;
        }
        	int indimension = world.provider.getDimension();

		if(indimension == -1) {
		String song = getSongForEvent(EVENT_IN_NETHER);
	        	if(song != null)
	        	return song;
		} else if(indimension == 1) {
			String song = getSongForEvent(EVENT_IN_END);
	        	if(song != null)
	        	return song;
		}

		Entity riding = player.getRidingEntity();
		if(riding != null) {
			if(riding instanceof EntityMinecart) {
	        	String song = getSongForEvent(EVENT_MINECART);
	        	if(song != null)
	        		return song;
	        } 
			if(riding instanceof EntityBoat) {
	        	String song = getSongForEvent(EVENT_BOAT);
	        	if(song != null)
	        		return song;
	        } 
			if(riding instanceof EntityHorse) {
	        	String song = getSongForEvent(EVENT_HORSE);
	        	if(song != null)
	        		return song;
	        } 
			if(riding instanceof EntityPig) {
	        	String song = getSongForEvent(EVENT_PIG);
	        	if(song != null)
	        		return song;
	        }
		}
		
		if(player.isInsideOfMaterial(Material.WATER)) {
        	String song = getSongForEvent(EVENT_UNDERWATER);
        	if(song != null)
        		return song;
		}
		
		boolean underground = !world.canSeeSky(pos);
		
		if(underground) {
			if(pos.getY() < 20) {
	        	String song = getSongForEvent(EVENT_DEEP_UNDEGROUND);
	        	if(song != null)
	        		return song;
	        }
			if(pos.getY() < 55) {
	        	String song = getSongForEvent(EVENT_UNDERGROUND);
	        	if(song != null)
	        		return song;
	        }
		} else if(world.isRaining()) {
        	String song = getSongForEvent(EVENT_RAIN);
        	if(song != null)
        		return song;
		}
		
		if(pos.getY() > 128) {
        	String song = getSongForEvent(EVENT_HIGH_UP);
        	if(song != null)
        		return song;
        }
		
		long time = world.getWorldTime() % 24000;
		if(time > 13300 && time < 23200) {
        	String song = getSongForEvent(EVENT_NIGHT);
        	if(song != null)
        		return song;
        }
		
		int villagerCount = world.getEntitiesWithinAABB(EntityVillager.class, new AxisAlignedBB(player.posX - 30, player.posY - 8, player.posZ - 30, player.posX + 30, player.posY + 8, player.posZ + 30)).size();
		if(villagerCount > 3) {
        	String song = getSongForEvent(EVENT_VILLAGE);
        	if(song != null)
        		return song;
		}


		
		event = new AmbienceEventEvent.Post(world, pos);
		MinecraftForge.EVENT_BUS.post(event);
    	eventr = getSongForEvent(event.event);
    	if(eventr != null)
    		return eventr;
		
        if(world != null) {
            Chunk chunk = world.getChunkFromBlockCoords(pos);
            Biome biome = chunk.getBiome(pos, world.getBiomeProvider());
            if(biomeMap.containsKey(biome))
            	return biomeMap.get(biome);
            
            BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);
            for(Type t : types)
            	if(primaryTagMap.containsKey(t))
            		return primaryTagMap.get(t);
            for(Type t : types)
            	if(secondaryTagMap.containsKey(t))
            		return secondaryTagMap.get(t);
        }
        
        return getSongForEvent(EVENT_GENERIC);
	}
	
	public static String getSongForEvent(String event) {
		if(eventMap.containsKey(event))
			return eventMap.get(event);
		
		return null;
	}
	
	public static void getSongForBiome(World world, int x, int y, int z) {
		
	}
	
	public static String getSongName(String song) {
		return song == null ? "" : song.replaceAll("([^A-Z])([A-Z])", "$1 $2");
	}
	
}
