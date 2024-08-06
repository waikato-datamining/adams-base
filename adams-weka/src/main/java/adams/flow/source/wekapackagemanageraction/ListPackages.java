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
 * ListPackages.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source.wekapackagemanageraction;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import weka.core.WekaPackageManager;
import weka.core.WekaPackageUtils;
import weka.core.packageManagement.Package;

import java.util.List;
import java.util.logging.Level;

/**
 * Lists the packages.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ListPackages
  extends AbstractWekaPackageManagerAction {

  private static final long serialVersionUID = -5149682404460521030L;

  /**
   * The type of list to generate.
   */
  public enum ListType {
    ALL,
    INSTALLED,
    AVAILABLE,
  }

  /**
   * How to output the packages.
   */
  public enum OutputFormat {
    SPREADSHEET,
    PACKAGE,
  }

  /** the type of list to generate. */
  protected ListType m_ListType;
  
  /** the output format. */
  protected OutputFormat m_OutputFormat;
  
  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a spreadsheet with the specified list of packages.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "list-type", "listType",
      ListType.ALL);

    m_OptionManager.add(
      "output-format", "outputFormat",
      OutputFormat.SPREADSHEET);
  }

  /**
   * Sets the type of list to generate.
   *
   * @param value	the type
   */
  public void setListType(ListType value) {
    m_ListType = value;
    reset();
  }

  /**
   * Returns the type of list to generate.
   *
   * @return		the type
   */
  public ListType getListType() {
    return m_ListType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String listTypeTipText() {
    return "The type of list to generate.";
  }

  /**
   * Sets the type of output format to generate.
   *
   * @param value	the type
   */
  public void setOutputFormat(OutputFormat value) {
    m_OutputFormat = value;
    reset();
  }

  /**
   * Returns the type of output format to generate.
   *
   * @return		the type
   */
  public OutputFormat getOutputFormat() {
    return m_OutputFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFormatTipText() {
    return "The type of output format to generate.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "listType", m_ListType, "list: ");
    result += QuickInfoHelper.toString(this, "outputFromat", m_OutputFormat, ", format: ");

    return result;
  }

  /**
   * The types of data the action generates.
   *
   * @return the output types
   */
  @Override
  public Class[] generates() {
    switch (m_OutputFormat) {
      case SPREADSHEET:
	return new Class[]{SpreadSheet.class};
      case PACKAGE:
	return new Class[]{Package[].class};
      default:
	throw new IllegalStateException("Unhandled output format: " + m_OutputFormat);
    }
  }

  /**
   * Returns the packages to output.
   *
   * @return		the packages
   * @throws Exception	if retrieval of packages fails
   */
  protected List<Package> getPackages() throws Exception {
    switch (m_ListType) {
      case ALL:
	return WekaPackageManager.getAllPackages();
      case INSTALLED:
	return WekaPackageManager.getInstalledPackages();
      case AVAILABLE:
	return WekaPackageManager.getAvailablePackages();
      default:
	throw new IllegalStateException("Unhandled list type: " + m_ListType);
    }
  }

  /**
   * Executes the action.
   *
   * @param errors for collecting errors
   * @return the generated output, null if failed to generated
   */
  @Override
  public Object doExecute(MessageCollection errors) {
    SpreadSheet 	sheet;
    Row			row;

    switch (m_OutputFormat) {
      case SPREADSHEET:
	sheet = new DefaultSpreadSheet();
	sheet.addComment("List type: " + m_ListType);

	// header
	row = sheet.getHeaderRow();
	row.addCell("N").setContent("Name");
	row.addCell("V").setContent("Version");
	row.addCell("U").setContent("URL");
	row.addCell("O").setContent("Official");

	// data
	try {
	  for (Package pkg: getPackages()) {
	    row = sheet.addRow();
	    row.addCell("N").setContentAsString(pkg.getName());
	    if (pkg.getPackageMetaData().containsKey("Version"))
	      row.addCell("V").setContentAsString("" + pkg.getPackageMetaData().get("Version"));
	    row.addCell("U").setContent(pkg.getPackageURL().toString());
	    row.addCell("O").setContent(WekaPackageUtils.isOfficial(pkg));
	  }

	  sheet.sort(0, true);
	}
	catch (Exception e) {
	  errors.add("Failed to add packages!", e);
	  getLogger().log(Level.SEVERE, "Failed to add packages!", e);
	  sheet = null;
	}
	return sheet;

      case PACKAGE:
	try {
	  return getPackages().toArray(new Package[0]);
	}
	catch (Exception e) {
	  errors.add("Failed to retrieve packages!", e);
	  getLogger().log(Level.SEVERE, "Failed to retrieve packages!", e);
	  return null;
	}

      default:
	errors.add("Unhandled output format: " + m_OutputFormat);
	return null;
    }
  }
}
