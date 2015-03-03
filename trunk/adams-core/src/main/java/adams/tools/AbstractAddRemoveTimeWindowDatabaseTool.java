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
 * AbstractAddRemoveTimeWindowDatabaseTool.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.tools;


/**
 * Ancestor for tools that add or remove stuff in the database.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAddRemoveTimeWindowDatabaseTool
  extends AbstractTimeWindowDatabaseTool {

  /** for serialization. */
  private static final long serialVersionUID = -4704807429728625180L;

  /** whether to add flags. */
  protected boolean m_Add;

  /** whether to remove the flags. */
  protected boolean m_Remove;

  /** whether to write a modified report back to the database. */
  protected boolean m_Store;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "add", "add",
	    false);

    m_OptionManager.add(
	    "remove", "remove",
	    false);

    m_OptionManager.add(
	    "store", "store",
	    false);
  }

  /**
   * Sets whether only new standard flags get set and current ones not revoked
   * or not.
   *
   * @param value 	if true then standard flags only get added but never
   * 			revoked; if false then previously standard flags can
   * 			be set to false (if they don't match the reg. exp.)
   */
  public void setAdd(boolean value) {
    m_Add = value;
  }

  /**
   * Returns whether only new standard flags get set and current ones not
   * revoked or not.
   *
   * @return 		true if only new standards get added and current ones
   * 			not (potentially) revoked.
   */
  public boolean getAdd() {
    return m_Add;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String addTipText();

  /**
   * Sets whether to remove the true flags using the regular expressions or not.
   *
   * @param value 	if true then standard flags only get added but never
   * 			revoked; if false then previously standard flags can
   * 			be set to false (if they don't match the reg. exp.)
   */
  public void setRemove(boolean value) {
    m_Remove = value;
  }

  /**
   * Returns whether the true flags are removed based on the regular
   * expressions or not.
   *
   * @return 		true if only new standards get added and current ones
   * 			not (potentially) revoked.
   */
  public boolean getRemove() {
    return m_Remove;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String removeTipText();

  /**
   * Sets whether a modified report gets written back to the database.
   *
   * @param value 	if true then modified reports get written back to the
   * 			database
   */
  public void setStore(boolean value) {
    m_Store = value;
  }

  /**
   * Returns whether a modified report gets written back to the database.
   *
   * @return 		true if a modified report gets written back to the
   * 			database
   */
  public boolean getStore() {
    return m_Store;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storeTipText() {
    return "Whether to write a modified data back to the database.";
  }

  /**
   * Before the actual run is executed. Checks the setup and corrects things,
   * if possible.
   */
  protected void preRun() {
    super.preRun();

    if (m_Add == m_Remove)
      throw new IllegalStateException("Either '-add' or '-remove' has to be set!");
  }

  /**
   * Peforms the "remove" run.
   */
  protected abstract void doRemoveRun();

  /**
   * Peforms the "add" run.
   */
  protected abstract void doAddRun();

  /**
   * Executes the tool.
   */
  protected void doRun() {
    if (m_Add)
      doAddRun();
    else if (m_Remove)
      doRemoveRun();
  }
}
