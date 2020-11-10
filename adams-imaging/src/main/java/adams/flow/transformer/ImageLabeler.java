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
 * ImageLabeler.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.data.conversion.MapToJson;
import adams.data.image.AbstractImageContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.flow.core.Token;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.visualization.image.interactionlogging.InteractionEvent;
import adams.gui.visualization.image.interactionlogging.InteractionLoggingFilter;
import adams.gui.visualization.image.interactionlogging.Null;
import adams.gui.visualization.object.ObjectAnnotationPanel;
import adams.gui.visualization.object.annotationsdisplay.DefaultAnnotationsDisplayGenerator;
import adams.gui.visualization.object.annotator.ClassificationLabelAnnotator;
import adams.gui.visualization.object.labelselector.AbstractLabelSelectorGenerator;
import adams.gui.visualization.object.labelselector.ButtonSelectorGenerator;
import adams.gui.visualization.object.labelselector.ComboBoxSelectorGenerator;
import adams.gui.visualization.object.mouseclick.NullProcessor;
import adams.gui.visualization.object.overlay.ClassificationLabelTextOverlay;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Allows the user to label images, setting a report field in the meta-data.<br>
 * Any logged interaction will get added as JSON under interaction-log in the report.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * - generates:<br>
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
 * &nbsp;&nbsp;&nbsp;default: ImageLabeler
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
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 1200
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -2
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -2
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-stop-if-canceled &lt;boolean&gt; (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-stop-mode &lt;GLOBAL|STOP_RESTRICTOR&gt; (property: stopMode)
 * &nbsp;&nbsp;&nbsp;The stop mode to use.
 * &nbsp;&nbsp;&nbsp;default: GLOBAL
 * </pre>
 *
 * <pre>-field &lt;adams.data.report.Field&gt; (property: field)
 * &nbsp;&nbsp;&nbsp;The field to use for the chosen label.
 * &nbsp;&nbsp;&nbsp;default: Classification[S]
 * </pre>
 *
 * <pre>-label &lt;adams.core.base.BaseString&gt; [-label ...] (property: labels)
 * &nbsp;&nbsp;&nbsp;The labels to use.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-use-buttons &lt;boolean&gt; (property: useButtons)
 * &nbsp;&nbsp;&nbsp;If enabled, buttons are used for selecting the label rather than a drop-down
 * &nbsp;&nbsp;&nbsp;list.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 * <pre>-overlay &lt;adams.gui.visualization.object.overlay.ClassificationLabelTextOverlay&gt; (property: overlay)
 * &nbsp;&nbsp;&nbsp;The overlay to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.object.overlay.ClassificationLabelTextOverlay
 * </pre>
 *
 * <pre>-left-divider-location &lt;int&gt; (property: leftDividerLocation)
 * &nbsp;&nbsp;&nbsp;The position for the left divider in pixels.
 * &nbsp;&nbsp;&nbsp;default: 200
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-right-divider-location &lt;int&gt; (property: rightDividerLocation)
 * &nbsp;&nbsp;&nbsp;The position for the right divider in pixels.
 * &nbsp;&nbsp;&nbsp;default: 900
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-zoom &lt;double&gt; (property: zoom)
 * &nbsp;&nbsp;&nbsp;The zoom level in percent.
 * &nbsp;&nbsp;&nbsp;default: 100.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0
 * &nbsp;&nbsp;&nbsp;maximum: 1600.0
 * </pre>
 *
 * <pre>-best-fit &lt;boolean&gt; (property: bestFit)
 * &nbsp;&nbsp;&nbsp;If enabled, the image gets fitted into the viewport.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-interaction-logging-filter &lt;adams.gui.visualization.image.interactionlogging.InteractionLoggingFilter&gt; (property: interactionLoggingFilter)
 * &nbsp;&nbsp;&nbsp;The interaction logger to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.image.interactionlogging.Null
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageLabeler
  extends AbstractInteractiveTransformerDialog {

  private static final long serialVersionUID = -3374468402777151698L;

  public static final String FIELD_INTERACTIONLOG = "interaction-log";

  /** the field to use in the report. */
  protected Field m_Field;

  /** the labels. */
  protected BaseString[] m_Labels;

  /** whether to use buttons or drop-down list. */
  protected boolean m_UseButtons;

  /** the overlay. */
  protected ClassificationLabelTextOverlay m_Overlay;

  /** the position for the left divider. */
  protected int m_LeftDividerLocation;

  /** the position for the right divider. */
  protected int m_RightDividerLocation;

  /** the zoom level. */
  protected double m_Zoom;

  /** whether to use best fit. */
  protected boolean m_BestFit;

  /** the interaction logger to use. */
  protected InteractionLoggingFilter m_InteractionLoggingFilter;

  /** whether the dialog got accepted. */
  protected boolean m_Accepted;

  /** the start timestamp. */
  protected transient Date m_StartTimestamp;

  /** the panel. */
  protected ObjectAnnotationPanel m_PanelObjectAnnotation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Allows the user to label images, setting a report field in the meta-data.\n"
	+ "Any logged interaction will get added as JSON under "
	+ FIELD_INTERACTIONLOG + " in the report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "field", "field",
      new Field("Classification", DataType.STRING));

    m_OptionManager.add(
      "label", "labels",
      new BaseString[0]);

    m_OptionManager.add(
      "use-buttons", "useButtons",
      true);

    m_OptionManager.add(
      "overlay", "overlay",
      new ClassificationLabelTextOverlay());

    m_OptionManager.add(
      "left-divider-location", "leftDividerLocation",
      200, 1, null);

    m_OptionManager.add(
      "right-divider-location", "rightDividerLocation",
      900, 1, null);

    m_OptionManager.add(
      "zoom", "zoom",
      100.0, 1.0, 1600.0);

    m_OptionManager.add(
      "best-fit", "bestFit",
      false);

    m_OptionManager.add(
      "interaction-logging-filter", "interactionLoggingFilter",
      new Null());
  }

  /**
   * Returns the default X position for the dialog.
   *
   * @return		the default X position
   */
  @Override
  protected int getDefaultX() {
    return -2;
  }

  /**
   * Returns the default Y position for the dialog.
   *
   * @return		the default Y position
   */
  @Override
  protected int getDefaultY() {
    return -2;
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 1200;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 800;
  }

  /**
   * Sets the field to use for the label.
   *
   * @param value 	the field
   */
  public void setField(Field value) {
    if (value.getDataType() == DataType.STRING) {
      m_Field = value;
      reset();
    }
    else {
      getLogger().warning("Data type of field must be string, but received: " + value);
    }
  }

  /**
   * Returns the field to use for the label.
   *
   * @return 		the field
   */
  public Field getField() {
    return m_Field;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldTipText() {
    return "The field to use for the chosen label.";
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
   * Sets the labels to use.
   *
   * @param value 	the labels
   */
  public void setUseButtons(boolean value) {
    m_UseButtons = value;
    reset();
  }

  /**
   * Returns whether to use buttons instead of drop-down list.
   *
   * @return 		true if to use buttons
   */
  public boolean getUseButtons() {
    return m_UseButtons;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useButtonsTipText() {
    return "If enabled, buttons are used for selecting the label rather than a drop-down list.";
  }

  /**
   * Sets the overlay to use.
   *
   * @param value 	the overlay
   */
  public void setOverlay(ClassificationLabelTextOverlay value) {
    m_Overlay = value;
    reset();
  }

  /**
   * Returns the overlay to use.
   *
   * @return 		the overlay
   */
  public ClassificationLabelTextOverlay getOverlay() {
    return m_Overlay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlayTipText() {
    return "The overlay to use.";
  }

  /**
   * Sets the position for the left divider in pixels.
   *
   * @param value 	the position
   */
  public void setLeftDividerLocation(int value) {
    if (getOptionManager().isValid("leftDividerLocation", value)) {
      m_LeftDividerLocation = value;
      reset();
    }
  }

  /**
   * Returns the position for the left divider in pixels.
   *
   * @return 		the position
   */
  public int getLeftDividerLocation() {
    return m_LeftDividerLocation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String leftDividerLocationTipText() {
    return "The position for the left divider in pixels.";
  }

  /**
   * Sets the position for the right divider in pixels.
   *
   * @param value 	the position
   */
  public void setRightDividerLocation(int value) {
    if (getOptionManager().isValid("rightDividerLocation", value)) {
      m_RightDividerLocation = value;
      reset();
    }
  }

  /**
   * Returns the position for the right divider in pixels.
   *
   * @return 		the position
   */
  public int getRightDividerLocation() {
    return m_RightDividerLocation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rightDividerLocationTipText() {
    return "The position for the right divider in pixels.";
  }

  /**
   * Sets the zoom level in percent (0-1600).
   *
   * @param value 	the zoom, 0-1600
   */
  public void setZoom(double value) {
    if (getOptionManager().isValid("zoom", value)) {
      m_Zoom = value;
      reset();
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
   * Sets whether to use best fit for the image or not.
   *
   * @param value 	true if to use
   */
  public void setBestFit(boolean value) {
    m_BestFit = value;
    reset();
  }

  /**
   * Returns whether to use best fit for the image or not.
   *
   * @return 		true if to use
   */
  public boolean getBestFit() {
    return m_BestFit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bestFitTipText() {
    return "If enabled, the image gets fitted into the viewport.";
  }

  /**
   * Sets the interaction logger to use.
   *
   * @param value 	the logger
   */
  public void setInteractionLoggingFilter(InteractionLoggingFilter value) {
    m_InteractionLoggingFilter = value;
    reset();
  }

  /**
   * Returns the interaction logger in use.
   *
   * @return 		the logger
   */
  public InteractionLoggingFilter getInteractionLoggingFilter() {
    return m_InteractionLoggingFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String interactionLoggingFilterTipText() {
    return "The interaction logger to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "labels", m_Labels, ", labels: ");
    result += QuickInfoHelper.toString(this, "useButtons", (m_UseButtons ? "buttons" : "drop-down list"), ", ");

    return result;
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
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    ClassificationLabelAnnotator	annotator;
    ClassificationLabelTextOverlay	overlay;
    AbstractLabelSelectorGenerator 	labelSelector;

    m_PanelObjectAnnotation = new ObjectAnnotationPanel();
    m_PanelObjectAnnotation.setAnnotationsPanel(new DefaultAnnotationsDisplayGenerator().generate());
    if (m_UseButtons) {
      labelSelector = new ButtonSelectorGenerator();
      ((ButtonSelectorGenerator) labelSelector).setLabels(m_Labels);
    }
    else {
      labelSelector = new ComboBoxSelectorGenerator();
      ((ComboBoxSelectorGenerator) labelSelector).setLabels(m_Labels);
    }
    m_PanelObjectAnnotation.setLabelSelectorPanel(labelSelector.generate(m_PanelObjectAnnotation));
    annotator = new ClassificationLabelAnnotator();
    annotator.setField(m_Field);
    m_PanelObjectAnnotation.setAnnotator(annotator);
    overlay = ObjectCopyHelper.copyObject(m_Overlay);
    overlay.setField(m_Field);
    m_PanelObjectAnnotation.setOverlay(overlay);
    m_PanelObjectAnnotation.setMouseClickProcessor(new NullProcessor());
    m_PanelObjectAnnotation.setZoom(m_Zoom / 100.0);
    m_PanelObjectAnnotation.setBestFit(m_BestFit);
    m_PanelObjectAnnotation.setInteractionLoggingFilter(ObjectCopyHelper.copyObject(m_InteractionLoggingFilter));
    m_PanelObjectAnnotation.setLeftDividerLocation(m_LeftDividerLocation);
    m_PanelObjectAnnotation.setRightDividerLocation(m_RightDividerLocation - m_LeftDividerLocation);
    return m_PanelObjectAnnotation;
  }

  /**
   * Hook method after the dialog got created.
   *
   * @param dialog	the dialog that got just created
   * @param panel	the panel displayed in the frame
   */
  protected void postCreateDialog(final BaseDialog dialog, BasePanel panel) {
    BaseButton	buttonOK;
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
   * Adds the interactions to the report.
   *
   * @param report	the report to add to
   * @param events	the events to add, ignored if null
   */
  protected void addInterationsToReport(Report report, List<InteractionEvent> events) {
    Field	field;
    MapToJson	m2j;
    DateFormat	formatter;
    JSONArray 	array;
    JSONObject	interaction;
    String 	value;
    JSONParser	parser;
    String	msg;

    if (events == null)
      return;

    array     = new JSONArray();
    m2j       = new MapToJson();
    formatter = DateUtils.getTimestampFormatterMsecs();
    field     = new Field(FIELD_INTERACTIONLOG, DataType.STRING);

    // any old interactions?
    if (report.hasValue(field)) {
      value = "" + report.getValue(field);
      if (value.length() > 0) {
	try {
	  parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
	  array = (JSONArray) parser.parse(value);
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to parse old interactions: " + value, e);
	}
      }
    }


    // separator
    if (array.size() > 0) {
      interaction = new JSONObject();
      interaction.put("timestamp", formatter.format(m_StartTimestamp));
      interaction.put("id", "---");
      array.add(interaction);
    }

    // new interactions
    for (InteractionEvent event: events) {
      interaction = new JSONObject();
      interaction.put("timestamp", formatter.format(event.getTimestamp()));
      interaction.put("id", event.getID());
      if (event.getData() != null) {
	m2j.setInput(event.getData());
	msg = m2j.convert();
	if (msg == null) {
	  interaction.put("data", m2j.getOutput());
	}
	else {
	  getLogger().warning("Failed to convert interaction data to JSON: " + event.getData());
	}
      }
      array.add(interaction);
    }

    report.addField(field);
    report.setValue(field, array.toString());
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    AbstractImageContainer	cont;

    m_Accepted = false;

    cont = (AbstractImageContainer) m_InputToken.getPayload();

    m_StartTimestamp = new Date();

    // annotate
    registerWindow(m_Dialog, m_Dialog.getTitle());
    m_PanelObjectAnnotation.clear();
    m_PanelObjectAnnotation.setImage(cont.toBufferedImage());
    m_PanelObjectAnnotation.setReport(cont.getReport().getClone());
    if (cont.getReport().hasValue(m_Field.getName()))
      m_PanelObjectAnnotation.preselectCurrentLabel(cont.getReport().getStringValue(m_Field.getName()));
    else
      m_PanelObjectAnnotation.preselectCurrentLabel(null);
    m_PanelObjectAnnotation.annotationsChanged(this);
    m_PanelObjectAnnotation.labelChanged(this);
    m_Dialog.setVisible(true);
    deregisterWindow(m_Dialog);

    if (m_Accepted) {
      cont.setReport(m_PanelObjectAnnotation.getReport());
      if (!(m_InteractionLoggingFilter instanceof Null))
	addInterationsToReport(cont.getReport(), m_PanelObjectAnnotation.getInteractionLog());
      m_OutputToken = new Token(cont);
    }

    return m_Accepted;
  }
}
