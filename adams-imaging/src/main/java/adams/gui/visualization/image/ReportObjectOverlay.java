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

import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.option.AbstractOptionHandler;
import adams.data.image.ImageAnchor;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.AcceptAllLocatedObjectsFilter;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjectFilter;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.Fonts;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.TranslucentColorProvider;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Ancestor for overlays that use object locations from a report.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ReportObjectOverlay
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 6356419097401574024L;

  /** the default prefix. */
  public final static String PREFIX_DEFAULT = "Object.";

  /** the prefix for the objects in the report. */
  protected String m_Prefix;

  /** the color for the objects. */
  protected Color m_Color;

  /** whether to use colors per type. */
  protected boolean m_UseColorsPerType;

  /** the color provider to use. */
  protected ColorProvider m_TypeColorProvider;

  /** the suffix for the type. */
  protected String m_TypeSuffix;

  /** the regular expression for the types to draw. */
  protected BaseRegExp m_TypeRegExp;

  /** the label for the rectangles. */
  protected String m_LabelFormat;

  /** the label font. */
  protected Font m_LabelFont;

  /** the label anchor. */
  protected ImageAnchor m_LabelAnchor;

  /** the x offset for the label. */
  protected int m_LabelOffsetX;

  /** the y offset for the label. */
  protected int m_LabelOffsetY;

  /** all located objects. */
  protected LocatedObjects m_AllObjects;

  /** the filtered located objects. */
  protected LocatedObjects m_FilteredObjects;

  /** the cached object/location relation. */
  protected Map<LocatedObject,Polygon> m_ObjectLocationMappings;

  /** the cached locations. */
  protected List<Polygon> m_Locations;

  /** the type/color mapping. */
  protected HashMap<String,Color> m_TypeColors;

  /** the cached colors. */
  protected HashMap<Polygon,Color> m_Colors;

  /** the cached shape colors. */
  protected HashMap<Polygon,Color> m_ShapeColors;

  /** the labels. */
  protected HashMap<Polygon,String> m_Labels;

  /** predefined labels. */
  protected BaseString[] m_PredefinedLabels;

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
	"prefix", "prefix",
	PREFIX_DEFAULT);

    m_OptionManager.add(
	"color", "color",
	Color.RED);

    m_OptionManager.add(
	"use-colors-per-type", "useColorsPerType",
	false);

    m_OptionManager.add(
	"type-color-provider", "typeColorProvider",
	new DefaultColorProvider());

    m_OptionManager.add(
	"type-suffix", "typeSuffix",
	".type");

    m_OptionManager.add(
	"type-regexp", "typeRegExp",
	new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	"label-format", "labelFormat",
	"#");

    m_OptionManager.add(
	"label-font", "labelFont",
	Fonts.getSansFont(14));

    m_OptionManager.add(
	"label-anchor", "labelAnchor",
	ImageAnchor.TOP_RIGHT);

    m_OptionManager.add(
	"label-offset-x", "labelOffsetX",
	0);

    m_OptionManager.add(
	"label-offset-y", "labelOffsetY",
	0);

    m_OptionManager.add(
	"predefined-labels", "predefinedLabels",
	new BaseString[0]);

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

    m_ObjectLocationMappings = null;
    m_Locations       = null;
    m_AllObjects      = null;
    m_FilteredObjects = null;
    m_TypeColors      = new HashMap<>();
    m_Colors          = new HashMap<>();
    m_ShapeColors     = new HashMap<>();
    m_Labels          = new HashMap<>();
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
   * Returns the prefix to use for the objects in the report.
   *
   * @return 		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix of fields in the report to identify as object location, eg 'Object.'.";
  }

  /**
   * Sets the color to use for the objects.
   *
   * @param value 	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color to use for the objects.
   *
   * @return 		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color to use for the objects.";
  }

  /**
   * Sets whether to use colors per type.
   *
   * @param value 	true if to use colors per type
   */
  public void setUseColorsPerType(boolean value) {
    m_UseColorsPerType = value;
    reset();
  }

  /**
   * Returns whether to use colors per type.
   *
   * @return 		true if to use colors per type
   */
  public boolean getUseColorsPerType() {
    return m_UseColorsPerType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useColorsPerTypeTipText() {
    return "If enabled, individual colors per type are used.";
  }

  /**
   * Sets the color provider to use for the types.
   *
   * @param value 	the provider
   */
  public void setTypeColorProvider(ColorProvider value) {
    m_TypeColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use for the types.
   *
   * @return 		the provider
   */
  public ColorProvider getTypeColorProvider() {
    return m_TypeColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeColorProviderTipText() {
    return "The color provider to use for the various types.";
  }

  /**
   * Sets the suffix to use for the types.
   *
   * @param value 	the suffix
   */
  public void setTypeSuffix(String value) {
    m_TypeSuffix = value;
    reset();
  }

  /**
   * Returns the suffix to use for the types.
   *
   * @return 		the suffix
   */
  public String getTypeSuffix() {
    return m_TypeSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeSuffixTipText() {
    return "The suffix of fields in the report to identify the type.";
  }

  /**
   * Sets the regular expression that the types must match in order to get
   * drawn.
   *
   * @param value 	the expression
   */
  public void setTypeRegExp(BaseRegExp value) {
    m_TypeRegExp = value;
    reset();
  }

  /**
   * Returns the regular expression that the types must match in order to get
   * drawn.
   *
   * @return 		the expression
   */
  public BaseRegExp getTypeRegExp() {
    return m_TypeRegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeRegExpTipText() {
    return "The regular expression that the types must match in order to get drawn (eg only plotting a subset).";
  }

  /**
   * Sets the label format.
   *
   * @param value 	the label format
   */
  public void setLabelFormat(String value) {
    m_LabelFormat = value;
    reset();
  }

  /**
   * Returns the label format.
   *
   * @return 		the label format
   */
  public String getLabelFormat() {
    return m_LabelFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelFormatTipText() {
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
  public void setLabelFont(Font value) {
    m_LabelFont = value;
    reset();
  }

  /**
   * Returns the label font.
   *
   * @return 		the label font
   */
  public Font getLabelFont() {
    return m_LabelFont;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelFontTipText() {
    return "The font to use for the labels.";
  }

  /**
   * Sets the anchor for the label.
   *
   * @param value 	the anchor
   */
  public void setLabelAnchor(ImageAnchor value) {
    m_LabelAnchor = value;
    reset();
  }

  /**
   * Returns the anchor for the label.
   *
   * @return 		the anchor
   */
  public ImageAnchor getLabelAnchor() {
    return m_LabelAnchor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelAnchorTipText() {
    return "The anchor for the label.";
  }

  /**
   * Sets the X offset for the label.
   *
   * @param value 	the X offset
   */
  public void setLabelOffsetX(int value) {
    m_LabelOffsetX = value;
    reset();
  }

  /**
   * Returns the X offset for the label.
   *
   * @return 		the X offset
   */
  public int getLabelOffsetX() {
    return m_LabelOffsetX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelOffsetXTipText() {
    return "The X offset for the label; values of 0 or greater are interpreted as absolute pixels, -1 uses left as anchor, -2 the center and -3 the right.";
  }

  /**
   * Sets the Y offset for the label.
   *
   * @param value 	the Y offset
   */
  public void setLabelOffsetY(int value) {
    m_LabelOffsetY = value;
    reset();
  }

  /**
   * Returns the Y offset for the label.
   *
   * @return 		the Y offset
   */
  public int getLabelOffsetY() {
    return m_LabelOffsetY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelOffsetYTipText() {
    return "The Y offset for the label values of 0 or greater are interpreted as absolute pixels, -1 uses top as anchor, -2 the middle and -3 the bottom.";
  }

  /**
   * Sets the predefined labels.
   *
   * @param value	the labels
   */
  public void setPredefinedLabels(BaseString[] value) {
    m_PredefinedLabels = value;
    reset();
  }

  /**
   * Returns the predefined labels.
   *
   * @return		the labels
   */
  public BaseString[] getPredefinedLabels() {
    return m_PredefinedLabels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predefinedLabelsTipText() {
    return "The predefined labels to use for setting up the colors; avoids constants changing in color pallet.";
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
   * Returns the type suffix without the leading dot.
   *
   * @return		the sufix
   */
  protected String determineTypeSuffix() {
    return m_TypeSuffix.isEmpty() ? "" : m_TypeSuffix.substring(1);
  }

  /**
   * Applies further format options. Does nothing if format or value are empty.
   *
   * @param value	the value to format
   * @param format	the format to apply
   * @return		the processed value
   */
  protected String applyFormatOptions(String value, String format) {
    int		decimals;
    boolean	isDouble;
    String	tmp;

    if (format.isEmpty() || value.isEmpty())
      return value;

    isDouble = Utils.isDouble(value);

    // max # of decimals?
    if (format.startsWith(".") && isDouble) {
      tmp = format.substring(1);
      if (Utils.isInteger(tmp)) {
        decimals = Integer.parseInt(tmp);
	value    = Utils.doubleToString(Double.parseDouble(value), decimals);
      }
    }

    return value;
  }

  /**
   * Applies the label format to the object to generate a display string.
   *
   * @param object 	the object to use as basis
   * @param type 	the meta-data key for the type
   * @return		the generated label
   */
  protected String applyLabelFormat(LocatedObject object, String type) {
    String 	result;
    String	key;
    String	value;
    int		start;
    int		end;
    String	format;

    result = m_LabelFormat
      .replace("#", "" + object.getMetaData().get(LocatedObjects.KEY_INDEX))
      .replace("@", type)
      .replace("$", type.replaceAll(".*\\.", ""));

    // other meta-data keys?
    while (((start = result.indexOf("{")) > -1) && ((end = result.indexOf("}", start)) > -1)) {
      key    = result.substring(start + 1, end);
      format = "";
      if (key.contains("|")) {
	format = key.substring(key.indexOf("|") + 1);
	key    = key.substring(0, key.indexOf("|"));
      }
      if (object.getMetaData().containsKey(key))
        value = "" + object.getMetaData().get(key);
      else
        value = "";
      value = applyFormatOptions(value, format);
      result = result.substring(0, start) + value + result.substring(end + 1);
    }

    return result;
  }

  /**
   * Determines the locations of the objects.
   *
   * @param report	the report to inspect
   * @return 		true if updated
   */
  public boolean determineLocations(Report report) {
    return determineLocations(report, new AcceptAllLocatedObjectsFilter());
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
   * Checks whether a color has been stored for the given object.
   *
   * @param poly	the object to check
   * @return		true if custom color available
   */
  public boolean hasColor(Polygon poly) {
    return m_Colors.containsKey(poly);
  }

  /**
   * Returns the color for the object.
   *
   * @param poly	the object to get the color for
   * @return		the color, null if none available
   */
  public Color getColor(Polygon poly) {
    return m_Colors.get(poly);
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

  /**
   * Checks whether a color has been stored for the given object type.
   *
   * @param type	the type to check
   * @return		true if custom color available
   */
  public boolean hasTypeColor(String type) {
    return m_UseColorsPerType && m_TypeColors.containsKey(type);
  }

  /**
   * Returns the color for the object type.
   *
   * @param type	the type to get the color for
   * @return		the color, null if none available
   */
  public Color getTypeColor(String type) {
    return m_TypeColors.get(type);
  }

  /**
   * Checks whether a label has been stored for the given object.
   *
   * @param poly	the object to check
   * @return		true if custom label available
   */
  public boolean hasLabel(Polygon poly) {
    return !m_LabelFormat.isEmpty() && m_Labels.containsKey(poly);
  }

  /**
   * Returns the label for the object.
   *
   * @param poly	the object to get the label for
   * @return		the label, null if none available
   */
  public String getLabel(Polygon poly) {
    return m_Labels.get(poly);
  }

  /**
   * Checks whether any object/location mappings are available.
   *
   * @return		true if mappings available
   */
  public boolean hasObjectLocationMappings() {
    return (m_ObjectLocationMappings != null) && (m_ObjectLocationMappings.size() > 0);
  }

  /**
   * Returns the current object/location mappings.
   *
   * @return		the mappings, null if not initialized
   * @see		#determineLocations(Report)
   */
  public Map<LocatedObject,Polygon> getObjectLocationMappings() {
    return m_ObjectLocationMappings;
  }

  /**
   * Checks whether any locations are available.
   *
   * @return		true if locations available
   */
  public boolean hasLocations() {
    return (m_Locations != null) && (m_Locations.size() > 0);
  }

  /**
   * Returns the current locations.
   *
   * @return		the locations, null if not initialized
   * @see		#determineLocations(Report)
   */
  public List<Polygon> getLocations() {
    return m_Locations;
  }

  /**
   * Returns whether the objects have been initialized.
   *
   * @return		true if initialized
   */
  public boolean hasAllObjects() {
    return (m_AllObjects != null);
  }

  /**
   * Returns all the objects.
   *
   * @return		the objects, null if not initialized yet
   */
  public LocatedObjects getAllObjects() {
    return m_AllObjects;
  }

  /**
   * Returns whether the objects have been initialized.
   *
   * @return		true if initialized
   */
  public boolean hasFilteredObjects() {
    return (m_FilteredObjects != null);
  }

  /**
   * Returns the filtered objects.
   *
   * @return		the objects, null if not initialized yet
   */
  public LocatedObjects getFilteredObjects() {
    return m_FilteredObjects;
  }

  /**
   * Calculates the string dimensions in pixels.
   *
   * @param g		the graphics context
   * @param f		the font to use
   * @param s		the string to measure
   * @return		the dimensions in pixels
   */
  public Dimension calcStringDimenions(Graphics g, Font f, String s) {
    FontMetrics metrics;

    metrics = g.getFontMetrics(f);
    return new Dimension(metrics.stringWidth(s), metrics.getHeight());
  }

  /**
   * Draws the string at the specified position.
   *
   * @param g		the graphics context
   * @param rect	the bounding box
   * @param label	the label to draw
   */
  public void drawString(Graphics g, Rectangle rect, String label) {
    int		offsetX;
    int		offsetY;
    Dimension	dims;

    if (label.isEmpty())
      return;

    offsetX = getLabelOffsetX();
    offsetY = getLabelOffsetY();
    dims    = calcStringDimenions(g, getLabelFont(), label);

    switch (offsetX) {
      case -1:
        offsetX = 0;
        break;
      case -2:
        offsetX = -dims.width / 2;
        break;
      case -3:
        offsetX = -dims.width;
        break;
      default:
	if (offsetX < 0)
	  offsetX = 0;
    }

    switch (offsetY) {
      case -1:
        offsetY = 0;
        break;
      case -2:
        offsetY = dims.height / 2;
        break;
      case -3:
        offsetY = dims.height;
        break;
      default:
	if (offsetY < 0)
	  offsetY = 0;
    }

    switch (getLabelAnchor()) {
      case TOP_LEFT:
	g.drawString(
	  label,
	  (int) (rect.getX() + offsetX),
	  (int) (rect.getY() + offsetY));
	break;
      case TOP_CENTER:
	g.drawString(
	  label,
	  (int) (rect.getX() + rect.width / 2 - dims.width / 2 + offsetX),
	  (int) (rect.getY() + offsetY));
	break;
      case TOP_RIGHT:
	g.drawString(
	  label,
	  (int) (rect.getX() + rect.width + offsetX),
	  (int) (rect.getY() + offsetY));
	break;
      case MIDDLE_LEFT:
	g.drawString(
	  label,
	  (int) (rect.getX() + offsetX),
	  (int) (rect.getY() + rect.height / 2 - dims.height / 2 + offsetY));
	break;
      case MIDDLE_CENTER:
	g.drawString(
	  label,
	  (int) (rect.getX() + rect.width / 2 - dims.width / 2 + offsetX),
	  (int) (rect.getY() + rect.height / 2 - dims.height / 2 + offsetY));
	break;
      case MIDDLE_RIGHT:
	g.drawString(
	  label,
	  (int) (rect.getX() + rect.width + offsetX),
	  (int) (rect.getY() + rect.height / 2 - dims.height / 2 + offsetY));
	break;
      case BOTTOM_LEFT:
	g.drawString(
	  label,
	  (int) (rect.getX() + offsetX),
	  (int) (rect.getY() + rect.height + offsetY));
	break;
      case BOTTOM_CENTER:
	g.drawString(
	  label,
	  (int) (rect.getX() + rect.width / 2 - dims.width / 2 + offsetX),
	  (int) (rect.getY() + rect.height + offsetY));
	break;
      case BOTTOM_RIGHT:
	g.drawString(
	  label,
	  (int) (rect.getX() + rect.width + offsetX),
	  (int) (rect.getY() + rect.height + offsetY));
	break;
      default:
        throw new IllegalStateException("Unhandled label anchor: " + getLabelAnchor());
    }
  }
}
