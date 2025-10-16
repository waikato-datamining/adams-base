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
 * ClassRangeBasedClassifierErrors.java
 * Copyright (C) 2013-2025 University of Waikato, Hamilton, New Zealand
 */

package weka.gui.visualize.plugins;

import adams.core.logging.LoggingHelper;
import adams.gui.core.GUIHelper;
import weka.core.Instances;
import weka.gui.visualize.Plot2D;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.VisualizePanel;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Displays the classifier errors using Weka panels, but with a sizes adjusted
 * to the class range. Only works with numeric class attributes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassRangeBasedClassifierErrors
  implements ErrorVisualizePlugin {

  /** the error size of the reference error (mid-class range). */
  public final static int REFERENCE_SIZE = 20;
  
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
    // we can only handle numeric class attributes
    if (!predInst.classAttribute().isNumeric())
      return null;
    JMenuItem result = new JMenuItem("Classifier errors (class range)");
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	// setup visualize panel
	VisualizePanel vp = new VisualizePanel();
	vp.setName("Absolute classifier errors for " + predInst.relationName());
	PlotData2D tempd = new PlotData2D(predInst);
	ArrayList plotSize = new ArrayList();
	ArrayList plotShape = new ArrayList();
	// determine class range
	double minClass = Double.MAX_VALUE;
	double maxClass = Double.MIN_VALUE;
	for (int i = 0; i < predInst.numInstances(); i++) {
	  double actual = predInst.instance(i).value(predInst.classIndex());
	  if (minClass > actual)
	    minClass = actual;
	  if (maxClass < actual)
	    maxClass = actual;
	}
	double refClass = (maxClass - minClass) / 2 + minClass;
	// generate error plot
	for (int i = 0; i < predInst.numInstances(); i++) {
	  double actual = predInst.instance(i).value(predInst.classIndex());
	  double predicted = predInst.instance(i).value(predInst.classIndex() - 1);
	  if (weka.core.Utils.isMissingValue(actual) || weka.core.Utils.isMissingValue(predicted)) {
	    // missing shape if actual class not present or prediction is missing
	    plotShape.add(Plot2D.MISSING_SHAPE);
	    plotSize.add(1);
	  }
	  else {
	    int size = (int) (Math.abs(actual - predicted) / refClass * REFERENCE_SIZE);
	    plotShape.add(Plot2D.CONST_AUTOMATIC_SHAPE);
	    plotSize.add(size);
	  }
	}
	try {
	  tempd.setShapeSize(plotSize);
	  tempd.setShapeType(plotShape);
	  tempd.setPlotName("Class-range based classifier errors for " + predInst.relationName());
	  tempd.addInstanceNumberAttribute();

	  vp.addPlot(tempd);
	  vp.setColourIndex(predInst.classIndex()+1);
	}
	catch (Exception ex) {
	  LoggingHelper.global().log(Level.SEVERE, "Failed to add plot!", e);
	  return;
	}
	// pre-select class and predicted class
	try {
	  vp.setXIndex(vp.getInstances().classIndex());  // class
	  vp.setYIndex(vp.getInstances().classIndex() - 1);  // predicted class
	}
	catch (Exception ex) {
	  // ignored
	}

	// create and display frame
	final JFrame jf = new JFrame("Class-range based classifier errors for " + predInst.relationName());
	jf.setSize(GUIHelper.getDefaultDialogDimension());
	jf.getContentPane().setLayout(new BorderLayout());
	jf.getContentPane().add(vp, BorderLayout.CENTER);
	jf.addWindowListener(new WindowAdapter() {
	  @Override
	  public void windowClosing(WindowEvent e) {
	    jf.dispose();
	  }
	});
	jf.setVisible(true);
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
    return "3.7.9";
  }
}
