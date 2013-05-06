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
  protected void toSVG(RRDiagramToSVG rrDiagramToSVG, int xOffset, int yOffset, StringBuilder sb) {
    LayoutInfo layoutInfo = getLayoutInfo();
    int width = layoutInfo.getWidth();
    int height = layoutInfo.getHeight();
    if(link != null) {
      sb.append("<a xlink:href=\"").append(Utils.escapeXML(link))/*.append("\" xlink:title=\"").append(Utils.escapeXML(text))*/.append("\">");
    }
    Insets insets;
    Font font;
    String cssClass;
    BoxShape shape;
    switch(type) {
      case RULE:
        insets = rrDiagramToSVG.getRuleInsets();
        font = rrDiagramToSVG.getRuleFont();
        cssClass = "rule";
        shape = rrDiagramToSVG.getRuleShape();
        break;
      case LITERAL:
        insets = rrDiagramToSVG.getLiteralInsets();
        font = rrDiagramToSVG.getLiteralFont();
        cssClass = "literal";
        shape = rrDiagramToSVG.getLiteralShape();
        break;
      case SPECIAL_SEQUENCE:
        insets = rrDiagramToSVG.getSpecialSequenceInsets();
        font = rrDiagramToSVG.getSpecialSequenceFont();
        cssClass = "special";
        shape = rrDiagramToSVG.getSpecialSequenceShape();
        break;
      default:
        throw new IllegalStateException("Unknown type: " + type);
    }
    switch(shape) {
      case RECTANGLE:
        sb.append("<rect class=\"").append(cssClass).append("\" x=\"").append(xOffset).append("\" y=\"").append(yOffset).append("\" width=\"").append(width).append("\" height=\"").append(height).append("\"/>\n");
        break;
      case ROUNDED_RECTANGLE:
        // Connector may be in rounded area if there are huge margins at top, but this is an unrealistic case so we don't add lines to complete the connector.
        int rx = (insets.left + insets.right + insets.top + insets.bottom) / 4;
        sb.append("<rect class=\"").append(cssClass).append("\" x=\"").append(xOffset).append("\" y=\"").append(yOffset).append("\" width=\"").append(width).append("\" height=\"").append(height).append("\" rx=\"").append(rx).append("\"/>\n");
        break;
      case HEXAGON:
        // We don't calculate the exact length of the connector: it goes behind the shape.
        // We should calculate if we want to support transparent shapes.
        int connectorOffset = layoutInfo.getConnectorOffset();
        sb.append("<line class=\"connector\" x1=\"").append(xOffset).append("\" y1=\"").append(yOffset + connectorOffset).append("\" x2=\"").append(xOffset + insets.left).append("\" y2=\"").append(yOffset + connectorOffset).append("\"/>");
        sb.append("<line class=\"connector\" x1=\"").append(xOffset + width).append("\" y1=\"").append(yOffset + connectorOffset).append("\" x2=\"").append(xOffset + width - insets.right).append("\" y2=\"").append(yOffset + connectorOffset).append("\"/>");
        sb.append("<polygon class=\"").append(cssClass).append("\" points=\"").append(xOffset).append(" ").append(yOffset + height / 2).append(" ").append(xOffset + insets.left).append(" ").append(yOffset).append(" ").append(xOffset + width - insets.right).append(" ").append(yOffset).append(" ").append(xOffset + width).append(" ").append(yOffset + height / 2).append(" ").append(xOffset + width - insets.right).append(" ").append(yOffset + height).append(" ").append(xOffset + insets.left).append(" ").append(yOffset + height).append("\"/>\n");
        break;
    }
    FontRenderContext fontRenderContext = new FontRenderContext(null, true, false);
    Rectangle2D stringBounds = font.getStringBounds(text, fontRenderContext);
    int textXOffset = xOffset + insets.left;
    int textYOffset = yOffset + insets.top + (int)Math.round(stringBounds.getHeight()) - fontYOffset;
    sb.append("<text class=\"").append(cssClass).append("_text\" x=\"").append(textXOffset).append("\" y=\"").append(textYOffset).append("\">").append(Utils.escapeXML(text)).append("</text>");
    if(link != null) {
      sb.append("</a>");
    }
    sb.append("\n");
  }

}
