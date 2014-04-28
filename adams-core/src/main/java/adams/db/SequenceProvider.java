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

/**
 * SequenceProvider.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import java.util.Hashtable;
import java.util.Vector;

import adams.data.sequence.XYSequence;

/**
 * Interface for classes that return XYSequences.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface SequenceProvider {

  /**
   * Reloads the templates.
   */
  public void refresh();

  /**
   * Retrieves sequence(s) from a given SQL statement.
   *
   * @param query	the statement to retrieve the sequences
   * @return		the sequence(s)
   */
  public Vector<XYSequence> retrieve(String query);

  /**
   * Retrieves sequence(s) from a given SQL statement.
   *
   * @param query	the statement to retrieve the sequences
   * @param nameX	the name of the column to use as x-axis, if null then
   * 			the first column will be used
   * @return		the sequence(s)
   */
  public Vector<XYSequence> retrieve(String query, String nameX);

  /**
   * Returns all the templates currently stored.
   *
   * @return		the templates (name &lt;-&gt; sql relation)
   */
  public Hashtable<String,String> getTemplates();
}
