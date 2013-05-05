/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.rr.diagram;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * @author Christopher Deckers
 */
public class RRText extends RRElement {

  public static enum Type {
    LITERAL,
    RULE,
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

  private int fontOffset;

  @Override
  protected void computeLayoutInfo(RRDiagramToSVG rrDiagramToSVG) {
    FontRenderContext fontRenderContext = new FontRenderContext(null, true, false);
    Font font = new Font("Verdana", Font.PLAIN, 12);
    fontOffset = (int)Math.round(font.getLineMetrics(text, fontRenderContext).getDescent());
    Rectangle2D stringBounds = font.getStringBounds(text, fontRenderContext);
    int width = (int)Math.round(stringBounds.getWidth());
    int height = (int)Math.round(stringBounds.getHeight());
    int connectorOffset = 5 + height - fontOffset;
    width += 10 + 10;
    height += 5 + 5;
    setLayoutInfo(new LayoutInfo(width, height, connectorOffset));
  }

  @Override
  protected void toSVG(RRDiagramToSVG rrDiagramToSVG, int xOffset, int yOffset, StringBuilder sb) {
    LayoutInfo layoutInfo = getLayoutInfo();
    int width = layoutInfo.getWidth();
    int height = layoutInfo.getHeight();
    if(link != null) {
      sb.append("<a xlink:href=\"").append(link)/*.append("\" xlink:title=\"").append(text)*/.append("\">");
    }
    switch(type) {
      case RULE:
        sb.append("<rect class=\"rule\" x=\"").append(xOffset).append("\" y=\"").append(yOffset).append("\" width=\"").append(width).append("\" height=\"").append(height).append("\"/>\n");
        break;
      case LITERAL:
        sb.append("<rect class=\"literal\" x=\"").append(xOffset).append("\" y=\"").append(yOffset).append("\" width=\"").append(width).append("\" height=\"").append(height).append("\" rx=\"10\"/>\n");
        break;
        // TODO: support other shapes like polygons?
    }
    FontRenderContext fontRenderContext = new FontRenderContext(null, true, false);
    Rectangle2D stringBounds = new Font("Verdana", Font.PLAIN, 12).getStringBounds(text, fontRenderContext);
    int textXOffset = xOffset + 10;
    int textYOffset = yOffset + 5 + (int)Math.round(stringBounds.getHeight()) - fontOffset;
    sb.append("<text class=\"desc\" x=\"").append(textXOffset).append("\" y=\"").append(textYOffset).append("\">").append(text).append("</text>");
    if(link != null) {
      sb.append("</a>");
    }
    sb.append("\n");
  }

}
