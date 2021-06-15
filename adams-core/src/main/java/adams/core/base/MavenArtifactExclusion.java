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
 * MavenArtifactExclusion.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.core.base;

/**
 * Encapsulates Maven artifact exclusions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MavenArtifactExclusion
  extends AbstractBaseString {

  private static final long serialVersionUID = 1516586362049381050L;

  public final static String SEPARATOR = ":";

  /**
   * Initializes the string with length 0.
   */
  public MavenArtifactExclusion() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public MavenArtifactExclusion(String s) {
    super(s);
  }

  /**
   * Initializes the object with the artifact coordinates.
   *
   * @param groupId	the group ID
   * @param artifactId 	the artifact ID
   */
  public MavenArtifactExclusion(String groupId, String artifactId) {
    super(groupId + SEPARATOR + artifactId);
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if non-null
   */
  @Override
  public boolean isValid(String value) {
    return (value != null) && (value.split(SEPARATOR).length == 2);
  }

  /**
   * Returns the specified part of the coordinate triplet.
   *
   * @param index	the index from the triplet to return
   * @return		the value or empty string if invalid string or index
   */
  protected String getPart(int index) {
    String[]	parts;

    if (isEmpty())
      return "";

    parts = getValue().split(SEPARATOR);
    if (parts.length != 2)
      return "";

    return parts[index];
  }

  /**
   * Returns the group ID part, if possible.
   *
   * @return		the group ID
   */
  public String groupIdValue() {
    return getPart(0);
  }

  /**
   * Returns the artifact ID part, if possible.
   *
   * @return		the artifact ID
   */
  public String artifactIdValue() {
    return getPart(1);
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return the tool tip
   */
  @Override
  public String getTipText() {
    return "The two coordinates of a Maven artifact exclusion: groupId:artifactId";
  }
}
