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
 * ListActorUsage.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.processor;

import adams.core.Utils;
import adams.core.base.BaseClassname;
import adams.core.option.AbstractOption;
import adams.core.option.OptionTraversalPath;
import adams.flow.core.Actor;
import adams.flow.standalone.SetVariable;
import nz.ac.waikato.cms.locator.ClassLocator;

/**
 <!-- globalinfo-start -->
 * Looks for all the occurrences of the specified actor class.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-actor-class &lt;adams.core.base.BaseClassname&gt; (property: actorClass)
 * &nbsp;&nbsp;&nbsp;The actor class to look for.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.standalone.SetVariable
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ListActorUsage
  extends AbstractActorListingProcessor {

  private static final long serialVersionUID = 3925071321732277210L;

  /** the actor class to look for. */
  protected BaseClassname m_ActorClass;

  /** the actual class to look for. */
  protected transient Class m_ActualClass;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Looks for all the occurrences of the specified actor class.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"actor-class", "actorClass",
	new BaseClassname(SetVariable.class));
  }

  /**
   * Sets the actor class to look for.
   *
   * @param value 	the class
   */
  public void setActorClass(BaseClassname value) {
    if ((value != null) && (ClassLocator.hasInterface(Actor.class, value.classValue()))) {
      m_ActorClass = value;
      reset();
    }
    else {
      getLogger().warning("Class needs to implement " + Utils.classToString(Actor.class) + "!");
    }
  }

  /**
   * Returns the actor class to look for.
   *
   * @return 		the class
   */
  public BaseClassname getActorClass() {
    return m_ActorClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorClassTipText() {
    return "The actor class to look for.";
  }

  /**
   * Checks whether the object is valid and should be added to the list.
   *
   * @param option	the current option
   * @param obj		the object to check
   * @param path	the traversal path of properties
   * @return		true if valid
   */
  @Override
  protected boolean isValid(AbstractOption option, Object obj, OptionTraversalPath path) {
    if (m_ActualClass == null)
      m_ActualClass = m_ActorClass.classValue();
    return option.getOwner().getOwner().getClass().equals(m_ActualClass);
  }

  /**
   * Returns the header to use in the dialog, i.e., the one-liner that
   * explains the output.
   *
   * @return		the header, null if no header available
   */
  @Override
  protected String getHeader() {
    return "Locations where " + m_ActorClass + " was found:";
  }

  /**
   * Returns the title for the dialog.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Locations for " + m_ActorClass;
  }
}
