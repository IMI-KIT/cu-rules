package edu.kit.imi.knoholem.cu.rules.dataquality;

import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import org.stringtemplate.v4.ST;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class IndividualInClass implements Criterion {

    private final String className;
    private final String individualName;
    private final Templates templates;

    public IndividualInClass(String className, String individualName, Templates templates) {
        this.className = className;
        this.individualName = individualName;
        this.templates = templates;
    }

    public String getIndividualName() {
        return individualName;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String asAskQuery(OntologyContext ontologyContext) {
        ST individualInClassQuery = templates.getTemplate("individualInClass");
        individualInClassQuery.add("prefix", "ontology");
        individualInClassQuery.add("namespace", ontologyContext.getOntologyNamespace());
        individualInClassQuery.add("className", className);
        individualInClassQuery.add("individualName", individualName);

        return individualInClassQuery.render();
    }

    @Override
    public String toString() {
        return "IndividualInClass{" +
                "className='" + className + '\'' +
                ", individualName='" + individualName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndividualInClass that = (IndividualInClass) o;

        if (!className.equals(that.className)) return false;
        if (!individualName.equals(that.individualName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + individualName.hashCode();
        return result;
    }
}
