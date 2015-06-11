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
 * StoragePanel.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow;

import adams.core.CleanUpHandler;
import adams.core.Utils;
import adams.flow.control.Storage;
import adams.flow.control.StorageHandler;
import adams.flow.control.StorageName;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTable;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SortableAndSearchableTableWithButtons;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.visualization.debug.InspectionPanel;
import adams.gui.visualization.debug.objectrenderer.AbstractObjectRenderer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Displays the current items stored in the temp storage of a flow.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StoragePanel
  extends BasePanel
  implements CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = 8244881694557542183L;

  /**
   * Table model for displaying a storage container.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class TableModel
    extends AbstractBaseTableModel {

    /** for serialization. */
    private static final long serialVersionUID = 3509104625095997777L;

    /** the underlying storage. */
    protected Storage m_Storage;

    /** the converted storage. */
    protected String[][] m_Data;

    /**
     * Initializes the table model with no data.
     */
    public TableModel() {
      this(new Storage());
    }

    /**
     * Initializes the table model the supplied storage.
     *
     * @param storage	the storage to display
     */
    public TableModel(Storage storage) {
      super();
      m_Storage = storage;
      initialize();
    }

    /**
     * Generates the class string to display.
     *
     * @param obj 	the object to create the string for
     * @return		the generated class string
     */
    protected String getClassString(Object obj) {
      String	result;
      Class	cls;

      cls = obj.getClass();
      if (cls.isArray())
	result = Utils.classToString(cls) + " (length: " + Array.getLength(obj) + ")";
      else if (obj instanceof Collection)
	result = cls.getName() + " (size: " + ((Collection) obj).size() + ")";
      else
	result = cls.getName();

      return result;
    }

    /**
     * Initializes the model.
     */
    protected void initialize() {
      int			size;
      Iterator<String>		caches;
      String			cache;
      List<StorageName>		keys;
      int			index;

      // determine size
      size = m_Storage.size();
      caches = m_Storage.caches();
      while (caches.hasNext()) {
	cache = caches.next();
	size += m_Storage.size(cache);
      }

      // fill in data
      m_Data = new String[size][3];
      index  = 0;
      // regular
      keys   = new ArrayList<StorageName>(m_Storage.keySet());
      Collections.sort(keys);
      for (StorageName key: keys) {
	m_Data[index][0] = "";
	m_Data[index][1] = key.getValue();
	m_Data[index][2] = getClassString(m_Storage.get(key));
	index++;
      }
      // caches
      caches = m_Storage.caches();
      while (caches.hasNext()) {
	cache = caches.next();
	keys  = new Vector<StorageName>(m_Storage.keySet(cache));
	Collections.sort(keys);
	for (StorageName key: keys) {
	  m_Data[index][0] = cache;
	  m_Data[index][1] = key.getValue();
	  m_Data[index][2] = getClassString(m_Storage.get(cache, key));
	  index++;
	}
      }
    }

    /**
     * Returns the object associated with the specified key.
     *
     * @param cache	the cache to access, use null or empty string if regular storage
     * @param key	the key for the object to retrieve
     * @return		the associated object, null if not found
     */
    public Object getObject(String cache, String key) {
      if ((cache == null) || (cache.length() == 0))
	return m_Storage.get(new StorageName(key));
      else
	return m_Storage.get(cache, new StorageName(key));
    }

    /**
     * Returns the number of columns in the model.
     *
     * @return		always 3
     */
    public int getColumnCount() {
      return 3;
    }

    /**
     * Returns the number of rows.
     *
     * @return		the number of rows
     */
    public int getRowCount() {
      return m_Data.length;
    }

    /**
     * Returns the value at the specified position.
     *
     * @param rowIndex		the row of the cell
     * @param columnIndex	the column of the cell
     * @return			the value
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
      return m_Data[rowIndex][columnIndex];
    }

    /**
     * Returns the class of the column.
     *
     * @param columnIndex	the index of the column
     * @return			always String.class
     */
    @Override
    public Class getColumnClass(int columnIndex) {
      return String.class;
    }

    /**
     * Returns the name of the column.
     *
     * @param column		the column to get the name for
     */
    @Override
    public String getColumnName(int column) {
      if (column == 0)
	return "Cache";
      else if (column == 1)
	return "Name";
      else if (column == 2)
	return "Type";
      else
	throw new IllegalArgumentException("Illegal column index: " + column);
    }
  }

  /** the current storage handler. */
  protected StorageHandler m_Handler;

  /** the table to display the storage items. */
  protected SortableAndSearchableTableWithButtons m_Table;

  /** the table model. */
  protected TableModel m_TableModel;

  /** the button for inspecting an item. */
  protected JButton m_ButtonInspect;

  /** the checkbox for previewing items. */
  protected JCheckBox m_CheckBoxPreview;

  /** the dialog for inspecting an item. */
  protected BaseDialog m_DialogInspect;

  /** the panel for inspecing an item. */
  protected InspectionPanel m_PanelInspect;

  /** the search panel. */
  protected SearchPanel m_PanelSearch;

  /** the panel with the preview. */
  protected JPanel m_PanelPreview;

  /** the split pane for table and preview. */
  protected BaseSplitPane m_SplitPane;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel panelTable;
    
    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setDividerLocation(150);
    add(m_SplitPane, BorderLayout.CENTER);

    // preview
    m_PanelPreview = new JPanel(new BorderLayout());
    m_SplitPane.setBottomComponent(m_PanelPreview);
    m_SplitPane.setBottomComponentHidden(false);

    // table
    panelTable = new JPanel(new BorderLayout());
    m_SplitPane.setTopComponent(panelTable);

    m_TableModel = new TableModel();
    m_Table      = new SortableAndSearchableTableWithButtons(m_TableModel);
    m_Table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_Table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	updateButtons();
	updatePreview();
      }
    });
    panelTable.add(m_Table, BorderLayout.CENTER);

    m_ButtonInspect = new JButton("Inspect...");
    m_ButtonInspect.setMnemonic('I');
    m_ButtonInspect.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	inspect();
      }
    });
    m_Table.addToButtonsPanel(m_ButtonInspect);
    m_Table.setDoubleClickButton(m_ButtonInspect);
    
    m_CheckBoxPreview = new JCheckBox("Preview");
    m_CheckBoxPreview.setSelected(true);
    m_CheckBoxPreview.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_SplitPane.setBottomComponentHidden(!m_CheckBoxPreview.isSelected());
	updatePreview();
      }
    });
    m_Table.addToButtonsPanel(m_CheckBoxPreview);

    // search
    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, true);
    m_PanelSearch.addSearchListener(new SearchListener() {
      public void searchInitiated(SearchEvent e) {
	m_Table.getComponent().search(
	    e.getParameters().getSearchString(),
	    e.getParameters().isRegExp());
      }
    });
    panelTable.add(m_PanelSearch, BorderLayout.SOUTH);

    updateButtons();
  }
  
  /**
   * Updates the enabled state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonInspect.setEnabled(m_Table.getSelectedRowCount() == 1);
  }

  /**
   * Updates the preview.
   */
  protected void updatePreview() {
    Object			obj;
    AbstractObjectRenderer	renderer;
    
    if (!m_PanelPreview.isVisible())
      return;
    if (m_Table.getSelectedRowCount() != 1)
      return;
    obj = m_TableModel.getObject(
	(String) m_Table.getValueAt(m_Table.getSelectedRow(), 0),
	(String) m_Table.getValueAt(m_Table.getSelectedRow(), 1));

    m_PanelPreview.removeAll();
    renderer = AbstractObjectRenderer.getRenderer(obj).get(0);
    renderer.render(obj, m_PanelPreview);
    m_PanelPreview.invalidate();
    m_PanelPreview.validate();
    m_PanelPreview.repaint();
  }

  /**
   * Brings up the dialog for inspecting an item.
   */
  protected void inspect() {
    String	title;
    String	cache;
    String	name;

    if (m_DialogInspect == null) {
      m_PanelInspect = new InspectionPanel();
      if (getParentDialog() != null)
	m_DialogInspect = new BaseDialog(getParentDialog());
      else
	m_DialogInspect = new BaseDialog(getParentFrame());
      m_DialogInspect.getContentPane().setLayout(new BorderLayout());
      m_DialogInspect.getContentPane().add(m_PanelInspect, BorderLayout.CENTER);
      m_DialogInspect.setLocationRelativeTo(this);
    }
    cache = (String) m_Table.getValueAt(m_Table.getSelectedRow(), 0);
    name  = (String) m_Table.getValueAt(m_Table.getSelectedRow(), 1);
    title = "Inspect (cache: " + (((cache == null) || cache.isEmpty()) ? "-none-" : cache) + "/name: " + name + ")";
    m_DialogInspect.setTitle(title);
    m_PanelInspect.setCurrent(m_TableModel.getObject(cache, name));
    m_DialogInspect.setSize(800, 600);
    m_DialogInspect.setVisible(true);
  }

  /**
   * Sets the handler to display the storage items for.
   *
   * @param value	the handler to use
   */
  public void setHandler(StorageHandler value) {
    m_Handler    = value;
    m_TableModel = new TableModel(value.getStorage());
    m_Table.setModel(m_TableModel);
    m_Table.setOptimalColumnWidth();
    updateButtons();
  }

  /**
   * Returns the current storage handler.
   *
   * @return		the handler, null if none set
   */
  public StorageHandler getHandler() {
    return m_Handler;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (m_PanelInspect != null) {
      m_PanelInspect.closeParent();
      m_PanelInspect = null;
    }
  }
}
