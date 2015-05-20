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
 * AbstractListingProcessor.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adams.core.Utils;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.AbstractActor;
import adams.gui.dialog.TextPanel;

/**
 * Ancestor for processors that list stuff.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractListingProcessor
  extends AbstractActorProcessor 
  implements GraphicalOutputProducingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 5159872442351040225L;
  
  /** for database connections. */
  protected List<String> m_List;
  
  /**
   * Checks whether the object is valid and should be added to the list.
   * 
   * @param obj		the object to check
   * @return		true if valid
   */
  protected abstract boolean isValid(Object obj);

  /**
   * Returns the string representation of the object that is added to the list.
   * <br><br>
   * Default implementation only calls the <code>toString()</code> method.
   * 
   * @param obj		the object to turn into a string
   * @return		the string representation, null if to ignore the item
   */
  protected String objectToString(Object obj) {
    return obj.toString();
  }
  
  /**
   * Processes the object.
   * 
   * @param obj		the object 
   */
  protected void process(Object obj) {
    String	item;
    
    if (isValid(obj)) {
      item = objectToString(obj);
      if (item == null)
	return;
      if (isUniqueList() && m_List.contains(item))
	return;
      m_List.add(item);
    }
  }

  /**
   * Returns whether the list should be sorted.
   * 
   * @return		true if the list should get sorted
   */
  protected abstract boolean isSortedList();

  /**
   * Returns whether the list should not contain any duplicates.
   * 
   * @return		true if the list contains no duplicates
   */
  protected abstract boolean isUniqueList();
  
  /**
   * Initializes the list.
   * <br><br>
   * Default implementation creates an empty vector.
   */
  protected void initializeList() {
    m_List = new ArrayList<String>();
  }
  
  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original for
   * 			processors implementing ModifyingProcessor)
   * @see		ModifyingProcessor
   */
  @Override
  protected void processActor(AbstractActor actor) {
    initializeList();
    
    actor.getOptionManager().traverse(new OptionTraverser() {
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	Object current = option.getCurrentValue();
	if (option.isMultiple()) {
	  for (int i = 0; i < Array.getLength(current); i++)
	    process(Array.get(current, i));
	}
	else {
	  process(current);
	}
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	// ignored
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	// ignored
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
    
    finalizeList();
  }

  /**
   * Finishes up the list, e.g., sorts it.
   * 
   * @see		#isSortedList()
   */
  protected void finalizeList() {
    if (isSortedList())
      Collections.sort(m_List);
  }
  
  /**
   * Returns whether graphical output was generated.
   *
   * @return		true if graphical output was generated
   */
  @Override
  public boolean hasGraphicalOutput() {
    return (m_List.size() > 0);
  }

  /**
   * Returns the header to use in the dialog, i.e., the one-liner that
   * explains the output.
   * 
   * @return		the header, null if no header available
   */
  protected abstract String getHeader();
  
  /**
   * Returns the default sie of the dialog.
   * <br><br>
   * The default is 400x300.
   * 
   * @return		the size
   */
  protected Dimension getDefaultSize() {
    return new Dimension(400, 300);
  }
  
  /**
   * Returns the graphical output that was generated.
   *
   * @return		the graphical output
   */
  @Override
  public Component getGraphicalOutput() {
    TextPanel	result;
    
    result = new TextPanel();
    result.setPreferredSize(getDefaultSize());
    result.setEditable(false);
    result.setContent(Utils.flatten(m_List, "\n"));
    result.setInfoText(getHeader());
    
    return result;
  }
}
