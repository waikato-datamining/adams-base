/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Anchored.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.label;

import adams.core.QuickInfoHelper;
import adams.data.image.ImageAnchor;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.gui.core.Fonts;
import adams.gui.visualization.object.objectannotations.AnnotationUtils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Plots the label relative to the anchor.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Anchored
    extends AbstractLabelPlotter {

  private static final long serialVersionUID = -2032898070308967178L;

  /** what to use as reference for the anchor. */
  public enum AnchorReference {
    BOUNDING_BOX,
    POLYGON_BOUNDS,
  }

  /** the key in the meta-data that contains the type. */
  protected String m_MetaDataKey;

  /** the label for the rectangles. */
  protected String m_Format;

  /** the label font. */
  protected Font m_Font;

  /** the anchor reference. */
  protected AnchorReference m_Reference;

  /** the label anchor. */
  protected ImageAnchor m_Anchor;

  /** the x offset for the label. */
  protected int m_OffsetX;

  /** the y offset for the label. */
  protected int m_OffsetY;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Plots the label relative to the anchor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "meta-data-key", "metaDataKey",
        "type");

    m_OptionManager.add(
	"format", "format",
	"#. $");

    m_OptionManager.add(
	"font", "font",
	Fonts.getSansFont(14));

    m_OptionManager.add(
        "reference", "reference",
        AnchorReference.BOUNDING_BOX);

    m_OptionManager.add(
	"anchor", "anchor",
	ImageAnchor.TOP_LEFT);

    m_OptionManager.add(
	"offset-x", "offsetX",
	0);

    m_OptionManager.add(
	"offset-y", "offsetY",
	0);
  }

  /**
   * Sets the key in the meta-data of the object that contains the type.
   *
   * @param value 	the key
   */
  public void setMetaDataKey(String value) {
    m_MetaDataKey = value;
    reset();
  }

  /**
   * Returns the key in the meta-data of the object that contains the type.
   *
   * @return 		the key
   */
  public String getMetaDataKey() {
    return m_MetaDataKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataKeyTipText() {
    return "The key in the meta-data of the object that contains the type.";
  }

  /**
   * Sets the label format.
   *
   * @param value 	the label format
   */
  public void setFormat(String value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the label format.
   *
   * @return 		the label format
   */
  public String getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The label format string to use for the rectangles; "
	+ "'#' for index, '@' for type and '$' for short type (type suffix "
	+ "must be defined for '@' and '$'), '{BLAH}' gets replaced with the "
	+ "value associated with the meta-data key 'BLAH'; "
	+ "for instance: '# @' or '# {BLAH}'; in case of numeric values, use '|.X' "
	+ "to limit the number of decimals, eg '{BLAH|.2}' for a maximum of decimals "
	+ "after the decimal point.";
  }

  /**
   * Sets the label font.
   *
   * @param value 	the label font
   */
  public void setFont(Font value) {
    m_Font = value;
    reset();
  }

  /**
   * Returns the label font.
   *
   * @return 		the label font
   */
  public Font getFont() {
    return m_Font;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontTipText() {
    return "The font to use for the labels.";
  }

  /**
   * Sets the reference for the anchor.
   *
   * @param value 	the reference
   */
  public void setReference(AnchorReference value) {
    m_Reference = value;
    reset();
  }

  /**
   * Returns the reference for the anchor.
   *
   * @return 		the reference
   */
  public AnchorReference getReference() {
    return m_Reference;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String referenceTipText() {
    return "The reference rectangle to use for the label anchor.";
  }

  /**
   * Sets the anchor for the label.
   *
   * @param value 	the anchor
   */
  public void setAnchor(ImageAnchor value) {
    m_Anchor = value;
    reset();
  }

  /**
   * Returns the anchor for the label.
   *
   * @return 		the anchor
   */
  public ImageAnchor getAnchor() {
    return m_Anchor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String anchorTipText() {
    return "The anchor for the label.";
  }

  /**
   * Sets the X offset for the label.
   *
   * @param value 	the X offset
   */
  public void setOffsetX(int value) {
    m_OffsetX = value;
    reset();
  }

  /**
   * Returns the X offset for the label.
   *
   * @return 		the X offset
   */
  public int getOffsetX() {
    return m_OffsetX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String offsetXTipText() {
    return "The X offset for the label; values of 0 or greater are interpreted as absolute pixels, -1 uses left as anchor, -2 the center and -3 the right.";
  }

  /**
   * Sets the Y offset for the label.
   *
   * @param value 	the Y offset
   */
  public void setOffsetY(int value) {
    m_OffsetY = value;
    reset();
  }

  /**
   * Returns the Y offset for the label.
   *
   * @return 		the Y offset
   */
  public int getOffsetY() {
    return m_OffsetY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String offsetYTipText() {
    return "The Y offset for the label values of 0 or greater are interpreted as absolute pixels, -1 uses top as anchor, -2 the middle and -3 the bottom.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return null if no info available, otherwise short string
   */
  @Override
  protected String generateQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "metaDataKey", m_MetaDataKey, "key: ");
    result += QuickInfoHelper.toString(this, "format", m_Format, ", format: ");
    result += QuickInfoHelper.toString(this, "anchor", m_Anchor, ", anchor: ");

    return result;
  }

  /**
   * Plots the label.
   *
   * @param object the object to plot
   * @param color  the color to use
   * @param g      the graphics context
   */
  @Override
  protected void doPlotLabel(LocatedObject object, Color color, Graphics2D g) {
    String	label;
    Rectangle   rect;

    if (m_Format.isEmpty())
      return;

    label = AnnotationUtils.applyLabelFormat(object, m_MetaDataKey, m_Format);
    if (label.isEmpty())
      return;

    switch (m_Reference) {
      case BOUNDING_BOX:
        rect = object.getRectangle();
        break;
      case POLYGON_BOUNDS:
        if (object.hasPolygon())
          rect = object.getPolygon().getBounds();
        else
          rect = object.getRectangle();
        break;
      default:
        throw new IllegalStateException("Unhandled anchor reference: " + m_Reference);
    }

    g.setColor(color);
    AnnotationUtils.drawString(g, rect, m_Anchor, label, m_OffsetX, m_OffsetY, m_Font);
  }
}
