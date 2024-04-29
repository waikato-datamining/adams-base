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
 * CsvNotesReader.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser.notes;

import adams.core.MessageCollection;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import java.util.HashMap;
import java.util.Map;

/**
 * Reads notes in CSV format.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class CsvNotesReader
  extends AbstractNotesReader {

  private static final long serialVersionUID = 5325262681062607109L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads notes in CSV format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return getCorrespondingWriter().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return getCorrespondingWriter().getFormatExtensions();
  }

  /**
   * Returns the default extension of the format.
   *
   * @return the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return getCorrespondingWriter().getDefaultFormatExtension();
  }

  /**
   * Returns the default file name for the notes file.
   *
   * @return the name, no path
   */
  @Override
  protected String getDefaultFileName() {
    return getCorrespondingWriter().getDefaultFileName();
  }

  /**
   * Returns the corresponding writer.
   *
   * @return the writer, null if not available
   */
  @Override
  public AbstractNotesWriter getCorrespondingWriter() {
    return new PropertiesNotesWriter();
  }

  /**
   * Reads the notes from the specified file.
   *
   * @param file   the file to read the notes from
   * @param errors for recording errors
   * @return the notes, null if failed to read
   */
  @Override
  protected Map<String, String> doRead(PlaceholderFile file, MessageCollection errors) {
    Map<String, String>		result;
    PlaceholderDirectory	dir;
    CsvSpreadSheetReader	reader;
    SpreadSheet			sheet;

    reader = new CsvSpreadSheetReader();
    sheet  = reader.read(file);
    if (sheet.getColumnCount() != 2) {
      errors.add("Expected two columns, but found " + sheet.getColumnCount() + "!");
      return null;
    }
    result = new HashMap<>();
    dir    = new PlaceholderDirectory(file.getParentFile());
    for (Row row: sheet.rows())
      addIfFileExists(result, dir, row.getCell(0).getContent(), row.getCell(1).getContent());

    return result;
  }
}
