/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.rrdiagram;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * @author Christopher Deckers
 */
public class RRLoop extends RRElement {

  private RRElement rrElement;
  private RRElement loopElement;
  private int minRepetitionCount;
  private Integer maxRepetitionCount;

  /**
   * @param loopElement can be null.
   */
  public RRLoop(RRElement rrElement, RRElement loopElement) {
    this(rrElement, loopElement, 0, null);
  }

  public RRLoop(RRElement rrElement, RRElement loopElement, int minRepetitionCount, Integer maxRepetitionCount) {
    this.rrElement = rrElement;
    this.loopElement = loopElement;
    if(minRepetitionCount < 0) {
      throw new IllegalArgumentException("Minimum repetition must be positive!");
    }
    if(maxRepetitionCount != null && maxRepetitionCount < minRepetitionCount) {
      throw new IllegalArgumentException("Maximum repetition must not be smaller than minimum!");
    }
    this.minRepetitionCount = minRepetitionCount;
    this.maxRepetitionCount = maxRepetitionCount;
  }

  private String cardinalitiesText;
  private int cardinalitiesWidth;
  private int fontYOffset;

  @Override
  protected void computeLayoutInfo(RRDiagramToSVG rrDiagramToSVG) {
    cardinalitiesText = null;
    cardinalitiesWidth = 0;
    fontYOffset = 0;
    if(minRepetitionCount > 0 || maxRepetitionCount != null) {
      cardinalitiesText = minRepetitionCount + ".." + (maxRepetitionCount == null? "N": maxRepetitionCount);
      FontRenderContext fontRenderContext = new FontRenderContext(null, true, false);
      Font font = new Font("Verdana", Font.PLAIN, 12);
      fontYOffset = Math.round(font.getLineMetrics(cardinalitiesText, fontRenderContext).getDescent());
      Rectangle2D stringBounds = font.getStringBounds(cardinalitiesText, fontRenderContext);
      cardinalitiesWidth = (int)Math.round(stringBounds.getWidth()) + 2;

    }
    rrElement.computeLayoutInfo(rrDiagramToSVG);
    LayoutInfo layoutInfo1 = rrElement.getLayoutInfo();
    int width = layoutInfo1.getWidth();
    int height = layoutInfo1.getHeight();
    int connectorOffset = layoutInfo1.getConnectorOffset();
    if(loopElement != null) {
      loopElement.computeLayoutInfo(rrDiagramToSVG);
      LayoutInfo layoutInfo2 = loopElement.getLayoutInfo();
      width = Math.max(width, layoutInfo2.getWidth());
      int height2 = layoutInfo2.getHeight();
      height += 5 + height2;
      connectorOffset += 5 + height2;
    } else {
      height += 15;
      connectorOffset += 15;
    }
    width += 20 + 20 + cardinalitiesWidth;
    setLayoutInfo(new LayoutInfo(width, height, connectorOffset));
  }

  @Override
  protected void toSVG(RRDiagramToSVG rrDiagramToSVG, int xOffset, int yOffset, StringBuilder sb) {
    LayoutInfo layoutInfo1 = rrElement.getLayoutInfo();
    int width1 = layoutInfo1.getWidth();
    int maxWidth = width1;
    int yOffset2 = yOffset;
    LayoutInfo layoutInfo = getLayoutInfo();
    int connectorOffset = layoutInfo.getConnectorOffset();
    int y1 = yOffset;
    int loopOffset = 0;
    int loopWidth = 0;
    if(loopElement != null) {
      LayoutInfo layoutInfo2 = loopElement.getLayoutInfo();
      loopWidth = layoutInfo2.getWidth();
      maxWidth = Math.max(maxWidth, loopWidth);
      loopOffset = xOffset + 20 + (maxWidth - loopWidth) / 2;
      loopElement.toSVG(rrDiagramToSVG, loopOffset, yOffset, sb);
      yOffset2 += 5 + layoutInfo2.getHeight();
      y1 += layoutInfo2.getConnectorOffset();
    } else {
      yOffset2 += 15;
      y1 += 5;
    }
    rrElement.toSVG(rrDiagramToSVG, xOffset + 20 + (maxWidth - width1) / 2, yOffset2, sb);
    int x1 = xOffset + 10;
    int x2 = xOffset + 20 + maxWidth + 10 + cardinalitiesWidth;
    int y2 = yOffset + connectorOffset;
    sb.append("<line class=\"connector\" x1=\"").append(x1 - 10).append("\" y1=\"").append(y2).append("\" x2=\"").append(x1 + 10 + (maxWidth - width1) / 2).append("\" y2=\"").append(y2).append("\"/>\n");
    sb.append("<path class=\"connector\" d=\"");
    sb.append("M ").append(x1 + 5).append(" ").append(y2);
    sb.append(" Q ").append(x1).append(" ").append(y2).append(" ").append(x1).append(" ").append(y2 - 5);
    sb.append(" V ").append(y1 + 5);
    sb.append(" Q ").append(x1).append(" ").append(y1).append(" ").append(x1 + 5).append(" ").append(y1);
    if(loopElement != null) {
      sb.append(" H ").append(loopOffset);
      sb.append(" M ").append(loopOffset + loopWidth).append(" ").append(y1);
      sb.append(" H ").append(x2 - 5);
    } else {
      sb.append(" H ").append(x2 - 5);
    }
    sb.append(" Q ").append(x2).append(" ").append(y1).append(" ").append(x2).append(" ").append(y1 + 5);
    sb.append(" V ").append(y2 - 5);
    sb.append(" Q ").append(x2).append(" ").append(y2).append(" ").append(x2 - 5).append(" ").append(y2);
    sb.append("\"/>\n");
    sb.append("<line class=\"connector\" x1=\"").append(x2 - cardinalitiesWidth - 10 - (maxWidth - width1) / 2).append("\" y1=\"").append(y2).append("\" x2=\"").append(xOffset + layoutInfo.getWidth()).append("\" y2=\"").append(y2).append("\"/>\n");
    if(cardinalitiesText != null) {
      sb.append("<text class=\"desc\" x=\"").append(x2 - cardinalitiesWidth).append("\" y=\"").append(y2 - fontYOffset - 5).append("\">").append(cardinalitiesText).append("</text>");
    }
  }

}
