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
 * DummyStandalone.java
 * Copyright (C) 2012-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.flow.core.Actor;
import adams.flow.standalone.CallableActors;

/**
 <!-- globalinfo-start -->
 * A dummy standalone template, used as default by the TemplateStandalone actor.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The new name for the actor; leave empty to use current.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DummyStandalone
  extends AbstractActorTemplate {

  /** for serialization. */
  private static final long serialVersionUID = 5827973520910591110L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A dummy standalone template, used as default by the TemplateStandalone actor.";
  }

  /**
   * Whether the flow generated is an interactive one.
   *
   * @return		true if interactive
   */
  @Override
  public boolean isInteractive() {
    return false;
  }

  /**
   * Generates the actor.
   *
   * @return 		the generated actor
   */
  @Override
  protected Actor doGenerate() {
    return new CallableActors();
  }
}
