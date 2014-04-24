package edu.kit.imi.knoholem.cu.rules.atoms.processing;

import edu.kit.imi.knoholem.cu.rules.atoms.Literal;
import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;

import java.util.*;

public class PredicateMap {

    private final List<Predicate> predicateList;

    public PredicateMap(Collection<? extends Predicate> inputPredicates) {
        predicateList = new ArrayList<Predicate>(inputPredicates.size());
        predicateList.addAll(inputPredicates);
    }

    public List<PredicateMapEntry> byLeftOperand() {
        return (new Mapper() {
            @Override
            public Literal getOperand(Predicate predicate) {
                return predicate.getLeftOperand();
            }
        }).map();
    }

    public List<PredicateMapEntry> byRightOperand() {
        return (new Mapper() {
            @Override
            public Literal getOperand(Predicate predicate) {
                return predicate.getRightOperand();
            }
        }).map();
    }

    private abstract class Mapper {

        public abstract Literal getOperand(Predicate predicate);

        public List<PredicateMapEntry> map() {
            Map<Literal, List<Predicate>> predicateMap = new LinkedHashMap<Literal, List<Predicate>>();

            for (Predicate predicate : predicateList) {
                if (predicateMap.containsKey(getOperand(predicate))) {
                    predicateMap.get(getOperand(predicate)).add(predicate);
                } else {
                    List<Predicate> predicateList = new ArrayList<Predicate>();
                    predicateList.add(predicate);
                    predicateMap.put(getOperand(predicate), predicateList);
                }
            }

            return flattenMap(predicateMap);
        }

        private List<PredicateMapEntry> flattenMap(Map<Literal, List<Predicate>> predicateMap) {
            List<PredicateMapEntry> entries = new ArrayList<PredicateMapEntry>(predicateMap.keySet().size());

            for (Literal classifier : predicateMap.keySet()) {
                entries.add(new PredicateMapEntry(classifier, predicateMap.get(classifier)));
            }

            return entries;
        }
    }

}
