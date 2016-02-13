# CU-RULES

A library for processing sensitivity analysis rules produced by Cardiff
University within the project [KnoholEM][1].

# Installation

The library builds with maven. Ensure that you have a maven installation,
e.g issuing:

```
mvn -v
```

If this fails, follow the installation instructions on the [maven
website][2].

A jar package can be produced via:

```
mvn package
```

It will be placed under the `target` subdirectory. You can add the jar in your
classpath manually, or alternatively, if you use maven, install it with:

```
mvn install
```

# Using

The rule parser needs a configuration in order to work. You can make yours
by implementing `RuleParserConfiguration` or use the default one:

```java
RuleParser parser = new RuleParser(RuleParserConfiguration.getDefaultConfiguration());
```

Once you've got an sensitivity analysis rule string conforming with the
configuration, you can parse it like this:

```java
SensitivityAnalysisRule rule = parser.parseRule(ruleLiteral);
```

A `SensitivityAnalysisRule` can be converted to an
[SWRL](https://www.w3.org/Submission/SWRL/) rule via the `SWRLConverter`
interface. Similarly to the `RuleParser` it needs a configuration instance:

```java
SWRLConverter converter = new SWRLConverter(SWRLConverterConfiguration.getDefaultConfiguration());
SWRLRule swrlRule = converter.convertRule(rule);
```

Before converting to the [SWRL](https://www.w3.org/Submission/SWRL/)
representation, you should make sure that all referenced entities could be
found in the target ontology. Otherwise the entities would be written in the
ontology without producing any error while breaking the consistency of the
ABox.

The `ontology` package provides an interface to a reference ontology which
implements the [OWL API](http://owlapi.sourceforge.net/). Use the
implementations there to perform the necessary filtering of the rule atoms with
the KnoHolEM ontology or write your own when adopting a different
specification.

Once the rules are converted to the SWRL representation, they can be written
in an ontology via the `ontology.rulesprocessing.RuleExporter` mapper. An
example implementation could be found in the `ExportRules` class.

# Running

The jar package has a main entry point which allows you to convert the
sensitivity analysis rules in a set of files and print the resulting SWRL
representations on the standard output.

```
java -jar target/cu-rules-0.1.0-SNAPSHOT.jar /path/to/rules...
```

[1]: http://knoholem.eu "KnoholEM"
[2]: http://maven.apache.org/ "Apache Maven"
