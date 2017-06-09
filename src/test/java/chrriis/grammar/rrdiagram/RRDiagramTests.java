package chrriis.grammar.rrdiagram;

import static org.joox.JOOX.$;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import chrriis.grammar.model.BNFToGrammar;
import chrriis.grammar.model.Choice;
import chrriis.grammar.model.Grammar;
import chrriis.grammar.model.GrammarToRRDiagram;
import chrriis.grammar.model.Literal;
import chrriis.grammar.model.Repetition;
import chrriis.grammar.model.Rule;
import chrriis.grammar.model.RuleReference;
import chrriis.grammar.model.Sequence;
import chrriis.grammar.model.SpecialSequence;

/**
 * @author Lukas Eder
 */
public class RRDiagramTests {

  @Test
  public void testConsecutiveRuleReferencesSeparatedByNewline() throws Exception {
    assertEquals(5, $(svg("rule = a b\nc d\n\te;")).find("rect").size());
    assertEquals(5, $(svg("rule = a b\r\nc d\ne;")).find("rect").size());
  }

  @Test
  public void testGrammarToString() throws Exception {
    assertEquals("r = a b;", grammar("r = a b;").toString());
    assertEquals("r1 = a b;\nr2 = c d;", grammar("r1 = a b;\nr2 = c d;").toString());
  }

  @Test
  public void testRuleToString() throws Exception {
    assertEquals("r = a b;", rule("r = a b;").toString());
  }

  @Test
  public void testChoiceToString() {
    assertEquals("a | b", new Choice(new RuleReference("a"), new RuleReference("b")).toString());
  }

  @Test
  public void testLiteralToString() {
    assertEquals("'a'", new Literal("a").toString());
  }

  @Test
  public void testRuleReferenceToString() {
    assertEquals("a", new RuleReference("a").toString());
  }

  @Test
  public void testSequenceToString() {
    assertEquals("a b", new Sequence(new RuleReference("a"), new RuleReference("b")).toString());
  }

  @Test
  public void testSpecialSequenceToString() {
    assertEquals("(? abc ?)", new SpecialSequence("abc").toString());
  }

  @Test
  public void testRepetitionToString() {
    assertEquals("{ a }", new Repetition(new RuleReference("a"), 0, null).toString());
    assertEquals("[ a ]", new Repetition(new RuleReference("a"), 0, 1).toString());
    assertEquals("2 * [ a ]", new Repetition(new RuleReference("a"), 0, 2).toString());
  }

  // Test utilities

  private String svg(String string) throws IOException {
    Grammar grammar = grammar(string);
    Rule[] rules = grammar.getRules();
    GrammarToRRDiagram grammarToRRDiagram = new GrammarToRRDiagram();
    RRDiagram diagram = grammarToRRDiagram.convert(rules[0]);
    RRDiagramToSVG rrDiagramToSVG = new RRDiagramToSVG();
    String svg = rrDiagramToSVG.convert(diagram);
    return svg;
  }

  private Grammar grammar(String string) throws IOException {
    BNFToGrammar bnfToGrammar = new BNFToGrammar();
    Grammar grammar = bnfToGrammar.convert(new StringReader(string));
    return grammar;
  }

  private Rule rule(String string) throws IOException {
    return grammar(string).getRules()[0];
  }
}
