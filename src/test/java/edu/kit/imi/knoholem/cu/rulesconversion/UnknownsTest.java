package edu.kit.imi.knoholem.cu.rulesconversion;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.kit.imi.knoholem.cu.rules.logicalentities.Unknown;

public class UnknownsTest {

	@Test
	public void testInitialUnknown() {
		assertEquals(new Unknown("a"), new Unknowns().nextUnknown());
	}

	@Test
	public void testNextUnknown() {
		Unknowns unknowns = new Unknowns();
		assertEquals(new Unknown("a"), unknowns.nextUnknown());
		assertEquals(new Unknown("b"), unknowns.nextUnknown());
		assertEquals(new Unknown("c"), unknowns.nextUnknown());
	}

	@Test
	public void testMultiplePlaces() {
		assertEquals(new Unknown("z"), Unknowns.getUnknown(25));
		assertEquals(new Unknown("aa"), Unknowns.getUnknown(26));
		assertEquals(new Unknown("ab"), Unknowns.getUnknown(27));
		assertEquals(new Unknown("ac"), Unknowns.getUnknown(28));
		assertEquals(new Unknown("ba"), Unknowns.getUnknown(52));
		assertEquals(new Unknown("aaa"), Unknowns.getUnknown(676));
		assertEquals(new Unknown("aab"), Unknowns.getUnknown(677));
	}

}
