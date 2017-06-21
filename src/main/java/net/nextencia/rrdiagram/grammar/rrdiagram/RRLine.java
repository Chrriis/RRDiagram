/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package net.nextencia.rrdiagram.grammar.rrdiagram;

import net.nextencia.rrdiagram.grammar.rrdiagram.RRDiagram.SvgContent;

/**
 * @author Christopher Deckers
 */
public class RRLine extends RRElement {

  @Override
  protected void computeLayoutInfo(RRDiagramToSVG rrDiagramToSVG) {
    setLayoutInfo(new LayoutInfo(0, 10, 5));
  }

  @Override
  protected void toSVG(RRDiagramToSVG rrDiagramToSVG, int xOffset, int yOffset, SvgContent svgContent) {
  }

}
