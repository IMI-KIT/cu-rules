package edu.kit.imi.knoholem.cu.rules.parser;

import edu.kit.imi.knoholem.cu.rules.History;
import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.atoms.processing.PredicateMap;
import edu.kit.imi.knoholem.cu.rules.atoms.processing.PredicateMapEntry;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InspectingParser extends AlignedRuleParser {

    private final History history;
    private final Map<String, DescriptiveStatistics> sensorStats;
    private final Map<String, Integer> warnings;

    public InspectingParser(RuleParserConfiguration configuration, Collection<String> sensorsInDatabase, History history) {
        super(configuration, sensorsInDatabase);
        this.history = history;
        this.sensorStats = new HashMap<String, DescriptiveStatistics>();
        this.warnings = new HashMap<String, Integer>();
    }

    public Map<String, Integer> getWarnings() {
        return warnings;
    }

    @Override
    protected List<Predicate> collectPredicates(List<String> ruleBodyTokens) {
        List<Predicate> predicates = super.collectPredicates(ruleBodyTokens);
        for (PredicateMapEntry mapEntry : new PredicateMap(predicates).byLeftOperand()) {
            String sensor = mapEntry.getClassifier().asString();
            if (raiseWarning(mapEntry, getSensorStatistics(sensor))) {
                incrementWarning(sensor);
            }
        }
        return predicates;
    }

    protected void incrementWarning(String sensorName) {
        if (warnings.containsKey(sensorName)) {
            warnings.put(sensorName, warnings.get(sensorName) + 1);
        } else {
            warnings.put(sensorName, 1);
        }
    }

    protected DescriptiveStatistics getSensorStatistics(String sensorName) {
        DescriptiveStatistics statistics;
        if (sensorStats.containsKey(sensorName)) {
            statistics = sensorStats.get(sensorName);
        } else {
            try {
                System.err.println("Fetching statistics for `" + sensorName + "'.");
                statistics = history.sensorStats(sensorName);
                sensorStats.put(sensorName, statistics);
            } catch (SQLException e) {
                throw new RuntimeException("Could not fetch statistics for `" + sensorName + "'", e);
            }
        }
        return statistics;
    }

    protected boolean raiseWarning(PredicateMapEntry predicateMapEntry, DescriptiveStatistics statistics) {
        for (Predicate predicate : predicateMapEntry.getPredicates()) {
            double predicateValue = predicate.getRightOperand().asDouble();
            return predicateValue < statistics.getPercentile(25) || predicateValue > statistics.getPercentile(75);
        }
        return false;
    }
}
