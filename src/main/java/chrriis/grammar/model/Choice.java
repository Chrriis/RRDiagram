/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.model;

import java.util.ArrayList;
import java.util.List;

import chrriis.grammar.rrdiagram.RRChoice;
import chrriis.grammar.rrdiagram.RRElement;

/**
 * @author Christopher Deckers
 */
public class Choice extends Expression {

  private Expression[] expressions;

  public Choice(Expression... expressions) {
    this.expressions = expressions;
  }

  public Expression[] getExpressions() {
    return expressions;
  }

  @Override
  protected RRElement toRRElement(GrammarToRRDiagram grammarToRRDiagram) {
    RRElement[] rrElements = new RRElement[expressions.length];
    for(int i=0; i<rrElements.length; i++) {
      rrElements[i] = expressions[i].toRRElement(grammarToRRDiagram);
    }
    return new RRChoice(rrElements);
  }

  @Override
  protected void toBNF(GrammarToBNF grammarToBNF, StringBuilder sb, boolean isNested) {
    List<Expression> expressionList = new ArrayList<Expression>();
    boolean hasNoop = false;
    for(Expression expression: expressions) {
      if(expression instanceof Sequence && ((Sequence)expression).getExpressions().length == 0) {
        hasNoop = true;
      } else {
        expressionList.add(expression);
      }
    }
    if(expressionList.isEmpty()) {
      sb.append("( )");
    } else if(hasNoop && expressionList.size() == 1) {
      boolean isUsingMultiplicationTokens = grammarToBNF.isUsingMultiplicationTokens();
      if(!isUsingMultiplicationTokens) {
        sb.append("[ ");
      }
      expressionList.get(0).toBNF(grammarToBNF, sb, isUsingMultiplicationTokens);
      if(!isUsingMultiplicationTokens) {
        sb.append(" ]");
      }
    } else {
      boolean isUsingMultiplicationTokens = grammarToBNF.isUsingMultiplicationTokens();
      if(hasNoop && !isUsingMultiplicationTokens) {
        sb.append("[ ");
      } else if(hasNoop || isNested && expressionList.size() > 1) {
        sb.append("( ");
      }
      int count = expressionList.size();
      for(int i=0; i<count; i++) {
        if(i > 0) {
          sb.append(" | ");
        }
        expressionList.get(i).toBNF(grammarToBNF, sb, false);
      }
      if(hasNoop && !isUsingMultiplicationTokens) {
        sb.append(" ]");
      } else if(hasNoop || isNested && expressionList.size() > 1) {
        sb.append(" )");
        if(hasNoop) {
          sb.append("?");
        }
      }
    }
  }

}
