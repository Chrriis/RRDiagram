/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.rrdiagram;

import chrriis.grammar.rrdiagram.RRDiagram.SvgContent;

/**
 * @author Christopher Deckers
 */
public class RRChoice extends RRElement {

  private RRElement[] rrElements;

  public RRChoice(RRElement... rrElements) {
    this.rrElements = rrElements;
  }

  @Override
  protected void computeLayoutInfo(RRDiagramToSVG rrDiagramToSVG) {
    int width = 0;
    int height = 0;
    int connectorOffset = 0;
    for (int i = 0; i < rrElements.length; i++) {
      RRElement rrElement = rrElements[i];
      rrElement.computeLayoutInfo(rrDiagramToSVG);
      LayoutInfo layoutInfo = rrElement.getLayoutInfo();
      if(i == 0) {
        connectorOffset = layoutInfo.getConnectorOffset();
      } else {
        height += 5;
      }
      height += layoutInfo.getHeight();
      width = Math.max(width, layoutInfo.getWidth());
    }
    width += 20 + 20;
    setLayoutInfo(new LayoutInfo(width, height, connectorOffset));
  }

  @Override
  protected void toSVG(RRDiagramToSVG rrDiagramToSVG, int xOffset, int yOffset, SvgContent svgContent) {
    LayoutInfo layoutInfo = getLayoutInfo();
    int y1 = yOffset + layoutInfo.getConnectorOffset();
    int x1 = xOffset + 10;
    int x2 = xOffset + layoutInfo.getWidth() - 10;
    int xOffset2 = xOffset + 20;
    int y2 = 0;
    int yOffset2 = yOffset;
    for (int i = 0; i < rrElements.length; i++) {
      RRElement rrElement = rrElements[i];
      LayoutInfo layoutInfo2 = rrElement.getLayoutInfo();
      int width = layoutInfo2.getWidth();
      int height = layoutInfo2.getHeight();
      y2 = yOffset2 + layoutInfo2.getConnectorOffset();
      if(i == 0) {
        // Line to first element
        svgContent.addLineConnector(x1 - 10, y1, x1 + 10, y1);
      } else {
        if(i == rrElements.length - 1) {
          // Curve and vertical down
          svgContent.addPathConnector(x1 - 5, y1, "q5 0 5 5", x1, y1 + 5);
          svgContent.addLineConnector(x1, y1 + 5, x1, y2 - 5);
        }
        // Curve and horizontal line to element
        svgContent.addPathConnector(x1, y2 - 5, "q0 5 5 5", x1 + 5, y2);
        svgContent.addLineConnector(x1 + 5, y2, xOffset2, y2);
      }
      rrElement.toSVG(rrDiagramToSVG, xOffset2, yOffset2, svgContent);
      if(i == 0) {
        // Line to first element
        svgContent.addLineConnector(xOffset2 + width, y2, x2 + 10, y2);
      } else {
        // Horizontal line to element and curve
        svgContent.addLineConnector(x2 - 5, y2, xOffset2 + width, y2);
        svgContent.addPathConnector(x2 - 5, y2, "q5 0 5-5", x2, y2 - 5);
        if(i == rrElements.length - 1) {
          // Vertical up and curve
          svgContent.addLineConnector(x2, y2 - 5, x2, y1 + 5);
          svgContent.addPathConnector(x2, y1 + 5, "q0-5 5-5", x2 + 5, y1);
        }
      }
      yOffset2 += height + 5;
    }
  }

}
