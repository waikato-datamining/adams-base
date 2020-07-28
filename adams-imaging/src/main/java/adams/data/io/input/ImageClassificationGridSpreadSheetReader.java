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
 * ImageClassificationGridSpreadSheetReader.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Constants;
import adams.core.Utils;
import adams.data.report.Report;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Turns grid cells in the spreadsheet with labels ('label=score') into object locations.<br>
 * Spreadsheet format:<br>
 * y,x,label1,label2,...<br>
 * 0,0,cat=0.98,,...<br>
 * 0,1,,,...<br>
 * 0,2,dog=0.9,cat=0.2,...
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The file to read and turn into a report.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width to use.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height to use.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageClassificationGridSpreadSheetReader
  extends AbstractReportReader<Report> {

  private static final long serialVersionUID = 2621489607429248730L;

  /** the image width to use. */
  protected int m_Width;

  /** the image height to use. */
  protected int m_Height;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns grid cells in the spreadsheet with labels ('label=score') into object locations.\n"
      + "Spreadsheet format:\n"
      + "y,x,label1,label2,...\n"
      + "0,0,cat=0.98,,...\n"
      + "0,1,,,...\n"
      + "0,2,dog=0.9,cat=0.2,...";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "width", "width",
      1000, 1, null);

    m_OptionManager.add(
      "height", "height",
      1000, 1, null);
  }

  /**
   * Sets the width to use.
   *
   * @param value	the image width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the width to use.
   *
   * @return		the image width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width to use.";
  }

  /**
   * Sets the height to use.
   *
   * @param value	the image height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the height to use.
   *
   * @return		the image height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height to use.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Image classification grid";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"csv"};
  }

  /**
   * Tries to determine the parent ID for the current report.
   *
   * @param report	the report to determine the ID for
   * @return		the parent database ID, -1 if it cannot be determined
   */
  @Override
  protected int determineParentID(Report report) {
    return Constants.NO_ID;
  }

  /**
   * Returns a new instance of the report class in use.
   *
   * @return		the new (empty) report
   */
  @Override
  public Report newInstance() {
    return new Report();
  }

  /**
   * Performs the actual reading.
   *
   * @return		the reports that were read
   */
  @Override
  protected List<Report> readData() {
    List<Report>		result;
    CsvSpreadSheetReader	reader;
    SpreadSheet 		sheet;
    int				colX;
    int				colY;
    SpreadSheetColumnIndex	index;
    List<String>		labels;
    String[]			parts;
    String			label;
    double			score;
    String			labelHighest;
    double			scoreHighest;
    int				x;
    int				y;
    int				cellWidth;
    int				cellHeight;
    TIntList 			colLabels;
    int				i;
    int				numCols;
    int				numRows;
    LocatedObjects		objects;
    LocatedObject		object;

    result = new ArrayList<>();
    reader = new CsvSpreadSheetReader();
    sheet  = reader.read(m_Input);

    if (sheet == null) {
      getLogger().severe("Failed to read spreadsheet: " + m_Input);
      return result;
    }

    // x
    index = new SpreadSheetColumnIndex("x");
    index.setData(sheet);
    colX = index.getIntIndex();
    if (colX == -1) {
      getLogger().severe("Failed to locate column: " + index.getIndex());
      return result;
    }

    // y
    index = new SpreadSheetColumnIndex("y");
    index.setData(sheet);
    colY = index.getIntIndex();
    if (colY == -1) {
      getLogger().severe("Failed to locate column: " + index.getIndex());
      return result;
    }

    // labels
    colLabels = new TIntArrayList();
    for (i = 0; i < sheet.getColumnCount(); i++) {
      if ((i == colX) || (i == colY))
        continue;
      colLabels.add(i);
    }
    labels = new ArrayList<>();

    // cell dims
    numCols = -1;
    numRows = -1;
    for (Row row: sheet.rows()) {
      labels.clear();
      if (!row.hasCell(colX) || !row.hasCell(colY))
        continue;
      if (!row.getCell(colX).isLong() || !row.getCell(colY).isLong())
        continue;
      x = row.getCell(colX).toLong().intValue();
      y = row.getCell(colY).toLong().intValue();
      numCols = Math.max(x, numCols);
      numRows = Math.max(y, numRows);
    }
    numCols++;
    numRows++;
    cellWidth  = Math.round(m_Width / numCols);
    cellHeight = Math.round(m_Height / numRows);

    // data
    objects = new LocatedObjects();
    for (Row row: sheet.rows()) {
      if (!row.hasCell(colX) || !row.hasCell(colY))
	continue;
      if (!row.getCell(colX).isLong() || !row.getCell(colY).isLong())
	continue;
      x = row.getCell(colX).toLong().intValue();
      y = row.getCell(colY).toLong().intValue();
      labels.clear();
      for (i = 0; i < colLabels.size(); i++) {
        if (!row.getCell(colLabels.get(i)).isMissing() && !row.getCell(colLabels.get(i)).getContent().trim().isEmpty())
	  labels.add(row.getCell(colLabels.get(i)).getContent().trim());
      }
      if (labels.size() > 0) {
	labelHighest = "";
	scoreHighest = 0;
        for (String l: labels) {
          if (l.contains("=")) {
	    parts = l.split("=");
	    if ((parts.length == 2) && Utils.isDouble(parts[1])) {
	      label = parts[0];
	      score = Double.parseDouble(parts[1]);
	      if (score > scoreHighest) {
	        scoreHighest = score;
	        labelHighest = label;
	      }
	    }
	  }
	}
	if (!labelHighest.isEmpty()) {
          object = new LocatedObject(x*cellWidth, y*cellHeight, cellWidth, cellHeight);
          object.getMetaData().put("type", labelHighest);
          object.getMetaData().put("score", scoreHighest);
          object.getMetaData().put("labels", Utils.flatten(labels, ","));
          objects.add(object);
	}
      }
    }

    result.add(objects.toReport("Object."));

    return result;
  }
}
