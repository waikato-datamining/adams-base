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
 * RowNorm.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.filters.SimpleStreamFilter;
import weka.filters.UnsupervisedFilter;
import adams.data.statistics.StatCalc;

/**
 <!-- globalinfo-start -->
 * Row wise normalization.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RowNorm
  extends SimpleStreamFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = 6812351429964183179L;

  /**
   * Returns a string describing this filter.
   *
   * @return      a description of the filter suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Row wise normalization.";
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  public Capabilities getCapabilities() {
    Capabilities 	result;

    result = new Capabilities(this);

    result.enable(Capability.NUMERIC_ATTRIBUTES);

    result.enableAllClasses();
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Determines the output format based on the input format and returns
   * this.
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   */
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    return new Instances(inputFormat, 0);
  }

  /**
   * processes the given instance (may change the provided instance) and
   * returns the modified version.
   *
   * @param instance    the instance to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   */
  protected Instance process(Instance instance) throws Exception {
    Instance	result;
    double[]	values;
    StatCalc 	sc;
    int		i;
    double 	mn;
    double 	sd;

    values = instance.toDoubleArray();

    sc = new StatCalc();
    for (i = 0; i < values.length; i++) {
      if (i == instance.classIndex())
	continue;
      sc.enter(values[i]);
    }

    mn = sc.getMean();
    sd = sc.getStandardDeviation();

    result = (Instance) instance.copy();
    for (i = 0; i < values.length; i++) {
      if (i == instance.classIndex())
	continue;
      result.setValue(i, ((values[i] - mn)/sd));
    }

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for testing this class.
   *
   * @param args should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new RowNorm(), args);
  }
}
