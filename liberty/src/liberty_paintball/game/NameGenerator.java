package liberty_paintball.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Dave Waddling
 * 
 * Generates the various names which are given to players for their in-game names. Instantiation is expensive
 * as that is when the list is populated and there is no method for freeing a name if a player leaves the game.
 * Also this currently limits the number of players to be 26 * 8 * 8 = 1664.
 *
 */
public class NameGenerator {

	private Random random;

	// @formatter:off
	private static final String[] POSITIVE_ADJECTIVES = {
		"Adept",
		"Brave",
		"Clever",
		"Dashing",
		"Erudite",
		"Feisty",
		"Grand",
		"Heroic",
		"Incredible",
		"Jazzy",
		"Keen",
		"Lively",
		"Masterful",
		"Nimble",
		"Outstanding",
		"Proud",
		"Quaint",
		"Refined",
		"Speedy",
		"Trendy",
		"Upstanding",
		"Vigilant",
		"Wise",
		"Xenial",
		"Youthful",
		"Zany"
	};

	// The hexadecimal RGB color values are used on the app so that a player's color
	// maps to their icons.
	private static final Map<String,String> MAP_COLOR_NAME_TO_HEX;
	static {
		Map<String, String> tempMap = new HashMap<String,String>();
		tempMap.put("White","#FFFFFF");
		tempMap.put("Black","#000000");
		tempMap.put("Red","#FF0000");
		tempMap.put("Orange","#FFA500");
		tempMap.put("Yellow","#FFFF00");
		tempMap.put("Green","#008000");
		tempMap.put("Blue","#0000FF");
		tempMap.put("Indigo","#4B0082");
		tempMap.put("Violet","#EE82EE");
		MAP_COLOR_NAME_TO_HEX = Collections.unmodifiableMap(tempMap);
	}
	
	private static final String[] BRITISH_FOREST_ANIMALS = {
		"Badger",
		"Rabbit",
		"Owl",
		"Fox",
		"Weasel",
		"Deer",
		"Squirrel",
		"Hedgehog"
	};
	// @formatter:on

	private List<String>						names;
	private Map<String, String>					mapNameToHexColor;


	public NameGenerator() {
		int entryCount = POSITIVE_ADJECTIVES.length * MAP_COLOR_NAME_TO_HEX.size() * BRITISH_FOREST_ANIMALS.length;
		mapNameToHexColor = new HashMap<String, String>(entryCount);
		names = new ArrayList<String>(entryCount);

		for (String adjective : POSITIVE_ADJECTIVES) {
			for (Map.Entry<String, String> entryColorNameToHex : MAP_COLOR_NAME_TO_HEX.entrySet()) {
				for (String animal : BRITISH_FOREST_ANIMALS) {
					String name = adjective + entryColorNameToHex.getKey() + animal;
					names.add(name);
					mapNameToHexColor.put(name, entryColorNameToHex.getValue());
				}
			}
		}

		random = new Random();
	}


	// TODO Cope with running out of names.
	public synchronized PlayerIdentity createRandomUniqueIdentity(String username) {
		int randomIndex = random.nextInt(mapNameToHexColor.size());

		String name = names.remove(randomIndex);
		String hexColor = mapNameToHexColor.remove(name);

		return new PlayerIdentity(username, name, hexColor);
	}

}
