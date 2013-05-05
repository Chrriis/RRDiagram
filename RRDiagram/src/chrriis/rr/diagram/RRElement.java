/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.rr.diagram;

/**
 * @author Christopher Deckers
 */
public abstract class RRElement {

  private LayoutInfo layoutInfo;

  public void setLayoutInfo(LayoutInfo layoutInfo) {
    this.layoutInfo = layoutInfo;
  }

  public LayoutInfo getLayoutInfo() {
    return layoutInfo;
  }

  protected abstract void computeLayoutInfo(RRDiagramToSVG rrDiagramToSVG);

  protected abstract void toSVG(RRDiagramToSVG rrDiagramToSVG, int xOffset, int yOffset, StringBuilder sb);

}
