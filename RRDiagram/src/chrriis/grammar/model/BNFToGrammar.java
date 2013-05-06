/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.model;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christopher Deckers
 */
public class BNFToGrammar {

  private enum ChunkType {
    RULE,
    REPETITION_TOKEN,
//    CONCATENATION,
    ALTERNATION,
    GROUP,
    COMMENT,
    SPECIAL_SEQUENCE,
    LITERAL,
    OPTION,
    REPETITION,
    CHOICE,
  }

  private static class Chunk {
    private ChunkType type;
    public Chunk(ChunkType type) {
      this(type, null);
    }
    public Chunk(ChunkType type, String text) {
      this.type = type;
      this.text = text;
    }
    public ChunkType getType() {
      return type;
    }
    public void setType(ChunkType type) {
      this.type = type;
    }
    private String text;
    public void setText(String text) {
      this.text = text;
    }
    private int minCount;
    public void setMinCount(int minCount) {
      this.minCount = minCount;
    }
    private Integer maxCount;
    public void setMaxCount(Integer maxCount) {
      this.maxCount = maxCount;
    }
    private List<Chunk> chunkList;
    public void addChunk(Chunk chunk) {
      if(chunkList == null) {
        chunkList = new ArrayList<Chunk>();
      }
      chunkList.add(chunk);
    }
    private void prune() {
      boolean hasAlternation = false;
      for(int i=chunkList.size()-1; i>=0; i--) {
        Chunk chunk = chunkList.get(i);
        switch(chunk.getType()) {
          case REPETITION_TOKEN: {
            if("*".equals(chunk.text)) {
              chunkList.remove(i);
              Chunk previousChunk = chunkList.get(i - 1);
              Integer multiplier = null;
              // Case of: 3 * expression
              if(previousChunk.getType() == ChunkType.RULE) {
                try {
                  multiplier = Integer.parseInt(previousChunk.text);
                } catch(Exception e) {
                }
              }
              if(multiplier != null) {
                // The current one is removed, so next one is at index i.
                Chunk nextChunk = chunkList.get(i);
                if(nextChunk.getType() == ChunkType.OPTION) {
                  Chunk newChunk = new Chunk(ChunkType.REPETITION);
                  newChunk.setMinCount(0);
                  newChunk.setMaxCount(multiplier);
                  for(Chunk c: nextChunk.chunkList) {
                    newChunk.addChunk(c);
                  }
                  chunkList.remove(i);
                  chunkList.set(i - 1, newChunk);
                } else {
                  Chunk newChunk = new Chunk(ChunkType.REPETITION);
                  newChunk.setMinCount(multiplier);
                  newChunk.setMaxCount(multiplier);
                  newChunk.addChunk(nextChunk);
                  chunkList.remove(i);
                  chunkList.set(i - 1, newChunk);
                }
              } else {
                Chunk newChunk = new Chunk(ChunkType.REPETITION);
                newChunk.setMinCount(0);
                newChunk.addChunk(previousChunk);
                chunkList.set(i - 1, newChunk);
              }
            } else if("+".equals(chunk.text)) {
              chunkList.remove(i);
              Chunk newChunk = new Chunk(ChunkType.REPETITION);
              newChunk.setMinCount(1);
              Chunk previousChunk = chunkList.get(i - 1);
              newChunk.addChunk(previousChunk);
              chunkList.set(i - 1, newChunk);
            } else if("?".equals(chunk.text)) {
              chunkList.remove(i);
              Chunk newChunk = new Chunk(ChunkType.OPTION);
              Chunk previousChunk = chunkList.get(i - 1);
              newChunk.addChunk(previousChunk);
              chunkList.set(i - 1, newChunk);
            }
            break;
          }
          case COMMENT: {
            // For now, nothing to do
            chunkList.remove(i);
          }
          case ALTERNATION: {
            hasAlternation = true;
            break;
          }
          case GROUP: {
            // Group could be empty
            if(chunk.chunkList != null) {
              chunk.prune();
              if(chunk.chunkList.size() == 1) {
                chunkList.set(i, chunk.chunkList.get(0));
              }
            }
            break;
          }
          case OPTION:
          case REPETITION: {
            chunk.prune();
            break;
          }
        }
      }
      if(hasAlternation) {
        List<List<Chunk>> alternationSequenceList = new ArrayList<List<Chunk>>();
        alternationSequenceList.add(new ArrayList<Chunk>());
        for(Chunk chunk: chunkList) {
          if(chunk.getType() == ChunkType.ALTERNATION) {
            alternationSequenceList.add(new ArrayList<Chunk>());
          } else {
            List<Chunk> list = alternationSequenceList.get(alternationSequenceList.size() - 1);
            list.add(chunk);
          }
        }
        Chunk choiceChunk = new Chunk(ChunkType.CHOICE);
        for(List<Chunk> subList: alternationSequenceList) {
          if(subList.size() == 1) {
            choiceChunk.addChunk(subList.get(0));
          } else {
            Chunk groupChunk = new Chunk(ChunkType.GROUP);
            for(Chunk c: subList) {
              groupChunk.addChunk(c);
            }
            choiceChunk.addChunk(groupChunk);
          }
        }
        chunkList.clear();
        chunkList.add(choiceChunk);
      }
    }
    private Expression getExpression() {
      switch(type) {
        case GROUP: {
          if(chunkList == null) {
            // Group is empty.
            return new Sequence();
          }
          if(chunkList.size() == 1) {
            return chunkList.get(0).getExpression();
          }
          List<Expression> expressionList = new ArrayList<Expression>();
          for(Chunk chunk: chunkList) {
            expressionList.add(chunk.getExpression());
          }
          return new Sequence(expressionList.toArray(new Expression[0]));
        }
        case CHOICE: {
          if(chunkList.size() == 1) {
            return chunkList.get(0).getExpression();
          }
          List<Expression> expressionList = new ArrayList<Expression>();
          boolean hasLine = false;
          for(Chunk chunk: chunkList) {
            Expression expression = chunk.getExpression();
            if(expression instanceof Repetition) {
              Repetition repetition = (Repetition)expression;
              if(repetition.getMinRepetitionCount() == 0) {
                if(repetition.getMaxRepetitionCount() == null || repetition.getMaxRepetitionCount() != 1) {
                  expression = new Repetition(repetition.getExpression(), 1, repetition.getMaxRepetitionCount());
                } else {
                  expression = repetition.getExpression();
                }
                hasLine = true;
              }
            }
            if(expression instanceof Choice) {
              for(Expression exp: ((Choice)expression).getExpressions()) {
                expressionList.add(exp);
              }
            } else {
              expressionList.add(expression);
            }
          }
          if(hasLine && (expressionList.isEmpty() || !isNoop(expressionList.get(expressionList.size() - 1)))) {
            expressionList.add(new Sequence());
          }
          return new Choice(expressionList.toArray(new Expression[0]));
        }
        case RULE: {
          return new RuleReference(text);
        }
        case LITERAL: {
          return new Literal(text);
        }
        case SPECIAL_SEQUENCE: {
          return new SpecialSequence(text);
        }
        case OPTION: {
          if(chunkList.size() == 1) {
            Chunk subChunk = chunkList.get(0);
            if(subChunk.getType() == ChunkType.CHOICE) {
              Chunk newChunk = new Chunk(ChunkType.CHOICE);
              for(Chunk cChunk: subChunk.chunkList) {
                newChunk.addChunk(cChunk);
              }
              newChunk.addChunk(new Chunk(ChunkType.GROUP));
              return newChunk.getExpression();
            }
            return new Repetition(subChunk.getExpression(), 0, 1);
          }
          List<Expression> expressionList = new ArrayList<Expression>();
          for(Chunk chunk: chunkList) {
            expressionList.add(chunk.getExpression());
          }
          return new Repetition(new Sequence(expressionList.toArray(new Expression[0])), 0, 1);
        }
        case REPETITION: {
          if(chunkList.size() == 1) {
            return new Repetition(chunkList.get(0).getExpression(), minCount, maxCount);
          }
          List<Expression> expressionList = new ArrayList<Expression>();
          for(Chunk chunk: chunkList) {
            expressionList.add(chunk.getExpression());
          }
          return new Repetition(new Sequence(expressionList.toArray(new Expression[0])), minCount, maxCount);
        }
      }
      throw new IllegalStateException("Type should not be reachable: " + type);
    }
    @Override
    public String toString() {
      String s = String.valueOf(type);
      if(text != null) {
        s += " (" + text + ")";
      }
      return s;
    }
  }

