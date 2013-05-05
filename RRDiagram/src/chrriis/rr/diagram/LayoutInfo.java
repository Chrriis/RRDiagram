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
public class LayoutInfo {

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
