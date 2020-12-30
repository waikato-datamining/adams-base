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
 * SerializedFilter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package weka.filters;

import adams.core.SerializationHelper;
import adams.core.io.PlaceholderFile;
import weka.core.Capabilities;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Processes the data with a the (trained) filter deserialized from the specified file.
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
 * <pre> -serialized &lt;file&gt;
 *  The serialized filter file.
 *  (default: .)</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SerializedFilter
  extends SimpleBatchFilter {

  /** for serialization. */
  private static final long serialVersionUID = 4204884126927666844L;

  /** the flow file to process the data with. */
  protected PlaceholderFile m_Serialized = new PlaceholderFile();

  /** the actual filter. */
  protected Filter m_ActualFilter;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return 
	"Processes the data with a the (trained) filter deserialized from the "
          + "specified file.";
  }

  /**
   * Resets the filter.
   */
  protected void reset() {
    super.reset();

    m_ActualFilter = null;
  }
  
  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector		result;
    Enumeration		enm;

    result = new Vector();

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    result.addElement(new Option(
	"\tThe serialized filter file.\n"
	+ "\t(default: .)",
	"serialized", 1, "-serialized <file>"));

    return result.elements();
  }

  /**
   * returns the options of the current setup.
   *
   * @return      the current options
   */
  public String[] getOptions() {
    List<String> result;

    result = new ArrayList<>(Arrays.asList(super.getOptions()));

    result.add("-serialized");
    result.add("" + getSerialized());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object.
   *
   * @param options	the options to use
   * @throws Exception	if the option setting fails
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption("serialized", options);
    if (tmpStr.length() != 0)
      setSerialized(new PlaceholderFile(tmpStr));
    else
      setSerialized(new PlaceholderFile());

    super.setOptions(options);
  }

  /**
   * Sets the serialized filter file.
   *
   * @param value	the file
   */
  public void setSerialized(PlaceholderFile value) {
    m_Serialized = value;
    reset();
  }

  /**
   * Returns the serialized filter file.
   *
   * @return 		the file
   */
  public PlaceholderFile getSerialized() {
    return m_Serialized;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String serializedTipText() {
    return "The file containing the (trained) serialized filter.";
  }

  /**
   * Returns the actual filter in use, loads it if necessary.
   *
   * @return		the actual filter in use, null if failed to load or no file provided
   */
  protected synchronized Filter getActualFilter() {
    if (m_ActualFilter == null) {
      if (!m_Serialized.exists())
	return null;
      if (m_Serialized.isDirectory())
	return null;

      try {
	m_ActualFilter = (Filter) SerializationHelper.read(m_Serialized.getAbsolutePath());
      }
      catch (Exception e) {
	System.err.println(getClass().getName() + ": Failed to load serialized filter from " + m_Serialized);
	e.printStackTrace();
	return null;
      }
    }

    return m_ActualFilter;
  }

  /**
   * Determines the output format based on the input format and returns
   * this. In case the output format cannot be returned immediately, i.e.,
   * immediateOutputFormat() returns false, then this method will be called
   * from batchFinished().
   * <br><br>
   * Simply returns the input format. The process() method determines the 
   * actual format as the format is not known apriori.
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   */
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    if (getActualFilter() != null)
      return getActualFilter().getOutputFormat();
    else
      return new Instances(inputFormat, 0);
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  public Capabilities getCapabilities() {
    if (getActualFilter() != null)
      return getActualFilter().getCapabilities();
    else
      return super.getCapabilities();
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
    if (getActualFilter() != null)
      return Filter.useFilter(instances, getActualFilter());
    else
      throw new IllegalStateException("No serialized filter loaded!");
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
   * @param args 	should contain arguments to the filter: use -h for help
   */
  public static void main(String[] args) {
    runFilter(new SerializedFilter(), args);
  }
}
