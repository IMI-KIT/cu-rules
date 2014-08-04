package edu.kit.imi.knoholem.cu.rules.dataquality;

import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import org.stringtemplate.v4.ST;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class DeclaredIndividual implements Criterion {

    private final String individualName;
    private final Templates templates;

    public DeclaredIndividual(String individualName, Templates templates) {
        this.individualName = individualName;
        this.templates = templates;
    }

    public String getIndividualName() {
        return individualName;
    }

    @Override
    public String asAskQuery(OntologyContext context) {
        ST queryTemplate = templates.getTemplate("declared_individual");
        queryTemplate.add("prefix", "ontology");
        queryTemplate.add("namespace", context.getOntologyNamespace());
        queryTemplate.add("individualName", individualName);

        return queryTemplate.render();
    }

    @Override
    public String toString() {
        return "DeclaredIndividual{" +
                "individualName='" + individualName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeclaredIndividual that = (DeclaredIndividual) o;

        if (!individualName.equals(that.individualName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return individualName.hashCode();
    }
}
