/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.rrdiagram;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import chrriis.common.Utils;
import chrriis.grammar.rrdiagram.RRElement.LayoutInfo;

/**
 * @author Christopher Deckers
 */
public class RRDiagram {

  private RRElement rrElement;

  public RRDiagram(RRElement rrElement) {
    this.rrElement = rrElement;
  }

  static final String SVG_ELEMENTS_SEPARATOR = "";//"\n";
  static final String CSS_CONNECTOR_CLASS = "c";
  static final String CSS_RULE_CLASS = "r";
  static final String CSS_LITERAL_CLASS = "l";
  static final String CSS_SPECIAL_SEQUENCE_CLASS = "s";
  static final String CSS_LOOP_CARDINALITIES_TEXT_CLASS = "lc";
  static final String CSS_RULE_TEXT_CLASS = "rt";
  static final String CSS_LITERAL_TEXT_CLASS = "lt";
  static final String CSS_SPECIAL_SEQUENCE_TEXT_CLASS = "st";

  public static class SvgUsage {
    private boolean isLoopUsed;
    public void setLoopCardinalitiesUsed(boolean isLoopUsed) {
      this.isLoopUsed = isLoopUsed;
    }
    public boolean isLoopUsed() {
      return isLoopUsed;
    }
    private boolean isRuleUsed;
    public void setRuleUsed(boolean isRuleUsed) {
      this.isRuleUsed = isRuleUsed;
    }
    public boolean isRuleUsed() {
      return isRuleUsed;
    }
    private boolean isLiteralUsed;
    public void setLiteralUsed(boolean isLiteralUsed) {
      this.isLiteralUsed = isLiteralUsed;
    }
    public boolean isLiteralUsed() {
      return isLiteralUsed;
    }
    private boolean isSpecialSequenceUsed;
    public void setSpecialSequenceUsed(boolean isSpecialSequenceUsed) {
      this.isSpecialSequenceUsed = isSpecialSequenceUsed;
    }
    public boolean isSpecialSequenceUsed() {
      return isSpecialSequenceUsed;
    }
  }

