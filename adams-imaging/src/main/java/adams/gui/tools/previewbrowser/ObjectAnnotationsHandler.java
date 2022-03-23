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
 * ObjectAnnotationsHandler.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser;

import adams.core.ObjectCopyHelper;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractReportReader;
import adams.data.io.input.JAIImageReader;
import adams.data.io.input.ObjectLocationsSpreadSheetReader;
import adams.data.report.Report;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
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
 * </pre>
 *
 * <pre>-file-suffix &lt;java.lang.String&gt; (property: fileSuffix)
 * &nbsp;&nbsp;&nbsp;The forced suffix (incl ext) to append to the image name for generating
 * &nbsp;&nbsp;&nbsp;the meta-data file name.
 * &nbsp;&nbsp;&nbsp;default:
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
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ObjectAnnotationsHandler
    extends AbstractContentHandler
    implements ObjectPrefixHandler {

  private static final long serialVersionUID = -6655562227841341465L;

  /** the file suffix to force (incl extension). */
  protected String m_FileSuffix;

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
        "file-suffix", "fileSuffix",
        "");

    m_OptionManager.add(
        "reader", "reader",
        getDefaultReader());

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
   * Sets the forced suffix (incl ext) to append to the image name for generating the meta-data file name.
   *
   * @param value 	the suffix
   */
  public void setFileSuffix(String value) {
    m_FileSuffix = value;
    reset();
  }

  /**
   * Returns the forced suffix (incl ext) to append to the image name for generating the meta-data file name.
   *
   * @return 		the suffix
   */
  public String getFileSuffix() {
    return m_FileSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fileSuffixTipText() {
    return "The forced suffix (incl ext) to append to the image name for generating the meta-data file name.";
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
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new JAIImageReader().getFormatExtensions();
  }

  /**
   * Loads the report associated with the image.
   *
   * @param panel 	the context panel
   * @param file	the image file
   * @return		the report, null if failed to load report data or none available
   */
  protected Report loadAnnotations(ImagePanel panel, File file) {
    Report 		result;
    File		baseFile;
    File 		locFile;
    List<Report> 	reports;

    result   = null;
    baseFile = file;
    if (m_FileSuffix.isEmpty())
      locFile = FileUtils.replaceExtension(baseFile, "." + m_Reader.getDefaultFormatExtension());
    else
      locFile = FileUtils.replaceExtension(baseFile, m_FileSuffix);
    if (locFile.exists() && locFile.isFile()) {
      m_Reader.setInput(new PlaceholderFile(locFile));
      reports = m_Reader.read();
      if (reports.size() > 0)
        result = reports.get(0);
    }

    return result;
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    ImagePanel 		panel;
    Report 		report;
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
    panel  = new ImagePanel();
    panel.getUndo().setEnabled(false);
    panel.addImageOverlay(overlay);
    report = loadAnnotations(panel, file);
    panel.load(file, new JAIImageReader(), -1.0);
    panel.setAdditionalProperties(report);

    return new PreviewPanel(panel, panel.getPaintPanel());
  }

  /**
   * Reuses the last preview, if possible.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  public PreviewPanel reusePreview(File file, PreviewPanel previewPanel) {
    ImagePanel 	panel;
    Report	report;

    panel  = (ImagePanel) previewPanel.getComponent();
    report = loadAnnotations(panel, file);
    panel.load(file, new JAIImageReader(), panel.getScale());
    panel.setAdditionalProperties(report);

    return previewPanel;
  }
}
