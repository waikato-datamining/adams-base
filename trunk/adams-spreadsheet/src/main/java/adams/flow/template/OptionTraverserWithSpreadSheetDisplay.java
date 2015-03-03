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
 * OptionTraverserWithSpreadSheetDisplay.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.flow.core.MutableActorHandler;
import adams.flow.sink.DisplayPanelManager;
import adams.flow.sink.SpreadSheetDisplay;
import adams.flow.transformer.StringToSpreadSheet;

/**
 <!-- globalinfo-start -->
 * Generates a sub-flow that displays the result of the specified option traversal algorithm, e.g., for displaying currently attached variables.<br/>
 * Displays the results in a table.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
 * <pre>-traverser &lt;adams.core.option.OptionTraverser&gt; (property: traverser)
 * &nbsp;&nbsp;&nbsp;The traverser to use.
 * &nbsp;&nbsp;&nbsp;default: adams.core.option.VariableLister
 * </pre>
 * 
 * <pre>-start &lt;ROOT|PARENT&gt; (property: start)
 * &nbsp;&nbsp;&nbsp;Defines where to start the traversal from.
 * &nbsp;&nbsp;&nbsp;default: ROOT
 * </pre>
 * 
 * <pre>-once (property: once)
 * &nbsp;&nbsp;&nbsp;If enabled, the option traversal gets executed only once.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OptionTraverserWithSpreadSheetDisplay
  extends OptionTraverser {

  /** for serialization. */
  private static final long serialVersionUID = -3635536517699129894L;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	super.globalInfo()
	+ "\nDisplays the results in a table.";
  }

  /**
   * Adds the display actors.
   * 
   * @param handler	the handler to add the display actors to
   */
  @Override
  protected void addDisplay(MutableActorHandler handler) {
    DisplayPanelManager		manager;
    SpreadSheetDisplay		display;
    
    handler.add(new StringToSpreadSheet());
    display = new SpreadSheetDisplay();
    
    if (m_Once) {
      handler.add(display);
    }
    else {
      manager = new DisplayPanelManager();
      manager.setPanelProvider(display);
      handler.add(manager);
    }
  }
}
