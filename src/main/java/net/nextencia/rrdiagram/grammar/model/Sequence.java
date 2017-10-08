/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package net.nextencia.rrdiagram.grammar.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.nextencia.rrdiagram.grammar.rrdiagram.RRElement;
import net.nextencia.rrdiagram.grammar.rrdiagram.RRLoop;
import net.nextencia.rrdiagram.grammar.rrdiagram.RRSequence;

/**
 * @author Christopher Deckers
 */
public class Sequence extends Expression {

  private Expression[] expressions;

  public Sequence(Expression... expressions) {
    this.expressions = expressions;
  }

  public Expression[] getExpressions() {
    return expressions;
  }

  @Override
  protected RRElement toRRElement(GrammarToRRDiagram grammarToRRDiagram) {
    List<RRElement> rrElementList = new ArrayList<RRElement>();
    for(int i=0; i<expressions.length; i++) {
      Expression expression = expressions[i];
      RRElement rrElement = expression.toRRElement(grammarToRRDiagram);
      // Treat special case of: "a (',' a)*" and "a (a)*"
      if(i < expressions.length - 1 && expressions[i + 1] instanceof Repetition) {
        Repetition repetition = (Repetition)expressions[i + 1];
        Expression repetitionExpression = repetition.getExpression();
        if(repetitionExpression instanceof Sequence) {
          // Treat special case of: "expr (',' expr)*"
          Expression[] subExpressions = ((Sequence)repetitionExpression).getExpressions();
          if(subExpressions.length == 2 && subExpressions[0] instanceof Literal) {
            if(expression.equals(subExpressions[1])) {
              Integer maxRepetitionCount = repetition.getMaxRepetitionCount();
              if(maxRepetitionCount == null || maxRepetitionCount > 1) {
                rrElement = new RRLoop(expression.toRRElement(grammarToRRDiagram), subExpressions[0].toRRElement(grammarToRRDiagram), repetition.getMinRepetitionCount(), (maxRepetitionCount == null? null: maxRepetitionCount));
                i++;
              }
            }
          }
        } else if(expression instanceof RuleReference) {
          RuleReference ruleLink = (RuleReference)expression;
          // Treat special case of: a (a)*
          if(repetitionExpression instanceof RuleReference && ((RuleReference)repetitionExpression).getRuleName().equals(ruleLink.getRuleName())) {
            Integer maxRepetitionCount = repetition.getMaxRepetitionCount();
            if(maxRepetitionCount == null || maxRepetitionCount > 1) {
              rrElement = new RRLoop(ruleLink.toRRElement(grammarToRRDiagram), null, repetition.getMinRepetitionCount(), (maxRepetitionCount == null? null: maxRepetitionCount));
              i++;
            }
          }
        }
      }
      rrElementList.add(rrElement);
    }
    return new RRSequence(rrElementList.toArray(new RRElement[0]));
  }

  @Override
  protected void toBNF(GrammarToBNF grammarToBNF, StringBuilder sb, boolean isNested) {
    if(expressions.length == 0) {
      sb.append("( )");
      return;
    }
    if(isNested && expressions.length > 1) {
      sb.append("( ");
    }
    boolean isCommaSeparator = grammarToBNF.isCommaSeparator();
    for(int i=0; i<expressions.length; i++) {
      if(i > 0) {
        if(isCommaSeparator) {
          sb.append(" ,");
        }
        sb.append(" ");
      }
      expressions[i].toBNF(grammarToBNF, sb, expressions.length == 1 && isNested || !isCommaSeparator);
    }
    if(isNested && expressions.length > 1) {
      sb.append(" )");
    }
  }

  @Override
  public boolean equals(Object o) {
    if(!(o instanceof Sequence)) {
      return false;
    }
    return Arrays.equals(expressions, ((Sequence)o).expressions);
  }

}
