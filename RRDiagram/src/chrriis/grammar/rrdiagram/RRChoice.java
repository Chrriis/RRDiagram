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
  protected void toSVG(RRDiagramToSVG rrDiagramToSVG, int xOffset, int yOffset, StringBuilder sb) {
    LayoutInfo layoutInfo = getLayoutInfo();
    int y1 = yOffset + layoutInfo.getConnectorOffset();
    int x1 = xOffset + 10;
    int x2 = xOffset + layoutInfo.getWidth() - 10;
    int y2 = 0;
    int xOffset2 = xOffset + 20;
    int yOffset2 = yOffset;
    for (int i = 0; i < rrElements.length; i++) {
      RRElement rrElement = rrElements[i];
      LayoutInfo layoutInfo2 = rrElement.getLayoutInfo();
      int width = layoutInfo2.getWidth();
      int height = layoutInfo2.getHeight();
      rrElement.toSVG(rrDiagramToSVG, xOffset2, yOffset2, sb);
      y2 = yOffset2 + layoutInfo2.getConnectorOffset();
      if(i == 0) {
        sb.append("<line class=\"connector\" x1=\"").append(x1 - 10).append("\" y1=\"").append(y1).append("\" x2=\"").append(x1 + 10).append("\" y2=\"").append(y1).append("\"/>\n");
        sb.append("<line class=\"connector\" x1=\"").append(xOffset2 + width).append("\" y1=\"").append(y2).append("\" x2=\"").append(x2 + 10).append("\" y2=\"").append(y2).append("\"/>\n");
      } else {
        sb.append("<path class=\"connector\" d=\"");
        sb.append("M ").append(x1).append(" ").append(y2 - 5);
        sb.append(" Q ").append(x1).append(" ").append(y2).append(" ").append(x1 + 5).append(" ").append(y2);
        sb.append(" H ").append(xOffset2);
        sb.append("\"/>\n");
        sb.append("<path class=\"connector\" d=\"");
        sb.append("M ").append(x2).append(" ").append(y2 - 5);
        sb.append(" Q ").append(x2).append(" ").append(y2).append(" ").append(x2 - 5).append(" ").append(y2);
        sb.append(" H ").append(xOffset2 + width);
        sb.append("\"/>\n");
      }
      yOffset2 += height + 5;
    }
    sb.append("<path class=\"connector\" d=\"");
    sb.append("M ").append(x1 - 5).append(" ").append(y1);
    sb.append(" Q ").append(x1).append(" ").append(y1).append(" ").append(x1).append(" ").append(y1 + 5);
    sb.append(" V ").append(y2 - 5);
    sb.append("\"/>\n");
    sb.append("<path class=\"connector\" d=\"");
    sb.append("M ").append(x2 + 5).append(" ").append(y1);
    sb.append(" Q ").append(x2).append(" ").append(y1).append(" ").append(x2).append(" ").append(y1 + 5);
    sb.append(" V ").append(y2 - 5);
    sb.append("\"/>\n");
  }

}
