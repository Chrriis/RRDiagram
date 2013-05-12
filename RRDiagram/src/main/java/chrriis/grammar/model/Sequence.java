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

import chrriis.grammar.rrdiagram.RRElement;
import chrriis.grammar.rrdiagram.RRLoop;
import chrriis.grammar.rrdiagram.RRSequence;

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
      // Treat special case of: a (',' a)*
      if(i < expressions.length - 1 && expression instanceof RuleReference && expressions[i + 1] instanceof Repetition) {
        RuleReference ruleLink = (RuleReference)expression;
        Repetition repetition = (Repetition)expressions[i + 1];
        Expression reptitionExpression = repetition.getExpression();
        if(reptitionExpression instanceof Sequence) {
          Expression[] subExpressions = ((Sequence)reptitionExpression).getExpressions();
          if(subExpressions.length == 2 && subExpressions[0] instanceof Literal && subExpressions[1] instanceof RuleReference && ((RuleReference)subExpressions[1]).getRuleName().equals(ruleLink.getRuleName())) {
            Integer maxRepetitionCount = repetition.getMaxRepetitionCount();
            if(maxRepetitionCount == null || maxRepetitionCount > 1) {
              rrElement = new RRLoop(ruleLink.toRRElement(grammarToRRDiagram), subExpressions[0].toRRElement(grammarToRRDiagram), repetition.getMinRepetitionCount(), (maxRepetitionCount == null? null: maxRepetitionCount));
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

}
