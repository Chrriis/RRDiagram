/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package net.nextencia.rrdiagram.grammar.rrdiagram;

import net.nextencia.rrdiagram.common.Utils;
import net.nextencia.rrdiagram.grammar.rrdiagram.RRDiagram.SvgContent;

/**
 * @author Christopher Deckers
 */
public class RRStartEndShapeElement extends RRElement {

  public static enum StartEndShape {
    EMPTY_CIRCLE,
    FILLED_CIRCLE,
    VERTICAL_LINE,
    DOUBLE_VERTICAL_LINE,
    EMPTY_ARROW,
    FILLED_ARROW,
  }

  private StartEndShape startEndShape;
  private boolean isStart;

  public RRStartEndShapeElement(StartEndShape startEndShape, boolean isStart) {
    this.startEndShape = startEndShape;
    this.isStart = isStart;
  }

  public StartEndShape getStartEndShape() {
    return startEndShape;
  }

  public boolean isStart() {
    return isStart;
  }

  @Override
  protected void computeLayoutInfo(RRDiagramToSVG rrDiagramToSVG) {
    int width = 10;
    int height = 10;
    switch (startEndShape) {
      case EMPTY_CIRCLE:
      case FILLED_CIRCLE:
        width = 10;
        height = 10;
        break;
      case DOUBLE_VERTICAL_LINE:
        width = 4;
        height = 10;
        break;
      case VERTICAL_LINE:
        width = 1;
        height = 10;
        break;
      case EMPTY_ARROW:
      case FILLED_ARROW:
        width = 8;
        height = 10;
        break;
    }
    setLayoutInfo(new LayoutInfo(width, height, 0));
  }

  @Override
  protected void toSVG(RRDiagramToSVG rrDiagramToSVG, int xOffset, int yOffset, SvgContent svgContent) {
    LayoutInfo layoutInfo = getLayoutInfo();
    int width = layoutInfo.getWidth();
    int height = layoutInfo.getHeight();
    String connectorColor = Utils.convertColorToHtml(rrDiagramToSVG.getConnectorColor());
    switch (startEndShape) {
      case EMPTY_CIRCLE: {
        double radius = (width - 1) / 2.0;
        String cssClass = svgContent.setCSSClass(isStart? RRDiagram.CSS_CONNECTOR_START_CLASS: RRDiagram.CSS_CONNECTOR_END_CLASS, "fill:none;stroke:" + connectorColor + ";");
        svgContent.addElement("<ellipse class=\"" + cssClass + "\" cx=\"" + (xOffset + 0.5 + radius) +"\" cy=\"" + yOffset + "\" rx=\"" + radius + "\" ry=\"" + radius + "\"/>");
        break;
      }
      case FILLED_CIRCLE: {
        double radius = (width - 1) / 2.0;
        String cssClass = svgContent.setCSSClass(isStart? RRDiagram.CSS_CONNECTOR_START_CLASS: RRDiagram.CSS_CONNECTOR_END_CLASS, "fill:" + connectorColor + ";stroke:" + connectorColor + ";");
        svgContent.addElement("<ellipse class=\"" + cssClass + "\" cx=\"" + (xOffset + 0.5 + radius) +"\" cy=\"" + yOffset + "\" rx=\"" + radius + "\" ry=\"" + radius + "\"/>");
        break;
      }
      case DOUBLE_VERTICAL_LINE: {
        double radius = height / 2.0;
        String cssClass = svgContent.setCSSClass(isStart? RRDiagram.CSS_CONNECTOR_START_CLASS: RRDiagram.CSS_CONNECTOR_END_CLASS, "stroke:" + connectorColor + ";");
        double x = xOffset;
        if(isStart) {
          x++;
        }
        svgContent.addElement("<line class=\"" + cssClass + "\" x1=\"" + x + "\" x2=\"" + x + "\" y1=\"" + (yOffset - radius) + "\" y2=\"" + (yOffset + radius) + "\"/>");
        svgContent.addElement("<line class=\"" + cssClass + "\" x1=\"" + (x + width - 1) + "\" x2=\"" + (x + width - 1) + "\" y1=\"" + (yOffset - radius) + "\" y2=\"" + (yOffset + radius) + "\"/>");
        break;
      }
      case VERTICAL_LINE: {
        double radius = height / 2.0;
        String cssClass = svgContent.setCSSClass(isStart? RRDiagram.CSS_CONNECTOR_START_CLASS: RRDiagram.CSS_CONNECTOR_END_CLASS, "stroke:" + connectorColor + ";");
        double x = xOffset;
        if(isStart) {
          x++;
        }
        svgContent.addElement("<line class=\"" + cssClass + "\" x1=\"" + x + "\" x2=\"" + x + "\" y1=\"" + (yOffset - radius) + "\" y2=\"" + (yOffset + radius) + "\"/>");
        break;
      }
      case EMPTY_ARROW: {
        double radius = height / 2.0;
        String cssClass = svgContent.setCSSClass(isStart? RRDiagram.CSS_CONNECTOR_START_CLASS: RRDiagram.CSS_CONNECTOR_END_CLASS, "fill:none;stroke:" + connectorColor + ";");
        svgContent.addElement("<polygon class=\"" + cssClass + "\" points=\"" + xOffset + "," + (yOffset - radius) + " " + xOffset + "," + (yOffset + radius) + " " + (xOffset + width) + "," + yOffset + "\"/>");
        break;
      }
      case FILLED_ARROW: {
        double radius = height / 2.0;
        String cssClass = svgContent.setCSSClass(isStart? RRDiagram.CSS_CONNECTOR_START_CLASS: RRDiagram.CSS_CONNECTOR_END_CLASS, "stroke:" + connectorColor + ";");
        svgContent.addElement("<polygon class=\"" + cssClass + "\" points=\"" + xOffset + "," + (yOffset - radius) + " " + xOffset + "," + (yOffset + radius) + " " + (xOffset + width) + "," + yOffset + "\"/>");
        break;
      }
    }
  }

}
