/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.model;

/**
 * @author Christopher Deckers
 */
public class Grammar {

  private Rule[] rules;

  public Grammar(Rule... rules) {
    this.rules = rules;
  }

  public Rule[] getRules() {
    return rules;
  }

  String toBNF(GrammarToBNF grammarToBNF) {
    StringBuilder sb = new StringBuilder();
    for(int i=0; i<rules.length; i++) {
      if(i > 0) {
        sb.append("\n");
      }
      sb.append(rules[i].toBNF(grammarToBNF));
    }
    return sb.toString();
  }

}
