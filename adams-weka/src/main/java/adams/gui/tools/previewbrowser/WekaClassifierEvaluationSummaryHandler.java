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
 * WekaClassifierEvaluationSummaryHandler.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTable;
import adams.gui.core.MapTableModel;
import adams.gui.core.SortableAndSearchableTable;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Displays the textual evaluation summaries of Weka classifiers as tables (ext: txt)
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaClassifierEvaluationSummaryHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8339099303070121780L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the textual evaluation summaries of Weka classifiers as tables (ext: " + Utils.arrayToString(getExtensions()) + ")";
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the array of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"txt"};
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  public PreviewPanel createPreview(File file) {
    JPanel			panel;
    SortableAndSearchableTable 	table;
    List<String> 		lines;
    Map<String,Object> 		stats;
    String			name;
    String			value;

    stats = new HashMap<>();
    lines = FileUtils.loadFromFile(file);
    Utils.removeEmptyLines(lines, true);
    for (String line: lines) {
      if (line.contains("==="))
	continue;
      name = line.substring(0, 35).trim();
      value = line.substring(35).trim();
      while (value.contains("  "))
	value = value.replaceAll("  ", " ");
      if (value.contains(" %"))
	value = value.replace(" %", "%");
      if (value.contains(" "))
	value = value.replace(" ", " / ");
      stats.put(name, value);
    }

    table = new SortableAndSearchableTable(new MapTableModel(stats));
    table.setShowSimplePopupMenus(true);
    table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    table.setOptimalColumnWidth();
    panel = new JPanel(new BorderLayout());
    panel.add(new BaseScrollPane(table));

    return new PreviewPanel(panel, table);
  }
}
