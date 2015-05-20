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
 * LatestRecords.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;
import weka.core.WekaException;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;

/**
 <!-- globalinfo-start -->
 * Retains the latest database records.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre> -att-name
 *  The name of the attribute that holds the numeric DB ID.
 *  (default: db_id)</pre>
 * 
 * <pre> -amount
 *  The amount of latest records to keep.
 *  (0,1]: percentage, (1,+inf): absolute number
 *  (default: 0.5)</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LatestRecords
  extends SimpleBatchFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = -6195745510550220758L;

  /** the default attribute name. */
  public final static String DEFAULT_ATTNAME = "db_id";
  
  /** the default number of records to keep. */
  public final static double DEFAULT_AMOUNT = 0.5;
  
  /** the name of the attribute that holds the numeric database ID. */
  protected String m_AttributeName = DEFAULT_ATTNAME;
  
  /** the amount to keep (less than 1: percentage, otherwise absolute number). */
  protected double m_Amount = DEFAULT_AMOUNT;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Retains the latest database records.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector 	result;

    result = new Vector();

    result.addElement(new Option(
	"\tThe name of the attribute that holds the numeric DB ID.\n"
	    + "\t(default: " + DEFAULT_ATTNAME + ")",
	    "att-name", 1, "-att-name"));

    result.addElement(new Option(
	"\tThe amount of latest records to keep.\n"
	    + "\t(0,1]: percentage, (1,+inf): absolute number\n"
	    + "\t(default: " + DEFAULT_AMOUNT + ")",
	    "amount", 1, "-amount"));

    return result.elements();
  }

  /**
   * Parses a given list of options. <br><br>
   *
   <!-- options-start -->
   * Valid options are: <br><br>
   * 
   * <pre> -att-name
   *  The name of the attribute that holds the numeric DB ID.
   *  (default: db_id)</pre>
   * 
   * <pre> -amount
   *  The amount of latest records to keep.
   *  (0,1]: percentage, (1,+inf): absolute number
   *  (default: 0.5)</pre>
   * 
   <!-- options-end -->
   *
   * @param options the list of options as an array of string.s
   * @throws Exception if an option is not supported.
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;
    
    tmpStr = Utils.getOption("att-name", options);
    if (tmpStr.length() > 0)
      setAttributeName(tmpStr);
    else
      setAttributeName(DEFAULT_ATTNAME);
    
    tmpStr = Utils.getOption("amount", options);
    if (tmpStr.length() > 0)
      setAmount(Double.parseDouble(tmpStr));
    else
      setAmount(DEFAULT_AMOUNT);

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions.
   */
  public String[] getOptions() {
    ArrayList<String>	result;

    result = new ArrayList<String>(Arrays.asList(super.getOptions()));

    result.add("-att-name");
    result.add(getAttributeName());

    result.add("-amount");
    result.add("" + getAmount());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the name of the attribute containing the numeric database ID.
   *
   * @param value 	the name of the attribute
   */
  public void setAttributeName(String value) {
    if ((value != null) && (value.length() > 0))
      m_AttributeName = value;
    else
      System.err.println("Attribute name cannot be null or empty!");
  }

  /**
   * Returns the name of the attribute containing the numeric database ID.
   *
   * @return 		the name of the attribute
   */
  public String getAttributeName() {
    return m_AttributeName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String attributeNameTipText() {
    return "The name of the attribute containing the numeric database ID.";
  }

  /**
   * Sets the amount of records to keep (0,1]=percentage, (1,+inf)=absolute 
   * number.
   *
   * @param value 	the amount
   */
  public void setAmount(double value) {
    if (value > 0)
      m_Amount = value;
    else
      System.err.println("Amount must be >0, provided: " + value);
  }

  /**
   * Returns the amount of records to keep.
   *
   * @return 		the amount
   */
  public double getAmount() {
    return m_Amount;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String amountTipText() {
    return "The amount of records to keep: (0,1]=percentage; (1,+inf)=absolute number.";
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
    result.enableAll();
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.MISSING_VALUES);
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
    Attribute	att;
    
    att = inputFormat.attribute(m_AttributeName);
    if (att == null)
      throw new WekaException("Attribute '" + m_AttributeName + "' not present!");
    if (!att.isNumeric())
      throw new UnsupportedAttributeTypeException("Database ID attribute ('" + m_AttributeName + "') must be numeric!");
    return new Instances(inputFormat, 0);
  }

  /**
   * Processes the given data (may change the provided dataset) and returns
   * the modified version. This method is called in batchFinished().
   *
   * @param instances   the data to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   */
  protected Instances process(Instances instances) throws Exception {
    Instances	result;
    double[]	ids;
    int		i;
    int		amount;
    int		index;
    double	min;

    // amount of records to keep
    if (m_Amount <= 1)
      amount = (int) Math.round(instances.numInstances() * m_Amount);
    else
      amount = (int) Math.round(m_Amount);
    if (amount < 0)
      amount = 0;
    if (amount > instances.numInstances())
      amount = instances.numInstances();

    // do we keep all?
    if (amount == instances.numInstances()) {
      result = new Instances(instances);
      return result;
    }
    
    // get IDs
    ids    = new double[instances.numInstances()];
    index  = instances.attribute(m_AttributeName).index();
    for (i = 0; i < instances.numInstances(); i++)
      ids[i] = instances.instance(i).value(index);
    Arrays.sort(ids);
    min = ids[ids.length - amount];
    ids = null;
    
    // add instances
    result = new Instances(instances, amount);
    for (i = 0; i < instances.numInstances(); i++) {
      if (instances.instance(i).value(index) >= min)
	result.add((Instance) instances.instance(i).copy());
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
   * Main method for running this filter.
   *
   * @param args should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new LatestRecords(), args);
  }
}
