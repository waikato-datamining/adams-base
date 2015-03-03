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
 * ActorClassTreeFilter.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe.classtree;

import java.util.Hashtable;

import adams.core.ClassLocator;
import adams.core.Utils;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.Compatibility;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.gui.core.dotnotationtree.AbstractItemFilter;

/**
 * Filter for actors. Takes the generates/accepts of each actor into account
 * and compares it against the
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorClassTreeFilter
  extends AbstractItemFilter
  implements StrictClassTreeFilter {

  /** the classes that the actor must accept, null if not relevant. */
  protected Class[] m_Accepts;

  /** the classes that the actor must generate, null if not relevant. */
  protected Class[] m_Generates;

  /** for checking the compatibility. */
  protected Compatibility m_Compatibility;

  /** whether standalones are allowed. */
  protected boolean m_StandalonesAllowed;

  /** whether sources are allowed. */
  protected boolean m_SourcesAllowed;

  /** whether we are restricted to classes/interfaces. */
  protected Class[] m_Restrictions;

  /** for caching class/instance relationship. */
  protected static Hashtable<Class,AbstractActor> m_Cache;
  static {
    m_Cache = new Hashtable<Class,AbstractActor>();
  }

  /**
   * Initializes the filter.
   */
  protected void initialize() {
    super.initialize();

    m_Compatibility      = new Compatibility();
    m_Accepts            = null;
    m_Generates          = null;
    m_StandalonesAllowed = true;
    m_SourcesAllowed     = true;
    m_Restrictions       = new Class[0];
  }

  /**
   * Sets whether to use strict or relaxed filtering.
   *
   * @param value	if true strict mode is enabled
   */
  public void setStrict(boolean value) {
    m_Compatibility.setStrict(value);
  }

  /**
   * Returns whether strict or relaxed filtering is used.
   *
   * @return		true if strict mode is enabled
   */
  public boolean isStrict() {
    return m_Compatibility.isStrict();
  }

  /**
   * Sets whether standalones are allowed.
   *
   * @param value	if true then standalones are allowed
   */
  public void setStandalonesAllowed(boolean value) {
    m_StandalonesAllowed = value;
  }

  /**
   * Returns whether standalones are allowed.
   *
   * @return		true if standalones are allowed
   */
  public boolean getStandalonesAllowed() {
    return m_StandalonesAllowed;
  }

  /**
   * Sets whether sources are allowed.
   *
   * @param value	if true then sources are allowed
   */
  public void setSourcesAllowed(boolean value) {
    m_SourcesAllowed = value;
  }

  /**
   * Returns whether sources are allowed.
   *
   * @return		true if sources are allowed
   */
  public boolean getSourcesAllowed() {
    return m_SourcesAllowed;
  }

  /**
   * Sets the classes that the actor must accept.
   *
   * @param value	the classes, null if to ignore
   */
  public void setAccepts(Class[] value) {
    m_Accepts = value;
  }

  /**
   * Returns the classes that the actor must accept.
   *
   * @return		the classes, null if ignored
   */
  public Class[] getAccepts() {
    return m_Accepts;
  }

  /**
   * Sets the classes that the actor must generate.
   *
   * @param value	the classes, null if to ignore
   */
  public void setGenerates(Class[] value) {
    m_Generates = value;
  }

  /**
   * Returns the classes that the actor must generate.
   *
   * @return		the classes, null if ignored
   */
  public Class[] getGenerates() {
    return m_Generates;
  }

  /**
   * Sets the classes/interfaces that are allowed.
   *
   * @param value	the classes, null if to ignore
   */
  public void setRestrictions(Class[] value) {
    m_Restrictions = value;
  }

  /**
   * Returns the classes/interfaces that are allowed.
   *
   * @return		the classes, null if ignored
   */
  public Class[] getRestrictions() {
    return m_Restrictions;
  }

  /**
   * Performs the actual filtering.
   *
   * @param item	the class to check
   * @return		true if class can be displayed in the ClassTree
   */
  protected boolean doFilter(String item) {
    AbstractActor	actor;
    boolean		result;
    Class		cls;

    result = true;

    // nothing to check?
    if ((m_Accepts == null) && (m_Generates == null) && m_StandalonesAllowed && m_SourcesAllowed && (m_Restrictions == null))
      return result;

    try {
      cls = Class.forName(item);
    }
    catch (Exception e) {
      return result;
    }

    synchronized(m_Cache) {
      if (!m_Cache.containsKey(cls)) {
	try {
	  actor = (AbstractActor) cls.newInstance();
	  m_Cache.put(cls, actor);
	}
	catch (Exception e) {
	  // ignored
	  actor = null;
	}
      }
      else {
	actor = m_Cache.get(cls);
      }
    }

    if (actor == null)
      result = false;

    if (result && !m_StandalonesAllowed)
      result = !ActorUtils.isStandalone(actor);

    if (result && !m_SourcesAllowed)
      result = !ActorUtils.isSource(actor);

    if (result && (m_Accepts != null)) {
      result =    (actor instanceof InputConsumer)
               && m_Compatibility.isCompatible(m_Accepts, ((InputConsumer) actor).accepts());
    }

    if (result && (m_Generates != null)) {
      result =    (actor instanceof OutputProducer)
                && m_Compatibility.isCompatible(((OutputProducer) actor).generates(), m_Generates);
    }

    if (result && m_Restrictions != null) {
      for (Class restriction: m_Restrictions) {
	if (restriction.isInterface())
	  result = ClassLocator.hasInterface(restriction, cls);
	else
	  result = ClassLocator.isSubclass(restriction, cls);
	if (!result)
	  break;
      }
    }

    return result;
  }

  /**
   * Returns a short representation of the filter.
   *
   * @return		the representation
   */
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder();
    result.append("enabled=" + isEnabled());
    result.append(", ");
    result.append("accepts=" + (getAccepts() == null ? "null" : Utils.arrayToString(getAccepts(), true)));
    result.append(", ");
    result.append("generates=" + (getGenerates() == null ? "null" : Utils.arrayToString(getGenerates(), true)));
    result.append(", ");
    result.append("standalones=" + m_StandalonesAllowed);

    return result.toString();
  }
}
