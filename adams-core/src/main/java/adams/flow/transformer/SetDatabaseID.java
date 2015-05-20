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
 * SetDatabaseID.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.id.MutableDatabaseIDHandler;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Updates the database ID of the database ID handler passing through with the provided 'ID' value. Attach a variable to the 'ID' option to allow for more flexibility.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.id.MutableDatabaseIDHandler<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.id.MutableDatabaseIDHandler<br>
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
 * &nbsp;&nbsp;&nbsp;default: SetDatabaseID
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
 * <pre>-id &lt;java.lang.Integer&gt; (property: ID)
 * &nbsp;&nbsp;&nbsp;The new database ID to use.
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetDatabaseID
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 7195919809805609634L;

  /** the database ID to set. */
  protected Integer m_ID;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Updates the database ID of the database ID handler passing through with the provided "
	+ "'ID' value. Attach a variable to the 'ID' option to allow for more "
        + "flexibility.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "id", "ID",
	    -1);
  }

  /**
   * Sets the new database ID to use.
   *
   * @param value	the ID
   */
  public void setID(Integer value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the database ID to use.
   *
   * @return		the ID
   */
  public Integer getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String IDTipText() {
    return "The new database ID to use.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "ID", m_ID);
  }
  
  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.id.MutableDatabaseIDHandler.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{MutableDatabaseIDHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.data.id.MutableDatabaseIDHandler.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{MutableDatabaseIDHandler.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    MutableDatabaseIDHandler	handler;

    result = null;

    try {
      handler       = (MutableDatabaseIDHandler) m_InputToken.getPayload();
      handler.setDatabaseID(m_ID);
      m_OutputToken = new Token(handler);
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to set database ID: " + m_InputToken.getPayload(), e);
    }

    return result;
  }
}
