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

package adams.gui.flow.tree.record.enclose;

import adams.core.DateUtils;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.env.Environment;
import adams.flow.core.ActorHandler;
import adams.gui.flow.tree.Tree;

import javax.swing.tree.TreePath;
import java.io.File;
import java.util.Date;

/**
 * Simply logs what actors get enclosed.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SimpleLog
  extends AbstractRecordActorEnclosed {

  private static final long serialVersionUID = -6799588540903263127L;

  /** the file to write the "add" history to .*/
  public final static String FILENAME = "FlowEncloseHistory.csv";

  /**
   * Returns whether the recording is active.
   *
   * @param tree	the current tree
   * @return		true if active
   */
  @Override
  public boolean isEnabled(Tree tree) {
    return tree.getRecordEnclose();
  }

  /**
   * Records the enclosing action.
   *
   * @param tree	the context
   * @param paths	the paths to add
   * @param handler 	the handler to enclose the paths with
   */
  protected void doRecord(Tree tree, TreePath[] paths, ActorHandler handler) {
    StringBuilder	line;
    String		filename;

    filename = Environment.getInstance().getHome() + File.separator + FILENAME;

    // header?
    if (!new File(filename).exists()) {
      line = new StringBuilder();
      line.append("Timestamp");
      line.append(",");
      line.append("Handler");
      line.append(",");
      line.append("# Actors");
      FileUtils.writeToFile(filename, line, false);
    }

    line = new StringBuilder();
    line.append(DateUtils.getTimestampFormatterMsecs().format(new Date()));
    line.append(",");
    line.append(Utils.classToString(handler));
    line.append(",");
    line.append(paths.length);
    FileUtils.writeToFile(filename, line, true);
  }
}
