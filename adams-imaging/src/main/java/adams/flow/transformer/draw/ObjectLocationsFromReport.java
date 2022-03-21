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
 * ObjectLocationsFromReport.java
 * Copyright (C) 2017-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.draw;

import adams.data.image.BufferedImageContainer;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.TranslucentColorProvider;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Displays the locations of objects in the image, using data from the attached report.<br>
 * Suffixes:<br>
 * .x<br>
 * .y<br>
 * .width<br>
 * .height<br>
 * Optionally, if type information is available per object, the locations can be displayed in distinct colors per type. The type itself can be displayed as well.<br>
 * If polygon data should be available (.poly_x and .poly_y), then this takes precedence over the rectangle coordinates.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix of fields in the report to identify as object location, eg 'Object.
 * &nbsp;&nbsp;&nbsp;'.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color to use for the objects.
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 *
 * <pre>-use-colors-per-type &lt;boolean&gt; (property: useColorsPerType)
 * &nbsp;&nbsp;&nbsp;If enabled, individual colors per type are used.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-type-color-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: typeColorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use for the various types.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-type-suffix &lt;java.lang.String&gt; (property: typeSuffix)
 * &nbsp;&nbsp;&nbsp;The suffix of fields in the report to identify the type.
 * &nbsp;&nbsp;&nbsp;default: .type
 * </pre>
 *
 * <pre>-type-regexp &lt;adams.core.base.BaseRegExp&gt; (property: typeRegExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the types must match in order to get drawn (
 * &nbsp;&nbsp;&nbsp;eg only plotting a subset).
 * &nbsp;&nbsp;&nbsp;default: .*
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-label-format &lt;java.lang.String&gt; (property: labelFormat)
 * &nbsp;&nbsp;&nbsp;The label format string to use for the rectangles; '#' for index, '&#64;' for
 * &nbsp;&nbsp;&nbsp;type and '$' for short type (type suffix must be defined for '&#64;' and '$'
 * &nbsp;&nbsp;&nbsp;), '{BLAH}' gets replaced with the value associated with the meta-data key
 * &nbsp;&nbsp;&nbsp;'BLAH'; for instance: '# &#64;' or '# {BLAH}'; in case of numeric values, use
 * &nbsp;&nbsp;&nbsp;'|.X' to limit the number of decimals, eg '{BLAH|.2}' for a maximum of decimals
 * &nbsp;&nbsp;&nbsp;after the decimal point.
 * &nbsp;&nbsp;&nbsp;default: #
 * </pre>
 *
 * <pre>-label-font &lt;java.awt.Font&gt; (property: labelFont)
 * &nbsp;&nbsp;&nbsp;The font to use for the labels.
 * &nbsp;&nbsp;&nbsp;default: Display-PLAIN-14
 * </pre>
 *
 * <pre>-label-anchor &lt;TOP_LEFT|TOP_CENTER|TOP_RIGHT|MIDDLE_LEFT|MIDDLE_CENTER|MIDDLE_RIGHT|BOTTOM_LEFT|BOTTOM_CENTER|BOTTOM_RIGHT&gt; (property: labelAnchor)
 * &nbsp;&nbsp;&nbsp;The anchor for the label.
 * &nbsp;&nbsp;&nbsp;default: TOP_RIGHT
 * </pre>
 *
 * <pre>-label-offset-x &lt;int&gt; (property: labelOffsetX)
 * &nbsp;&nbsp;&nbsp;The X offset for the label; values of 0 or greater are interpreted as absolute
 * &nbsp;&nbsp;&nbsp;pixels, -1 uses left as anchor, -2 the center and -3 the right.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 *
 * <pre>-label-offset-y &lt;int&gt; (property: labelOffsetY)
 * &nbsp;&nbsp;&nbsp;The Y offset for the label values of 0 or greater are interpreted as absolute
 * &nbsp;&nbsp;&nbsp;pixels, -1 uses top as anchor, -2 the middle and -3 the bottom.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 *
 * <pre>-predefined-labels &lt;adams.core.base.BaseString&gt; [-predefined-labels ...] (property: predefinedLabels)
 * &nbsp;&nbsp;&nbsp;The predefined labels to use for setting up the colors; avoids constants
 * &nbsp;&nbsp;&nbsp;changing in color pallet.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-stroke-thickness &lt;float&gt; (property: strokeThickness)
 * &nbsp;&nbsp;&nbsp;The thickness of the stroke.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 *
 * <pre>-filled &lt;boolean&gt; (property: filled)
 * &nbsp;&nbsp;&nbsp;If enabled, the shape is drawn filled.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-polygon-bounds &lt;boolean&gt; (property: polygonBounds)
 * &nbsp;&nbsp;&nbsp;If enabled, the polygon bounds are drawn as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-bounding-box-fallback-ratio &lt;double&gt; (property: boundingBoxFallbackRatio)
 * &nbsp;&nbsp;&nbsp;The threshold for the ratio between the areas (shape &#47; bbox), below which
 * &nbsp;&nbsp;&nbsp;the bounding box is used over the polygon (ie bad masks&#47;shapes).
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 * <pre>-vary-shape-color &lt;boolean&gt; (property: varyShapeColor)
 * &nbsp;&nbsp;&nbsp;If enabled, the shape colors get varied.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-shape-color-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: shapeColorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use when varying the shape colors.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.TranslucentColorProvider -provider adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ObjectLocationsFromReport
    extends AbstractDrawObjectsFromReport {

  /** for serialization. */
  private static final long serialVersionUID = 6356419097401574024L;

  /** the thickness of the stroke. */
  protected float m_StrokeThickness;

  /** whether to draw the shape filled. */
  protected boolean m_Filled;

  /** the alpha value to use for the outlines. */
  protected int m_OutlineAlpha;

  /** the colors for the polygon bounds. */
  protected transient Map<Color,Color> m_OutlineColors;

  /** whether to draw the bounds of the polygon as well. */
  protected boolean m_PolygonBounds;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Displays the locations of objects in the image, using data from the "
	    + "attached report.\n"
	    + "Suffixes:\n"
	    + LocatedObjects.KEY_X + "\n"
	    + LocatedObjects.KEY_Y + "\n"
	    + LocatedObjects.KEY_WIDTH + "\n"
	    + LocatedObjects.KEY_HEIGHT + "\n"
	    + "Optionally, if type information is available per object, the locations "
	    + "can be displayed in distinct colors per type. The type itself can be "
	    + "displayed as well.\n"
	    + "If polygon data should be available (" + LocatedObjects.KEY_POLY_X
	    + " and " + LocatedObjects.KEY_POLY_Y + "), then this takes precedence "
	    + "over the rectangle coordinates.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"stroke-thickness", "strokeThickness",
	1.0f, 0.01f, null);

    m_OptionManager.add(
	"filled", "filled",
	false);

    m_OptionManager.add(
	"outline-alpha", "outlineAlpha",
	255, 0, 255);

    m_OptionManager.add(
	"polygon-bounds", "polygonBounds",
	false);

    m_OptionManager.add(
	"bounding-box-fallback-ratio", "boundingBoxFallbackRatio",
	0.0, 0.0, 1.0);

    m_OptionManager.add(
	"vary-shape-color", "varyShapeColor",
	false);

    m_OptionManager.add(
	"shape-color-provider", "shapeColorProvider",
	new TranslucentColorProvider());
  }

  /**
   * Sets the stroke thickness to use.
   *
   * @param value	the thickness
   */
  public void setStrokeThickness(float value) {
    m_StrokeThickness = value;
    reset();
  }

  /**
   * Returns the current stroke thickness.
   *
   * @return		the thickness
   */
  public float getStrokeThickness() {
    return m_StrokeThickness;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String strokeThicknessTipText() {
    return "The thickness of the stroke.";
  }

  /**
   * Sets whether to draw the shape filled.
   *
   * @param value 	true if to fill
   */
  public void setFilled(boolean value) {
    m_Filled = value;
    reset();
  }

  /**
   * Returns whether to draw the shape filled.
   *
   * @return 		true if to fill
   */
  public boolean getFilled() {
    return m_Filled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filledTipText() {
    return "If enabled, the shape is drawn filled.";
  }

  /**
   * Sets the alpha value to use when drawing the outlines.
   *
   * @param value 	the alpha value (0: transparent, 255: opaque)
   */
  public void setOutlineAlpha(int value) {
    if (getOptionManager().isValid("polygonBoundsAlpha", value)) {
      m_OutlineAlpha = value;
      reset();
    }
  }

  /**
   * Returns the alpha value to use when drawing the outlines.
   *
   * @return 		the alpha value (0: transparent, 255: opaque)
   */
  public int getOutlineAlpha() {
    return m_OutlineAlpha;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outlineAlphaTipText() {
    return "This alpha is applied to the color of the outlines.";
  }

  /**
   * Sets whether to draw the polygon bounds.
   *
   * @param value 	true if to draw bounds
   */
  public void setPolygonBounds(boolean value) {
    m_PolygonBounds = value;
    reset();
  }

  /**
   * Returns whether to draw the polygon bounds.
   *
   * @return 		true if to draw bounds
   */
  public boolean getPolygonBounds() {
    return m_PolygonBounds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String polygonBoundsTipText() {
    return "If enabled, the polygon bounds are drawn as well.";
  }

  /**
   * Sets the ratio between shape area over bbox area. If below the bbox is used
   * instead of the polygon.
   *
   * @param value 	the ratio
   */
  public void setBoundingBoxFallbackRatio(double value) {
    m_Overlays.setBoundingBoxFallbackRatio(value);
    reset();
  }

  /**
   * Returns the ratio between shape area over bbox area. If below the bbox is used
   * instead of the polygon.
   *
   * @return 		the ratio
   */
  public double getBoundingBoxFallbackRatio() {
    return m_Overlays.getBoundingBoxFallbackRatio();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String boundingBoxFallbackRatioTipText() {
    return m_Overlays.boundingBoxFallbackRatioTipText();
  }

  /**
   * Sets whether to vary the colors of the shapes.
   *
   * @param value 	true if to vary
   */
  public void setVaryShapeColor(boolean value) {
    m_Overlays.setVaryShapeColor(value);
    reset();
  }

  /**
   * Returns whether to vary the colors of the shapes.
   *
   * @return 		true if to vary
   */
  public boolean getVaryShapeColor() {
    return m_Overlays.getVaryShapeColor();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String varyShapeColorTipText() {
    return m_Overlays.varyShapeColorTipText();
  }

  /**
   * Sets the color provider to use when varying the shape colors.
   *
   * @param value 	the provider
   */
  public void setShapeColorProvider(ColorProvider value) {
    m_Overlays.setShapeColorProvider(value);
    reset();
  }

  /**
   * Returns the color provider to use when varying the shape colors.
   *
   * @return 		the provider
   */
  public ColorProvider getShapeColorProvider() {
    return m_Overlays.getShapeColorProvider();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shapeColorProviderTipText() {
    return m_Overlays.shapeColorProviderTipText();
  }

  /**
   * Returns the thickness of the stroke.
   *
   * @param g		graphics context to get the thickness from
   * @param defValue	the default value to return in case of failure
   * @return		the stroke, default value if failed to extract
   */
  protected float getStrokeWidth(Graphics g, float defValue) {
    Graphics2D g2d;

    if (g instanceof Graphics2D) {
      g2d = (Graphics2D) g;
      if (g2d.getStroke() instanceof BasicStroke)
	return ((BasicStroke) g2d.getStroke()).getLineWidth();
    }

    return defValue;
  }

  /**
   * Applies the stroke thickness.
   *
   * @param stroke	the thickness to apply
   */
  protected void applyStroke(Graphics g, float stroke) {
    Graphics2D 	g2d;

    if (g instanceof Graphics2D) {
      g2d = (Graphics2D) g;
      g2d.setStroke(new BasicStroke(stroke));
    }
  }

  /**
   * Performs the actual drawing of the objects.
   *
   * @param image	the image
   * @param locations	the locations to paint
   * @return		null if OK, otherwise error message
   */
  protected String doDraw(BufferedImageContainer image, List<Polygon> locations) {
    Graphics 	g;
    String	label;
    Rectangle	rect;
    float	width;
    Color 	labelColor;
    Color	shapeColor;
    Color	actualColor;
    Color	alphaColor;

    g = image.getImage().getGraphics();

    width = getStrokeWidth(g, 1.0f);
    applyStroke(g, m_StrokeThickness);

    labelColor = getColor();
    g.setFont(getLabelFont());
    for (Polygon poly : locations) {
      if (getUseColorsPerType()) {
	if (m_Overlays.hasColor(poly))
	  labelColor = m_Overlays.getColor(poly);
      }

      shapeColor = null;
      if (getVaryShapeColor()) {
	if (m_Overlays.hasShapeColor(poly))
	  shapeColor = m_Overlays.getShapeColor(poly);
      }

      actualColor = (shapeColor == null) ? labelColor : shapeColor;
      if (m_OutlineColors == null)
	m_OutlineColors = new HashMap<>();
      if (!m_OutlineColors.containsKey(actualColor)) {
	alphaColor = new Color(actualColor.getRed(), actualColor.getGreen(), actualColor.getBlue(), m_OutlineAlpha);
	m_OutlineColors.put(actualColor, alphaColor);
      }
      if (m_Filled) {
	g.setColor(shapeColor == null ? labelColor : shapeColor);
	g.fillPolygon(poly);
      }
      g.setColor(m_OutlineColors.get(actualColor));
      g.drawPolygon(poly);

      rect = null;
      if (m_PolygonBounds) {
	g.setColor(m_OutlineColors.get(actualColor));
	rect = poly.getBounds();
	g.drawRect(rect.x, rect.y, rect.width, rect.height);
      }
      if (m_Overlays.hasLabel(poly)) {
	g.setColor(labelColor);
	label = m_Overlays.getLabel(poly);
	if (label != null) {
	  if (rect == null)
	    rect = poly.getBounds();
	  m_Overlays.drawString(g, rect, label);
	}
      }
    }

    applyStroke(g, width);

    return null;
  }
}
