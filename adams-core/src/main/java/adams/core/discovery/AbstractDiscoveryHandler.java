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
 * AbstractDiscoveryHandler.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import adams.core.ShallowCopySupporter;
import adams.core.base.BaseRegExp;
import adams.core.discovery.PropertyPath.Path;
import adams.core.discovery.PropertyPath.PropertyContainer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for discovery algorithms.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDiscoveryHandler
  extends AbstractOptionHandler
  implements ShallowCopySupporter<AbstractDiscoveryHandler> {

  private static final long serialVersionUID = 714181953546661217L;

  /** the regular expression on the property path. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching sense. */
  protected boolean m_InvertMatching;

  /** the located property containers. */
  protected transient List<PropertyContainer> m_Containers;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "invert-matching", "invertMatching",
      false);
  }

  /**
   * Resets the handler.
   */
  @Override
  protected void reset() {
    super.reset();

    if (m_Containers != null)
      m_Containers.clear();
  }

  /**
   * Sets the regular expression to match the path against.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the path against.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to match the path against.";
  }

  /**
   * Sets whether to invert the matching sense of the path regexp.
   *
   * @param value	true if to invert
   */
  public void setInvertMatching(boolean value) {
    m_InvertMatching = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense of the path regexp.
   *
   * @return		true if to invert
   */
  public boolean getInvertMatching() {
    return m_InvertMatching;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertMatchingTipText() {
    return "If enabled, the path matching is inverted.";
  }

  /**
   * Stores the container.
   *
   * @param value	the container
   */
  public void addContainer(PropertyContainer value) {
    getContainers().add(value);
  }

  /**
   * The stored property container(s).
   *
   * @return		the container(s)
   */
  public synchronized List<PropertyContainer> getContainers() {
    if (m_Containers == null)
      m_Containers = new ArrayList<>();
    return m_Containers;
  }

  /**
   * Checks whether this object is handled by this discovery handler.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  protected abstract boolean handles(Object obj);

  /**
   * Checks whether this object is handled by this discovery handler.
   *
   * @param path	the associated path
   * @param obj		the object to check
   * @return		true if handled
   */
  public boolean handles(Path path, Object obj) {
    if (m_RegExp.isMatchAll())
      return handles(obj);

    if (m_InvertMatching)
      return !m_RegExp.isMatch(path.toString()) && handles(obj);
    else
      return m_RegExp.isMatch(path.toString()) && handles(obj);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractDiscoveryHandler shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractDiscoveryHandler shallowCopy(boolean expand) {
    return (AbstractDiscoveryHandler) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a short description of the handler.
   *
   * @return		the description
   */
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder(getClass().getName() + "\n");
    for (PropertyContainer cont : getContainers())
      result.append(" - " + cont.getPath().getFullPath() + "\n");

    return result.toString();
  }
}
