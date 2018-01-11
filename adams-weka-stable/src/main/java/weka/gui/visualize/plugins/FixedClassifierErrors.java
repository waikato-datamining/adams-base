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
 * FixedClassifierErrors.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package weka.gui.visualize.plugins;

import adams.gui.core.GUIHelper;
import weka.core.FastVector;
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

/**
 * Displays the classifier errors using Weka panels, but with a fixed size of
 * the error plots.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedClassifierErrors
  implements ErrorVisualizePlugin {

  /** fixed plot size. */
  public final static int PLOT_SIZE = 4;

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
  public JMenuItem getVisualizeMenuItem(Instances predInst) {
    final Instances predInstF = predInst;
    JMenuItem result = new JMenuItem("Classifier errors (fixed)");
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	// setup visualize panel
	VisualizePanel vp = new VisualizePanel();
	vp.setName("Absolute classifier errors for " + predInstF.relationName());
	PlotData2D tempd = new PlotData2D(predInstF);
	FastVector plotSize = new FastVector();
	FastVector plotShape = new FastVector();
	for (int i = 0; i < predInstF.numInstances(); i++) {
	  double actual = predInstF.instance(i).value(predInstF.classIndex());
	  double predicted = predInstF.instance(i).value(predInstF.classIndex() - 1);
	  if (predInstF.classAttribute().isNominal()) {
	    if (weka.core.Utils.isMissingValue(actual) || weka.core.Utils.isMissingValue(predicted)) {
	      plotShape.addElement(new Integer(Plot2D.MISSING_SHAPE));
	    }
	    else if (actual != predicted) {
	      // set to default error point shape
	      plotShape.addElement(new Integer(Plot2D.ERROR_SHAPE));
	    }
	    else {
	      // otherwise set to constant (automatically assigned) point shape
	      plotShape.addElement(new Integer(Plot2D.CONST_AUTOMATIC_SHAPE));
	    }
	    plotSize.addElement(new Integer(PLOT_SIZE));
	  }
	  else {
	    if (weka.core.Utils.isMissingValue(actual) || weka.core.Utils.isMissingValue(predicted)) {
	      // missing shape if actual class not present or prediction is missing
	      plotShape.addElement(new Integer(Plot2D.MISSING_SHAPE));
	    }
	    else {
	      plotShape.addElement(new Integer(Plot2D.CONST_AUTOMATIC_SHAPE));
	    }
	    plotSize.addElement(PLOT_SIZE);
	  }
	}
	try {
	  tempd.setShapeSize(plotSize);
	  tempd.setShapeType(plotShape);
	  tempd.setPlotName("Absolute classifier errors for " + predInstF.relationName());
	  tempd.addInstanceNumberAttribute();

	  vp.addPlot(tempd);
	  vp.setColourIndex(predInstF.classIndex()+1);
	}
	catch (Exception ex) {
	  ex.printStackTrace();
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
	final JFrame jf = new JFrame("Classifier errors for " + predInstF.relationName());
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
    return "3.6.0";
  }
}
