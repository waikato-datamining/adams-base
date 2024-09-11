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
 * ViewDataClickAction.java
 * Copyright (C) 2016-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.stats.scatterplot.action;

import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.GUIHelper;
import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.stats.paintlet.AbstractScatterPlotHitDetector;
import adams.gui.visualization.stats.paintlet.ScatterPlotCircleHitDetector;
import adams.gui.visualization.stats.scatterplot.ScatterPlot;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.awt.Dialog.ModalityType;
import java.awt.Polygon;
import java.awt.event.MouseEvent;

/**
 * Displays the data that the user clicked on in a table.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ViewDataClickAction
  extends AbstractMouseClickAction {

  /** for serialization. */
  private static final long serialVersionUID = -1383042782074675611L;

  /** the hit detector to use. */
  protected AbstractScatterPlotHitDetector m_HitDetector;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the data that the user selected.\n"
	     + "A single left-click determines the affected data points.\n"
	     + "Left-clicking while holding SHIFT selects polygon vertices to enclose points to display. "
	     + "A SHIFT+right-click finalizes the polygon and displays the points. "
	     + "A CTRL+right-click discards the polygon vertices.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "hit-detector", "hitDetector",
      new ScatterPlotCircleHitDetector());
  }

  /**
   * Sets the hit detector to use.
   *
   * @param value 	the hit detector
   */
  public void setHitDetector(AbstractScatterPlotHitDetector value) {
    m_HitDetector = value;
    reset();
  }

  /**
   * Returns the hit detector to use.
   *
   * @return 		the hit detector
   */
  public AbstractScatterPlotHitDetector getHitDetector() {
    return m_HitDetector;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hitDetectorTipText() {
    return "The hit detector to use.";
  }

  /**
   * Displays the data.
   *
   * @param panel 	the associated panel
   * @param sheet 	the data to display
   */
  protected void showData(ScatterPlot panel, SpreadSheet sheet) {
    SpreadSheetDialog		dialog;

    if (sheet.getRowCount() > 0) {
      if (panel.getParentDialog() != null)
	dialog = new SpreadSheetDialog(panel.getParentDialog(), ModalityType.MODELESS);
      else
	dialog = new SpreadSheetDialog(panel.getParentFrame(), false);
      dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
      dialog.setTitle("Data");
      dialog.setSize(GUIHelper.getDefaultDialogDimension());
      dialog.setLocationRelativeTo(panel);
      dialog.setSpreadSheet(sheet);
      dialog.getTable().setOptimalColumnWidthBounded(100);
      dialog.setVisible(true);
    }
  }

  /**
   * Displays the points that were surrounded by the polygon.
   *
   * @param panel	the associated panel
   */
  protected void showPolygonPoints(ScatterPlot panel) {
    int[]	x;
    int[]	y;
    int		i;
    Polygon	poly;
    SpreadSheet	sheet;
    SpreadSheet	data;
    int		colX;
    int		colY;
    TIntList	hits;
    Double	valX;
    Double	valY;
    int		posX;
    int		posY;
    AxisPanel 	axisX;
    AxisPanel	axisY;

    if (panel.getSelection().size() < 3) {
      panel.clearSelection();
      return;
    }

    axisX = panel.getPlot().getAxis(Axis.BOTTOM);
    axisY = panel.getPlot().getAxis(Axis.LEFT);

    // create polygon
    x = new int[panel.getSelection().size()];
    y = new int[panel.getSelection().size()];
    for (i = 0; i < panel.getSelection().size(); i++) {
      x[i] = axisX.valueToPos(panel.getSelection().get(i).getX());
      y[i] = axisY.valueToPos(panel.getSelection().get(i).getY());
    }
    poly = new Polygon(x, y, x.length);

    // iterate data
    hits  = new TIntArrayList();
    sheet = panel.toSpreadSheet();
    colX  = panel.getXIntIndex();
    colY  = panel.getYIntIndex();
    for (i = 0; i < sheet.getRowCount(); i++) {
      valX = sheet.getCell(i, colX).toDouble();
      valY = sheet.getCell(i, colY).toDouble();
      if ((valX == null) || (valY == null))
	continue;
      if (Double.isNaN(valX) || (Double.isNaN(valY)))
	continue;
      posX = axisX.valueToPos(valX);
      posY = axisY.valueToPos(valY);
      if (poly.contains(posX, posY))
	hits.add(i);
    }

    // clear points
    panel.clearSelection();
    panel.update();

    // display data
    data = sheet.toView(hits.toArray(), null);
    showData(panel, data);
  }

  /**
   * Displays the points that were determined by the hit detector.
   *
   * @param panel 	the associated panel
   * @param hits	the hits
   */
  protected void showHits(ScatterPlot panel, int[] hits) {
    SpreadSheet		sheet;

    sheet = m_HitDetector.getOwner().getData().getHeader();
    for (int hit: hits)
      sheet.addRow().assign(m_HitDetector.getOwner().getData().getRow(hit));

    showData(panel, sheet);
  }

  /**
   * Gets triggered if the user clicks on the canvas.
   *
   * @param panel	the associated panel
   * @param e		the mouse event
   */
  @Override
  public void mouseClickOccurred(ScatterPlot panel, MouseEvent e) {
    Object	located;

    if (MouseUtils.isRightClick(e)) {
      if (KeyUtils.isShiftDown(e.getModifiersEx())) {
	e.consume();
	showPolygonPoints(panel);
      }
    }
    else if (MouseUtils.isLeftClick(e)) {
      if (KeyUtils.isNoneDown(e.getModifiersEx())) {
	e.consume();
	if (m_HitDetector.getOwner() != panel.getPaintlet())
	  m_HitDetector.setOwner(panel.getPaintlet());
	located = m_HitDetector.locate(e);
	if (located instanceof int[]) {
	  showHits(panel, (int[]) located);
	}
      }
    }
  }
}
