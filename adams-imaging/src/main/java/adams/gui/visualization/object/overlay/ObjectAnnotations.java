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
 * ObjectAnnotations.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.overlay;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;
import adams.gui.visualization.object.ObjectAnnotationPanel;
import adams.gui.visualization.object.objectannotations.check.AnnotationCheck;
import adams.gui.visualization.object.objectannotations.check.PassThrough;
import adams.gui.visualization.object.objectannotations.cleaning.AnnotationCleaner;
import adams.gui.visualization.object.objectannotations.colors.AnnotationColors;
import adams.gui.visualization.object.objectannotations.colors.FixedColor;
import adams.gui.visualization.object.objectannotations.label.LabelPlotter;
import adams.gui.visualization.object.objectannotations.label.NoLabel;
import adams.gui.visualization.object.objectannotations.outline.NoOutline;
import adams.gui.visualization.object.objectannotations.outline.OutlinePlotter;
import adams.gui.visualization.object.objectannotations.outline.PolygonOutline;
import adams.gui.visualization.object.objectannotations.shape.FilledPolygon;
import adams.gui.visualization.object.objectannotations.shape.NoShape;
import adams.gui.visualization.object.objectannotations.shape.ShapePlotter;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Flexible overlay for object annotations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ObjectAnnotations
    extends AbstractOverlay
    implements ObjectPrefixHandler, OverlayWithCustomAlphaSupport {

  private static final long serialVersionUID = 7620433880368599467L;

  /** the prefix to use. */
  protected String m_Prefix;

  /** the cleaners to use. */
  protected AnnotationCleaner[] m_Cleaners;

  /** the shape plotters. */
  protected ShapePlotter[] m_ShapePlotters;

  /** the colorizers for the shape. */
  protected AnnotationColors[] m_ShapeColors;

  /** the outline plotters. */
  protected OutlinePlotter[] m_OutlinePlotters;

  /** the colorizers for the outline. */
  protected AnnotationColors[] m_OutlineColors;

  /** the label plotters. */
  protected LabelPlotter[] m_LabelPlotters;

  /** the colorizers for the labels. */
  protected AnnotationColors[] m_LabelColors;

  /** for detecting invalid annotations and displaying them differently. */
  protected AnnotationCheck m_AnnotationCheck;

  /** the shape plotter for annotations (invalid annotations). */
  protected ShapePlotter m_InvalidShapePlotter;

  /** the colorizers for the shape annotations (invalid annotations). */
  protected AnnotationColors m_InvalidShapeColor;

  /** the outline plotters (invalid annotations). */
  protected OutlinePlotter m_InvalidOutlinePlotter;

  /** the colorizers for the outline (invalid annotations). */
  protected AnnotationColors m_InvalidOutlineColor;

  /** the annotations. */
  protected transient LocatedObjects m_Annotations;

  /** indices of invalid annotations. */
  protected transient TIntSet m_InvalidAnnotations;

  /** whether a custom alpha is in use. */
  protected boolean m_CustomAlphaEnabled;

  /** the custom alpha value to use. */
  protected int m_CustomAlpha;

  /** the color cache. */
  protected Map<Color,Color> m_CustomAlphaColorCache;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Flexible overlay for object annotations.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"prefix", "prefix",
	"Object.");

    m_OptionManager.add(
	"cleaner", "cleaners",
	new AnnotationCleaner[0]);

    m_OptionManager.add(
	"shape-plotter", "shapePlotters",
	new ShapePlotter[0]);

    m_OptionManager.add(
	"shape-color", "shapeColors",
	new AnnotationColors[0]);

    m_OptionManager.add(
	"outline-plotter", "outlinePlotters",
	new OutlinePlotter[0]);

    m_OptionManager.add(
	"outline-color", "outlineColors",
	new AnnotationColors[0]);

    m_OptionManager.add(
	"label-plotter", "labelPlotters",
	new LabelPlotter[0]);

    m_OptionManager.add(
	"label-color", "labelColors",
	new AnnotationColors[0]);

    m_OptionManager.add(
	"annotation-check", "annotationCheck",
	new PassThrough());

    m_OptionManager.add(
	"invalid-shape-plotter", "invalidShapePlotter",
	new FilledPolygon());

    m_OptionManager.add(
	"invalid-shape-color", "invalidShapeColor",
	new FixedColor());

    m_OptionManager.add(
	"invalid-outline-plotter", "invalidOutlinePlotter",
	new PolygonOutline());

    m_OptionManager.add(
	"invalid-outline-color", "invalidOutlineColor",
	new FixedColor());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CustomAlpha = 255;
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Annotations = null;
  }

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  @Override
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
   */
  @Override
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String prefixTipText() {
    return "The report field prefix used for objects.";
  }

  /**
   * Sets the cleaners for the annotations.
   *
   * @param value 	the cleaners
   */
  public void setCleaners(AnnotationCleaner[] value) {
    m_Cleaners = value;
    reset();
  }

  /**
   * Returns the cleaners for the annotations.
   *
   * @return 		the cleaners
   */
  public AnnotationCleaner[] getCleaners() {
    return m_Cleaners;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cleanersTipText() {
    return "The cleaners to apply to the annotations.";
  }

  /**
   * Sets the colorizers for the shape plotters.
   *
   * @param value 	the colorizers
   */
  public void setShapeColors(AnnotationColors[] value) {
    m_ShapeColors   = value;
    m_ShapePlotters = (ShapePlotter[]) Utils.adjustArray(m_ShapePlotters, m_ShapeColors.length, new NoShape());
    reset();
  }

  /**
   * Returns the colorizers for the shape plotters.
   *
   * @return 		the colorizers
   */
  public AnnotationColors[] getShapeColors() {
    return m_ShapeColors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shapeColorsTipText() {
    return "The colorizers for the corresponding shape plotters.";
  }

  /**
   * Sets the plotters for the shapes.
   *
   * @param value 	the plotters
   */
  public void setShapePlotters(ShapePlotter[] value) {
    m_ShapePlotters = value;
    m_ShapeColors   = (AnnotationColors[]) Utils.adjustArray(m_ShapeColors, m_ShapePlotters.length, new FixedColor());
    reset();
  }

  /**
   * Returns the plotters for the shapes.
   *
   * @return 		the plotters
   */
  public ShapePlotter[] getShapePlotters() {
    return m_ShapePlotters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shapePlottersTipText() {
    return "The plotters to use for drawing the shapes.";
  }

  /**
   * Sets the colorizers for the outline plotters.
   *
   * @param value 	the colorizers
   */
  public void setOutlineColors(AnnotationColors[] value) {
    m_OutlineColors   = value;
    m_OutlinePlotters = (OutlinePlotter[]) Utils.adjustArray(m_OutlinePlotters, m_OutlineColors.length, new NoOutline());
    reset();
  }

  /**
   * Returns the colorizers for the outline plotters.
   *
   * @return 		the colorizers
   */
  public AnnotationColors[] getOutlineColors() {
    return m_OutlineColors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outlineColorsTipText() {
    return "The colorizers for the corresponding outline plotters.";
  }

  /**
   * Sets the plotters for the outlines.
   *
   * @param value 	the plotters
   */
  public void setOutlinePlotters(OutlinePlotter[] value) {
    m_OutlinePlotters = value;
    m_OutlineColors   = (AnnotationColors[]) Utils.adjustArray(m_OutlineColors, m_OutlinePlotters.length, new FixedColor());
    reset();
  }

  /**
   * Returns the plotters for the outlines.
   *
   * @return 		the plotters
   */
  public OutlinePlotter[] getOutlinePlotters() {
    return m_OutlinePlotters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outlinePlottersTipText() {
    return "The plotters to use for drawing the outlines.";
  }

  /**
   * Sets the colorizers for the label plotters.
   *
   * @param value 	the colorizers
   */
  public void setLabelColors(AnnotationColors[] value) {
    m_LabelColors   = value;
    m_LabelPlotters = (LabelPlotter[]) Utils.adjustArray(m_LabelPlotters, m_LabelColors.length, new NoLabel());
    reset();
  }

  /**
   * Returns the colorizers for the label plotters.
   *
   * @return 		the colorizers
   */
  public AnnotationColors[] getLabelColors() {
    return m_LabelColors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelColorsTipText() {
    return "The colorizers for the corresponding label plotters.";
  }

  /**
   * Sets the plotters for the labels.
   *
   * @param value 	the plotters
   */
  public void setLabelPlotters(LabelPlotter[] value) {
    m_LabelPlotters = value;
    m_LabelColors   = (AnnotationColors[]) Utils.adjustArray(m_LabelColors, m_LabelPlotters.length, new FixedColor());
    reset();
  }

  /**
   * Returns the plotters for the labels.
   *
   * @return 		the plotters
   */
  public LabelPlotter[] getLabelPlotters() {
    return m_LabelPlotters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelPlottersTipText() {
    return "The plotters to use for drawing the labels.";
  }

  /**
   * Sets the check scheme to use for identifying invalid annotations.
   *
   * @param value 	the check
   */
  public void setAnnotationCheck(AnnotationCheck value) {
    m_AnnotationCheck = value;
    reset();
  }

  /**
   * Returns the check scheme to use for identifying invalid annotations.
   *
   * @return 		the check
   */
  public AnnotationCheck getAnnotationCheck() {
    return m_AnnotationCheck;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String annotationCheckTipText() {
    return "The check scheme to use to determine invalid annotations.";
  }

  /**
   * Sets the colorizer for the shape plotter for invalid annotations.
   *
   * @param value 	the colorizer
   */
  public void setInvalidShapeColor(AnnotationColors value) {
    m_InvalidShapeColor = value;
    reset();
  }

  /**
   * Returns the colorizer for the shape plotter for invalid annotations.
   *
   * @return 		the colorizer
   */
  public AnnotationColors getInvalidShapeColor() {
    return m_InvalidShapeColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invalidShapeColorsTipText() {
    return "The colorizer for the corresponding shape plotter for invalid annotations.";
  }

  /**
   * Sets the plotter for the shapes of invalid annotations.
   *
   * @param value 	the plotter
   */
  public void setInvalidShapePlotter(ShapePlotter value) {
    m_InvalidShapePlotter = value;
    reset();
  }

  /**
   * Returns the plotter for the shapes of invalid annotations.
   *
   * @return 		the plotter
   */
  public ShapePlotter getInvalidShapePlotter() {
    return m_InvalidShapePlotter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invalidShapePlottersTipText() {
    return "The plotter to use for drawing the shapes of invalid annotations.";
  }

  /**
   * Sets the colorizer for the outline plotter of invalid annotations.
   *
   * @param value 	the colorizer
   */
  public void setInvalidOutlineColor(AnnotationColors value) {
    m_InvalidOutlineColor = value;
    reset();
  }

  /**
   * Returns the colorizer for the outline plotter of invalid annotations.
   *
   * @return 		the colorizer
   */
  public AnnotationColors getInvalidOutlineColor() {
    return m_InvalidOutlineColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invalidOutlineColorTipText() {
    return "The colorizer for the corresponding outline plotter for invalid annotations.";
  }

  /**
   * Sets the plotter for the outlines of invalid annotations.
   *
   * @param value 	the plotter
   */
  public void setInvalidOutlinePlotter(OutlinePlotter value) {
    m_InvalidOutlinePlotter = value;
    reset();
  }

  /**
   * Returns the plotter for the outlines of invalid annotations.
   *
   * @return 		the plotter
   */
  public OutlinePlotter getInvalidOutlinePlotter() {
    return m_InvalidOutlinePlotter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invalidOutlinePlotterTipText() {
    return "The plotter to use for drawing the outlines of invalid annotations.";
  }

  /**
   * Sets whether to use a custom alpha value for the overlay colors.
   *
   * @param value	true if to use custom alpha
   */
  @Override
  public void setCustomAlphaEnabled(boolean value) {
    m_CustomAlphaEnabled = value;
    annotationsChanged();
  }

  /**
   * Returns whether a custom alpha value is in use for the overlay colors.
   *
   * @return		true if custom alpha in use
   */
  @Override
  public boolean isCustomAlphaEnabled() {
    return m_CustomAlphaEnabled;
  }

  /**
   * Sets the custom alpha value (0: transparent, 255: opaque).
   *
   * @param value	the alpha value
   */
  @Override
  public void setCustomAlpha(int value) {
    m_CustomAlpha = value;
    annotationsChanged();
  }

  /**
   * Returns the custom alpha value (0: transparent, 255: opaque).
   *
   * @return		the alpha value
   */
  @Override
  public int getCustomAlpha() {
    return m_CustomAlpha;
  }

  /**
   * Hook method for when annotations change.
   */
  public void annotationsChanged() {
    super.annotationsChanged();
    m_Annotations           = null;
    m_InvalidAnnotations    = null;
    m_CustomAlphaColorCache = new HashMap<>();
  }

  /**
   * Initializes the annotations.
   *
   * @param panel	the context
   */
  protected void initAnnotations(ObjectAnnotationPanel panel) {
    MessageCollection 	errors;
    int[]		invalid;

    if (m_Annotations != null)
      return;

    errors = new MessageCollection();

    // clean
    m_Annotations = LocatedObjects.fromReport(panel.getReport(), m_Prefix);
    for (AnnotationCleaner cleaner: m_Cleaners) {
      m_Annotations = cleaner.cleanAnnotations(m_Annotations, errors);
      if (!errors.isEmpty())
	break;
    }
    if (!errors.isEmpty()) {
      getLogger().severe(errors.toString());
      return;
    }

    // invalid ones
    invalid = m_AnnotationCheck.findInvalidAnnotationsIndices(m_Annotations);
    m_InvalidAnnotations = new TIntHashSet(invalid);

    // shape colors
    for (AnnotationColors colors: m_ShapeColors) {
      colors.initColors(m_Annotations, errors);
      if (!errors.isEmpty())
	break;
    }
    if (!errors.isEmpty()) {
      getLogger().severe(errors.toString());
      return;
    }

    // outline colors
    for (AnnotationColors colors: m_OutlineColors) {
      colors.initColors(m_Annotations, errors);
      if (!errors.isEmpty())
	break;
    }
    if (!errors.isEmpty()) {
      getLogger().severe(errors.toString());
      return;
    }

    // label colors
    for (AnnotationColors colors: m_LabelColors) {
      colors.initColors(m_Annotations, errors);
      if (!errors.isEmpty())
	break;
    }
    if (!errors.isEmpty()) {
      getLogger().severe(errors.toString());
      return;
    }
  }

  /**
   * Applies the custom alpha value to the color if necessary.
   *
   * @param c		the color to update
   * @return		the (potentially) updated color
   */
  protected Color applyAlpha(Color c) {
    Color	result;

    result = c;

    if (m_CustomAlphaEnabled) {
      if (!m_CustomAlphaColorCache.containsKey(c)) {
        result = new Color(c.getRed(), c.getGreen(), c.getBlue(), m_CustomAlpha);
        m_CustomAlphaColorCache.put(c, result);
      }
      else {
	result = m_CustomAlphaColorCache.get(c);
      }
    }

    return result;
  }

  /**
   * Paints the overlay.
   *
   * @param panel 	the owning panel
   * @param g		the graphics context
   */
  @Override
  protected void doPaint(ObjectAnnotationPanel panel, Graphics g) {
    int			o;
    int			i;
    LocatedObject 	object;
    Graphics2D 		g2d;

    initAnnotations(panel);

    g2d = (Graphics2D) g;
    for (o = 0; o < m_Annotations.size(); o++) {
      object = m_Annotations.get(o);
      if (m_InvalidAnnotations.contains(o)) {
	m_InvalidShapePlotter.plotShape(object, applyAlpha(m_InvalidShapeColor.getColor(object)), g2d);
	m_InvalidOutlinePlotter.plotOutline(object, applyAlpha(m_InvalidOutlineColor.getColor(object)), g2d);
      }
      else {
	for (i = 0; i < m_ShapePlotters.length; i++)
	  m_ShapePlotters[i].plotShape(object, applyAlpha(m_ShapeColors[i].getColor(object)), g2d);
	for (i = 0; i < m_OutlinePlotters.length; i++)
	  m_OutlinePlotters[i].plotOutline(object, applyAlpha(m_OutlineColors[i].getColor(object)), g2d);
	for (i = 0; i < m_LabelPlotters.length; i++)
	  m_LabelPlotters[i].plotLabel(object, applyAlpha(m_LabelColors[i].getColor(object)), g2d);
      }
    }
  }
}
