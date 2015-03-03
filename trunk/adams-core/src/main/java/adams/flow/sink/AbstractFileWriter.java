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
 * AbstractFileWriter.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;

/**
 * Ancestor for sinks that write to output files.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFileWriter
  extends AbstractSink
  implements FileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 3429427421208115104L;

  /** the output file. */
  protected PlaceholderFile m_OutputFile;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output", "outputFile",
	    getDefaultOutputFile());
  }

  /**
   * Returns the default output file.
   *
   * @return		the file
   */
  protected PlaceholderFile getDefaultOutputFile() {
    return new PlaceholderFile(".");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "outputFile", m_OutputFile);
  }

  /**
   * Set output file.
   *
   * @param value	file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Get output file.
   *
   * @return	file
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String outputFileTipText();

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    String	variable;

    result = super.setUp();

    if (result == null) {
      variable = getOptionManager().getVariableForProperty("outputFile");
      if (variable == null) {
	if (m_OutputFile.isDirectory())
	  result = m_OutputFile + " is a directory!";
      }
    }

    return result;
  }
}
