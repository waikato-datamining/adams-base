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
 * SerializedObjectAppender.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.appendarchive;

import adams.core.SerializationHelper;
import adams.core.io.ArchiveManager;
import adams.core.logging.LoggingHelper;

/**
 * Serializes objects and adds them to the archive.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SerializedObjectAppender
  extends AbstractArchiveObjectAppender {

  private static final long serialVersionUID = 7835224948738910363L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Serializes objects and adds them to the archive.";
  }

  /**
   * Whether the appender can handle this particular object.
   *
   * @param obj 	the object to check
   * @return 		true if the object can be handled
   */
  @Override
  public boolean canProcess(Object obj) {
    return true;
  }

  /**
   * Processes the given object.
   *
   * @param manager 	the context
   * @param obj     	the object to process
   * @param name    	the name to use in the archive
   * @return		null if successfully added, otherwise error message
   */
  @Override
  public String process(ArchiveManager manager, Object obj, String name) {
    try {
      manager.add(SerializationHelper.toByteArray(obj), name);
      return null;
    }
    catch (Exception e) {
      return LoggingHelper.handleException(this, "Failed to serialize object into archive!", e);
    }
  }
}
