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
 * AbstractNotesReader.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser.notes;

import adams.core.MessageCollection;
import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;

import java.util.Map;

/**
 * Ancestor for notes readers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractNotesReader
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
   * Returns the corresponding writer.
   *
   * @return		the writer, null if not available
   */
  public abstract AbstractNotesWriter getCorrespondingWriter();

  /**
   * Reads the notes from the specified file.
   *
   * @param file	the file to read the notes from
   * @param errors 	for recording errors
   * @return		the notes, null if failed to read
   */
  protected abstract Map<String,String> doRead(PlaceholderFile file, MessageCollection errors);

  /**
   * Hook method for checks before reading the notes.
   *
   * @param file	the file to store the notes in
   * @return		null if checks passed, otherwise error message
   */
  protected String check(PlaceholderFile file) {
    if (!file.exists())
      return "File does not exist: " + file;
    return null;
  }

  /**
   * Adds the note if the file exists.
   *
   * @param notes	the notes to update
   * @param dir		the directory the notes are for
   * @param file	the file that must exist
   * @param note	the note for the file
   */
  protected void addIfFileExists(Map<String,String> notes, PlaceholderDirectory dir, String file, String note) {
    PlaceholderFile	phFile;

    phFile = new PlaceholderFile(dir, file);
    if (phFile.exists())
      notes.put(file, note);
  }

  /**
   * Reads the notes from the specified directory. Uses the default notes name.
   *
   * @param dir		the directory to store the notes in
   * @param errors 	for recording errors
   * @return		the notes, null if failed to read
   * @see		#getDefaultFileName()
   */
  public Map<String,String> read(PlaceholderDirectory dir, MessageCollection errors) {
    Map<String,String>	result;
    String		msg;
    PlaceholderFile	file;

    result = null;
    file   = new PlaceholderFile(dir, getDefaultFileName());
    msg    = check(file);
    if (msg == null)
      result = doRead(file, errors);
    else
      errors.add(msg);

    return result;
  }

  /**
   * Reads the notes from the specified file.
   *
   * @param file	the file to read the notes from
   * @param errors 	for recording errors
   * @return		null if successfully written, otherwise error message
   */
  public Map<String,String> read(PlaceholderFile file, MessageCollection errors) {
    Map<String,String>	result;
    String		msg;

    result = null;
    msg    = check(file);
    if (msg == null)
      result = doRead(file, errors);
    else
      errors.add(msg);

    return result;
  }
}
