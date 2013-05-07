/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.rrdiagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chrriis.common.Utils;
import chrriis.grammar.rrdiagram.RRElement.LayoutInfo;

/**
 * @author Christopher Deckers
 */
public class RRDiagram {

  private RRElement rrElement;

  public RRDiagram(RRElement rrElement) {
    this.rrElement = rrElement;
  }

  private static final String SVG_ELEMENTS_SEPARATOR = "";//\n";
  private static final String CSS_CONNECTOR_CLASS = "c";
  static final String CSS_RULE_CLASS = "r";
  static final String CSS_RULE_TEXT_CLASS = "i";
  static final String CSS_LITERAL_CLASS = "l";
  static final String CSS_LITERAL_TEXT_CLASS = "j";
  static final String CSS_SPECIAL_SEQUENCE_CLASS = "s";
  static final String CSS_SPECIAL_SEQUENCE_TEXT_CLASS = "k";
  static final String CSS_LOOP_CARDINALITIES_TEXT_CLASS = "u";

  private static abstract class SvgConnector {
  }

  private static class SvgPath extends SvgConnector {
    private StringBuilder pathSB = new StringBuilder();
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    public SvgPath(int startX, int startY, String path, int endX, int endY) {
      this.startX = startX;
      this.startY = startY;
      pathSB.append(path);
      this.endX = endX;
      this.endY = endY;
    }
    public void addPath(int x1, int y1, String path, int x2, int y2) {
      if(x1 != this.endX || y1 != this.endY) {
        if(x1 == this.endX && y1 == this.endY + 1) {
          pathSB.append("v").append(y1 - y2);
        } else if(y1 == this.endY && x1 == this.endX + 1) {
          pathSB.append("h").append(x1 - x2);
        } else {
          pathSB.append("m").append(x1 - endX);
          if(y1 - endY >= 0) {
            pathSB.append(" ");
          }
          pathSB.append(y1 - endY);
        }
      }
      pathSB.append(path);
      this.endX = x2;
      this.endY = y2;
    }
    public void addPath(SvgPath svgPath) {
      addPath(svgPath.startX, svgPath.startY, svgPath.getPath(), svgPath.endX, svgPath.endY);
    }
    public void addLine(SvgLine svgLine) {
      int x1 = svgLine.getX1();
      int y1 = svgLine.getY1();
      int x2 = svgLine.getX2();
      int y2 = svgLine.getY2();
      if(x1 == x2 && endX == x1) {
        if(endY == y1 || endY == y1 - 1) {
          pathSB.append("v").append(y2 - endY);
          endY = y2;
          return;
        }
        if(endY == y2 || endY == y2 + 1) {
          pathSB.append("v").append(y1 - endY);
          endY = y1;
          return;
        }
      } else if(y1 == y2 && endY == y1) {
        if(endX == x1 || endX == x1 - 1) {
          pathSB.append("h").append(x2 - endX);
          endX = x2;
          return;
        }
        if(endX == x2 || endX == x2 + 1) {
          pathSB.append("h").append(x1 - endX);
          endX = x1;
          return;
        }
      }
      pathSB.append("m").append(x1 - endX);
      if(y1 - endY >= 0) {
        pathSB.append(" ");
      }
      pathSB.append(y1 - endY);
      if(x1 == x2) {
        pathSB.append("v").append(y2 - y1);
      } else if(y1 == y2) {
        pathSB.append("h").append(x2 - x1);
      } else {
        pathSB.append("l").append(x2 - x1);
        if(y2 - y1 >= 0) {
          pathSB.append(" ");
        }
        pathSB.append(y2 - y1);
      }
      endX = x2;
      endY = y2;
    }
    public String getPath() {
      return pathSB.toString();
    }
  }

