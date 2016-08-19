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
 * ClusterAssignments.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.clustertab.output;

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.SpreadSheetTable;
import adams.gui.tools.wekainvestigator.output.TableContentPanel;
import adams.gui.tools.wekainvestigator.tab.clustertab.ResultItem;

/**
 * Displays the cluster assignments.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClusterAssignments
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the cluster assignments.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Cluster assignments";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation() && (item.getEvaluation().getClusterAssignments() != null);
  }

  /**
   * Generates output and adds it to the {@link ResultItem}.
   *
   * @param item	the item to add the output to
   * @return		null if output could be generated, otherwise error message
   */
  @Override
  public String generateOutput(ResultItem item) {
    SpreadSheet		sheet;
    SpreadSheetTable	table;

    sheet = new DefaultSpreadSheet();

    // header
    sheet.getHeaderRow().addCell("A").setContent("Assignment");

    // data
    for (double assignment: item.getEvaluation().getClusterAssignments())
      sheet.addRow().addCell("A").setContent(assignment);

    table = new SpreadSheetTable(sheet);

    addTab(item, new TableContentPanel(table, true));

    return null;
  }
}
