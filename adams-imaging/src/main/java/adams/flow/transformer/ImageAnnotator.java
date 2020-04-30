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
 * ImageAnnotator.java
 * Copyright (C) 2016-2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.core.option.OptionUtils;
import adams.data.conversion.MapToJson;
import adams.data.image.AbstractImageContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseToggleButton;
import adams.gui.core.ColorHelper;
import adams.gui.core.KeyUtils;
import adams.gui.event.ImagePanelLeftClickEvent;
import adams.gui.event.ImagePanelLeftClickListener;
import adams.gui.visualization.image.ImageOverlay;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ImagePanel.PaintPanel;
import adams.gui.visualization.image.NullOverlay;
import adams.gui.visualization.image.TypeColorProvider;
import adams.gui.visualization.image.interactionlogging.InteractionEvent;
import adams.gui.visualization.image.interactionlogging.InteractionLoggingFilter;
import adams.gui.visualization.image.interactionlogging.Null;
import adams.gui.visualization.image.selection.NullProcessor;
import adams.gui.visualization.image.selection.SelectionProcessor;
import adams.gui.visualization.image.selection.SelectionProcessorWithLabelSupport;
import adams.gui.visualization.image.selectionshape.RectanglePainter;
import adams.gui.visualization.image.selectionshape.SelectionShapePainter;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
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
 * Allows the user to label objects located on the image and pass on this enriched meta-data.
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
 * &nbsp;&nbsp;&nbsp;default: ImageAnnotator
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
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix to use for the located objects.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-suffix &lt;java.lang.String&gt; (property: suffix)
 * &nbsp;&nbsp;&nbsp;The report field suffix to use for the labels.
 * &nbsp;&nbsp;&nbsp;default: .type
 * </pre>
 *
 * <pre>-label &lt;adams.core.base.BaseString&gt; [-label ...] (property: labels)
 * &nbsp;&nbsp;&nbsp;The labels to use.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-selection-processor &lt;adams.gui.visualization.image.selection.AbstractSelectionProcessor&gt; (property: selectionProcessor)
 * &nbsp;&nbsp;&nbsp;The selection processor to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.image.selection.NullProcessor
 * </pre>
 *
 * <pre>-selection-box-color &lt;java.awt.Color&gt; (property: selectionBoxColor)
 * &nbsp;&nbsp;&nbsp;The color of the selection box.
 * &nbsp;&nbsp;&nbsp;default: #808080
 * </pre>
 * 
 * <pre>-overlay &lt;adams.gui.visualization.image.ImageOverlay&gt; (property: overlay)
 * &nbsp;&nbsp;&nbsp;The overlay to use for highlighting the objects.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.image.NullOverlay
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
public class ImageAnnotator
  extends AbstractInteractiveTransformerDialog {

  private static final long serialVersionUID = -3374468402777151698L;

  /**
   * Panel for annotating an image.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   */
  public static class AnnotatorPanel
    extends BasePanel
    implements ImagePanelLeftClickListener {

    private static final long serialVersionUID = 301202246788374114L;

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

    /** the interaction logger to use. */
    protected InteractionLoggingFilter m_InteractionLoggingFilter;

    /** the label buttons. */
    protected BaseToggleButton[] m_ButtonLabels;

    /** the unset button. */
    protected BaseToggleButton m_ButtonUnset;

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

    /** the rectangles of the located objects. */
    protected LocatedObjects m_Objects;

    /** the current scale. */
    protected Double m_CurrentScale;

    /** the actual selection processor. */
    protected SelectionProcessor m_ActualSelectionProcessor;

    /** the actual overlay to use for highlighting the objects. */
    protected ImageOverlay m_ActualOverlay;

    /**
     * Initializes the panel.
     *
     * @param prefix 			the prefix in the report
     * @param suffix 			the suffix in the report
     * @param labels 			the labels
     * @param selectionProcessor 	the selection process
     * @param selectionShapePainter 	the painter
     * @param overlay 			the overlay
     * @param zoom 			the zoom
     * @param interactionLoggingFilter 	the interaction logger
     */
    public AnnotatorPanel(String prefix, String suffix, BaseString[] labels, SelectionProcessor selectionProcessor, SelectionShapePainter selectionShapePainter, ImageOverlay overlay, double zoom, InteractionLoggingFilter interactionLoggingFilter) {
      super();

      m_Prefix                = prefix;
      m_Suffix                = suffix;
      m_Labels                = labels;
      m_SelectionProcessor    = selectionProcessor;
      m_SelectionShapePainter = selectionShapePainter;
      m_Overlay               = overlay;
      m_Zoom                  = zoom;
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
      m_Objects      = new LocatedObjects();
      m_CurrentScale = null;
      m_ActualSelectionProcessor = null;
      m_ActualOverlay            = null;
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

      m_ButtonGroup = new ButtonGroup();
      comps         = new ArrayList<>();
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

      m_ButtonUnset = new BaseToggleButton("Unset");
      m_ButtonUnset.addActionListener((ActionEvent e) -> setCurrentLabel(null));
      comps.add(m_ButtonUnset);
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
      m_PanelImage.addLeftClickListener(this);
      m_ActualOverlay = (ImageOverlay) OptionUtils.shallowCopy(m_Overlay, false, true);
      if (m_ActualOverlay instanceof TypeColorProvider)
	((TypeColorProvider) m_ActualOverlay).addLocationsUpdatedListeners((ChangeEvent e) -> updateLabelButtons());
      m_PanelImage.addImageOverlay(m_ActualOverlay);
      m_ActualSelectionProcessor = (SelectionProcessor) OptionUtils.shallowCopy(m_SelectionProcessor, false, true);
      m_PanelImage.addSelectionListener(m_ActualSelectionProcessor);
      m_PanelImage.setSelectionShapePainter((SelectionShapePainter) OptionUtils.shallowCopy(m_SelectionShapePainter, false, true));
      m_PanelImage.setSelectionEnabled(true);
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

      if (m_ButtonLabels.length > 0)
	m_ButtonLabels[0].doClick();
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
     * Updates the colors of the label button.
     */
    protected void updateLabelButtons() {
      int			i;
      TypeColorProvider		provider;
      String			label;

      if (m_ActualOverlay instanceof TypeColorProvider) {
        provider = (TypeColorProvider) m_ActualOverlay;
	for (i = 0; i < m_Labels.length; i++) {
	  label = m_Labels[i].getValue();
	  if (provider.hasTypeColor(label))
	    m_ButtonLabels[i].setText("<html><font color=\"" + ColorHelper.toHex(provider.getTypeColor(label)) + "\">&#x2588;</font> " + label + "</html>");
	  else
	    m_ButtonLabels[i].setText(label);
	}
      }
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

      m_CurrentLabel = label;
      notifyLabelChange(label);
    }

    /**
     * Sends notifications that the label has changed.
     *
     * @param label	the new label
     */
    protected void notifyLabelChange(String label) {
      if (m_ActualSelectionProcessor instanceof SelectionProcessorWithLabelSupport)
	((SelectionProcessorWithLabelSupport) m_ActualSelectionProcessor).setLabel(label == null ? "" : label);
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
      updateObjects();
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
          m_ButtonLabels[i].setSelected(true);
          m_CurrentLabel = label;
          break;
	}
      }

      notifyLabelChange(label);
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
     * Reads the object locations from the report.
     */
    protected void updateObjects() {
      Report			report;
      double			actual;
      LocatedObjects		located;

      if (m_CurrentImage == null) {
        m_Objects = new LocatedObjects();
	return;
      }

      report = m_PanelImage.getAdditionalProperties();
      if (report == null)
        report = m_CurrentImage.getReport();
      located = LocatedObjects.fromReport(report, m_Prefix);
      actual  = m_PanelImage.calcActualScale(m_PanelImage.getScale());
      located.scale(actual);
      m_Objects = located;
    }

    /**
     * Notifies the overlay that the image has changed.
     *
     * @param panel	the panel this overlay belongs to
     */
    public void imageChanged(PaintPanel panel) {
      updateObjects();
    }

    /**
     * Logs the addition/removal of a label.
     *
     * @param add	true if label added, false if removed
     * @param object	the affected object
     * @param label 	the label, can be null
     */
    protected void logLabelClick(boolean add, LocatedObject object, String label) {
      Map<String,Object>	data;

      data = new HashMap<>();
      data.putAll(object.getMetaData());
      data.put("x", object.getX());
      data.put("y", object.getY());
      data.put("width", object.getWidth());
      data.put("height", object.getWidth());
      if (object.hasPolygon()) {
	data.put("poly_x", object.getPolygonX());
	data.put("poly_y", object.getPolygonY());
      }
      if (label == null)
        data.put("label", UNSET);
      else
        data.put("label", label);

      m_PanelImage.getInteractionLoggingFilter().filterInteractionLog(
        new InteractionEvent(
          m_PanelImage,
	  new Date(),
	  (add ? "left-click-set-label" : "left-click-remove-label"),
	  data));
    }

    /**
     * Logs the removal of an object.
     *
     * @param object	the removed object
     */
    protected void logObjectRemoval(LocatedObject object) {
      Map<String,Object>	data;

      data = new HashMap<>();
      data.putAll(object.getMetaData());
      data.put("x", object.getX());
      data.put("y", object.getY());
      data.put("width", object.getWidth());
      data.put("height", object.getWidth());
      if (object.hasPolygon()) {
	data.put("poly_x", object.getPolygonX());
	data.put("poly_y", object.getPolygonY());
      }

      m_PanelImage.getInteractionLoggingFilter().filterInteractionLog(
        new InteractionEvent(
          m_PanelImage,
	  new Date(),
	  "remove",
	  data));
    }

    /**
     * Invoked when a left-click happened in an {@link ImagePanel}.
     *
     * @param e		the event
     */
    public void clicked(ImagePanelLeftClickEvent e) {
      boolean			hit;
      Report			report;
      Report			reportNew;
      double			actual;
      boolean   		contained;
      List<LocatedObject>	hits;

      if (KeyUtils.isNoneDown(e.getModifiersEx())) {
	// resized?
	actual = m_PanelImage.calcActualScale(m_PanelImage.getScale());
	if ((m_CurrentScale == null) || (m_CurrentScale != actual)) {
	  updateObjects();
	  m_CurrentScale = actual;
	}

	hit = false;
	report = m_PanelImage.getAdditionalProperties();
	for (LocatedObject obj : m_Objects) {
	  if (obj.hasPolygon())
	    contained = obj.getActualPolygon().contains(e.getPosition());
	  else
	    contained = obj.getActualRectangle().contains(e.getPosition());
	  if (contained) {
	    hit = true;
	    if (m_CurrentLabel == null) {
	      obj.getMetaData().remove(m_Suffix.substring(1));
	      logLabelClick(false, obj, m_CurrentLabel);
	    }
	    else {
	      logLabelClick(true, obj, m_CurrentLabel);
	      obj.getMetaData().put(m_Suffix.substring(1), m_CurrentLabel);
	    }
	    break;
	  }
	}

	if (hit) {
	  report    = m_PanelImage.getAdditionalProperties().getClone();
	  report.removeValuesStartingWith(m_Prefix);
	  reportNew = m_Objects.toReport(m_Prefix);
	  reportNew.mergeWith(report);
	  m_PanelImage.setAdditionalProperties(reportNew);
	  updateObjects();
	}
      }
      else if (KeyUtils.isOnlyShiftDown(e.getModifiersEx())) {
        hits = new ArrayList<>();
	for (LocatedObject obj : m_Objects) {
	  if (obj.hasPolygon())
	    contained = obj.getActualPolygon().contains(e.getPosition());
	  else
	    contained = obj.getActualRectangle().contains(e.getPosition());
	  if (contained) {
	    hits.add(obj);
	  }
	}
	if (hits.size() > 0) {
	  m_Objects.removeAll(hits);
	  report    = m_PanelImage.getAdditionalProperties().getClone();
	  report.removeValuesStartingWith(m_Prefix);
	  reportNew = m_Objects.toReport(m_Prefix);
	  reportNew.mergeWith(report);
	  for (LocatedObject obj: hits)
	    logObjectRemoval(obj);
	  m_PanelImage.setAdditionalProperties(reportNew);
	  updateObjects();
	}
      }
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
      "Allows the user to label objects located on the image and pass on "
	+ "this enriched meta-data.\n"
	+ "Any logged interaction will get added as JSON under "
	+ FIELD_INTERACTIONLOG + " in the report.\n"
	+ "Clicking on objects while holding down the SHIFT key removes them.";
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

    m_OptionManager.add(
      "interaction-logging-filter", "interactionLoggingFilter",
      new Null());
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
    result += QuickInfoHelper.toString(this, "selectionProcessor", m_SelectionProcessor, ", selection: ");
    result += QuickInfoHelper.toString(this, "overlay", m_Overlay, ", overlay: ");
    result += QuickInfoHelper.toString(this, "labels", m_Labels, ", labels: ");

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
    return new AnnotatorPanel(
      m_Prefix,
      m_Suffix,
      m_Labels,
      m_SelectionProcessor,
      m_SelectionShapePainter,
      m_Overlay,
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
    ((AnnotatorPanel) m_Panel).clearInteractionLog();
    ((AnnotatorPanel) m_Panel).setCurrentImage(cont);
    ((AnnotatorPanel) m_Panel).preselectLabel(m_LastLabel);
    if (m_LastMainDividerLocation != null)
      ((AnnotatorPanel) m_Panel).setMainDividerLocation(m_LastMainDividerLocation);
    m_Dialog.setVisible(true);
    deregisterWindow(m_Dialog);
    m_LastLabel               = ((AnnotatorPanel) m_Panel).getCurrentLabel();
    m_LastMainDividerLocation = ((AnnotatorPanel) m_Panel).getMainDividerLocation();

    if (m_Accepted) {
      cont = ((AnnotatorPanel) m_Panel).getCurrentImage();
      cont.setReport(((AnnotatorPanel) m_Panel).getCurrentReport());
      if (!(m_InteractionLoggingFilter instanceof Null))
        addInterationsToReport(cont.getReport(), ((AnnotatorPanel) m_Panel).getInteractionLog());
      m_OutputToken = new Token(cont);
    }

    return m_Accepted;
  }
}
