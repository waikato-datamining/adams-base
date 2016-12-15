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
 * ReducedData.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.attseltab.output;

import adams.core.MessageCollection;
import adams.gui.tools.wekainvestigator.output.TableContentPanel;
import adams.gui.tools.wekainvestigator.tab.attseltab.ResultItem;
import adams.gui.visualization.instances.InstancesTable;
import weka.core.Instances;

import javax.swing.JComponent;

/**
 * Generates the reduced dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReducedData
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates the reduced dataset.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Reduced data";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return !item.isCrossValidation() && item.hasFull();
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    InstancesTable	table;
    Instances		reduced;

    if (!item.hasFull()) {
      errors.add("No dataset available to reduce!");
      return null;
    }

    try {
      reduced = item.getAttributeSelection().reduceDimensionality(item.getFull());
    }
    catch (Exception e) {
      errors.add("Failed to reduce data!", e);
      return null;
    }
    table = new InstancesTable(reduced);

    return new TableContentPanel(table, true);
  }
}
