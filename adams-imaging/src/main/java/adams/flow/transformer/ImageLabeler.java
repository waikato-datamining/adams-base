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
import adams.core.QuickInfoHelper;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.option.OptionUtils;
import adams.data.conversion.MapToJson;
import adams.data.image.AbstractImageContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.flow.core.Token;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseToggleButton;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.interactionlogging.InteractionEvent;
import adams.gui.visualization.image.interactionlogging.InteractionLoggingFilter;
import adams.gui.visualization.image.interactionlogging.Null;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: 1
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
 * <pre>-zoom &lt;double&gt; (property: zoom)
 * &nbsp;&nbsp;&nbsp;The zoom level in percent.
 * &nbsp;&nbsp;&nbsp;default: 100.0
 * &nbsp;&nbsp;&nbsp;minimum: -1.0
 * &nbsp;&nbsp;&nbsp;maximum: 1600.0
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

  /**
   * Panel for annotating an image.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   */
  public static class LabelerPanel
    extends BasePanel {

    private static final long serialVersionUID = 301202246788374114L;

    /** the field to use in the report. */
    protected Field m_Field;

    /** the labels. */
    protected BaseString[] m_Labels;

    /** whether to use buttons or dropdown list. */
    protected boolean m_UseButtons;

    /** the zoom level. */
    protected double m_Zoom;

    /** the interaction logger to use. */
    protected InteractionLoggingFilter m_InteractionLoggingFilter;

    /** the label buttons. */
    protected BaseToggleButton[] m_ButtonLabels;

    /** the label dropdown list. */
    protected BaseComboBox<String> m_ComboBoxLabels;

    /** the set button. */
    protected BaseButton m_ButtonApply;

    /** the unset button. */
    protected AbstractButton m_ButtonUnset;

    /** the reset button. */
    protected BaseButton m_ButtonReset;

    /** the button group. */
    protected ButtonGroup m_ButtonGroup;

    /** the image panel. */
    protected ImagePanel m_PanelImage;

    /** the current label. */
    protected String m_CurrentLabel;

    /** the current image. */
    protected AbstractImageContainer m_CurrentImage;

    /** the backup of the report. */
    protected Report m_ReportBackup;

    /** the current scale. */
    protected Double m_CurrentScale;

    /**
     * Initializes the panel.
     *
     * @param field 			the field in the report
     * @param labels 			the labels
     * @param useButtons 		whether to use buttons or dropdown list
     * @param zoom 			the zoom
     * @param interactionLoggingFilter 	the interaction logger
     */
    public LabelerPanel(Field field, BaseString[] labels, boolean useButtons, double zoom, InteractionLoggingFilter interactionLoggingFilter) {
      super();

      m_Field                    = field;
      m_Labels                   = labels;
      m_UseButtons               = useButtons;
      m_Zoom                     = zoom;
      m_InteractionLoggingFilter = interactionLoggingFilter;

      initialize();
      initGUI();
      finishInit();
    }

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      if(m_Labels == null)
	return;

      super.initialize();

      m_CurrentLabel = null;
      if (m_Labels.length > 0)
	m_CurrentLabel = m_Labels[0].getValue();
      m_CurrentImage = null;
      m_ReportBackup = null;
      m_CurrentScale = null;
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      int			i;
      JPanel			panelButtons;
      JPanel			panelLeft;
      GridBagLayout 		layout;
      GridBagConstraints 	con;
      int 			gapVertical;
      int 			gapHorizontal;
      List<Component> 		comps;
      JPanel			panel;

      if(m_Labels == null)
	return;

      super.initGUI();

      setLayout(new BorderLayout());

      // buttons
      panelLeft = new JPanel(new BorderLayout(5, 5));
      add(panelLeft, BorderLayout.WEST);

      gapHorizontal = 5;
      gapVertical   = 2;
      layout = new GridBagLayout();
      panelButtons = new JPanel(layout);
      panelLeft.add(new BaseScrollPane(panelButtons), BorderLayout.CENTER);

      comps         = new ArrayList<>();
      m_ButtonGroup = null;
      if (m_UseButtons) {
	m_ButtonGroup  = new ButtonGroup();
	m_ButtonLabels = new BaseToggleButton[m_Labels.length];
	for (i = 0; i < m_Labels.length; i++) {
	  final String label = m_Labels[i].getValue();
	  m_ButtonLabels[i] = new BaseToggleButton(label);
	  m_ButtonLabels[i].addActionListener((ActionEvent e) -> setCurrentLabel(label));
	  if (i == 0)
	    m_ButtonLabels[i].setSelected(true);
	  m_ButtonGroup.add(m_ButtonLabels[i]);
	  comps.add(m_ButtonLabels[i]);
	}
      }
      else {
	m_ComboBoxLabels = new BaseComboBox<>(BaseObject.toStringArray(m_Labels));
	m_ComboBoxLabels.addActionListener((ActionEvent e) -> m_ButtonApply.setEnabled(m_ComboBoxLabels.getSelectedIndex() > -1));
	comps.add(m_ComboBoxLabels);
	m_ButtonApply = new BaseButton("Apply");
	m_ButtonApply.addActionListener((ActionEvent e) -> setCurrentLabel(m_ComboBoxLabels.getSelectedItem()));
	comps.add(m_ButtonApply);
      }

      if (m_UseButtons)
	m_ButtonUnset = new BaseToggleButton("Unset");
      else
	m_ButtonUnset = new BaseButton("Unset");
      m_ButtonUnset.addActionListener((ActionEvent e) -> setCurrentLabel(null));
      comps.add(m_ButtonUnset);
      if (m_ButtonGroup != null)
	m_ButtonGroup.add(m_ButtonUnset);

      m_ButtonReset = new BaseButton("Reset");
      m_ButtonReset.addActionListener((ActionEvent e) -> resetLabels());
      comps.add(m_ButtonReset);

      for (i = 0; i < comps.size(); i++) {
	con = new GridBagConstraints();
	con.anchor  = GridBagConstraints.WEST;
	con.fill    = GridBagConstraints.HORIZONTAL;
	con.gridy   = i;
	con.gridx   = 0;
	con.weightx = 100;
	con.ipadx   = 20;
	con.insets  = new Insets(gapVertical, gapHorizontal, gapVertical, gapHorizontal);
	layout.setConstraints(comps.get(i), con);
	panelButtons.add(comps.get(i));
      }

      // filler at bottom
      panel         = new JPanel();
      con           = new GridBagConstraints();
      con.anchor    = GridBagConstraints.WEST;
      con.fill      = GridBagConstraints.BOTH;
      con.gridy     = comps.size();
      con.gridx     = 0;
      con.weighty   = 100;
      con.gridwidth = GridBagConstraints.REMAINDER;
      layout.setConstraints(panel, con);
      panelButtons.add(panel);

      // image
      m_PanelImage = new ImagePanel();
      m_PanelImage.setShowProperties(true);
      m_PanelImage.setScale(m_Zoom);
      m_PanelImage.setInteractionLoggingFilter((InteractionLoggingFilter) OptionUtils.shallowCopy(m_InteractionLoggingFilter, false, true));
      add(m_PanelImage, BorderLayout.CENTER);
    }

    /**
     * finishes the initialization.
     */
    @Override
    protected void finishInit() {
      if (m_Labels == null)
	return;

      super.finishInit();

      if (!m_UseButtons) {
	if (m_ComboBoxLabels.getItemCount() > 0)
	  m_ComboBoxLabels.setSelectedItem(m_Labels[0].getValue());
      }
      else {
	m_ButtonGroup.clearSelection();
      }
    }

    /**
     * Returns the underlying image panel.
     *
     * @return		the panel
     */
    public ImagePanel getImagePanel() {
      return m_PanelImage;
    }

    /**
     * Sets the label to use from now on.
     *
     * @param label	the label, null if to unset
     */
    protected void setCurrentLabel(String label) {
      Map<String,Object> 	data;

      data = new HashMap<>();
      data.put("old-label", (m_CurrentLabel == null ? UNSET : m_CurrentLabel));
      data.put("new-label", (label == null ? UNSET : label));
      m_PanelImage.getInteractionLoggingFilter().filterInteractionLog(new InteractionEvent(m_PanelImage, new Date(), "change label", data));
      if (m_PanelImage.getAdditionalProperties() != null) {
	m_PanelImage.getAdditionalProperties().addField(m_Field);
	m_PanelImage.getAdditionalProperties().setValue(m_Field, label);
	m_PanelImage.displayProperties();
      }

      m_CurrentLabel = label;
      notifyLabelChange(label);
    }

    /**
     * Sends notifications that the label has changed.
     *
     * @param label	the new label
     */
    protected void notifyLabelChange(String label) {
      Map<String,Object> 	data;

      data = new HashMap<>();
      data.put("new-label", (label == null ? UNSET : label));
      m_PanelImage.getInteractionLoggingFilter().filterInteractionLog(new InteractionEvent(m_PanelImage, new Date(), "change label", data));
    }

    /**
     * Returns the currently used label.
     *
     * @return		the label
     */
    public String getCurrentLabel() {
      return m_CurrentLabel;
    }

    /**
     * Resets all the labels.
     */
    protected void resetLabels() {
      m_PanelImage.getInteractionLoggingFilter().filterInteractionLog(new InteractionEvent(m_PanelImage, new Date(), "reset labels"));
      m_CurrentImage.setReport(m_ReportBackup.getClone());
      m_PanelImage.setCurrentImage(m_CurrentImage, m_PanelImage.getScale());
    }

    /**
     * Pre-selects the label.
     *
     * @param label	the label to use, ignored if null
     */
    public void preselectLabel(String label) {
      int		i;

      if (label == null)
	return;

      for (i = 0; i < m_Labels.length; i++) {
	if (m_Labels[i].getValue().equals(label)) {
	  if (!m_UseButtons)
	    m_ComboBoxLabels.setSelectedIndex(i);
	  m_CurrentLabel = label;
	  break;
	}
      }

      if (m_UseButtons)
        m_ButtonGroup.clearSelection();
    }

    /**
     * Returns the divider location between image and properties.
     *
     * @return		the position
     */
    public int getMainDividerLocation() {
      return m_PanelImage.getMainDividerLocation();
    }

    /**
     * Sets the divider location between image and properties.
     *
     * @param value	the position
     */
    public void setMainDividerLocation(int value) {
      m_PanelImage.setMainDividerLocation(value);
    }

    /**
     * Sets the current image.
     *
     * @param value	the image
     */
    public void setCurrentImage(AbstractImageContainer value) {
      m_ReportBackup = value.getReport().getClone();
      m_CurrentImage = value;
      if (m_PanelImage.getCurrentImage() == null)
	m_PanelImage.setCurrentImage(value, m_Zoom);
      else
	m_PanelImage.setCurrentImage(value, m_PanelImage.getScale());
    }

    /**
     * Sets the current image.
     *
     * @param value	the image
     * @param zoom 	the zoom to use
     */
    public void setCurrentImage(AbstractImageContainer value, double zoom) {
      m_ReportBackup = value.getReport().getClone();
      m_CurrentImage = value;
      m_PanelImage.setCurrentImage(value, zoom);
    }

    /**
     * Returns the current image.
     *
     * @return		the image
     */
    public AbstractImageContainer getCurrentImage() {
      return m_CurrentImage;
    }

    /**
     * Returns the current report.
     *
     * @return		the report
     */
    public Report getCurrentReport() {
      return m_PanelImage.getAdditionalProperties();
    }

    /**
     * Clears the interaction log.
     */
    public void clearInteractionLog() {
      m_PanelImage.clearInteractionLog();
    }

    /**
     * Checks whether there have been any interactions recorded.
     *
     * @return		true if interactions are available
     */
    public boolean hasInteractionLog() {
      return m_PanelImage.hasInteractionLog();
    }

    /**
     * Returns the interaction log.
     *
     * @return		the log, null if nothing recorded
     */
    public List<InteractionEvent> getInteractionLog() {
      return m_PanelImage.getInteractionLog();
    }
  }

  public static final String FIELD_INTERACTIONLOG = "interaction-log";

  public final static String UNSET = "[unset]";

  /** the field to use in the report. */
  protected Field m_Field;

  /** the labels. */
  protected BaseString[] m_Labels;

  /** whether to use buttons or drop-down list. */
  protected boolean m_UseButtons;

  /** the zoom level. */
  protected double m_Zoom;

  /** the interaction logger to use. */
  protected InteractionLoggingFilter m_InteractionLoggingFilter;

  /** whether the dialog got accepted. */
  protected boolean m_Accepted;

  /** the last selected label. */
  protected transient String m_LastLabel;

  /** the last main divider location. */
  protected transient Integer m_LastMainDividerLocation;

  /** the start timestamp. */
  protected transient Date m_StartTimestamp;

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
      "zoom", "zoom",
      100.0, -1.0, 1600.0);

    m_OptionManager.add(
      "interaction-logging-filter", "interactionLoggingFilter",
      new Null());
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
    return new LabelerPanel(
      m_Field,
      m_Labels,
      m_UseButtons,
      m_Zoom,
      m_InteractionLoggingFilter);
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
      try {
	parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
	array  = (JSONArray) parser.parse(value);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to parse old interactions: " + value, e);
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
    ((LabelerPanel) m_Panel).clearInteractionLog();
    ((LabelerPanel) m_Panel).setCurrentImage(cont);
    if (cont.getReport().hasValue(m_Field.getName()))
      ((LabelerPanel) m_Panel).preselectLabel(cont.getReport().getStringValue(m_Field.getName()));
    else
      ((LabelerPanel) m_Panel).preselectLabel(m_LastLabel);
    if (m_LastMainDividerLocation != null)
      ((LabelerPanel) m_Panel).setMainDividerLocation(m_LastMainDividerLocation);
    m_Dialog.setVisible(true);
    deregisterWindow(m_Dialog);
    m_LastLabel               = ((LabelerPanel) m_Panel).getCurrentLabel();
    m_LastMainDividerLocation = ((LabelerPanel) m_Panel).getMainDividerLocation();

    if (m_Accepted) {
      cont = ((LabelerPanel) m_Panel).getCurrentImage();
      cont.setReport(((LabelerPanel) m_Panel).getCurrentReport());
      if (!(m_InteractionLoggingFilter instanceof Null))
	addInterationsToReport(cont.getReport(), ((LabelerPanel) m_Panel).getInteractionLog());
      m_OutputToken = new Token(cont);
    }

    return m_Accepted;
  }
}
