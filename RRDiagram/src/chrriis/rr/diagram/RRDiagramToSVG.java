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
public class RRDiagramToSVG {

  public String convert(RRDiagram rrDiagram) {
    return rrDiagram.toSVG(this);
  }

}
