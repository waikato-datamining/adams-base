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
 * AbstractViewerTab.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.tab;

import adams.core.ClassLister;
import adams.core.option.OptionUtils;
import adams.gui.core.BasePanel;
import adams.gui.tools.spreadsheetviewer.SpreadSheetPanel;

/**
 * Ancestor for tabs that show up in the spreadsheet viewer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractViewerTab
  extends BasePanel
  implements Comparable<AbstractViewerTab> {

  /** for serialization. */
  private static final long serialVersionUID = -1380932223368136260L;

  /** the owning tab manager. */
  protected ViewerTabManager m_Owner;
  
  /**
   * Sets the tab manager this tab belongs to.
   * 
   * @param value	the owner
   */
  public void setOwner(ViewerTabManager value) {
    m_Owner = value;
  }
  
  /**
   * Returns the tab manager that this tab belongs to.
   * 
   * @return		the owner
   */
  public ViewerTabManager getOwner() {
    return m_Owner;
  }
  
  /**
   * Returns the current flow panel.
   * 
   * @return		the current panel, null if not available
   */
  public SpreadSheetPanel getCurrentPanel() {
    if (getOwner() == null)
      return null;
    if (getOwner().getOwner() == null)
      return null;
    return getOwner().getOwner().getCurrentPanel();
  }
  
  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  public abstract String getTitle();

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Simply uses the title for comparison.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  @Override
  public int compareTo(AbstractViewerTab o) {
    return getTitle().compareTo(o.getTitle());
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
   * Only compares the title of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof AbstractViewerTab)
      return (compareTo((AbstractViewerTab) o) == 0);
    else
      return false;
  }

  /**
   * Returns a list with classnames of tabs.
   *
   * @return		the tab classnames
   */
  public static String[] getTabs() {
    return ClassLister.getSingleton().getClassnames(AbstractViewerTab.class);
  }

  /**
   * Instantiates the tab with the given options.
   *
   * @param classname	the classname of the tab to instantiate
   * @return		the instantiated tab or null if an error occurred
   */
  public static AbstractViewerTab forName(String classname) {
    AbstractViewerTab	result;

    try {
      result = (AbstractViewerTab) OptionUtils.forName(AbstractViewerTab.class, classname, new String[0]);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }
}
