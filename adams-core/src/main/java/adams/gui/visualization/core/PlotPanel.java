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
 * PlotPanel.java
 * Copyright (C) 2008-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

import adams.gui.event.PaintEvent;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.event.PaintListener;
import adams.gui.event.PlotPanelPanningListener;
import adams.gui.event.PlotPanelZoomListener;
import adams.gui.print.PrintMouseListener;
import adams.gui.visualization.core.axis.Direction;
import adams.gui.visualization.core.axis.Orientation;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.axis.Visibility;
import adams.gui.visualization.core.plot.AbstractHitDetector;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.core.plot.ContentPanel;
import adams.gui.visualization.core.plot.TipTextCustomizer;

/**
 * A panel that contains a drawing (= content) area with (usually) two axes.
 * Display and parametrization of top/bottom/left/right axis is possible.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlotPanel
  extends JPanel {

  /** for serialization. */
  private static final long serialVersionUID = -3406313912401195452L;

  /** the bottom axis panel. */
  protected AxisPanel m_AxisBottom;

  /** the top axis panel. */
  protected AxisPanel m_AxisTop;

  /** the left axis panel. */
  protected AxisPanel m_AxisLeft;

  /** the right axis panel. */
  protected AxisPanel m_AxisRight;

  /** the content panel for drawing. */
  protected ContentPanel m_PanelContent;

  /** the width of the axes. */
  protected int m_AxisWidth;

  /** the top-left corner panel. */
  protected JPanel m_CornerTopLeft;

  /** the top-right corner panel. */
  protected JPanel m_CornerTopRight;

  /** the bottom-left corner panel. */
  protected JPanel m_CornerBottomLeft;

  /** the bottom-right corner panel. */
  protected JPanel m_CornerBottomRight;

  /** the paint listeners. */
  protected HashSet<PaintListener> m_PaintListeners;

  /** the mouse click listeners. */
  protected HashSet<MouseListener> m_MouseClickListeners;

  /** the vector with the axes that make up the values in the tooltip. */
  protected Vector<Axis> m_ToolTipAxes;

  /** whether debug mode is on. */
  protected boolean m_Debug;

  /** the color for the grid. */
  protected Color m_GridColor;

  /** the background color. */
  protected Color m_BackgroundColor;

  /** the foregorund color. */
  protected Color m_ForegroundColor;

  /**
   * Initializes the panel.
   */
  public PlotPanel() {
    this(false);
  }

  /**
   * Initializes the panel.
   *
   * @param debug	if true then some debugging is turned on.
   */
  public PlotPanel(boolean debug) {
    super();

    m_Debug               = debug;
    m_AxisWidth           = 40;
    m_PaintListeners      = new HashSet<PaintListener>();
    m_MouseClickListeners = new HashSet<MouseListener>();
    m_ToolTipAxes         = new Vector<Axis>();
    m_GridColor           = new Color(235, 235, 235);
    m_BackgroundColor     = Color.WHITE;
    m_ForegroundColor     = Color.BLACK;

    initGUI();
  }

  /**
   * Creates a corner panel (square panel that cannot be resized).
   *
   * @return		the corner panel
   */
  protected JPanel createCornerPanel() {
    JPanel	result;

    result = new JPanel();
    result.setMinimumSize(new Dimension(m_AxisWidth, 0));
    result.setMaximumSize(new Dimension(m_AxisWidth, 0));
    result.setPreferredSize(new Dimension(m_AxisWidth, 0));

    return result;
  }

  /**
   * updates the dimensions of the corner panel.
   *
   * @param panel	the panel to updated
   * @param width	the new width
   * @param height	the new height
   */
  protected void updateCornerPanel(JPanel panel, int width, int height) {
    Dimension	size;

    size = new Dimension(width, height);
    panel.setPreferredSize(size);
    panel.setMinimumSize(size);
    panel.setMaximumSize(size);
  }

  /**
   * Initializes the GUI components.
   */
  protected void initGUI() {
    JPanel	panelNorth;
    JPanel	panelSouth;

    /*
     *      _________
     *     |____T____|
     *  _   _________   _
     * | | |         | | |
     * | | |         | | |
     * |L| | Content | |R|
     * | | |         | | |
     * |_| |_________| |_|
     *      _________
     *     |____B____|
     */

    setLayout(new BorderLayout());

    m_PanelContent = new ContentPanel(this);
    add(m_PanelContent, BorderLayout.CENTER);

    // axes
    m_AxisLeft = new AxisPanel(Direction.VERTICAL, Orientation.LEFT_TO_RIGHT, Type.ABSOLUTE);
    m_AxisLeft.setAxisWidth(m_AxisWidth);
    m_AxisLeft.addChangeListener(m_PanelContent);

    m_AxisRight = new AxisPanel(Direction.VERTICAL, Orientation.RIGHT_TO_LEFT, Type.ABSOLUTE);
    m_AxisRight.setAxisWidth(m_AxisWidth);
    m_AxisRight.addChangeListener(m_PanelContent);

    m_AxisTop = new AxisPanel(Direction.HORIZONTAL, Orientation.RIGHT_TO_LEFT, Type.ABSOLUTE);
    m_AxisTop.setAxisWidth(m_AxisWidth);
    m_AxisTop.addChangeListener(m_PanelContent);

    m_AxisBottom = new AxisPanel(Direction.HORIZONTAL, Orientation.LEFT_TO_RIGHT, Type.ABSOLUTE);
    m_AxisBottom.setAxisWidth(m_AxisWidth);
    m_AxisBottom.addChangeListener(m_PanelContent);

    // left-clicking on axes sets focus in plotpanel (if possible)
    for (Axis axis: Axis.values()) {
      getAxis(axis).addMouseListener(new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent e) {
	  if (    (e.getButton() == MouseEvent.BUTTON1)
	       && (e.getModifiers() == MouseEvent.BUTTON1_MASK) ) {
	    getContent().requestFocusInWindow();
	    e.consume();
	  }
	  else {
	    super.mouseClicked(e);
	  }
	}
      });
    }

    // corner panels
    m_CornerTopLeft     = createCornerPanel();
    m_CornerTopRight    = createCornerPanel();
    m_CornerBottomLeft  = createCornerPanel();
    m_CornerBottomRight = createCornerPanel();

    // left
    add(m_AxisLeft, BorderLayout.WEST);

    // right
    add(m_AxisRight, BorderLayout.EAST);

    // top
    panelNorth = new JPanel(new BorderLayout());
    add(panelNorth, BorderLayout.NORTH);
    panelNorth.add(m_CornerTopLeft, BorderLayout.WEST);
    panelNorth.add(m_AxisTop, BorderLayout.CENTER);
    panelNorth.add(m_CornerTopRight, BorderLayout.EAST);

    // bottom
    panelSouth = new JPanel(new BorderLayout());
    add(panelSouth, BorderLayout.SOUTH);
    panelSouth.add(m_CornerBottomLeft, BorderLayout.WEST);
    panelSouth.add(m_AxisBottom, BorderLayout.CENTER);
    panelSouth.add(m_CornerBottomRight, BorderLayout.EAST);

    // adjust sizes
    setAxisWidth(Axis.LEFT, 80);
    setAxisWidth(Axis.RIGHT, 80);
    setAxisWidth(Axis.TOP, 40);
    setAxisWidth(Axis.BOTTOM, 40);

    // disable all axes by default
    setAxisVisibility(Axis.LEFT, Visibility.INVISIBLE);
    setAxisVisibility(Axis.RIGHT, Visibility.INVISIBLE);
    setAxisVisibility(Axis.TOP, Visibility.INVISIBLE);
    setAxisVisibility(Axis.BOTTOM, Visibility.INVISIBLE);

    if (m_Debug) {
      m_AxisLeft.setAxisName("Left");
      m_AxisRight.setAxisName("Right");
      m_AxisTop.setAxisName("Top");
      m_AxisBottom.setAxisName("Bottom");

      m_AxisLeft.setType(Type.PERCENTAGE);
      m_AxisLeft.setMinimum(1.0);
      m_AxisLeft.setMaximum(11.0);
      m_AxisRight.setType(Type.ABSOLUTE);
      m_AxisRight.setMinimum(-2.0);
      m_AxisRight.setMaximum(11.0);
      m_AxisTop.setType(Type.ABSOLUTE);
      m_AxisTop.setMinimum(0.0);
      m_AxisTop.setMaximum(1.0);
      m_AxisBottom.setType(Type.PERCENTAGE);
      m_AxisBottom.setMinimum(3.0);
      m_AxisBottom.setMaximum(27.0);

      m_AxisLeft.setBackground(Color.RED.brighter());
      m_AxisRight.setBackground(Color.RED.darker());
      m_AxisTop.setBackground(Color.GREEN.brighter());
      m_AxisBottom.setBackground(Color.GREEN.darker());
      m_PanelContent.setBackground(Color.BLUE.brighter());

      m_CornerTopLeft.setBackground(Color.YELLOW);
      m_CornerTopRight.setBackground(Color.YELLOW);
      m_CornerBottomLeft.setBackground(Color.YELLOW);
      m_CornerBottomRight.setBackground(Color.YELLOW);

      addToolTipAxis(Axis.LEFT);
      addToolTipAxis(Axis.RIGHT);
      addToolTipAxis(Axis.BOTTOM);
      addToolTipAxis(Axis.TOP);

      //m_AxisLeft.setAxisNameCentered(true);
      //m_AxisRight.setAxisNameCentered(true);
      //m_AxisTop.setAxisNameCentered(true);
      //m_AxisBottom.setAxisNameCentered(true);

      setAxisVisibility(Axis.LEFT, Visibility.VISIBLE);
      setAxisVisibility(Axis.RIGHT, Visibility.VISIBLE);
      setAxisVisibility(Axis.TOP, Visibility.VISIBLE);
      setAxisVisibility(Axis.BOTTOM, Visibility.VISIBLE);

      // plot some reference points
      addPaintListener(new PaintListener() {
	public void painted(PaintEvent e) {
	  Graphics g = e.getGraphics();
	  int y = m_AxisLeft.valueToPos(1.0);
	  int x = m_AxisBottom.valueToPos(5.0);
	  g.drawLine(x, y, x, y);
	}
      });
    }

    addPrintScreenListener(this);
    addPrintScreenListener(m_PanelContent);
    addPrintScreenListener(m_AxisTop);
    addPrintScreenListener(m_AxisBottom);
    addPrintScreenListener(m_AxisLeft);
    addPrintScreenListener(m_AxisRight);
    
    m_PanelContent.addMouseListener(new MouseListener() {
      @Override
      public void mouseReleased(MouseEvent e) {
      }
      @Override
      public void mousePressed(MouseEvent e) {
      }
      @Override
      public void mouseExited(MouseEvent e) {
      }
      @Override
      public void mouseEntered(MouseEvent e) {
      }
      @Override
      public void mouseClicked(MouseEvent e) {
	if (m_MouseClickListeners.size() > 0)
	  notifyMouseClickListeners(e);
      }
    });
  }

  /**
   * Adds a "print screen" listener to the container.
   *
   * @param comp	the component to add the listener to
   */
  protected void addPrintScreenListener(JComponent comp) {
    new PrintMouseListener(comp, this);
  }

  /**
   * Sets the foreground color to use.
   *
   * @param value	the color to use
   */
  public void setForegroundColor(Color value) {
    m_ForegroundColor = value;
    repaint();
  }

  /**
   * Returns the current foreground color in use.
   *
   * @return		the color in use
   */
  public Color getForegroundColor() {
    return m_ForegroundColor;
  }

  /**
   * Sets the background color to use.
   *
   * @param value	the color to use
   */
  public void setBackgroundColor(Color value) {
    m_BackgroundColor = value;
    repaint();
  }

  /**
   * Returns the current background color in use.
   *
   * @return		the color in use
   */
  public Color getBackgroundColor() {
    return m_BackgroundColor;
  }

  /**
   * Sets the widht of all the axes.
   *
   * @param value	the width to set
   */
  public void setAxisWidths(int value) {
    m_AxisWidth = value;

    m_AxisLeft.setAxisWidth(m_AxisWidth);
    m_AxisRight.setAxisWidth(m_AxisWidth);
    m_AxisTop.setAxisWidth(m_AxisWidth);
    m_AxisBottom.setAxisWidth(m_AxisWidth);

    m_CornerTopLeft.setSize(new Dimension(m_AxisWidth, m_AxisWidth));
    m_CornerTopRight.setSize(new Dimension(m_AxisWidth, m_AxisWidth));
    m_CornerBottomLeft.setSize(new Dimension(m_AxisWidth, m_AxisWidth));
    m_CornerBottomRight.setSize(new Dimension(m_AxisWidth, m_AxisWidth));
  }

  /**
   * Sets the size of the specified axis.
   *
   * @param axis	the axis to set the width for
   * @param width	the width
   */
  public void setAxisWidth(Axis axis, int width) {
    getAxis(axis).setAxisWidth(width);
    updateCorner(axis);
  }

  /**
   * Updates the dimensions of the corner panels.
   *
   * @param axis	the axis to update
   */
  protected void updateCorner(Axis axis) {
    switch (axis) {
      case LEFT:
	updateCornerPanel(m_CornerTopLeft, getActualAxisWidth(axis), m_CornerTopLeft.getPreferredSize().height);
	updateCornerPanel(m_CornerBottomLeft, getActualAxisWidth(axis), m_CornerBottomLeft.getPreferredSize().height);
	break;

      case RIGHT:
	updateCornerPanel(m_CornerTopRight, getActualAxisWidth(axis), m_CornerTopRight.getPreferredSize().height);
	updateCornerPanel(m_CornerBottomRight, getActualAxisWidth(axis), m_CornerBottomRight.getPreferredSize().height);
	break;

      case TOP:
	updateCornerPanel(m_CornerTopLeft, m_CornerTopLeft.getPreferredSize().width, getActualAxisWidth(axis));
	updateCornerPanel(m_CornerTopRight, m_CornerTopRight.getPreferredSize().width, getActualAxisWidth(axis));
	break;

      case BOTTOM:
	updateCornerPanel(m_CornerBottomLeft, m_CornerBottomLeft.getPreferredSize().width, getActualAxisWidth(axis));
	updateCornerPanel(m_CornerBottomRight, m_CornerBottomRight.getPreferredSize().width, getActualAxisWidth(axis));
	break;
    }
  }

  /**
   * Updates all the corners
   */
  public void updateCorners() {
    for (Axis axis: Axis.values())
      updateCorner(axis);
  }
  
  /**
   * Returns the size of the specified axis.
   *
   * @param axis	the axis to get the width for
   * @return		the width
   */
  public int getAxisWidth(Axis axis) {
    return getAxis(axis).getAxisWidth();
  }

  /**
   * Returns the actual size of the specified axis, depending on visibility.
   *
   * @param axis	the axis to get the width for
   * @return		the actual width
   */
  public int getActualAxisWidth(Axis axis) {
    return getAxis(axis).getActualAxisWidth();
  }

  /**
   * Returns the specified axis.
   *
   * @param axis	the axis to return
   * @return		the corresponding axis
   */
  public AxisPanel getAxis(Axis axis) {
    if (axis == Axis.LEFT)
      return m_AxisLeft;
    else if (axis == Axis.RIGHT)
      return m_AxisRight;
    else if (axis == Axis.TOP)
      return m_AxisTop;
    else if (axis == Axis.BOTTOM)
      return m_AxisBottom;
    else
      throw new IllegalStateException("Unhandled axis '" + axis + "'!");
  }

  /**
   * Sets the visibility state of the specified axis.
   *
   * @param axis	the axis to hide/show
   * @param visible	the state
   */
  public void setAxisVisibility(Axis axis, Visibility visible) {
    getAxis(axis).setVisibility(visible);
    updateCorner(axis);
  }

  /**
   * Returns the visibility of the specified axis.
   *
   * @param axis	the axis to retrieve the visibility state for
   * @return		the state
   */
  public Visibility getAxisVisibility(Axis axis) {
    return getAxis(axis).getVisibility();
  }

  /**
   * Sets whether zooming is enabled or not.
   *
   * @param value	if true then zooming is enabled
   */
  public void setZoomingEnabled(boolean value) {
    m_PanelContent.setZoomingEnabled(value);
  }

  /**
   * Returns whether zooming is enabled.
   *
   * @return		true if zooming is enabled
   */
  public boolean isZoomingEnabled() {
    return m_PanelContent.isZoomingEnabled();
  }

  /**
   * Returns true if any of the axis is zoomed.
   *
   * @return		true if the graph was zoomed
   */
  public boolean isZoomed() {
    if (!isZoomingEnabled())
      return false;
    else
      return    (m_AxisLeft.isZoomed())
             || (m_AxisRight.isZoomed())
             || (m_AxisTop.isZoomed())
             || (m_AxisBottom.isZoomed());
  }

  /**
   * Clears the zoom.
   */
  public void clearZoom() {
    m_PanelContent.clearZoom();
  }

  /**
   * Sets whether panning is enabled or not.
   *
   * @param value	if true then panning is enabled
   */
  public void setPanningEnabled(boolean value) {
    m_PanelContent.setPanningEnabled(value);
  }

  /**
   * Returns whether panning is enabled.
   *
   * @return		true if panning is enabled
   */
  public boolean isPanningEnabled() {
    return m_PanelContent.isPanningEnabled();
  }

  /**
   * Returns true if any of the axis has a pixel offset != 0.
   *
   * @return		true if the graph was panned
   */
  public boolean isPanned() {
    if (!isPanningEnabled())
      return false;
    else
      return    (m_AxisLeft.getPixelOffset() != 0)
             || (m_AxisRight.getPixelOffset() != 0)
             || (m_AxisTop.getPixelOffset() != 0)
             || (m_AxisBottom.getPixelOffset() != 0);
  }

  /**
   * Clears the panning.
   */
  public void clearPanning() {
    m_PanelContent.clearPanning();
  }

  /**
   * Returns the content panel.
   *
   * @return		the panel to draw on
   */
  public ContentPanel getContent() {
    return m_PanelContent;
  }

  /**
   * Adds the given listener to the internal list of paint listeners.
   *
   * @param l		the listener to add
   */
  public void addPaintListener(PaintListener l) {
    m_PaintListeners.add(l);
  }

  /**
   * Removes the given listener from the internal list of paint listeners.
   *
   * @param l		the listener to remove
   */
  public void removePaintListener(PaintListener l) {
    m_PaintListeners.remove(l);
  }

  /**
   * Notifies all paint listeners.
   *
   * @param g		the graphics context of the paint update
   * @param moment	the paint moment, indicating which paintlets are to
   * 			be executed
   */
  public void notifyPaintListeners(Graphics g, PaintMoment moment) {
    Iterator<PaintListener>	iter;
    PaintEvent			e;

    e    = new PaintEvent(this, g, moment);
    iter = m_PaintListeners.iterator();
    while (iter.hasNext())
      iter.next().painted(e);
  }

  /**
   * Adds the given listener to the internal list of mouse click listeners.
   *
   * @param l		the listener to add
   */
  public void addMouseClickListener(MouseListener l) {
    m_MouseClickListeners.add(l);
  }

  /**
   * Removes the given listener from the internal list of mouse click listeners.
   *
   * @param l		the listener to remove
   */
  public void removeMouseClickListener(MouseListener l) {
    m_MouseClickListeners.remove(l);
  }

  /**
   * Notifies all paint listeners.
   *
   * @param g		the graphics context of the paint update
   * @param moment	the paint moment, indicating which paintlets are to
   * 			be executed
   */
  public void notifyMouseClickListeners(MouseEvent e) {
    Iterator<MouseListener>	iter;

    iter = m_MouseClickListeners.iterator();
    while (iter.hasNext())
      iter.next().mouseClicked(e);
  }

  /**
   * Adds the given listener to the internal list of zoom listeners.
   *
   * @param l		the listener to add
   */
  public void addZoomListener(PlotPanelZoomListener l) {
    m_PanelContent.addZoomListener(l);
  }

  /**
   * Removes the given listener from the internal list of zoom listeners.
   *
   * @param l		the listener to remove
   */
  public void removeZoomListener(PlotPanelZoomListener l) {
    m_PanelContent.removeZoomListener(l);
  }

  /**
   * Adds the given listener to the internal list of panning listeners.
   *
   * @param l		the listener to add
   */
  public void addPanningListener(PlotPanelPanningListener l) {
    m_PanelContent.addPanningListener(l);
  }

  /**
   * Removes the given listener from the internal list of panning listeners.
   *
   * @param l		the listener to remove
   */
  public void removePanningListener(PlotPanelPanningListener l) {
    m_PanelContent.removePanningListener(l);
  }

  /**
   * Adds the given listener to the internal list of mouse movement 
   * tracking listeners.
   *
   * @param l		the listener to add
   */
  public void addMouseMovementTracker(MouseMovementTracker l) {
    m_PanelContent.addMouseMovementTracker(l);
  }

  /**
   * Removes the given listener from the internal list of mouse movement 
   * tracking listeners.
   *
   * @param l		the listener to remove
   */
  public void removeMouseMovementTracker(MouseMovementTracker l) {
    m_PanelContent.removeMouseMovementTracker(l);
  }

  /**
   * Removes display of tool tips for all axis.
   */
  public void clearToolTipAxes() {
    m_ToolTipAxes.clear();
  }

  /**
   * Adds the axis at the end of the vector with the axes making up the
   * tooltip of values of the content panel. If the axis already existed,
   * then it will be deleted first before added at the end.
   *
   * @param axis	the axis to add
   */
  public void addToolTipAxis(Axis axis) {
    removeToolTipAxis(axis);
    m_ToolTipAxes.add(axis);
  }

  /**
   * Removes the axis from the vector with the axes making up the tooltip
   * of values of the content panel.
   *
   * @param axis	the axis to remove
   */
  public void removeToolTipAxis(Axis axis) {
    if (m_ToolTipAxes.contains(axis))
      m_ToolTipAxes.remove(axis);
  }

  /**
   * Checks whether the specified axis is used in making up the tooltip.
   *
   * @param axis	the axis to check
   * @return		true if the axis is used in making up the toolip
   */
  public boolean hasToolTipAxis(Axis axis) {
    return m_ToolTipAxes.contains(axis);
  }

  /**
   * Sets the color to use for painting the coordinates grid.
   *
   * @param value	the color to use
   */
  public void setGridColor(Color value) {
    m_GridColor = value;
  }

  /**
   * Returns the color used for painting the coordinates grid.
   *
   * @return		the color in use
   */
  public Color getGridColor() {
    return m_GridColor;
  }

  /**
   * Sets the class to customize the right-click popup menu.
   *
   * @param value	the customizer
   */
  public void setPopupMenuCustomizer(PopupMenuCustomizer value) {
    m_PanelContent.setPopupMenuCustomizer(value);
  }

  /**
   * Returns the current customizer, can be null.
   *
   * @return		the customizer
   */
  public PopupMenuCustomizer getPopupMenuCustomizer() {
    return m_PanelContent.getPopupMenuCustomizer();
  }

  /**
   * Sets the class to customize the right-click popup menu of an axis.
   *
   * @param axis	the axis to set the customizer for
   * @param value	the customizer
   */
  public void setAxisPopupMenuCustomizer(Axis axis, PopupMenuCustomizer value) {
    getAxis(axis).setPopupMenuCustomizer(value);
  }

  /**
   * Returns the current axis customizer, can be null.
   *
   * @param axis	the axis to get the customizer for
   * @return		the customizer
   */
  public PopupMenuCustomizer getAxisPopupMenuCustomizer(Axis axis) {
    return getAxis(axis).getPopupMenuCustomizer();
  }

  /**
   * Sets the class for customizing the tip text.
   *
   * @param value	the customizer
   */
  public void setTipTextCustomizer(TipTextCustomizer value) {
    m_PanelContent.setTipTextCustomizer(value);
  }

  /**
   * Returns the current tip text customizer, can be null.
   *
   * @return		the customizer
   */
  public TipTextCustomizer getTipTextCustomizer() {
    return m_PanelContent.getTipTextCustomizer();
  }

  /**
   * Removes all hit detectors.
   */
  public void clearHitDetectors() {
    m_PanelContent.clearHitDetectors();
  }

  /**
   * Adds the detector to the internal list of detectors.
   *
   * @param detector		the detector to add
   */
  public void addHitDetector(AbstractHitDetector detector) {
    m_PanelContent.addHitDetector(detector);
  }

  /**
   * Removes the detector from the internal list of detectors.
   *
   * @param detector		the detector to remover
   */
  public void removeHitDetector(AbstractHitDetector detector) {
    m_PanelContent.removeHitDetector(detector);
  }

  /**
   * Adds a zoom.
   *
   * @param top		the top value
   * @param left	the left value
   * @param bottom	the bottom value
   * @param right	the right value
   */
  public void addZoom(double top, double left, double bottom, double right) {
    m_PanelContent.addZoom(top, left, bottom, right);
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
    m_PanelContent.addZoom(top, left, bottom, right);
  }
  
  /**
   * Returns a short description of the panel.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    return "left: " + m_AxisLeft + ", top: " + m_AxisTop + ", right: " + m_AxisRight + ", bottom: " + m_AxisBottom;
  }
}