  private static boolean isNoop(Expression expression) {
    return expression instanceof Sequence && ((Sequence)expression).getExpressions().length == 0;
  }

  public Grammar convert(Reader reader) throws IOException {
    StringBuilder sb = new StringBuilder();
    List<Rule> ruleList = new ArrayList<Rule>();
    for(int x; (x=reader.read()) != -1; ) {
      char c = (char)x;
      switch(c) {
        case '=': {
          Chunk chunk = new Chunk(ChunkType.GROUP);
          loadExpression(chunk, reader, ';');
          String ruleName = sb.toString();
          sb.delete(0, sb.length());
          if(ruleName.endsWith(":")) {
            ruleName = ruleName.substring(0, ruleName.length() - 1);
            if(ruleName.endsWith(":")) {
              ruleName = ruleName.substring(0, ruleName.length() - 1);
            }
          }
          ruleName = ruleName.trim();
          ruleList.add(createRule(ruleName, chunk));
          break;
        }
        // Consider that '(' in rule name is start of a comment.
        case '(': {
          if(reader.read() != '*') {
            throw new IllegalStateException("Expecting start of a comment after '(' but could not find '*'!");
          }
          char lastChar = 0;
          for(int x2; (x2=reader.read()) != -1; ) {
            char c2 = (char)x2;
            if(c2 == ')' && lastChar == '*') {
              break;
            }
            lastChar = c2;
          }
          break;
        }
        default: {
          if(!Character.isWhitespace(c) || sb.length() > 0) {
            sb.append(c);
          }
          break;
        }
      }
    }
    return new Grammar(ruleList.toArray(new Rule[0]));
  }

