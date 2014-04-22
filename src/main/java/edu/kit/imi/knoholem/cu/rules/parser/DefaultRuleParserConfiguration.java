package edu.kit.imi.knoholem.cu.rules.parser;

class DefaultRuleParserConfiguration extends RuleParserConfiguration {

    @Override
    public int zoneIdIndex() {
        return 0;
    }

    @Override
	public int weightIndex() {
		return 1;
	}

	@Override
	public int typeIndex() {
		return 2;
	}

	@Override
	public int reductionIndex() {
		return 3;
	}

	@Override
	public int monthIndex() {
		return 4;
	}

	@Override
	public int dayIndex() {
		return 5;
	}

	@Override
	public int hourIndex() {
		return 6;
	}

	@Override
	public String andLiteral() {
		return "\\^";
	}

	@Override
	public String ifLiteral() {
		return "IF";
	}

	@Override
	public String thenLiteral() {
		return "THEN";
	}

}
