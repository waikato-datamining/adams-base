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
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.image;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;
import adams.gui.visualization.object.objectannotations.cleaning.AnnotationCleaner;
import adams.gui.visualization.object.objectannotations.colors.AnnotationColors;
import adams.gui.visualization.object.objectannotations.colors.FixedColor;
import adams.gui.visualization.object.objectannotations.label.LabelPlotter;
import adams.gui.visualization.object.objectannotations.label.NoLabel;
import adams.gui.visualization.object.objectannotations.outline.NoOutline;
import adams.gui.visualization.object.objectannotations.outline.OutlinePlotter;
import adams.gui.visualization.object.objectannotations.shape.NoShape;
import adams.gui.visualization.object.objectannotations.shape.ShapePlotter;

import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Overlays object annotations from the report.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ObjectAnnotations
    extends AbstractImageOverlay
    implements ObjectPrefixHandler {

  private static final long serialVersionUID = -3088909952142797917L;

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

  /** the annotations. */
  protected transient LocatedObjects m_Annotations;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Overlays object annotations from the report.";
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
   * Notifies the overlay that the image has changed.
   *
   * @param panel the panel this overlay belongs to
   */
  @Override
  protected void doImageChanged(ImagePanel.PaintPanel panel) {
    m_Annotations = null;
  }

  /**
   * Initializes the annotations.
   *
   * @param panel	the context
   */
  protected void initAnnotations(ImagePanel.PaintPanel panel) {
    MessageCollection	errors;

    if (m_Annotations != null)
      return;

    errors = new MessageCollection();

    // clean
    m_Annotations = LocatedObjects.fromReport(panel.getOwner().getAllProperties(), m_Prefix);
    for (AnnotationCleaner cleaner: m_Cleaners) {
      m_Annotations = cleaner.cleanAnnotations(m_Annotations, errors);
      if (!errors.isEmpty())
        break;
    }
    if (!errors.isEmpty()) {
      getLogger().severe(errors.toString());
      return;
    }

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
   * Performs the actual painting of the overlay.
   *
   * @param panel the panel this overlay is for
   * @param g     the graphics context
   */
  @Override
  protected void doPaintOverlay(ImagePanel.PaintPanel panel, Graphics g) {
    int		i;
    Graphics2D	g2d;

    initAnnotations(panel);

    g2d = (Graphics2D) g;
    for (LocatedObject object: m_Annotations) {
      for (i = 0; i < m_ShapePlotters.length; i++)
        m_ShapePlotters[i].plotShape(object, m_ShapeColors[i].getColor(object), g2d);
      for (i = 0; i < m_OutlinePlotters.length; i++)
        m_OutlinePlotters[i].plotOutline(object, m_OutlineColors[i].getColor(object), g2d);
      for (i = 0; i < m_LabelPlotters.length; i++)
        m_LabelPlotters[i].plotLabel(object, m_LabelColors[i].getColor(object), g2d);
    }
  }
}
