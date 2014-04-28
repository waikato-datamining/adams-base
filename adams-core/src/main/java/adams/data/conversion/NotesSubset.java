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
 * NotesToString.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.Notes;

/**
 <!-- globalinfo-start -->
 * Generates a subset of a adams.data.Notes object.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-output-errors (property: outputErrors)
 * &nbsp;&nbsp;&nbsp;If set to true, then the errors will be output.
 * </pre>
 * 
 * <pre>-output-warnings (property: outputWarnings)
 * &nbsp;&nbsp;&nbsp;If set to true, then the warnings will be output.
 * </pre>
 * 
 * <pre>-output-process-info (property: outputProcessInformation)
 * &nbsp;&nbsp;&nbsp;If set to true, then the process information will be output.
 * </pre>
 * 
 * <pre>-output-all (property: outputAll)
 * &nbsp;&nbsp;&nbsp;If set to true, then everything will be output.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NotesSubset
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 2680919631770139037L;

  /** whether to output the errors. */
  protected boolean m_OutputErrors;

  /** whether to output the warnings. */
  protected boolean m_OutputWarnings;

  /** whether to output the process information. */
  protected boolean m_OutputProcessInformation;

  /** whether to output everything. */
  protected boolean m_OutputAll;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a subset of a " + Notes.class.getName() + " object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output-errors", "outputErrors",
	    false);

    m_OptionManager.add(
	    "output-warnings", "outputWarnings",
	    false);

    m_OptionManager.add(
	    "output-process-info", "outputProcessInformation",
	    false);

    m_OptionManager.add(
	    "output-all", "outputAll",
	    false);
  }

  /**
   * Sets whether to output the errors.
   *
   * @param value 	if true then the errors will be output
   */
  public void setOutputErrors(boolean value) {
    m_OutputErrors = value;
    reset();
  }

  /**
   * Returns whether to output the errors.
   *
   * @return 		true if the errors will be output
   */
  public boolean getOutputErrors() {
    return m_OutputErrors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputErrorsTipText() {
    return "If set to true, then the errors will be output.";
  }

  /**
   * Sets whether to output the warnings.
   *
   * @param value 	if true then the warnings will be output
   */
  public void setOutputWarnings(boolean value) {
    m_OutputWarnings = value;
    reset();
  }

  /**
   * Returns whether to output the warnings.
   *
   * @return 		true if the warnings will be output
   */
  public boolean getOutputWarnings() {
    return m_OutputWarnings;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputWarningsTipText() {
    return "If set to true, then the warnings will be output.";
  }

  /**
   * Sets whether to output the process information.
   *
   * @param value 	if true then the process information will be output
   */
  public void setOutputProcessInformation(boolean value) {
    m_OutputProcessInformation = value;
    reset();
  }

  /**
   * Returns whether to output the process information.
   *
   * @return 		true if the process information will be output
   */
  public boolean getOutputProcessInformation() {
    return m_OutputProcessInformation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputProcessInformationTipText() {
    return "If set to true, then the process information will be output.";
  }

  /**
   * Sets whether to output everything.
   *
   * @param value 	if true then everything will be output
   */
  public void setOutputAll(boolean value) {
    m_OutputAll = value;
    reset();
  }

  /**
   * Returns whether to output everything.
   *
   * @return 		true if everything will be output
   */
  public boolean getOutputAll() {
    return m_OutputAll;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputAllTipText() {
    return "If set to true, then everything will be output.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Notes.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Notes.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Notes	result;
    Notes	input;
    
    result = new Notes();
    input  = (Notes) m_Input;

    if (m_OutputAll || m_OutputErrors || m_OutputWarnings || m_OutputProcessInformation) {
      if (m_OutputAll) {
	result.mergeWith(input);
      }
      else {
	if (m_OutputErrors)
	  result.mergeWith(input.getErrors());
	if (m_OutputWarnings)
	  result.mergeWith(input.getWarnings());
	if (m_OutputProcessInformation)
	  result.mergeWith(input.getProcessInformation());
      }
    }
    
    return result;
  }
}
