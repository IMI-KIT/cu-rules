package edu.kit.imi.knoholem.cu.rules.dataquality;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Tonev</a>
 */
class Templates {

    public static Templates loadTemplates() {
        URL resourceURL = Templates.class.getResource("/templates/templates.stg");
        Path path;
        try {
            path = Paths.get(resourceURL.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return new Templates(new STGroupFile(path.toFile().getPath()));
    }

    private final STGroup parentGroup;

    Templates(STGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    ST getTemplate(String templateName) {
        return parentGroup.getInstanceOf(templateName);
    }
}
