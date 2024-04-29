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
 * PropertiesNotesWriter.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser.notes;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;

import java.util.Map;

/**
 * Writes the notes in Java properties format.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PropertiesNotesWriter
  extends AbstractNotesWriter {

  private static final long serialVersionUID = -3776637762398687653L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes the notes in Java properties format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Properties notes";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"props", "properties"};
  }

  /**
   * Returns the default extension of the format.
   *
   * @return the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return "props";
  }

  /**
   * Returns the default file name for the notes file.
   *
   * @return the name, no path
   */
  @Override
  protected String getDefaultFileName() {
    return "notes.props";
  }

  /**
   * Returns the corresponding reader.
   *
   * @return the reader, null if not available
   */
  @Override
  public AbstractNotesReader getCorrespondingReader() {
    return new PropertiesNotesReader();
  }

  /**
   * Writes the notes to the specified file.
   *
   * @param notes the notes to write
   * @param file  the file to store the notes in
   * @return null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(Map<String, String> notes, PlaceholderFile file) {
    Properties	props;

    props = new Properties();
    props.putAll(notes);
    if (props.save(file.getAbsolutePath()))
      return null;
    else
      return "Failed to save to: " + file;
  }
}
