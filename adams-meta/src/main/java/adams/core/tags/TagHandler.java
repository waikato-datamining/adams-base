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
 * TagHandler.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.tags;

import adams.core.base.BaseKeyValuePair;

import java.util.List;

/**
 * Interface for classes that handle tags.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface TagHandler {

  /**
   * Adds the tag.
   *
   * @param value	the tag to add
   */
  public void addTag(BaseKeyValuePair value);

  /**
   * Sets the tags (generators may make use of them).
   *
   * @param value	the tags
   */
  public void setTags(BaseKeyValuePair[] value);

  /**
   * Returns the tags (generators may make use of them).
   *
   * @return		the tags
   */
  public BaseKeyValuePair[] getTags();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tagsTipText();

  /**
   * Retrieves all tags, going up in the actor tree, with lower ones overriding
   * ones defined higher up.
   *
   * @return		all tags
   */
  public List<BaseKeyValuePair> getAllTags();
}
