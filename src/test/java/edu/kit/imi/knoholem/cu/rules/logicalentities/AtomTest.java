package edu.kit.imi.knoholem.cu.rules.logicalentities;

import static org.junit.Assert.*;

import org.junit.Test;

public class AtomTest {

	@Test
	public void testClassAtomFalseEquality() {
		assertFalse(new ClassAtom("Omnivore", new Unknown("a")).equals(
				new ClassAtom("Omnivore", new Unknown("b"))));
		assertFalse(new ClassAtom("Carnivore", new Unknown("a")).equals(
				new ClassAtom("Omnivore", new Unknown("a"))));
	}

	@Test
	public void testClassAtomEquality() {
		assertTrue(new ClassAtom("Carnivore", new Individual("trex")).equals(
				new ClassAtom("Carnivore", new Individual("trex"))));
	}

}
