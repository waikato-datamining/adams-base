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
 * AbstractRecordActorEnclosed.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.record.enclose;

import adams.core.ClassLister;
import adams.core.CleanUpHandler;
import adams.core.Utils;
import adams.core.logging.ConsoleLoggingObject;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.gui.core.ConsolePanel;
import adams.gui.flow.tree.Tree;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor schemes that record when actor(s) got enclosed.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRecordActorEnclosed
    extends ConsoleLoggingObject
    implements CleanUpHandler {

  private static final long serialVersionUID = 6333200461304637302L;

  /** the available recording schemes. */
  protected static List<AbstractRecordActorEnclosed> m_Recorders;

  /**
   * Returns whether the recording is active.
   *
   * @param tree	the current tree
   * @return		true if active
   */
  public abstract boolean isEnabled(Tree tree);

  /**
   * Records the enclosing action.
   *
   * @param tree	the context
   * @param paths	the paths to add
   * @param handler 	the handler to enclose the paths with
   */
  protected abstract void doRecord(Tree tree, TreePath[] paths, ActorHandler handler);

  /**
   * Records the enclosing action.
   *
   * @param tree	the context
   * @param paths	the paths to add
   * @param handler 	the handler to enclose the paths with
   */
  public void record(Tree tree, TreePath[] paths, ActorHandler handler) {
    Actor	addedActor;
    Actor	parentActor;
    Actor	beforeActor;
    Actor	afterActor;

    if (paths == null)
      return;
    if (paths.length == 0)
      return;

    if (!isEnabled(tree))
      return;

    doRecord(tree, paths, handler);
  }

  /**
   * Cleans up data structures, frees up memory.
   * <br/>
   * Default implementation does nothing.
   */
  public void cleanUp() {
  }

  /**
   * Records the actor that was added.
   *
   * @param tree	the context
   * @param paths	the paths to add
   * @param handler 	the handler to enclose the paths with
   */
  public static synchronized void recordAll(Tree tree, TreePath[] paths, ActorHandler handler) {
    Class[]			classes;
    AbstractRecordActorEnclosed rec;

    // initialize list
    if (m_Recorders == null) {
      m_Recorders = new ArrayList<>();
      classes     = ClassLister.getSingleton().getClasses(AbstractRecordActorEnclosed.class);
      for (Class cls: classes) {
	try {
	  rec = (AbstractRecordActorEnclosed) cls.newInstance();
	  m_Recorders.add(rec);
	}
	catch (Exception e) {
	  ConsolePanel.getSingleton().append("Failed to instantiate: " + Utils.classToString(cls), e);
	}
      }
    }

    for (AbstractRecordActorEnclosed recorder: m_Recorders) {
      if (recorder.isEnabled(tree))
	recorder.record(tree, paths, handler);
    }
  }

  /**
   * Cleans up data structures, frees up memory, for all recorders.
   */
  public static void cleanUpAll() {
    if (m_Recorders == null)
      return;
    for (AbstractRecordActorEnclosed recorder: m_Recorders)
      recorder.cleanUp();
  }
}
