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
import chrriis.grammar.rrdiagram.RRDiagram.SvgUsage;
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
  protected void toSVG(RRDiagramToSVG rrDiagramToSVG, int xOffset, int yOffset, StringBuilder sb, SvgUsage svgUsage) {
    LayoutInfo layoutInfo = getLayoutInfo();
    int width = layoutInfo.getWidth();
    int height = layoutInfo.getHeight();
    if(link != null) {
      sb.append("<a xlink:href=\"").append(Utils.escapeXML(link))/*.append("\" xlink:title=\"").append(Utils.escapeXML(text))*/.append("\">").append(RRDiagram.SVG_ELEMENTS_SEPARATOR);
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
        cssClass = RRDiagram.CSS_RULE_CLASS;
        cssTextClass = RRDiagram.CSS_RULE_TEXT_CLASS;
        shape = rrDiagramToSVG.getRuleShape();
        svgUsage.setRuleUsed(true);
        break;
      case LITERAL:
        insets = rrDiagramToSVG.getLiteralInsets();
        font = rrDiagramToSVG.getLiteralFont();
        cssClass = RRDiagram.CSS_LITERAL_CLASS;
        cssTextClass = RRDiagram.CSS_LITERAL_TEXT_CLASS;
        shape = rrDiagramToSVG.getLiteralShape();
        svgUsage.setLiteralUsed(true);
        break;
      case SPECIAL_SEQUENCE:
        insets = rrDiagramToSVG.getSpecialSequenceInsets();
        font = rrDiagramToSVG.getSpecialSequenceFont();
        cssClass = RRDiagram.CSS_SPECIAL_SEQUENCE_CLASS;
        cssTextClass = RRDiagram.CSS_SPECIAL_SEQUENCE_TEXT_CLASS;
        shape = rrDiagramToSVG.getSpecialSequenceShape();
        svgUsage.setSpecialSequenceUsed(true);
        break;
      default:
        throw new IllegalStateException("Unknown type: " + type);
    }
    switch(shape) {
      case RECTANGLE:
        sb.append("<rect class=\"").append(cssClass).append("\" x=\"").append(xOffset).append("\" y=\"").append(yOffset).append("\" width=\"").append(width).append("\" height=\"").append(height).append("\"/>").append(RRDiagram.SVG_ELEMENTS_SEPARATOR);
        break;
      case ROUNDED_RECTANGLE:
        // Connector may be in rounded area if there are huge margins at top, but this is an unrealistic case so we don't add lines to complete the connector.
        int rx = (insets.left + insets.right + insets.top + insets.bottom) / 4;
        sb.append("<rect class=\"").append(cssClass).append("\" x=\"").append(xOffset).append("\" y=\"").append(yOffset).append("\" width=\"").append(width).append("\" height=\"").append(height).append("\" rx=\"").append(rx).append("\"/>").append(RRDiagram.SVG_ELEMENTS_SEPARATOR);
        break;
      case HEXAGON:
        // We don't calculate the exact length of the connector: it goes behind the shape.
        // We should calculate if we want to support transparent shapes.
        int connectorOffset = layoutInfo.getConnectorOffset();
        sb.append("<line class=\"").append(RRDiagram.CSS_CONNECTOR_CLASS).append("\" x1=\"").append(xOffset).append("\" y1=\"").append(yOffset + connectorOffset).append("\" x2=\"").append(xOffset + insets.left).append("\" y2=\"").append(yOffset + connectorOffset).append("\"/>").append(RRDiagram.SVG_ELEMENTS_SEPARATOR);
        sb.append("<line class=\"").append(RRDiagram.CSS_CONNECTOR_CLASS).append("\" x1=\"").append(xOffset + width).append("\" y1=\"").append(yOffset + connectorOffset).append("\" x2=\"").append(xOffset + width - insets.right).append("\" y2=\"").append(yOffset + connectorOffset).append("\"/>").append(RRDiagram.SVG_ELEMENTS_SEPARATOR);
        sb.append("<polygon class=\"").append(cssClass).append("\" points=\"").append(xOffset).append(" ").append(yOffset + height / 2).append(" ").append(xOffset + insets.left).append(" ").append(yOffset).append(" ").append(xOffset + width - insets.right).append(" ").append(yOffset).append(" ").append(xOffset + width).append(" ").append(yOffset + height / 2).append(" ").append(xOffset + width - insets.right).append(" ").append(yOffset + height).append(" ").append(xOffset + insets.left).append(" ").append(yOffset + height).append("\"/>").append(RRDiagram.SVG_ELEMENTS_SEPARATOR);
        break;
    }
    FontRenderContext fontRenderContext = new FontRenderContext(null, true, false);
    Rectangle2D stringBounds = font.getStringBounds(text, fontRenderContext);
    int textXOffset = xOffset + insets.left;
    int textYOffset = yOffset + insets.top + (int)Math.round(stringBounds.getHeight()) - fontYOffset;
    sb.append("<text class=\"").append(cssTextClass).append("\" x=\"").append(textXOffset).append("\" y=\"").append(textYOffset).append("\">").append(Utils.escapeXML(text)).append("</text>").append(RRDiagram.SVG_ELEMENTS_SEPARATOR);
    if(link != null) {
      sb.append("</a>").append(RRDiagram.SVG_ELEMENTS_SEPARATOR);
    }
  }

}
