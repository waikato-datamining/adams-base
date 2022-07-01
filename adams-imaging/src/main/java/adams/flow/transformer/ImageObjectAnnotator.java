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
 * ImageObjectAnnotator.java
 * Copyright (C) 2020-2022 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.data.conversion.MapToJson;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.flow.core.Token;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.Undo;
import adams.gui.visualization.image.interactionlogging.InteractionEvent;
import adams.gui.visualization.image.interactionlogging.InteractionLoggingFilter;
import adams.gui.visualization.image.interactionlogging.Null;
import adams.gui.visualization.object.ObjectAnnotationPanel;
import adams.gui.visualization.object.annotationsdisplay.AbstractAnnotationsDisplayGenerator;
import adams.gui.visualization.object.annotationsdisplay.DefaultAnnotationsDisplayGenerator;
import adams.gui.visualization.object.annotator.AbstractAnnotator;
import adams.gui.visualization.object.annotator.AutoAdvanceAnnotator;
import adams.gui.visualization.object.annotator.BoundingBoxAnnotator;
import adams.gui.visualization.object.labelselector.AbstractLabelSelectorGenerator;
import adams.gui.visualization.object.labelselector.ButtonSelectorGenerator;
import adams.gui.visualization.object.mouseclick.AbstractMouseClickProcessor;
import adams.gui.visualization.object.mouseclick.NullProcessor;
import adams.gui.visualization.object.objectannotations.check.AnnotationCheck;
import adams.gui.visualization.object.objectannotations.check.PassThrough;
import adams.gui.visualization.object.overlay.ObjectLocationsOverlayFromReport;
import adams.gui.visualization.object.overlay.Overlay;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * User interface for annotating objects in images.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.awt.image.BufferedImage<br>
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
 * &nbsp;&nbsp;&nbsp;default: ImageObjectAnnotator
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
 * <pre>-annotations-display &lt;adams.gui.visualization.object.annotationsdisplay.AbstractAnnotationsDisplayGenerator&gt; (property: annotationsDisplay)
 * &nbsp;&nbsp;&nbsp;The generator to use for instantiating the annotations display.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.object.annotationsdisplay.DefaultAnnotationsDisplayGenerator
 * </pre>
 *
 * <pre>-annotator &lt;adams.gui.visualization.object.annotator.AbstractAnnotator&gt; (property: annotator)
 * &nbsp;&nbsp;&nbsp;The annotator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.object.annotator.BoundingBoxAnnotator
 * </pre>
 *
 * <pre>-label-selector &lt;adams.gui.visualization.object.labelselector.AbstractLabelSelectorGenerator&gt; (property: labelSelector)
 * &nbsp;&nbsp;&nbsp;The generator for creating the panel with the labels.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.object.labelselector.ButtonSelectorGenerator
 * </pre>
 *
 * <pre>-mouse-click &lt;adams.gui.visualization.object.mouseclick.AbstractMouseClickProcessor&gt; (property: mouseClick)
 * &nbsp;&nbsp;&nbsp;The processor for handling mouse clicks.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.object.mouseclick.NullProcessor
 * </pre>
 *
 * <pre>-overlay &lt;adams.gui.visualization.object.overlay.Overlay&gt; (property: overlay)
 * &nbsp;&nbsp;&nbsp;The overlay to use for visualizing the annotations.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.object.overlay.ObjectLocationsOverlayFromReport -type-color-provider adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-annotation-check &lt;adams.gui.visualization.object.objectannotations.check.AnnotationCheck&gt; (property: annotationCheck)
 * &nbsp;&nbsp;&nbsp;The check to apply to the annotations before enabling the OK button.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.object.objectannotations.check.PassThrough
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
 * <pre>-allow-using-previous-report &lt;boolean&gt; (property: allowUsingPreviousReport)
 * &nbsp;&nbsp;&nbsp;If enabled, allows the user to make use of the previous report (ie annotations
 * &nbsp;&nbsp;&nbsp;); useful when annotations do not change much between images.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-max-undo &lt;int&gt; (property: maxUndo)
 * &nbsp;&nbsp;&nbsp;The maximum undo steps to allow, use -1 for unlimited 0 to turn off (CAUTION:
 * &nbsp;&nbsp;&nbsp; uses copies of images in memory).
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageObjectAnnotator
    extends AbstractInteractiveTransformerDialog {

  private static final long serialVersionUID = -761517109077084448L;

  public static final String FIELD_INTERACTIONLOG = "interaction-log";

  /** the annotations display to use. */
  protected AbstractAnnotationsDisplayGenerator m_AnnotationsDisplay;

  /** the annotator to use. */
  protected AbstractAnnotator m_Annotator;

  /** the label selector to use. */
  protected AbstractLabelSelectorGenerator m_LabelSelector;

  /** the mouse click processor. */
  protected AbstractMouseClickProcessor m_MouseClick;

  /** the overlay to use. */
  protected Overlay m_Overlay;

  /** the annotation check to apply. */
  protected AnnotationCheck m_AnnotationCheck;

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

  /** whether to allow using the previous report. */
  protected boolean m_AllowUsingPreviousReport;

  /** the maximum undo steps. */
  protected int m_MaxUndo;

  /** the panel. */
  protected ObjectAnnotationPanel m_PanelObjectAnnotation;

  /** whether the dialog got accepted. */
  protected boolean m_Accepted;

  /** the start timestamp. */
  protected transient Date m_StartTimestamp;

  /** the previous report. */
  protected Report m_PreviousReport;

  /** the previous label used. */
  protected String m_PreviousLabel;

  /** the OK button. */
  protected BaseButton m_ButtonOK;

  /** the Cancel button. */
  protected BaseButton m_ButtonCancel;

  /** the change listener for updating the OK button. */
  protected ChangeListener m_ChangeListenerAnnotations;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "User interface for annotating objects in images.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "annotations-display", "annotationsDisplay",
        new DefaultAnnotationsDisplayGenerator());

    m_OptionManager.add(
        "annotator", "annotator",
        new BoundingBoxAnnotator());

    m_OptionManager.add(
        "label-selector", "labelSelector",
        new ButtonSelectorGenerator());

    m_OptionManager.add(
        "mouse-click", "mouseClick",
        new NullProcessor());

    m_OptionManager.add(
        "overlay", "overlay",
        new ObjectLocationsOverlayFromReport());

    m_OptionManager.add(
        "annotation-check", "annotationCheck",
        new PassThrough());

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

    m_OptionManager.add(
        "allow-using-previous-report", "allowUsingPreviousReport",
        false);

    m_OptionManager.add(
        "max-undo", "maxUndo",
        Undo.DEFAULT_MAX_UNDO, -1, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_PreviousReport = null;
    m_PreviousLabel  = null;
    m_ChangeListenerAnnotations = (ChangeEvent e) -> checkAnnotations();
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
   * Sets the generator for the annotations display.
   *
   * @param value 	the generator
   */
  public void setAnnotationsDisplay(AbstractAnnotationsDisplayGenerator value) {
    m_AnnotationsDisplay = value;
    reset();
  }

  /**
   * Returns the generator for the annotations display.
   *
   * @return 		the generator
   */
  public AbstractAnnotationsDisplayGenerator getAnnotationsDisplay() {
    return m_AnnotationsDisplay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String annotationsDisplayTipText() {
    return "The generator to use for instantiating the annotations display.";
  }

  /**
   * Sets the annotator to use.
   *
   * @param value 	the annotator
   */
  public void setAnnotator(AbstractAnnotator value) {
    m_Annotator = value;
    reset();
  }

  /**
   * Returns the annotator in use.
   *
   * @return 		the annotator
   */
  public AbstractAnnotator getAnnotator() {
    return m_Annotator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String annotatorTipText() {
    return "The annotator to use.";
  }

  /**
   * Sets the generator to use for creating the panel with the labels.
   *
   * @param value 	the generator
   */
  public void setLabelSelector(AbstractLabelSelectorGenerator value) {
    m_LabelSelector = value;
    reset();
  }

  /**
   * Returns the generator to use for creating the panel with the labels.
   *
   * @return 		the generator
   */
  public AbstractLabelSelectorGenerator getLabelSelector() {
    return m_LabelSelector;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelSelectorTipText() {
    return "The generator for creating the panel with the labels.";
  }

  /**
   * Sets the processor for mouse clicks.
   *
   * @param value 	the processor
   */
  public void setMouseClick(AbstractMouseClickProcessor value) {
    m_MouseClick = value;
    reset();
  }

  /**
   * Returns the processor for mouse clicks.
   *
   * @return 		the processor
   */
  public AbstractMouseClickProcessor getMouseClick() {
    return m_MouseClick;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String mouseClickTipText() {
    return "The processor for handling mouse clicks.";
  }

  /**
   * Sets the overlay for the annotations.
   *
   * @param value 	the overlay
   */
  public void setOverlay(Overlay value) {
    m_Overlay = value;
    reset();
  }

  /**
   * Returns the overlay for the annotations.
   *
   * @return 		the overlay
   */
  public Overlay getOverlay() {
    return m_Overlay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlayTipText() {
    return "The overlay to use for visualizing the annotations.";
  }

  /**
   * Sets the check to apply to the annotations before enabling the OK button.
   *
   * @param value 	the check
   */
  public void setAnnotationCheck(AnnotationCheck value) {
    m_AnnotationCheck = value;
    reset();
  }

  /**
   * Returns the check to apply to the annotations before enabling the OK button.
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
    return "The check to apply to the annotations before enabling the OK button.";
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
   * Sets whether to allow using the previous report.
   *
   * @param value 	true if allowed
   */
  public void setAllowUsingPreviousReport(boolean value) {
    m_AllowUsingPreviousReport = value;
    reset();
  }

  /**
   * Returns whether to allow using the previous report.
   *
   * @return 		true if allowed
   */
  public boolean getAllowUsingPreviousReport() {
    return m_AllowUsingPreviousReport;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String allowUsingPreviousReportTipText() {
    return "If enabled, allows the user to make use of the previous report "
        + "(ie annotations); useful when annotations do not change much between images.";
  }

  /**
   * Sets whether to allow using the previous report.
   *
   * @param value 	true if allowed
   */
  public void setMaxUndo(int value) {
    m_MaxUndo = value;
    reset();
  }

  /**
   * Returns the maximum undo steps.
   *
   * @return 		the maximum (-1: unlimited, 0: off)
   */
  public int getMaxUndo() {
    return m_MaxUndo;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxUndoTipText() {
    return "The maximum undo steps to allow, use -1 for unlimited 0 to turn off (CAUTION: uses copies of images in memory).";
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
    result += QuickInfoHelper.toString(this, "annotator", m_Annotator, ", annotator: ");
    result += QuickInfoHelper.toString(this, "overlay", m_Overlay, ", overlay: ");
    result += QuickInfoHelper.toString(this, "mouseClick", m_MouseClick, ", mouse: ");
    if (m_BestFit)
      result += QuickInfoHelper.toString(this, "bestFit", m_BestFit, "best fit", ", ");
    else
      result += QuickInfoHelper.toString(this, "zoom", m_Zoom, ", zoom: ");
    result += QuickInfoHelper.toString(this, "allowUsingPreviousReport", m_AllowUsingPreviousReport, "can use previous report", ", ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{BufferedImage.class, AbstractImageContainer.class};
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
    if (m_PanelObjectAnnotation != null)
      m_PanelObjectAnnotation.clear();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    m_PanelObjectAnnotation = new ObjectAnnotationPanel();
    m_PanelObjectAnnotation.setAnnotationsPanel(m_AnnotationsDisplay.generate());
    m_PanelObjectAnnotation.setLabelSelectorPanel(m_LabelSelector.generate(m_PanelObjectAnnotation));
    m_PanelObjectAnnotation.setAnnotator(ObjectCopyHelper.copyObject(m_Annotator));
    m_PanelObjectAnnotation.setOverlay(ObjectCopyHelper.copyObject(m_Overlay));
    m_PanelObjectAnnotation.setMouseClickProcessor(ObjectCopyHelper.copyObject(m_MouseClick));
    m_PanelObjectAnnotation.setLeftDividerLocation(m_LeftDividerLocation);
    m_PanelObjectAnnotation.setRightDividerLocation(m_RightDividerLocation - m_LeftDividerLocation);
    m_PanelObjectAnnotation.setZoom(m_Zoom / 100.0);
    m_PanelObjectAnnotation.setBestFit(m_BestFit);
    m_PanelObjectAnnotation.getUndo().setMaxUndo(m_MaxUndo);
    m_PanelObjectAnnotation.getUndo().setEnabled(m_MaxUndo != 0);
    m_PanelObjectAnnotation.setInteractionLoggingFilter(ObjectCopyHelper.copyObject(m_InteractionLoggingFilter));
    m_PanelObjectAnnotation.setUsePreviousReportVisible(m_AllowUsingPreviousReport);
    return m_PanelObjectAnnotation;
  }

  /**
   * Hook method after the dialog got created.
   *
   * @param dialog	the dialog that got just created
   * @param panel	the panel displayed in the frame
   */
  protected void postCreateDialog(final BaseDialog dialog, BasePanel panel) {
    JPanel panelButtons;

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    dialog.getContentPane().add(panelButtons, BorderLayout.SOUTH);

    m_ButtonOK = new BaseButton("OK");
    m_ButtonOK.addActionListener((ActionEvent e) -> {
      m_Accepted = true;
      dialog.setVisible(false);
    });
    panelButtons.add(m_ButtonOK);

    m_ButtonCancel = new BaseButton("Cancel");
    m_ButtonCancel.addActionListener((ActionEvent e) -> {
      m_Accepted = false;
      dialog.setVisible(false);
    });
    panelButtons.add(m_ButtonCancel);
  }

  /**
   * Checks the annotations and updates the OK button accordingly.
   */
  protected void checkAnnotations() {
    String	msg;

    if (m_AnnotationCheck == null)
      return;

    msg = m_AnnotationCheck.checkAnnotations(m_PanelObjectAnnotation.getObjects());
    if (msg == null) {
      m_ButtonOK.setToolTipText(null);
      m_ButtonOK.setEnabled(true);
    }
    else {
      m_ButtonOK.setToolTipText(GUIHelper.processTipText(msg));
      m_ButtonOK.setEnabled(false);
    }
  }

  /**
   * Adds the interactions to the report.
   *
   * @param report	the report to add to
   * @param events	the events to add, ignored if null
   */
  protected void addInteractionsToReport(Report report, List<InteractionEvent> events) {
    Field field;
    MapToJson m2j;
    DateFormat formatter;
    JSONArray array;
    JSONObject	interaction;
    String 	value;
    JSONParser parser;
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
      if (value.isEmpty()) {
        array = new JSONArray();
      }
      else {
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
    BufferedImage		img;
    AbstractImageContainer	imgcont;
    boolean			resetLabel;

    m_Accepted       = false;
    m_StartTimestamp = new Date();
    if (m_AllowUsingPreviousReport)
      m_PreviousReport = m_PanelObjectAnnotation.getReport();
    else
      m_PreviousReport = null;

    if (m_InputToken.hasPayload(BufferedImage.class)) {
      img     = m_InputToken.getPayload(BufferedImage.class);
      imgcont = new BufferedImageContainer();
      imgcont.setImage(img);
    }
    else {
      imgcont = m_InputToken.getPayload(AbstractImageContainer.class);
    }

    // annotate
    registerWindow(m_Dialog, m_Dialog.getTitle());
    m_PanelObjectAnnotation.clear();
    m_PanelObjectAnnotation.setImage(imgcont.toBufferedImage());
    m_PanelObjectAnnotation.setReport(imgcont.getReport());
    m_PanelObjectAnnotation.setPreviousReport(m_PreviousReport);
    m_PanelObjectAnnotation.annotationsChanged(this);
    m_PanelObjectAnnotation.labelChanged(this);
    resetLabel = false;
    if (m_Annotator instanceof AutoAdvanceAnnotator)
      resetLabel = ((AutoAdvanceAnnotator) m_Annotator).getAutoAdvanceLabels();
    if (resetLabel) {
      if (m_PanelObjectAnnotation.getLabelSelectorPanel().getLabels().length > 0)
        m_PanelObjectAnnotation.preselectCurrentLabel(m_PanelObjectAnnotation.getLabelSelectorPanel().getLabels()[0]);
      else
        m_PanelObjectAnnotation.preselectCurrentLabel(null);
    }
    else {
      m_PanelObjectAnnotation.preselectCurrentLabel(m_PreviousLabel);
    }
    m_PanelObjectAnnotation.addAnnotationChangeListener(m_ChangeListenerAnnotations);
    m_Dialog.setVisible(true);
    deregisterWindow(m_Dialog);
    m_PanelObjectAnnotation.removeAnnotationChangeListener(m_ChangeListenerAnnotations);

    if (m_Accepted) {
      imgcont = new BufferedImageContainer();
      imgcont.setImage(m_PanelObjectAnnotation.getImage());
      imgcont.setReport(m_PanelObjectAnnotation.getReport().getClone());
      if (!(m_InteractionLoggingFilter instanceof Null))
        addInteractionsToReport(imgcont.getReport(), m_PanelObjectAnnotation.getInteractionLog());
      m_OutputToken = new Token(imgcont);
    }

    m_PreviousLabel = m_PanelObjectAnnotation.getCurrentLabel();

    return m_Accepted;
  }
}
