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
 * AnnotateImage.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.io.input.AbstractReportReader;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.io.input.JAIImageReader;
import adams.data.io.output.AbstractReportWriter;
import adams.data.io.output.DefaultSimpleReportWriter;
import adams.data.report.Report;
import adams.flow.transformer.ImageAnnotator.AnnotatorPanel;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.ImageManager;
import adams.gui.visualization.image.ImageOverlay;
import adams.gui.visualization.image.NullOverlay;
import adams.gui.visualization.image.interactionlogging.Null;
import adams.gui.visualization.image.leftclick.AddMetaData;
import adams.gui.visualization.image.selection.NullProcessor;
import adams.gui.visualization.image.selection.SelectionProcessor;
import adams.gui.visualization.image.selectionshape.RectanglePainter;
import adams.gui.visualization.image.selectionshape.SelectionShapePainter;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

/**
 * Allows annotating images and/or modifying their meta-data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AnnotateImage
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3962259305718630395L;

  /**
   * Panel to combine the other panels and allow setting of current file name.
   */
  public class CombinedPanel
    extends BasePanel {

    private static final long serialVersionUID = -5987843428926585139L;

    /** for annotating the image. */
    protected AnnotatorPanel m_PanelAnnotator;

    /** the panel for the buttons. */
    protected JPanel m_PanelButtons;

    /** the save button. */
    protected JButton m_ButtonSave;

    /** the current file. */
    protected File m_CurrentFile;

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_CurrentFile = null;
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      AddMetaData	addMetaData;

      super.initGUI();

      setLayout(new BorderLayout());

      addMetaData = new AddMetaData();
      addMetaData.setCtrlDown(true);
      m_PanelAnnotator = new AnnotatorPanel(m_Prefix, m_Suffix, m_Labels, m_SelectionProcessor, m_SelectionShapePainter, m_Overlay, m_Zoom, new Null());
      m_PanelAnnotator.getImagePanel().addLeftClickListener(addMetaData);
      add(m_PanelAnnotator, BorderLayout.CENTER);

      m_PanelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      add(m_PanelButtons, BorderLayout.SOUTH);

      m_ButtonSave = new BaseButton(ImageManager.getIcon("save.gif"));
      m_ButtonSave.addActionListener((ActionEvent e) -> saveReport(m_PanelAnnotator, m_CurrentFile));
      m_PanelButtons.add(m_ButtonSave);
    }

    /**
     * Sets the current file.
     *
     * @param value	the file
     */
    public void setCurrentFile(File value) {
      m_CurrentFile = value;
    }

    /**
     * Returns the current file.
     *
     * @return		the file
     */
    public File getCurrentFile() {
      return m_CurrentFile;
    }

    /**
     * Sets the current image.
     *
     * @param value	the image
     */
    public void setCurrentImage(AbstractImageContainer value) {
      m_PanelAnnotator.setCurrentImage(value);
    }

    /**
     * Sets the current image.
     *
     * @param value	the image
     * @param zoom 	the zoom to use
     */
    public void setCurrentImage(AbstractImageContainer value, double zoom) {
      m_PanelAnnotator.setCurrentImage(value, zoom);
    }

    /**
     * Returns the annotator panel.
     *
     * @return		the panel
     */
    public AnnotatorPanel getAnnotatorPanel() {
      return m_PanelAnnotator;
    }
  }

  /** the reader to use. */
  protected AbstractReportReader m_Reader;

  /** the alternative file suffix to use. */
  protected String m_AlternativeFileSuffix;

  /** the report writer for updating the file. */
  protected AbstractReportWriter m_Writer;

  /** the prefix to use in the report. */
  protected String m_Prefix;

  /** the suffix to use for the labels. */
  protected String m_Suffix;

  /** the labels. */
  protected BaseString[] m_Labels;

  /** the selection processor to apply. */
  protected SelectionProcessor m_SelectionProcessor;

  /** the painter for the selection shape. */
  protected SelectionShapePainter m_SelectionShapePainter;

  /** the overlay to use for highlighting the objects. */
  protected ImageOverlay m_Overlay;

  /** the zoom level. */
  protected double m_Zoom;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows annotating images and/or modifying their meta-data.\n"
      + "Hold down CTRL key when clicking on an annotation to add meta-data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "reader", "reader",
      new DefaultSimpleReportReader());

    m_OptionManager.add(
      "alternative-file-suffix", "alternativeFileSuffix",
      "-rois");

    m_OptionManager.add(
      "writer", "writer",
      new DefaultSimpleReportWriter());

    m_OptionManager.add(
      "prefix", "prefix",
      "Object.");

    m_OptionManager.add(
      "suffix", "suffix",
      ".type");

    m_OptionManager.add(
      "label", "labels",
      new BaseString[0]);

    m_OptionManager.add(
      "selection-processor", "selectionProcessor",
      new NullProcessor());

    m_OptionManager.add(
      "selection-shape-painter", "selectionShapePainter",
      new RectanglePainter());

    m_OptionManager.add(
      "overlay", "overlay",
      new NullOverlay());

    m_OptionManager.add(
      "zoom", "zoom",
      100.0, -1.0, 1600.0);
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
   * Sets the alternative file suffix to use for locating the associated spreadsheet (eg '-rois').
   *
   * @param value 	the suffix
   */
  public void setAlternativeFileSuffix(String value) {
    m_AlternativeFileSuffix = value;
    reset();
  }

  /**
   * Returns the alternative file suffix to use for locating the associated spreadsheet (eg '-rois').
   *
   * @return 		the suffix
   */
  public String getAlternativeFileSuffix() {
    return m_AlternativeFileSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alternativeFileSuffixTipText() {
    return "The alternative file suffix to use for locating the associated spreadsheet (eg '-rois').";
  }

  /**
   * Sets the field prefix to use for the located objects.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix to use for the located objects.
   *
   * @return 		the field prefix
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
    return "The report field prefix to use for the located objects.";
  }

  /**
   * Sets the field suffix to use for the labels.
   *
   * @param value 	the field suffix
   */
  public void setSuffix(String value) {
    m_Suffix = value;
    reset();
  }

  /**
   * Returns the field suffix to use for the labels.
   *
   * @return 		the field suffix
   */
  public String getSuffix() {
    return m_Suffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suffixTipText() {
    return "The report field suffix to use for the labels.";
  }

  /**
   * Sets the labels to use.
   *
   * @param value 	the labels
   */
  public void setLabels(BaseString[] value) {
    m_Labels = value;
    reset();
  }

  /**
   * Returns the labels to use.
   *
   * @return 		the labels
   */
  public BaseString[] getLabels() {
    return m_Labels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelsTipText() {
    return "The labels to use.";
  }

  /**
   * Sets the selection processor to use.
   *
   * @param value 	the processor
   */
  public void setSelectionProcessor(SelectionProcessor value) {
    m_SelectionProcessor = value;
    reset();
  }

  /**
   * Returns the selection processor in use.
   *
   * @return 		the processor
   */
  public SelectionProcessor getSelectionProcessor() {
    return m_SelectionProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String selectionProcessorTipText() {
    return "The selection processor to use.";
  }

  /**
   * Sets the painter for the selection shape.
   *
   * @param value 	the painter
   */
  public void setSelectionShapePainter(SelectionShapePainter value) {
    m_SelectionShapePainter = value;
    reset();
  }

  /**
   * Returns the painter for the selection shape.
   *
   * @return 		the painter
   */
  public SelectionShapePainter getSelectionShapePainter() {
    return m_SelectionShapePainter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String selectionShapePainterTipText() {
    return "The painter to use for the selection shape.";
  }

  /**
   * Sets the overlay to use for highlighting the objects.
   *
   * @param value 	the overlay
   */
  public void setOverlay(ImageOverlay value) {
    m_Overlay = value;
    reset();
  }

  /**
   * Returns the overlay to use for highlighting the objects.
   *
   * @return 		the overlay
   */
  public ImageOverlay getOverlay() {
    return m_Overlay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlayTipText() {
    return "The overlay to use for highlighting the objects.";
  }

  /**
   * Sets the zoom level in percent (0-1600).
   *
   * @param value 	the zoom, -1 to fit window, or 0-1600
   */
  public void setZoom(double value) {
    if ((value == -1) || ((value > 0) && (value <= 1600))) {
      m_Zoom = value;
      reset();
    }
    else {
      getLogger().warning("Zoom must -1 to fit window or 0 < x < 1600, provided: " + value);
    }
  }

  /**
   * Returns the zoom level in percent.
   *
   * @return 		the zoom
   */
  public double getZoom() {
    return m_Zoom;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String zoomTipText() {
    return "The zoom level in percent.";
  }

  /**
   * Sets the report writer to use for updating the report on disk.
   *
   * @param value 	the writer
   */
  public void setWriter(AbstractReportWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the report writer to use for updating the report on disk.
   *
   * @return 		the writer
   */
  public AbstractReportWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for writing the modified report back to disk.";
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new JAIImageReader().getFormatExtensions();
  }

  /**
   * Determines the report file file for the image.
   *
   * @param file	the image file
   * @param mustExist 	whether the report must exist
   * @return		the report file, null if not available
   */
  protected File determineReportFile(File file, boolean mustExist, boolean reader) {
    String	ext;
    File 	reportFile1;
    File 	reportFile2;

    if (reader)
      ext = m_Reader.getDefaultFormatExtension();
    else
      ext = m_Writer.getDefaultFormatExtension();
    reportFile1 = FileUtils.replaceExtension(file, "." + ext);
    reportFile2 = FileUtils.replaceExtension(file, m_AlternativeFileSuffix + "." + ext);
    if (reportFile2.exists() && reportFile2.isFile())
      reportFile1 = reportFile2;
    if (mustExist && reportFile1.exists() && reportFile1.isFile())
      return reportFile1;
    else if (!mustExist)
      return reportFile1;
    else
      return null;
  }

  /**
   * Loads the report associated with the image file.
   *
   * @param file	the image file
   * @return		the report, null if not present or failed to load
   */
  protected Report loadReport(File file) {
    Report 		result;
    File		reportFile;
    List<Report> 	reports;

    result   = null;
    reportFile = determineReportFile(file, true, true);
    if (reportFile != null) {
      m_Reader.setInput(new PlaceholderFile(reportFile));
      reports = m_Reader.read();
      if (reports.size() > 0)
        result = reports.get(0);
    }

    return result;
  }

  /**
   * Reads the image and any associated report.
   *
   * @param file	the image to read
   * @return		the generated container
   */
  protected BufferedImageContainer loadContainer(File file) {
    BufferedImageContainer 	result;
    Report			report;
    JAIImageReader		reader;

    reader = new JAIImageReader();
    result = reader.read(new PlaceholderFile(file));
    if (result != null) {
      report = loadReport(file);
      if (report != null)
	result.setReport(report);
    }

    return result;
  }

  /**
   * Saves the report of the panel to disk.
   *
   * @param file	the image file for determining the report file
   */
  protected void saveReport(AnnotatorPanel panel, File file) {
    File	reportFile;

    reportFile = determineReportFile(file, false, false);
    if (reportFile != null) {
      m_Writer.setOutput(new PlaceholderFile(reportFile));
      if (!m_Writer.write(panel.getCurrentReport()))
        getLogger().severe("Failed to write report to: " + reportFile);
    }
    else {
      getLogger().severe("Failed to determine report file, cannot save to disk!");
    }
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(final File file) {
    BufferedImageContainer	cont;
    CombinedPanel		combined;

    combined = new CombinedPanel();
    cont  = loadContainer(file);
    if (cont != null)
      combined.setCurrentImage(cont, m_Zoom);

    return new PreviewPanel(combined, combined.getAnnotatorPanel());
  }

  /**
   * Reuses the last preview, if possible.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  public PreviewPanel reusePreview(File file, PreviewPanel previewPanel) {
    CombinedPanel 		panel;
    BufferedImageContainer	cont;

    panel  = (CombinedPanel) previewPanel.getComponent();
    if (panel != null) {
      cont = loadContainer(file);
      if (cont != null) {
	panel.setCurrentFile(file);
	panel.setCurrentImage(cont);
      }
    }
    else {
      previewPanel = createPreview(file);
    }

    return previewPanel;
  }
}
