package edu.kit.imi.knoholem.cu.rules.parser.processing;

import edu.kit.imi.knoholem.cu.rules.parser.RuleParseError;

import java.io.File;

/**
 * @author <a href="mailto:kiril.tonev@kit.edu">Kiril Tonev</a>
 */
class RuleFileParserError extends RuleParseError {

    RuleFileParserError(File file, RuleParseError cause) {
        super(cause.getRuleLiteral(), file.getPath() + ": " + cause.getMessage());
    }
}
