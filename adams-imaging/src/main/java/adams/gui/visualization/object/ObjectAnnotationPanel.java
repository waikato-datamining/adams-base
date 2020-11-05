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
import adams.gui.visualization.object.annotationsdisplay.AbstractAnnotationsDisplayPanel;
import adams.gui.visualization.object.annotationsdisplay.DefaultAnnotationsDisplayGenerator;
import adams.gui.visualization.object.annotator.AbstractAnnotator;
import adams.gui.visualization.object.annotator.NullAnnotator;
import adams.gui.visualization.object.annotator.PolygonAnnotator;
import adams.gui.visualization.object.mouseclick.AbstractMouseClickProcessor;
import adams.gui.visualization.object.mouseclick.AddMetaData;
import adams.gui.visualization.object.mouseclick.MultiProcessor;
import adams.gui.visualization.object.mouseclick.NullProcessor;
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
import java.util.List;

/**
 * Panel for annotating objects in images.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ObjectAnnotationPanel
  extends BasePanel
  implements CleanUpHandler, UndoHandlerWithQuickAccess, UndoListener {

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

  /** the text field for the zoom. */
  protected NumberTextField m_TextZoom;

  /** the button for clearing zoom. */
  protected BaseFlatButton m_ButtonZoomClear;

  /** the button for zooming in. */
  protected BaseFlatButton m_ButtonZoomIn;

  /** the button for zooming out. */
  protected BaseFlatButton m_ButtonZoomOut;

  /** the button for applying the zoom. */
  protected BaseFlatButton m_ButtonZoom;

  /** the button for performing an undo. */
  protected BaseFlatButton m_ButtonUndo;

  /** the button for performing a redo. */
  protected BaseFlatButton m_ButtonRedo;

  /** the brightness to use. */
  protected NumberTextField m_TextBrightness;

  /** the button for applying the values. */
  protected BaseFlatButton m_ButtonBrightness;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

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

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Overlay             = new NullOverlay();
    m_MouseClickProcessor = new NullProcessor();
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
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panel, BorderLayout.NORTH);

    m_TextZoom = new NumberTextField(Type.DOUBLE, "100");
    m_TextZoom.setColumns(5);
    m_TextZoom.setToolTipText("100 = original image size");
    m_TextZoom.setCheckModel(new BoundedNumberCheckModel(Type.DOUBLE, 1.0, null));
    m_TextZoom.addAnyChangeListener((ChangeEvent e) -> m_ButtonZoom.setIcon(GUIHelper.getIcon("validate_blue.png")));
    label = new JLabel("Zoom");
    label.setDisplayedMnemonic('Z');
    label.setLabelFor(m_TextZoom);
    panel.add(label);
    panel.add(m_TextZoom);
    m_ButtonZoom = new BaseFlatButton(GUIHelper.getIcon("validate.png"));
    m_ButtonZoom.setToolTipText("Apply zoom");
    m_ButtonZoom.addActionListener((ActionEvent e) -> {
      setZoom(m_TextZoom.getValue().doubleValue() / 100.0);
      m_ButtonZoom.setIcon(GUIHelper.getIcon("validate.png"));
      update();
    });
    panel.add(m_ButtonZoom);
    m_ButtonZoomClear = new BaseFlatButton(GUIHelper.getIcon("zoom_clear.png"));
    m_ButtonZoomClear.setToolTipText("Clear zoom");
    m_ButtonZoomClear.addActionListener((ActionEvent e) -> clearZoom());
    panel.add(m_ButtonZoomClear);
    m_ButtonZoomIn = new BaseFlatButton(GUIHelper.getIcon("zoom_in.png"));
    m_ButtonZoomIn.setToolTipText("Zoom in");
    m_ButtonZoomIn.addActionListener((ActionEvent e) -> zoomIn());
    panel.add(m_ButtonZoomIn);
    m_ButtonZoomOut = new BaseFlatButton(GUIHelper.getIcon("zoom_out.png"));
    m_ButtonZoomOut.setToolTipText("Zoom out");
    m_ButtonZoomOut.addActionListener((ActionEvent e) -> zoomOut());
    panel.add(m_ButtonZoomOut);
    panel.add(new JLabel(" "));
    m_ButtonUndo = new BaseFlatButton(GUIHelper.getIcon("undo.gif"));
    m_ButtonUndo.setToolTipText("Undo changes");
    m_ButtonUndo.addActionListener((ActionEvent e) -> undo());
    panel.add(m_ButtonUndo);
    m_ButtonRedo = new BaseFlatButton(GUIHelper.getIcon("redo.gif"));
    m_ButtonRedo.setToolTipText("Redo changes");
    m_ButtonRedo.addActionListener((ActionEvent e) -> redo());
    panel.add(m_ButtonRedo);

    panel.add(new JLabel("Brightness"));
    m_TextBrightness = new NumberTextField(Type.DOUBLE, "100");
    m_TextBrightness.setColumns(5);
    m_TextBrightness.setToolTipText("100 = original brightness");
    m_TextBrightness.setCheckModel(new BoundedNumberCheckModel(Type.DOUBLE, 1.0, null));
    m_TextBrightness.addAnyChangeListener((ChangeEvent e) -> m_ButtonBrightness.setIcon(GUIHelper.getIcon("validate_blue.png")));
    panel.add(m_TextBrightness);
    m_ButtonBrightness = new BaseFlatButton(GUIHelper.getIcon("validate.png"));
    m_ButtonBrightness.setToolTipText("Apply current values");
    m_ButtonBrightness.addActionListener((ActionEvent e) -> {
      m_PanelCanvas.setBrightness(m_TextBrightness.getValue().floatValue());
      update();
    });
    panel.add(m_ButtonBrightness);

    // split pane
    m_SplitPane = new BaseSplitPane();
    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setResizeWeight(1.0);
    add(m_SplitPane, BorderLayout.CENTER);

    m_PanelCanvas = new CanvasPanel();
    m_PanelCanvas.setOwner(this);
    m_PanelCanvas.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	m_MouseClickProcessor.process(ObjectAnnotationPanel.this, e);
      }
    });
    m_ScrollPane = new BaseScrollPane(m_PanelCanvas);
    m_SplitPane.setLeftComponent(m_ScrollPane);

    m_PanelAnnotations = new DefaultAnnotationsDisplayGenerator().generate();
    m_PanelAnnotations.setOwner(this);
    m_SplitPane.setRightComponent(m_PanelAnnotations);

    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);
  }

  /**
   * Sets the zoom/scale.
   *
   * @param value	the scale to use
   */
  public void setZoom(double value) {
    m_TextZoom.setValue(value * 100);
    m_ButtonZoom.setIcon(GUIHelper.getIcon("validate.png"));
    m_PanelCanvas.setScale(value);
  }

  /**
   * Returns the current zoom/scale.
   *
   * @return		the scale in use
   */
  public double getZoom() {
    return m_PanelCanvas.getScale();
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
   * Sets the annotations panel.
   *
   * @param value	the panel to use
   */
  public void setAnnotationsPanel(AbstractAnnotationsDisplayPanel value) {
    m_PanelAnnotations.cleanUp();
    m_PanelAnnotations = value;
    m_SplitPane.setRightComponent(m_PanelAnnotations);
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
   * Sets the location of the divider.
   *
   * @param value	the position in pixels
   */
  public void setDividerLocation(int value) {
    m_SplitPane.setDividerLocation(value);
  }

  /**
   * Returns the divider location.
   *
   * @return		the position in pixels
   */
  public int getDividerLocation() {
    return m_SplitPane.getDividerLocation();
  }

  /**
   * Sets the proportional location.
   *
   * @param value	the location (0-1)
   */
  public void setDividerLocation(double value) {
    m_SplitPane.setDividerLocation(value);
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
    panel.setZoom(0.25);
    //panel.setAnnotator(new BoundingBoxAnnotator());
    panel.setAnnotator(new PolygonAnnotator());
    panel.setOverlay(new ObjectLocationsOverlayFromReport());
    MultiProcessor multiproc = new MultiProcessor();
    multiproc.addProcessor(new ViewObjects());
    AddMetaData add = new AddMetaData();
    add.setShiftDown(true);
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
    panel.setDividerLocation(0.75);
    panel.update();
  }
}
