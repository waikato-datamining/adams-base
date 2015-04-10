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
 * AbstractPropertyUpdater.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.QuickInfoHelper;
import adams.gui.goe.PropertyPath;
import adams.gui.goe.PropertyPath.PropertyContainer;

/**
 * Abstract ancestor for actors that manipulate properties of callable actors,
 * e.g., WEKA classes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCallableActorPropertyUpdater
  extends AbstractPropertyUpdater {

  /** for serialization. */
  private static final long serialVersionUID = 8068932300654252910L;

  /** the name of the global actor. */
  protected CallableActorReference m_ActorName;

  /** the callable actor to update the property for. */
  protected transient AbstractActor m_CallableActor;

  /** the property container of the property to update. */
  protected transient PropertyContainer m_Container;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "actor-name", "actorName",
	    new CallableActorReference("unknown"));
  }

  /**
   * Sets the name of the actor to update.
   *
   * @param value	the name
   */
  public void setActorName(CallableActorReference value) {
    m_ActorName = value;
    reset();
  }

  /**
   * Returns the name of the actor to update.
   *
   * @return		the name
   */
  public CallableActorReference getActorName() {
    return m_ActorName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorNameTipText() {
    return "The name of the callable actor to update the property for.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "actorName", m_ActorName, ", name: ");

    return result;
  }

  /**
   * Updates the property.
   *
   * @param s		the string to set
   */
  protected void updateProperty(String s) {
    Object	value;

    value = PropertyHelper.convertValue(m_Container, s);

    // could we convert the value?
    if (value != null) {
      PropertyPath.setValue(m_CallableActor, m_Property, value);
      if (isLoggingEnabled())
	getLogger().info("Property '" + m_Property + "' of '" + m_ActorName + "' changed to: " + value);
    }
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    CallableActorHelper	helper;
    Class		cls;

    result = super.setUp();

    if (result == null) {
      helper        = new CallableActorHelper();
      m_CallableActor = helper.findCallableActorRecursive(this, m_ActorName);
      if (m_CallableActor == null) {
	result = "Cannot find callable actor '" + m_ActorName + "'!";
      }
      else {
	m_Container = PropertyPath.find(m_CallableActor, m_Property);
	if (m_Container == null) {
	  result = "Cannot find property '" + m_Property + "' in callable actor '" + m_ActorName + "'!";
	}
	else {
	  cls = m_Container.getReadMethod().getReturnType();
	  if (cls.isArray())
	    result = "Property '" + m_Property + "' is an array!";
	}
      }
    }

    return result;
  }
}
