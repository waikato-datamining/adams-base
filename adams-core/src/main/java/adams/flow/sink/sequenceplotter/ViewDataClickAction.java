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
 * Copyright (C) 2014-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.GUIHelper;
import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.AbstractXYSequencePointHitDetector;
import adams.gui.visualization.sequence.CircleHitDetector;
import adams.gui.visualization.sequence.XYSequenceContainer;

import java.awt.Dialog.ModalityType;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

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
  protected AbstractXYSequencePointHitDetector m_HitDetector;

  /** the polygon points collected so far. */
  protected List<Point> m_Polygon;

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
      new CircleHitDetector());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Polygon = new ArrayList<>();
  }

  /**
   * Sets the hit detector to use.
   *
   * @param value 	the hit detector
   */
  public void setHitDetector(AbstractXYSequencePointHitDetector value) {
    m_HitDetector = value;
    reset();
  }

  /**
   * Returns the hit detector to use.
   *
   * @return 		the hit detector
   */
  public AbstractXYSequencePointHitDetector getHitDetector() {
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
  protected void showData(SequencePlotterPanel panel, SpreadSheet sheet) {
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
  protected void showPolygonPoints(SequencePlotterPanel panel) {
    int[]			x;
    int[]			y;
    int				i;
    Polygon 			poly;
    SpreadSheet			data;
    List<XYSequencePoint> 	hits;
    int				posX;
    int				posY;
    AxisPanel 			axisX;
    AxisPanel			axisY;
    XYSequenceContainer		cont;
    XYSequence			seq;

    if (m_Polygon.size() < 3) {
      m_Polygon.clear();
      return;
    }

    // create polygon
    x = new int[m_Polygon.size()];
    y = new int[m_Polygon.size()];
    for (i = 0; i < m_Polygon.size(); i++) {
      x[i] = (int) m_Polygon.get(i).getX();
      y[i] = (int) m_Polygon.get(i).getY();
    }
    poly = new Polygon(x, y, x.length);

    // iterate data
    hits  = new ArrayList<>();
    axisX = panel.getPlot().getAxis(Axis.BOTTOM);
    axisY = panel.getPlot().getAxis(Axis.LEFT);
    for (i = 0; i < panel.getSequenceManager().count(); i++) {
      cont = (XYSequenceContainer) panel.getSequenceManager().get(i);
      seq  = cont.getData();
      for (XYSequencePoint point: seq.toList()) {
	posX = axisX.valueToPos(point.getX());
	posY = axisY.valueToPos(point.getY());
	if (poly.contains(posX, posY))
	  hits.add(point);
      }
    }

    // clear points
    m_Polygon.clear();

    // display data
    showHits(panel, hits);
  }

  /**
   * Displays the points that were determined by the hit detector.
   *
   * @param panel 	the associated panel
   * @param hits	the hits
   */
  protected void showHits(SequencePlotterPanel panel, List<XYSequencePoint> hits) {
    SpreadSheet		sheet;

    if (hits.isEmpty())
      sheet = new DefaultSpreadSheet();
    else
      sheet = ((XYSequence) hits.get(0).getParent()).toSpreadSheet(hits);

    showData(panel, sheet);
  }

  /**
   * Gets triggered if the user clicks on the canvas.
   *
   * @param panel	the associated panel
   * @param e		the mouse event
   */
  @Override
  public void mouseClickOccurred(SequencePlotterPanel panel, MouseEvent e) {
    Object	located;

    if (MouseUtils.isRightClick(e)) {
      if (KeyUtils.isShiftDown(e.getModifiersEx())) {
	e.consume();
	showPolygonPoints(panel);
      }
      else if (KeyUtils.isCtrlDown(e.getModifiersEx())) {
	e.consume();
	m_Polygon.clear();
      }
    }
    else if (MouseUtils.isLeftClick(e)) {
      if (KeyUtils.isNoneDown(e.getModifiersEx())) {
	e.consume();
	if (m_HitDetector.getOwner() != panel.getDataPaintlet())
	  m_HitDetector.setOwner(panel.getDataPaintlet());
	located = m_HitDetector.locate(e);
	if (located instanceof List) {
	  showHits(panel, (List<XYSequencePoint>) located);
	}
      }
      else if (KeyUtils.isOnlyShiftDown(e.getModifiersEx())) {
	e.consume();
	// add polygon point
	m_Polygon.add(new Point(e.getX(), e.getY()));
      }
    }
  }
}
