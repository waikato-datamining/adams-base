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
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer;

import java.util.Hashtable;

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

  /** for storing the last setup for a plugin. */
  protected static Hashtable<Class,Object> m_LastSetup;
  static {
    m_LastSetup = new Hashtable<Class,Object>();
  }

  /** the current panel. */
  protected SpreadSheetPanel m_CurrentPanel;

  /** the generated panel. */
  protected BasePanel m_Panel;
  
  /** whether the user canceled the operation. */
  protected boolean m_CanceledByUser;

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
   * Sets the current panel.
   * 
   * @param value	the panel
   */
  public void setCurrentPanel(SpreadSheetPanel value) {
    m_CurrentPanel = value;
  }
  
  /**
   * Returns the current panel.
   * 
   * @return		the panel, null if none set
   */
  public SpreadSheetPanel getCurrentPanel() {
    return m_CurrentPanel;
  }

  /**
   * Checks whether there is a setup available for the class of this object.
   *
   * @param obj		the object to check for
   * @return		true if a setup is available
   */
  protected boolean hasLastSetup() {
    return m_LastSetup.containsKey(getClass());
  }

  /**
   * Returns the last setup for this object's class.
   *
   * @param obj		the object (actually the class) to get the setup for
   * @return		the setup, null if none available
   */
  protected Object getLastSetup() {
    return m_LastSetup.get(getClass());
  }

  /**
   * Stores the setup for this object's class.
   *
   * @param obj		the object (actually the class) to get the setup for
   * @param setup	the setup to store
   */
  protected void setLastSetup(Object setup) {
    m_LastSetup.put(getClass(), setup);
  }

  /**
   * Returns whether the operation was canceled by the user.
   *
   * @return		true if the user canceled the operation
   */
  public boolean getCanceledByUser() {
    return m_CanceledByUser;
  }

  /**
   * Returns whether a view can be generated.
   * 
   * @param panel	the panel to check
   * @return		true if view can be generated
   */
  public boolean canView(SpreadSheetPanel panel) {
    return (panel != null) && (panel.getSheet() != null);
  }

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
   * Returns whether the dialog requires the OK/Cancel buttons.
   * <p/>
   * Default implementation returns true.
   * 
   * @return		true if the dialog requires the buttons
   */
  public boolean requiresButtons() {
    return true;
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
    
    m_Panel = doGenerate(sheet);
    
    return m_Panel;
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
