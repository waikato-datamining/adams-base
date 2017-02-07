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
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

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
 * @version $Revision$
 * @param <T> the type of report to handle
 */
public abstract class AbstractSimpleReportWriter<T extends Report>
  extends AbstractReportWriter<T> {

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
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  @Override
  protected boolean writeData(T data) {
    boolean		result;
    Properties		props;
    FileWriter		writer;
    List<AbstractField>	fields;
    int			i;
    StringWriter	swriter;
    FileOutputStream	fos;
    GZIPOutputStream	gos;
    String[]		lines;

    props = new Properties();

    // the parent ID
    props.setInteger(Report.PROPERTY_PARENTID, data.getDatabaseID());

    // transfer properties
    fields = data.getFields();
    for (i = 0; i < fields.size(); i++) {
      props.setProperty(fields.get(i).toString(), data.getValue(fields.get(i)).toString());
      props.setProperty(fields.get(i).toString() + Report.DATATYPE_SUFFIX, fields.get(i).getDataType().toString());
    }

    // write props file
    fos    = null;
    gos    = null;
    writer = null;
    try {
      swriter = new StringWriter();
      props.store(swriter, "Simple report format (= Java properties file format)");
      lines = swriter.toString().split("\n");
      Arrays.sort(lines);
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
}
