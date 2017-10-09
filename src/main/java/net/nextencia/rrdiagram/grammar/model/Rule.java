/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package net.nextencia.rrdiagram.grammar.model;

import net.nextencia.rrdiagram.grammar.rrdiagram.RRDiagram;

/**
 * @author Christopher Deckers
 */
public class Rule {

  private String name;
  private Expression expression;
  private String originalExpressionText;

  public Rule(String name, Expression expression) {
    this(name, expression, null);
  }

  public Rule(String name, Expression expression, String originalExpressionText) {
    this.name = name;
    this.expression = expression;
    this.originalExpressionText = originalExpressionText;
  }

  public String getName() {
    return name;
  }

  public String getOriginalExpressionText() {
    return originalExpressionText;
  }

  RRDiagram toRRDiagram(GrammarToRRDiagram grammarToRRDiagram) {
    return new RRDiagram(expression.toRRElement(grammarToRRDiagram));
  }

  String toBNF(GrammarToBNF grammarToBNF) {
    StringBuilder sb = new StringBuilder();
    sb.append(name);
    sb.append(" ");
    switch(grammarToBNF.getRuleDefinitionSign()) {
      case EQUAL: sb.append("="); break;
      case COLON_EQUAL: sb.append(":="); break;
      case COLON_COLON_EQUAL: sb.append("::="); break;
    }
    sb.append(" ");
    expression.toBNF(grammarToBNF, sb, false);
    sb.append(";");
    return sb.toString();
  }

  @Override
  public String toString() {
    return toBNF(new GrammarToBNF());
  }
}
