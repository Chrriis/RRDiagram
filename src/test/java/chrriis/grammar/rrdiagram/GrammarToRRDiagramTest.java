package chrriis.grammar.rrdiagram;

import static org.joox.JOOX.$;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import chrriis.grammar.model.BNFToGrammar;
import chrriis.grammar.model.Grammar;
import chrriis.grammar.model.GrammarToRRDiagram;
import chrriis.grammar.model.Rule;

/**
 * @author Lukas Eder
 */
public class GrammarToRRDiagramTest {

  @Test
  public void testConsecutiveRuleReferencesSeparatedByNewline() throws Exception {
    assertEquals(5, $(svg("rule = a b\nc d\n\te;")).find("rect").size());
    assertEquals(5, $(svg("rule = a b\r\nc d\ne;")).find("rect").size());
  }

  private String svg(String string) throws IOException {
    BNFToGrammar bnfToGrammar = new BNFToGrammar();
    Grammar grammar = bnfToGrammar.convert(new StringReader(string));
    Rule[] rules = grammar.getRules();
    GrammarToRRDiagram grammarToRRDiagram = new GrammarToRRDiagram();
    RRDiagram diagram = grammarToRRDiagram.convert(rules[0]);
    RRDiagramToSVG rrDiagramToSVG = new RRDiagramToSVG();
    String svg = rrDiagramToSVG.convert(diagram);
    return svg;
  }
}
