/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.rrdiagram;

/**
 * @author Christopher Deckers
 */
public class RRSequence extends RRElement {

  private RRElement[] rrElements;

  public RRSequence(RRElement... rrElements) {
    this.rrElements = rrElements;
  }

  public RRElement[] getRRElements() {
    return rrElements;
  }

  @Override
  protected void computeLayoutInfo(RRDiagramToSVG rrDiagramToSVG) {
    int width = 0;
    int aboveConnector = 0;
    int belowConnector = 0;
    for (int i = 0; i < rrElements.length; i++) {
      RRElement rrElement = rrElements[i];
      rrElement.computeLayoutInfo(rrDiagramToSVG);
      if(i > 0) {
        width += 10;
      }
      LayoutInfo layoutInfo = rrElement.getLayoutInfo();
      width += layoutInfo.getWidth();
      int height = layoutInfo.getHeight();
      int connectorOffset = layoutInfo.getConnectorOffset();
      aboveConnector = Math.max(aboveConnector, connectorOffset);
      belowConnector = Math.max(belowConnector, height - connectorOffset);
    }
    setLayoutInfo(new LayoutInfo(width, aboveConnector + belowConnector, aboveConnector));
  }

  @Override
  protected void toSVG(RRDiagramToSVG rrDiagramToSVG, int xOffset, int yOffset, StringBuilder sb) {
    LayoutInfo layoutInfo = getLayoutInfo();
    int connectorOffset = layoutInfo.getConnectorOffset();
    int widthOffset = 0;
    for (int i = 0; i < rrElements.length; i++) {
      RRElement rrElement = rrElements[i];
      LayoutInfo layoutInfo2 = rrElement.getLayoutInfo();
      int width2 = layoutInfo2.getWidth();
      int connectorOffset2 = layoutInfo2.getConnectorOffset();
      int xOffset2 = widthOffset + xOffset;
      int yOffset2 = yOffset + connectorOffset - connectorOffset2;
      if(i > 0) {
        sb.append("<line class=\"connector\" x1=\"").append(xOffset2 - 10).append("\" y1=\"").append(yOffset + connectorOffset).append("\" x2=\"").append(xOffset2).append("\" y2=\"").append(yOffset + connectorOffset).append("\"/>\n");
      }
      rrElement.toSVG(rrDiagramToSVG, xOffset2, yOffset2, sb);
      widthOffset += 10;
      widthOffset += width2;
    }
  }

}
