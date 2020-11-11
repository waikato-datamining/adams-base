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
 * ObjectAnnotationPanel.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object;

import adams.core.CleanUpHandler;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.data.RoundingUtils;
import adams.data.image.BufferedImageHelper;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.report.Report;
import adams.env.Environment;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BaseFrame;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.GUIHelper;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.BoundedNumberCheckModel;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.Undo;
import adams.gui.core.UndoHandlerWithQuickAccess;
import adams.gui.event.UndoEvent;
import adams.gui.event.UndoListener;
import adams.gui.visualization.image.interactionlogging.InteractionEvent;
import adams.gui.visualization.image.interactionlogging.InteractionLogManager;
import adams.gui.visualization.image.interactionlogging.InteractionLoggingFilter;
import adams.gui.visualization.object.annotationsdisplay.AbstractAnnotationsDisplayPanel;
import adams.gui.visualization.object.annotationsdisplay.DefaultAnnotationsDisplayGenerator;
import adams.gui.visualization.object.annotator.AbstractAnnotator;
import adams.gui.visualization.object.annotator.BoundingBoxAnnotator;
import adams.gui.visualization.object.annotator.NullAnnotator;
import adams.gui.visualization.object.labelselector.AbstractLabelSelectorPanel;
import adams.gui.visualization.object.labelselector.ButtonSelectorGenerator;
import adams.gui.visualization.object.mouseclick.AbstractMouseClickProcessor;
import adams.gui.visualization.object.mouseclick.AddMetaData;
import adams.gui.visualization.object.mouseclick.MultiProcessor;
import adams.gui.visualization.object.mouseclick.NullProcessor;
import adams.gui.visualization.object.mouseclick.SetLabel;
import adams.gui.visualization.object.mouseclick.ViewObjects;
import adams.gui.visualization.object.overlay.AbstractOverlay;
import adams.gui.visualization.object.overlay.NullOverlay;
import adams.gui.visualization.object.overlay.ObjectLocationsOverlayFromReport;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for annotating objects in images.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ObjectAnnotationPanel
  extends BasePanel
  implements CleanUpHandler, UndoHandlerWithQuickAccess, UndoListener, InteractionLogManager {

  private static final long serialVersionUID = 2804494506168717754L;

  /**
   * For undo/redo.
   */
  public static class AnnotationsState
    implements Serializable {

    private static final long serialVersionUID = -578329279093068996L;

    public Report report;
  }

  /** the zoom factor to use. */
  public final static double ZOOM_FACTOR = 1.4;

  /** the panel for the zoom controls. */
  protected JPanel m_PanelZoom;

  /** the text field for the zoom. */
  protected NumberTextField m_TextZoom;

  /** the button for clearing zoom. */
  protected BaseFlatButton m_ButtonZoomClear;

  /** the button for zooming in. */
  protected BaseFlatButton m_ButtonZoomIn;

  /** the button for zooming out. */
  protected BaseFlatButton m_ButtonZoomOut;

  /** the button for best fit zoom. */
  protected BaseFlatButton m_ButtonZoomBestFit;

  /** the button for applying the zoom. */
  protected BaseFlatButton m_ButtonZoom;

  /** the panel for the undo/redo controls. */
  protected JPanel m_PanelUndo;

  /** the button for performing an undo. */
  protected BaseFlatButton m_ButtonUndo;

  /** the button for performing a redo. */
  protected BaseFlatButton m_ButtonRedo;

  /** the panel for the brightness controls. */
  protected JPanel m_PanelBrightness;

  /** the brightness to use. */
  protected NumberTextField m_TextBrightness;

  /** the button for applying the values. */
  protected BaseFlatButton m_ButtonBrightness;

  /** the panel for the last button controls. */
  protected JPanel m_PanelUsePreviousReport;
  
  /** the button for using the last report. */
  protected BaseFlatButton m_ButtonUsePreviousReport;

  /** the left split panel (label selector | rest). */
  protected BaseSplitPane m_SplitPaneLeft;

  /** the right split pane (image | annotations). */
  protected BaseSplitPane m_SplitPaneRight;

  /** the label selector panel. */
  protected AbstractLabelSelectorPanel m_PanelLabelSelector;

  /** the canvas in use. */
  protected CanvasPanel m_PanelCanvas;

  /** the JScrollPane that embeds the paint panel. */
  protected BaseScrollPane m_ScrollPane;

  /** the annotations panel. */
  protected AbstractAnnotationsDisplayPanel m_PanelAnnotations;

  /** the status bar label. */
  protected BaseStatusBar m_StatusBar;

  /** the overlay. */
  protected AbstractOverlay m_Overlay;

  /** the mouse click processor. */
  protected AbstractMouseClickProcessor m_MouseClickProcessor;

  /** the annotator. */
  protected AbstractAnnotator m_Annotator;

  /** the undo manager. */
  protected Undo m_Undo;

  /** the current label. */
  protected String m_CurrentLabel;

  /** the interaction log. */
  protected List<InteractionEvent> m_InteractionLog;

  /** the report from the previous session. */
  protected Report m_PreviousReport;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Overlay             = new NullOverlay();
    m_MouseClickProcessor = new NullProcessor();
    m_PanelLabelSelector  = null;
    m_CurrentLabel        = null;
    m_InteractionLog      = null;
    m_PreviousReport      = null;
    m_Undo                = new Undo(List.class, true);
    m_Undo.addUndoListener(this);
    setAnnotator(new NullAnnotator());
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JLabel	label;

    super.initGUI();

    setLayout(new BorderLayout());

    // top
    panel = new JPanel(new FlowLayout(0, 0, FlowLayout.LEFT));
    add(panel, BorderLayout.NORTH);

    m_PanelZoom = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_PanelZoom);
    m_TextZoom = new NumberTextField(Type.DOUBLE, "100");
    m_TextZoom.setColumns(5);
    m_TextZoom.setToolTipText("100 = original image size");
    m_TextZoom.setCheckModel(new BoundedNumberCheckModel(Type.DOUBLE, 1.0, null));
    m_TextZoom.addAnyChangeListener((ChangeEvent e) -> m_ButtonZoom.setIcon(GUIHelper.getIcon("validate_blue.png")));
    label = new JLabel("Zoom");
    label.setDisplayedMnemonic('Z');
    label.setLabelFor(m_TextZoom);
    m_PanelZoom.add(label);
    m_PanelZoom.add(m_TextZoom);
    m_ButtonZoom = new BaseFlatButton(GUIHelper.getIcon("validate.png"));
    m_ButtonZoom.setToolTipText("Apply zoom");
    m_ButtonZoom.addActionListener((ActionEvent e) -> {
      setZoom(m_TextZoom.getValue().doubleValue() / 100.0);
      m_ButtonZoom.setIcon(GUIHelper.getIcon("validate.png"));
      update();
    });
    m_PanelZoom.add(m_ButtonZoom);

    m_PanelZoom.add(new JLabel(" "));

    m_ButtonZoomClear = new BaseFlatButton(GUIHelper.getIcon("zoom_clear.png"));
    m_ButtonZoomClear.setToolTipText("Clear zoom");
    m_ButtonZoomClear.addActionListener((ActionEvent e) -> clearZoom());
    m_PanelZoom.add(m_ButtonZoomClear);
    m_ButtonZoomIn = new BaseFlatButton(GUIHelper.getIcon("zoom_in.png"));
    m_ButtonZoomIn.setToolTipText("Zoom in");
    m_ButtonZoomIn.addActionListener((ActionEvent e) -> zoomIn());
    m_PanelZoom.add(m_ButtonZoomIn);
    m_ButtonZoomOut = new BaseFlatButton(GUIHelper.getIcon("zoom_out.png"));
    m_ButtonZoomOut.setToolTipText("Zoom out");
    m_ButtonZoomOut.addActionListener((ActionEvent e) -> zoomOut());
    m_PanelZoom.add(m_ButtonZoomOut);
    m_ButtonZoomBestFit = new BaseFlatButton(GUIHelper.getIcon("zoom_fit.png"));
    m_ButtonZoomBestFit.setToolTipText("Best fit");
    m_ButtonZoomBestFit.addActionListener((ActionEvent e) -> bestFitZoom());
    m_PanelZoom.add(m_ButtonZoomBestFit);

    m_PanelUndo = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_PanelUndo);
    m_PanelUndo.add(new JLabel(" "));
    m_ButtonUndo = new BaseFlatButton(GUIHelper.getIcon("undo.gif"));
    m_ButtonUndo.setToolTipText("Undo changes");
    m_ButtonUndo.addActionListener((ActionEvent e) -> undo());
    m_PanelUndo.add(m_ButtonUndo);
    m_ButtonRedo = new BaseFlatButton(GUIHelper.getIcon("redo.gif"));
    m_ButtonRedo.setToolTipText("Redo changes");
    m_ButtonRedo.addActionListener((ActionEvent e) -> redo());
    m_PanelUndo.add(m_ButtonRedo);

    m_PanelBrightness = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_PanelBrightness);
    m_PanelBrightness.add(new JLabel(" "));
    m_PanelBrightness.add(new JLabel("Brightness"));
    m_TextBrightness = new NumberTextField(Type.DOUBLE, "100");
    m_TextBrightness.setColumns(5);
    m_TextBrightness.setToolTipText("100 = original brightness");
    m_TextBrightness.setCheckModel(new BoundedNumberCheckModel(Type.DOUBLE, 1.0, null));
    m_TextBrightness.addAnyChangeListener((ChangeEvent e) -> m_ButtonBrightness.setIcon(GUIHelper.getIcon("validate_blue.png")));
    m_PanelBrightness.add(m_TextBrightness);
    m_ButtonBrightness = new BaseFlatButton(GUIHelper.getIcon("validate.png"));
    m_ButtonBrightness.setToolTipText("Apply current values");
    m_ButtonBrightness.addActionListener((ActionEvent e) -> {
      m_PanelCanvas.setBrightness(m_TextBrightness.getValue().floatValue());
      m_ButtonBrightness.setIcon(GUIHelper.getIcon("validate.png"));
      update();
    });
    m_PanelBrightness.add(m_ButtonBrightness);

    m_PanelUsePreviousReport = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_PanelUsePreviousReport);
    m_PanelUsePreviousReport.add(new JLabel(" "));
    m_ButtonUsePreviousReport = new BaseFlatButton("Apply previous report");
    m_ButtonUsePreviousReport.setToolTipText("Apply previous report");
    m_ButtonUsePreviousReport.addActionListener((ActionEvent e) -> {
      applyPreviousReport();
      update();
    });
    m_PanelUsePreviousReport.add(m_ButtonUsePreviousReport);
    m_PanelUsePreviousReport.setVisible(false);
    
    // left split pane
    m_SplitPaneLeft = new BaseSplitPane();
    m_SplitPaneLeft.setOneTouchExpandable(true);
    m_SplitPaneLeft.setResizeWeight(0.0);
    add(m_SplitPaneLeft, BorderLayout.CENTER);

    // right split pane
    m_SplitPaneRight = new BaseSplitPane();
    m_SplitPaneRight.setOneTouchExpandable(true);
    m_SplitPaneRight.setResizeWeight(1.0);
    m_SplitPaneLeft.setRightComponent(m_SplitPaneRight);

    m_PanelCanvas = new CanvasPanel();
    m_PanelCanvas.setOwner(this);
    m_PanelCanvas.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	m_MouseClickProcessor.process(ObjectAnnotationPanel.this, e);
      }
    });
    m_ScrollPane = new BaseScrollPane(m_PanelCanvas);
    m_SplitPaneRight.setLeftComponent(m_ScrollPane);

    m_PanelAnnotations = new DefaultAnnotationsDisplayGenerator().generate();
    m_PanelAnnotations.setOwner(this);
    m_SplitPaneRight.setRightComponent(m_PanelAnnotations);

    // bottom
    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    setLeftDividerLocation(0.2);
    setRightDividerLocation(0.75);
  }

  /**
   * Sets whether the zoom controls are visible or not.
   * 
   * @param value	true if visible
   */
  public void setZoomVisible(boolean value) {
    m_PanelZoom.setVisible(value);
  }

  /**
   * Returns whether the zoom controls are visible or not.
   * 
   * @return		true if visible
   */
  public boolean isZoomVisible() {
    return m_PanelZoom.isVisible();
  }

  /**
   * Sets whether the undo controls are visible or not.
   * 
   * @param value	true if visible
   */
  public void setUndoVisible(boolean value) {
    m_PanelUndo.setVisible(value);
  }

  /**
   * Returns whether the undo controls are visible or not.
   * 
   * @return		true if visible
   */
  public boolean isUndoVisible() {
    return m_PanelUndo.isVisible();
  }

  /**
   * Sets whether the brightness controls are visible or not.
   * 
   * @param value	true if visible
   */
  public void setBrightnessVisible(boolean value) {
    m_PanelBrightness.setVisible(value);
  }

  /**
   * Returns whether the brightness controls are visible or not.
   * 
   * @return		true if visible
   */
  public boolean isBrightnessVisible() {
    return m_PanelBrightness.isVisible();
  }

  /**
   * Sets whether the controls for using the previous report are visible or not.
   * 
   * @param value	true if visible
   */
  public void setUsePreviousReportVisible(boolean value) {
    m_PanelUsePreviousReport.setVisible(value);
  }

  /**
   * Returns whether the controls for using the previous report are visible or not.
   * 
   * @return		true if visible
   */
  public boolean isUsePreviousReportVisible() {
    return m_PanelUsePreviousReport.isVisible();
  }
  
  /**
   * Sets whether to use best fit or specified scale.
   *
   * @param value	true if to use best fit
   */
  public void setBestFit(boolean value) {
    m_PanelCanvas.setBestFit(value);
  }

  /**
   * Sets whether to use best fit.
   *
   * @return		true if to use best fit
   */
  public boolean getBestFit() {
    return m_PanelCanvas.getBestFit();
  }

  /**
   * Sets the zoom.
   *
   * @param value	the zoom to use (1 = 100%)
   */
  public void setZoom(double value) {
    m_TextZoom.setValue(RoundingUtils.round(value * 100, 1));
    m_ButtonZoom.setIcon(GUIHelper.getIcon("validate.png"));
    m_PanelCanvas.setZoom(value);
  }

  /**
   * Returns the current zoom.
   *
   * @return		the zoom (1 = 100%)
   */
  public double getZoom() {
    return m_PanelCanvas.getZoom();
  }

  /**
   * Returns the actual zoom (taking best fit into account if set).
   *
   * @return		the zoom in use (1 = 100%)
   */
  public double getActualZoom() {
    return m_PanelCanvas.getActualZoom();
  }

  /**
   * Clears the zoom.
   */
  public void clearZoom() {
    m_TextZoom.setValue(100);
    m_ButtonZoom.setIcon(GUIHelper.getIcon("validate.png"));
    setZoom(1.0);
    update();
  }

  /**
   * Zooms in.
   */
  public void zoomIn() {
    m_TextZoom.setValue(RoundingUtils.round(m_TextZoom.getValue().doubleValue() * ZOOM_FACTOR, 1));
    m_ButtonZoom.setIcon(GUIHelper.getIcon("validate.png"));
    setZoom(m_TextZoom.getValue().doubleValue() / 100.0);
    update();
  }

  /**
   * Zooms out.
   */
  public void zoomOut() {
    m_TextZoom.setValue(RoundingUtils.round(m_TextZoom.getValue().doubleValue() / ZOOM_FACTOR, 1));
    m_ButtonZoom.setIcon(GUIHelper.getIcon("validate.png"));
    setZoom(m_TextZoom.getValue().doubleValue() / 100.0);
    update();
  }

  /**
   * Fits the image.
   */
  public void bestFitZoom() {
    setBestFit(true);
    update();
    m_TextZoom.setValue(RoundingUtils.round(getActualZoom() * 100.0, 1));
    m_ButtonZoom.setIcon(GUIHelper.getIcon("validate.png"));
    setBestFit(true);
  }

  /**
   * Clears label, image, annotations and interaction log.
   */
  public void clear() {
    m_CurrentLabel = null;
    setImage(null);
    setReport(new Report());
    annotationsChanged(this);
    clearInteractionLog();
  }

  /**
   * Sets the image to display.
   *
   * @param value	the image, null for none
   */
  public void setImage(BufferedImage value) {
    m_PanelCanvas.setImage(value);
  }

  /**
   * Returns the image on display.
   *
   * @return		the image, null if none set
   */
  public BufferedImage getImage() {
    return m_PanelCanvas.getImage();
  }

  /**
   * Sets the report to display.
   *
   * @param value	the report
   */
  public void setReport(Report value) {
    m_PanelAnnotations.setReport(value);
  }

  /**
   * Returns the current report.
   *
   * @return		the report
   */
  public Report getReport() {
    return m_PanelAnnotations.getReport();
  }

  /**
   * Sets the previous report.
   *
   * @param value	the report, null to unset
   */
  public void setPreviousReport(Report value) {
    m_PreviousReport = value;
  }

  /**
   * Returns the previous report.
   *
   * @return		the report, null if not set
   */
  public Report getPreviousReport() {
    return m_PreviousReport;
  }

  /**
   * Applies the previous report, if possible.
   */
  protected void applyPreviousReport() {
    Report	report;

    if (m_PreviousReport == null)
      return;

    addUndoPoint("Applying previous report");
    report = getReport().getClone();
    report.removeValuesStartingWith(m_PanelAnnotations.getPrefix());
    report.mergeWith(m_PreviousReport);
    setReport(report);
    annotationsChanged(this);
  }

  /**
   * Sets the located objects to use.
   *
   * @param value	the report
   */
  public void setObjects(LocatedObjects value) {
    m_PanelAnnotations.setObjects(value);
  }

  /**
   * Returns the current located objects.
   *
   * @return		the objects
   */
  public LocatedObjects getObjects() {
    return m_PanelAnnotations.getObjects();
  }

  /**
   * Sets the brightness to use.
   *
   * @param value	the brightness (100 = default)
   */
  public void setBrightness(float value) {
    m_TextBrightness.setValue(value);
    m_ButtonBrightness.doClick();
  }

  /**
   * Returns the brightness in use.
   *
   * @return		the brightness (100 = default)
   */
  public float getBrightness() {
    return m_TextBrightness.getValue().floatValue();
  }

  /**
   * Sets the undo manager to use, can be null if no undo-support wanted.
   *
   * @param value	the undo manager to use
   */
  @Override
  public void setUndo(Undo value) {
    if (m_Undo != null)
      m_Undo.removeUndoListener(this);
    m_Undo = value;
    m_Undo.addUndoListener(this);
  }

  /**
   * Returns the current undo manager, can be null.
   *
   * @return		the undo manager, if any
   */
  @Override
  public Undo getUndo() {
    return m_Undo;
  }

  /**
   * Returns whether an Undo manager is currently available.
   *
   * @return		true if an undo manager is set
   */
  @Override
  public boolean isUndoSupported() {
    return (m_Undo != null);
  }

  /**
   * performs an undo if possible.
   */
  public void undo() {
    if (!getUndo().canUndo())
      return;
    getUndo().undo();
  }

  /**
   * performs a redo if possible.
   */
  public void redo() {
    if (!getUndo().canRedo())
      return;
    getUndo().redo();
  }

  /**
   * Returns the current state.
   *
   * @return		the state
   */
  public AnnotationsState getState() {
    AnnotationsState	result;

    result = new AnnotationsState();
    result.report = getReport();

    return result;
  }

  /**
   * Restores the state of the layer.
   *
   * @param state	the state
   */
  public void setState(AnnotationsState state) {
    if (state.report != null)
      setReport(state.report);
  }

  /**
   * Adds an undo point with the given comment.
   *
   * @param comment	the comment for the undo point
   */
  @Override
  public void addUndoPoint(String comment) {
    if (isUndoSupported() && getUndo().isEnabled())
      getUndo().addUndo(getState(), comment);
  }

  /**
   * An undo event, like add or remove, has occurred.
   *
   * @param e		the trigger event
   */
  public void undoOccurred(UndoEvent e) {
    switch (e.getType()) {
      case UNDO:
	getUndo().addRedo(getState(), e.getUndoPoint().getComment());
	setState((AnnotationsState) e.getUndoPoint().getData());
	annotationsChanged(this);
	break;
      case REDO:
	getUndo().addUndo(getState(), e.getUndoPoint().getComment(), true);
	setState((AnnotationsState) e.getUndoPoint().getData());
	annotationsChanged(this);
      default:
	break;
    }
  }

  /**
   * Pre-selects the label.
   *
   * @param label	the label to use
   */
  public void preselectCurrentLabel(String label) {
    if (m_PanelLabelSelector != null)
      m_PanelLabelSelector.preselectCurrentLabel(label);
  }

  /**
   * Sets the current label to use.
   *
   * @param value	the label, null to unset
   */
  public void setCurrentLabel(String value) {
    m_CurrentLabel = value;
  }

  /**
   * Returns the current label in use.
   *
   * @return		the label, null if not set
   */
  public String getCurrentLabel() {
    return m_CurrentLabel;
  }

  /**
   * Sets the label selector panel.
   *
   * @param value	the panel, null to hide
   */
  public void setLabelSelectorPanel(AbstractLabelSelectorPanel value) {
    m_PanelLabelSelector = value;
    if (m_SplitPaneLeft.getLeftComponent() != null)
      m_SplitPaneLeft.remove(m_SplitPaneLeft.getLeftComponent());
    if (value == null) {
      m_SplitPaneLeft.setLeftComponentHidden(true);
    }
    else {
      value.setOwner(this);
      m_SplitPaneLeft.setLeftComponent(value);
    }
  }

  /**
   * Returns the label selector panel.
   *
   * @return		the panel, null if none available
   */
  public AbstractLabelSelectorPanel getLabelSelectorPanel() {
    return (AbstractLabelSelectorPanel) m_SplitPaneLeft.getLeftComponent();
  }

  /**
   * Sets the annotations panel.
   *
   * @param value	the panel to use
   */
  public void setAnnotationsPanel(AbstractAnnotationsDisplayPanel value) {
    m_PanelAnnotations.cleanUp();
    m_PanelAnnotations = value;
    m_SplitPaneRight.setRightComponent(m_PanelAnnotations);
  }

  /**
   * Returns the annotations panel.
   *
   * @return		the panel in use
   */
  public AbstractAnnotationsDisplayPanel getAnnotationsPanel() {
    return m_PanelAnnotations;
  }

  /**
   * Returns the scrollpane.
   *
   * @return		the scroll pane
   */
  public BaseScrollPane getScrollPane() {
    return m_ScrollPane;
  }

  /**
   * Returns the canvas.
   *
   * @return		the panel for drawing
   */
  public CanvasPanel getCanvas() {
    return m_PanelCanvas;
  }

  /**
   * Sets the location of the left divider.
   *
   * @param value	the position in pixels
   */
  public void setLeftDividerLocation(int value) {
    m_SplitPaneLeft.setDividerLocation(value);
  }

  /**
   * Sets the proportional location for the left divider.
   *
   * @param value	the location (0-1)
   */
  public void setLeftDividerLocation(double value) {
    m_SplitPaneLeft.setDividerLocation(value);
  }

  /**
   * Returns the left divider location.
   *
   * @return		the position in pixels
   */
  public int getLeftDividerLocation() {
    return m_SplitPaneLeft.getDividerLocation();
  }

  /**
   * Sets the location of the right divider.
   *
   * @param value	the position in pixels
   */
  public void setRightDividerLocation(int value) {
    m_SplitPaneRight.setDividerLocation(value);
  }

  /**
   * Sets the proportional location for the right divider.
   *
   * @param value	the location (0-1)
   */
  public void setRightDividerLocation(double value) {
    m_SplitPaneRight.setDividerLocation(value);
  }

  /**
   * Returns the right divider location.
   *
   * @return		the position in pixels
   */
  public int getRightDividerLocation() {
    return m_SplitPaneRight.getDividerLocation();
  }

  /**
   * Sets the overlay to use.
   *
   * @param value	the overlay
   */
  public void setOverlay(AbstractOverlay value) {
    m_Overlay = value;
  }

  /**
   * Returns the current overlay.
   *
   * @return		the overlay
   */
  public AbstractOverlay getOverlay() {
    return m_Overlay;
  }

  /**
   * Sets the mouse click processor to use.
   *
   * @param value	the processor
   */
  public void setMouseClickProcessor(AbstractMouseClickProcessor value) {
    m_MouseClickProcessor = value;
  }

  /**
   * Returns the current mouse click processor.
   *
   * @return		the processor
   */
  public AbstractMouseClickProcessor getMouseClickProcessor() {
    return m_MouseClickProcessor;
  }

  /**
   * For notifying everyone that the annotations have changed
   *
   * @param source 	the source triggering the change
   */
  public void annotationsChanged(Object source) {
    if (source != m_Overlay)
      m_Overlay.annotationsChanged();
    update();
  }

  /**
   * For notifying everyone that the label have changed
   *
   * @param source 	the source triggering the change
   */
  public void labelChanged(Object source) {
    if ((m_PanelLabelSelector != null) && (source != m_PanelLabelSelector))
      m_PanelLabelSelector.setCurrentLabel(m_CurrentLabel);
    if ((m_Annotator != null) && (source != m_Annotator))
      m_Annotator.labelChanged();
    update();
  }

  /**
   * Sets and installs the annotator.
   *
   * @param value	the annotator
   */
  public void setAnnotator(AbstractAnnotator value) {
    if (m_Annotator != null)
      m_Annotator.uninstall();
    m_Annotator = value;
    m_Annotator.setOwner(this);
    m_Annotator.install();
  }

  /**
   * Returns the current annotator.
   *
   * @return		the annotator
   */
  public AbstractAnnotator getAnnotator() {
    return m_Annotator;
  }

  /**
   * Updates the status bar.
   */
  public void updateStatus() {
    Point	pos;

    pos = getMousePosition();
    if (pos != null)
      updateStatus(pos.getLocation());
  }

  /**
   * Updates the status bar.
   *
   * @param pos	the mouse position
   */
  public void updateStatus(Point pos) {
    Point	loc;

    loc = mouseToPixelLocation(pos);
    showStatus(
      "X: " + (int) (loc.getX() + 1)
	+ "   "
	+ "Y: " + (int) (loc.getY() + 1)
	+ "   "
	+ "Zoom: " + Utils.doubleToString(getZoom() * 100, 1) + "%");
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    m_StatusBar.showStatus(msg);
  }

  /**
   * Updates the state of the buttons.
   */
  public void updateButtons() {
    m_ButtonUndo.setEnabled(getUndo().canUndo());
    m_ButtonRedo.setEnabled(getUndo().canRedo());
    m_ButtonUsePreviousReport.setEnabled(m_PreviousReport != null);
  }

  /**
   * Updates the image, buttons, status.
   */
  public void update() {
    updateButtons();
    updateStatus();
    m_PanelCanvas.update();
  }

  /**
   * Updates the image.
   *
   * @param doLayout 	whether to update the layout
   */
  public void update(boolean doLayout) {
    m_PanelCanvas.update(doLayout);
  }

  /**
   * Turns the mouse position into pixel location.
   * Limits the pixel position to the size of the image, i.e., no negative
   * pixel locations or ones that exceed the image size are generated.
   *
   * @param mousePos	the mouse position
   * @return		the pixel location
   */
  public Point mouseToPixelLocation(Point mousePos) {
    return m_PanelCanvas.mouseToPixelLocation(mousePos);
  }

  /**
   * Converts the pixel position (at 100% scale) to a mouse location.
   *
   * @param pixelPos	the pixel position
   * @return		the mouse position
   */
  public Point pixelToMouseLocation(Point pixelPos) {
    return m_PanelCanvas.pixelToMouseLocation(pixelPos);
  }

  /**
   * Sets the interaction log filter to use.
   *
   * @param value	the filter
   */
  public void setInteractionLoggingFilter(InteractionLoggingFilter value) {
    m_PanelCanvas.setInteractionLoggingFilter(value);
  }

  /**
   * Returns the interaction log filter in use.
   *
   * @return		the filter
   */
  public InteractionLoggingFilter getInteractionLoggingFilter() {
    return m_PanelCanvas.getInteractionLoggingFilter();
  }

  /**
   * Clears the interaction log.
   */
  @Override
  public void clearInteractionLog() {
    m_InteractionLog = null;
  }

  /**
   * Adds the interaction event to the log.
   *
   * @param e		the event to add
   */
  @Override
  public void addInteractionLog(InteractionEvent e) {
    if (m_InteractionLog == null)
      m_InteractionLog = new ArrayList<>();
    m_InteractionLog.add(e);
  }

  /**
   * Checks whether there have been any interactions recorded.
   *
   * @return		true if interactions are available
   */
  @Override
  public boolean hasInteractionLog() {
    return (m_InteractionLog != null);
  }

  /**
   * Returns the interaction log.
   *
   * @return		the log, null if nothing recorded
   */
  @Override
  public List<InteractionEvent> getInteractionLog() {
    return m_InteractionLog;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (m_PanelAnnotations != null)
      m_PanelAnnotations.cleanUp();
    if (m_Overlay != null)
      m_Overlay.cleanUp();
  }

  /**
   * For testing only.
   *
   * @param args	the image to display
   * @throws Exception	if something goes wrong
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    ObjectAnnotationPanel panel = new ObjectAnnotationPanel();
    File img = new File(args[0]);
    panel.setImage(BufferedImageHelper.read(img).getImage());
    panel.setBestFit(true);
    ButtonSelectorGenerator labelGen = new ButtonSelectorGenerator();
    labelGen.setLabels(new BaseString[]{new BaseString("Car"), new BaseString("Bike")});
    panel.setLabelSelectorPanel(labelGen.generate(panel));
    panel.setAnnotator(new BoundingBoxAnnotator());
    panel.setOverlay(new ObjectLocationsOverlayFromReport());
    MultiProcessor multiproc = new MultiProcessor();
    multiproc.addProcessor(new SetLabel());
    ViewObjects view = new ViewObjects();
    view.setShiftDown(true);
    multiproc.addProcessor(view);
    AddMetaData add = new AddMetaData();
    add.setShiftDown(true);
    add.setCtrlDown(true);
    multiproc.addProcessor(add);
    panel.setMouseClickProcessor(multiproc);
    DefaultSimpleReportReader reader = new DefaultSimpleReportReader();
    reader.setInput(new PlaceholderFile(args[1]));
    List<Report> reports = reader.read();
    panel.setReport(reports.get(0));
    BaseFrame frame = new BaseFrame("Object annotations");
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.setSize(GUIHelper.makeWider(GUIHelper.getDefaultLargeDialogDimension()));
    frame.setLocationRelativeTo(null);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    frame.setVisible(true);
    panel.setLeftDividerLocation(0.1);
    panel.setRightDividerLocation(0.75);
    panel.update();
  }
}
