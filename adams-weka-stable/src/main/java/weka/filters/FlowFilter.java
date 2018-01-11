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
 * FlowFilter.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package weka.filters;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Capabilities;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import adams.core.io.FlowFile;
import adams.flow.control.SubProcess;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Processes the data with a flow. The flow's outer control actor must be a adams.flow.control.SubProcess actor, which takes weka.core.Instances as input and generates weka.core.Instances again.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 * 
 * <pre> -flow-file &lt;file&gt;
 *  The flow to use for processing the data.
 *  (default: .)</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowFilter
  extends SimpleBatchFilter {

  /** for serialization. */
  private static final long serialVersionUID = 4204884126927666844L;

  /** the flow file to process the data with. */
  protected FlowFile m_FlowFile = new FlowFile(".");

  /** the flow for processing the data. */
  protected SubProcess m_SubProcess = null;
  
  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return 
	"Processes the data with a flow. The flow's outer control actor "
	+ "must be a " + SubProcess.class.getName() + " actor, which "
	+ "takes " + Instances.class.getName() + " as input and generates "
	+ Instances.class.getName() + " again.";
  }

  /**
   * Resets the filter.
   */
  protected void reset() {
    super.reset();
    
    if (m_SubProcess != null) {
      m_SubProcess.destroy();
      m_SubProcess = null;
    }
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
	"\tThe flow to use for processing the data.\n"
	+ "\t(default: .)",
	"flow-file", 1, "-flow-file <file>"));

    return result.elements();
  }

  /**
   * returns the options of the current setup.
   *
   * @return      the current options
   */
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>(Arrays.asList(super.getOptions()));

    result.add("-flow-file");
    result.add("" + getFlowFile());

    return (String[]) result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object. <br><br>
   *
   <!-- options-start -->
   * Valid options are: <br><br>
   * 
   * <pre> -D
   *  Turns on output of debugging information.</pre>
   * 
   * <pre> -flow-file &lt;file&gt;
   *  The flow to use for processing the data.
   *  (default: .)</pre>
   * 
   <!-- options-end -->
   *
   * @param options	the options to use
   * @throws Exception	if the option setting fails
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    super.setOptions(options);

    tmpStr = Utils.getOption("flow-file", options);
    if (tmpStr.length() != 0)
      setFlowFile(new FlowFile(tmpStr));
    else
      setFlowFile(new FlowFile("."));
  }

  /**
   * Sets the flow to process the data with.
   *
   * @param value	the flow
   */
  public void setFlowFile(FlowFile value) {
    m_FlowFile = value;
  }

  /**
   * Returns the flow to process the data with.
   *
   * @return 		the flow
   */
  public FlowFile getFlowFile() {
    return m_FlowFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String flowFileTipText() {
    return "The flow to process the data with.";
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
    return new Instances(inputFormat, 0);
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  public Capabilities getCapabilities() {
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
    Instances	result;
    String	msg;
    
    result = null;
    
    // load flow
    if (!m_FirstBatchDone) {
      if (m_FlowFile.isDirectory())
	throw new IllegalStateException("No flow file defined: " + m_FlowFile);
      m_SubProcess = (SubProcess) ActorUtils.read(m_FlowFile.getAbsolutePath());
      msg = m_SubProcess.setUp();
      if (msg != null)
	throw new IllegalStateException(msg);
    }

    m_SubProcess.input(new Token(instances));
    msg = m_SubProcess.execute();
    if (msg == null) {
      if (m_SubProcess.hasPendingOutput())
	result = (Instances) m_SubProcess.output().getPayload();
      else
	throw new IllegalStateException("Flow did not generate any data!");
    }
    m_SubProcess.wrapUp();

    if (!m_FirstBatchDone && (result != null))
      setOutputFormat(new Instances(result));
    
    return result;
  }
}
