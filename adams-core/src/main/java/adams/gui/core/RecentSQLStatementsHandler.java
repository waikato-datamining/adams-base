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
 * RecentSQLStatementsHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.Shortening;
import adams.db.SQLStatement;

/**
 * A class that handles a list of recent SQL statements.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RecentSQLStatementsHandler<M>
  extends AbstractRecentItemsHandler<M, SQLStatement> {

  /** for serialization. */
  private static final long serialVersionUID = 7532226757387619342L;

  /** the property for storing the number of recent statements. */
  public final static String RECENTSTATEMENTS_COUNT = "RecentStatementsCount";

  /** the property prefix for a recent statement. */
  public final static String RECENTSTATEMENTS_PREFIX = "RecentStatement.";

  /**
   * Initializes the handler with a maximum of 5 items.
   *
   * @param propsFile	the props file to store the files in
   * @param menu	the menu to add the recent files as subitems to
   */
  public RecentSQLStatementsHandler(String propsFile, M menu) {
    super(propsFile, menu);
  }

  /**
   * Initializes the handler.
   *
   * @param propsFile	the props file to store the files in
   * @param maxCount	the maximum number of files to keep in menu
   * @param menu	the menu to add the recent files as subitems to
   */
  public RecentSQLStatementsHandler(String propsFile, int maxCount, M menu) {
    super(propsFile, maxCount, menu);
  }

  /**
   * Initializes the handler.
   *
   * @param propsFile	the props file to store the files in
   * @param propPrefix	the properties prefix, use null to ignore
   * @param maxCount	the maximum number of files to keep in menu
   * @param menu	the menu to add the recent files as subitems to
   */
  public RecentSQLStatementsHandler(String propsFile, String propPrefix, int maxCount, M menu) {
    super(propsFile, propPrefix, maxCount, menu);
  }

  /**
   * Returns the key to use for the counts in the props file.
   * 
   * @return		the key
   */
  @Override
  protected String getCountKey() {
    return RECENTSTATEMENTS_COUNT;
  }

  /**
   * Returns the key prefix to use for the items in the props file.
   * 
   * @return		the prefix
   */
  @Override
  protected String getItemPrefix() {
    return RECENTSTATEMENTS_PREFIX;
  }

  /**
   * Turns an object into a string for storing in the props.
   * 
   * @param obj		the object to convert
   * @return		the string representation
   */
  @Override
  protected String toString(SQLStatement obj) {
    return obj.toString();
  }

  /**
   * Turns the string obtained from the props into an object again.
   * 
   * @param s		the string representation
   * @return		the parsed object
   */
  @Override
  protected SQLStatement fromString(String s) {
    return new SQLStatement(s);
  }
  
  /**
   * Generates the text for the menuitem.
   * 
   * @param index	the index of the item
   * @param item	the item itself
   * @return		the generated text
   */
  @Override
  protected String createMenuItemText(int index, SQLStatement item) {
    return Shortening.shortenEnd(item.getValue().replaceAll("\\s", " "), 40);
  }
}
