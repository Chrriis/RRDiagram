/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.rrdiagram;

import java.awt.Font;
import java.awt.Insets;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import chrriis.common.Utils;
import chrriis.grammar.rrdiagram.RRDiagram.SvgContent;
import chrriis.grammar.rrdiagram.RRDiagramToSVG.BoxShape;

/**
 * @author Christopher Deckers
 */
public class RRText extends RRElement {

  public static enum Type {
    LITERAL,
    RULE,
    SPECIAL_SEQUENCE,
  }

  private Type type;
  private String text;
  private String link;

  public RRText(Type type, String text, String link) {
    this.type = type;
    this.text = text;
    this.link = link;
  }

  public Type getType() {
    return type;
  }

  public String getText() {
    return text;
  }

  public String getLink() {
    return link;
  }

  private int fontYOffset;

  @Override
  protected void computeLayoutInfo(RRDiagramToSVG rrDiagramToSVG) {
    FontRenderContext fontRenderContext = new FontRenderContext(null, true, false);
    Font font;
    Insets insets;
    switch(type) {
      case RULE:
        insets = rrDiagramToSVG.getRuleInsets();
        font = rrDiagramToSVG.getRuleFont();
        break;
      case LITERAL:
        insets = rrDiagramToSVG.getLiteralInsets();
        font = rrDiagramToSVG.getLiteralFont();
        break;
      case SPECIAL_SEQUENCE:
        insets = rrDiagramToSVG.getSpecialSequenceInsets();
        font = rrDiagramToSVG.getSpecialSequenceFont();
        break;
      default: throw new IllegalStateException("Unknown type: " + type);
    }
    LineMetrics lineMetrics = font.getLineMetrics(text, fontRenderContext);
    fontYOffset = Math.round(lineMetrics.getDescent());
    Rectangle2D stringBounds = font.getStringBounds(text, fontRenderContext);
    int width = (int)Math.round(stringBounds.getWidth());
    int height = (int)Math.round(stringBounds.getHeight());
    int connectorOffset = insets.top + height - fontYOffset;
    width += insets.left + insets.right;
    height += insets.top + insets.bottom;
    setLayoutInfo(new LayoutInfo(width, height, connectorOffset));
  }

