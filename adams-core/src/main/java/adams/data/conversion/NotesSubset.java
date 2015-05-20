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
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.Notes;

/**
 <!-- globalinfo-start -->
 * Generates a subset of a adams.data.Notes object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-output-errors &lt;boolean&gt; (property: outputErrors)
 * &nbsp;&nbsp;&nbsp;If set to true, then the errors will be output.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-warnings &lt;boolean&gt; (property: outputWarnings)
 * &nbsp;&nbsp;&nbsp;If set to true, then the warnings will be output.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-process-info &lt;boolean&gt; (property: outputProcessInformation)
 * &nbsp;&nbsp;&nbsp;If set to true, then the process information will be output.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-other &lt;boolean&gt; (property: outputOther)
 * &nbsp;&nbsp;&nbsp;If set to true, then the other notes will be output.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-all &lt;boolean&gt; (property: outputAll)
 * &nbsp;&nbsp;&nbsp;If set to true, then everything will be output.
 * &nbsp;&nbsp;&nbsp;default: false
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

  /** whether to output all other notes. */
  protected boolean m_OutputOther;

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
	    "output-other", "outputOther",
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
   * Sets whether to output the other notes.
   *
   * @param value 	if true then the other notes will be output
   */
  public void setOutputOther(boolean value) {
    m_OutputOther = value;
    reset();
  }

  /**
   * Returns whether to output the other notes.
   *
   * @return 		true if the other notes will be output
   */
  public boolean getOutputOther() {
    return m_OutputOther;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputOtherTipText() {
    return "If set to true, then the other notes will be output.";
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

    if (m_OutputAll || m_OutputErrors || m_OutputWarnings || m_OutputProcessInformation || m_OutputOther) {
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
	if (m_OutputOther)
	  result.mergeWith(input.getOthers());
      }
    }
    
    return result;
  }
}
