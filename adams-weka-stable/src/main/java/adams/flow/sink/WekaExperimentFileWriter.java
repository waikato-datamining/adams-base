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
 * WekaExperimentFileWriter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.gui.tools.wekamultiexperimenter.io.AbstractExperimentIO;
import adams.gui.tools.wekamultiexperimenter.io.DefaultAdamsExperimentIO;

/**
 <!-- globalinfo-start -->
 * Saves an experiment file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaExperimentFileWriter
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: outputFile)
 * &nbsp;&nbsp;&nbsp;The file to write the experiment to.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-handler &lt;adams.gui.tools.wekamultiexperimenter.io.AbstractExperimentIO&gt; (property: handler)
 * &nbsp;&nbsp;&nbsp;The I&#47;O handler to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.tools.wekamultiexperimenter.io.DefaultAdamsExperimentIO
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaExperimentFileWriter
  extends AbstractFileWriter {

  private static final long serialVersionUID = -1488720963594190156L;

  /** the IO handler. */
  protected AbstractExperimentIO m_Handler;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Saves an experiment file."
	+ "Supported file formats of current handler: "
	+ Utils.flatten(m_Handler.getSupportedFileExtensions(false), ",");
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "handler", "handler",
      getDefaultHandler());
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputFileTipText() {
    return "The file to write the experiment to.";
  }

  /**
   * Returns the default IO handler.
   *
   * @return		the handler
   */
  protected AbstractExperimentIO getDefaultHandler() {
    return new DefaultAdamsExperimentIO();
  }

  /**
   * Sets the IO handler.
   *
   * @param value	the handler
   */
  public void setHandler(AbstractExperimentIO value) {
    m_Handler = value;
    reset();
  }

  /**
   * Returns the IO handler.
   *
   * @return		the handler
   */
  public AbstractExperimentIO getHandler() {
    return m_Handler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String handlerTipText() {
    return "The I/O handler to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "outputFile", m_OutputFile, "output: ");
    result += QuickInfoHelper.toString(this, "handler", m_Handler, ", handler: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{m_Handler.getExperimentClass()};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    if (!m_Handler.save(m_InputToken.getPayload(), m_OutputFile.getAbsoluteFile()))
      result = "Failed to write experiment to: " + m_OutputFile;

    return result;
  }
}
