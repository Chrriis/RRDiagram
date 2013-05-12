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

import chrriis.common.Utils;
import chrriis.grammar.rrdiagram.RRDiagram.SvgContent;

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
      Font font = rrDiagramToSVG.getLoopFont();
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
  protected void toSVG(RRDiagramToSVG rrDiagramToSVG, int xOffset, int yOffset, SvgContent svgContent) {
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
      yOffset2 += 5 + layoutInfo2.getHeight();
      y1 += layoutInfo2.getConnectorOffset();
    } else {
      yOffset2 += 15;
      y1 += 5;
    }
    int x1 = xOffset + 10;
    int x2 = xOffset + 20 + maxWidth + 10 + cardinalitiesWidth;
    int y2 = yOffset + connectorOffset;
    svgContent.addLineConnector(x1 - 10, y2, x1 + 10 + (maxWidth - width1) / 2, y2);
    int loopPathStartX = x1 + 5;
    svgContent.addPathConnector(x1 + 5, y2, "q-5 0-5-5", x1, y2 - 5);
    svgContent.addLineConnector(x1, y2 - 5, x1, y1 + 5);
    svgContent.addPathConnector(x1, y1 + 5, "q0-5 5-5", x1 + 5, y1);
    if(loopElement != null) {
      svgContent.addLineConnector(x1 + 5, y1, loopOffset, y1);
      loopElement.toSVG(rrDiagramToSVG, loopOffset, yOffset, svgContent);
      loopPathStartX = loopOffset + loopWidth;
    }
    svgContent.addLineConnector(loopPathStartX, y1, x2 - 5, y1);
    svgContent.addPathConnector(x2 - 5, y1, "q5 0 5 5", x2, y1 + 5);
    svgContent.addLineConnector(x2, y1 + 5, x2, y2 - 5);
    svgContent.addPathConnector(x2, y2 - 5, "q0 5-5 5", x2 - 5, y2);
    if(cardinalitiesText != null) {
      String cssClass = svgContent.getDefinedCSSClass(RRDiagram.CSS_LOOP_CARDINALITIES_TEXT_CLASS);
      if(cssClass == null) {
        Font loopFont = rrDiagramToSVG.getLoopFont();
        String loopTextColor = Utils.convertColorToHtml(rrDiagramToSVG.getLoopTextColor());
        cssClass = svgContent.setCSSClass(RRDiagram.CSS_LOOP_CARDINALITIES_TEXT_CLASS, "fill:" + loopTextColor + ";" + Utils.convertFontToCss(loopFont));
      }
      svgContent.addElement("<text class=\"" + cssClass + "\" x=\"" + (x2 - cardinalitiesWidth) + "\" y=\"" + (y2 - fontYOffset - 5) + "\">" + Utils.escapeXML(cardinalitiesText) + "</text>");
    }
    rrElement.toSVG(rrDiagramToSVG, xOffset + 20 + (maxWidth - width1) / 2, yOffset2, svgContent);
    svgContent.addLineConnector(x2 - cardinalitiesWidth - 10 - (maxWidth - width1) / 2, y2, xOffset + layoutInfo.getWidth(), y2);
  }

}
