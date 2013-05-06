/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.model;

import chrriis.grammar.model.GrammarToBNF.LiteralDefinitionSign;
import chrriis.grammar.rrdiagram.RRElement;
import chrriis.grammar.rrdiagram.RRText;
import chrriis.grammar.rrdiagram.RRText.Type;

/**
 * @author Christopher Deckers
 */
public class Literal extends Expression {

  private String text;

  public Literal(String text) {
    this.text = text;
  }

  @Override
  protected RRElement toRRElement(GrammarToRRDiagram grammarToRRDiagram) {
    return new RRText(Type.LITERAL, text, null);
  }

  @Override
  protected void toBNF(GrammarToBNF grammarToBNF, StringBuilder sb, boolean isNested) {
    char c = grammarToBNF.getLiteralDefinitionSign() == LiteralDefinitionSign.DOUBLE_QUOTE? '"': '\'';
    sb.append(c);
    sb.append(text);
    sb.append(c);
  }

}
