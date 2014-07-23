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
 * FixedClassifierErrorsPlot.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package weka.gui.visualize.plugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JMenuItem;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.flow.sink.sequenceplotter.SequencePlotContainer;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.CrossPaintlet;
import adams.gui.visualization.sequence.StraightLineOverlayPaintlet;

/**
 * Displays the classifier errors using an ADAMS plot with fixed size crosses.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7326 $
 */
public class FixedClassifierErrorsPlot
  implements ErrorVisualizePlugin {

  /**
   * Get a JMenu or JMenuItem which contain action listeners
   * that perform the visualization of the classifier errors.
   * <p/>
   * The actual class is the attribute declared as class attribute, the
   * predicted class values is found in the attribute prior to the class
   * attribute's position. In other words, if the <code>classIndex()</code>
   * method returns 10, then the attribute position for the predicted class
   * values is 9.
   * <p/>
   * Exceptions thrown because of changes in Weka since compilation need to
   * be caught by the implementer.
   *
   * @see NoClassDefFoundError
   * @see IncompatibleClassChangeError
   *
   * @param predInst 	the instances with the actual and predicted class values
   * @return menuitem 	for opening visualization(s), or null
   *         		to indicate no visualization is applicable for the input
   */
  public JMenuItem getVisualizeMenuItem(final Instances predInst) {
    JMenuItem result = new JMenuItem("Classifier errors plot (fixed)");
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	// setup plot
	SequencePlotterPanel plot = new SequencePlotterPanel(predInst.relationName());
	CrossPaintlet paintlet = new CrossPaintlet();
	plot.setPaintlet(paintlet);
	StraightLineOverlayPaintlet overlay = new StraightLineOverlayPaintlet();
	overlay.setColor(Color.RED.darker());
	plot.setOverlayPaintlet(overlay);
	FancyTickGenerator tick = new FancyTickGenerator();
	tick.setNumTicks(10);
	plot.getPlot().getAxis(Axis.LEFT).setTickGenerator(tick.shallowCopy());
	plot.getPlot().getAxis(Axis.LEFT).setNumberFormat("0.0");
	plot.getPlot().getAxis(Axis.LEFT).setNthValueToShow(2);
	plot.getPlot().getAxis(Axis.LEFT).setAxisName("Predicted");;
	plot.getPlot().getAxis(Axis.BOTTOM).setTickGenerator(tick.shallowCopy());
	plot.getPlot().getAxis(Axis.BOTTOM).setNumberFormat("0.0");
	plot.getPlot().getAxis(Axis.BOTTOM).setNthValueToShow(2);
	plot.getPlot().getAxis(Axis.BOTTOM).setAxisName("Actual");;
	// create plot data
	SequencePlotSequence seq = new SequencePlotSequence();
	seq.setComparison(Comparison.X_AND_Y);
	seq.setID("Act vs Pred");
	DateFormat format = DateUtils.getTimestampFormatter();
	for (int i = 0; i < predInst.numInstances(); i++) {
	  Instance inst = predInst.instance(i);
	  double actual = inst.value(predInst.classIndex());
	  double predicted = inst.value(predInst.classIndex() - 1);
	  SequencePlotPoint point = new SequencePlotPoint("Act vs Pred", actual, predicted);
	  if (predInst.numAttributes() > 2) {
	    HashMap<String,Object> meta = new HashMap<String,Object>();
	    for (int n = 0; n < predInst.numAttributes(); n++) {
	      if ((n == predInst.classIndex()) || (n == predInst.classIndex() - 1))
		continue;
	      if (inst.isMissing(n))
		meta.put(predInst.attribute(n).name(), "?");
	      else if (predInst.attribute(n).type() == Attribute.NUMERIC)
		meta.put(predInst.attribute(n).name(), inst.value(n));
	      else if (predInst.attribute(n).type() == Attribute.DATE)
		meta.put(predInst.attribute(n).name(), format.format(new Date((int) inst.value(n))));
	      else if (predInst.attribute(n).type() == Attribute.NOMINAL)
		meta.put(predInst.attribute(n).name(), inst.stringValue(n));
	      else if (predInst.attribute(n).type() == Attribute.STRING)
		meta.put(predInst.attribute(n).name(), inst.stringValue(n));
	    }
	    point.setMetaData(meta);
	  }
	  seq.add(point);
	}
	SequencePlotContainer cont = (SequencePlotContainer) plot.getContainerManager().newContainer(seq);
	plot.getContainerManager().add(cont);
	// display
	ApprovalDialog dialog = new ApprovalDialog((Dialog) null, ModalityType.MODELESS);
	dialog.setTitle("Absolute classifier errors");
	dialog.getContentPane().add(plot, BorderLayout.CENTER);
	dialog.setSize(800, 600);
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
    return "3.5.9";
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
    return "3.7.0";
  }
}
