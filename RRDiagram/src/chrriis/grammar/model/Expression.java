/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.model;

import chrriis.grammar.rrdiagram.RRElement;

/**
 * @author Christopher Deckers
 */
public abstract class Expression {

  protected abstract RRElement toRRElement(GrammarToRRDiagram grammarToRRDiagram);

  protected abstract void toBNF(GrammarToBNF grammarToBNF, StringBuilder sb, boolean isNested);

}
