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
 * IsGzipCompressed.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.condition.bool;

import adams.core.io.GzipUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Actor;
import adams.flow.core.Token;

import java.io.File;

/**
 * Checks whether the file or byte array is Gzip compressed.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IsGzipCompressed
  extends AbstractBooleanCondition {

  private static final long serialVersionUID = -6905054669148352248L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the file or byte array is Gzip compressed.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return adams.flow.core.Unknown.class
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class, byte[].class};
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner the owning actor
   * @param token the current token passing through
   * @return the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    if (token.hasPayload(String.class))
      return GzipUtils.isGzipCompressed(new PlaceholderFile(token.getPayload(String.class)));
    else if (token.hasPayload(File.class))
      return GzipUtils.isGzipCompressed(token.getPayload(File.class));
    else if (token.hasPayload(byte[].class))
      return GzipUtils.isGzipCompressed(token.getPayload(byte[].class));
    else
      return false;
  }
}
