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
 * AbstractArchiveObjectAppender.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.appendarchive;

import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for classes that append objects to compressed archives.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractArchiveObjectAppender
  extends AbstractOptionHandler
  implements ArchiveObjectAppender {

  private static final long serialVersionUID = -3434670978786012702L;
}
