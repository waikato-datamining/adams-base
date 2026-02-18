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
 * RemoveSampleIDs.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Removes all the sample IDs listed in the specified text file (one sample ID per line).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -att-name
 *  The name of the attribute that holds the numeric DB ID.
 *  (default: sample_id)</pre>
 *
 * <pre> -sampleid-file
 *  The text file with the sample IDs to remove (one per line).
 *  (default: .)</pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RemoveSampleIDs
  extends SimpleBatchFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = -6195745510550220758L;

  /** the default attribute name. */
  public final static String DEFAULT_ATTNAME = "sample_id";

  /** the default file with sample IDs. */
  public final static String DEFAULT_SAMPLEID_FILE = ".";

  /** the name of the attribute that holds the numeric database ID. */
  protected String m_AttributeName = DEFAULT_ATTNAME;

  /** the file with the sample IDs to remove. */
  protected PlaceholderFile m_SampleIDFile = new PlaceholderFile(DEFAULT_SAMPLEID_FILE);

  /** the sample IDs to remove. */
  protected Set<String> m_SampleIDs;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Removes all the sample IDs listed in the specified text file (one sample ID per line).";
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
      "\tThe text file with the sample IDs to remove (one per line).\n"
	+ "\t(default: " + DEFAULT_SAMPLEID_FILE + ")",
      "sampleid-file", 1, "-sampleid-file"));

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options the list of options as an array of string.s
   * @throws Exception if an option is not supported.
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption("att-name", options);
    if (!tmpStr.isEmpty())
      setAttributeName(tmpStr);
    else
      setAttributeName(DEFAULT_ATTNAME);

    tmpStr = Utils.getOption("sampleid-file", options);
    if (!tmpStr.isEmpty())
      setSampleIDFile(new PlaceholderFile(tmpStr));
    else
      setSampleIDFile(new PlaceholderFile(DEFAULT_SAMPLEID_FILE));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions.
   */
  public String[] getOptions() {
    ArrayList<String>	result;

    result = new ArrayList<>(Arrays.asList(super.getOptions()));

    result.add("-att-name");
    result.add(getAttributeName());

    result.add("-sampleid-file");
    result.add("" + getSampleIDFile());

    return result.toArray(new String[0]);
  }

  /**
   * Sets the name of the attribute containing the numeric database ID.
   *
   * @param value 	the name of the attribute
   */
  public void setAttributeName(String value) {
    if ((value != null) && (!value.isEmpty()))
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
   * Sets the text file with the sample IDs to remove (one per line).
   *
   * @param value 	the file
   */
  public void setSampleIDFile(PlaceholderFile value) {
    m_SampleIDFile = value;
  }

  /**
   * Returns the text file with the sample IDs to remove (one per line).
   *
   * @return 		the file
   */
  public PlaceholderFile getSampleIDFile() {
    return m_SampleIDFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String sampleIDFileTipText() {
    return "The text file with the sample IDs to remove (one per line).";
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
    if (!(att.isString() || att.isNominal()))
      throw new UnsupportedAttributeTypeException("Database ID attribute ('" + m_AttributeName + "') must be nominal or string!");
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
    Instances		result;
    List<String> 	ids;
    Attribute		att;
    String		current;
    int			i;
    int			index;

    // the att index
    att = instances.attribute(m_AttributeName);
    if (att == null)
      throw new IllegalStateException("Attribute '" + m_AttributeName + "' not found!");
    index = att.index();

    // load sample IDs if necessary
    if (m_SampleIDs == null) {
      m_SampleIDs = new HashSet<>();
      if (m_SampleIDFile.exists() && !m_SampleIDFile.isDirectory()) {
	if (getDebug())
	  System.out.println(getClass().getName() + ": Loading sample ID file: " + m_SampleIDFile);
	ids = FileUtils.loadFromFile(m_SampleIDFile);
	for (String id: ids) {
	  id = id.trim();
	  if (!id.isEmpty())
	    m_SampleIDs.add(id);
	}
      }
    }

    // add instances
    result = new Instances(instances, instances.numInstances());
    for (i = 0; i < instances.numInstances(); i++) {
      current = instances.instance(i).stringValue(index);
      if (m_SampleIDs.contains(current)) {
	if (getDebug())
	  System.out.println(getClass().getName() + ": Skipping sample ID: " + current);
	continue;
      }
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
    runFilter(new RemoveSampleIDs(), args);
  }
}
