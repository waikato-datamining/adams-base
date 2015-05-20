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
 * Tool.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;
import adams.flow.core.ActorUtils;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.tools.AbstractTool;
import adams.tools.DatabaseIDProcessor;
import adams.tools.InitializeTables;
import adams.tools.InputFileHandler;
import adams.tools.OutputFileGenerator;

/**
 <!-- globalinfo-start -->
 * Runs a tool. If the tool is an InputFileHandler, then it will only accept String or File tokens, otherwise the token will be only used to trigger the execution. If the tool is an OutputFileGenerator then this File will be forwarded as token, otherwise the input token will be forwarded as it is.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: Tool
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
 * <pre>-tools &lt;adams.tools.AbstractTool&gt; (property: tool)
 * &nbsp;&nbsp;&nbsp;The tool to run.
 * &nbsp;&nbsp;&nbsp;default: adams.tools.InitializeTables
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Tool
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -295054877801672294L;

  /** the tool to run. */
  protected AbstractTool m_Tool;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Runs a tool. If the tool is an InputFileHandler, then it will only "
      + "accept String or File tokens, otherwise the token will be only used "
      + "to trigger the execution. If the tool is an OutputFileGenerator then "
      + "this File will be forwarded as token, otherwise the input token will "
      + "be forwarded as it is.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "tools", "tool",
	    new InitializeTables());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "tool", m_Tool);
  }

  /**
   * Sets the tool to use.
   *
   * @param value	the tool
   */
  public void setTool(AbstractTool value) {
    m_Tool = value;
    reset();
  }

  /**
   * Returns the tool in use.
   *
   * @return 		the tool
   */
  public AbstractTool getTool() {
    return m_Tool;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String toolTipText() {
    return "The tool to run.";
  }

  /**
   * Returns the default database connection.
   *
   * @return 		the default database connection
   */
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  protected adams.db.AbstractDatabaseConnection getDatabaseConnection() {
    return ActorUtils.getDatabaseConnection(
	  this,
	  adams.flow.standalone.DatabaseConnection.class,
	  getDefaultDatabaseConnection());
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    List<Class> result;

    result = new ArrayList<Class>();

    if (m_Tool instanceof InputFileHandler) {
      result.add(String.class);
      result.add(File.class);
    }

    if (m_Tool instanceof DatabaseIDProcessor)
      result.add(Integer[].class);

    if (result.size() == 0)
      result.add(Unknown.class);

    return result.toArray(new Class[result.size()]);
  }

  /**
   * Updates the database connection.
   */
  protected void updateDatabaseConnection() {
    if (m_Tool instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_Tool).setDatabaseConnection(getDatabaseConnection());
  }
  
  /**
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    int[]	ids;
    int		i;

    result = null;
    
    try {
      updateDatabaseConnection();
      
      // process input
      if (m_Tool instanceof InputFileHandler) {
	if (m_InputToken.getPayload() instanceof String)
	  ((InputFileHandler) m_Tool).setInputFile(new PlaceholderFile((String) m_InputToken.getPayload()));
	else if (m_InputToken.getPayload() instanceof File)
	  ((InputFileHandler) m_Tool).setInputFile(new PlaceholderFile((File) m_InputToken.getPayload()));
      }
      if (m_Tool instanceof DatabaseIDProcessor) {
	if (m_InputToken.getPayload() instanceof Integer[]) {
	  ids = new int[((Integer[]) m_InputToken.getPayload()).length];
	  for (i = 0; i < ids.length; i++)
	    ids[i] = ((Integer[]) m_InputToken.getPayload())[i];
	  ((DatabaseIDProcessor) m_Tool).setDatabaseIDs(ids);
	}
      }

      // execute tool
      m_Tool.run();

      // generate output
      if (m_Tool.isStopped()) {
	result        = "Tool was stopped!";
	m_OutputToken = null;
      }
      else {
	if (m_Tool instanceof OutputFileGenerator)
	  m_OutputToken = new Token(((OutputFileGenerator) m_Tool).getOutputFile());
	else
	  m_OutputToken = m_InputToken;
      }
    }
    catch (Exception e) {
      result = handleException("Failed to execute tool: " + m_Tool, e);
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  public Class[] generates() {
    if (m_Tool instanceof OutputFileGenerator)
      return new Class[]{File.class};
    else
      return new Class[]{Unknown.class};
  }
  
  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Tool != null)
      m_Tool.stopExecution();
    super.stopExecution();
  }
}
