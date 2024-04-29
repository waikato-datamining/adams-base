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
 * AbstractNotesWriter.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser.notes;

import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Ancestor for notes writers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractNotesWriter
  extends AbstractOptionHandler
  implements FileFormatHandler {

  private static final long serialVersionUID = 1084973275401249160L;

  /**
   * Returns the default file name for the notes file.
   *
   * @return		the name, no path
   */
  protected abstract String getDefaultFileName();

  /**
   * Returns the corresponding reader.
   *
   * @return		the reader, null if not available
   */
  public abstract AbstractNotesReader getCorrespondingReader();

  /**
   * Hook method for checks before writing the notes.
   *
   * @param notes	the notes to write
   * @param file	the file to store the notes in
   * @return		null if checks passed, otherwise error message
   */
  protected String check(Map<String,String> notes, PlaceholderFile file) {
    if (!file.getParentFile().exists())
      return "Directory does not exist: " + file.getParentFile();
    if (!file.getParentFile().isDirectory())
      return "Specified directory is not an actual directory: " + file.getParentFile();
    return null;
  }

  /**
   * Sorts the files (keys) and returns them as list.
   *
   * @param notes	the notes to get the files/keys from
   * @return		the sorted files/keys
   */
  protected List<String> getSortedFiles(Map<String,String> notes) {
    List<String>	result;

    result = new ArrayList<>(notes.keySet());
    Collections.sort(result);

    return result;
  }

  /**
   * Writes the notes to the specified file.
   *
   * @param notes	the notes to write
   * @param file	the file to store the notes in
   * @return		null if successfully written, otherwise error message
   */
  protected abstract String doWrite(Map<String,String> notes, PlaceholderFile file);

  /**
   * Writes the notes to the specified directory. Uses the default notes name.
   *
   * @param notes	the notes to write
   * @param dir		the directory to store the notes in
   * @return		null if successfully written, otherwise error message
   * @see		#getDefaultFileName()
   */
  public String write(Map<String,String> notes, PlaceholderDirectory dir) {
    String		result;
    PlaceholderFile	file;

    file = new PlaceholderFile(dir, getDefaultFileName());
    result = check(notes, file);
    if (result == null)
      result = doWrite(notes, file);

    return result;
  }

  /**
   * Writes the notes to the specified file.
   *
   * @param notes	the notes to write
   * @param file	the file to store the notes in
   * @return		null if successfully written, otherwise error message
   */
  public String write(Map<String,String> notes, PlaceholderFile file) {
    String	result;

    result = check(notes, file);
    if (result == null)
      result = doWrite(notes, file);

    return result;
  }
}
