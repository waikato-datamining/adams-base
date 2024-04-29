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
 * JsonNotesWriter.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser.notes;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.PrettyPrintingSupporter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * Writes the notes in JSON format.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class JsonNotesWriter
  extends AbstractNotesWriter
  implements PrettyPrintingSupporter {

  private static final long serialVersionUID = -3776637762398687653L;

  /** whether to use pretty printing. */
  protected boolean m_PrettyPrinting;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes the notes in JSON format.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "pretty-printing", "prettyPrinting",
      false);
  }

  /**
   * Sets whether to use pretty-printing or not.
   *
   * @param value	true if to use pretty-printing
   */
  public void setPrettyPrinting(boolean value) {
    m_PrettyPrinting = value;
    reset();
  }

  /**
   * Returns whether pretty-printing is used or not.
   *
   * @return		true if to use pretty-printing
   */
  public boolean getPrettyPrinting() {
    return m_PrettyPrinting;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prettyPrintingTipText() {
    return "If enabled, the JSON is output in pretty-print format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return "JSON notes";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"json"};
  }

  /**
   * Returns the default extension of the format.
   *
   * @return the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return "json";
  }

  /**
   * Returns the default file name for the notes file.
   *
   * @return the name, no path
   */
  @Override
  protected String getDefaultFileName() {
    return "notes.json";
  }

  /**
   * Returns the corresponding reader.
   *
   * @return the reader, null if not available
   */
  @Override
  public AbstractNotesReader getCorrespondingReader() {
    return new JsonNotesReader();
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
    JsonObject 		json;
    GsonBuilder		builder;
    Gson 		gson;
    String		content;

    json = new JsonObject();
    for (String key: getSortedFiles(notes))
      json.addProperty(key, notes.get(key));
    builder = new GsonBuilder();
    if (m_PrettyPrinting)
      builder.setPrettyPrinting();
    gson    = builder.create();
    content = gson.toJson(json);
    result  = FileUtils.writeToFileMsg(file.getAbsolutePath(), content, false, null);

    return result;
  }
}
