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
  
  /** the type of list to generate. */
  protected ListType m_ListType;
  
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "listType", m_ListType, "list: ");
  }

  /**
   * The types of data the action generates.
   *
   * @return the output types
   */
  @Override
  public Class[] generates() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Executes the action.
   *
   * @param errors for collecting errors
   * @return the generated output, null if failed to generated
   */
  @Override
  public Object doExecute(MessageCollection errors) {
    SpreadSheet		result;
    Row			row;
    List<Package> 	pkgs;

    result = new DefaultSpreadSheet();
    result.addComment("List type: " + m_ListType);

    // header
    row = result.getHeaderRow();
    row.addCell("N").setContent("Name");
    row.addCell("V").setContent("Version");
    row.addCell("U").setContent("URL");

    // data
    try {
      switch (m_ListType) {
	case ALL:
	  pkgs = WekaPackageManager.getAllPackages();
	  break;
	case INSTALLED:
	  pkgs = WekaPackageManager.getInstalledPackages();
	  break;
	case AVAILABLE:
	  pkgs = WekaPackageManager.getAvailablePackages();
	  break;
	default:
	  throw new IllegalStateException("Unhandled list type: " + m_ListType);
      }

      for (Package pkg: pkgs) {
	row = result.addRow();
	row.addCell("N").setContentAsString(pkg.getName());
	if (pkg.getPackageMetaData().containsKey("Version"))
	  row.addCell("V").setContentAsString("" + pkg.getPackageMetaData().get("Version"));
	row.addCell("U").setContent(pkg.getPackageURL().toString());
      }

      result.sort(0, true);
    }
    catch (Exception e) {
      errors.add("Failed to add packages!", e);
      getLogger().log(Level.SEVERE, "Failed to add packages!", e);
    }

    return result;
  }
}
