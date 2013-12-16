package edu.kit.imi.knoholem.cu.rulesconversion;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.kit.imi.knoholem.cu.rules.logicalentities.Unknown;

class Unknowns {

	private static final Map<Character,Character> characterMap;
	static {
		Map<Character, Character> initializationMap = new HashMap<Character, Character>();
		initializationMap.put('0', 'a');
		initializationMap.put('1', 'b');
		initializationMap.put('2', 'c');
		initializationMap.put('3', 'd');
		initializationMap.put('4', 'e');
		initializationMap.put('5', 'f');
		initializationMap.put('6', 'g');
		initializationMap.put('7', 'h');
		initializationMap.put('8', 'i');
		initializationMap.put('9', 'j');
		initializationMap.put('a', 'k');
		initializationMap.put('b', 'l');
		initializationMap.put('c', 'm');
		initializationMap.put('d', 'n');
		initializationMap.put('e', 'o');
		initializationMap.put('f', 'p');
		initializationMap.put('g', 'q');
		initializationMap.put('h', 'r');
		initializationMap.put('i', 's');
		initializationMap.put('j', 't');
		initializationMap.put('k', 'u');
		initializationMap.put('l', 'v');
		initializationMap.put('m', 'w');
		initializationMap.put('n', 'x');
		initializationMap.put('o', 'y');
		initializationMap.put('p', 'z');
		characterMap = Collections.unmodifiableMap(initializationMap);
	}

	public static Unknown getUnknown(int number) {
		String base26 = Integer.toString(number, 26);
		return getUnknown(base26);
	}

	private static Unknown getUnknown(String base26) {
		StringBuilder name = new StringBuilder();
		int i = base26.length() - 1;
		for (; i > 0; i--) {
			char character = base26.charAt(i);
			name.append(characterMap.get(character));
		}
		if (base26.length() > 1) {
			int numerical = Integer.parseInt(String.valueOf(base26.charAt(0)), 26);
			name.append(characterMap.get(Integer.toString(numerical - 1, 26).charAt(0)));
		} else {
			name.append(characterMap.get(base26.charAt(0)));
		}
		return new Unknown(name.reverse().toString());
	}

	private int counter;

	public Unknowns() {
		this.counter = 0;
	}

	Unknown nextUnknown() {
		Unknown unknown = getUnknown(counter);
		counter = counter + 1;
		return unknown;
	}

}
