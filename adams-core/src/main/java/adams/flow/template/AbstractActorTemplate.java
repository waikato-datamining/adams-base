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
 * AbstractActorTemplate.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.core.ClassLister;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.flow.core.Actor;

/**
 * Ancestor for generators that use templates for generating Actor
 * objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractActorTemplate
  extends AbstractOptionHandler
  implements ShallowCopySupporter<AbstractActorTemplate> {

  /** for serialization. */
  private static final long serialVersionUID = 4962299214177742036L;

  /** the new name for the generated actor. */
  protected String m_Name;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"name", "name",
        "");
  }

  /**
   * Sets the new name for the actor. Empty string uses the current one.
   *
   * @param value	the name
   */
  public void setName(String value) {
    if (value != null) {
      m_Name = value;
      reset();
    }
    else {
      getLogger().severe("Name cannot be null!");
    }
  }

  /**
   * Returns the new name for the actor. Empty String uses the current one.
   *
   * @return		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String nameTipText() {
    return "The new name for the actor; leave empty to use current.";
  }

  /**
   * Hook before generating the actor.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void preGenerate() {
  }

  /**
   * Generates the actor.
   *
   * @return 		the generated acto
   */
  protected abstract Actor doGenerate();

  /**
   * Hook before generating the actor.
   * <br><br>
   * Default implementation just updates the name of the actor, if necessary.
   *
   * @param actor	the actor to post-process
   * @return		the processed actor
   */
  protected Actor postGenerate(Actor actor) {
    if (m_Name.length() > 0)
      actor.setName(m_Name);

    return actor;
  }

  /**
   * Returns the generated actor.
   *
   * @return		the actor
   */
  public Actor generate() {
    Actor	result;

    getLogger().fine("preGenerate");
    preGenerate();

    getLogger().fine("doGenerate");
    result = doGenerate();
    getLogger().fine("  --> generated result? " + (result != null));

    if (result != null) {
      getLogger().fine("postGenerate");
      result = postGenerate(result);
      getLogger().fine("  --> generated result? " + (result != null));
    }

    return result;
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractActorTemplate shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractActorTemplate shallowCopy(boolean expand) {
    return (AbstractActorTemplate) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine((AbstractActorTemplate) o));
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a list with classnames of template generator schemes.
   *
   * @return		the template generator classnames
   */
  public static String[] getBaselineCorrections() {
    return ClassLister.getSingleton().getClassnames(AbstractActorTemplate.class);
  }

  /**
   * Instantiates the template generator scheme with the given options.
   *
   * @param classname	the classname of the template generator scheme to instantiate
   * @param options	the options for the template generator scheme
   * @return		the instantiated template generator scheme or null if an error occurred
   */
  public static AbstractActorTemplate forName(String classname, String[] options) {
    AbstractActorTemplate	result;

    try {
      result = (AbstractActorTemplate) OptionUtils.forName(AbstractActorTemplate.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the template generator scheme from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			template generator scheme to instantiate
   * @return		the instantiated template generator scheme
   * 			or null if an error occurred
   */
  public static AbstractActorTemplate forCommandLine(String cmdline) {
    return (AbstractActorTemplate) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
