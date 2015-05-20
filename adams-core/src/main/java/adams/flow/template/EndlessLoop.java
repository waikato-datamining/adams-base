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
 * EndlessLoop.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.flow.condition.bool.True;
import adams.flow.control.Sleep;
import adams.flow.control.WhileLoop;
import adams.flow.core.AbstractActor;
import adams.flow.source.Start;

/**
 <!-- globalinfo-start -->
 * Generates a simple while-loop that goes on forever.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
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
 * @version $Revision$
 */
public class EndlessLoop
  extends AbstractActorTemplate {

  /** for serialization. */
  private static final long serialVersionUID = 4941969321036423043L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Generates a simple while-loop that goes on forever.";
  }

  /**
   * Generates the actor.
   *
   * @return 		the generated acto
   */
  protected AbstractActor doGenerate() {
    WhileLoop	result;

    result = new WhileLoop();
    result.setCondition(new True());
    
    result.add(new Start());
    result.add(new Sleep());

    return result;
  }
}
