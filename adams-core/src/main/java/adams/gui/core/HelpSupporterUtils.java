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
 * HelpSupporterUtils.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.discovery.PropertyPath.Path;
import adams.core.discovery.PropertyTraversal;
import adams.core.discovery.PropertyTraversal.Observer;
import adams.flow.sink.ComponentSupplier;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions for dealing with HelpSupporter objects.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class HelpSupporterUtils {

  /**
   * For locating {@link HelpSupporter} instances.
   */
  public static class HelpSupporterObserver
    implements Observer {

    /** the instances. */
    protected List<HelpSupporter> m_Supporters;

    /**
     * Initializes the observer.
     */
    public HelpSupporterObserver() {
      m_Supporters = new ArrayList<>();
    }

    /**
     * Presents the current path, descriptor and object to the observer.
     *
     * @param path   the path
     * @param desc   the property descriptor
     * @param parent the parent object
     * @param child  the child object
     * @return true if to continue observing
     */
    @Override
    public boolean observe(Path path, PropertyDescriptor desc, Object parent, Object child) {
      if (child instanceof HelpSupporter)
	m_Supporters.add((HelpSupporter) child);
      return true;
    }

    /**
     * Returns the supporters, if any.
     *
     * @return		the supporters
     */
    public List<HelpSupporter> getSupporters() {
      return m_Supporters;
    }
  }

  /**
   * Locates the first help supporter in the property tree.
   *
   * @param obj		the object to traverse
   * @return		the helper supporter, if any
   */
  protected static HelpSupporter locate(Object obj) {
    HelpSupporterObserver	observer;

    observer = new HelpSupporterObserver();
    new PropertyTraversal().traverse(observer, obj);
    if (observer.getSupporters().isEmpty()) {
      if (obj instanceof ComponentSupplier)
	return locate(((ComponentSupplier) obj).supplyComponent());
      else
	return null;
    }
    else {
      return observer.getSupporters().get(0);
    }
  }

  /**
   * Clears any help information.
   *
   * @param obj 	traverses the
   */
  public static void clearHelp(Object obj) {
    HelpSupporter	supporter;

    supporter = locate(obj);
    if (supporter != null)
      supporter.clearHelp();
  }

  /**
   * Returns whether any help information is available.
   *
   * @return		true if help available
   */
  public static boolean hasHelp(Object obj) {
    HelpSupporter	supporter;

    supporter = locate(obj);
    return (supporter != null) && supporter.hasHelp();
  }

  /**
   * Sets the help information to offer.
   *
   * @param help	the help
   * @param isHtml	whether html or plain text
   */
  public static void setHelp(Object obj, String help, boolean isHtml) {
    HelpSupporter	supporter;

    supporter = locate(obj);
    if (supporter != null)
      supporter.setHelp(help, isHtml);

  }

  /**
   * Returns the help information if any.
   *
   * @return		the help information
   */
  public static String getHelp(Object obj) {
    HelpSupporter	supporter;

    supporter = locate(obj);
    if (supporter != null)
      return supporter.getHelp();
    else
      return null;
  }

  /**
   * Returns whether the help is html or plain text.
   *
   * @return		true if supporter available and help is html
   */
  public static boolean isHelpHtml(Object obj) {
    HelpSupporter	supporter;

    supporter = locate(obj);
    if (supporter != null)
      return supporter.isHelpHtml();
    else
      return false;
  }

  /**
   * Displays the help.
   */
  public static void showHelp(Object obj) {
    HelpSupporter	supporter;

    supporter = locate(obj);
    if (supporter != null)
      supporter.showHelp();
  }
}
