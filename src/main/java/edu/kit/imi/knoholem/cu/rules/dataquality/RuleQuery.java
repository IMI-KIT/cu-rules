package edu.kit.imi.knoholem.cu.rules.dataquality;

import edu.kit.imi.knoholem.cu.rules.rulesconversion.SWRLRule;
import edu.kit.imi.knoholem.cu.rules.swrlentities.Atom;
import edu.kit.imi.knoholem.cu.rules.swrlentities.ClassAtom;
import edu.kit.imi.knoholem.cu.rules.swrlentities.Individual;
import edu.kit.imi.knoholem.cu.rules.swrlentities.PropertyAtom;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class RuleQuery {

    public Set<Individual> getIndividuals(SWRLRule rule) {
        Set<Individual> result = new HashSet<Individual>();

        for (ClassAtom classAtom : getIndividualClassAtoms(rule)) {
            result.add((Individual) classAtom.getOperand());
        }

        result.addAll(extractIndividualReferencesFromProperties(rule.getAntecedent()));
        result.addAll(extractIndividualReferencesFromProperties(rule.getConsequent()));

        return Collections.unmodifiableSet(result);
    }

    public Set<Individual> getIndividualsInConsequent(SWRLRule rule) {
        Set<Individual> result = new HashSet<Individual>();

        for (ClassAtom classAtom : getIndividualClassAtomsInConsequent(rule)) {
            result.add((Individual) classAtom.getOperand());
        }

        result.addAll(extractIndividualReferencesFromProperties(rule.getConsequent()));

        return Collections.unmodifiableSet(result);
    }

    public Set<ClassAtom> getClassAtoms(SWRLRule swrlRule) {
        Set<ClassAtom> result = new HashSet<ClassAtom>();

        result.addAll(getClassAtomsInAntecedent(swrlRule));
        result.addAll(getClassAtomsInConsequent(swrlRule));

        return Collections.unmodifiableSet(result);
    }

    public Set<ClassAtom> getClassAtomsWithIndividuals(SWRLRule rule) {
        Set<ClassAtom> result = new HashSet<ClassAtom>();

        for (ClassAtom atom : getClassAtoms(rule)) {
            if (atom.getOperand() instanceof Individual) {
                result.add(atom);
            }
        }

        return Collections.unmodifiableSet(result);
    }

    public Set<PropertyAtom> getQualifiedPropertyAtomsInAntecedent(String propertyName, SWRLRule rule) {
        Set<PropertyAtom> result = new HashSet<PropertyAtom>();

        for (PropertyAtom atom : extractPropertyAtoms(rule.getAntecedent())) {
            if (atom.getAtomName().equals(propertyName)) {
                result.add(atom);
            }
        }

        return result;
    }

    Set<ClassAtom> getIndividualClassAtoms(SWRLRule swrlRule) {
        Set<ClassAtom> result = new HashSet<ClassAtom>();

        result.addAll(getIndividualClassAtomsInAntecedent(swrlRule));
        result.addAll(getIndividualClassAtomsInConsequent(swrlRule));

        return Collections.unmodifiableSet(result);
    }

    Set<ClassAtom> getIndividualClassAtomsInAntecedent(SWRLRule swrlRule) {
        return Collections.unmodifiableSet(extractIndividualClassAtoms(extractClassAtoms(swrlRule.getAntecedent())));
    }

    Set<ClassAtom> getIndividualClassAtomsInConsequent(SWRLRule swrlRule) {
        return Collections.unmodifiableSet(extractIndividualClassAtoms(extractClassAtoms(swrlRule.getConsequent())));
    }

    Set<ClassAtom> getClassAtomsInAntecedent(SWRLRule swrlRule) {
        return Collections.unmodifiableSet(extractClassAtoms(swrlRule.getAntecedent()));
    }

    Set<ClassAtom> getClassAtomsInConsequent(SWRLRule swrlRule) {
        return Collections.unmodifiableSet(extractClassAtoms(swrlRule.getConsequent()));
    }

    private Set<ClassAtom> extractIndividualClassAtoms(Collection<? extends ClassAtom> atoms) {
        Set<ClassAtom> result = new HashSet<ClassAtom>();

        for (ClassAtom classAtom : atoms) {
            if (classAtom.getOperand() instanceof Individual) {
                result.add(classAtom);
            }
        }

        return result;
    }

    private Set<ClassAtom> extractClassAtoms(Collection<? extends Atom> atomsList) {
        Set<ClassAtom> result = new HashSet<ClassAtom>();

        for (Atom atom : atomsList) {
            if (atom instanceof ClassAtom) {
                result.add((ClassAtom) atom);
            }
        }

        return result;
    }

    private Set<Individual> extractIndividualReferencesFromProperties(Collection<? extends Atom> atoms) {
        Set<Individual> result = new HashSet<Individual>();
        Set<PropertyAtom> propertyAtoms = extractPropertyAtoms(atoms);

        for (PropertyAtom atom : extractPropertyAtomsWithIndividuals(propertyAtoms)) {
            result.addAll(extractIndividualsInPropertyAtom(atom));
        }

        return result;
    }

    private Set<PropertyAtom> extractPropertyAtoms(Collection<? extends Atom> atoms) {
        Set<PropertyAtom> result = new HashSet<PropertyAtom>();

        for (Atom atom : atoms) {
            if (atom.getClass() == PropertyAtom.class) {
                result.add((PropertyAtom) atom);
            }
        }

        return result;
    }

    private Set<PropertyAtom> extractPropertyAtomsWithIndividuals(Collection<PropertyAtom> atoms) {
        Set<PropertyAtom> result = new HashSet<PropertyAtom>();

        for (PropertyAtom atom : atoms) {
            if (atom.getLeftOperand() instanceof Individual || atom.getRightOperand() instanceof Individual) {
                result.add(atom);
            }
        }

        return result;
    }

    private Set<Individual> extractIndividualsInPropertyAtom(PropertyAtom atom) {
        Set<Individual> result = new HashSet<Individual>();

        if (atom.getLeftOperand() instanceof Individual) {
            result.add((Individual) atom.getLeftOperand());
        }

        if (atom.getRightOperand() instanceof Individual) {
            result.add((Individual) atom.getRightOperand());
        }

        return result;
    }
}
