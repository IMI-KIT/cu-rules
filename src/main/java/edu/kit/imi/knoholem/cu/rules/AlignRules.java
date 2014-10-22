package edu.kit.imi.knoholem.cu.rules;

import edu.kit.imi.knoholem.cu.rules.atoms.Predicate;
import edu.kit.imi.knoholem.cu.rules.atoms.SensitivityAnalysisRule;
import edu.kit.imi.knoholem.cu.rules.functions.Collect;
import edu.kit.imi.knoholem.cu.rules.functions.Monad;
import edu.kit.imi.knoholem.cu.rules.parser.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
public class AlignRules {

    public static void main(String[] args) {
        String url = args[0];
        String user = args[1];
        String password = args[2];

        String sensorsTable = args[3];
        String setpointsTable = args[4];

        String sensorColumn = args[5];
        String setpointColumn = args[6];

        List<String> files = Arrays.asList(args).subList(7, args.length);
        RuleParserConfiguration ruleParserConfiguration = RuleParserConfiguration.getDefaultConfiguration();
        SensorsDatabase alignRules = new SensorsDatabase(url, user, password, sensorsTable, setpointsTable, sensorColumn, setpointColumn);

        Connection connection = null;
        try {
            connection = alignRules.initializeConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection == null) {
                System.exit(1);
            }
        }

        Set<String> sensorsInDatabase = null;
        try {
            sensorsInDatabase = alignRules.fetchAllNames(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.err.println("Fetched " + sensorsInDatabase.size() + " sensors.");
        AlignedRuleParser ruleParser = new AlignedRuleParser(ruleParserConfiguration, sensorsInDatabase);
        try {
            Collect collector = new ConvertRules.MultipleRuleFileParser(files, ruleParser).execute();
            Monad<String> alignedRules = collector.getRules().map(new RulePrinter(ruleParserConfiguration, new DecimalFormat("0.00")));
            for (String name : alignedRules) {
                System.out.println(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static class AlignedRuleParser extends RuleParser {

        private final Collection<String> sensorsInDatabase;

        private AlignedRuleParser(RuleParserConfiguration configuration, Collection<String> sensorsInDatabase) {
            super(configuration);
            this.sensorsInDatabase = sensorsInDatabase;
        }

        public SensitivityAnalysisRule parseRule(String ruleLiteral) {
            try {
                RuleLiteral parsedLiteral = new RuleLiteral(ruleLiteral, configuration);

                SensitivityAnalysisRule rule = new SensitivityAnalysisRule();
                rule.setAntecedent(collectPredicates(parsedLiteral.getAntecedentAtoms()));
                rule.setConsequent(super.collectPredicates(parsedLiteral.getConsequentAtoms()));
                rule.setMetadata(parseRuleMetadata(parsedLiteral));

                return rule;
            } catch (Throwable t) {
                throw new RuleParseError(ruleLiteral, t);
            }
        }

        @Override
        protected List<Predicate> collectPredicates(List<String> ruleBodyTokens) {
            List<Predicate> predicates = new LinkedList<Predicate>();
            for (String token : ruleBodyTokens) {
                Predicate predicate = parsePredicate(token);
                if (sensorsInDatabase.contains(predicate.getLeftOperand().asString())) {
                    predicates.add(predicate);
                }
            }
            return predicates;
        }
    }
}
