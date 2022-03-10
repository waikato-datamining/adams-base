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
 * AbstractRecordActorAdded.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.record.add;

import adams.core.ClassLister;
import adams.core.CleanUpHandler;
import adams.core.Utils;
import adams.core.logging.ConsoleLoggingObject;
import adams.flow.core.Actor;
import adams.gui.core.ConsolePanel;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor schemes that record when an actor got added.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractRecordActorAdded
    extends ConsoleLoggingObject
    implements CleanUpHandler {

  private static final long serialVersionUID = 6333200461304637302L;

  /** the available recording schemes. */
  protected static List<AbstractRecordActorAdded> m_Recorders;

  /**
   * Returns whether the recording is active.
   *
   * @param added	the node that got added (for context)
   * @return		true if active
   */
  public abstract boolean isEnabled(Node added);

  /**
   * Records the actor that was added.
   *
   * @param added	the actor that was added
   * @param parent	the parent of the added actor
   * @param before	the immediate actor before the added actor, can be null
   * @param after	the immediate actor after the added actor, can be null
   * @param position	how the actor was added
   */
  protected abstract void record(Actor added, Actor parent, Actor before, Actor after, TreeOperations.InsertPosition position);

  /**
   * Records the actor that was added.
   *
   * @param added	the node that was added
   * @param parent	the parent of the added actor
   * @param position	how the actor was added
   */
  public void record(Node added, Node parent, TreeOperations.InsertPosition position) {
    Actor	addedActor;
    Actor	parentActor;
    Actor	beforeActor;
    Actor	afterActor;

    if (!isEnabled(added))
      return;

    addedActor  = added.getActor();
    parentActor = parent.getActor();
    beforeActor = null;
    afterActor  = null;
    if (added.getPreviousSibling() != null)
      beforeActor = ((Node) added.getPreviousSibling()).getActor();
    if (added.getNextSibling() != null)
      afterActor = ((Node) added.getNextSibling()).getActor();

    record(addedActor, parentActor, beforeActor, afterActor, position);
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
   * @param added	the node that was added
   * @param parent	the parent of the added actor
   * @param position	how the actor was added
   */
  public static synchronized void recordAll(Node added, Node parent, TreeOperations.InsertPosition position) {
    Class[]			classes;
    AbstractRecordActorAdded	rec;

    // initialize list
    if (m_Recorders == null) {
      m_Recorders = new ArrayList<>();
      classes     = ClassLister.getSingleton().getClasses(AbstractRecordActorAdded.class);
      for (Class cls: classes) {
        try {
          rec = (AbstractRecordActorAdded) cls.newInstance();
          m_Recorders.add(rec);
	}
        catch (Exception e) {
	  ConsolePanel.getSingleton().append("Failled to instantiate: " + Utils.classToString(cls), e);
	}
      }
    }

    for (AbstractRecordActorAdded recorder: m_Recorders) {
      if (recorder.isEnabled(added))
        recorder.record(added, parent, position);
    }
  }

  /**
   * Cleans up data structures, frees up memory, for all recorders.
   */
  public static void cleanUpAll() {
    if (m_Recorders == null)
      return;
    for (AbstractRecordActorAdded recorder: m_Recorders)
      recorder.cleanUp();
  }
}
