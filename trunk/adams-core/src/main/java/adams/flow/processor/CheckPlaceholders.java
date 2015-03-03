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
 * CheckPlaceholders.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import adams.core.ClassLocator;
import adams.core.Placeholders;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.AbstractActor;
import adams.gui.dialog.TextPanel;

/**
 <!-- globalinfo-start -->
 * Checks whether all placeholder files&#47;dirs can be expanded, i.e., whether all placeholders are actually defined.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CheckPlaceholders
  extends AbstractActorProcessor 
  implements GraphicalOutputProducingProcessor, CheckProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -5756293162246129385L;
  
  /** the invalid placeholders (invalid placeholderfile/dir). */
  protected List<String> m_Warnings;
  
  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Checks whether all placeholder files/dirs can be expanded, i.e., "
	+ "whether all placeholders are actually defined.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Warnings = new ArrayList<String>();
  }
  
  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process
   */
  @Override
  protected void processActor(AbstractActor actor) {
    m_Warnings.clear();
    
    actor.getOptionManager().traverse(new OptionTraverser() {
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	// ignored
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	// ignored
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	if (!ClassLocator.isSubclass(PlaceholderFile.class, option.getBaseClass()))
	  return;
	if (option.isMultiple()) {
	  Object array = option.getCurrentValue();
	  for (int i = 0; i < Array.getLength(array); i++) {
	    PlaceholderFile file = (PlaceholderFile) Array.get(array, i);
	    if (!Placeholders.isValidStr(file.getAbsolutePath()))
	      m_Warnings.add(file.toString());
	  }
	}
	else {
	  PlaceholderFile file = (PlaceholderFile) option.getCurrentValue();
	  if (!Placeholders.isValidStr(file.getAbsolutePath()))
	    m_Warnings.add(file.toString());
	}
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
        return true;
      }
      public boolean canRecurse(Object obj) {
	return canRecurse(obj.getClass());
      }
    });
  }
  
  /**
   * Returns whether graphical output was generated.
   *
   * @return		true if graphical output was generated
   */
  public boolean hasGraphicalOutput() {
    return (m_Warnings.size() > 0);
  }

  /**
   * Returns the string that explains the warnings.
   * 
   * @return		the heading for the warnings, null if not available
   */
  public String getWarningHeader() {
    return "Files/directories with invalid placeholders:";
  }

  /**
   * Returns the warnings, if any, resulting from the check.
   * 
   * @return		the warnings, null if no warnings.
   */
  public String getWarnings() {
    if (m_Warnings.size() == 0)
      return null;
    else
      return Utils.flatten(m_Warnings, "\n");
  }

  /**
   * Returns the graphical output that was generated.
   *
   * @return		the graphical output
   */
  public Component getGraphicalOutput() {
    TextPanel	result;
    
    result = new TextPanel();
    result.setTitle("Placeholder check");
    result.setPreferredSize(new Dimension(400, 300));
    result.setEditable(false);
    result.setContent(Utils.flatten(m_Warnings, "\n"));
    result.setInfoText(getWarningHeader());
    
    return result;
  }
}
