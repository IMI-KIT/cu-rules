package edu.kit.imi.knoholem.cu.rules.dataquality;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class IndividualInClass {

    private final String className;
    private final String individualName;
    private final String query;

    public IndividualInClass(String className, String individualName, Templates templates) {
        this.className = className;
        this.individualName = individualName;
        this.query = initializeQuery(templates);
    }

    String initializeQuery(Templates templates) {
        // TODO Implement it.
        return null;
    }
}
