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
 * ObjectAnnotationsReportHandler.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser;

import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractReportReader;
import adams.data.io.input.ObjectLocationsSpreadSheetReader;
import adams.data.report.Report;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ObjectAnnotations;
import adams.gui.visualization.object.objectannotations.cleaning.AnnotationCleaner;
import adams.gui.visualization.object.objectannotations.colors.AnnotationColors;
import adams.gui.visualization.object.objectannotations.colors.FixedColor;
import adams.gui.visualization.object.objectannotations.label.LabelPlotter;
import adams.gui.visualization.object.objectannotations.label.NoLabel;
import adams.gui.visualization.object.objectannotations.outline.NoOutline;
import adams.gui.visualization.object.objectannotations.outline.OutlinePlotter;
import adams.gui.visualization.object.objectannotations.outline.RectangleOutline;
import adams.gui.visualization.object.objectannotations.shape.NoShape;
import adams.gui.visualization.object.objectannotations.shape.ShapePlotter;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Overlays the annotations onto the image.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-image-width &lt;int&gt; (property: imageWidth)
 * &nbsp;&nbsp;&nbsp;The image width to use; use &lt;= 0 for automatically determining minimal bbox
 * &nbsp;&nbsp;&nbsp;around annotations.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-image-height &lt;int&gt; (property: imageHeight)
 * &nbsp;&nbsp;&nbsp;The image height to use; use &lt;= 0 for automatically determining minimal
 * &nbsp;&nbsp;&nbsp;bbox around annotations.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-image-background &lt;java.awt.Color&gt; (property: imageBackground)
 * &nbsp;&nbsp;&nbsp;The background color to use for the image.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 *
 * <pre>-reader &lt;adams.data.io.input.AbstractReportReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader setup to use for reading the object locations from the spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.ObjectLocationsSpreadSheetReader -reader \"adams.data.io.input.CsvSpreadSheetReader -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet\" -row-finder adams.data.spreadsheet.rowfinder.AllFinder -col-left x0 -col-top y0 -col-right x1 -col-bottom y1 -col-type label_str
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used for objects.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-cleaner &lt;adams.gui.visualization.object.objectannotations.cleaning.AnnotationCleaner&gt; [-cleaner ...] (property: cleaners)
 * &nbsp;&nbsp;&nbsp;The cleaners to apply to the annotations.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-shape-plotter &lt;adams.gui.visualization.object.objectannotations.shape.ShapePlotter&gt; [-shape-plotter ...] (property: shapePlotters)
 * &nbsp;&nbsp;&nbsp;The plotters to use for drawing the shapes.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-shape-color &lt;adams.gui.visualization.object.objectannotations.colors.AnnotationColors&gt; [-shape-color ...] (property: shapeColors)
 * &nbsp;&nbsp;&nbsp;The colorizers for the corresponding shape plotters.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-outline-plotter &lt;adams.gui.visualization.object.objectannotations.outline.OutlinePlotter&gt; [-outline-plotter ...] (property: outlinePlotters)
 * &nbsp;&nbsp;&nbsp;The plotters to use for drawing the outlines.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.object.objectannotations.outline.RectangleOutline
 * </pre>
 *
 * <pre>-outline-color &lt;adams.gui.visualization.object.objectannotations.colors.AnnotationColors&gt; [-outline-color ...] (property: outlineColors)
 * &nbsp;&nbsp;&nbsp;The colorizers for the corresponding outline plotters.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.object.objectannotations.colors.FixedColor
 * </pre>
 *
 * <pre>-label-plotter &lt;adams.gui.visualization.object.objectannotations.label.LabelPlotter&gt; [-label-plotter ...] (property: labelPlotters)
 * &nbsp;&nbsp;&nbsp;The plotters to use for drawing the labels.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-label-color &lt;adams.gui.visualization.object.objectannotations.colors.AnnotationColors&gt; [-label-color ...] (property: labelColors)
 * &nbsp;&nbsp;&nbsp;The colorizers for the corresponding label plotters.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-show-object-panel &lt;boolean&gt; (property: showObjectPanel)
 * &nbsp;&nbsp;&nbsp;If enabled, the panel for selecting located objects is being displayed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ObjectAnnotationsReportHandler
  extends AbstractObjectContentHandler
  implements ObjectPrefixHandler {

  private static final long serialVersionUID = -6655562227841341465L;

  public final static int MISSING_WIDTH = 100;

  public final static int MISSING_HEIGHT = 100;

  /** the image width to use. */
  protected int m_ImageWidth;

  /** the image height to use. */
  protected int m_ImageHeight;

  /** the background color to use. */
  protected Color m_ImageBackground;

  /** the reader to use. */
  protected AbstractReportReader m_Reader;

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

  /** whether to show the located object panel. */
  protected boolean m_ShowObjectPanel;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Overlays the annotations onto the image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "image-width", "imageWidth",
      getDefaultImageWidth(), -1, null);

    m_OptionManager.add(
      "image-height", "imageHeight",
      getDefaultImageHeight(), -1, null);

    m_OptionManager.add(
      "image-background", "imageBackground",
      getDefaultImageBackground());

    m_OptionManager.add(
      "reader", "reader",
      getDefaultReader());

    m_OptionManager.add(
      "prefix", "prefix",
      LocatedObjects.DEFAULT_PREFIX);

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
      new OutlinePlotter[]{new RectangleOutline()});

    m_OptionManager.add(
      "outline-color", "outlineColors",
      new AnnotationColors[]{new FixedColor()});

    m_OptionManager.add(
      "label-plotter", "labelPlotters",
      new LabelPlotter[0]);

    m_OptionManager.add(
      "label-color", "labelColors",
      new AnnotationColors[0]);

    m_OptionManager.add(
      "show-object-panel", "showObjectPanel",
      false);
  }

  /**
   * Returns the default image width.
   *
   * @return		the default
   */
  protected int getDefaultImageWidth() {
    return -1;
  }

  /**
   * Sets the image width to use rather than auto determined one (-1).
   *
   * @param value	the image width (<= 0: auto)
   */
  public void setImageWidth(int value) {
    if (getOptionManager().isValid("imageWidth", value)) {
      m_ImageWidth = value;
      reset();
    }
  }

  /**
   * Returns the image width to use rather than auto determined one (-1).
   *
   * @return		the image width (<= 0: auto)
   */
  public int getImageWidth() {
    return m_ImageWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageWidthTipText() {
    return "The image width to use; use <= 0 for automatically determining minimal bbox around annotations.";
  }

  /**
   * Returns the default image height.
   *
   * @return		the default
   */
  protected int getDefaultImageHeight() {
    return -1;
  }

  /**
   * Sets the image height to use rather than auto determined one (-1).
   *
   * @param value	the image height (<= 0: auto)
   */
  public void setImageHeight(int value) {
    if (getOptionManager().isValid("imageHeight", value)) {
      m_ImageHeight = value;
      reset();
    }
  }

  /**
   * Returns the image height to use rather than auto determined one (-1).
   *
   * @return		the image height (<= 0: auto)
   */
  public int getImageHeight() {
    return m_ImageHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageHeightTipText() {
    return "The image height to use; use <= 0 for automatically determining minimal bbox around annotations.";
  }

  /**
   * Returns the default background color.
   *
   * @return		the default
   */
  protected Color getDefaultImageBackground() {
    return Color.WHITE;
  }

  /**
   * Sets the color to use for the background.
   *
   * @param value	the color
   */
  public void setImageBackground(Color value) {
    m_ImageBackground = value;
    reset();
  }

  /**
   * Returns the color to use for the background.
   *
   * @return		the color
   */
  public Color getImageBackground() {
    return m_ImageBackground;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageBackgroundTipText() {
    return "The background color to use for the image.";
  }

  /**
   * Returns the default reader.
   *
   * @return		the reader
   */
  protected AbstractReportReader getDefaultReader() {
    ObjectLocationsSpreadSheetReader  result;

    result = new ObjectLocationsSpreadSheetReader();
    result.setColLeft(new SpreadSheetColumnIndex("x0"));
    result.setColTop(new SpreadSheetColumnIndex("y0"));
    result.setColRight(new SpreadSheetColumnIndex("x1"));
    result.setColBottom(new SpreadSheetColumnIndex("y1"));
    result.setColType(new SpreadSheetColumnIndex("label_str"));

    return result;
  }

  /**
   * Sets the reader setup to use for reading the object locations from the spreadsheet.
   *
   * @param value 	the reader
   */
  public void setReader(AbstractReportReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader setup to use for reading the object locations from the spreadsheet.
   *
   * @return 		the reader
   */
  public AbstractReportReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader setup to use for reading the object locations from the spreadsheet.";
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
   * Sets whether to show the panel with the located panels.
   *
   * @param value 	true if to show
   */
  public void setShowObjectPanel(boolean value) {
    m_ShowObjectPanel = value;
    reset();
  }

  /**
   * Returns whether to show the panel with the located objects.
   *
   * @return 		true if to show
   */
  public boolean getShowObjectPanel() {
    return m_ShowObjectPanel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String showObjectPanelTipText() {
    return "If enabled, the panel for selecting located objects is being displayed.";
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return getReader().getFormatExtensions();
  }

  /**
   * Loads the report associated with the image.
   *
   * @param file	the image file
   * @return		the report, null if failed to load report data or none available
   */
  protected Report loadAnnotations(File file) {
    Report 		result;
    List<Report> 	reports;

    result   = null;
    if (file.exists() && file.isFile()) {
      m_Reader.setInput(new PlaceholderFile(file));
      reports = m_Reader.read();
      if (reports.size() > 0)
        result = reports.get(0);
    }

    return result;
  }

  /**
   * Creates an empty dummy image that will fit all the annotations.
   * If a dimension cannot be determined (eg no annotations), then default ones are used.
   *
   * @param report	the report to use
   * @return		the dummy image
   * @see		#MISSING_WIDTH
   * @see		#MISSING_HEIGHT
   */
  protected BufferedImage createImage(Report report) {
    BufferedImage	result;
    Graphics2D		g2d;
    int			width;
    int			height;
    int			maxWidth;
    int			maxHeight;
    int			right;
    int			bottom;
    LocatedObjects	objs;

    if (report == null) {
      width  = MISSING_WIDTH;
      height = MISSING_HEIGHT;
    }
    else {
      width     = m_ImageWidth;
      height    = m_ImageHeight;
      maxWidth  = -1;
      maxHeight = -1;

      if ((width <= 0) || (height <= 0)) {
        objs      = LocatedObjects.fromReport(report, m_Prefix);
        for (LocatedObject obj: objs) {
          right     = obj.getX() + obj.getWidth() - 1;
          bottom    = obj.getY() + obj.getHeight() - 1;
          maxWidth  = Math.max(maxWidth, right);
          maxHeight = Math.max(maxHeight, bottom);
        }
      }

      if (width <= 0) {
        if (maxWidth > -1)
          width = maxWidth + 10;
        else
          width = MISSING_WIDTH;
      }

      if (height <= 0) {
        if (maxHeight > -1)
          height = maxHeight + 10;
        else
          height = MISSING_HEIGHT;
      }
    }

    result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    g2d = result.createGraphics();
    g2d.setColor(m_ImageBackground);
    g2d.fillRect(0, 0, result.getWidth(), result.getHeight());
    g2d.dispose();

    return result;
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  public PreviewPanel createPreview(File file) {
    return createPreview(loadAnnotations(file));
  }

  /**
   * Reuses the last preview, if possible.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  public PreviewPanel reusePreview(File file, PreviewPanel previewPanel) {
    return reusePreview(loadAnnotations(file), previewPanel);
  }

  /**
   * Checks whether the class is handled by this content handler.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  @Override
  public boolean canHandle(Class cls) {
    return ClassLocator.isSubclass(Report.class, cls);
  }

  /**
   * Reuses the last preview, if possible.
   *
   * @param obj		the object to create the view for
   * @return		the preview
   */
  @Override
  public PreviewPanel reusePreview(Object obj, PreviewPanel lastPreview) {
    ImagePanel 	panel;

    panel  = (ImagePanel) lastPreview.getComponent();
    panel.setCurrentImage(createImage((Report) obj), panel.getScale());
    panel.setAdditionalProperties((Report) obj);

    return lastPreview;
  }

  /**
   * Creates the actual preview.
   *
   * @param obj		the object to create the view for
   * @return		the preview
   */
  @Override
  public PreviewPanel createPreview(Object obj) {
    ImagePanel 		panel;
    ObjectAnnotations 	overlay;

    overlay = new ObjectAnnotations();
    overlay.setPrefix(m_Prefix);
    overlay.setCleaners(ObjectCopyHelper.copyObjects(m_Cleaners));
    overlay.setShapePlotters(ObjectCopyHelper.copyObjects(m_ShapePlotters));
    overlay.setShapeColors(ObjectCopyHelper.copyObjects(m_ShapeColors));
    overlay.setOutlinePlotters(ObjectCopyHelper.copyObjects(m_OutlinePlotters));
    overlay.setOutlineColors(ObjectCopyHelper.copyObjects(m_OutlineColors));
    overlay.setLabelPlotters(ObjectCopyHelper.copyObjects(m_LabelPlotters));
    overlay.setLabelColors(ObjectCopyHelper.copyObjects(m_LabelColors));
    overlay.setShowObjectPanel(m_ShowObjectPanel);
    panel  = new ImagePanel();
    panel.getUndo().setEnabled(false);
    panel.addImageOverlay(overlay);
    panel.setCurrentImage(createImage((Report) obj), -1);
    panel.setAdditionalProperties((Report) obj);

    return new PreviewPanel(panel, panel.getPaintPanel());
  }
}
