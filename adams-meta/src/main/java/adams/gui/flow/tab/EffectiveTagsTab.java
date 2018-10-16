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
 * EffectiveTagsTab.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tab;

import adams.core.base.BaseKeyValuePair;
import adams.core.tags.TagProcessorHelper;
import adams.flow.core.Actor;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.KeyValuePairTableModel;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.flow.tree.TreeHelper;

import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Tab for displaying the effective tags for the currently selected actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class EffectiveTagsTab
  extends AbstractEditorTab
  implements SelectionAwareEditorTab {

  /** for serialization. */
  private static final long serialVersionUID = 3860012648562358118L;

  /** the column names. */
  public final static String[] COLUMN_NAMES = new String[]{"Tag", "Value"};

  /** for displaying the help text. */
  protected SortableAndSearchableTable m_Table;

  /** the model for the tags. */
  protected KeyValuePairTableModel m_Model;

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_Model = new KeyValuePairTableModel(new Object[0][], COLUMN_NAMES);
    m_Table = new SortableAndSearchableTable(m_Model);
    m_Table.setShowSimpleCellPopupMenu(true);
    m_Table.setUseOptimalColumnWidths(true);

    add(new BaseScrollPane(m_Table), BorderLayout.CENTER);
  }

  /**
   * Returns the title of the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Effective tags";
  }

  /**
   * Notifies the tab of the currently selected actors.
   *
   *
   * @param paths	the selected paths
   * @param actors	the currently selected actors
   */
  public void actorSelectionChanged(TreePath[] paths, Actor[] actors) {
    Runnable			run;
    List<BaseKeyValuePair>	tags;
    Object[][]			data;
    int				i;

    if (actors.length != 1) {
      m_Model = new KeyValuePairTableModel(new Object[0][], COLUMN_NAMES);
      run = () -> m_Table.setModel(m_Model);
      SwingUtilities.invokeLater(run);
      return;
    }

    tags = TagProcessorHelper.getAllTags(TreeHelper.pathToNode(paths[0]), true);
    data = new String[tags.size()][2];
    for (i = 0; i < tags.size(); i++)
      data[i] = new String[]{tags.get(i).getPairKey(), tags.get(i).getPairValue()};
    m_Model = new KeyValuePairTableModel(data, COLUMN_NAMES);

    run = () -> m_Table.setModel(m_Model);
    SwingUtilities.invokeLater(run);
  }
}
