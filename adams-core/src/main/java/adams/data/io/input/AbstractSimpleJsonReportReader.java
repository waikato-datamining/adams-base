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
 * AbstractSimpleJsonReportReader.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.report.Report;
import adams.data.report.ReportJsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Abstract ancestor for reports to be written in CSV format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of report to use
 */
public abstract class AbstractSimpleJsonReportReader<T extends Report>
  extends AbstractReportReader<T> {

  /** for serialization. */
  private static final long serialVersionUID = 8997935127278404969L;

  /** the file extension. */
  public final static String FILE_EXTENSION = "json";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads a report file in JSON file format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Simple report JSON file format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{FILE_EXTENSION};
  }

  /**
   * Tries to determine the parent ID for the current report.
   *
   * @param report	the report to determine the ID for
   * @return		the parent database ID, -1 if it cannot be determined
   */
  @Override
  protected int determineParentID(Report report) {
    return report.getDatabaseID();
  }

  /**
   * Performs the actual reading.
   *
   * @return		the reports that were read
   */
  @Override
  protected List<T> readData() {
    List<T>		result;
    List<String>	lines;
    String 		input;
    Report		report;
    BufferedReader 	breader;
    JsonParser 		jp;
    JsonElement 	je;

    result = new ArrayList<>();
    result.add(newInstance());
    result.get(0).setLoggingLevel(getLoggingLevel());
    lines = FileUtils.loadFromFile(getInput());
    if (lines != null) {
      input = Utils.flatten(lines, "\n");
      breader = null;
      try {
	breader = new BufferedReader(new StringReader(input));
	jp = new JsonParser();
	je = jp.parse(breader);
	report = ReportJsonUtils.fromJson(je.getAsJsonObject());
	result.get(0).mergeWith(report);
      }
      catch (Exception e) {
        getLogger().log(Level.SEVERE, "Failed to parse JSON!", e);
      }
      finally {
	FileUtils.closeQuietly(breader);
      }
    }

    return result;
  }
}