  String toSVG(RRDiagramToSVG rrDiagramToSVG) {
    List<RRElement> rrElementList = new ArrayList<RRElement>();
    if(rrElement instanceof RRSequence) {
      List<RRElement> cursorElementList = new ArrayList<RRElement>();
      for(RRElement element: ((RRSequence)rrElement).getRRElements()) {
        if(element instanceof RRBreak) {
          if(!cursorElementList.isEmpty()) {
            rrElementList.add(cursorElementList.size() == 1? cursorElementList.get(0): new RRSequence(cursorElementList.toArray(new RRElement[0])));
            cursorElementList.clear();
          }
        } else {
          cursorElementList.add(element);
        }
      }
      if(!cursorElementList.isEmpty()) {
        rrElementList.add(cursorElementList.size() == 1? cursorElementList.get(0): new RRSequence(cursorElementList.toArray(new RRElement[0])));
      }
    } else {
      rrElementList.add(rrElement);
    }
    int width = 5;
    int height = 5;
    for (int i = 0; i < rrElementList.size(); i++) {
      if(i > 0) {
        height += 5;
      }
      RRElement rrElement = rrElementList.get(i);
      rrElement.computeLayoutInfo(rrDiagramToSVG);
      LayoutInfo layoutInfo = rrElement.getLayoutInfo();
      width = Math.max(width, 5 + layoutInfo.getWidth() + 5);
      height += layoutInfo.getHeight() + 5;
    }
    SvgUsage svgUsage = new SvgUsage();
    // First, generate the XML for the elements, to know the usage.
    StringBuilder elementsSB = new StringBuilder();
    int xOffset = 0;
    int yOffset = 5;
//    sb.append("<rect fill=\"#FFFFFF\" stroke=\"#FF0000\" x1=\"0\" y1=\"0\" width=\"").append(width).append("\" height=\"").append(height).append("\"/>").append(SVG_ELEMENTS_SEPARATOR);
    for(RRElement rrElement: rrElementList) {
      LayoutInfo layoutInfo2 = rrElement.getLayoutInfo();
      int connectorOffset2 = layoutInfo2.getConnectorOffset();
      int width2 = layoutInfo2.getWidth();
      int height2 = layoutInfo2.getHeight();
      int y1 = yOffset + connectorOffset2;
      elementsSB.append("<line class=\"").append(CSS_CONNECTOR_CLASS).append("\" x1=\"").append(xOffset).append("\" y1=\"").append(y1).append("\" x2=\"").append(xOffset + 5).append("\" y2=\"").append(y1).append("\"/>").append(SVG_ELEMENTS_SEPARATOR);
      elementsSB.append("<line class=\"").append(CSS_CONNECTOR_CLASS).append("\" x1=\"").append(xOffset + 5 + width2).append("\" y1=\"").append(y1).append("\" x2=\"").append(xOffset + 5 + width2 + 5).append("\" y2=\"").append(y1).append("\"/>").append(SVG_ELEMENTS_SEPARATOR);
      // TODO: add decorations (like arrows)?
      rrElement.toSVG(rrDiagramToSVG, xOffset + 5, yOffset, elementsSB, svgUsage);
      yOffset += height2 + 10;
    }
    // Then generate the rest (CSS and SVG container tags) based on that usage.
    StringBuilder sb = new StringBuilder();
    sb.append("<svg xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns=\"http://www.w3.org/2000/svg\" width=\"").append(width).append("\" height=\"").append(height).append("\"><defs>");
    String cssElementSeparator = SVG_ELEMENTS_SEPARATOR.length() == 0? " ": SVG_ELEMENTS_SEPARATOR;
    sb.append("<style type=\"text/css\">").append(SVG_ELEMENTS_SEPARATOR);
    String connectorColor = Utils.convertColorToHtml(rrDiagramToSVG.getConnectorColor());
    sb.append(".").append(CSS_CONNECTOR_CLASS).append(" {fill: none; stroke: ").append(connectorColor).append(";}").append(cssElementSeparator);
    if(svgUsage.isRuleUsed()) {
      String ruleBorderColor = Utils.convertColorToHtml(rrDiagramToSVG.getRuleBorderColor());
      String ruleFillColor = Utils.convertColorToHtml(rrDiagramToSVG.getRuleFillColor());
      Font ruleFont = rrDiagramToSVG.getRuleFont();
      String ruleTextColor = Utils.convertColorToHtml(rrDiagramToSVG.getRuleTextColor());
      sb.append(".").append(CSS_RULE_CLASS).append(" {fill: ").append(ruleFillColor).append("; stroke: ").append(ruleBorderColor).append(";}").append(cssElementSeparator);
      sb.append(".").append(CSS_RULE_TEXT_CLASS).append(" {fill: ").append(ruleTextColor).append("; ").append(Utils.convertFontToCss(ruleFont)).append("}").append(cssElementSeparator);
    }
    if(svgUsage.isLiteralUsed()) {
      String literalBorderColor = Utils.convertColorToHtml(rrDiagramToSVG.getLiteralBorderColor());
      String literalFillColor = Utils.convertColorToHtml(rrDiagramToSVG.getLiteralFillColor());
      Font literalFont = rrDiagramToSVG.getLiteralFont();
      String literalTextColor = Utils.convertColorToHtml(rrDiagramToSVG.getLiteralTextColor());
      sb.append(".").append(CSS_LITERAL_CLASS).append(" {fill: ").append(literalFillColor).append("; stroke: ").append(literalBorderColor).append(";}").append(cssElementSeparator);
      sb.append(".").append(CSS_LITERAL_TEXT_CLASS).append(" {fill: ").append(literalTextColor).append("; ").append(Utils.convertFontToCss(literalFont)).append("}").append(cssElementSeparator);
    }
    if(svgUsage.isSpecialSequenceUsed()) {
      String specialSequenceBorderColor = Utils.convertColorToHtml(rrDiagramToSVG.getSpecialSequenceBorderColor());
      String specialSequenceFillColor = Utils.convertColorToHtml(rrDiagramToSVG.getSpecialSequenceFillColor());
      Font specialSequenceFont = rrDiagramToSVG.getSpecialSequenceFont();
      String specialSequenceTextColor = Utils.convertColorToHtml(rrDiagramToSVG.getSpecialSequenceTextColor());
      sb.append(".").append(CSS_SPECIAL_SEQUENCE_CLASS).append(" {fill: ").append(specialSequenceFillColor).append("; stroke: ").append(specialSequenceBorderColor).append(";}").append(cssElementSeparator);
      sb.append(".").append(CSS_SPECIAL_SEQUENCE_TEXT_CLASS).append(" {fill: ").append(specialSequenceTextColor).append("; ").append(Utils.convertFontToCss(specialSequenceFont)).append(";}").append(cssElementSeparator);
    }
    if(svgUsage.isLoopUsed()) {
      Font loopFont = rrDiagramToSVG.getLoopFont();
      String loopTextColor = Utils.convertColorToHtml(rrDiagramToSVG.getLoopTextColor());
      sb.append(".").append(CSS_LOOP_CARDINALITIES_TEXT_CLASS).append(" {fill: ").append(loopTextColor).append("; ").append(Utils.convertFontToCss(loopFont)).append("}").append(cssElementSeparator);
    }
    sb.append("</style>");
    sb.append("</defs>").append(SVG_ELEMENTS_SEPARATOR);
    sb.append(elementsSB);
    sb.append("</svg>");
    return sb.toString();
  }

}
