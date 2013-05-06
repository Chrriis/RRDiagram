/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.model;

import chrriis.grammar.rrdiagram.RRDiagram;

/**
 * @author Christopher Deckers
 */
public class Rule {

  public String name;
  private Expression expression;

  public Rule(String name, Expression expression) {
    this.name = name;
    this.expression = expression;
  }

  public String getName() {
    return name;
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

}
