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
public class RRBreak extends RRElement {

  @Override
  protected void computeLayoutInfo(RRDiagramToSVG rrDiagramToSVG) {
    throw new IllegalStateException("This element must not be nested and should have been processed before entering generation.");
  }

  @Override
  protected void toSVG(RRDiagramToSVG rrDiagramToSVG, int xOffset, int yOffset, SvgContent svgContent) {
    throw new IllegalStateException("This element must not be nested and should have been processed before entering generation.");
  }

}