  private static class SvgLine extends SvgConnector {
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    public SvgLine(int x1, int y1, int x2, int y2) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
    }
    public int getX1() {
      return x1;
    }
    public int getY1() {
      return y1;
    }
    public int getX2() {
      return x2;
    }
    public int getY2() {
      return y2;
    }
    public boolean mergeLine(int x1, int y1, int x2, int y2) {
      if(x1 == x2 && this.x1 == this.x2 && x1 == this.x1) {
        if(y2 >= this.y1 - 1 && y1 <= this.y2 + 1) {
          this.y1 = Math.min(this.y1, y1);
          this.y2 = Math.max(this.y2, y2);
          return true;
        }
      } else if(y1 == y2 && this.y1 == this.y2 && y1 == this.y1) {
        if(x2 >= this.x1 - 1 && x1 <= this.x2 + 1) {
          this.x1 = Math.min(this.x1, x1);
          this.x2 = Math.max(this.x2, x2);
          return true;
        }
      }
      return false;
    }
  }

  public static class SvgContent {
    private List<SvgConnector> connectorList = new ArrayList<SvgConnector>();
    public void addPathConnector(int x1, int y1, String path, int x2, int y2) {
      Object c = connectorList.isEmpty()? null: connectorList.get(connectorList.size() - 1);
      if(c != null) {
        if(c instanceof SvgPath) {
          ((SvgPath)c).addPath(x1, y1, path, x2, y2);
        } else {
          SvgLine svgLine = (SvgLine)c;
          int x1_ = svgLine.getX1();
          int y1_ = svgLine.getY1();
          int x2_ = svgLine.getX2();
          int y2_ = svgLine.getY2();
          if(x1_ == x2_ && x1 == x1_) {
            if(y2_ == y1 - 1) {
              svgLine.mergeLine(x1_, y1_, x2_, y2_ + 1);
            } else if(y1_ == y1 + 1) {
              svgLine.mergeLine(x1_, y1_ - 1, x2_, y2_);
            }
          } else if(y1_ == y2_ && y1 == y1_) {
            if(x2_ == x1 - 1) {
              svgLine.mergeLine(x1_, y1_, x2_ + 1, y2_);
            } else if(x1_ == x1 + 1) {
              svgLine.mergeLine(x1_ - 1, y1_, x2_, y2_);
            }
          }
          connectorList.add(new SvgPath(x1, y1, path, x2, y2));
        }
      } else {
        connectorList.add(new SvgPath(x1, y1, path, x2, y2));
      }
    }
    public void addLineConnector(int x1, int y1, int x2, int y2) {
      int x1_ = Math.min(x1, x2);
      int y1_ = Math.min(y1, y2);
      int x2_ = Math.max(x1, x2);
      int y2_ = Math.max(y1, y2);
      Object c = connectorList.isEmpty()? null: connectorList.get(connectorList.size() - 1);
      if(c == null || !(c instanceof SvgLine) || !((SvgLine)c).mergeLine(x1_, y1_, x2_, y2_)) {
        connectorList.add(new SvgLine(x1_, y1_, x2_, y2_));
      }
    }
    private String getConnectorElement(RRDiagramToSVG rrDiagramToSVG) {
      if(connectorList.isEmpty()) {
        return "";
      }
      SvgPath path0 = null;
      for(SvgConnector connector: connectorList) {
        if(path0 == null) {
          if(connector instanceof SvgPath) {
            path0 = (SvgPath)connector;
          } else {
            SvgLine svgLine = (SvgLine)connector;
            int x1 = svgLine.getX1();
            int y1 = svgLine.getY1();
            path0 = new SvgPath(x1, y1, "M" + x1 + (y1 < 0? y1: " " + y1), x1, y1);
            path0.addLine(svgLine);
          }
        } else {
          if(connector instanceof SvgPath) {
            path0.addPath((SvgPath)connector);
          } else {
            path0.addLine((SvgLine)connector);
          }
        }
      }
      String connectorColor = Utils.convertColorToHtml(rrDiagramToSVG.getConnectorColor());
      String cssClass = setCSSClass(CSS_CONNECTOR_CLASS, "fill:none;stroke:" + connectorColor + ";");
      return "<path class=\"" + cssClass + "\" d=\""+ path0.getPath() + "\"/>" + SVG_ELEMENTS_SEPARATOR;
    }
    private StringBuilder elementsSB = new StringBuilder();
    public void addElement(String element) {
      elementsSB.append(element).append(SVG_ELEMENTS_SEPARATOR);
    }
    private String getElements() {
      return elementsSB.toString();
    }
    private Map<String, String> cssClassToDefinitionMap = new HashMap<String, String>();
    public String getDefinedCSSClass(String style) {
      String definition = cssClassToDefinitionMap.get(style);
      return definition == null? null: definition.endsWith(";")? style: definition;
    }
    /**
     * @return the name of a CSS class to use, which can be different from the cssClass parameter if they share the same definition.
     */
    public String setCSSClass(String cssClass, String definition) {
      definition = definition.trim();
      if(!definition.endsWith(";")) {
        throw new IllegalArgumentException("The definition is not well formed, it does not end with a semi-colon!");
      }
      // The class name is either mapped to a definition ending with a semi-colon, or the name of another class that shares the same definition.
      String pDefinition = cssClassToDefinitionMap.get(cssClass);
      if(pDefinition != null) {
        if(!pDefinition.endsWith(";")) {
          pDefinition = cssClassToDefinitionMap.get(pDefinition);
        }
        if(!definition.equals(pDefinition)) {
          throw new IllegalStateException("The CSS class \"" + cssClass + "\" is already defined, but with a different definition!");
        }
      } else {
        for(Map.Entry<String, String> e: cssClassToDefinitionMap.entrySet()) {
          if(e.getValue().equals(definition)) {
            String redirectCssClass = e.getKey();
            cssClassToDefinitionMap.put(cssClass, redirectCssClass);
            return redirectCssClass;
          }
        }
        cssClassToDefinitionMap.put(cssClass, definition);
      }
      return cssClass;
    }
    private String getCSSStyles() {
      StringBuilder sb = new StringBuilder();
      String[] cssClasses = cssClassToDefinitionMap.keySet().toArray(new String[0]);
      Arrays.sort(cssClasses);
      for(int i=0; i<cssClasses.length; i++) {
        if(sb.length() > 0) {
          sb.append(SVG_ELEMENTS_SEPARATOR);
        }
        String cssClass = cssClasses[i];
        String definition = cssClassToDefinitionMap.get(cssClass);
        if(definition.endsWith(";")) {
          sb.append(".").append(cssClass).append("{").append(definition).append("}");
        }
      }
      return sb.toString();
    }
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
    SvgContent svgContent = new SvgContent();
    // First, generate the XML for the elements, to know the usage.
    int xOffset = 0;
    int yOffset = 5;
    for(RRElement rrElement: rrElementList) {
      LayoutInfo layoutInfo2 = rrElement.getLayoutInfo();
      int connectorOffset2 = layoutInfo2.getConnectorOffset();
      int width2 = layoutInfo2.getWidth();
      int height2 = layoutInfo2.getHeight();
      int y1 = yOffset + connectorOffset2;
      svgContent.addLineConnector(xOffset, y1, xOffset + 5, y1);
      // TODO: add decorations (like arrows)?
      rrElement.toSVG(rrDiagramToSVG, xOffset + 5, yOffset, svgContent);
      svgContent.addLineConnector(xOffset + 5 + width2, y1, xOffset + 5 + width2 + 5, y1);
      yOffset += height2 + 10;
    }
    String connectorElement = svgContent.getConnectorElement(rrDiagramToSVG);
    String elements = svgContent.getElements();
    // Then generate the rest (CSS and SVG container tags) based on that usage.
    StringBuilder sb = new StringBuilder();
    sb.append("<svg version=\"1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns=\"http://www.w3.org/2000/svg\" width=\"").append(width).append("\" height=\"").append(height).append("\">").append(SVG_ELEMENTS_SEPARATOR);
    String styles = svgContent.getCSSStyles();
    if(styles.length() > 0) {
      sb.append("<defs><style type=\"text/css\">").append(SVG_ELEMENTS_SEPARATOR);
      sb.append(styles).append(SVG_ELEMENTS_SEPARATOR);
      sb.append("</style></defs>").append(SVG_ELEMENTS_SEPARATOR);
    }
    sb.append(connectorElement);
    sb.append(elements);
    sb.append("</svg>");
    return sb.toString();
  }

}
