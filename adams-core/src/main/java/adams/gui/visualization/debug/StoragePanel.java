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
 * StoragePanel.java
 * Copyright (C) 2011-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug;

import adams.core.CleanUpHandler;
import adams.core.Utils;
import adams.event.StorageChangeEvent;
import adams.event.StorageChangeEvent.Type;
import adams.event.StorageChangeListener;
import adams.flow.control.Storage;
import adams.flow.control.StorageHandler;
import adams.flow.control.StorageName;
import adams.gui.chooser.ObjectExporterFileChooser;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTextField;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SortableAndSearchableTableWithButtons;
import adams.gui.core.UISettings;
import adams.gui.event.SearchEvent;
import adams.gui.goe.EditorHelper;
import adams.gui.visualization.debug.objectexport.AbstractObjectExporter;
import adams.gui.visualization.debug.objectrenderer.AbstractObjectRenderer;
import adams.gui.visualization.debug.objectrenderer.ObjectRenderer;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Displays the current items stored in the temp storage of a flow.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class StoragePanel
  extends BasePanel
  implements CleanUpHandler, StorageChangeListener {

  /** for serialization. */
  private static final long serialVersionUID = 8244881694557542183L;

  /**
   * Table model for displaying a storage container.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class TableModel
    extends AbstractBaseTableModel {

    /** for serialization. */
    private static final long serialVersionUID = 3509104625095997777L;

    /** the underlying storage. */
    protected Storage m_Storage;

    /** whether cache data is available. */
    protected boolean m_HasCacheData;

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
      m_HasCacheData = hasCaches(m_Storage);
      m_Data         = takeSnapshot(m_Storage);
    }

    /**
     * Checks whether any cache data is to be displayed.
     *
     * @param storage	the storage to inspect
     * @return		true if cache data present
     */
    protected boolean hasCaches(Storage storage) {
      boolean   		result;
      Iterator<String>		caches;

      caches = storage.caches();
      result = caches.hasNext();

      return result;
    }

    /**
     * Takes a snapshot of the storage and turns it into a string array.
     *
     * @param storage	the storage to use
     * @return		the snapshot
     */
    protected String[][] takeSnapshot(Storage storage) {
      String[][] 		result;
      int			size;
      Iterator<String>		caches;
      String			cache;
      List<StorageName>		keys;
      int			index;

      // determine size
      size = storage.size();
      caches = storage.caches();
      while (caches.hasNext()) {
	cache = caches.next();
	size += storage.size(cache);
      }

      // fill in data
      result = new String[size][3];
      index  = 0;
      // regular
      keys   = new ArrayList<>(storage.keySet());
      Collections.sort(keys);
      for (StorageName key: keys) {
	result[index][0] = "";
	result[index][1] = key.getValue();
	result[index][2] = getClassString(storage.get(key));
	index++;
      }
      // caches
      caches = storage.caches();
      while (caches.hasNext()) {
	cache = caches.next();
	keys  = new ArrayList<>(storage.keySet(cache));
	Collections.sort(keys);
	for (StorageName key: keys) {
	  result[index][0] = cache;
	  result[index][1] = key.getValue();
	  result[index][2] = getClassString(storage.get(cache, key));
	  index++;
	}
      }

      return result;
    }

    /**
     * Returns whether cache data is present.
     *
     * @return		true if cache data present (3 columns instead of 2)
     */
    public boolean hasCacheData() {
      return m_HasCacheData;
    }

    /**
     * Returns the object associated with the specified key.
     *
     * @param cache	the cache to access, use null or empty string if regular storage
     * @param key	the key for the object to retrieve
     * @return		the associated object, null if not found
     */
    public Object getObject(String cache, String key) {
      if ((cache == null) || !cache.isEmpty())
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
      return (!m_HasCacheData ? 2 : 3);
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
      if (!m_HasCacheData)
	return m_Data[rowIndex][columnIndex + 1];
      else
	return m_Data[rowIndex][columnIndex];
    }

    /**
     * Sets the value at the specified position.
     *
     * @param aValue		the value to set
     * @param rowIndex		the row of the cell
     * @param columnIndex	the column of the cell
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      String	cache;
      String	name;

      if (m_HasCacheData) {
	cache = (String) getValueAt(rowIndex, 0);
	name  = (String) getValueAt(rowIndex, 1);
      }
      else {
	cache = null;
	name  = (String) getValueAt(rowIndex, 0);
      }

      if (name != null) {
	if ((cache == null) || cache.isEmpty())
	  m_Storage.put(new StorageName(name), aValue);
	else
	  m_Storage.put(cache, new StorageName(name), aValue);
      }

      fireTableCellUpdated(rowIndex, columnIndex);
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
      if (!m_HasCacheData) {
	if (column == 0)
	  return "Name";
	else if (column == 1)
	  return "Type";
      }
      else {
	if (column == 0)
	  return "Cache";
	else if (column == 1)
	  return "Name";
	else if (column == 2)
	  return "Type";
      }
      throw new IllegalArgumentException("Illegal column index: " + column);
    }

    /**
     * Checks whether the storage contains different data.
     *
     * @param other	the other storage to compare with
     * @return		true if different
     */
    public boolean isDifferent(Storage other) {
      String[][]	otherData;
      int		i;
      int		n;

      otherData = takeSnapshot(other);
      if (m_Data.length != otherData.length)
	return true;

      for (n = 0; n < m_Data.length; n++) {
	for (i = 0; i < m_Data[n].length; i++) {
	  if ((m_Data[n][i] == null) && (otherData[n][i] == null))
	    continue;
	  if ((m_Data[n][i] == null) && (otherData[n][i] != null))
	    return true;
	  if ((m_Data[n][i] != null) && (otherData[n][i] == null))
	    return true;
	  if (!m_Data[n][i].equals(otherData[n][i]))
	    return true;
	}
      }

      return false;
    }
  }

  /** the current storage handler. */
  protected StorageHandler m_Handler;

  /** the table to display the storage items. */
  protected SortableAndSearchableTableWithButtons m_Table;

  /** the table model. */
  protected TableModel m_TableModel;

  /** the button for inspecting an item. */
  protected BaseButton m_ButtonInspect;

  /** the button for editing an item. */
  protected BaseButton m_ButtonEdit;

  /** the button for exporting an item. */
  protected BaseButton m_ButtonExport;

  /** the checkbox for previewing items. */
  protected BaseCheckBox m_CheckBoxPreview;

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

  /** the filechooser for exporting the object. */
  protected ObjectExporterFileChooser m_FileChooser;

  /** the checkbox for applying a render limit. */
  protected BaseCheckBox m_CheckBoxRenderLimit;

  /** the textfield for the render limit. */
  protected BaseTextField m_TextFieldRenderLimit;

  /** the button for applying the render limit. */
  protected BaseFlatButton m_ButtonRenderLimit;

  /** whether the render limit changed. */
  protected boolean m_RenderLimitChanged;

  /** whether the rendering limit is supported. */
  protected boolean m_RenderLimitSupported;

  /** the cache for the renderers. */
  protected Map<String,ObjectRenderer> m_RendererCache;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_RendererCache        = new HashMap<>();
    m_RenderLimitChanged   = false;
    m_RenderLimitSupported = false;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel 	panelTable;
    JPanel	panelBottom;
    JPanel	panelRenderLimit;

    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setDividerLocation(UISettings.get(getClass(), "Divider", 150));
    m_SplitPane.setUISettingsParameters(getClass(), "Divider");
    add(m_SplitPane, BorderLayout.CENTER);

    // bottom
    panelBottom = new JPanel(new BorderLayout(0, 2));
    panelBottom.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    m_SplitPane.setBottomComponent(panelBottom);
    m_SplitPane.setBottomComponentHidden(false);

    // preview
    m_PanelPreview = new JPanel(new BorderLayout());
    panelBottom.add(m_PanelPreview, BorderLayout.CENTER);

    // render limit
    panelRenderLimit = new JPanel(new BorderLayout(0, 0));
    panelBottom.add(panelRenderLimit, BorderLayout.SOUTH);
    m_CheckBoxRenderLimit = new BaseCheckBox("Limit");
    m_CheckBoxRenderLimit.addActionListener((ActionEvent e) -> updatePreview());
    panelRenderLimit.add(m_CheckBoxRenderLimit, BorderLayout.WEST);
    m_TextFieldRenderLimit = new BaseTextField(5);
    m_TextFieldRenderLimit.setToolTipText("Some renderers use a limit to speed up the display: leave empty for using the renderer's default, -1 for unlimited");
    m_TextFieldRenderLimit.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	m_RenderLimitChanged = true;
	updateRenderWidgets();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	m_RenderLimitChanged = true;
	updateRenderWidgets();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	m_RenderLimitChanged = true;
	updateRenderWidgets();
      }
    });
    panelRenderLimit.add(m_TextFieldRenderLimit, BorderLayout.CENTER);
    m_ButtonRenderLimit = new BaseFlatButton(ImageManager.getIcon("validate.png"));
    m_ButtonRenderLimit.addActionListener((ActionEvent e) -> updateRenderLimit());
    panelRenderLimit.add(m_ButtonRenderLimit, BorderLayout.EAST);

    // table
    panelTable = new JPanel(new BorderLayout());
    m_SplitPane.setTopComponent(panelTable);

    m_TableModel = new TableModel();
    m_Table      = new SortableAndSearchableTableWithButtons(m_TableModel);
    m_Table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_Table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
      updateButtons();
      updatePreview();
      updateInspection();
    });
    m_Table.addCellPopupMenuListener((MouseEvent e) -> showTablePopup(e));
    panelTable.add(m_Table, BorderLayout.CENTER);

    m_ButtonInspect = new BaseButton("Inspect...");
    m_ButtonInspect.setMnemonic('I');
    m_ButtonInspect.addActionListener((ActionEvent e) -> inspect());
    m_Table.addToButtonsPanel(m_ButtonInspect);
    m_Table.setDoubleClickButton(m_ButtonInspect);

    m_ButtonEdit = new BaseButton("Edit...");
    m_ButtonEdit.setMnemonic('E');
    m_ButtonEdit.addActionListener((ActionEvent e) -> edit());
    m_Table.addToButtonsPanel(m_ButtonEdit);

    m_ButtonExport = new BaseButton("Export...");
    m_ButtonExport.setMnemonic('x');
    m_ButtonExport.addActionListener((ActionEvent e) -> export());
    m_Table.addToButtonsPanel(m_ButtonExport);

    m_CheckBoxPreview = new BaseCheckBox("Preview");
    m_CheckBoxPreview.setSelected(true);
    m_CheckBoxPreview.addActionListener((ActionEvent e) -> {
      m_SplitPane.setBottomComponentHidden(!m_CheckBoxPreview.isSelected());
      updatePreview();
    });
    m_Table.addToButtonsPanel(m_CheckBoxPreview);

    // search
    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, false);
    m_PanelSearch.addSearchListener((SearchEvent e) -> {
      m_Table.getComponent().search(
	e.getParameters().getSearchString(),
	e.getParameters().isRegExp());
    });
    m_PanelSearch.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    panelTable.add(m_PanelSearch, BorderLayout.SOUTH);

    updateButtons();
  }

  /**
   * Updates the enabled state of the buttons.
   */
  protected void updateButtons() {
    int			selCount;
    Object		selObj;

    selCount = m_Table.getSelectedRowCount();
    selObj   = getSelectedObject();

    m_ButtonInspect.setEnabled(selCount == 1);
    m_ButtonEdit.setEnabled((selCount == 1) && canEdit(selObj));
    m_ButtonExport.setEnabled(selCount == 1);
  }

  /**
   * Returns the current render limit.
   *
   * @return		the limit
   */
  protected Integer getRenderLimit() {
    Integer	result;

    result = null;

    if (m_CheckBoxRenderLimit.isSelected()) {
      if (Utils.isInteger(m_TextFieldRenderLimit.getText()))
	result = Integer.parseInt(m_TextFieldRenderLimit.getText());
    }

    return result;
  }

  /**
   * Updates the state of the render widgets.
   */
  protected void updateRenderWidgets() {
    if (m_RenderLimitChanged)
      m_ButtonRenderLimit.setIcon(ImageManager.getIcon("validate_blue.png"));
    else
      m_ButtonRenderLimit.setIcon(ImageManager.getIcon("validate.png"));

    m_CheckBoxRenderLimit.setEnabled(m_RenderLimitSupported);
    m_TextFieldRenderLimit.setEnabled(m_RenderLimitSupported && m_CheckBoxRenderLimit.isSelected());
    m_ButtonRenderLimit.setEnabled(m_RenderLimitSupported && m_CheckBoxRenderLimit.isSelected());
  }

  /**
   * Updates the render limit.
   */
  protected void updateRenderLimit() {
    m_RenderLimitChanged = false;
    updateRenderWidgets();
    updatePreview();
  }

  /**
   * Renders the specified object in the provided panel.
   *
   * @param panel	the panel to use for rendering
   * @param obj 	the object to render
   */
  protected void renderObject(JPanel panel, Object obj) {
    String		key;
    ObjectRenderer 	renderer;
    Integer		limit;

    panel.removeAll();
    renderer = null;
    key      = (obj == null ? "null" : obj.getClass().getName());
    if ((obj != null) && m_RendererCache.containsKey(key))
      renderer = m_RendererCache.get(key);
    if (renderer == null)
      renderer = AbstractObjectRenderer.getRenderer(obj).get(0);
    m_RenderLimitSupported = renderer.supportsLimit(obj);
    limit = (m_RenderLimitSupported ? getRenderLimit() : null);
    renderer.renderCached(obj, panel, limit);
    if ((obj != null) && !m_RendererCache.containsKey(key))
      m_RendererCache.put(key, renderer);
    updateRenderWidgets();
  }

  /**
   * Updates the preview.
   */
  protected void updatePreview() {
    Object		obj;

    if (!m_PanelPreview.isVisible())
      return;
    if (m_Table.getSelectedRowCount() != 1)
      return;
    if (m_TableModel.hasCacheData())
      obj = m_TableModel.getObject(
	(String) m_Table.getValueAt(m_Table.getSelectedRow(), 0),
	(String) m_Table.getValueAt(m_Table.getSelectedRow(), 1));
    else
      obj = m_TableModel.getObject(
	null,
	(String) m_Table.getValueAt(m_Table.getSelectedRow(), 0));

    renderObject(m_PanelPreview, obj);
  }

  /**
   * Updates the inspection (if visible).
   *
   * @see		#inspect()
   */
  protected void updateInspection() {
    if (m_DialogInspect == null)
      return;
    inspect();
  }

  /**
   * Shows the preview in a new dialog.
   */
  protected void newPreview() {
    Object		obj;
    JPanel 		preview;
    BaseDialog		dialog;

    if (m_Table.getSelectedRowCount() != 1)
      return;
    if (m_TableModel.hasCacheData())
      obj = m_TableModel.getObject(
	(String) m_Table.getValueAt(m_Table.getSelectedRow(), 0),
	(String) m_Table.getValueAt(m_Table.getSelectedRow(), 1));
    else
      obj = m_TableModel.getObject(
	null,
	(String) m_Table.getValueAt(m_Table.getSelectedRow(), 0));

    preview = new JPanel(new BorderLayout());
    renderObject(preview, obj);

    if (getParentDialog() != null)
      dialog = new BaseDialog(getParentDialog());
    else
      dialog= new BaseDialog(getParentFrame());
    dialog.setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(preview, BorderLayout.CENTER);
    dialog.setTitle("Preview (" + getSelectedObjectID() + ")");
    dialog.setSize(GUIHelper.getDefaultDialogDimension());
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
  }

  /**
   * Returns the ID of the currently selected object.
   *
   * @return		the ID, null if none selected
   */
  protected String getSelectedObjectID() {
    String	cache;
    String	name;

    if (m_Table.getSelectedRow() == -1)
      return null;

    if (m_TableModel.hasCacheData()) {
      cache = (String) m_Table.getValueAt(m_Table.getSelectedRow(), 0);
      name = (String) m_Table.getValueAt(m_Table.getSelectedRow(), 1);

      return "cache: " + (((cache == null) || cache.isEmpty()) ? "-none-" : cache) + "/name: " + name;
    }
    else {
      return "name: " + m_Table.getValueAt(m_Table.getSelectedRow(), 0);
    }
  }

  /**
   * Returns the currently selected object.
   *
   * @return		the object, null if none selected
   */
  protected Object getSelectedObject() {
    String	cache;
    String	name;

    if (m_Table.getSelectedRow() == -1)
      return null;

    if (m_TableModel.hasCacheData()) {
      cache = (String) m_Table.getValueAt(m_Table.getSelectedRow(), 0);
      name  = (String) m_Table.getValueAt(m_Table.getSelectedRow(), 1);
    }
    else {
      cache = null;
      name  = (String) m_Table.getValueAt(m_Table.getSelectedRow(), 0);
    }

    return m_TableModel.getObject(cache, name);
  }

  /**
   * Copies the name to the clipboard.
   */
  protected void copyName() {
    String	value;

    if (m_TableModel.hasCacheData())
      value = (String) m_Table.getValueAt(m_Table.getSelectedRow(), 1);
    else
      value = (String) m_Table.getValueAt(m_Table.getSelectedRow(), 0);
    if ((value != null) && !value.isEmpty())
      ClipboardHelper.copyToClipboard(value);
  }

  /**
   * Copies the cache to the clipboard.
   */
  protected void copyCache() {
    String	value;

    if (m_TableModel.hasCacheData()) {
      value = (String) m_Table.getValueAt(m_Table.getSelectedRow(), 0);
      if ((value != null) && !value.isEmpty())
	ClipboardHelper.copyToClipboard(value);
    }
  }

  /**
   * Brings up the dialog for inspecting an item.
   */
  protected void inspect() {
    inspect(false);
  }

  /**
   * Brings up the dialog for inspecting an item.
   *
   * @param newDialog	true if to create a new dialog instead of using {@link #m_DialogInspect}
   */
  protected void inspect(boolean newDialog) {
    BaseDialog		dialog;
    InspectionPanel	inspectionPanel;

    if (newDialog) {
      inspectionPanel = new InspectionPanel();
      if (getParentDialog() != null)
	dialog = new BaseDialog(getParentDialog());
      else
	dialog = new BaseDialog(getParentFrame());
      dialog.setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);
      dialog.getContentPane().setLayout(new BorderLayout());
      dialog.getContentPane().add(inspectionPanel, BorderLayout.CENTER);
      dialog.setTitle("Inspect (" + getSelectedObjectID() + ")");
      inspectionPanel.setCurrent(getSelectedObject());
      dialog.setSize(GUIHelper.getDefaultDialogDimension());
      dialog.setLocationRelativeTo(dialog.getParent());
      dialog.setVisible(true);
    }
    else {
      if (m_DialogInspect == null) {
	m_PanelInspect = new InspectionPanel();
	if (getParentDialog() != null)
	  m_DialogInspect = new BaseDialog(getParentDialog());
	else
	  m_DialogInspect = new BaseDialog(getParentFrame());
	m_DialogInspect.setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);
	m_DialogInspect.addWindowListener(new WindowAdapter() {
	  @Override
	  public void windowClosed(WindowEvent e) {
	    m_DialogInspect = null;
	  }
	});
	m_DialogInspect.getContentPane().setLayout(new BorderLayout());
	m_DialogInspect.getContentPane().add(m_PanelInspect, BorderLayout.CENTER);
        m_DialogInspect.setSize(GUIHelper.getDefaultDialogDimension());
	m_DialogInspect.setLocationRelativeTo(m_DialogInspect.getParent());
      }
      m_DialogInspect.setTitle("Inspect (" + getSelectedObjectID() + ")");
      m_PanelInspect.setCurrent(getSelectedObject());
      m_DialogInspect.setSize(GUIHelper.getDefaultDialogDimension());
      m_DialogInspect.setVisible(true);
    }
  }

  /**
   * Returns whether the object can be edited.
   *
   * @param obj		the object to check
   * @return		true if editable
   */
  protected boolean canEdit(Object obj) {
    if (obj == null)
      return false;
    if (obj.getClass().isArray())
      return true;
    return (PropertyEditorManager.findEditor(obj.getClass()) != null);
  }

  /**
   * Brings up the dialog for editing an item.
   */
  protected void edit() {
    Object	newObj;

    newObj = EditorHelper.simpleEdit(this, getSelectedObject(), getSelectedObjectID());
    if (newObj != null) {
      if (m_TableModel.hasCacheData())
	m_Table.setValueAt(newObj, m_Table.getSelectedRow(), 2);
      else
	m_Table.setValueAt(newObj, m_Table.getSelectedRow(), 1);
      updatePreview();
      updateInspection();
    }
  }

  /**
   * Brings up the dialog for exporting an item.
   */
  protected void export() {
    int				retVal;
    AbstractObjectExporter	exporter;
    String			msg;

    if (m_FileChooser == null)
      m_FileChooser = new ObjectExporterFileChooser();
    m_FileChooser.setCurrentClass(getSelectedObject().getClass());
    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != ObjectExporterFileChooser.APPROVE_OPTION)
      return;

    exporter = m_FileChooser.getWriter();
    msg      = exporter.export(getSelectedObject(), m_FileChooser.getSelectedFile());
    if (msg != null)
      GUIHelper.showErrorMessage(this, "Failed to export object '" + getSelectedObjectID() + "':\n" + msg);
  }

  /**
   * Sets the handler to display the storage items for.
   *
   * @param value	the handler to use
   */
  public void setHandler(StorageHandler value) {
    if ((value != null) && (m_Handler != null)) {
      if (m_Handler.getStorage() == value.getStorage()) {
	if (!m_TableModel.isDifferent(value.getStorage()))
	  return;
      }
    }

    if (m_Handler != null)
      m_Handler.getStorage().removeChangeListener(this);
    m_Handler    = value;
    m_TableModel = new TableModel(value == null ? new Storage() : value.getStorage());
    m_Table.setModel(m_TableModel);
    m_Table.setOptimalColumnWidth();
    m_Handler.getStorage().addChangeListener(this);
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
   * Shows popup for table.
   *
   * @param e		the mouse event
   */
  protected void showTablePopup(MouseEvent e) {
    BasePopupMenu	menu;
    JMenuItem		menuitem;

    menu = new BasePopupMenu();

    menuitem = new JMenuItem("Copy name");
    menuitem.setEnabled(m_Table.getSelectedRowCount() == 1);
    menuitem.addActionListener((ActionEvent ae) -> copyName());
    menu.add(menuitem);

    if (m_TableModel.hasCacheData()) {
      menuitem = new JMenuItem("Copy cache");
      menuitem.setEnabled(m_Table.getSelectedRowCount() == 1);
      menuitem.addActionListener((ActionEvent ae) -> copyCache());
      menu.add(menuitem);
    }

    menu.addSeparator();

    menuitem = new JMenuItem("Inspect...");
    menuitem.setEnabled(m_Table.getSelectedRowCount() == 1);
    menuitem.addActionListener((ActionEvent ae) -> inspect());
    menu.add(menuitem);

    menuitem = new JMenuItem("Inspect (separate dialog)...");
    menuitem.setEnabled(m_Table.getSelectedRowCount() == 1);
    menuitem.addActionListener((ActionEvent ae) -> inspect(true));
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Edit...");
    menuitem.setEnabled(m_Table.getSelectedRowCount() == 1);
    menuitem.addActionListener((ActionEvent ae) -> edit());
    menu.add(menuitem);

    menuitem = new JMenuItem("Export...");
    menuitem.setEnabled(m_Table.getSelectedRowCount() == 1);
    menuitem.addActionListener((ActionEvent ae) -> export());
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Preview");
    menuitem.setEnabled(m_Table.getSelectedRowCount() == 1);
    menuitem.addActionListener((ActionEvent ae) -> updatePreview());
    menu.add(menuitem);

    menuitem = new JMenuItem("Preview (separate dialog)");
    menuitem.setEnabled(m_Table.getSelectedRowCount() == 1);
    menuitem.addActionListener((ActionEvent ae) -> newPreview());
    menu.add(menuitem);

    menu.show(m_Table.getComponent(), e.getX(), e.getY());
  }

  /**
   * Gets triggered when a storage item changed (added, modified, removed).
   *
   * @param e		the event
   */
  public void storageChanged(StorageChangeEvent e) {
    List<String> 	names;
    int			i;
    int			n;
    TIntList		rows;

    names = null;
    if (m_Table.getSelectedRow() > -1) {
      names = new ArrayList<>();
      for (int index: m_Table.getSelectedRows())
	names.add((String) m_Table.getValueAt(index, 1));
    }

    if (e.getType() == Type.MODIFIED) {
      m_TableModel.fireTableRowsUpdated(0, m_TableModel.getRowCount() - 1);
      updatePreview();
      updateInspection();
    }
    else {
      m_TableModel.fireTableDataChanged();
    }

    // reselect
    if (names != null) {
      rows = new TIntArrayList();
      for (i = 0; i < m_Table.getRowCount(); i++) {
	n = 0;
	while (n < names.size()) {
	  if (names.get(n).equals(m_Table.getValueAt(i, 1))) {
	    rows.add(i);
	    names.remove(n);
	    break;
	  }
	  else {
	    n++;
	  }
	}
	if (names.isEmpty())
	  break;
      }
      if (!rows.isEmpty()) {
	final int[] rowArray = rows.toArray();
	SwingUtilities.invokeLater(() -> m_Table.setSelectedRows(rowArray));
      }
    }
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (m_Handler != null)
      m_Handler.getStorage().removeChangeListener(this);
    if (m_PanelInspect != null) {
      m_PanelInspect.cleanUp();
      m_PanelInspect.closeParent();
      m_PanelInspect = null;
    }
  }
}
