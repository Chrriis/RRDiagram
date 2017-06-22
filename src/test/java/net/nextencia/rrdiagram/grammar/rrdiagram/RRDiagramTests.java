package net.nextencia.rrdiagram.grammar.rrdiagram;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import net.nextencia.rrdiagram.grammar.model.BNFToGrammar;
import net.nextencia.rrdiagram.grammar.model.Choice;
import net.nextencia.rrdiagram.grammar.model.Grammar;
import net.nextencia.rrdiagram.grammar.model.GrammarToBNF;
import net.nextencia.rrdiagram.grammar.model.GrammarToRRDiagram;
import net.nextencia.rrdiagram.grammar.model.Literal;
import net.nextencia.rrdiagram.grammar.model.Repetition;
import net.nextencia.rrdiagram.grammar.model.Rule;
import net.nextencia.rrdiagram.grammar.model.RuleReference;
import net.nextencia.rrdiagram.grammar.model.Sequence;
import net.nextencia.rrdiagram.grammar.model.SpecialSequence;

/**
 * @author Lukas Eder
 */
public class RRDiagramTests {

  @Test
  public void testConsecutiveRuleReferencesSeparatedByNewline() {
    assertEquals(5, countElements("rect", svg("rule = a b\nc d\n\te;")));
    assertEquals(5, countElements("rect", svg("rule = a b\r\nc d\ne;")));
  }

  @Test
  public void testGrammarToString() {
    assertEquals("r = a b;", grammar("r = a b;").toString());
    assertEquals("r1 = a b;\nr2 = c d;", grammar("r1 = a b;\nr2 = c d;").toString());
  }

  @Test
  public void testRuleToString() {
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

  @Test
  public void testConversionsToBNF() {
    String bnf1 =
        "BNF1 = a*;" +
        "BNF2 = a+;" +
        "BNF3 = a?;" +
        "BNF4 = a (',' a)*;" +
        "BNF5 = 3 * a;" +
        "BNF6 = 3 * a?;" +
        "BNF7 = a 3 * (',' a);" +
        "BNF8 = a 3 * (',' a)?;" +
        "BNF9 = a (? [a-zA-Z]+ ?) 3 * (',' a)?;" +
        "BNF10 = a | c | ();" +
        "BNF11 = 3 * 'a<b\"';";
    Grammar grammar1 = grammar(bnf1);
    GrammarToBNF grammarToBNF = new GrammarToBNF();
    String bnf2 = grammarToBNF.convert(grammar1);
    // Resulting BNF may be different depending on format options.
    // Nevertheless, they should be equivalent, and one way to test this is
    // to produce the both SVG and compare them.
//    System.err.println(bnf2);
    Grammar grammar2 = grammar(bnf2);
    Rule[] rules1 = grammar1.getRules();
    Rule[] rules2 = grammar2.getRules();
    assertEquals(rules1.length, rules2.length);
    GrammarToRRDiagram grammarToRRDiagram = new GrammarToRRDiagram();
    RRDiagramToSVG rrDiagramToSVG = new RRDiagramToSVG();
    for (int i = 0; i < rules1.length; i++) {
      Rule rule1 = rules1[i];
      Rule rule2 = rules2[i];
      assertEquals("Rules have same name", rule1.getName(), rule2.getName());
      RRDiagram diagram1 = grammarToRRDiagram.convert(rule1);
      String svg1 = rrDiagramToSVG.convert(diagram1);
      RRDiagram diagram2 = grammarToRRDiagram.convert(rule2);
      String svg2 = rrDiagramToSVG.convert(diagram2);
//      System.err.println("SVG1: " + svg1);
//      System.err.println("SVG2: " + svg2);
      assertEquals("SVG for \"" + rule1.getName() + "\" are identical", svg1, svg2);
    }
  }

  // Test utilities

  private String svg(String string) {
    Grammar grammar = grammar(string);
    Rule[] rules = grammar.getRules();
    GrammarToRRDiagram grammarToRRDiagram = new GrammarToRRDiagram();
    RRDiagram diagram = grammarToRRDiagram.convert(rules[0]);
    RRDiagramToSVG rrDiagramToSVG = new RRDiagramToSVG();
    String svg = rrDiagramToSVG.convert(diagram);
    return svg;
  }

  private Grammar grammar(String string) {
    BNFToGrammar bnfToGrammar = new BNFToGrammar();
    Grammar grammar = bnfToGrammar.convert(string);
    return grammar;
  }

  private Rule rule(String string) {
    return grammar(string).getRules()[0];
  }

  private int countElements(String tagName, String svg) {
    try {
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(svg)));
      return document.getElementsByTagName(tagName).getLength();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
