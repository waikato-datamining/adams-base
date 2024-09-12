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
 * AbstractScatterPlot.java
 * Copyright (C) 2011-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.scatterplot;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.flow.sink.TextSupplier;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;
import adams.gui.visualization.core.PaintablePanel;
import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.core.plot.HitDetector;
import adams.gui.visualization.core.plot.HitDetectorSupporter;
import adams.gui.visualization.core.plot.TipTextCustomizer;
import adams.gui.visualization.stats.paintlet.AbstractScatterPlotPaintlet;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Abstract class for displaying a single scatterplotpanel on a paintable panel.
 *
 * @author msf8
 */
public abstract class AbstractScatterPlot
  extends PaintablePanel
  implements TipTextCustomizer, SpreadSheetSupporter, TextSupplier {

  /** for serialization */
  private static final long serialVersionUID = -3526702766287841051L;

  /**Scatter plot panel to display */
  protected ScatterPlotPanel m_Plot;

  /**Instances to be plotted */
  protected SpreadSheet m_Data;

  /**Index of attribute to be displayed on x axis */
  protected int m_XIntIndex;

  /**Index of attribute to be displayed on y axis */
  protected  int m_YIntIndex;

  /**Paintlet to display the data points on the scatter plot */
  protected AbstractScatterPlotPaintlet m_Paintlet;

  /** Array of overlay options */
  protected AbstractScatterPlotOverlay[] m_Overlays;

  /** whether selection is enabled. */
  protected boolean m_SelectionEnabled;

  /** the selection points collected so far. */
  protected List<Point2D.Double> m_Selection;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_XIntIndex        = 0;
    m_YIntIndex        = 0;
    m_Selection        = new ArrayList<>();
    m_SelectionEnabled = false;
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    m_Plot.addMouseClickListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (m_SelectionEnabled) {
	  if (MouseUtils.isRightClick(e)) {
	    if (KeyUtils.isCtrlDown(e.getModifiersEx())) {
	      e.consume();
	      m_Selection.clear();
	      update();
	    }
	  }
	  else if (MouseUtils.isLeftClick(e)) {
	    if (KeyUtils.isOnlyShiftDown(e.getModifiersEx())) {
	      e.consume();
	      // add polygon point
	      double valX = getPlot().getAxis(Axis.BOTTOM).posToValue(e.getX());
	      double valY = getPlot().getAxis(Axis.LEFT).posToValue(e.getY());
	      m_Selection.add(new Point2D.Double(valX, valY));
	    }
	  }
	}
      }
    });
  }

  /**
   * Returns the plot.
   *
   * @return		the plot
   */
  public PlotPanel getPlot() {
    return m_Plot;
  }

  /**
   * Set the paintlet for dawing the data
   * @param val			Paintlet for plotting
   */
  public void setPaintlet(AbstractScatterPlotPaintlet val) {
    removePaintlet(m_Paintlet);
    m_Paintlet = val;
  }

  /**
   * Returns true if the paintlets can be executed.
   *
   * @param g		the graphics context
   * @return		true if painting can go ahead
   */
  protected boolean canPaint(Graphics g) {
    return (m_Plot != null && m_Data != null);
  }

  /**
   * Sets the instances to be plotted
   * @param data	Instances to be plotted
   */
  public void setData(SpreadSheet data) {
    m_Data = data;
  }

  /**
   * Get the instances being plotted
   * @return		Instances being plotted
   */
  public SpreadSheet getData() {
    return m_Data;
  }

  /**
   * gets the index of the x attribute
   * @return		index of x attribute
   */
  public int getXIntIndex() {
    return m_XIntIndex;
  }

  /**
   * Sets the index of the y attribute
   * @return		index of y attribute
   */
  public int getYIntIndex() {
    return m_YIntIndex;
  }

  /**
   * Removes all overlays from the scatterplot. Removes the paintlets that
   * do the drawing as well as resetting the array
   */
  public void removeOverlays() {
    if(m_Overlays != null) {
      for(int i = 0; i< m_Overlays.length; i++)
	removePaintlet(m_Overlays[i].getPaintlet());
      m_Overlays = null;
    }
  }

  /**
   * Set the index of the attribute to be displayed on
   * the x axis
   * @param value		0-based index of attribute
   */
  public void setXIntIndex(int value) {
    m_XIntIndex = value;
    update();
  }

  /**
   * Set the index of the attribute to be displayed on
   * the y axis
   * @param value		0-based index of attribute
   */
  public void setYIntIndex(int value) {
    m_YIntIndex = value;
    update();
  }

  /**
   * Processes the given tip text. Among the current mouse position, the
   * panel that initiated the call are also provided.
   *
   * @param panel	the content panel that initiated this call
   * @param mouse	the mouse position
   * @param tiptext	the tiptext so far
   * @return		the processed tiptext
   */
  public String processTipText(PlotPanel panel, Point mouse, String tiptext) {
    String		result;
    MouseEvent 		event;
    String		hit;
    HitDetector 	detector;

    result = tiptext;
    event  = new MouseEvent(
      getPlot().getContent(),
      MouseEvent.MOUSE_MOVED,
      new Date().getTime(),
      0,
      (int) mouse.getX(),
      (int) mouse.getY(),
      0,
      false);

    detector = ((HitDetectorSupporter) m_Paintlet).getHitDetector();
    if (detector != null) {
      hit = (String) detector.detect(event);
      if (hit != null) {
	if (result == null)
	  result = hit;
	else
	  result = " (" + hit + ")";
      }
    }

    return result;
  }

  /**
   * Returns the content as spreadsheet.
   *
   * @return		the content
   */
  @Override
  public SpreadSheet toSpreadSheet() {
    return m_Data;
  }

  /**
   * Returns the text for the menu item.
   *
   * @return		the menu item text, null for default
   */
  public String getCustomSupplyTextMenuItemCaption() {
    return "Save plot as...";
  }

  /**
   * Returns a custom file filter for the file chooser.
   *
   * @return		the file filter, null if to use default one
   */
  public ExtensionFileFilter getCustomTextFileFilter() {
    return new ExtensionFileFilter("CSV files", "csv");
  }

  /**
   * Supplies the text. May get called even if actor hasn't been executed yet.
   *
   * @return		the text, null if none available
   */
  public String supplyText() {
    return toSpreadSheet().toString();
  }

  /**
   * Returns the currently selected points.
   *
   * @return		the points
   */
  public List<Point2D.Double> getSelection() {
    return m_Selection;
  }

  /**
   * Removes any selected points.
   */
  public void clearSelection() {
    m_Selection.clear();
  }

  /**
   * Sets whether data points can be selected.
   *
   * @param value	true if can be selected
   */
  public void setSelectionEnabled(boolean value) {
    m_SelectionEnabled = value;
  }

  /**
   * Returns whether data points can be selected.
   *
   * @return		true if can be selected
   */
  public boolean isSelectionEnabled() {
    return m_SelectionEnabled;
  }
}