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
  private static final String CSS_CONNECTOR_CLASS = "c";
  static final String CSS_RULE_CLASS = "r";
  static final String CSS_LITERAL_CLASS = "l";
  static final String CSS_SPECIAL_SEQUENCE_CLASS = "s";
  static final String CSS_LOOP_CARDINALITIES_TEXT_CLASS = "lc";
  static final String CSS_RULE_TEXT_CLASS = "rt";
  static final String CSS_LITERAL_TEXT_CLASS = "lt";
  static final String CSS_SPECIAL_SEQUENCE_TEXT_CLASS = "st";

  public static class SvgContent {
    private StringBuilder pathSB = new StringBuilder();
    public void addPathConnector(String path) {
      if(pathSB.length() > 0) {
        pathSB.append(' ');
      }
      pathSB.append(path);
    }
    public void addLineConnector(int x1, int y1, int x2, int y2) {
      if(x1 == x2) {
        addPathConnector("M " + x1 + " " + y1 + " V " + y2);
      } else if(y1 == y2) {
        addPathConnector("M " + x1 + " " + y1 + " H " + x2);
      } else {
        addPathConnector("M " + x1 + " " + y1 + " L " + x2 + " " + y2);
      }
    }
    private String getConnectorElement() {
      return pathSB.length() == 0? "": "<path class=\"" + CSS_CONNECTOR_CLASS + "\" d=\""+ pathSB + "\"/>" + SVG_ELEMENTS_SEPARATOR;
    }
    private StringBuilder elementSB = new StringBuilder();
    public void addElement(String element) {
      elementSB.append(element).append(SVG_ELEMENTS_SEPARATOR);
    }
    private String getElement() {
      return elementSB.toString();
    }
    private boolean isLoopCardinalitiesUsed;
    public void setLoopCardinalitiesUsed(boolean isLoopCardinalitiesUsed) {
      this.isLoopCardinalitiesUsed = isLoopCardinalitiesUsed;
    }
    private boolean isLoopCardinalitiesUsed() {
      return isLoopCardinalitiesUsed;
    }
    private boolean isRuleUsed;
    public void setRuleUsed(boolean isRuleUsed) {
      this.isRuleUsed = isRuleUsed;
    }
    private boolean isRuleUsed() {
      return isRuleUsed;
    }
    private boolean isLiteralUsed;
    public void setLiteralUsed(boolean isLiteralUsed) {
      this.isLiteralUsed = isLiteralUsed;
    }
    private boolean isLiteralUsed() {
      return isLiteralUsed;
    }
    private boolean isSpecialSequenceUsed;
    public void setSpecialSequenceUsed(boolean isSpecialSequenceUsed) {
      this.isSpecialSequenceUsed = isSpecialSequenceUsed;
    }
    private boolean isSpecialSequenceUsed() {
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
    SvgContent svgContent = new SvgContent();
    // First, generate the XML for the elements, to know the usage.
    int xOffset = 0;
    int yOffset = 5;
//    sb.append("<rect fill=\"#FFFFFF\" stroke=\"#FF0000\" x1=\"0\" y1=\"0\" width=\"").append(width).append("\" height=\"").append(height).append("\"/>").append(SVG_ELEMENTS_SEPARATOR);
    for(RRElement rrElement: rrElementList) {
      LayoutInfo layoutInfo2 = rrElement.getLayoutInfo();
      int connectorOffset2 = layoutInfo2.getConnectorOffset();
      int width2 = layoutInfo2.getWidth();
      int height2 = layoutInfo2.getHeight();
      int y1 = yOffset + connectorOffset2;
      svgContent.addLineConnector(xOffset, y1, xOffset + 5, y1);
      svgContent.addLineConnector(xOffset + 5 + width2, y1, xOffset + 5 + width2 + 5, y1);
      // TODO: add decorations (like arrows)?
      rrElement.toSVG(rrDiagramToSVG, xOffset + 5, yOffset, svgContent);
      yOffset += height2 + 10;
    }
    // Then generate the rest (CSS and SVG container tags) based on that usage.
    StringBuilder sb = new StringBuilder();
    sb.append("<svg version=\"1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns=\"http://www.w3.org/2000/svg\" width=\"").append(width).append("\" height=\"").append(height).append("\"><defs>");
    String cssElementSeparator = SVG_ELEMENTS_SEPARATOR.length() == 0? " ": SVG_ELEMENTS_SEPARATOR;
    sb.append("<style type=\"text/css\">").append(SVG_ELEMENTS_SEPARATOR);
    String connectorColor = Utils.convertColorToHtml(rrDiagramToSVG.getConnectorColor());
    sb.append(".").append(CSS_CONNECTOR_CLASS).append(" {fill: none; stroke: ").append(connectorColor).append(";}").append(cssElementSeparator);
    if(svgContent.isRuleUsed()) {
      String ruleBorderColor = Utils.convertColorToHtml(rrDiagramToSVG.getRuleBorderColor());
      String ruleFillColor = Utils.convertColorToHtml(rrDiagramToSVG.getRuleFillColor());
      Font ruleFont = rrDiagramToSVG.getRuleFont();
      String ruleTextColor = Utils.convertColorToHtml(rrDiagramToSVG.getRuleTextColor());
      sb.append(".").append(CSS_RULE_CLASS).append(" {fill: ").append(ruleFillColor).append("; stroke: ").append(ruleBorderColor).append(";}").append(cssElementSeparator);
      sb.append(".").append(CSS_RULE_TEXT_CLASS).append(" {fill: ").append(ruleTextColor).append("; ").append(Utils.convertFontToCss(ruleFont)).append("}").append(cssElementSeparator);
    }
    if(svgContent.isLiteralUsed()) {
      String literalBorderColor = Utils.convertColorToHtml(rrDiagramToSVG.getLiteralBorderColor());
      String literalFillColor = Utils.convertColorToHtml(rrDiagramToSVG.getLiteralFillColor());
      Font literalFont = rrDiagramToSVG.getLiteralFont();
      String literalTextColor = Utils.convertColorToHtml(rrDiagramToSVG.getLiteralTextColor());
      sb.append(".").append(CSS_LITERAL_CLASS).append(" {fill: ").append(literalFillColor).append("; stroke: ").append(literalBorderColor).append(";}").append(cssElementSeparator);
      sb.append(".").append(CSS_LITERAL_TEXT_CLASS).append(" {fill: ").append(literalTextColor).append("; ").append(Utils.convertFontToCss(literalFont)).append("}").append(cssElementSeparator);
    }
    if(svgContent.isSpecialSequenceUsed()) {
      String specialSequenceBorderColor = Utils.convertColorToHtml(rrDiagramToSVG.getSpecialSequenceBorderColor());
      String specialSequenceFillColor = Utils.convertColorToHtml(rrDiagramToSVG.getSpecialSequenceFillColor());
      Font specialSequenceFont = rrDiagramToSVG.getSpecialSequenceFont();
      String specialSequenceTextColor = Utils.convertColorToHtml(rrDiagramToSVG.getSpecialSequenceTextColor());
      sb.append(".").append(CSS_SPECIAL_SEQUENCE_CLASS).append(" {fill: ").append(specialSequenceFillColor).append("; stroke: ").append(specialSequenceBorderColor).append(";}").append(cssElementSeparator);
      sb.append(".").append(CSS_SPECIAL_SEQUENCE_TEXT_CLASS).append(" {fill: ").append(specialSequenceTextColor).append("; ").append(Utils.convertFontToCss(specialSequenceFont)).append(";}").append(cssElementSeparator);
    }
    if(svgContent.isLoopCardinalitiesUsed()) {
      Font loopFont = rrDiagramToSVG.getLoopFont();
      String loopTextColor = Utils.convertColorToHtml(rrDiagramToSVG.getLoopTextColor());
      sb.append(".").append(CSS_LOOP_CARDINALITIES_TEXT_CLASS).append(" {fill: ").append(loopTextColor).append("; ").append(Utils.convertFontToCss(loopFont)).append("}").append(cssElementSeparator);
    }
    sb.append("</style>");
    sb.append("</defs>").append(SVG_ELEMENTS_SEPARATOR);
    sb.append(svgContent.getConnectorElement());
    sb.append(svgContent.getElement());
    sb.append("</svg>");
    return sb.toString();
  }

}
