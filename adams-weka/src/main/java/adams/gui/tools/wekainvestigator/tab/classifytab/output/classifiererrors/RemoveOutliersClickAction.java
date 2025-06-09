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
 * RemoveOutliersClickAction.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output.classifiererrors;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.RemoveOutliers;
import adams.flow.sink.sequenceplotter.AbstractMouseClickAction;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.KeyUtils;
import adams.gui.core.MouseUtils;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.AbstractXYSequencePointHitDetector;
import adams.gui.visualization.sequence.CrossHitDetector;
import adams.gui.visualization.sequence.XYSequenceContainer;

import java.awt.Dialog.ModalityType;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Allows the user to toggle the outlier state of data points to be removed.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RemoveOutliersClickAction
  extends AbstractMouseClickAction {

  private static final long serialVersionUID = -214148459426250712L;

  /** the hit detector to use. */
  protected AbstractXYSequencePointHitDetector m_HitDetector;

  /** the listener for removing the data. */
  protected RemoveDataListener m_RemoveDataListener;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to toggle the outlier state of data points to be removed.\n"
	     + "Use CTRL+left-click to toggle individual points.\n"
	     + "Use SHIFT+left-click to add polygon vertices and CTRL+SHIFT+left-click to finalize the "
	     + "polygon and toggle points enclosed by the polygon. CTRL+right-click discards the polygon vertices.\n"
	     + "SHIFT+right-clicks displays the outliers scheduled for removal.\n"
	     + "CTRL+SHIFT+right-click clears all outlier flags.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_RemoveDataListener = null;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "hit-detector", "hitDetector",
      new CrossHitDetector());
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
   * Sets the listener.
   *
   * @param l		the listener, null to unset
   */
  public void setRemoveDataListener(RemoveDataListener l) {
    m_RemoveDataListener = l;
  }

  /**
   * Returns the listener, if any.
   *
   * @return		the listener, null if none set
   */
  public RemoveDataListener getRemoveDataListener() {
    return m_RemoveDataListener;
  }

  /**
   * Sets or toggles the outlier state of the specified points.
   *
   * @param panel 	the panel the points belong to
   * @param hits 	the points to process
   */
  protected void toggleHits(SequencePlotterPanel panel, List<XYSequencePoint> hits) {
    SequencePlotPoint point;

    for (XYSequencePoint hit : hits) {
      if (hit instanceof SequencePlotPoint) {
	point = (SequencePlotPoint) hit;
	if (point.hasMetaData()) {
	  if (point.getMetaData().containsKey(RemoveOutliers.KEY_OUTLIER))
	    point.getMetaData().put(
	      RemoveOutliers.KEY_OUTLIER,
	      !((Boolean) point.getMetaData().get(RemoveOutliers.KEY_OUTLIER)));
	  else
	    point.getMetaData().put(
	      RemoveOutliers.KEY_OUTLIER,
	      true);
	}
	else {
	  point.setMetaData(new HashMap<>());
	  point.getMetaData().put(
	    RemoveOutliers.KEY_OUTLIER,
	    true);
	}
      }
    }
    panel.update();
  }

  /**
   * Displays the points that were surrounded by the polygon.
   *
   * @param panel	the associated panel
   */
  protected void togglePolygonPoints(SequencePlotterPanel panel) {
    int[]			x;
    int[]			y;
    int				i;
    Polygon 			poly;
    List<XYSequencePoint> 	hits;
    int				posX;
    int				posY;
    AxisPanel 			axisX;
    AxisPanel			axisY;
    XYSequenceContainer 	cont;
    XYSequence 			seq;

    if (panel.getSelection().size() < 3) {
      panel.clearSelection();
      return;
    }

    panel.addUndoPoint("toggled polygon");

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
    hits  = new ArrayList<>();
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
    panel.clearSelection();

    // update points
    toggleHits(panel, hits);
  }

  /**
   * Displays the data.
   *
   * @param panel 	the associated panel
   * @param sheet 	the data to display
   */
  protected void showData(SequencePlotterPanel panel, SpreadSheet sheet) {
    SpreadSheetDialog dialog;

    if (sheet.getRowCount() > 0) {
      if (panel.getParentDialog() != null)
	dialog = new SpreadSheetDialog(panel.getParentDialog(), ModalityType.MODELESS);
      else
	dialog = new SpreadSheetDialog(panel.getParentFrame(), false);
      dialog.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
      dialog.setTitle("Data");
      dialog.setDiscardCaption("Remove");
      dialog.setDiscardVisible(true);
      dialog.setDiscardEnabled(true);
      dialog.setSize(GUIHelper.getDefaultDialogDimension());
      dialog.setLocationRelativeTo(panel);
      dialog.setSpreadSheet(sheet);
      dialog.getTable().setOptimalColumnWidthBounded(100);
      dialog.addAfterHideAction(() -> {
	if (dialog.getOption() == SpreadSheetDialog.DISCARD_OPTION)
	  remove(sheet);
      });
      dialog.setVisible(true);
    }
  }

  /**
   * Displays the points that were flagged as outliers.
   *
   * @param panel	the associated panel
   */
  protected void showOutliers(SequencePlotterPanel panel) {
    int				i;
    List<XYSequencePoint> 	hits;
    XYSequenceContainer		cont;
    XYSequence			seq;
    SpreadSheet			sheet;

    // iterate data
    hits  = new ArrayList<>();
    for (i = 0; i < panel.getSequenceManager().count(); i++) {
      cont = (XYSequenceContainer) panel.getSequenceManager().get(i);
      seq  = cont.getData();
      for (XYSequencePoint point: seq.toList()) {
	if (point.getMetaData().containsKey(RemoveOutliers.KEY_OUTLIER)
	      && point.getMetaData().get(RemoveOutliers.KEY_OUTLIER).equals(true)) {
	  hits.add(point);
	}
      }
    }

    // display data
    if (hits.isEmpty())
      sheet = new DefaultSpreadSheet();
    else
      sheet = ((XYSequence) hits.get(0).getParent()).toSpreadSheet(hits);

    showData(panel, sheet);
  }

  /**
   * Resets the outlier status of all points.
   */
  protected void clearOutliers(SequencePlotterPanel panel) {
    int			i;
    XYSequenceContainer	cont;
    XYSequence		seq;

    for (i = 0; i < panel.getSequenceManager().count(); i++) {
      cont = (XYSequenceContainer) panel.getSequenceManager().get(i);
      seq  = cont.getData();
      for (XYSequencePoint point: seq.toList())
	point.getMetaData().remove(RemoveOutliers.KEY_OUTLIER);
    }

    panel.clearSelection();
    panel.update();
  }

  /**
   * Removes the data points from the current dataset.
   *
   * @param data	the data points of the instances to remove
   */
  protected void remove(SpreadSheet data) {
    if (m_RemoveDataListener != null)
      m_RemoveDataListener.removeData(data);
  }

  /**
   * Gets triggered if the user clicks on the canvas.
   *
   * @param panel	the associated panel
   * @param e		the mouse event
   */
  @Override
  public void mouseClickOccurred(SequencePlotterPanel panel, MouseEvent e) {
    List<XYSequencePoint>	located;

    if (MouseUtils.isLeftClick(e)) {
      if (KeyUtils.isOnlyCtrlDown(e.getModifiersEx())) {
	e.consume();
	if (m_HitDetector.getOwner() != panel.getDataPaintlet())
	  m_HitDetector.setOwner(panel.getDataPaintlet());
	located = m_HitDetector.locate(e);
	if (located != null) {
	  panel.addUndoPoint("toggled individual");
	  toggleHits(panel, located);
	}
      }
      else if (KeyUtils.isShiftDown(e.getModifiersEx()) && KeyUtils.isCtrlDown(e.getModifiersEx())) {
	e.consume();
	togglePolygonPoints(panel);
      }
    }
    else if (MouseUtils.isRightClick(e)) {
      if (KeyUtils.isOnlyShiftDown(e.getModifiersEx())) {
	e.consume();
	showOutliers(panel);
      }
      else if (KeyUtils.isShiftDown(e.getModifiersEx()) && KeyUtils.isCtrlDown(e.getModifiersEx())) {
	e.consume();
	clearOutliers(panel);
      }
    }
  }
}
