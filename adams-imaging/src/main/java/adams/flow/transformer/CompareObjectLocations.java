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
 * CompareObjectLocations.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.image.AbstractImageContainer;
import adams.data.io.input.AbstractReportReader;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.report.Report;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseToggleButton;
import adams.gui.visualization.image.ImageOverlay;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Visualizes object locations (annotations and predicted) for the incoming image side-by-side.
 * <br><br>
 * Only forwards the image container when accepted.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: CompareObjectLocations
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-display-type &lt;adams.flow.core.displaytype.AbstractDisplayType&gt; (property: displayType)
 * &nbsp;&nbsp;&nbsp;Determines how to show the display, eg as standalone frame (default) or
 * &nbsp;&nbsp;&nbsp;in the Flow editor window.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.core.displaytype.Default
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 *
 * <pre>-annotations-reader &lt;adams.data.io.input.AbstractReportReader&gt; (property: annotationsReader)
 * &nbsp;&nbsp;&nbsp;The reader to use for loading the annotations (ground truth).
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.DefaultSimpleReportReader
 * </pre>
 *
 * <pre>-annotations-file &lt;adams.core.io.PlaceholderFile&gt; (property: annotationsFile)
 * &nbsp;&nbsp;&nbsp;The file containing the annotations.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-annotations-overlay &lt;adams.gui.visualization.image.ImageOverlay&gt; (property: annotationsOverlay)
 * &nbsp;&nbsp;&nbsp;The overlay to apply to the annotations.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.image.ObjectLocationsOverlayFromReport -type-color-provider adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-annotations-prefix &lt;java.lang.String&gt; (property: annotationsPrefix)
 * &nbsp;&nbsp;&nbsp;The object prefix that the annotations use.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-annotations-label-suffix &lt;java.lang.String&gt; (property: annotationsLabelSuffix)
 * &nbsp;&nbsp;&nbsp;The report suffix that the annotations use for storing the label.
 * &nbsp;&nbsp;&nbsp;default: type
 * </pre>
 *
 * <pre>-predictions-reader &lt;adams.data.io.input.AbstractReportReader&gt; (property: predictionsReader)
 * &nbsp;&nbsp;&nbsp;The reader to use for loading the predictions.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.DefaultSimpleReportReader
 * </pre>
 *
 * <pre>-predictions-file &lt;adams.core.io.PlaceholderFile&gt; (property: predictionsFile)
 * &nbsp;&nbsp;&nbsp;The file containing the predictions.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-predictions-overlay &lt;adams.gui.visualization.image.ImageOverlay&gt; (property: predictionsOverlay)
 * &nbsp;&nbsp;&nbsp;The overlay to apply to the predictions.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.image.ObjectLocationsOverlayFromReport -type-color-provider adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-predictions-prefix &lt;java.lang.String&gt; (property: predictionsPrefix)
 * &nbsp;&nbsp;&nbsp;The object prefix that the predictions use.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-predictions-label-suffix &lt;java.lang.String&gt; (property: predictionsLabelSuffix)
 * &nbsp;&nbsp;&nbsp;The report suffix that the predictions use for storing the label.
 * &nbsp;&nbsp;&nbsp;default: type
 * </pre>
 *
 * <pre>-zoom &lt;double&gt; (property: zoom)
 * &nbsp;&nbsp;&nbsp;The zoom level in percent.
 * &nbsp;&nbsp;&nbsp;default: 100.0
 * &nbsp;&nbsp;&nbsp;minimum: -1.0
 * &nbsp;&nbsp;&nbsp;maximum: 1600.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CompareObjectLocations
  extends AbstractInteractiveTransformerDialog {

  private static final long serialVersionUID = 2191236912048968711L;

  /** the report reader for the annotations. */
  protected AbstractReportReader m_AnnotationsReader;

  /** the annotations file to read. */
  protected PlaceholderFile m_AnnotationsFile;

  /** the image overlays for the annotations. */
  protected ImageOverlay m_AnnotationsOverlay;

  /** the annotations object prefix. */
  protected String m_AnnotationsPrefix;

  /** the annotations label suffix. */
  protected String m_AnnotationsLabelSuffix;

  /** the report reader for the predictions. */
  protected AbstractReportReader m_PredictionsReader;

  /** the predictions file to read. */
  protected PlaceholderFile m_PredictionsFile;

  /** the image overlays for the predictions. */
  protected ImageOverlay m_PredictionsOverlay;

  /** the predictions object prefix. */
  protected String m_PredictionsPrefix;
  
  /** the predictions label suffix. */
  protected String m_PredictionsLabelSuffix;

  /** the zoom level. */
  protected double m_Zoom;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the image panel with the annotations. */
  protected ImagePanel m_PanelImageAnnotations;

  /** the image panel with the predictions. */
  protected ImagePanel m_PanelImagePredictions;

  /** the annotations report. */
  protected Report m_AnnotationsReport;

  /** the located objects / annotations. */
  protected LocatedObjects m_AnnotationsLocatedObjects;

  /** the predictions report. */
  protected Report m_PredictionsReport;

  /** the located objects / predictions. */
  protected LocatedObjects m_PredictionsLocatedObjects;

  /** the panel with the labels. */
  protected JPanel m_PanelLabels;

  /** the toggle buttons. */
  protected List<BaseToggleButton> m_ButtonLabels;

  /** the button group. */
  protected ButtonGroup m_ButtonGroup;

  /** the last selected label. */
  protected String m_LastLabel;

  /** whether the dialog got accepted. */
  protected boolean m_Accepted;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Visualizes object locations (annotations and predicted) for the incoming image side-by-side.\n"
      + "Only forwards the image container when accepted.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "annotations-reader", "annotationsReader",
      new DefaultSimpleReportReader());

    m_OptionManager.add(
      "annotations-file", "annotationsFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "annotations-overlay", "annotationsOverlay",
      new ObjectLocationsOverlayFromReport());

    m_OptionManager.add(
      "annotations-prefix", "annotationsPrefix",
      "Object.");

    m_OptionManager.add(
      "annotations-label-suffix", "annotationsLabelSuffix",
      "type");

    m_OptionManager.add(
      "predictions-reader", "predictionsReader",
      new DefaultSimpleReportReader());

    m_OptionManager.add(
      "predictions-file", "predictionsFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "predictions-overlay", "predictionsOverlay",
      new ObjectLocationsOverlayFromReport());

    m_OptionManager.add(
      "predictions-prefix", "predictionsPrefix",
      "Object.");

    m_OptionManager.add(
      "predictions-label-suffix", "predictionsLabelSuffix",
      "type");

    m_OptionManager.add(
      "zoom", "zoom",
      100.0, -1.0, 1600.0);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_AnnotationsReport         = new Report();
    m_AnnotationsLocatedObjects = new LocatedObjects();
    m_PredictionsReport         = new Report();
    m_PredictionsLocatedObjects = new LocatedObjects();
    m_ButtonLabels = new ArrayList<>();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "annotationsReader", m_AnnotationsReader, ", ann/reader: ");
    result += QuickInfoHelper.toString(this, "annotationsFile", m_AnnotationsFile, ", ann/file: ");
    result += QuickInfoHelper.toString(this, "annotationsOverlay", m_AnnotationsOverlay, ", ann/overlay: ");
    result += QuickInfoHelper.toString(this, "predictionsReader", m_PredictionsReader, ", pred/reader: ");
    result += QuickInfoHelper.toString(this, "predictionsFile", m_PredictionsFile, ", pred/file: ");
    result += QuickInfoHelper.toString(this, "predictionsOverlay", m_PredictionsOverlay, ", pred/overlay: ");

    return result;
  }

  /**
   * Sets the reader to use for the annotations.
   *
   * @param value 	the reader
   */
  public void setAnnotationsReader(AbstractReportReader value) {
    m_AnnotationsReader = value;
    reset();
  }

  /**
   * Returns the reader to use for the annotations.
   *
   * @return 		the reader
   */
  public AbstractReportReader getAnnotationsReader() {
    return m_AnnotationsReader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String annotationsReaderTipText() {
    return "The reader to use for loading the annotations (ground truth).";
  }

  /**
   * Sets the annotations file to read.
   *
   * @param value 	the file
   */
  public void setAnnotationsFile(PlaceholderFile value) {
    m_AnnotationsFile = value;
    reset();
  }

  /**
   * Returns the annotations file to read.
   *
   * @return 		the file
   */
  public PlaceholderFile getAnnotationsFile() {
    return m_AnnotationsFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String annotationsFileTipText() {
    return "The file containing the annotations.";
  }

  /**
   * Sets the overlay to use for the annotations.
   *
   * @param value 	the overlay
   */
  public void setAnnotationsOverlay(ImageOverlay value) {
    m_AnnotationsOverlay = value;
    reset();
  }

  /**
   * Returns the overlay to use for the annotations.
   *
   * @return 		the overlay
   */
  public ImageOverlay getAnnotationsOverlay() {
    return m_AnnotationsOverlay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String annotationsOverlayTipText() {
    return "The overlay to apply to the annotations.";
  }

  /**
   * Sets the object prefix to use for the annotations.
   *
   * @param value 	the object prefix
   */
  public void setAnnotationsPrefix(String value) {
    m_AnnotationsPrefix = value;
    reset();
  }

  /**
   * Returns the object prefix to use for the annotations.
   *
   * @return 		the object prefix
   */
  public String getAnnotationsPrefix() {
    return m_AnnotationsPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String annotationsPrefixTipText() {
    return "The object prefix that the annotations use.";
  }

  /**
   * Sets the report suffix that the annotations use for storing the label.
   *
   * @param value 	the suffix
   */
  public void setAnnotationsLabelSuffix(String value) {
    m_AnnotationsLabelSuffix = value;
    reset();
  }

  /**
   * Returns the report suffix that the annotations use for storing the label.
   *
   * @return 		the suffix
   */
  public String getAnnotationsLabelSuffix() {
    return m_AnnotationsLabelSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String annotationsLabelSuffixTipText() {
    return "The report suffix that the annotations use for storing the label.";
  }

  /**
   * Sets the reader to use for the predictions.
   *
   * @param value 	the reader
   */
  public void setPredictionsReader(AbstractReportReader value) {
    m_PredictionsReader = value;
    reset();
  }

  /**
   * Returns the reader to use for the predictions.
   *
   * @return 		the reader
   */
  public AbstractReportReader getPredictionsReader() {
    return m_PredictionsReader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictionsReaderTipText() {
    return "The reader to use for loading the predictions.";
  }

  /**
   * Sets the predictions file to read.
   *
   * @param value 	the file
   */
  public void setPredictionsFile(PlaceholderFile value) {
    m_PredictionsFile = value;
    reset();
  }

  /**
   * Returns the predictions file to read.
   *
   * @return 		the file
   */
  public PlaceholderFile getPredictionsFile() {
    return m_PredictionsFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictionsFileTipText() {
    return "The file containing the predictions.";
  }

  /**
   * Sets the overlay to use for the predictions.
   *
   * @param value 	the overlay
   */
  public void setPredictionsOverlay(ImageOverlay value) {
    m_PredictionsOverlay = value;
    reset();
  }

  /**
   * Returns the overlay to use for the predictions.
   *
   * @return 		the overlay
   */
  public ImageOverlay getPredictionsOverlay() {
    return m_PredictionsOverlay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictionsOverlayTipText() {
    return "The overlay to apply to the predictions.";
  }

  /**
   * Sets the object prefix to use for the predictions.
   *
   * @param value 	the object prefix
   */
  public void setPredictionsPrefix(String value) {
    m_PredictionsPrefix = value;
    reset();
  }

  /**
   * Returns the object prefix to use for the predictions.
   *
   * @return 		the object prefix
   */
  public String getPredictionsPrefix() {
    return m_PredictionsPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictionsPrefixTipText() {
    return "The object prefix that the predictions use.";
  }

  /**
   * Sets the report suffix that the predictions use for storing the label.
   *
   * @param value 	the suffix
   */
  public void setPredictionsLabelSuffix(String value) {
    m_PredictionsLabelSuffix = value;
    reset();
  }

  /**
   * Returns the report suffix that the predictions use for storing the label.
   *
   * @return 		the suffix
   */
  public String getPredictionsLabelSuffix() {
    return m_PredictionsLabelSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictionsLabelSuffixTipText() {
    return "The report suffix that the predictions use for storing the label.";
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
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{AbstractImageContainer.class};
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    m_PanelImageAnnotations.clear();
    m_PanelImagePredictions.clear();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel 		result;
    JPanel		panel;

    result = new BasePanel();
    result.setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setResizeWeight(0.5);
    result.add(m_SplitPane, BorderLayout.CENTER);

    m_PanelImageAnnotations = new ImagePanel();
    m_PanelImageAnnotations.setBorder(BorderFactory.createTitledBorder("Annotations"));
    m_PanelImageAnnotations.addImageOverlay((ImageOverlay) OptionUtils.shallowCopy(m_AnnotationsOverlay));
    m_SplitPane.setLeftComponent(m_PanelImageAnnotations);

    m_PanelImagePredictions = new ImagePanel();
    m_PanelImagePredictions.setBorder(BorderFactory.createTitledBorder("Predictions"));
    m_PanelImagePredictions.addImageOverlay((ImageOverlay) OptionUtils.shallowCopy(m_PredictionsOverlay));
    m_SplitPane.setRightComponent(m_PanelImagePredictions);

    panel = new JPanel(new BorderLayout());
    result.add(panel, BorderLayout.WEST);
    m_PanelLabels = new JPanel(new GridBagLayout());
    panel.add(new BaseScrollPane(m_PanelLabels), BorderLayout.CENTER);

    m_ButtonGroup = new ButtonGroup();

    return result;
  }

  /**
   * Hook method after the dialog got created.
   *
   * @param dialog	the dialog that got just created
   * @param panel	the panel displayed in the frame
   */
  protected void postCreateDialog(final BaseDialog dialog, BasePanel panel) {
    BaseButton buttonOK;
    BaseButton	buttonCancel;
    JPanel	panelButtons;

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    dialog.getContentPane().add(panelButtons, BorderLayout.SOUTH);

    buttonOK = new BaseButton("OK");
    buttonOK.addActionListener((ActionEvent e) -> {
      m_Accepted = true;
      dialog.setVisible(false);
    });
    panelButtons.add(buttonOK);

    buttonCancel = new BaseButton("Cancel");
    buttonCancel.addActionListener((ActionEvent e) -> {
      m_Accepted = false;
      dialog.setVisible(false);
    });
    panelButtons.add(buttonCancel);
  }

  /**
   * Filters the objects using the specified label and updates the GUI.
   *
   * @param label	the label to restrict display to, empty/null for all
   */
  protected void filterObjects(String label) {
    LocatedObjects	annotations;
    LocatedObjects	predictions;

    if (label == null)
      label = "";

    // annotations
    if (label.isEmpty()) {
      m_PanelImageAnnotations.setAdditionalProperties(m_AnnotationsReport);
    }
    else {
      annotations = new LocatedObjects();
      for (LocatedObject obj : m_AnnotationsLocatedObjects) {
	if (obj.getMetaData().containsKey(m_AnnotationsLabelSuffix)) {
	  if (obj.getMetaData().get(m_AnnotationsLabelSuffix).toString().equals(label))
	    annotations.add(obj.getClone());
	}
      }
      m_PanelImageAnnotations.setAdditionalProperties(annotations.toReport(m_AnnotationsPrefix));
    }

    // predictions
    if (label.isEmpty()) {
      m_PanelImagePredictions.setAdditionalProperties(m_PredictionsReport);
    }
    else {
      predictions = new LocatedObjects();
      for (LocatedObject obj : m_PredictionsLocatedObjects) {
	if (obj.getMetaData().containsKey(m_PredictionsLabelSuffix)) {
	  if (obj.getMetaData().get(m_PredictionsLabelSuffix).toString().equals(label))
	    predictions.add(obj.getClone());
	}
      }
      m_PanelImagePredictions.setAdditionalProperties(predictions.toReport(m_PredictionsPrefix));
    }
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  protected void display(Token token) {
    AbstractImageContainer	cont;
    double			zoom;
    List<Report> 		reports;
    Set<String>			labels;
    List<String>		labelsSorted;
    BaseToggleButton		button;
    GridBagLayout 		layout;
    GridBagConstraints 		con;
    int 			gapVertical;
    int 			gapHorizontal;
    int				i;
    JPanel			panel;

    if (m_Zoom == -1)
      zoom = m_Zoom;
    else
      zoom = m_Zoom / 100.0;

    // read annotations
    try {
      m_AnnotationsReader.setInput(m_AnnotationsFile);
      reports = m_AnnotationsReader.read();
      if (reports.size() != 1)
        getLogger().warning("Expected to find one annotations report, but found: " + reports.size());
      if (reports.size() > 0)
	m_AnnotationsReport = reports.get(0);
      else
        m_AnnotationsReport = new Report();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read annotations report '" + m_AnnotationsFile + "'!", e);
    }
    m_AnnotationsLocatedObjects = LocatedObjects.fromReport(m_AnnotationsReport, m_AnnotationsPrefix);

    // read predictions
    try {
      m_PredictionsReader.setInput(m_PredictionsFile);
      reports = m_PredictionsReader.read();
      if (reports.size() != 1)
        getLogger().warning("Expected to find one predictions report, but found: " + reports.size());
      if (reports.size() > 0)
	m_PredictionsReport = reports.get(0);
      else
        m_PredictionsReport = new Report();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read predictions report '" + m_PredictionsFile + "'!", e);
    }
    m_PredictionsLocatedObjects = LocatedObjects.fromReport(m_PredictionsReport, m_PredictionsPrefix);

    // determine labels
    labels = new HashSet<>();
    for (LocatedObject obj: m_AnnotationsLocatedObjects) {
      if (obj.getMetaData().containsKey(m_AnnotationsLabelSuffix))
	labels.add("" + obj.getMetaData().get(m_AnnotationsLabelSuffix));
    }
    for (LocatedObject obj: m_PredictionsLocatedObjects) {
      if (obj.getMetaData().containsKey(m_PredictionsLabelSuffix))
	labels.add("" + obj.getMetaData().get(m_PredictionsLabelSuffix));
    }
    labelsSorted = new ArrayList<>(labels);
    Collections.sort(labelsSorted);

    // update GUI
    cont = token.getPayload(AbstractImageContainer.class);
    m_PanelImageAnnotations.setCurrentImage(cont);
    m_PanelImageAnnotations.setAdditionalProperties(m_AnnotationsReport);
    m_PanelImageAnnotations.setScale(zoom);
    m_PanelImagePredictions.setCurrentImage(cont);
    m_PanelImagePredictions.setAdditionalProperties(m_PredictionsReport);
    m_PanelImagePredictions.setScale(zoom);

    gapHorizontal = 5;
    gapVertical   = 2;
    layout = new GridBagLayout();
    m_PanelLabels.setLayout(layout);

    for (BaseToggleButton b: m_ButtonLabels)
      m_ButtonGroup.remove(b);
    m_ButtonLabels.clear();
    m_PanelLabels.removeAll();
    button = new BaseToggleButton("All");
    button.addActionListener((ActionEvent e) -> filterObjects(""));
    button.setToolTipText(button.getText());
    m_ButtonGroup.add(button);
    m_ButtonLabels.add(button);
    for (final String label: labelsSorted) {
      button = new BaseToggleButton(label);
      button.addActionListener((ActionEvent e) -> filterObjects(label));
      button.setToolTipText(button.getText());
      m_ButtonGroup.add(button);
      m_ButtonLabels.add(button);
    }

    for (i = 0; i < m_ButtonLabels.size(); i++) {
      con = new GridBagConstraints();
      con.anchor  = GridBagConstraints.WEST;
      con.fill    = GridBagConstraints.HORIZONTAL;
      con.gridy   = i;
      con.gridx   = 0;
      con.weightx = 100;
      con.ipadx   = 20;
      con.insets  = new Insets(gapVertical, gapHorizontal, gapVertical, gapHorizontal);
      layout.setConstraints(m_ButtonLabels.get(i), con);
      m_PanelLabels.add(m_ButtonLabels.get(i));
    }

    // filler at bottom
    panel         = new JPanel();
    con           = new GridBagConstraints();
    con.anchor    = GridBagConstraints.WEST;
    con.fill      = GridBagConstraints.BOTH;
    con.gridy     = m_ButtonLabels.size();
    con.gridx     = 0;
    con.weighty   = 100;
    con.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(panel, con);
    m_PanelLabels.add(panel);

    // use last label again, if possible
    if (!labels.contains(m_LastLabel))
      m_LastLabel = "";
    if (m_LastLabel.isEmpty())
      m_ButtonLabels.get(0).doClick();
    else if (labelsSorted.contains(m_LastLabel))
      m_ButtonLabels.get(labelsSorted.indexOf(m_LastLabel) + 1).doClick();
  }

  /**
   * Performs the interaction with the user.
   * <br><br>
   * Default implementation simply displays the dialog and returns always true.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    m_Accepted = false;

    registerWindow(m_Dialog, m_Dialog.getTitle());
    display(m_InputToken);
    m_Dialog.setVisible(true);
    deregisterWindow(m_Dialog);

    if (m_Accepted)
      m_OutputToken = new Token(m_InputToken.getPayload());

    return m_Accepted;
  }
}
