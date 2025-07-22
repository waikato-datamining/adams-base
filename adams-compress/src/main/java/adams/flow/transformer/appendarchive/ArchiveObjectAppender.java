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
 * ArchiveObjectAppender.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.appendarchive;

import adams.core.io.ArchiveManager;
import adams.core.option.OptionHandler;

/**
 * Interface for classes that append objects to archives.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ArchiveObjectAppender
  extends OptionHandler {

  /**
   * Whether the appender can handle this particular object.
   *
   * @param obj		the object to check
   * @return		true if the object can be handled
   */
  public boolean canProcess(Object obj);

  /**
   * Processes the given object.
   *
   * @param manager	the context
   * @param obj		the object to process
   * @param name 	the name to use in the archive
   * @return		null if successfully added, otherwise error message
   */
  public String process(ArchiveManager manager, Object obj, String name);
}
