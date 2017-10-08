/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package net.nextencia.rrdiagram.grammar.model;

import net.nextencia.rrdiagram.grammar.rrdiagram.RRChoice;
import net.nextencia.rrdiagram.grammar.rrdiagram.RRElement;
import net.nextencia.rrdiagram.grammar.rrdiagram.RRLine;
import net.nextencia.rrdiagram.grammar.rrdiagram.RRLoop;

/**
 * @author Christopher Deckers
 */
public class Repetition extends Expression {

  private Expression expression;
  private int minRepetitionCount;
  private Integer maxRepetitionCount;

  public Repetition(Expression expression, int minRepetitionCount, Integer maxRepetitionCount) {
    this.expression = expression;
    this.minRepetitionCount = minRepetitionCount;
    this.maxRepetitionCount = maxRepetitionCount;
  }

  public Expression getExpression() {
    return expression;
  }

  public int getMinRepetitionCount() {
    return minRepetitionCount;
  }

  public Integer getMaxRepetitionCount() {
    return maxRepetitionCount;
  }

  @Override
  protected RRElement toRRElement(GrammarToRRDiagram grammarToRRDiagram) {
    RRElement rrElement = expression.toRRElement(grammarToRRDiagram);
    if(minRepetitionCount == 0) {
      if(maxRepetitionCount == null || maxRepetitionCount > 1) {
        return new RRChoice(new RRLoop(rrElement, null, 0, (maxRepetitionCount == null? null: maxRepetitionCount - 1)), new RRLine());
      }
      return new RRChoice(rrElement, new RRLine());
    }
    return new RRLoop(rrElement, null, minRepetitionCount - 1, (maxRepetitionCount == null? null: maxRepetitionCount - 1));
  }

  @Override
  protected void toBNF(GrammarToBNF grammarToBNF, StringBuilder sb, boolean isNested) {
    boolean isUsingMultiplicationTokens = grammarToBNF.isUsingMultiplicationTokens();
    if(maxRepetitionCount == null) {
      if(minRepetitionCount > 0) {
        if(minRepetitionCount == 1 && isUsingMultiplicationTokens) {
          expression.toBNF(grammarToBNF, sb, true);
          sb.append("+");
        } else {
          if(isNested) {
            sb.append("( ");
          }
          if(minRepetitionCount > 1) {
            sb.append(minRepetitionCount);
            sb.append(" * ");
          }
          expression.toBNF(grammarToBNF, sb, false);
          if(grammarToBNF.isCommaSeparator()) {
            sb.append(" ,");
          }
          sb.append(" ");
          sb.append("{ ");
          expression.toBNF(grammarToBNF, sb, false);
          sb.append(" }");
          if(isNested) {
            sb.append(" )");
          }
        }
      } else {
        if(isUsingMultiplicationTokens) {
          expression.toBNF(grammarToBNF, sb, true);
          sb.append("*");
        } else {
          sb.append("{ ");
          expression.toBNF(grammarToBNF, sb, false);
          sb.append(" }");
        }
      }
    } else {
      if(minRepetitionCount == 0) {
        if(maxRepetitionCount == 1 && isUsingMultiplicationTokens) {
          expression.toBNF(grammarToBNF, sb, true);
          sb.append("?");
        } else {
          if(maxRepetitionCount > 1) {
            sb.append(maxRepetitionCount);
            sb.append(" * ");
          }
          sb.append("[ ");
          expression.toBNF(grammarToBNF, sb, false);
          sb.append(" ]");
        }
      } else {
        if(minRepetitionCount == maxRepetitionCount) {
          sb.append(minRepetitionCount);
          sb.append(" * ");
          expression.toBNF(grammarToBNF, sb, isNested);
        } else {
          if(isNested) {
            sb.append("( ");
          }
          sb.append(minRepetitionCount);
          sb.append(" * ");
          expression.toBNF(grammarToBNF, sb, false);
          if(grammarToBNF.isCommaSeparator()) {
            sb.append(" ,");
          }
          sb.append(" ");
          sb.append(maxRepetitionCount - minRepetitionCount);
          sb.append(" * ");
          sb.append("[ ");
          expression.toBNF(grammarToBNF, sb, false);
          sb.append(" ]");
          if(isNested) {
            sb.append(" )");
          }
        }
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if(!(o instanceof Repetition)) {
      return false;
    }
    Repetition exp2 = (Repetition)o;
    return expression.equals(exp2.expression) && minRepetitionCount == exp2.minRepetitionCount && maxRepetitionCount == null? exp2.maxRepetitionCount == null: maxRepetitionCount.equals(exp2.maxRepetitionCount);
  }

}
