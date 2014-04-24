package edu.kit.imi.knoholem.cu.rules.swrlentities;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
