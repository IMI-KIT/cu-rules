package edu.kit.imi.knoholem.cu.rules.dataquality;

import edu.kit.imi.knoholem.cu.rules.rulesconversion.SWRLRule;
import edu.kit.imi.knoholem.cu.rules.swrlentities.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class RuleQueryTest {

    private SWRLRule rule;

    @Before
    public void setup() {
        List<Atom> antecedent = new LinkedList<Atom>();
        List<Atom> consequent = new LinkedList<Atom>();

        antecedent.add(new ClassAtom("A", new Individual("a1")));
        antecedent.add(new ClassAtom("A", new Individual("a2")));
        antecedent.add(new ClassAtom("X", new Unknown("x")));
        antecedent.add(new PropertyAtom("hasProperty", new Individual("a3"), new Individual("a4")));

        consequent.add(new ClassAtom("A", new Individual("a5")));
        consequent.add(new PropertyAtom("hasProperty", new Unknown("x"), new Value("xxx")));

        rule = new SWRLRule(antecedent, consequent);
    }

    @Test
    public void testGetIndividuals() {
        Set<Individual> expecteds = new HashSet<Individual>();
        expecteds.add(new Individual("a1"));
        expecteds.add(new Individual("a2"));
        expecteds.add(new Individual("a3"));
        expecteds.add(new Individual("a4"));
        expecteds.add(new Individual("a5"));

        Assert.assertEquals(expecteds, new RuleQuery().getIndividuals(rule));
    }

    @Test
    public void testGetClassAtoms() {
        Set<ClassAtom> expecteds = new HashSet<ClassAtom>();
        expecteds.add(new ClassAtom("A", new Individual("a1")));
        expecteds.add(new ClassAtom("A", new Individual("a2")));
        expecteds.add(new ClassAtom("A", new Individual("a5")));
        expecteds.add(new ClassAtom("X", new Unknown("x")));

        Assert.assertEquals(expecteds, new RuleQuery().getClassAtoms(rule));
    }

    @Test
    public void testGetClassAtomsWithIndividuals() {
        Set<ClassAtom> expecteds = new HashSet<ClassAtom>();
        expecteds.add(new ClassAtom("A", new Individual("a1")));
        expecteds.add(new ClassAtom("A", new Individual("a2")));
        expecteds.add(new ClassAtom("A", new Individual("a5")));

        Assert.assertEquals(expecteds, new RuleQuery().getClassAtomsWithIndividuals(rule));
    }
}