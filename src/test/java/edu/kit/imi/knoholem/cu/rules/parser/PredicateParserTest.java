package edu.kit.imi.knoholem.cu.rules.parser;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.kit.imi.knoholem.cu.rules.parser.PredicateParser;

public class PredicateParserTest {

	@Test
	public void testGetLiteral() {
		assertEquals("RC0.13ATRMGRND", new PredicateParser("ZoneID= RC0.13ATRMGRND").getRightLiteral());
		assertEquals("0.00", new PredicateParser("2257<= 0.00").getRightLiteral());
		assertEquals("0.00", new PredicateParser("2241>= 0.00").getRightLiteral());
	}

	@Test
	public void testGetOperator() {
		assertEquals("=", new PredicateParser("ZoneID= RC0.13ATRMGRND").getOperator());
		assertEquals("<=", new PredicateParser("2257<= 0.00").getOperator());
		assertEquals(">=", new PredicateParser("2241>= 0.00").getOperator());
		assertEquals("<", new PredicateParser("2257< 0.00").getOperator());
		assertEquals(">", new PredicateParser("2241> 0.00").getOperator());
	}

	@Test
	public void testGetLeftLiteral() {
		assertEquals("Tot_Cool_Reduc", new PredicateParser("Tot_Cool_Reduc= 5.00%").getLeftLiteral());
		assertEquals("6", new PredicateParser("6>= 0.00").getLeftLiteral());
		assertEquals("Occupancy", new PredicateParser("Occupancy<= 20.50").getLeftLiteral());
	}

}
