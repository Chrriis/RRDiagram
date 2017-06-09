/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.model;

import chrriis.grammar.model.GrammarToRRDiagram.RuleLinkProvider;
import chrriis.grammar.rrdiagram.RRBreak;
import chrriis.grammar.rrdiagram.RRElement;
import chrriis.grammar.rrdiagram.RRText;
import chrriis.grammar.rrdiagram.RRText.Type;

/**
 * @author Christopher Deckers
 */
public class RuleReference extends Expression {

  private String ruleName;

  public RuleReference(String ruleName) {
    this.ruleName = ruleName;
  }

  public String getRuleName() {
    return ruleName;
  }

  @Override
  protected RRElement toRRElement(GrammarToRRDiagram grammarToRRDiagram) {
    String ruleConsideredAsLinebreak = grammarToRRDiagram.getRuleConsideredAsLinebreak();
    if(ruleConsideredAsLinebreak != null && ruleConsideredAsLinebreak.equals(ruleName)) {
      return new RRBreak();
    }
    RuleLinkProvider ruleLinkProvider = grammarToRRDiagram.getRuleLinkProvider();
    return new RRText(Type.RULE, ruleName, ruleLinkProvider == null? null: ruleLinkProvider.getLink(ruleName));
  }

  @Override
  protected void toBNF(GrammarToBNF grammarToBNF, StringBuilder sb, boolean isNested) {
    sb.append(ruleName);
    String ruleConsideredAsLinebreak = grammarToBNF.getRuleConsideredAsLinebreak();
    if(ruleConsideredAsLinebreak != null && ruleConsideredAsLinebreak.equals(ruleName)) {
      sb.append("\n");
    }
  }

}
