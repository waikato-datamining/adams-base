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
 * SegmentationPanel.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation;

import adams.core.ClassLister;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.RoundingUtils;
import adams.data.image.BufferedImageHelper;
import adams.data.io.input.PNGImageReader;
import adams.env.Environment;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BaseFrame;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseToggleButton;
import adams.gui.core.ConsolePanel;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.BoundedNumberCheckModel;
import adams.gui.core.NumberTextField.Type;
import adams.gui.event.UndoEvent;
import adams.gui.event.UndoListener;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.segmentation.layer.LayerManager;
import adams.gui.visualization.segmentation.layer.OverlayLayer;
import adams.gui.visualization.segmentation.tool.AbstractTool;
import adams.gui.visualization.segmentation.tool.Pointer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.Date;

/**
 * Panel for performing segmentation annotations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SegmentationPanel
  extends BasePanel
  implements ChangeListener, UndoListener {

  private static final long serialVersionUID = -7354416525309860289L;

  /** the zoom factor to use. */
  public final static double ZOOM_FACTOR = 1.4;

  /** layer manager. */
  protected LayerManager m_Manager;

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

  /** the button for adding an undo. */
  protected BaseFlatButton m_ButtonAddUndo;

  /** the button for performing an undo. */
  protected BaseFlatButton m_ButtonUndo;

  /** the button for performing a redo. */
  protected BaseFlatButton m_ButtonRedo;

  /** the main split pane. */
  protected BaseSplitPane m_SplitPaneLeft;

  /** the left split pane. */
  protected BaseSplitPane m_SplitPaneRight;

  /** the left panel. */
  protected BasePanel m_PanelLeft;

  /** the layers panel. */
  protected BasePanel m_PanelLayers;

  /** the tools panel. */
  protected BasePanel m_PanelTools;

  /** the split pane for the tools. */
  protected BaseSplitPane m_SplitPaneTools;

  /** the panel for displaying the tool options. */
  protected BasePanel m_PanelToolOptions;

  /** the center panel. */
  protected BasePanel m_PanelCenter;

  /** the panel for drawing. */
  protected CanvasPanel m_PanelCanvas;

  /** the last mouse listener in use. */
  protected MouseListener m_LastMouseListener;

  /** the last mouse motion listener in use. */
  protected MouseMotionListener m_LastMouseMotionListener;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_LastMouseListener       = null;
    m_LastMouseMotionListener = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel		panel;
    Class[]		tools;
    JLabel		label;
    BaseToggleButton	button;
    ButtonGroup		group;

    super.initGUI();

    setLayout(new BorderLayout());

    // top
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panel, BorderLayout.NORTH);

    m_TextZoom = new NumberTextField(Type.DOUBLE, "100");
    m_TextZoom.setColumns(5);
    m_TextZoom.setToolTipText("100 = original image size");
    m_TextZoom.setCheckModel(new BoundedNumberCheckModel(Type.DOUBLE, 1.0, null));
    label = new JLabel("Zoom");
    label.setDisplayedMnemonic('Z');
    label.setLabelFor(m_TextZoom);
    panel.add(Fonts.usePlain(label));
    panel.add(m_TextZoom);
    m_ButtonZoom = new BaseFlatButton(GUIHelper.getIcon("validate.png"));
    m_ButtonZoom.setToolTipText("Apply zoom");
    m_ButtonZoom.addActionListener((ActionEvent e) -> {
      m_Manager.setZoom(m_TextZoom.getValue().doubleValue() / 100.0);
      m_Manager.update();
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
    m_ButtonAddUndo = new BaseFlatButton(GUIHelper.getIcon("undo_add.gif"));
    m_ButtonAddUndo.setToolTipText("Add undo point");
    m_ButtonAddUndo.addActionListener((ActionEvent e) -> addUndo());
    panel.add(m_ButtonAddUndo);
    m_ButtonUndo = new BaseFlatButton(GUIHelper.getIcon("undo.gif"));
    m_ButtonUndo.setToolTipText("Undo changes");
    m_ButtonUndo.addActionListener((ActionEvent e) -> undo());
    panel.add(m_ButtonUndo);
    m_ButtonRedo = new BaseFlatButton(GUIHelper.getIcon("redo.gif"));
    m_ButtonRedo.setToolTipText("Redo changes");
    m_ButtonRedo.addActionListener((ActionEvent e) -> redo());
    panel.add(m_ButtonRedo);

    // left
    m_SplitPaneLeft = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPaneLeft.setResizeWeight(0.0);
    add(m_SplitPaneLeft, BorderLayout.CENTER);

    m_PanelLeft = new BasePanel(new BorderLayout());
    m_SplitPaneLeft.setLeftComponent(m_PanelLeft);

    m_PanelLayers = new BasePanel();
    m_PanelLayers.setBorder(BorderFactory.createTitledBorder("Layers"));
    m_PanelLeft.add(m_PanelLayers, BorderLayout.CENTER);

    // right
    m_SplitPaneRight = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPaneRight.setResizeWeight(1.0);
    m_SplitPaneLeft.setRightComponent(m_SplitPaneRight);

    m_PanelTools = new BasePanel(new BorderLayout());
    m_PanelTools.setBorder(BorderFactory.createTitledBorder("Tools"));
    m_SplitPaneRight.setRightComponent(m_PanelTools);
    m_SplitPaneTools = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_PanelTools.add(m_SplitPaneTools, BorderLayout.CENTER);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_SplitPaneTools.setTopComponent(panel);
    m_PanelToolOptions = new BasePanel(new BorderLayout());
    m_SplitPaneTools.setBottomComponent(m_PanelToolOptions);
    tools = ClassLister.getSingleton().getClasses(AbstractTool.class);
    group = new ButtonGroup();
    for (Class t: tools) {
      try {
        final AbstractTool tool = (AbstractTool) t.newInstance();
        tool.setCanvas(m_PanelCanvas);
        button = new BaseToggleButton(tool.getIcon());
        button.setToolTipText(tool.getName());
        button.addActionListener((ActionEvent e) -> {
          if (m_LastMouseListener != null)
            m_PanelCanvas.removeMouseListener(m_LastMouseListener);
          if (m_LastMouseMotionListener != null)
            m_PanelCanvas.removeMouseMotionListener(m_LastMouseMotionListener);
          m_PanelToolOptions.removeAll();
          tool.setCanvas(m_PanelCanvas);
          m_PanelToolOptions.add(tool.getOptionPanel(), BorderLayout.CENTER);
          m_PanelCanvas.setCursor(tool.getCursor());
          m_LastMouseListener = tool.getMouseListener();
          if (m_LastMouseListener != null)
	    m_PanelCanvas.addMouseListener(m_LastMouseListener);
          m_LastMouseMotionListener = tool.getMouseMotionListener();
          if (m_LastMouseMotionListener != null)
	    m_PanelCanvas.addMouseMotionListener(m_LastMouseMotionListener);
          m_SplitPaneTools.setDividerLocation(m_SplitPaneTools.getDividerLocation());
	});
        group.add(button);
        if (t.equals(Pointer.class))
          panel.add(button, 0);
        else
	  panel.add(button);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append("Failed to instantiate tool class: " + t.getName(), e);
      }
    }

    // center
    m_PanelCenter = new BasePanel(new BorderLayout());
    m_SplitPaneRight.setLeftComponent(m_PanelCenter);
    m_PanelCanvas = new CanvasPanel();
    m_PanelCanvas.setOwner(this);
    m_PanelCenter.add(new BaseScrollPane(m_PanelCanvas));
    m_Manager = new LayerManager(m_PanelCanvas);
    m_Manager.addChangeListener(this);
    m_Manager.getUndo().addUndoListener(this);

    m_SplitPaneLeft.setDividerLocation(250);
    m_SplitPaneRight.setDividerLocation(680);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateButtons();
  }

  /**
   * Returns the layer manager.
   *
   * @return		the manager
   */
  public LayerManager getManager() {
    return m_Manager;
  }

  /**
   * Gets called when the layers have changed somehow.
   *
   * @param e		the event
   */
  @Override
  public void stateChanged(ChangeEvent e) {
    int		location;
    JPanel 	outer;
    JPanel 	nested;

    m_PanelLayers.removeAll();
    outer = m_PanelLayers;
    for (JComponent layer: m_Manager.getLayers()) {
      outer.add(layer, BorderLayout.NORTH);
      nested = new JPanel(new BorderLayout());
      outer.add(nested, BorderLayout.CENTER);
      outer = nested;
    }
    location = m_SplitPaneLeft.getDividerLocation();
    m_SplitPaneLeft.setLeftComponent(m_PanelLeft);
    m_SplitPaneLeft.setDividerLocation(location);
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonZoom.setEnabled(getManager().getImageLayer().getImage() != null);
    m_ButtonAddUndo.setEnabled(getManager().isUndoSupported());
    m_ButtonUndo.setEnabled(getManager().getUndo().canUndo());
    m_ButtonRedo.setEnabled(getManager().getUndo().canRedo());
  }

  /**
   * Adds an undo point.
   */
  public void addUndo() {
    getManager().addUndoPoint(new Date().toString());
    updateButtons();
  }

  /**
   * Performs an undo.
   */
  public void undo() {
    getManager().undo();
  }

  /**
   * Performs a redo.
   */
  public void redo() {
    getManager().redo();
  }

  /**
   * An undo event, like add or remove, has occurred.
   *
   * @param e		the trigger event
   */
  public void undoOccurred(UndoEvent e) {
    updateButtons();
  }

  /**
   * Updates buttons and manager.
   */
  public void update() {
    updateButtons();
    getManager().update();
  }

  /**
   * Sets the zoom to use.
   *
   * @param value	the zoom (100 = original size)
   */
  public void setZoom(double value) {
    m_TextZoom.setValue(value);
    m_Manager.setZoom(m_TextZoom.getValue().doubleValue() / 100.0);
    update();
  }

  /**
   * Returns the current zoom in use.
   *
   * @return		the zoom (100 = original size)
   */
  public double getZoom() {
    return m_TextZoom.getValue().doubleValue();
  }

  /**
   * Clears the zoom.
   */
  public void clearZoom() {
    m_TextZoom.setValue(100);
    m_Manager.setZoom(1.0);
    m_Manager.update();
  }

  /**
   * Zooms in.
   */
  public void zoomIn() {
    m_TextZoom.setValue(RoundingUtils.round(m_TextZoom.getValue().doubleValue() * ZOOM_FACTOR, 1));
    m_Manager.setZoom(m_TextZoom.getValue().doubleValue() / 100.0);
    m_Manager.update();
  }

  /**
   * Zooms out.
   */
  public void zoomOut() {
    m_TextZoom.setValue(RoundingUtils.round(m_TextZoom.getValue().doubleValue() / ZOOM_FACTOR, 1));
    m_Manager.setZoom(m_TextZoom.getValue().doubleValue() / 100.0);
    m_Manager.update();
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    SegmentationPanel panel = new SegmentationPanel();
    panel.getManager().clear();
    File img = new File(args[0]);
    panel.getManager().setImage(img.getName(), BufferedImageHelper.read(img).getImage());
    DefaultColorProvider provider = new DefaultColorProvider();
    for (int i = 1; i < args.length; i++) {
      File ovl = new File(args[i]);
      String label = FileUtils.replaceExtension(ovl.getName(), "").replaceAll(".*-", "");
      OverlayLayer layer = panel.getManager().addOverlay(label, provider.next(), 0.5f, new PNGImageReader().read(new PlaceholderFile(ovl)).getImage());
      layer.setRemovable(true);
      layer.setActionsAvailable(true);
    }
    panel.update();
    BaseFrame frame = new BaseFrame("Segmentation");
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.setSize(GUIHelper.makeWider(GUIHelper.getDefaultLargeDialogDimension()));
    frame.setLocationRelativeTo(null);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    frame.setVisible(true);
  }
}
