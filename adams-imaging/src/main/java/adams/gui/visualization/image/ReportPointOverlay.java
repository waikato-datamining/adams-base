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
 * ReportPointOverlay.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import adams.core.base.BaseString;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjectFilter;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Ancestor for overlays that use point locations from a report.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ReportPointOverlay
  extends AbstractReportBasedOverlayHelper {

  /** for serialization. */
  private static final long serialVersionUID = 6356419097401574024L;

  /** the default prefix. */
  public final static String PREFIX_DEFAULT = "Point.";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Computes colors and labels for points in report.";
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

    if (m_Locations != null)
      return false;
    if (report == null)
      return false;

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
      m_Locations.add(poly);
      m_ObjectLocationMappings.put(object, poly);
    }

    return true;
  }
}
