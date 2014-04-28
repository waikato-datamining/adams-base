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
 * AbstractViewPlugin.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer;

import adams.core.ClassLister;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BasePanel;

/**
 * Ancestor for "information" plugins in the spreadsheet viewer.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractViewPlugin
  extends AbstractOptionHandler 
  implements ShallowCopySupporter<AbstractViewPlugin> {

  /** for serialization. */
  private static final long serialVersionUID = 5139934776170019552L;

  /**
   * Returns the text of the menu item.
   *
   * @return 		the text
   */
  public abstract String getMenuText();

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  public abstract String getMenuIcon();

  /**
   * Checks the spreadsheet.
   * <p/>
   * Default implementation only checks whether data was provided.
   * 
   * @param sheet	the spreadsheet to check
   * @return		null if check passed, otherwise error message
   */
  protected String check(SpreadSheet sheet) {
    if (sheet == null)
      return "No spreadsheet supplied!";
    
    return null;
  }
  
  /**
   * Performs the actual generation of the information.
   * 
   * @param sheet	the sheet to process
   * @return		the generated information panel
   */
  protected abstract BasePanel doGenerate(SpreadSheet sheet);
  
  /**
   * Processes the spreadsheet.
   * 
   * @param sheet	the sheet to process
   * @return		the generated information panel
   * @throws IllegalArgumentException	if the check failed
   * @see #check(SpreadSheet)
   */
  public BasePanel generate(SpreadSheet sheet) {
    String	msg;
    
    msg = check(sheet);
    if (msg != null)
      throw new IllegalArgumentException(msg);
    
    return doGenerate(sheet);
  }
  
  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractViewPlugin shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractViewPlugin shallowCopy(boolean expand) {
    return (AbstractViewPlugin) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of plugins.
   *
   * @return		the plugin classnames
   */
  public static String[] getPlugins() {
    return ClassLister.getSingleton().getClassnames(AbstractViewPlugin.class);
  }
}
