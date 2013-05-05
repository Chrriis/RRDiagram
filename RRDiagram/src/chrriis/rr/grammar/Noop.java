/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.rr.grammar;

import chrriis.rr.diagram.RRElement;
import chrriis.rr.diagram.RRLine;

public class Noop extends Expression {

  @Override
  protected RRElement toRRElement(GrammarToRRDiagram grammarToRRDiagram) {
    return new RRLine();
  }

  @Override
  protected void toBNF(GrammarToBNF grammarToBNF, StringBuilder sb, boolean isNested) {
    sb.append("( )");
  }

}
