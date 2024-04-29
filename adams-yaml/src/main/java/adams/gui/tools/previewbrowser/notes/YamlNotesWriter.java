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
 * YamlNotesWriter.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser.notes;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

/**
 * Writes the notes in JSON format.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class YamlNotesWriter
  extends AbstractNotesWriter {

  private static final long serialVersionUID = -3776637762398687653L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes the notes in YAML format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return "YAML notes";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"yaml", "yml"};
  }

  /**
   * Returns the default extension of the format.
   *
   * @return the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return "yaml";
  }

  /**
   * Returns the default file name for the notes file.
   *
   * @return the name, no path
   */
  @Override
  protected String getDefaultFileName() {
    return "notes.yaml";
  }

  /**
   * Returns the corresponding reader.
   *
   * @return the reader, null if not available
   */
  @Override
  public AbstractNotesReader getCorrespondingReader() {
    return new YamlNotesReader();
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
    String		result;
    Yaml 		yaml;
    String		content;

    yaml = new Yaml();
    content = yaml.dump(notes);
    result  = FileUtils.writeToFileMsg(file.getAbsolutePath(), content, false, null);

    return result;
  }
}
