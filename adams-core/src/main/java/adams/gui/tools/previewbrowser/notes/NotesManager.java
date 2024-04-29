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
 * Notes.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser.notes;

import java.util.HashMap;
import java.util.Map;

/**
 * Global notes manager.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class NotesManager {

  /** the notes per directory. */
  protected Map<String, Map<String,String>> m_Notes;

  /** the singleton. */
  protected static NotesManager m_Singleton;

  /**
   * Initializes the notes manager.
   */
  protected NotesManager() {
    m_Notes = new HashMap<>();
  }

  /**
   * Clears all notes globally.
   */
  public void clear() {
    m_Notes.clear();
  }

  /**
   * Removes the notes for the specified directory.
   *
   * @param dir		the directory to remove the notes for
   */
  public void clear(String dir) {
    m_Notes.remove(dir);
  }

  /**
   * Sets the note for the file in the specified directory.
   *
   * @param dir		the directory the file resides in (absolute path)
   * @param file	the file to add the note for (no path)
   * @param note	the note to add
   */
  public void addNote(String dir, String file, String note) {
    if (!m_Notes.containsKey(dir))
      m_Notes.put(dir, new HashMap<>());
    m_Notes.get(dir).put(file, note);
  }

  /**
   * Removes the note for the file in the specified directory.
   *
   * @param dir		the directory the file resides in (absolute path)
   * @param file	the file to add the note for (no path)
   */
  public void removeNote(String dir, String file) {
    if (m_Notes.containsKey(dir))
      m_Notes.get(dir).remove(file);
  }

  /**
   * Checks whether a note is available for the file.
   *
   * @param dir		the directory the file resides in (absolute path)
   * @param file	the file to check for a note (no path)
   * @return		true if note present
   */
  public boolean hasNote(String dir, String file) {
    if (!m_Notes.containsKey(dir))
      return false;
    return m_Notes.get(dir).containsKey(file);
  }

  /**
   * Returns the note for the file.
   *
   * @param dir		the directory the file resides in (absolute path)
   * @param file	the file to get the note for (no path)
   * @return		the note, null if none available
   */
  public String getNote(String dir, String file) {
    if (!m_Notes.containsKey(dir))
      return null;
    return m_Notes.get(dir).get(file);
  }

  /**
   * Checks whether any notes are available at all.
   *
   * @return		true if notes available
   */
  public boolean hasNotes() {
    boolean	result;

    result = false;
    if (!m_Notes.isEmpty()) {
      for (String dir: m_Notes.keySet()) {
	if (hasNotes(dir)) {
	  result = true;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Checks whether any notes are available for the specified directory.
   *
   * @param dir		the directory to check
   * @return		true if notes available
   */
  public boolean hasNotes(String dir) {
    return m_Notes.containsKey(dir) && !m_Notes.get(dir).isEmpty();
  }

  /**
   * Returns the notes for the specified directory.
   *
   * @param dir		the directory to return the notes for
   * @return		the notes, null if none available
   */
  public Map<String,String> getNotes(String dir) {
    if (!hasNotes(dir))
      return null;
    return m_Notes.get(dir);
  }

  /**
   * Returns the notes manager singleton.
   *
   * @return		the singleton
   */
  public static synchronized NotesManager getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new NotesManager();
    return m_Singleton;
  }
}
