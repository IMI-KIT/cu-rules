package edu.kit.imi.knoholem.cu.rules.dataquality;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
public class Templates {

    public static Templates loadTemplates() {
        return new Templates(new STGroupFile("templates/templates.stg"));
    }

    private final STGroup parentGroup;

    Templates(STGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    ST getTemplate(String templateName) {
        return parentGroup.getInstanceOf(templateName);
    }
}
