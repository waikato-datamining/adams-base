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
 * SimpleTimeseriesReader.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.DateFormatString;
import adams.data.report.Report;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

/**
 * Reader for the simple timeseries data format, CSV-like with preceding comments.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleTimeseriesReader
  extends AbstractTimeseriesReader {

  /** for serialization. */
  private static final long serialVersionUID = 1598168889943642045L;

  /** the file format extension. */
  public final static String FILE_FORMAT = "sts";

  /** the file format extension (gzipped). */
  public final static String FILE_FORMAT_GZ = "sts.gz";

  /** the date format string to use for formatting the timestamp. */
  protected DateFormatString m_TimestampFormat;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reader for the simple timeseries format.";
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
    return new String[]{FILE_FORMAT, FILE_FORMAT_GZ};
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
   * Performs the actual reading.
   */
  @Override
  protected void readData() {
    Timeseries		series;
    TimeseriesPoint	point;
    String		line;
    List<String>	content;
    List<String>	report;
    Report		sd;
    BufferedReader	reader;
    FileInputStream	fis;
    FileReader		fr;
    String[]		parts;
    DateFormat		dformat;

    reader  = null;
    fr      = null;
    fis     = null;
    dformat = m_TimestampFormat.toDateFormat();
    series  = new Timeseries();
    
    try {
      if (m_Input.getName().endsWith(".gz")) {
	fis    = new FileInputStream(m_Input.getAbsolutePath());
	reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(fis)));
      }
      else {
	fr     = new FileReader(m_Input.getAbsolutePath());
	reader = new BufferedReader(fr);
      }
      
      // read whole file
      content  = new ArrayList<String>();
      while (((line = reader.readLine()) != null))
	content.add(line);

      // report?
      report = new ArrayList<String>();
      while ((content.size() > 0) && content.get(0).startsWith(Properties.COMMENT)) {
	report.add(content.get(0));
	content.remove(0);
      }
      if (report.size() > 0) {
	sd = Report.parseProperties(Properties.fromComment(Utils.flatten(report, "\n")));
	if (sd != null) {
	  series.setID(sd.getStringValue("ID"));
	  series.setReport(sd);
	}
      }

      // header - ignored
      content.remove(0);

      // data points
      while (content.size() > 0) {
	line   = content.get(0).trim();
	content.remove(0);
	if (line.length() == 0)
	  continue;
	parts = line.split(",");
	point = new TimeseriesPoint(dformat.parse(Utils.unDoubleQuote(parts[0])), Double.parseDouble(parts[1]));
	series.add(point);
      }
      
      m_ReadData.add(series);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read timeseries: " + m_Input, e);
    }
    finally {
      FileUtils.closeQuietly(reader);
      FileUtils.closeQuietly(fr);
      FileUtils.closeQuietly(fis);
    }
  }
}
