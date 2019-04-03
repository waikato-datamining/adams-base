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
 * YGradientGLSW.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.filters.supervised.attribute;

import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WekaOptionUtils;
import weka.core.matrix.MatrixHelper;
import weka.filters.SimpleBatchFilter;
import weka.filters.SupervisedFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Applies the Generalized Least Squares Weighting (GLSW) algorithm to the data.<br>
 * <br>
 * For more information see:<br>
 * http://wiki.eigenvector.com/index.php?title=Advanced_Preprocessing:_Multivariate_Filtering#Y-Gradient_GLSW
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -alpha &lt;value&gt;
 *  The alpha parameter. Defines how strongly GLSW downweights interferences. Larger values (&gt; 0.001) decreases the filtering effect. Smaller values (&lt; 0.001) increase the filtering effect.
 *  (default: 0.001)</pre>
 *
 * <pre> -output-debug-info
 *  If set, filter is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -do-not-check-capabilities
 *  If set, filter capabilities are not checked before filter is built
 *  (use with caution).</pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class YGradientGLSW
  extends SimpleBatchFilter
  implements SupervisedFilter {

  /** for serialization */
  static final long serialVersionUID = -3335106965521265631L;

  /** Alpha parameter. Defines how strongly GLSW downweights interferences */
  protected double m_Alpha = getDefaultAlpha();

  /** the GLSW algorithm. */
  protected com.github.waikatodatamining.matrix.algorithm.glsw.YGradientGLSW m_Algorithm;

  /**
   * Returns a string describing this classifier.
   *
   * @return a description of the classifier suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Applies the Generalized Least Squares Weighting (GLSW) algorithm to the data.\n\n"
      + "For more information see:\n"
      + "http://wiki.eigenvector.com/index.php?title=Advanced_Preprocessing:_Multivariate_Filtering#Y-Gradient_GLSW";
  }

  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<>();

    WekaOptionUtils.addOption(result, alphaTipText(), "" + getDefaultAlpha(), "alpha");
    WekaOptionUtils.add(result, super.listOptions());

    return result.elements();
  }

  /**
   * returns the options of the current setup
   *
   * @return the current options
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();

    WekaOptionUtils.add(result, "alpha", getAlpha());
    Collections.addAll(result, super.getOptions());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object.
   *
   * @param options the options to use
   * @throws Exception if the option setting fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setAlpha(WekaOptionUtils.parse(options, "alpha", getDefaultAlpha()));
    super.setOptions(options);
    Utils.checkForRemainingOptions(options);
  }

  /**
   * Returns the default algorithm.
   *
   * @return		the default
   */
  protected double getDefaultAlpha() {
    return 1e-3;
  }

  /**
   * Set the alpha parameter. Defines how strongly GLSW downweights
   * interferences. Larger values (> 0.001) decreases the filtering effect.
   * Smaller values (< 0.001) increase the filtering effect.
   *
   * @param value Alpha parameter
   */
  public void setAlpha(double value) {
    if (value <= 0) {
      System.err.println("Alpha must be > 0 but was " + value + ".");
    }
    else {
      m_Alpha = value;
      reset();
    }
  }

  /**
   * Returns the alpha parameter.
   *
   * @return		alpha
   */
  public double getAlpha() {
    return m_Alpha;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String alphaTipText() {
    return "The alpha parameter. Defines how strongly GLSW downweights "
      + "interferences. Larger values (> 0.001) decreases the filtering effect. "
      + "Smaller values (< 0.001) increase the filtering effect.";
  }

  /**
   * Determines the output format based on the input format and returns this. In
   * case the output format cannot be returned immediately, i.e.,
   * immediateOutputFormat() returns false, then this method will be called from
   * batchFinished().
   *
   * @param inputFormat the input format to base the output format on
   * @return the output format
   * @throws Exception in case the determination goes wrong
   * @see #hasImmediateOutputFormat()
   * @see #batchFinished()
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    Instances						result;
    ArrayList<Attribute>				atts;
    int							i;

    atts = new ArrayList<>();
    for (i = 0; i < inputFormat.numAttributes(); i++) {
      if (i == inputFormat.classIndex())
        continue;
      atts.add((Attribute) inputFormat.attribute(i).copy());
    }
    atts.add((Attribute) inputFormat.classAttribute().copy());
    result = new Instances(inputFormat.relationName(), atts, 0);
    result.setClassIndex(result.numAttributes() - 1);

    return result;
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;

    result = new Capabilities(this);
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.NUMERIC_CLASS);
    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Processes the given data (may change the provided dataset) and returns the
   * modified version. This method is called in batchFinished().
   *
   * @param instances the data to process
   * @return the modified data
   * @throws Exception in case the processing goes wrong
   * @see #batchFinished()
   */
  @Override
  protected Instances process(Instances instances) throws Exception {
    com.github.waikatodatamining.matrix.core.Matrix 	X;
    com.github.waikatodatamining.matrix.core.Matrix 	y;
    com.github.waikatodatamining.matrix.core.Matrix 	X_new;
    String						msg;

    X = adams.data.instancesanalysis.pls.MatrixHelper.wekaToMatrixAlgo(MatrixHelper.getX(instances));
    y = adams.data.instancesanalysis.pls.MatrixHelper.wekaToMatrixAlgo(MatrixHelper.getY(instances));

    if (!isFirstBatchDone()) {
      m_Algorithm = new com.github.waikatodatamining.matrix.algorithm.glsw.YGradientGLSW();
      m_Algorithm.setAlpha(m_Alpha);
      msg = m_Algorithm.initialize(X, y);
      if (msg != null)
	throw new Exception(msg);
    }

    X_new = m_Algorithm.transform(X);

    return adams.data.instancesanalysis.pls.MatrixHelper.toInstances(getOutputFormat(), adams.data.instancesanalysis.pls.MatrixHelper.matrixAlgoToWeka(X_new), adams.data.instancesanalysis.pls.MatrixHelper.matrixAlgoToWeka(y));
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 10364 $");
  }

  /**
   * runs the filter with the given arguments.
   *
   * @param args the commandline arguments
   */
  public static void main(String[] args) {
    runFilter(new YGradientGLSW(), args);
  }
}
