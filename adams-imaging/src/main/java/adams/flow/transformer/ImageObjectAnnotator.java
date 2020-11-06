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
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.flow.core.Token;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.visualization.object.ObjectAnnotationPanel;
import adams.gui.visualization.object.annotationsdisplay.AbstractAnnotationsDisplayGenerator;
import adams.gui.visualization.object.annotationsdisplay.DefaultAnnotationsDisplayGenerator;
import adams.gui.visualization.object.annotator.AbstractAnnotator;
import adams.gui.visualization.object.annotator.BoundingBoxAnnotator;
import adams.gui.visualization.object.labelselector.AbstractLabelSelectorGenerator;
import adams.gui.visualization.object.labelselector.DefaultLabelSelectorGenerator;
import adams.gui.visualization.object.mouseclick.AbstractMouseClickProcessor;
import adams.gui.visualization.object.mouseclick.NullProcessor;
import adams.gui.visualization.object.overlay.AbstractOverlay;
import adams.gui.visualization.object.overlay.ObjectLocationsOverlayFromReport;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

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
 * <pre>-anbnotations-display &lt;adams.gui.visualization.object.annotationsdisplay.AbstractAnnotationsDisplayGenerator&gt; (property: annotationsDisplay)
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
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.object.labelselector.DefaultLabelSelectorGenerator
 * </pre>
 *
 * <pre>-mouse-click &lt;adams.gui.visualization.object.mouseclick.AbstractMouseClickProcessor&gt; (property: mouseClick)
 * &nbsp;&nbsp;&nbsp;The processor for handling mouse clicks.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.object.mouseclick.NullProcessor
 * </pre>
 *
 * <pre>-overlay &lt;adams.gui.visualization.object.overlay.AbstractOverlay&gt; (property: overlay)
 * &nbsp;&nbsp;&nbsp;The overlay to use for visualizing the annotations.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.object.overlay.ObjectLocationsOverlayFromReport -type-color-provider adams.gui.visualization.core.DefaultColorProvider
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageObjectAnnotator
  extends AbstractInteractiveTransformerDialog {

  private static final long serialVersionUID = -761517109077084448L;

  /** the annotations display to use. */
  protected AbstractAnnotationsDisplayGenerator m_AnnotationsDisplay;

  /** the annotator to use. */
  protected AbstractAnnotator m_Annotator;

  /** the label selector to use. */
  protected AbstractLabelSelectorGenerator m_LabelSelector;

  /** the mouse click processor. */
  protected AbstractMouseClickProcessor m_MouseClick;

  /** the overlay to use. */
  protected AbstractOverlay m_Overlay;

  /** the position for the left divider. */
  protected int m_LeftDividerLocation;

  /** the position for the right divider. */
  protected int m_RightDividerLocation;

  /** the zoom level. */
  protected double m_Zoom;

  /** the panel. */
  protected ObjectAnnotationPanel m_PanelObjectAnnotation;

  /** whether the dialog got accepted. */
  protected boolean m_Accepted;

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
      "anbnotations-display", "annotationsDisplay",
      new DefaultAnnotationsDisplayGenerator());

    m_OptionManager.add(
      "annotator", "annotator",
      new BoundingBoxAnnotator());

    m_OptionManager.add(
      "label-selector", "labelSelector",
      new DefaultLabelSelectorGenerator());

    m_OptionManager.add(
      "mouse-click", "mouseClick",
      new NullProcessor());

    m_OptionManager.add(
      "overlay", "overlay",
      new ObjectLocationsOverlayFromReport());

    m_OptionManager.add(
      "left-divider-location", "leftDividerLocation",
      200, 1, null);

    m_OptionManager.add(
      "right-divider-location", "rightDividerLocation",
      900, 1, null);

    m_OptionManager.add(
      "zoom", "zoom",
      100.0, 1.0, 1600.0);
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
  public void setOverlay(AbstractOverlay value) {
    m_Overlay = value;
    reset();
  }

  /**
   * Returns the overlay for the annotations.
   *
   * @return 		the overlay
   */
  public AbstractOverlay getOverlay() {
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
    result += QuickInfoHelper.toString(this, "zoom", m_Zoom, ", zoom: ");

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
    return m_PanelObjectAnnotation;
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
    JPanel panelButtons;

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
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    BufferedImage		img;
    AbstractImageContainer	imgcont;

    m_Accepted = false;

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
    m_PanelObjectAnnotation.annotationsChanged(this);
    m_Dialog.setVisible(true);
    deregisterWindow(m_Dialog);

    if (m_Accepted) {
      imgcont = new BufferedImageContainer();
      imgcont.setImage(m_PanelObjectAnnotation.getImage());
      imgcont.setReport(m_PanelObjectAnnotation.getReport().getClone());
      m_OutputToken = new Token(imgcont);
    }

    return m_Accepted;
  }
}
