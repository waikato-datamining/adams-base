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
 * SimpleTimeseriesWriter.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.DateFormatString;
import adams.data.io.input.SimpleTimeseriesReader;
import adams.data.report.Report;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

/**
 * Writer for the simply timeseries format, CSV-like with preceding comments.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleTimeseriesWriter
  extends AbstractTimeseriesWriter {

  /** for serialization. */
  private static final long serialVersionUID = 2779645040618901178L;

  /** the date format string to use for formatting the timestamp. */
  protected DateFormatString m_TimestampFormat;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writer for the simple timeseries format.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "timestamp-format", "timestampFormat",
	    new DateFormatString(Constants.TIMESTAMP_FORMAT_MSECS));
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Simple timeseries";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{SimpleTimeseriesReader.FILE_FORMAT, SimpleTimeseriesReader.FILE_FORMAT_GZ};
  }

  /**
   * Sets the format to use for the timestamps.
   *
   * @param value 	the format
   */
  public void setTimestampFormat(DateFormatString value) {
    m_TimestampFormat = value;
    reset();
  }

  /**
   * Returns the format to use for the timestamps.
   *
   * @return 		the format
   */
  public DateFormatString getTimestampFormat() {
    return m_TimestampFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String timestampFormatTipText() {
    return "The format to use for the timestamps.";
  }

  /**
   * Performs the actual writing.
   * <br><br>
   * Writes only the first timeseries to the file.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  @Override
  protected boolean writeData(List<Timeseries> data) {
    boolean			result;
    Timeseries			series;
    Iterator<TimeseriesPoint>	iter;
    TimeseriesPoint		point;
    String[]			lines;
    BufferedWriter		writer;
    FileOutputStream            fos;
    FileWriter			fw;
    DateFormat			dformat;
    Report			report;

    writer  = null;
    fw      = null;
    fos     = null;
    dformat = m_TimestampFormat.toDateFormat();
    
    try {
      series = data.get(0);

      if (m_Output.getName().endsWith(".gz")) {
	fos    = new FileOutputStream(m_Output.getAbsolutePath());
	writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(fos)));
      }
      else {
	fw     = new FileWriter(m_Output.getAbsolutePath());
	writer = new BufferedWriter(fw);
      }
      
      // report?
      if (series.hasReport()) {
	report = series.getReport().getClone();
	report.setStringValue("ID", series.getID());
	lines = report.toProperties().toComment().split("\n");
	Arrays.sort(lines);
	writer.write(Utils.flatten(lines, "\n"));
	writer.newLine();
      }

      // header
      writer.write("Timestamp,Value");
      writer.newLine();

      // data points
      iter = series.iterator();
      while (iter.hasNext()) {
	point = iter.next();
	writer.write(Utils.doubleQuote(dformat.format(point.getTimestamp())));
	writer.write(",");
	writer.write(Utils.doubleToString(point.getValue(), 6));
	writer.newLine();
      }
      writer.flush();

      result = true;
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed to write timeseries to: " + m_Output, e);
    }
    finally {
      FileUtils.closeQuietly(writer);
      FileUtils.closeQuietly(fw);
      FileUtils.closeQuietly(fos);
    }

    return result;
  }
}
