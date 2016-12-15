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

import adams.core.MessageCollection;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.SpreadSheetTable;
import adams.gui.tools.wekainvestigator.output.TableContentPanel;
import adams.gui.tools.wekainvestigator.tab.clustertab.ResultItem;

import javax.swing.JComponent;

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
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    SpreadSheet		sheet;
    SpreadSheetTable	table;

    sheet = new DefaultSpreadSheet();

    // header
    sheet.getHeaderRow().addCell("A").setContent("Assignment");

    // data
    for (double assignment: item.getEvaluation().getClusterAssignments())
      sheet.addRow().addCell("A").setContent(assignment);

    table = new SpreadSheetTable(sheet);

    return new TableContentPanel(table, true);
  }
}
