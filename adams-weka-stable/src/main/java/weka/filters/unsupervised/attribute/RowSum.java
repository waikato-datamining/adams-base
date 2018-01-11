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
 * RowSum.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.filters.SimpleStreamFilter;
import weka.filters.UnsupervisedFilter;

import java.util.ArrayList;

/**
 <!-- globalinfo-start -->
 * Sums up all numeric values in a row and replaces them with it.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
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
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4521 $
 */
public class RowSum
  extends SimpleStreamFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = -8798054963031276516L;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Sums up all numeric values in a row and replaces them with it.";
  }

  /**
   * Returns the Capabilities of this filter. Derived filters have to
   * override this method to enable capabilities.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities 	result;

    result = new Capabilities(this);

    // attributes
    result.disableAllAttributes();
    result.enable(Capability.NUMERIC_ATTRIBUTES);

    // classes
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.NOMINAL_CLASS);
    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.DATE_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Determines the output format based on the input format and returns
   * this. In case the output format cannot be returned immediately, i.e.,
   * hasImmediateOutputFormat() returns false, then this method will called
   * from batchFinished() after the call of preprocess(Instances), in which,
   * e.g., statistics for the actual processing step can be gathered.
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    Instances			result;
    ArrayList<Attribute> 	atts;

    atts = new ArrayList<>();
    atts.add(new Attribute("Sum"));
    if (inputFormat.classIndex() > -1)
      atts.add((Attribute) inputFormat.classAttribute().copy());

    result = new Instances(inputFormat.relationName(), atts, 0);
    if (inputFormat.classIndex() > -1)
      result.setClassIndex(result.numAttributes() - 1);

    return result;
  }

  /**
   * processes the given instance (may change the provided instance) and
   * returns the modified version.
   *
   * @param instance    the instance to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   */
  @Override
  protected Instance process(Instance instance) throws Exception {
    Instance	result;
    double	sum;
    double[]	values;
    int		i;

    sum = 0;
    for (i = 0; i < instance.numAttributes(); i++) {
      if (i == instance.classIndex())
        continue;
      if (instance.attribute(i).isNumeric())
        sum += instance.value(i);
    }

    values = new double[outputFormatPeek().numAttributes()];
    values[0] = sum;
    if (values.length > 1)
      values[1] = instance.classValue();

    result = new DenseInstance(instance.weight(), values);
    result.setDataset(getOutputFormat());

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 4521 $");
  }

  /**
   * Main method for testing this class.
   *
   * @param args 	should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new RowSum(), args);
  }
}
