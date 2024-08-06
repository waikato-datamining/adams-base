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
 * InstallOfficial.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.wekapackagemanageraction;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import weka.core.WekaPackageManager;

import java.util.logging.Level;

/**
 * Action that installs official packages via their name and (optional) version.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class InstallOfficial
  extends AbstractWekaPackageManagerAction<SpreadSheet, SpreadSheet>{

  private static final long serialVersionUID = 551922326118868830L;

  /** the column with the name. */
  protected SpreadSheetColumnIndex m_ColName;

  /** the column with the version. */
  protected SpreadSheetColumnIndex m_ColVersion;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Action that installs official packages via their name and (optional) version.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "col-name", "colName",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "col-version", "colVersion",
      new SpreadSheetColumnIndex(""));
  }

  /**
   * Sets the spreadsheet column with the name.
   *
   * @param value	the column
   */
  public void setColName(SpreadSheetColumnIndex value) {
    m_ColName = value;
    reset();
  }

  /**
   * Returns the spreadsheet column with the name.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColName() {
    return m_ColName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colNameTipText() {
    return "The spreadsheet column with the package name.";
  }

  /**
   * Sets the (optional) spreadsheet column with the version.
   *
   * @param value	the column
   */
  public void setColVersion(SpreadSheetColumnIndex value) {
    m_ColVersion = value;
    reset();
  }

  /**
   * Returns the (optional) spreadsheet column with the version.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getColVersion() {
    return m_ColVersion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colVersionTipText() {
    return "The spreadsheet column with the (optional) package version.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String 	result;

    result = QuickInfoHelper.toString(this, "colName", m_ColName, "name: ");
    result += QuickInfoHelper.toString(this, "colVersion", (m_ColVersion.isEmpty() ? "-none-" : m_ColVersion), ", version: ");

    return result;
  }

  /**
   * The type of data the action accepts.
   *
   * @return the input type
   */
  @Override
  public Class accepts() {
    return SpreadSheet.class;
  }

  /**
   * The type of data the action generates.
   *
   * @return the output type
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Executes the action.
   *
   * @param input  the input to process
   * @param errors for collecting errors
   * @return the generated output, null if failed to generated
   */
  @Override
  public SpreadSheet doExecute(SpreadSheet input, MessageCollection errors) {
    SpreadSheet		result;
    Row			row;
    int			i;
    int			colName;
    int			colVersion;
    int			colInstalled;
    String		name;
    String		version;

    result = input.getClone();
    result.insertColumn(result.getColumnCount(), "Installed");
    colInstalled = result.getColumnCount() - 1;
    m_ColName.setData(result);
    colName = m_ColName.getIntIndex();
    if (colName == -1) {
      errors.add("Column with name not found: " + m_ColName.getIndex());
      return null;
    }
    m_ColVersion.setData(result);
    colVersion = m_ColVersion.getIntIndex();

    for (i = 0; i < result.getRowCount(); i++) {
      if (m_FlowContext.isStopped()) {
	result = null;
	break;
      }
      row = result.getRow(i);
      name = row.getCell(colName).getContent();
      version = null;
      if (colVersion > -1)
	version = row.getCell(colVersion).getContent();
      try {
	getLogger().info("Installing: " + name + "/" + (version == null ? "LATEST" : version));
	WekaPackageManager.installPackageFromRepository(name, version);
	row.getCell(colInstalled).setContent(true);
      }
      catch (Exception e) {
	getLogger().log(Level.WARNING, "Failed to install: " + name + "/" + (version == null ? "LATEST" : version), e);
	row.getCell(colInstalled).setContent(false);
      }
    }

    return result;
  }
}
