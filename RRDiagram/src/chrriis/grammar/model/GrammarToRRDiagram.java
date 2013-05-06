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
public class GrammarToRRDiagram {

  public static interface RuleLinkProvider {
    public String getLink(String ruleName);
  }

  private RuleLinkProvider ruleLinkProvider = new RuleLinkProvider() {
    @Override
    public String getLink(String ruleName) {
      return "#" + ruleName;
    }
  };

  public void setRuleLinkProvider(RuleLinkProvider ruleLinkProvider) {
    this.ruleLinkProvider = ruleLinkProvider;
  }

  public RuleLinkProvider getRuleLinkProvider() {
    return ruleLinkProvider;
  }

  private String ruleConsideredAsLinebreak;

  public void setRuleConsideredAsLineBreak(String ruleConsideredAsLinebreak) {
    this.ruleConsideredAsLinebreak = ruleConsideredAsLinebreak;
  }

  public String getRuleConsideredAsLinebreak() {
    return ruleConsideredAsLinebreak;
  }

  public RRDiagram convert(Rule rule) {
    return rule.toRRDiagram(this);
  }

}
