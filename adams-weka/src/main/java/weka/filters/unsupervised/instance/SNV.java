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
 * SNV.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.filters.SimpleStreamFilter;
import weka.filters.UnsupervisedFilter;

/**
 * Standard Normal Variate (SNV) filter for WEKA / ADAMS.
 * Centers and scales each instance (row) individually by subtracting
 * the row mean and dividing by the row standard deviation.
 *
 * @author dale (dale at waikato dot ac dot nz)
 */
public class SNV
  extends SimpleStreamFilter
  implements UnsupervisedFilter {

  /** For serialization */
  private static final long serialVersionUID = -4582910482918301824L;

  /**
   * Returns a string describing this filter suitable for
   * displaying in the Explorer/Experimenter GUI.
   *
   * @return description of the filter
   */
  @Override
  public String globalInfo() {
    return "Applies Standard Normal Variate (SNV) transformation row-wise "
	     + "(subtracts the row mean and divides by the row standard deviation).";
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return the capabilities of this object
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();
    result.disableAll();

    // Allowed attributes
    result.enableAllAttributeDependencies();
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.DATE_ATTRIBUTES);
    result.enable(Capability.MISSING_VALUES);

    // Allowed class types
    result.enableAllClasses();
    result.enableAllClassDependencies();
    result.enable(Capability.NO_CLASS);

    return result;
  }

  /**
   * Determines the output format based on the input format and returns it.
   *
   * @param inputFormat the input format to base the output format on
   * @return the output format
   * @throws Exception if output format determination fails
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    return new Instances(inputFormat, 0);
  }

  /**
   * Processes the given instance (subtracts row mean, divides by row standard deviation).
   *
   * @param instance the instance to process
   * @return the modified instance
   * @throws Exception if processing fails
   */
  @Override
  protected Instance process(Instance instance) throws Exception {
    Instance result = (Instance) instance.copy();
    int count = 0;
    double sum = 0.0;

    // Pass 1: Compute row mean across numeric non-missing attributes
    for (int i = 0; i < result.numAttributes(); i++) {
      if (i == result.classIndex()) {
	continue;
      }
      if (result.attribute(i).isNumeric() && !result.isMissing(i)) {
	sum += result.value(i);
	count++;
      }
    }

    if (count <= 1) {
      return result; // Cannot calculate standard deviation with <= 1 attribute
    }

    double mean = sum / count;

    // Pass 2: Compute sample variance and standard deviation
    double sumSqDiff = 0.0;
    for (int i = 0; i < result.numAttributes(); i++) {
      if (i == result.classIndex()) {
	continue;
      }
      if (result.attribute(i).isNumeric() && !result.isMissing(i)) {
	double diff = result.value(i) - mean;
	sumSqDiff += diff * diff;
      }
    }

    double stdDev = Math.sqrt(sumSqDiff / (count - 1));

    // Pass 3: Center and scale (x - mean) / stdDev
    if (stdDev != 0.0) {
      for (int i = 0; i < result.numAttributes(); i++) {
	if (i == result.classIndex()) {
	  continue;
	}
	if (result.attribute(i).isNumeric() && !result.isMissing(i)) {
	  result.setValue(i, (result.value(i) - mean) / stdDev);
	}
      }
    }

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for running and testing this filter from the command line.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    runFilter(new SNV(), args);
  }
}