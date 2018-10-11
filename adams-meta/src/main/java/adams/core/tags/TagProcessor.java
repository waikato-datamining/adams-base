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
 * TagProcessor.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.tags;

import java.util.Set;

/**
 * Interface for classes that make use of tags (see {@link TagHandler}).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see TagHandler
 */
public interface TagProcessor {

  /**
   * Returns the tags that are supported by this class, including an explanation
   * of what they do.
   *
   * @return		the tags with the associated help
   */
  public Set<TagInfo> getSupportedTags();
}
