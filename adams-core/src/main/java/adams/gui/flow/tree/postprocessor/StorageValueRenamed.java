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
 * StorageValueRenamed.java
 * Copyright (C) 2012-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree.postprocessor;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.flow.control.StorageName;
import adams.flow.core.Actor;
import adams.flow.processor.UpdateStorageName;
import adams.gui.flow.tree.Tree;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Updates all references of the storage value that was renamed.
 * <br><br>
 <!-- globalinfo-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StorageValueRenamed
  extends AbstractEditPostProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -8661419635908219055L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Updates all references of the storage value that was renamed.";
  }

  /**
   * Extracts the variable name (the option) from the actor.
   *
   * @param actor	the actor to get the variable name from
   * @return		the variable name, null if none found
   */
  protected StorageName getStorageName(Actor actor) {
    StorageName			result;
    int				i;
    List<AbstractOption>	options;
    AbstractArgumentOption	option;

    result = null;

    options = actor.getOptionManager().getOptionsList();
    for (i = 0; i < options.size(); i++) {
      if (options.get(i) instanceof AbstractArgumentOption) {
	option = (AbstractArgumentOption) options.get(i);
	if (!option.isMultiple() && option.getBaseClass().equals(StorageName.class)) {
	  result = (StorageName) option.getCurrentValue();
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Checks whether this post processor scheme applies to the current situation.
   *
   * @param oldActor	the old actor
   * @param newActor	the new, updated actor
   * @return		true if this post processor applies to the situation
   */
  @Override
  public boolean applies(Actor parent, Actor oldActor, Actor newActor) {
    boolean	result;
    StorageName	oldName;
    StorageName	newName;

    result = false;

    oldName = getStorageName(oldActor);
    newName = getStorageName(newActor);

    if (oldName != null)
      result = !oldName.equals(newName);

    return result;
  }

  /**
   * Post-processes the tree.
   *
   * @param tree	the tree to post-process
   * @param parent	the parent actor
   * @param oldActor	the old actor
   * @param newActor	the new, updated actor
   * @return		true if tree got modified
   */
  @Override
  protected boolean doPostProcess(Tree tree, Actor parent, Actor oldActor, Actor newActor) {
    boolean			result;
    UpdateStorageName		updater;

    result = false;

    updater = new UpdateStorageName();
    updater.setLoggingLevel(getLoggingLevel());
    updater.setOldName(getStorageName(oldActor).getValue());
    updater.setNewName(getStorageName(newActor).getValue());
    updater.process(tree.getActor());
    if (updater.isModified()) {
      result = true;
      tree.setActor(updater.getModifiedActor());
    }

    return result;
  }
}
