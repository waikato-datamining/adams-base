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
 * ListAllStorageNames.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.Utils;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;
import adams.flow.control.StorageUser;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.gui.dialog.TextPanel;

import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Lists all storage name occurrences in the flow whether being set or used.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ListAllStorageNames
  extends AbstractActorProcessor 
  implements GraphicalOutputProducingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 737084782888325641L;

  /** the storage names in use. */
  protected List<String> m_StorageNames;
  
  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lists all storage name occurrences in the flow whether being set or used.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_StorageNames = new ArrayList<>();
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process
   */
  @Override
  protected void processActor(AbstractActor actor) {
    final Set<String>   names;

    m_StorageNames.clear();
    names = new HashSet<>();

    actor.getOptionManager().traverse(new OptionTraverser() {
      protected void incrementSetCount(Object obj) {
	if (obj.getClass().isArray()) {
	  for (int i = 0; i < Array.getLength(obj); i++)
	    incrementSetCount(Array.get(obj, i));
	}
	else {
	  if (obj instanceof StorageName)
	    names.add(((StorageName) obj).getValue());
	}
      }
      protected void incrementUsageCount(Object obj) {
	if (obj.getClass().isArray()) {
	  for (int i = 0; i < Array.getLength(obj); i++)
	    incrementUsageCount(Array.get(obj, i));
	}
	else {
	  if (obj instanceof StorageName)
	    names.add(((StorageName) obj).getValue());
	}
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	// ignored
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	// user
	if ((option.getOptionHandler() instanceof StorageUpdater) && (option.getBaseClass() == StorageName.class)) {
	  if (((StorageUpdater) option.getOptionHandler()).isUpdatingStorage())
	    incrementSetCount(option.getCurrentValue());
	  return;
	}
	// user
	else if ((option.getOptionHandler() instanceof StorageUser) && (option.getBaseClass() == StorageName.class)) {
	  if (((StorageUser) option.getOptionHandler()).isUsingStorage())
	    incrementUsageCount(option.getCurrentValue());
	  return;
	}
	// storagename?
	if (option.getBaseClass() == StorageName.class) {
	  incrementUsageCount(option.getCurrentValue());
	}
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
        return true;
      }
      public boolean canRecurse(Object obj) {
	if (obj instanceof Actor)
	  return !((Actor) obj).getSkip() && canRecurse(obj.getClass());
	else
	  return canRecurse(obj.getClass());
      }
    });

    m_StorageNames.addAll(names);
    Collections.sort(m_StorageNames);
  }

  /**
   * Returns whether graphical output was generated.
   *
   * @return		true if graphical output was generated
   */
  public boolean hasGraphicalOutput() {
    return (m_StorageNames.size() > 0);
  }

  /**
   * Returns the title for the dialog.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Storage names";
  }

  /**
   * Returns the graphical output that was generated.
   *
   * @return		the graphical output
   */
  public Component getGraphicalOutput() {
    TextPanel	result;
    
    result = new TextPanel();
    result.setTitle("Storage names");
    result.setPreferredSize(new Dimension(400, 300));
    result.setEditable(false);
    result.setContent(Utils.flatten(m_StorageNames, "\n"));
    
    return result;
  }
}
