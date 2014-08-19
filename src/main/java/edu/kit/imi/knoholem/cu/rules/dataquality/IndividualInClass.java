package edu.kit.imi.knoholem.cu.rules.dataquality;

import edu.kit.imi.knoholem.cu.rules.ontology.OntologyContext;
import org.stringtemplate.v4.ST;

import java.util.Collections;
import java.util.Set;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class IndividualInClass implements Criterion {

    private final Set<String> classNames;
    private final String individualName;
    private final Templates templates;

    public IndividualInClass(String className, String individualName, Templates templates) {
        this.classNames = Collections.singleton(className);
        this.individualName = individualName;
        this.templates = templates;
    }

    public IndividualInClass(Set<String> classNames, String individualName, Templates templates) {
        this.classNames = classNames;
        this.individualName = individualName;
        this.templates = templates;
    }

    public String getIndividualName() {
        return individualName;
    }

    public Set<String> getClassNames() {
        return Collections.unmodifiableSet(classNames);
    }

    @Override
    public String asAskQuery(OntologyContext ontologyContext) {
        ST individualInClassQuery = templates.getTemplate("individualInClasses");
        individualInClassQuery.add("prefix", "ontology");
        individualInClassQuery.add("namespace", ontologyContext.getOntologyNamespace());
        individualInClassQuery.add("classes", classNames);
        individualInClassQuery.add("individualName", individualName);

        return individualInClassQuery.render();
    }

}
