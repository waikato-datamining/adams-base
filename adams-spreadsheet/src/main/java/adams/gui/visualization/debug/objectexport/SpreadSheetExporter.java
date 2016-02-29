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
 * SpreadSheetExporter.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.debug.objectexport;

import adams.core.ClassLocator;
import adams.data.io.output.AbstractSpreadSheetWriter;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Exports spreadsheet objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetExporter
  extends AbstractObjectExporter {

  private static final long serialVersionUID = 4899389310274830738L;

  /** the writer to use. */
  protected SpreadSheetWriter m_Writer;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "writer", "writer",
	    new CsvSpreadSheetWriter());
  }

  /**
   * Sets the spreadsheet writer to use.
   *
   * @param value	the writer
   */
  public void setWriter(SpreadSheetWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the spreadsheet writer to use.
   *
   * @return		the writer
   */
  public SpreadSheetWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String writerTipText() {
    return "The spreadsheet writer to use.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  public String getFormatDescription() {
    return "Spreadsheet files";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  public String[] getFormatExtensions() {
    List<String>	result;
    String[] 		classnames;
    SpreadSheetWriter	writer;
    String[]		extensions;

    result     = new ArrayList<>();
    classnames = AbstractSpreadSheetWriter.getWriters();
    for (String classname: classnames) {
      try {
	writer     = (SpreadSheetWriter) Class.forName(classname).newInstance();
	extensions = writer.getFormatExtensions();
	for (String extension: extensions) {
	  if (!result.contains(extension))
	    result.add(extension);
	}
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to instantiate '" + classname + "'!", e);
      }
    }

    Collections.sort(result);

    return result.toArray(new String[result.size()]);
  }

  /**
   * Checks whether the exporter can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the exporter can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return (ClassLocator.hasInterface(SpreadSheet.class, cls))
      || (ClassLocator.hasInterface(SpreadSheetSupporter.class, cls));
  }

  /**
   * Performs the actual export.
   *
   * @param obj		the object to export
   * @param file	the file to export to
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExport(Object obj, File file) {
    SpreadSheet 	sheet;

    if (obj instanceof SpreadSheet)
      sheet = (SpreadSheet) obj;
    else
      sheet = ((SpreadSheetSupporter) obj).toSpreadSheet();

    if (!m_Writer.write(sheet, file))
      return "Failed to export spreadsheet to '" + file + "'!";
    else
      return null;
  }
}
