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
 * DeleteData.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;


/**
 <!-- scriptlet-parameters-start -->
 * Action parameters:<br>
 * <pre>   delete-data &lt;comma-separated list of DB-IDs&gt;</pre>
 * <br><br>
 <!-- scriptlet-parameters-end -->
 *
 <!-- scriptlet-description-start -->
 * Description:
 * <pre>   Deletes the record with the specified DB-IDs from the database.</pre>
 * <br><br>
 <!-- scriptlet-description-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DeleteData
  extends AbstractScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = 1714520838655609203L;

  /** the action to execute. */
  public final static String ACTION = "delete-data";

  /**
   * Returns the action string used in the command processor.
   *
   * @return		the action string
   */
  @Override
  public String getAction() {
    return ACTION;
  }

  /**
   * Returns a one-line listing of the options of the action.
   *
   * @return		the options or null if none
   */
  @Override
  protected String getOptionsDescription() {
    return "<comma-separated list of DB-IDs>";
  }

  /**
   * Returns the full description of the action.
   *
   * @return		the full description
   */
  @Override
  public String getDescription() {
    return "Deletes the record with the specified DB-IDs from the database.";
  }

  /**
   * Processes the options.
   *
   * @param options	additional/optional options for the action
   * @return		null if no error, otherwise error message
   * @throws Exception 	if something goes wrong
   */
  @Override
  public String process(String options) throws Exception {
    String[]		ids;
    int			i;
    boolean		result;

    ids = options.replaceAll(" ", "").split(",");
    for (i = 0; i < ids.length; i++) {
      showStatus("Deleting record " + (i+1) + "/" + ids.length + ": " + ids[i]);
      result = m_DataProvider.remove(new Integer(ids[i]));
      if (!result)
	getLogger().severe("Error deleting record #" + ids[i]);
    }

    showStatus("");

    return null;
  }
}