  @Override
  protected void toSVG(RRDiagramToSVG rrDiagramToSVG, int xOffset, int yOffset, SvgContent svgContent) {
    LayoutInfo layoutInfo = getLayoutInfo();
    int width = layoutInfo.getWidth();
    int height = layoutInfo.getHeight();
    if(link != null) {
      svgContent.addElement("<a xlink:href=\"" + Utils.escapeXML(link)/* + "\" xlink:title=\"" + Utils.escapeXML(text)*/ + "\">");
    }
    Insets insets;
    Font font;
    String cssClass;
    String cssTextClass;
    BoxShape shape;
    switch(type) {
      case RULE:
        insets = rrDiagramToSVG.getRuleInsets();
        font = rrDiagramToSVG.getRuleFont();
        cssClass = svgContent.getDefinedCSSClass(RRDiagram.CSS_RULE_CLASS);
        cssTextClass = svgContent.getDefinedCSSClass(RRDiagram.CSS_RULE_TEXT_CLASS);
        if(cssClass == null) {
          String ruleBorderColor = Utils.convertColorToHtml(rrDiagramToSVG.getRuleBorderColor());
          String ruleFillColor = Utils.convertColorToHtml(rrDiagramToSVG.getRuleFillColor());
          Font ruleFont = rrDiagramToSVG.getRuleFont();
          String ruleTextColor = Utils.convertColorToHtml(rrDiagramToSVG.getRuleTextColor());
          cssClass = svgContent.setCSSClass(RRDiagram.CSS_RULE_CLASS, "fill:" + ruleFillColor + ";stroke:" + ruleBorderColor + ";");
          cssTextClass = svgContent.setCSSClass(RRDiagram.CSS_RULE_TEXT_CLASS, "fill:" + ruleTextColor + ";" + Utils.convertFontToCss(ruleFont));
        }
        shape = rrDiagramToSVG.getRuleShape();
        break;
      case LITERAL:
        insets = rrDiagramToSVG.getLiteralInsets();
        font = rrDiagramToSVG.getLiteralFont();
        cssClass = svgContent.getDefinedCSSClass(RRDiagram.CSS_LITERAL_CLASS);
        cssTextClass = svgContent.getDefinedCSSClass(RRDiagram.CSS_LITERAL_TEXT_CLASS);
        if(cssClass == null) {
          String literalBorderColor = Utils.convertColorToHtml(rrDiagramToSVG.getLiteralBorderColor());
          String literalFillColor = Utils.convertColorToHtml(rrDiagramToSVG.getLiteralFillColor());
          Font literalFont = rrDiagramToSVG.getLiteralFont();
          String literalTextColor = Utils.convertColorToHtml(rrDiagramToSVG.getLiteralTextColor());
          cssClass = svgContent.setCSSClass(RRDiagram.CSS_LITERAL_CLASS, "fill:" + literalFillColor + ";stroke:" + literalBorderColor + ";");
          cssTextClass = svgContent.setCSSClass(RRDiagram.CSS_LITERAL_TEXT_CLASS, "fill:" + literalTextColor + ";" + Utils.convertFontToCss(literalFont));
        }
        shape = rrDiagramToSVG.getLiteralShape();
        break;
      case SPECIAL_SEQUENCE:
        insets = rrDiagramToSVG.getSpecialSequenceInsets();
        font = rrDiagramToSVG.getSpecialSequenceFont();
        cssClass = svgContent.getDefinedCSSClass(RRDiagram.CSS_SPECIAL_SEQUENCE_CLASS);
        cssTextClass = svgContent.getDefinedCSSClass(RRDiagram.CSS_SPECIAL_SEQUENCE_TEXT_CLASS);
        if(cssClass == null) {
          String specialSequenceBorderColor = Utils.convertColorToHtml(rrDiagramToSVG.getSpecialSequenceBorderColor());
          String specialSequenceFillColor = Utils.convertColorToHtml(rrDiagramToSVG.getSpecialSequenceFillColor());
          Font specialSequenceFont = rrDiagramToSVG.getSpecialSequenceFont();
          String specialSequenceTextColor = Utils.convertColorToHtml(rrDiagramToSVG.getSpecialSequenceTextColor());
          cssClass = svgContent.setCSSClass(RRDiagram.CSS_SPECIAL_SEQUENCE_CLASS, "fill:" + specialSequenceFillColor + ";stroke:" + specialSequenceBorderColor + ";");
          cssTextClass = svgContent.setCSSClass(RRDiagram.CSS_SPECIAL_SEQUENCE_TEXT_CLASS, "fill:" + specialSequenceTextColor + ";" + Utils.convertFontToCss(specialSequenceFont));
        }
        shape = rrDiagramToSVG.getSpecialSequenceShape();
        break;
      default:
        throw new IllegalStateException("Unknown type: " + type);
    }
    switch(shape) {
      case RECTANGLE:
        svgContent.addElement("<rect class=\"" + cssClass + "\" x=\"" + xOffset + "\" y=\"" + yOffset + "\" width=\"" + width + "\" height=\"" + height + "\"/>");
        break;
      case ROUNDED_RECTANGLE:
        // Connector may be in rounded area if there are huge margins at top, but this is an unrealistic case so we don't add lines to complete the connector.
        int rx = (insets.left + insets.right + insets.top + insets.bottom) / 4;
        svgContent.addElement("<rect class=\"" + cssClass + "\" x=\"" + xOffset + "\" y=\"" + yOffset + "\" width=\"" + width + "\" height=\"" + height + "\" rx=\"" + rx + "\"/>");
        break;
      case HEXAGON:
        // We don't calculate the exact length of the connector: it goes behind the shape.
        // We should calculate if we want to support transparent shapes.
        int connectorOffset = layoutInfo.getConnectorOffset();
        svgContent.addLineConnector(xOffset, yOffset + connectorOffset, xOffset + insets.left, yOffset + connectorOffset);
        svgContent.addElement("<polygon class=\"" + cssClass + "\" points=\"" + xOffset + " " + (yOffset + height / 2) + " " + (xOffset + insets.left) + " " + yOffset + " " + (xOffset + width - insets.right) + " " + yOffset + " " + (xOffset + width) + " " + (yOffset + height / 2) + " " + (xOffset + width - insets.right) + " " + (yOffset + height) + " " + (xOffset + insets.left) + " " + (yOffset + height) + "\"/>");
        svgContent.addLineConnector(xOffset + width, yOffset + connectorOffset, xOffset + width - insets.right, yOffset + connectorOffset);
        break;
    }
    FontRenderContext fontRenderContext = new FontRenderContext(null, true, false);
    Rectangle2D stringBounds = font.getStringBounds(text, fontRenderContext);
    int textXOffset = xOffset + insets.left;
    int textYOffset = yOffset + insets.top + (int)Math.round(stringBounds.getHeight()) - fontYOffset;
    svgContent.addElement("<text class=\"" + cssTextClass + "\" x=\"" + textXOffset + "\" y=\"" + textYOffset + "\">" + Utils.escapeXML(text) + "</text>");
    if(link != null) {
      svgContent.addElement("</a>");
    }
  }

}