  private static Rule createRule(String name, Chunk chunk) {
    chunk.prune();
    Expression expression = chunk.getExpression();
    return new Rule(name, expression);
  }

  private static void loadExpression(Chunk parentChunk, Reader reader, char stopChar) throws IOException {
    char lastChar = 0;
    StringBuilder sb = new StringBuilder();
    boolean isFirst = true;
    boolean isInSpecialGroup = false;
    char specialGroupChar = 0;
    boolean isLiteral = parentChunk.getType() == ChunkType.LITERAL;
    for(int x; (x=reader.read()) != -1; ) {
      char c = (char)x;
      if(isLiteral) {
        if(c == stopChar) {
          String s = sb.toString();
          parentChunk.setText(s);
          return;
        }
        sb.append(c);
      } else {
        if(isFirst && parentChunk.getType() == ChunkType.GROUP) {
          switch(c) {
            case '*':
              isInSpecialGroup = true;
              specialGroupChar = c;
              break;
            case '?':
              isInSpecialGroup = true;
              specialGroupChar = c;
              break;
          }
        }
        isFirst = false;
        if(isInSpecialGroup) {
          if(c == ')' && lastChar == specialGroupChar) {
            // Mutate parent group
            switch(specialGroupChar) {
              case '*': parentChunk.setType(ChunkType.COMMENT); break;
              case '?': parentChunk.setType(ChunkType.SPECIAL_SEQUENCE); break;
            }
            String comment = sb.toString();
            comment = comment.substring(1, comment.length() - 1).trim();
            parentChunk.setText(comment);
            return;
          }
          if(sb.length() > 0 || !Character.isWhitespace(c)) {
            sb.append(c);
          }
        } else {
          if(c == stopChar) {
            String content = sb.toString().trim();
            if(content.length() > 0) {
              parentChunk.addChunk(new Chunk(ChunkType.RULE, content));
            }
            return;
          }
          switch(c) {
            case ',':
            case ' ': {
              String content = sb.toString().trim();
              if(content.length() > 0) {
                parentChunk.addChunk(new Chunk(ChunkType.RULE, content));
              }
              sb.delete(0, sb.length());
              //            parentChunk.addChunk(new Chunk(ChunkType.CONCATENATION));
              break;
            }
            case '|': {
              String content = sb.toString().trim();
              if(content.length() > 0) {
                parentChunk.addChunk(new Chunk(ChunkType.RULE, content));
              }
              sb.delete(0, sb.length());
              parentChunk.addChunk(new Chunk(ChunkType.ALTERNATION));
              break;
            }
            case '*':
            case '+':
            case '?': {
              String content = sb.toString().trim();
              if(content.length() > 0) {
                parentChunk.addChunk(new Chunk(ChunkType.RULE, content));
              }
              sb.delete(0, sb.length());
              parentChunk.addChunk(new Chunk(ChunkType.REPETITION_TOKEN, String.valueOf(c)));
              break;
            }
            case '\"': {
              String content = sb.toString().trim();
              if(content.length() > 0) {
                parentChunk.addChunk(new Chunk(ChunkType.RULE, content));
              }
              sb.delete(0, sb.length());
              Chunk literalChunk = new Chunk(ChunkType.LITERAL);
              loadExpression(literalChunk, reader, '\"');
              parentChunk.addChunk(literalChunk);
              break;
            }
            case '\'': {
              String content = sb.toString().trim();
              if(content.length() > 0) {
                parentChunk.addChunk(new Chunk(ChunkType.RULE, content));
              }
              sb.delete(0, sb.length());
              Chunk literalChunk = new Chunk(ChunkType.LITERAL);
              loadExpression(literalChunk, reader, '\'');
              parentChunk.addChunk(literalChunk);
              break;
            }
            case '(': {
              String content = sb.toString().trim();
              if(content.length() > 0) {
                parentChunk.addChunk(new Chunk(ChunkType.RULE, content));
              }
              sb.delete(0, sb.length());
              Chunk groupChunk = new Chunk(ChunkType.GROUP);
              loadExpression(groupChunk, reader, ')');
              parentChunk.addChunk(groupChunk);
              break;
            }
            case '[': {
              String content = sb.toString().trim();
              if(content.length() > 0) {
                parentChunk.addChunk(new Chunk(ChunkType.RULE, content));
              }
              sb.delete(0, sb.length());
              Chunk optionChunk = new Chunk(ChunkType.OPTION);
              loadExpression(optionChunk, reader, ']');
              parentChunk.addChunk(optionChunk);
              break;
            }
            case '{': {
              String content = sb.toString().trim();
              if(content.length() > 0) {
                parentChunk.addChunk(new Chunk(ChunkType.RULE, content));
              }
              sb.delete(0, sb.length());
              Chunk repetitionChunk = new Chunk(ChunkType.REPETITION);
              repetitionChunk.setMinCount(0);
              loadExpression(repetitionChunk, reader, '}');
              parentChunk.addChunk(repetitionChunk);
              break;
            }
            default: {
              if(sb.length() > 0 || !Character.isWhitespace(c)) {
                sb.append(c);
              }
              break;
            }
          }
        }
        lastChar = c;
      }
    }
  }

}
