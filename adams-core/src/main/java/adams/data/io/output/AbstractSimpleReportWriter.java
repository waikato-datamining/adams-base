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
 * AbstractSimpleReportWriter.java
 * Copyright (C) 2009-2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.io.input.AbstractSimpleReportReader;
import adams.data.report.AbstractField;
import adams.data.report.Report;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

/**
 * Abstract ancestor for writing reports in properties format.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of report to handle
 */
public abstract class AbstractSimpleReportWriter<T extends Report>
  extends AbstractReportWriter<T>
  implements StringReportWriter<T> {

  /** for serialization. */
  private static final long serialVersionUID = 1281189381638349284L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes reports in properties file format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Properties file format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{AbstractSimpleReportReader.FILE_EXTENSION, AbstractSimpleReportReader.FILE_EXTENSION_GZ};
  }

  /**
   * Turns the data into an array of strings (sorted field names).
   *
   * @param data 	the data to convert
   * @return		the generated string array
   * @throws Exception	if conversion fails
   */
  protected String[] toLines(T data) throws Exception {
    String[] 		result;
    Properties		props;
    List<AbstractField>	fields;
    int			i;
    StringWriter	swriter;

    props = new Properties();

    // the parent ID
    props.setInteger(Report.PROPERTY_PARENTID, data.getDatabaseID());

    // transfer properties
    fields = data.getFields();
    for (i = 0; i < fields.size(); i++) {
      props.setProperty(fields.get(i).toString(), data.getValue(fields.get(i)).toString());
      props.setProperty(fields.get(i).toString() + Report.DATATYPE_SUFFIX, fields.get(i).getDataType().toString());
    }

    swriter = new StringWriter();
    props.store(swriter, "Simple report format (= Java properties file format)");
    result = swriter.toString().split("\n");
    Arrays.sort(result);

    return result;
  }

  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  @Override
  protected boolean writeData(T data) {
    boolean		result;
    FileWriter		writer;
    FileOutputStream	fos;
    GZIPOutputStream	gos;
    String[]		lines;

    // write props file
    fos    = null;
    gos    = null;
    writer = null;
    try {
      lines = toLines(data);
      if (m_Output.getName().endsWith(".gz")) {
	fos = new FileOutputStream(m_Output.getAbsolutePath());
	gos = new GZIPOutputStream(fos);
	gos.write(Utils.flatten(lines, "\n").getBytes());
	gos.flush();
      }
      else {
	writer = new FileWriter(m_Output.getAbsolutePath());
	writer.write(Utils.flatten(lines, "\n"));
	writer.flush();
      }
      result = true;
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed to write to " + m_Output, e);
    }
    finally {
      FileUtils.closeQuietly(writer);
      FileUtils.closeQuietly(gos);
      FileUtils.closeQuietly(fos);
    }

    return result;
  }
  /**
   * Performs checks and converts the report to a string.
   *
   * @param data	the data to write
   * @param errors 	for collecting errors
   * @return		the generated data, null in case of failure
   */
  public String write(T data, MessageCollection errors) {
    String[]	lines;

    try {
      lines = toLines(data);
      return Utils.flatten(lines, "\n");
    }
    catch (Exception e) {
      errors.add("Failed to generate a string!", e);
      return null;
    }
  }
}
