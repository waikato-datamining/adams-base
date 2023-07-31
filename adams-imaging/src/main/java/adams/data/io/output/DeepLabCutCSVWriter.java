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
 * DeepLabCutCSVWriter.java
 * Copyright (C) 2021-2023 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.management.User;
import adams.data.io.input.DeepLabCutCSVReader;
import adams.data.report.Report;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;

import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Writes point annotations from multiple reports in DeepLabCut's CSV format.<br>
 * Expects the filename to be stored in the 'File' report field.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-output &lt;adams.core.io.PlaceholderFile&gt; (property: output)
 * &nbsp;&nbsp;&nbsp;The file to write the report to.
 * &nbsp;&nbsp;&nbsp;default: ${TMP}&#47;out.csv
 * </pre>
 *
 * <pre>-file-prefix &lt;java.lang.String&gt; (property: filePrefix)
 * &nbsp;&nbsp;&nbsp;The file prefix (ie path) used in the output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-individual &lt;adams.core.base.BaseString&gt; [-individual ...] (property: individuals)
 * &nbsp;&nbsp;&nbsp;The individuals for multi-animal output, leave empty for single animal output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-label &lt;adams.core.base.BaseString&gt; [-label ...] (property: labels)
 * &nbsp;&nbsp;&nbsp;The labels to output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Point.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DeepLabCutCSVWriter
  extends AbstractMultiReportWriter<Report>
  implements ObjectPrefixHandler {

  private static final long serialVersionUID = -2029708376194967647L;

  /** the file prefix to use. */
  protected String m_FilePrefix;

  /** the number of individuals. */
  protected BaseString[] m_Individuals;

  /** the labels to output. */
  protected BaseString[] m_Labels;

  /** the prefix to use. */
  protected String m_Prefix;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes point annotations from multiple reports in DeepLabCut's CSV format.\n"
      + "Expects the filename to be stored in the '" + DeepLabCutCSVReader.KEY_FILENAME + "' report field.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "file-prefix", "filePrefix",
      "");

    m_OptionManager.add(
      "individual", "individuals",
      new BaseString[0]);

    m_OptionManager.add(
      "label", "labels",
      new BaseString[0]);

    m_OptionManager.add(
      "prefix", "prefix",
      "Point.");

  }

  /**
   * Sets the file prefix (ie path) to use.
   *
   * @param value 	the prefix
   */
  public void setFilePrefix(String value) {
    m_FilePrefix = value;
    reset();
  }

  /**
   * Returns the file prefix (ie path) used.
   *
   * @return 		the prefix
   */
  public String getFilePrefix() {
    return m_FilePrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filePrefixTipText() {
    return "The file prefix (ie path) used in the output.";
  }

  /**
   * Sets the names of the number of individuals to output (leave empty for single-animal output).
   *
   * @param value 	the individuals
   */
  public void setIndividuals(BaseString[] value) {
    m_Individuals = value;
    reset();
  }

  /**
   * Returns the names of the individuals to output (leave empty for single-animal output).
   *
   * @return 		the individuals
   */
  public BaseString[] getIndividuals() {
    return m_Individuals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String individualsTipText() {
    return "The individuals for multi-animal output, leave empty for single animal output.";
  }

  /**
   * Sets the labels to output.
   *
   * @param value 	the labels
   */
  public void setLabels(BaseString[] value) {
    m_Labels = value;
    reset();
  }

  /**
   * Returns the labels to output.
   *
   * @return 		the labels
   */
  public BaseString[] getLabels() {
    return m_Labels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelsTipText() {
    return "The labels to output.";
  }

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  @Override
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
   */
  @Override
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String prefixTipText() {
    return "The report field prefix used in the report.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return new DeepLabCutCSVReader().getFormatDescription();
  }

  /**
   * Returns the extension of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new DeepLabCutCSVReader().getFormatExtensions();
  }

  /**
   * Returns the class of the report that the writer supports.
   *
   * @return the class
   */
  @Override
  public Class getReportClass() {
    return Report.class;
  }

  /**
   * Performs the actual writing.
   *
   * @param data the data to write
   * @return true if successfully written
   */
  @Override
  protected boolean writeData(Report[] data) {
    SpreadSheet			sheet;
    Row				row;
    CsvSpreadSheetWriter	writer;
    int				i;
    int				n;
    String			file;
    String			filePrefix;
    LocatedObjects 		objs;
    Map<String,Integer>		pos;
    int				col;
    String			label;
    boolean			multi;
    int				numIndividuals;
    String			individual;
    String			key;

    sheet = new DefaultSpreadSheet();

    multi          = (m_Individuals.length > 0);
    numIndividuals = (multi ? m_Individuals.length : 1);

    // header
    row = sheet.getHeaderRow();
    row.addCell("" + 0).setContentAsString("scorer");
    for (i = 0; i < numIndividuals * m_Labels.length * 2; i++)
      row.addCell("" + (i+1)).setContentAsString(User.getName());

    // data
    // individuals
    if (multi) {
      row = sheet.addRow();
      row.addCell(0).setContentAsString("individuals");
      col = 1;
      for (n = 0; n < numIndividuals; n++) {
	for (i = 0; i < m_Labels.length*2; i++) {
	  row.addCell(col).setContentAsString(m_Individuals[n].getValue());
	  col++;
	}
      }
    }

    // bodyparts
    row = sheet.addRow();
    row.addCell(0).setContentAsString("bodyparts");
    col = 1;
    for (n = 0; n < numIndividuals; n++) {
      for (i = 0; i < m_Labels.length; i++) {
	row.addCell(col).setContentAsString(m_Labels[i].getValue());
	col++;
	row.addCell(col).setContentAsString(m_Labels[i].getValue());
	col++;
      }
    }
    // coords
    pos = new HashMap<>();
    row = sheet.addRow();
    row.addCell(0).setContentAsString("coords");
    col = 1;
    for (n = 0; n < numIndividuals; n++) {
      for (i = 0; i < m_Labels.length; i++) {
        key = m_Labels[i].getValue();
        if (multi)
	  key = m_Individuals[n].getValue() + "-" + key;
        pos.put(key, col);
	row.addCell(col).setContentAsString("x");
	col++;
	row.addCell(col).setContentAsString("y");
	col++;
      }
    }

    // add annotations
    for (Report report: data) {
      row = sheet.addRow();

      // file
      if (report.hasValue(DeepLabCutCSVReader.KEY_FILENAME))
        file = report.getStringValue(DeepLabCutCSVReader.KEY_FILENAME);
      else
        file = "";
      if (!m_FilePrefix.isEmpty()) {
        filePrefix = FileUtils.useForwardSlashes(m_FilePrefix);
        if (!filePrefix.endsWith("/"))
          filePrefix += "/";
        if (!file.isEmpty())
	  file = filePrefix + new PlaceholderFile(file).getName();
        else
          file = filePrefix;
      }
      row.addCell(0).setContentAsString(file);

      // labels
      objs = LocatedObjects.fromReport(report, m_Prefix);
      for (LocatedObject obj: objs) {
        key = "" + obj.getMetaData().getOrDefault("type", "");
        if (multi)
          key = obj.getMetaData().getOrDefault("individual", "") + "-" + key;
        if (pos.containsKey(key)) {
          row.addCell(pos.get(key)).setContent(obj.getX());
          row.addCell(pos.get(key) + 1).setContent(obj.getY());
	}
      }
    }

    writer = new CsvSpreadSheetWriter();
    writer.setQuoteCharacter("");
    return writer.write(sheet, m_Output);
  }
}
