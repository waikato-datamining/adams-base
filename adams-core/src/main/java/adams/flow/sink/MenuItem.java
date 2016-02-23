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
 * MenuItem.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.AdditionalParameterHandler;
import adams.gui.menu.TextEditor;

/**
 <!-- globalinfo-start -->
 * Launches a menu item from the main menu. If the menu item accepts string parameter(s) then the incoming data will be forwarded to it.
 * <br><br>
 <!-- globalinfo-end -->
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
 * &nbsp;&nbsp;&nbsp;default: MenuItem
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
 * <pre>-menu-item &lt;adams.gui.application.AbstractMenuItemDefinition&gt; (property: menuItem)
 * &nbsp;&nbsp;&nbsp;The menu item to launch.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.menu.TextEditor
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MenuItem
  extends AbstractSink {

  /** for serialization. */
  private static final long serialVersionUID = -4210882711380055794L;
  
  /** the menu item to executre. */
  protected AbstractBasicMenuItemDefinition m_MenuItem;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Launches a menu item from the main menu. If the menu item accepts "
	+ "string parameter(s) then the incoming data will be forwarded to it.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "menu-item", "menuItem",
	    new TextEditor());
  }

  /**
   * Sets the class label index (1-based index).
   *
   * @param value 	the index
   */
  public void setMenuItem(AbstractBasicMenuItemDefinition value) {
    m_MenuItem = value;
    reset();
  }

  /**
   * Returns the class label index (1-based index).
   *
   * @return 		the index
   */
  public AbstractBasicMenuItemDefinition getMenuItem() {
    return m_MenuItem;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String menuItemTipText() {
    return "The menu item to launch.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "menuItem", m_MenuItem.getClass());
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.lang.String[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, String[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String[]		params;

    result = null;

    try {
      if (m_MenuItem instanceof AdditionalParameterHandler) {
	if (m_InputToken.getPayload() instanceof String)
	  params = new String[]{(String) m_InputToken.getPayload()};
	else
	  params = (String[]) m_InputToken.getPayload();
	((AdditionalParameterHandler) m_MenuItem).setAdditionalParameters(params);
	m_MenuItem.launch();
      }
    }
    catch (Exception e) {
      result = handleException("Failed to launch menu item:", e);
    }

    return result;
  }
}
