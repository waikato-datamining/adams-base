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
 * AboutBoxPanel.java
 * Copyright (C) 2009-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.application;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.env.Modules;
import adams.env.Modules.Module;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.ImageManager;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

/**
 * Represents an "About" box displayed from the main menu.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class AboutBoxPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -5180917605195603000L;

  /**
   * Custom model for Modules.
   */
  public static class ModulesTableModel
    extends AbstractTableModel {

    private static final long serialVersionUID = 3688398640745033949L;

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    @Override
    public int getRowCount() {
      return Modules.getSingleton().getModules().size();
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() {
      return 7;
    }

    /**
     * Returns the name of the column at <code>columnIndex</code>.  This is used
     * to initialize the table's column header name.  Note: this name does
     * not need to be unique; two columns in a table can have the same name.
     *
     * @param   columnIndex     the index of the column
     * @return  the name of the column
     */
    @Override
    public String getColumnName(int columnIndex) {
      switch (columnIndex) {
	case 0:
	  return "Logo";
	case 1:
	  return "Name";
	case 2:
	  return "Version";
	case 3:
	  return "Build timestamp";
	case 4:
	  return "Description";
	case 5:
	  return "Author";
	case 6:
	  return "Organization";
	default:
	  throw new IllegalStateException("Unhandled column index: " + columnIndex);
      }
    }

    /**
     * Returns the most specific superclass for all the cell values
     * in the column.  This is used by the <code>JTable</code> to set up a
     * default renderer and editor for the column.
     *
     * @param columnIndex  the index of the column
     * @return the common ancestor class of the object values in the model.
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex == 0)
	return Icon.class;
      else
	return String.class;
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param   rowIndex        the row whose value is to be queried
     * @param   columnIndex     the column whose value is to be queried
     * @return  the value Object at the specified cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      Module	module;

      module = Modules.getSingleton().getModules().get(rowIndex);
      switch (columnIndex) {
	case 0:
	  return module.getLogo();
	case 1:
	  return module.getName();
	case 2:
	  return module.getVersion().isEmpty() ? "?.?.?" : module.getVersion();
	case 3:
	  return module.getBuildTimestamp();
	case 4:
	  return module.getDescription();
	case 5:
	  return module.getAuthor();
	case 6:
	  return module.getOrganization();
	default:
	  throw new IllegalStateException("Unhandled column index: " + columnIndex);
      }
    }
  }

  /** the panel for the image. */
  protected JPanel m_PanelImage;

  /** the label displaying the image. */
  protected JLabel m_LabelImage;

  /** the panel for the title, copyright, etc. */
  protected JPanel m_PanelTitle;

  /** the table with the modules. */
  protected SortableAndSearchableTable m_TableModules;

  /** the search panel. */
  protected SearchPanel m_PanelSearch;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel		panel;

    super.initGUI();

    setLayout(new BorderLayout(5, 5));
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.NORTH);

    m_PanelImage = new JPanel(new BorderLayout());
    m_LabelImage = new JLabel(ImageManager.getLogoImage());
    m_PanelImage.add(m_LabelImage, BorderLayout.CENTER);
    panel.add(m_PanelImage, BorderLayout.NORTH);

    m_PanelTitle = new JPanel();
    m_PanelTitle.setLayout(new GridLayout(0, 1));
    m_PanelTitle.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    panel.add(m_PanelTitle, BorderLayout.CENTER);

    m_TableModules = new SortableAndSearchableTable(new ModulesTableModel());
    m_TableModules.setAutoResizeMode(SortableAndSearchableTable.AUTO_RESIZE_OFF);
    m_TableModules.setOptimalColumnWidthBounded(200);
    add(new BaseScrollPane(m_TableModules), BorderLayout.CENTER);

    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, false);
    m_PanelSearch.addSearchListener(new SearchListener() {
      @Override
      public void searchInitiated(SearchEvent e) {
	m_TableModules.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
	updateRowHeights();
      }
    });
    add(m_PanelSearch, BorderLayout.SOUTH);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateRowHeights();
  }

  /**
   * Updates the heights of the rows.
   */
  @MixedCopyright(
    author = "camickr",
    license = License.CC_BY_SA_25,
    url = "https://stackoverflow.com/a/1784601/4698227"
  )
  protected void updateRowHeights() {
    int		row;
    int		rowHeight;
    int		column;
    Component	comp;

    for (row = 0; row < m_TableModules.getRowCount(); row++) {
      rowHeight = m_TableModules.getRowHeight();
      for (column = 0; column < m_TableModules.getColumnCount(); column++) {
	comp      = m_TableModules.prepareRenderer(m_TableModules.getCellRenderer(row, column), row, column);
	rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
      }
      m_TableModules.setRowHeight(row, rowHeight);
    }
  }

  /**
   * Sets the image to display.
   *
   * @param name	the name of the image
   */
  public void setImage(String name) {
    m_LabelImage.setIcon(ImageManager.getIcon(name));
  }

  /**
   * Adds a label with the info.
   *
   * @param info	the information to display
   */
  public void addInfo(String info) {
    m_PanelTitle.add(new JLabel(info, SwingConstants.CENTER));
  }
}
