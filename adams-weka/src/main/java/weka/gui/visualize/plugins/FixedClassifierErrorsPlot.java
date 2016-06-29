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
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */

package weka.gui.visualize.plugins;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Properties;
import adams.core.Range;
import adams.data.sequence.XYSequencePointComparator.Comparison;
import adams.flow.sink.sequenceplotter.SequencePlotContainer;
import adams.flow.sink.sequenceplotter.SequencePlotPoint;
import adams.flow.sink.sequenceplotter.SequencePlotSequence;
import adams.flow.sink.sequenceplotter.SequencePlotterPanel;
import adams.flow.sink.sequenceplotter.ViewDataClickAction;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.CrossHitDetector;
import adams.gui.visualization.sequence.CrossPaintlet;
import adams.gui.visualization.sequence.LinearRegressionOverlayPaintlet;
import adams.gui.visualization.sequence.MultiPaintlet;
import adams.gui.visualization.sequence.StraightLineOverlayPaintlet;
import adams.gui.visualization.sequence.TextOverlayPaintlet;
import adams.gui.visualization.sequence.XYSequencePaintlet;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;

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
   * <br><br>
   * The actual class is the attribute declared as class attribute, the
   * predicted class values is found in the attribute prior to the class
   * attribute's position. In other words, if the <code>classIndex()</code>
   * method returns 10, then the attribute position for the predicted class
   * values is 9.
   * <br><br>
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
	// prompt user
	PropertiesParameterPanel params = new PropertiesParameterPanel();
	params.addPropertyType("Meta-data", PropertyType.RANGE);
	params.setHelp("Meta-data", "The range of attribiutes to add as meta-data; " + new Range().getExample());
	params.addPropertyType("Trend", PropertyType.BOOLEAN);
	params.setHelp("Trend", "Adds a best fit line using linear regression");
	Properties props = new Properties();
	props.setProperty("Meta-data", Range.ALL);
	props.setBoolean("Trend", true);
	params.setPropertyOrder(new String[]{"Meta-data", "Trend"});
	params.setProperties(props);
	ApprovalDialog dialog = new ApprovalDialog(null, ModalityType.DOCUMENT_MODAL);
	dialog.setTitle("Plot setup");
	dialog.getContentPane().add(params, BorderLayout.CENTER);
	dialog.pack();
	dialog.setLocationRelativeTo(null);
	dialog.setVisible(true);
	if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
	  return;
	props = params.getProperties();
	Range range = new Range(props.getProperty("Meta-data"));
	range.setMax(predInst.numAttributes());
	boolean trend = props.getBoolean("Trend");
	// setup plot
	SequencePlotterPanel plot = new SequencePlotterPanel(predInst.relationName());
	CrossPaintlet paintlet = new CrossPaintlet();
	plot.setPaintlet(paintlet);
	MultiPaintlet overlays = new MultiPaintlet();
	StraightLineOverlayPaintlet overlay = new StraightLineOverlayPaintlet();
	overlay.setColor(Color.RED.darker());
	LinearRegressionOverlayPaintlet lrPaintlet = new LinearRegressionOverlayPaintlet();
	lrPaintlet.setOutputSlopeIntercept(true);
	TextOverlayPaintlet text = new TextOverlayPaintlet();
	if (trend) {
	  text.setText("Red = diagonal, Black = LR fit");
	  text.setY(30);
	}
	else {
	  text.setText("Red = diagonal");
	}
	if (trend)
	  overlays.setSubPaintlets(new XYSequencePaintlet[]{overlay, lrPaintlet, text});
	else
	  overlays.setSubPaintlets(new XYSequencePaintlet[]{overlay, text});
	plot.setOverlayPaintlet(overlays);
	FancyTickGenerator tick = new FancyTickGenerator();
	tick.setNumTicks(10);
	ViewDataClickAction action = new ViewDataClickAction();
	action.setHitDetector(new CrossHitDetector());
	plot.setMouseClickAction(action);
	plot.getPlot().getAxis(Axis.LEFT).setTickGenerator(tick.shallowCopy());
	plot.getPlot().getAxis(Axis.LEFT).setNumberFormat("0.0");
	plot.getPlot().getAxis(Axis.LEFT).setNthValueToShow(2);
	plot.getPlot().getAxis(Axis.LEFT).setAxisName("Predicted");
	plot.getPlot().getAxis(Axis.BOTTOM).setTickGenerator(tick.shallowCopy());
	plot.getPlot().getAxis(Axis.BOTTOM).setNumberFormat("0.0");
	plot.getPlot().getAxis(Axis.BOTTOM).setNthValueToShow(2);
	plot.getPlot().getAxis(Axis.BOTTOM).setAxisName("Actual");
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
	  HashMap<String,Object> meta = new HashMap<String,Object>();
	  meta.put("Error", actual - predicted);
	  if (predInst.numAttributes() > 2) {
	    for (int n = 0; n < predInst.numAttributes(); n++) {
	      if ((n == predInst.classIndex()) || (n == predInst.classIndex() - 1))
		continue;
	      if (!range.isInRange(n))
		continue;
	      String name = "Att-" + predInst.attribute(n).name();
	      int type = predInst.attribute(n).type();
	      if (inst.isMissing(n))
		meta.put(name, "?");
	      else if (type == Attribute.NUMERIC)
		meta.put(name, inst.value(n));
	      else if (type == Attribute.DATE)
		meta.put(name, format.format(new Date((int) inst.value(n))));
	      else if (type == Attribute.NOMINAL)
		meta.put(name, inst.stringValue(n));
	      else if (type == Attribute.STRING)
		meta.put(name, inst.stringValue(n));
	    }
	  }
	  point.setMetaData(meta);
	  seq.add(point);
	}
	SequencePlotContainer cont = (SequencePlotContainer) plot.getContainerManager().newContainer(seq);
	plot.getContainerManager().add(cont);
	// display
	dialog = new ApprovalDialog(null, ModalityType.MODELESS);
	dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
	dialog.setTitle("Absolute classifier errors");
	dialog.getContentPane().add(plot, BorderLayout.CENTER);
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
