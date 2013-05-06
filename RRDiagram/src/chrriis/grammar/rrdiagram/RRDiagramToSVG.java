/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.rrdiagram;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

/**
 * @author Christopher Deckers
 */
public class RRDiagramToSVG {

  public String convert(RRDiagram rrDiagram) {
    return rrDiagram.toSVG(this);
  }

  private Color connectorColor = new Color(34, 34, 34);

  public void setConnectorColor(Color connectorColor) {
    this.connectorColor = connectorColor;
  }

  public Color getConnectorColor() {
    return connectorColor;
  }

  private Font loopFont = new Font("Verdana", Font.PLAIN, 10);

  public void setLoopFont(Font loopFont) {
    this.loopFont = loopFont;
  }

  public Font getLoopFont() {
    return loopFont;
  }

  private Color loopTextColor = Color.BLACK;

  public void setLoopTextColor(Color loopTextColor) {
    this.loopTextColor = loopTextColor;
  }

  public Color getLoopTextColor() {
    return loopTextColor;
  }

  public static enum BoxShape {
    RECTANGLE,
    ROUNDED_RECTANGLE,
    HEXAGON
  }

  private Insets ruleInsets = new Insets(5, 10, 5, 10);

  public void setRuleInsets(Insets ruleInsets) {
    this.ruleInsets = ruleInsets;
  }

   public Insets getRuleInsets() {
    return ruleInsets;
  }

  private Font ruleFont = new Font("Verdana", Font.PLAIN, 12);

  public void setRuleFont(Font ruleFont) {
    this.ruleFont = ruleFont;
  }

  public Font getRuleFont() {
    return ruleFont;
  }

  private Color ruleTextColor = Color.BLACK;

  public void setRuleTextColor(Color ruleTextColor) {
    this.ruleTextColor = ruleTextColor;
  }

  public Color getRuleTextColor() {
    return ruleTextColor;
  }

  private BoxShape ruleShape = BoxShape.RECTANGLE;

  public void setRuleShape(BoxShape ruleShape) {
    this.ruleShape = ruleShape;
  }

  public BoxShape getRuleShape() {
    return ruleShape;
  }

  private Color ruleBorderColor = connectorColor;

  public void setRuleBorderColor(Color ruleBorderColor) {
    this.ruleBorderColor = ruleBorderColor;
  }

  public Color getRuleBorderColor() {
    return ruleBorderColor;
  }

  private Color ruleFillColor = new Color(211, 240, 255);

  public void setRuleFillColor(Color ruleFillColor) {
    this.ruleFillColor = ruleFillColor;
  }

  public Color getRuleFillColor() {
    return ruleFillColor;
  }

  private Insets literalInsets = new Insets(5, 10, 5, 10);

  public void setLiteralInsets(Insets literalInsets) {
    this.literalInsets = literalInsets;
  }

   public Insets getLiteralInsets() {
    return literalInsets;
  }

  private Font literalFont = new Font("Verdana", Font.PLAIN, 12);

  public void setLiteralFont(Font literalFont) {
    this.literalFont = literalFont;
  }

  public Font getLiteralFont() {
    return literalFont;
  }

  private Color literalTextColor = Color.BLACK;

  public void setLiteralTextColor(Color literalTextColor) {
    this.literalTextColor = literalTextColor;
  }

  public Color getLiteralTextColor() {
    return literalTextColor;
  }

  private BoxShape literalShape = BoxShape.ROUNDED_RECTANGLE;

  public void setLiteralShape(BoxShape literalShape) {
    this.literalShape = literalShape;
  }

  public BoxShape getLiteralShape() {
    return literalShape;
  }

  private Color literalBorderColor = connectorColor;

  public void setLiteralBorderColor(Color literalBorderColor) {
    this.literalBorderColor = literalBorderColor;
  }

  public Color getLiteralBorderColor() {
    return literalBorderColor;
  }

  private Color literalFillColor = new Color(144, 217, 255);

  public void setLiteralFillColor(Color literalFillColor) {
    this.literalFillColor = literalFillColor;
  }

  public Color getLiteralFillColor() {
    return literalFillColor;
  }

  private Insets specialSequenceInsets = new Insets(5, 10, 5, 10);

  public void setSpecialSequenceInsets(Insets specialSequenceInsets) {
    this.specialSequenceInsets = specialSequenceInsets;
  }

   public Insets getSpecialSequenceInsets() {
    return specialSequenceInsets;
  }

  private Font specialSequenceFont = new Font("Verdana", Font.PLAIN, 12);

  public void setSpecialSequenceFont(Font specialSequenceFont) {
    this.specialSequenceFont = specialSequenceFont;
  }

  public Font getSpecialSequenceFont() {
    return specialSequenceFont;
  }

  private Color specialSequenceTextColor = Color.BLACK;

  public void setSpecialSequenceTextColor(Color specialSequenceTextColor) {
    this.specialSequenceTextColor = specialSequenceTextColor;
  }

  public Color getSpecialSequenceTextColor() {
    return specialSequenceTextColor;
  }

  private BoxShape specialSequenceShape = BoxShape.HEXAGON;

  public void setSpecialSequenceShape(BoxShape specialSequenceShape) {
    this.specialSequenceShape = specialSequenceShape;
  }

  public BoxShape getSpecialSequenceShape() {
    return specialSequenceShape;
  }

  private Color specialSequenceBorderColor = connectorColor;

  public void setSpecialSequenceBorderColor(Color specialSequenceBorderColor) {
    this.specialSequenceBorderColor = specialSequenceBorderColor;
  }

  public Color getSpecialSequenceBorderColor() {
    return specialSequenceBorderColor;
  }

  private Color specialSequenceFillColor = new Color(228, 244, 255);

  public void setSpecialSequenceFillColor(Color specialSequenceFillColor) {
    this.specialSequenceFillColor = specialSequenceFillColor;
  }

  public Color getSpecialSequenceFillColor() {
    return specialSequenceFillColor;
  }

}
