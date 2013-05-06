/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.rrdiagram;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christopher Deckers
 */
public class RRDiagram {

  private RRElement rrElement;

  public RRDiagram(RRElement rrElement) {
    this.rrElement = rrElement;
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
    StringBuilder sb = new StringBuilder();
    sb.append("<svg xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns=\"http://www.w3.org/2000/svg\" width=\"").append(width).append("\" height=\"").append(height).append("\"><defs>");
    sb.append("<style type=\"text/css\">\n");
    sb.append(".connector {fill: none; stroke: #222222;}\n");
    sb.append(".rule {fill: #D3F0FF; stroke: #222222;}\n");
    sb.append(".literal {fill: #90D9FF; stroke: #222222;}\n");
    sb.append(".special {fill: #E4F4FF; stroke: #222222;}\n");
    sb.append(".desc {font-family: Verdana, Sans-serif; font-size: 12px;}\n");
    sb.append("</style>");
    sb.append("</defs>\n");
    int xOffset = 0;
    int yOffset = 5;
//    sb.append("<rect fill=\"#FFFFFF\" stroke=\"#FF0000\" x1=\"0\" y1=\"0\" width=\"").append(width).append("\" height=\"").append(height).append("\"/>\n");
    for(RRElement rrElement: rrElementList) {
      LayoutInfo layoutInfo2 = rrElement.getLayoutInfo();
      int connectorOffset2 = layoutInfo2.getConnectorOffset();
      int width2 = layoutInfo2.getWidth();
      int height2 = layoutInfo2.getHeight();
      int y1 = yOffset + connectorOffset2;
      sb.append("<line class=\"connector\" x1=\"").append(xOffset).append("\" y1=\"").append(y1).append("\" x2=\"").append(xOffset + 5).append("\" y2=\"").append(y1).append("\"/>\n");
      sb.append("<line class=\"connector\" x1=\"").append(xOffset + 5 + width2).append("\" y1=\"").append(y1).append("\" x2=\"").append(xOffset + 5 + width2 + 5).append("\" y2=\"").append(y1).append("\"/>\n");
      // TODO: add decorations (like arrows)?
      rrElement.toSVG(rrDiagramToSVG, xOffset + 5, yOffset, sb);
      yOffset += height2 + 10;
    }
    sb.append("</svg>");
    return sb.toString();
  }

}
