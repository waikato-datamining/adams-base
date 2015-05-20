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
 * GnuplotScript.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.gnuplot.AbstractScriptlet;
import adams.core.gnuplot.AbstractScriptletWithDataFile;
import adams.core.gnuplot.CustomScriptlet;
import adams.core.io.PlaceholderFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 <!-- globalinfo-start -->
 * Actor for generating Gnuplot script files for plotting data stored in the data file that is received as input.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * <p/>
 <!-- flow-summary-end -->
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: GnuplotScript
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The name of the Gnuplot script file.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-append (property: append)
 * &nbsp;&nbsp;&nbsp;If set to true, scripts gets only appended.
 * </pre>
 *
 * <pre>-scriptlet &lt;adams.core.gnuplot.AbstractScriptlet&gt; (property: scriptlet)
 * &nbsp;&nbsp;&nbsp;The scriplet to use for producing the Gnuplot script for plotting the data.
 * &nbsp;&nbsp;&nbsp;default: adams.core.gnuplot.CustomScriptlet
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GnuplotScript
  extends AbstractAppendableFileWriter {

  /** for serialization. */
  private static final long serialVersionUID = 7939417611957322357L;

  /** the script generator to use. */
  protected AbstractScriptlet m_Scriptlet;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Actor for generating Gnuplot script files for plotting data stored "
      + "in the data file that is received as input.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "scriptlet", "scriptlet",
	    new CustomScriptlet());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The name of the Gnuplot script file.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String appendTipText() {
    return "If set to true, scripts gets only appended.";
  }

  /**
   * Sets the scriptlet to use.
   *
   * @param value	the scriptlet
   */
  public void setScriptlet(AbstractScriptlet value) {
    m_Scriptlet = value;
    m_Scriptlet.setOwner(this);
    reset();
  }

  /**
   * Returns the scriptlet in use.
   *
   * @return		the scriplet
   */
  public AbstractScriptlet getScriptlet() {
    return m_Scriptlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String scriptletTipText() {
    return "The scriplet to use for producing the Gnuplot script for plotting the data.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String		script;
    BufferedWriter	writer;
    PlaceholderFile	file;

    if (m_InputToken.getPayload() instanceof String)
      file = new PlaceholderFile((String) m_InputToken.getPayload());
    else
      file = new PlaceholderFile((File) m_InputToken.getPayload());
    if (m_Scriptlet instanceof AbstractScriptletWithDataFile)
      ((AbstractScriptletWithDataFile) m_Scriptlet).setDataFile(file);
    script = m_Scriptlet.generate();
    script = getVariables().expand(script);
    result = m_Scriptlet.getLastError();

    if (result == null) {
      try {
	writer = new BufferedWriter(new FileWriter(m_OutputFile.getAbsolutePath(), m_Append));
	writer.write(script);
	writer.flush();
	writer.close();
	result = null;
      }
      catch (Exception e) {
	result = handleException("Failed to write script to: " + m_OutputFile, e);
      }
    }

    return result;
  }
}
