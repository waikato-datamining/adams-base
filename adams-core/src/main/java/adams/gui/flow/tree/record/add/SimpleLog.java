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
 * SimpleLog.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.record.add;

import adams.core.io.FileUtils;
import adams.env.Environment;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeOperations;

import java.io.File;

/**
 * Simply logs what actors got added.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SimpleLog
  extends AbstractRecordActorAdded {

  private static final long serialVersionUID = -6799588540903263127L;

  /** the file to write the "add" history to .*/
  public final static String FILENAME = "FlowAddHistory.csv";

  /**
   * Returns whether the recording is active.
   *
   * @param added the node that got added (for context)
   * @return true if active
   */
  @Override
  public boolean isEnabled(Node added) {
    return added.getOwner().getRecordAdd();
  }

  /**
   * Adds details about an actor.
   *
   * @param actor 	the actor to add the details for
   * @param line	the buffer to add the details to
   */
  protected void record(Actor actor, StringBuilder line) {
    if (line.length() > 0)
      line.append(",");

    // class
    if (actor != null)
      line.append(actor.getClass().getName());
    line.append(",");

    // functional
    if (actor != null)
      line.append(ActorUtils.getFunctionalAspect(actor));
    line.append(",");

    // procedural
    if (actor != null)
      line.append(ActorUtils.getProceduralAspect(actor));
    line.append(",");

    // control?
    if (actor != null)
      line.append(ActorUtils.isControlActor(actor) ? "true" : "false");
  }

  /**
   * Records the actor that was added.
   *
   * @param added	the actor that was added
   * @param parent	the parent of the added actor
   * @param before	the immediate actor before the added actor, can be null
   * @param after	the immediate actor after the added actor, can be null
   * @param position	how the actor was added
   */
  protected void record(Actor added, Actor parent, Actor before, Actor after, TreeOperations.InsertPosition position) {
    StringBuilder	line;
    String		filename;

    filename = Environment.getInstance().getHome() + File.separator + FILENAME;

    // header?
    if (!new File(filename).exists()) {
      line = new StringBuilder();
      line.append("Actor-Class");
      line.append(",");
      line.append("Actor-Functional");
      line.append(",");
      line.append("Actor-Procedural");
      line.append(",");
      line.append("Actor-Control");
      line.append(",");
      line.append("Parent-Class");
      line.append(",");
      line.append("Parent-Functional");
      line.append(",");
      line.append("Parent-Procedural");
      line.append(",");
      line.append("Parent-Control");
      line.append(",");
      line.append("Before-Class");
      line.append(",");
      line.append("Before-Functional");
      line.append(",");
      line.append("Before-Procedural");
      line.append(",");
      line.append("Before-Control");
      line.append(",");
      line.append("After-Class");
      line.append(",");
      line.append("After-Functional");
      line.append(",");
      line.append("After-Procedural");
      line.append(",");
      line.append("After-Control");
      line.append(",");
      line.append("Position");
      FileUtils.writeToFile(filename, line, false);
    }

    line = new StringBuilder();
    record(added, line);
    record(parent, line);
    record(before, line);
    record(after, line);
    line.append(",");
    line.append(position.toString());
    FileUtils.writeToFile(filename, line, true);
  }
}
