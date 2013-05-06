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
public abstract class RRElement {

  protected static class LayoutInfo {

    private int width;
    private int height;
    private int connectorOffset;

    public LayoutInfo(int width, int height, int connectorOffset) {
      this.width = width;
      this.height = height;
      this.connectorOffset = connectorOffset;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }

    public int getConnectorOffset() {
      return connectorOffset;
    }

  }

  private LayoutInfo layoutInfo;

  public void setLayoutInfo(LayoutInfo layoutInfo) {
    this.layoutInfo = layoutInfo;
  }

  public LayoutInfo getLayoutInfo() {
    return layoutInfo;
  }

  protected abstract void computeLayoutInfo(RRDiagramToSVG rrDiagramToSVG);

  protected abstract void toSVG(RRDiagramToSVG rrDiagramToSVG, int xOffset, int yOffset, SvgContent svgContent);

}
