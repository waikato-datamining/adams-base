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
 * ContentPanel.java
 * Copyright (C) 2008-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.plot;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractCommandLineHandler;
import adams.core.option.OptionHandler;
import adams.gui.core.BaseMenu;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.event.PlotPanelPanningEvent;
import adams.gui.event.PlotPanelPanningEvent.PanningEventType;
import adams.gui.event.PlotPanelPanningListener;
import adams.gui.event.PlotPanelZoomEvent;
import adams.gui.event.PlotPanelZoomEvent.ZoomEventType;
import adams.gui.event.PlotPanelZoomListener;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.print.JComponentWriter;
import adams.gui.print.JComponentWriterFileChooser;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.MouseMovementTracker;
import adams.gui.visualization.core.Paintlet;
import adams.gui.visualization.core.PaintletManager;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.PopupMenuCustomizer;
import adams.gui.visualization.core.axis.Tick;
import adams.gui.visualization.core.axis.Visibility;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * A specialized panel that can notify listeners of paint updates.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ContentPanel
  extends BasePanel
  implements ChangeListener {

  /** the owner of the panel. */
  protected PlotPanel m_Owner;

  /** the panel itself. */
  protected ContentPanel m_Self;

  /** for serialization. */
  private static final long serialVersionUID = 1785560399953428177L;

  /** for outputting the values. */
  protected DecimalFormat m_Format;

  /** whether zooming is enabled. */
  protected boolean m_ZoomingEnabled;

  /** the color of the zoom box. */
  protected Color m_ZoomBoxColor;

  /** whether the zoom box is currently been drawn. */
  protected boolean m_Zooming;

  /** whether dragging has happened at all. */
  protected boolean m_Dragged;

  /** the top left corner of the zoom box. */
  protected Point m_ZoomTopLeft;

  /** the bottom right corner of the zoom box. */
  protected Point m_ZoomBottomRight;

  /** an optional customizer for the right-click popup. */
  protected PopupMenuCustomizer m_PopupMenuCustomizer;

  /** whether panning is enabled. */
  protected boolean m_PanningEnabled;

  /** whether the graph is currently moved around. */
  protected boolean m_Panning;

  /** the starting mouse position of panning. */
  protected Point m_PanningStart;

  /** the original pixel offset for the left axis. */
  protected int m_LeftPixelOffset;

  /** the original pixel offset for the right axis. */
  protected int m_RightPixelOffset;

  /** the original pixel offset for the top axis. */
  protected int m_TopPixelOffset;

  /** the original pixel offset for the bottom axis. */
  protected int m_BottomPixelOffset;

  /** for post-processing the tiptext. */
  protected TipTextCustomizer m_TipTextCustomizer;

  /** the hit detectors. */
  protected HashSet<AbstractHitDetector> m_HitDetectors;

  /** the zoom listeners. */
  protected HashSet<PlotPanelZoomListener> m_ZoomListeners;

  /** the panning listeners. */
  protected HashSet<PlotPanelPanningListener> m_PanningListeners;

  /** the mouse movement trackers. */
  protected HashSet<MouseMovementTracker> m_MouseMovementTrackers;

  /** the filechooser for saving the panel. */
  protected JComponentWriterFileChooser m_FileChooser;

  /**
   * Initializes the panel.
   *
   * @param owner	the plot panel this panel belongs to
   */
  public ContentPanel(PlotPanel owner) {
    super();

    m_Owner = owner;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Self                  = this;
    m_Format                = new DecimalFormat("0.0E0;-0.0E0");
    m_Zooming               = false;
    m_Dragged               = false;
    m_ZoomBoxColor          = Color.GRAY;
    m_PopupMenuCustomizer   = null;
    m_Panning               = false;
    m_TipTextCustomizer     = null;
    m_HitDetectors          = new HashSet<>();
    m_ZoomListeners         = new HashSet<>();
    m_PanningListeners      = new HashSet<>();
    m_MouseMovementTrackers = new HashSet<>();
    m_ZoomingEnabled        = true;
    m_PanningEnabled        = true;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    setToolTipText("");  // in order to enable the tooltip

    addMouseListener(new MouseAdapter() {
      // start zoom
      @Override
      public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        if (e.getButton() == MouseEvent.BUTTON1) {
          // get top/left coordinates for zoom
          if (!e.isShiftDown()) {
            if (m_ZoomingEnabled) {
              m_Zooming     = true;
              m_Dragged     = false;
              m_ZoomTopLeft = e.getPoint();
            }
          }
          // get start position of panning
          else {
            if (m_PanningEnabled) {
              m_Panning           = true;
              m_PanningStart      = e.getPoint();
              m_LeftPixelOffset   = getOwner().getAxis(Axis.LEFT).getPixelOffset();
              m_RightPixelOffset  = getOwner().getAxis(Axis.RIGHT).getPixelOffset();
              m_TopPixelOffset    = getOwner().getAxis(Axis.TOP).getPixelOffset();
              m_BottomPixelOffset = getOwner().getAxis(Axis.TOP).getPixelOffset();
            }
          }
        }
      }

      // perform zoom/panning
      @Override
      public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        if (e.getButton() == MouseEvent.BUTTON1) {
          // get bottom/right coordinates for zoom
          if (m_Zooming && m_Dragged) {
            m_Zooming         = false;
            m_Dragged         = false;
            m_ZoomBottomRight = e.getPoint();

            addZoom(
        	(int) m_ZoomTopLeft.getY(),
        	(int) m_ZoomTopLeft.getX(),
        	(int) m_ZoomBottomRight.getY(),
        	(int) m_ZoomBottomRight.getX());
          }
          else if (m_Panning) {
            m_Panning  = false;
            int deltaX = e.getX() - (int) m_PanningStart.getX();
            int deltaY = (int) m_PanningStart.getY() - e.getY();

            // update pixel offset
            getOwner().getAxis(Axis.LEFT).setPixelOffset(m_LeftPixelOffset + deltaY);
            getOwner().getAxis(Axis.RIGHT).setPixelOffset(m_RightPixelOffset + deltaY);
            getOwner().getAxis(Axis.TOP).setPixelOffset(m_TopPixelOffset + deltaX);
            getOwner().getAxis(Axis.BOTTOM).setPixelOffset(m_BottomPixelOffset + deltaX);

            repaint();
            
            notifyPanningListeners(PanningEventType.PANNING);
          }
        }
        
        m_Zooming = false;
        m_Panning = false;
      }

      // popup menu/hits
      @Override
      public void mouseClicked(MouseEvent e) {
        if (!MouseUtils.isPrintScreenClick(e)) {
          if (MouseUtils.isRightClick(e)) {
            BasePopupMenu menu = getPopupMenu(e);
            if (menu != null)
              menu.showAbsolute(m_Self, e);
          }
          else if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 1)) {
            detectHits(e);
          }
        }
      }
    });
    addMouseMotionListener(new MouseMotionAdapter() {
      // for zooming
      @Override
      public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);

        // update zoom box
        if (m_Zooming && !e.isShiftDown()) {
          m_Dragged         = true;
          m_ZoomBottomRight = e.getPoint();

          repaint();
        }
        else if (m_Panning && e.isShiftDown()) {
          int deltaX = e.getX() - (int) m_PanningStart.getX();
          int deltaY = (int) m_PanningStart.getY() - e.getY();

          // update pixel offset
          getOwner().getAxis(Axis.LEFT).setPixelOffset(m_LeftPixelOffset + deltaY);
          getOwner().getAxis(Axis.RIGHT).setPixelOffset(m_RightPixelOffset + deltaY);
          getOwner().getAxis(Axis.TOP).setPixelOffset(m_TopPixelOffset + deltaX);
          getOwner().getAxis(Axis.BOTTOM).setPixelOffset(m_BottomPixelOffset + deltaX);

          repaint();
        }
      }
      @Override
      public void mouseMoved(MouseEvent e) {
	if (m_MouseMovementTrackers.size() > 0)
	  notifyMouseMovementTrackers(e);
        super.mouseMoved(e);
      }
    });
    addMouseWheelListener((MouseWheelEvent e) -> {
      if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
	boolean zoomIn = (e.getWheelRotation() == -1);
	int amount = e.getScrollAmount();
	double factor;
	if (zoomIn)
	  factor = Math.pow(1/1.1, amount);
	else
	  factor = Math.pow(1.1, amount);

	double width = (getOwner().getAxis(Axis.BOTTOM).getActualMaximum() - getOwner().getAxis(Axis.BOTTOM).getActualMinimum());
	double height = (getOwner().getAxis(Axis.LEFT).getActualMaximum() - getOwner().getAxis(Axis.LEFT).getActualMinimum());
	double widthNew = width * factor;
	double heightNew = height * factor;

	addZoom(
	  getOwner().getAxis(Axis.LEFT).getActualMaximum() - (height - heightNew) / 2,
	  getOwner().getAxis(Axis.BOTTOM).getActualMinimum() + (width - widthNew) / 2,
	  getOwner().getAxis(Axis.LEFT).getActualMinimum() + (height - heightNew) / 2,
	  getOwner().getAxis(Axis.BOTTOM).getActualMaximum() - (width - widthNew) / 2);
      }
    });

    // making it focusable
    setFocusable(true);
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        requestFocusInWindow();
        super.mouseClicked(e);
      }
    });
  }

  /**
   * Returns the plot panel this panel belongs to.
   *
   * @return		the owning panel
   */
  public PlotPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the filechooser for saving the plot as image.
   */
  protected synchronized JComponentWriterFileChooser getFileChooser() {
    if (m_FileChooser == null)
      m_FileChooser = new JComponentWriterFileChooser();
    
    return m_FileChooser;
  }

  /**
   * Sets whether zooming is enabled or not.
   *
   * @param value	if true then zooming is enabled
   */
  public void setZoomingEnabled(boolean value) {
    m_ZoomingEnabled = value;
    if (!m_ZoomingEnabled)
      clearZoom();
  }

  /**
   * Returns whether zooming is enabled.
   *
   * @return		true if zooming is enabled
   */
  public boolean isZoomingEnabled() {
    return m_ZoomingEnabled;
  }

  /**
   * Pops the current zoom.
   */
  public void popZoom() {
    if (getOwner().isZoomed()) {
      getOwner().getAxis(Axis.TOP).popZoom();
      getOwner().getAxis(Axis.LEFT).popZoom();
      getOwner().getAxis(Axis.BOTTOM).popZoom();
      getOwner().getAxis(Axis.RIGHT).popZoom();
      notifyZoomListeners(ZoomEventType.POP);
    }
  }

  /**
   * Clears the zoom.
   */
  public void clearZoom() {
    getOwner().getAxis(Axis.TOP).clearZoom();
    getOwner().getAxis(Axis.LEFT).clearZoom();
    getOwner().getAxis(Axis.BOTTOM).clearZoom();
    getOwner().getAxis(Axis.RIGHT).clearZoom();
    notifyZoomListeners(ZoomEventType.CLEAR);
  }

  /**
   * Sets whether panning is enabled or not.
   *
   * @param value	if true then panning is enabled
   */
  public void setPanningEnabled(boolean value) {
    m_PanningEnabled = value;
    // clear panning
    if (!m_PanningEnabled)
      clearPanning();
  }

  /**
   * Returns whether panning is enabled.
   *
   * @return		true if panning is enabled
   */
  public boolean isPanningEnabled() {
    return m_PanningEnabled;
  }

  /**
   * Clears the panning.
   */
  public void clearPanning() {
    getOwner().getAxis(Axis.TOP).clearPanning();
    getOwner().getAxis(Axis.LEFT).clearPanning();
    getOwner().getAxis(Axis.BOTTOM).clearPanning();
    getOwner().getAxis(Axis.RIGHT).clearPanning();
    notifyPanningListeners(PanningEventType.RESET);
  }

  /**
   * swaps points if necessary, s.t., first is the smaller one.
   *
   * @param value1	the first value
   * @param value2	the second value
   * @return		the ordered values
   */
  protected int[] order(int value1, int value2) {
    int[]	result;

    if (value1 < value2)
      result = new int[]{value1, value2};
    else
      result = new int[]{value2, value1};

    return result;
  }

  /**
   * swaps points if necessary, s.t., first is the smaller one.
   *
   * @param value1	the first value
   * @param value2	the second value
   * @return		the ordered values
   */
  protected double[] order(double value1, double value2) {
    double[]	result;

    if (value1 < value2)
      result = new double[]{value1, value2};
    else
      result = new double[]{value2, value1};

    return result;
  }

  /**
   * Adds a zoom.
   *
   * @param top	the top value
   * @param left	the left value
   * @param bottom	the bottom value
   * @param right	the right value
   */
  public void addZoom(double top, double left, double bottom, double right) {
    double[] 	y;
    double[] 	x;

    y = order(top, bottom);
    x = order(left, right);

    // update axes
    if (   getOwner().getAxis(Axis.LEFT).canZoom(y[0], y[1])
	&& getOwner().getAxis(Axis.RIGHT).canZoom(y[0], y[1])
	&& getOwner().getAxis(Axis.TOP).canZoom(x[0], x[1])
	&& getOwner().getAxis(Axis.BOTTOM).canZoom(x[0], x[1]) ) {
      getOwner().getAxis(Axis.LEFT).pushZoom(y[0], y[1]);
      getOwner().getAxis(Axis.RIGHT).pushZoom(y[0], y[1]);
      getOwner().getAxis(Axis.TOP).pushZoom(x[0], x[1]);
      getOwner().getAxis(Axis.BOTTOM).pushZoom(x[0], x[1]);
      notifyZoomListeners(ZoomEventType.PUSH);
    }
    else {
      System.err.println("No further zoom possible!");
    }

    repaint();
  }

  /**
   * Adds a zoom.
   *
   * @param top	the top position
   * @param left	the left position
   * @param bottom	the bottom position
   * @param right	the right position
   */
  public void addZoom(int top, int left, int bottom, int right) {
    int[] 	y;
    int[] 	x;

    y = order(top, bottom);
    x = order(left, right);

    // update axes
    if (   getOwner().getAxis(Axis.LEFT).canZoom(
	    getOwner().getAxis(Axis.LEFT).posToValue(y[1]),
	    getOwner().getAxis(Axis.LEFT).posToValue(y[0]))
	&& getOwner().getAxis(Axis.RIGHT).canZoom(
	    getOwner().getAxis(Axis.RIGHT).posToValue(y[1]),
	    getOwner().getAxis(Axis.RIGHT).posToValue(y[0]))
	&& getOwner().getAxis(Axis.TOP).canZoom(
	    getOwner().getAxis(Axis.TOP).posToValue(x[0]),
	    getOwner().getAxis(Axis.TOP).posToValue(x[1]))
	&& getOwner().getAxis(Axis.BOTTOM).canZoom(
	    getOwner().getAxis(Axis.BOTTOM).posToValue(x[0]),
	    getOwner().getAxis(Axis.BOTTOM).posToValue(x[1])) ) {
      getOwner().getAxis(Axis.LEFT).pushZoom(
	  getOwner().getAxis(Axis.LEFT).posToValue(y[1]),
	  getOwner().getAxis(Axis.LEFT).posToValue(y[0]));
      getOwner().getAxis(Axis.RIGHT).pushZoom(
	  getOwner().getAxis(Axis.RIGHT).posToValue(y[1]),
	  getOwner().getAxis(Axis.RIGHT).posToValue(y[0]));
      getOwner().getAxis(Axis.TOP).pushZoom(
	  getOwner().getAxis(Axis.TOP).posToValue(x[0]),
	  getOwner().getAxis(Axis.TOP).posToValue(x[1]));
      getOwner().getAxis(Axis.BOTTOM).pushZoom(
	  getOwner().getAxis(Axis.BOTTOM).posToValue(x[0]),
	  getOwner().getAxis(Axis.BOTTOM).posToValue(x[1]));
      notifyZoomListeners(ZoomEventType.PUSH);
    }
    else {
      System.err.println("No further zoom possible!");
    }

    repaint();
  }

  /**
   * Sets the color for the zoom box.
   *
   * @param value	the color to use
   */
  public void setZoomBoxColor(Color value) {
    m_ZoomBoxColor = value;
    if (m_Zooming)
      repaint();
  }

  /**
   * Returns the color for the zoom box currently in use.
   *
   * @return		the color in use
   */
  public Color getZoomBoxColor() {
    return m_ZoomBoxColor;
  }

  /**
   * Sets the class to customize the right-click popup menu.
   *
   * @param value	the customizer
   */
  public void setPopupMenuCustomizer(PopupMenuCustomizer value) {
    m_PopupMenuCustomizer = value;
  }

  /**
   * Returns the current customizer, can be null.
   *
   * @return		the customizer
   */
  public PopupMenuCustomizer getPopupMenuCustomizer() {
    return m_PopupMenuCustomizer;
  }

  /**
   * Returns the popup menu, potentially customized.
   *
   * @param e		the mouse event
   * @return		the popup menu
   * @see		#m_PopupMenuCustomizer
   */
  public BasePopupMenu getPopupMenu(MouseEvent e) {
    BasePopupMenu	result;
    JMenuItem		item;
    BaseMenu		submenu;
    Iterator<Paintlet>	paintlets;

    result = null;

    if (m_ZoomingEnabled) {
      result = new BasePopupMenu();

      item = new JMenuItem("Zoom out", GUIHelper.getIcon("zoom_out.png"));
      item.setEnabled(getOwner().isZoomed());
      item.addActionListener((ActionEvent ae) -> {
	popZoom();
	repaint();
      });
      result.add(item);

      item = new JMenuItem("Clear zoom", GUIHelper.getIcon("zoom_clear.png"));
      item.setEnabled(getOwner().isZoomed());
      item.addActionListener((ActionEvent ae) -> {
	clearZoom();
	repaint();
      });
      result.add(item);
    }

    if (m_PanningEnabled) {
      if (result == null)
	result = new BasePopupMenu();

      item = new JMenuItem("Undo panning", GUIHelper.getEmptyIcon());
      item.setEnabled(getOwner().isPanned());
      item.addActionListener((ActionEvent ae) -> {
	clearPanning();
	repaint();
      });
      result.add(item);
    }

    if (result == null)
      result = new BasePopupMenu();

    item = new JMenuItem("Copy plot", GUIHelper.getIcon("copy.gif"));
    item.addActionListener((ActionEvent ae) -> copyPlot());
    result.add(item);

    item = new JMenuItem("Save plot...", GUIHelper.getIcon("save.gif"));
    item.addActionListener((ActionEvent ae) -> savePlot());
    result.add(item);

    // customize it?
    if (m_PopupMenuCustomizer != null) {
      if (m_PopupMenuCustomizer instanceof PaintletManager) {
	submenu   = new BaseMenu("Paintlets");
	paintlets = ((PaintletManager) m_PopupMenuCustomizer).paintlets();
	while (paintlets.hasNext()) {
	  final Paintlet paintlet = paintlets.next();
	  if (paintlet instanceof OptionHandler) {
	    item = new JMenuItem(paintlet.getClass().getSimpleName());
	    item.addActionListener((ActionEvent ae) -> editPaintlet(paintlet));
	    submenu.add(item);
	  }
	}
	submenu.sort();
	result.add(submenu);
      }
      m_PopupMenuCustomizer.customizePopupMenu(e, result);
    }

    return result;
  }

  /**
   * Clears the background.
   *
   * @param g		the graphics context
   */
  protected void clearBackground(Graphics g) {
    g.setColor(getOwner().getBackgroundColor());
    g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
  }

  /**
   * Paints the coordinates grid according to the ticks of the axes.
   *
   * @param g		the graphics context
   */
  protected void paintCoordinatesGrid(Graphics g) {
    AxisPanel		panel;
    List<Tick>		ticks;
    Tick		tick;
    int			i;
    boolean		vertical;
    int			pos;

    g.setColor(getOwner().getGridColor());

    for (Axis axis: Axis.values()) {
      if (!getOwner().getAxis(axis).getShowGridLines())
        continue;
      if (getOwner().getAxisVisibility(axis) == Visibility.INVISIBLE)
        continue;

      panel    = getOwner().getAxis(axis);
      ticks    = panel.getAxisModel().getTicks();
      vertical = ((axis == Axis.LEFT) || (axis == Axis.RIGHT));

      for (i = 0; i < ticks.size(); i++) {
        tick = ticks.get(i);

        pos = panel.correctPosition(tick.getPosition());
        if (vertical)
          g.drawLine(0, pos, getWidth(), pos);
        else
          g.drawLine(pos, 0, pos, getHeight());
      }
    }
  }

  /**
   * Paints the zoom box, if necessary (i.e., currently zooming/dragging).
   *
   * @param g		the graphics context
   */
  protected void paintZoomBox(Graphics g) {
    int	topX;
    int	bottomX;
    int	topY;
    int	bottomY;
    int	tmp;

    if (m_Zooming && m_Dragged) {
      g.setColor(m_ZoomBoxColor);

      topX    = (int) m_ZoomTopLeft.getX();
      topY    = (int) m_ZoomTopLeft.getY();
      bottomX = (int) m_ZoomBottomRight.getX();
      bottomY = (int) m_ZoomBottomRight.getY();

      // swap necessary?
      if (topX > bottomX) {
        tmp     = topX;
        topX    = bottomX;
        bottomX = tmp;
      }
      if (topY > bottomY) {
        tmp     = topY;
        topY    = bottomY;
        bottomY = tmp;
      }

      g.drawRect(
          topX,
          topY,
          (bottomX - topX + 1),
          (bottomY - topY + 1));
    }
  }

  /**
   * Performs the actual painting.
   * 
   * @param g		the graphics context
   */
  protected void doPaint(Graphics g) {
    // background
    clearBackground(g);
    getOwner().notifyPaintListeners(g, PaintMoment.BACKGROUND);

    // grid
    paintCoordinatesGrid(g);
    getOwner().notifyPaintListeners(g, PaintMoment.GRID);

    // other paint moments
    getOwner().notifyPaintListeners(g, PaintMoment.PRE_PAINT);
    getOwner().notifyPaintListeners(g, PaintMoment.PAINT);
    getOwner().notifyPaintListeners(g, PaintMoment.POST_PAINT);

    // paint zoom box
    paintZoomBox(g);
  }
  
  /**
   * paints the panel and notifies all listeners.
   *
   * @param g		the graphics context
   */
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    doPaint(g);
  }

  /**
   * prints the panel and notifies all listeners.
   *
   * @param g		the graphics context
   */
  @Override
  public void printComponent(Graphics g) {
    super.printComponent(g);
    doPaint(g);
  }

  /**
   * Allows the user to edit the paintlet. Only updates the options.
   *
   * @param paintlet	the paintlet to edit
   */
  protected void editPaintlet(Paintlet paintlet) {
    GenericObjectEditorDialog	dialog;
    AbstractCommandLineHandler	handler;
    Paintlet			updated;

    if (getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(getParentFrame(), true);
    dialog.setTitle("Edit paintlet");
    dialog.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
    dialog.getGOEEditor().setClassType(Paintlet.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(false);
    dialog.setCurrent(paintlet);
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    updated = (Paintlet) dialog.getCurrent();
    handler = AbstractCommandLineHandler.getHandler(paintlet);
    handler.setOptions(paintlet, handler.getOptions(updated));
  }

  /**
   * Sets the class for customizing the tip text.
   *
   * @param value	the customizer
   */
  public void setTipTextCustomizer(TipTextCustomizer value) {
    m_TipTextCustomizer = value;
  }

  /**
   * Returns the current tip text customizer, can be null.
   *
   * @return		the customizer
   */
  public TipTextCustomizer getTipTextCustomizer() {
    return m_TipTextCustomizer;
  }

  /**
   * Returns the values as tooltip.
   *
   * @param event	the event that triggered this method
   * @return		the tool tip
   */
  @Override
  public String getToolTipText(MouseEvent event) {
    String	result;
    String	str;
    AxisPanel	panel;

    result = null;

    for (Axis axis: Axis.values()) {
      if (!getOwner().hasToolTipAxis(axis))
        continue;

      panel = getOwner().getAxis(axis);
      m_Format.applyPattern(panel.getNumberFormat());

      if ((axis == Axis.LEFT) || (axis == Axis.RIGHT))
        str = panel.valueToDisplay(panel.posToValue(event.getY()));
      else
        str = panel.valueToDisplay(panel.posToValue(event.getX()));
      
      if (str != null) {
	if (result == null)
	  result = "";
	else
	  result += ", ";
	result += axis.getDisplayShort() + ": " + str;
      }
    }

    // post-process tiptext?
    if (m_TipTextCustomizer != null)
      result = m_TipTextCustomizer.processTipText(getOwner(), event.getPoint(), result);

    return result;
  }

  /**
   * Removes all hit detectors.
   */
  public void clearHitDetectors() {
    m_HitDetectors.clear();
  }

  /**
   * Adds the detector to the internal list of detectors.
   *
   * @param detector		the detector to add
   */
  public void addHitDetector(AbstractHitDetector detector) {
    m_HitDetectors.add(detector);
  }

  /**
   * Removes the detector from the internal list of detectors.
   *
   * @param detector		the detector to remover
   */
  public void removeHitDetector(AbstractHitDetector detector) {
    m_HitDetectors.remove(detector);
  }

  /**
   * Runs the mouseevent through all registered hit detectors.
   *
   * @param e		the mouse event for the detectors to analyze
   */
  protected void detectHits(MouseEvent e) {
    Iterator<AbstractHitDetector>	iter;
    AbstractHitDetector		detector;

    iter = m_HitDetectors.iterator();
    while (iter.hasNext()) {
      detector = iter.next();
      if (detector.isEnabled())
        detector.detect(e);
    }
  }

  /**
   * Adds the given listener to the internal list of zoom listeners.
   *
   * @param l		the listener to add
   */
  public void addZoomListener(PlotPanelZoomListener l) {
    m_ZoomListeners.add(l);
  }

  /**
   * Removes the given listener from the internal list of zoom listeners.
   *
   * @param l		the listener to remove
   */
  public void removeZoomListener(PlotPanelZoomListener l) {
    m_ZoomListeners.remove(l);
  }

  /**
   * Notifies all zoom listeners.
   *
   * @param type	the event type
   */
  public void notifyZoomListeners(ZoomEventType type) {
    Iterator<PlotPanelZoomListener>	iter;
    PlotPanelZoomEvent			e;

    e    = new PlotPanelZoomEvent(getOwner(), type);
    iter = m_ZoomListeners.iterator();
    while (iter.hasNext())
      iter.next().painted(e);
  }

  /**
   * Adds the given listener to the internal list of panning listeners.
   *
   * @param l		the listener to add
   */
  public void addPanningListener(PlotPanelPanningListener l) {
    m_PanningListeners.add(l);
  }

  /**
   * Removes the given listener from the internal list of panning listeners.
   *
   * @param l		the listener to remove
   */
  public void removePanningListener(PlotPanelPanningListener l) {
    m_PanningListeners.remove(l);
  }

  /**
   * Notifies all panning listeners.
   *
   * @param type	the event type
   */
  public void notifyPanningListeners(PanningEventType type) {
    Iterator<PlotPanelPanningListener>	iter;
    PlotPanelPanningEvent		e;

    e    = new PlotPanelPanningEvent(getOwner(), type);
    iter = m_PanningListeners.iterator();
    while (iter.hasNext())
      iter.next().panningOccurred(e);
  }

  /**
   * Adds the given listener to the internal list of mouse movement 
   * tracking listeners.
   *
   * @param l		the listener to add
   */
  public void addMouseMovementTracker(MouseMovementTracker l) {
    m_MouseMovementTrackers.add(l);
  }

  /**
   * Removes the given listener from the internal list of mouse movement 
   * tracking listeners.
   *
   * @param l		the listener to remove
   */
  public void removeMouseMovementTracker(MouseMovementTracker l) {
    m_MouseMovementTrackers.remove(l);
  }

  /**
   * Notifies all mouse movement tracking listeners.
   *
   * @param e		the mouse event
   */
  public void notifyMouseMovementTrackers(MouseEvent e) {
    Iterator<MouseMovementTracker>	iter;

    iter = m_MouseMovementTrackers.iterator();
    while (iter.hasNext())
      iter.next().mouseMovementTracked(e);
  }

  /**
   * In case an axis changes its type, e.g., log instead of percentage.
   *
   * @param e		the event
   */
  public void stateChanged(ChangeEvent e) {
    repaint();
  }

  /**
   * Copies the plot to the clipboard.
   */
  public void copyPlot() {
    ClipboardHelper.copyToClipboard(m_Owner);
  }

  /**
   * Displays a save dialog for saving the plot to a file.
   */
  public void savePlot() {
    int			result;
    JComponentWriter	writer;
    File		file;

    // display save dialog
    result = getFileChooser().showSaveDialog(m_Owner);
    if (result != JComponentWriterFileChooser.APPROVE_OPTION)
      return;

    // save the file
    try {
      file   = getFileChooser().getSelectedFile().getAbsoluteFile();
      writer = getFileChooser().getWriter();
      writer.setComponent(m_Owner);
      writer.setFile(new PlaceholderFile(file));
      writer.toOutput();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}