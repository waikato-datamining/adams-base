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
 * ReportObjectOverlay.java
 * Copyright (C) 2017-2021 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import adams.core.base.BaseString;
import adams.data.objectoverlap.BoundingBoxFallbackSupporter;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjectFilter;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.TranslucentColorProvider;

import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Ancestor for overlays that use object locations from a report.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ReportObjectOverlay
  extends AbstractReportBasedOverlayHelper
  implements BoundingBoxFallbackSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 6356419097401574024L;

  /** the default prefix. */
  public final static String PREFIX_DEFAULT = "Object.";

  /** the cached shape colors. */
  protected HashMap<Polygon,Color> m_ShapeColors;

  /** whether to vary the shape color. */
  protected boolean m_VaryShapeColor;

  /** the color provider to use when varying the shape colors. */
  protected ColorProvider m_ShapeColorProvider;

  /** the ratio used for determining whether to fall back from polygon on bbox. */
  protected double m_BoundingBoxFallbackRatio;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Computes colors and labels for objects in report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"vary-shape-color", "varyShapeColor",
	false);

    m_OptionManager.add(
	"shape-color-provider", "shapeColorProvider",
	new TranslucentColorProvider());

    m_OptionManager.add(
	"bounding-box-fallback-ratio", "boundingBoxFallbackRatio",
	0.0, 0.0, 1.0);
  }

  /**
   * Resets the scheme.
   */
  @Override
  public void reset() {
    super.reset();

    m_ShapeColors = new HashMap<>();
  }

  /**
   * Returns the default prefix for the objects in the report.
   *
   * @return		the default
   */
  @Override
  protected String getDefaultPrefix() {
    return PREFIX_DEFAULT;
  }

  /**
   * Sets the prefix to use for the objects in the report.
   *
   * @param value 	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Sets whether to vary the colors of the shapes.
   *
   * @param value 	true if to vary
   */
  public void setVaryShapeColor(boolean value) {
    m_VaryShapeColor = value;
    reset();
  }

  /**
   * Returns whether to vary the colors of the shapes.
   *
   * @return 		true if to vary
   */
  public boolean getVaryShapeColor() {
    return m_VaryShapeColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String varyShapeColorTipText() {
    return "If enabled, the shape colors get varied.";
  }

  /**
   * Sets the color provider to use when varying the shape colors.
   *
   * @param value 	the provider
   */
  public void setShapeColorProvider(ColorProvider value) {
    m_ShapeColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use when varying the shape colors.
   *
   * @return 		the provider
   */
  public ColorProvider getShapeColorProvider() {
    return m_ShapeColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shapeColorProviderTipText() {
    return "The color provider to use when varying the shape colors.";
  }

  /**
   * Sets the ratio between shape area over bbox area. If below the bbox is used
   * instead of the polygon.
   *
   * @param value 	the ratio
   */
  public void setBoundingBoxFallbackRatio(double value) {
    if (getOptionManager().isValid("boundingBoxFallbackRatio", value)) {
      m_BoundingBoxFallbackRatio = value;
      reset();
    }
  }

  /**
   * Returns the ratio between shape area over bbox area. If below the bbox is used
   * instead of the polygon.
   *
   * @return 		the ratio
   */
  public double getBoundingBoxFallbackRatio() {
    return m_BoundingBoxFallbackRatio;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String boundingBoxFallbackRatioTipText() {
    return "The threshold for the ratio between the areas (shape / bbox), below which the bounding box is used over the polygon (ie bad masks/shapes).";
  }

  /**
   * Determines the locations of the objects.
   * 
   * @param report	the report to inspect
   * @return 		true if updated
   */
  public boolean determineLocations(Report report, LocatedObjectFilter filter) {
    LocatedObjects 	result;
    HashSet<String>	types;
    String		suffix;
    String		type;
    Color		color;
    String		label;
    Polygon		poly;
    Polygon		bbox;
    int[] 		bbox_x;
    int[] 		bbox_y;
    double		area_poly;
    double		area_bbox;
    double		ratio;

    if (m_Locations != null)
      return false;
    if (report == null)
      return false;

    m_ShapeColorProvider.resetColors();

    // initialize colors
    if (m_UseColorsPerType) {
      m_TypeColors.clear();
      m_TypeColorProvider.resetColors();
      types = new HashSet<>();
      for (BaseString predefined: m_PredefinedLabels)
	m_TypeColors.put(predefined.getValue(), m_TypeColorProvider.next());
      for (AbstractField field: report.getFields()) {
	if (field.getName().endsWith(m_TypeSuffix))
	  types.add("" + report.getValue(field));
      }
      for (String t: types) {
        if (!m_TypeColors.containsKey(t))
	  m_TypeColors.put(t, m_TypeColorProvider.next());
      }
    }

    m_ObjectLocationMappings = new HashMap<>();
    m_Locations       = new ArrayList<>();
    m_AllObjects      = new LocatedObjects();
    m_FilteredObjects = new LocatedObjects();
    m_Colors          = new HashMap<>();
    m_ShapeColors     = new HashMap<>();
    m_Labels          = new HashMap<>();
    suffix            = determineTypeSuffix();
    result            = LocatedObjects.fromReport(report, m_Prefix);
    for (LocatedObject object: result) {
      poly = null;
      if (object.hasPolygon())
	poly = object.getPolygon();
      bbox_x = new int[]{object.getX(), object.getX() + object.getWidth() - 1, object.getX() + object.getWidth() - 1, object.getX()};
      bbox_y = new int[]{object.getY(), object.getY(), object.getY() + object.getHeight() - 1, object.getY() + object.getHeight() - 1};
      bbox   = new Polygon(bbox_x, bbox_y, bbox_x.length);

      // fall back on bbox?
      if ((poly != null) && (m_BoundingBoxFallbackRatio > 0)) {
        area_bbox = LocatedObject.toGeometry(bbox).getArea();
        area_poly = LocatedObject.toGeometry(poly).getArea();
        if (area_bbox > 0) {
	  ratio = area_poly / area_bbox;
	  if (ratio < m_BoundingBoxFallbackRatio)
	    poly = null;
	}
      }
      if (poly == null)
	poly = bbox;

      color = m_Color;

      if (!suffix.isEmpty() && (object.getMetaData() != null) && (object.getMetaData().containsKey(suffix))) {
	type  = "" + object.getMetaData().get(suffix);

	// draw type?
	if (!m_TypeRegExp.isMatchAll()) {
	  if (!m_TypeRegExp.isMatch(type))
	    continue;
	}

	// color per type?
	if (m_UseColorsPerType) {
	  if (m_TypeColors.containsKey(type))
	    color = m_TypeColors.get(type);
	}

	// label?
	if (!m_LabelFormat.isEmpty()) {
	  label = applyLabelFormat(object, type);
	  m_Labels.put(poly, label);
	}
      }
      else {
	// label?
	if (!m_LabelFormat.isEmpty()) {
	  label = applyLabelFormat(object, "");
	  m_Labels.put(poly, label);
	}
      }

      m_AllObjects.add(object);
      if (!filter.accept(object))
        continue;
      m_FilteredObjects.add(object);

      m_Colors.put(poly, color);
      m_ShapeColors.put(poly, m_ShapeColorProvider.next());
      m_Locations.add(poly);
      m_ObjectLocationMappings.put(object, poly);
    }

    return true;
  }

  /**
   * Checks whether a shape color has been stored for the given object.
   *
   * @param poly	the object to check
   * @return		true if custom color available
   */
  public boolean hasShapeColor(Polygon poly) {
    return m_ShapeColors.containsKey(poly);
  }

  /**
   * Returns the shape color for the object.
   *
   * @param poly	the object to get the color for
   * @return		the shape color, null if none available
   */
  public Color getShapeColor(Polygon poly) {
    return m_ShapeColors.get(poly);
  }
}
