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

/**
 * PAA.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import adams.data.statistics.StatCalc;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.SimpleStreamFilter;
import weka.filters.UnsupervisedFilter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 *
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre> -nth &lt;int&gt;
 *  Only every n-th point will be output (&gt;0).
 *  (default: 1)</pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PAA
  extends SimpleStreamFilter
  implements UnsupervisedFilter {

  /** suid.  */
  private static final long serialVersionUID = -3516731512819723355L;

  /** number of windows. */
  protected int m_windows = 80;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Piecewise Aggregate Approximation.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector 	result;

    result = new Vector();

    result.addElement(new Option(
	"\tNumber of windows for PAA (>1).\n"
	+ "\t(default: 80)",
	"windows", 80, "-windows <int>"));


    return result.elements();
  }

  /**
   * Parses a list of options for this object.
   * Also resets the state of the filter (this reset doesn't affect the
   * options).
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   * @see    		#reset()
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    reset();


    tmpStr = Utils.getOption("windows", options);
    if (tmpStr.length() > 0)
      setWindows(Integer.parseInt(tmpStr));
    else
      setWindows(80);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>();

    result.add("-windows");
    result.add("" + getWindows());

    return result.toArray(new String[result.size()]);
  }


  /**
   * Sets the nth point setting.
   *
   * @param value 	the nth point
   */
  public void setWindows(int value) {
    if (value > 0) {
      m_windows = value;
      reset();
    }
    else {
      System.err.println(
	  "'n' must be larger than 0 (provided: " + value + ")!");
    }
  }



  /**
   * Returns the nth point setting.
   *
   * @return 		the order
   */
  public int getWindows() {
    return m_windows;
  }


  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String windowsPointTipText() {
    return "number of windows for PAA (>0).";
  }

  /**
   * Returns the Capabilities of this filter. Derived filters have to
   * override this method to enable capabilities.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  public Capabilities getCapabilities() {
    Capabilities 	result;

    result = new Capabilities(this);

    // attributes
    result.enableAllAttributes();
    result.enable(Capability.MISSING_VALUES);

    // classes
    result.enableAllClasses();
    result.enable(Capability.NO_CLASS);
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
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    Instances			result;
    ArrayList<Attribute>	atts;
    int				i;
    int				count;
    boolean			hasClass;

    hasClass = (inputFormat.classIndex() > -1);

    FastVector values = new FastVector();

    // create new attributes
    atts  = new ArrayList<Attribute>();
    count = 0;
   /* for (i = 0; i < inputFormat.numAttributes(); i++) {
      if (i == inputFormat.classIndex())
	continue;
      count++;
      if (count % m_NthPoint == 0)
	atts.add((Attribute) inputFormat.attribute(i).copy());
    }*/
    for (i = 0; i < getWindows(); i++) {
      atts.add(new Attribute("PAA_" + (i+1)));
    }


    // add class attribute (if present)
    if (hasClass)
      atts.add((Attribute) inputFormat.classAttribute().copy());

    // create new dataset
    result = new Instances(inputFormat.relationName(), atts, 0);
    if (hasClass)
      result.setClassIndex(result.numAttributes() - 1);

    return result;
  }

  /**
   * Return an array where the 1st value is the mean, and the 2nd the standard deviation of
   * the attribute values.
   *
   * @param in		instance
   * @return		mean, stdev
   */
  protected double[] getMeanStdev(Instance in){
    double[] ret = new double[2];
    StatCalc st=new StatCalc();
    for (int i=0;i<in.numAttributes();i++){
      if (i == in.classIndex()){
	continue;
      }
      st.enter(in.value(i));
    }
    ret[0]=st.getMean();
    ret[1]=st.getStandardDeviation();
    return(ret);
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
    boolean	hasClass;
    int		count;

    if (getDebug()){
      double[] stats=getMeanStdev(instance);
      System.err.println("Instance mean="+stats[0]+", sd="+stats[1]);
    }
    hasClass = (instance.classIndex() > -1);

    //valuesOld = instance.toDoubleArray();
    double[] ivals=new double[instance.numAttributes()-1];
    count = 0;
    for (int i=0;i<instance.numAttributes();i++){
      if (i == instance.classIndex()) {
	continue;
      }
      ivals[count]=instance.value(i);
      count++;
    }


    double[] paavalues=adams.data.utils.SAXUtils.PAA(ivals, getWindows());

    values    = new double[getOutputFormat().numAttributes()];
    for (int i=0;i<paavalues.length;i++){
      values[i]=paavalues[i];
    }
    // add class value
    if (hasClass)
      values[values.length - 1] = instance.classValue();

    // create instance
    result = new DenseInstance(instance.weight(), values);
    result.setDataset(getOutputFormat());

    copyValues(result, false, instance.dataset(), getOutputFormat());

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
   * @param args 	should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new PAA(), args);
  }
}
