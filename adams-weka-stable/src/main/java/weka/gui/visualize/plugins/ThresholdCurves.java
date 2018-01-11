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
 * ThresholdCurves.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package weka.gui.visualize.plugins;

import adams.core.Utils;
import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.gui.core.BaseDialog;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.LinePaintlet;
import adams.gui.visualization.sequence.PaintletWithFixedXYRange;
import adams.gui.visualization.sequence.XYSequenceContainer;
import adams.gui.visualization.sequence.XYSequenceContainerManager;
import adams.gui.visualization.sequence.XYSequencePanel;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;
import weka.core.Instances;

import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Displays all the threshold curves (ROC) in a single plot.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ThresholdCurves
  implements VisualizePlugin {

  /**
   * Get a JMenu or JMenuItem which contain action listeners
   * that perform the visualization, using some but not
   * necessarily all of the data.  Exceptions thrown because of
   * changes in Weka since compilation need to be caught by
   * the implementer.
   *
   * @see NoClassDefFoundError
   * @see IncompatibleClassChangeError
   *
   * @param  preds predictions
   * @param  classAtt class attribute
   * @return menuitem for opening visualization(s), or null
   *         to indicate no visualization is applicable for the input
   */
  public JMenuItem getVisualizeMenuItem(final ArrayList<Prediction> preds, final Attribute classAtt) {
    if (!classAtt.isNominal())
      return null;

    JMenuItem result = new JMenuItem("Threshold curves");
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	XYSequencePanel panel = new XYSequencePanel();
	panel.getSidePanel().setPreferredSize(new Dimension(200, 0));
	panel.getPlot().getAxis(Axis.LEFT).setTopMargin(0.0);
	panel.getPlot().getAxis(Axis.LEFT).setBottomMargin(0.0);
	panel.getPlot().getAxis(Axis.LEFT).setNumberFormat("0.00");
	panel.getPlot().getAxis(Axis.BOTTOM).setTopMargin(0.0);
	panel.getPlot().getAxis(Axis.BOTTOM).setBottomMargin(0.0);
	panel.getPlot().getAxis(Axis.BOTTOM).setNumberFormat("0.00");
	LinePaintlet basePaintlet = new LinePaintlet();
	basePaintlet.setPaintAll(true);
	PaintletWithFixedXYRange paintlet = new PaintletWithFixedXYRange();
	paintlet.setMinX(0.0);
	paintlet.setMaxX(1.0);
	paintlet.setMinY(0.0);
	paintlet.setMaxY(1.0);
	paintlet.setPaintlet(basePaintlet);
	panel.setDataPaintlet(paintlet);
	XYSequenceContainerManager manager = panel.getContainerManager();
	for (int i = 0; i < classAtt.numValues(); i++) {
	  // generate curve
	  weka.classifiers.evaluation.ThresholdCurve tc = new weka.classifiers.evaluation.ThresholdCurve();
	  Instances result = tc.getCurve(preds, i);
	  // generate sequence
	  XYSequence seq = new XYSequence();
	  seq.setID(classAtt.value(i) + " (AUC: " + Utils.doubleToString(weka.classifiers.evaluation.ThresholdCurve.getROCArea(result), 4) + ")");
	  seq.setComparison(Comparison.X_AND_Y);
	  for (int n = 0; n < result.numInstances(); n++) {
	    double x = result.instance(n).value(result.attribute(weka.classifiers.evaluation.ThresholdCurve.FP_RATE_NAME));
	    double y = result.instance(n).value(result.attribute(weka.classifiers.evaluation.ThresholdCurve.TP_RATE_NAME));
	    XYSequencePoint point = new XYSequencePoint(x, y);
	    seq.add(point);
	  }
	  XYSequenceContainer cont = manager.newContainer(seq);
	  manager.add(cont);
	}
	BaseDialog dialog = new BaseDialog();
	dialog.setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);
	dialog.setTitle("Threshold curves (" + classAtt.name() + ")");
	dialog.getContentPane().setLayout(new BorderLayout());
	dialog.getContentPane().add(panel, BorderLayout.CENTER);
	dialog.setSize(GUIHelper.getDefaultDialogDimension());
	dialog.setLocationRelativeTo(null);
	dialog.setVisible(true);
      }
    });

    return result;
  }

  /**
   * Get the minimum version of Weka, inclusive, the class
   * is designed to work with.  eg: <code>3.5.0</code>
   *
   * @return		the minimum version
   */
  public String getMinVersion() {
    return "3.7.4";
  }

  /**
   * Get the maximum version of Weka, exclusive, the class
   * is designed to work with.  eg: <code>3.6.0</code>
   *
   * @return		the maximum version
   */
  public String getMaxVersion() {
    return "3.8.0";
  }

  /**
   * Get the specific version of Weka the class is designed for.
   * eg: <code>3.5.1</code>
   *
   * @return		the version the plugin was designed for
   */
  public String getDesignVersion() {
    return "3.7.4";
  }
}
