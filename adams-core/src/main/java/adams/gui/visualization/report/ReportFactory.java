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
 * ReportFactory.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.report;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.AbstractOption;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.ReportProvider;
import adams.gui.chooser.AbstractReportFileChooser;
import adams.gui.chooser.DefaultReportFileChooser;
import adams.gui.chooser.TextFileChooser;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.CustomSearchTableModel;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SearchParameters;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeListener;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.ContainerModel;
import adams.gui.visualization.container.DataContainerPanel;
import adams.gui.visualization.container.DataContainerPanelWithSidePanel;
import adams.gui.visualization.container.NamedContainer;
import adams.gui.visualization.container.VisibilityContainer;
import adams.gui.visualization.container.VisibilityContainerManager;
import adams.gui.visualization.report.reportfactory.AbstractTableAction;
import adams.gui.visualization.report.reportfactory.AddField;
import adams.gui.visualization.report.reportfactory.CopyFieldName;
import adams.gui.visualization.report.reportfactory.CopyFieldValue;
import adams.gui.visualization.report.reportfactory.ExcludedFlag;
import adams.gui.visualization.report.reportfactory.ModifyValue;
import adams.gui.visualization.report.reportfactory.PrintReport;
import adams.gui.visualization.report.reportfactory.RemoveField;
import adams.gui.visualization.report.reportfactory.SaveReport;
import adams.gui.visualization.report.reportfactory.ViewValue;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A factory for GUI components for reports.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportFactory {

  /**
   * A specialized model for a Report.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Model
    extends AbstractBaseTableModel
    implements CustomSearchTableModel {

    /** for serialization. */
    private static final long serialVersionUID = -6741006047295351384L;

    /** the underlying report. */
    protected Report m_Report;

    /** for faster access, caching the fields. */
    protected List<AbstractField> m_Fields;

    /** the number of decimals for numeric values (-1 means all). */
    protected int m_NumDecimals;
    
    /**
     * Initializes the model.
     */
    public Model() {
      this(null);
    }
    
    /**
     * Initializes the model.
     *
     * @param report	the report to base the model on
     */
    public Model(Report report) {
      super();

      m_Report = report;
      if (m_Report != null)
	m_Fields = m_Report.getFields();
      else
	m_Fields = new ArrayList<AbstractField>();
      m_NumDecimals = -1;
    }

    /**
     * Returns the underlying report.
     *
     * @return		the report
     */
    public Report getReport() {
      return m_Report;
    }

    /**
     * Returns the number of rows/targets in the report.
     *
     * @return		the number of rows
     */
    public int getRowCount() {
      return m_Fields.size();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return		the number of columns
     */
    public int getColumnCount() {
      if (m_Report == null)
	return 0;
      else
	return 3;
    }

    /**
     * Returns the name of the result.
     *
     * @param column	the column to retrieve the name for
     * @return		the name of the column
     */
    @Override
    public String getColumnName(int column) {
      if (column == 0)
	return "Name";
      else if (column == 1)
	return "Type";
      else
	return "Value";
    }

    /**
     * Returns the value at the given position.
     *
     * @param row	the row in the table
     * @param column	the column in the table
     * @return		the value
     */
    public Object getValueAt(int row, int column) {
      Object		result;
      AbstractField	field;

      result = null;

      if (m_Report != null) {
	field = m_Fields.get(row);
	if (column == 0) {
	  result = field.toDisplayString();
	}
	else if (column == 1) {
	  result = field.getDataType().toString();
	}
	else {
	  if ((field.getDataType() == DataType.NUMERIC) && (m_NumDecimals > -1))
	    result = Utils.doubleToString(m_Report.getDoubleValue(field), m_NumDecimals);
	  else
	    result = m_Report.getStringValue(field);
	}
      }

      return result;
    }

    /**
     * Returns the class for the column.
     *
     * @param column	the column to retrieve the class for
     * @return		the class
     */
    @Override
    public Class getColumnClass(int column) {
      Class	result;

      if (column == 2)
	result = Object.class;
      else
	result = String.class;

      return result;
    }
    /**
     * Tests whether the search matches the specified row.
     *
     * @param params	the search parameters
     * @param row	the row of the underlying, unsorted model
     * @return		true if the search matches this row
     */
    public boolean isSearchMatch(SearchParameters params, int row) {
      return params.matches(m_Fields.get(row).getName());
    }
    
    /**
     * Sets the number of decimals to display for numeric values.
     * 
     * @param value	the number of decimals (use -1 to display all)
     */
    public void setNumDecimals(int value) {
      if (value >= -1) {
	m_NumDecimals = value;
	fireTableDataChanged();
      }
      else {
	System.err.println("Number of decimals need to be >= -1, provided: " + value);
      }
    }
    
    /**
     * Returns the number of decimals used to display numeric values.
     * 
     * @return		the number of decimals
     */
    public int getNumDecimals() {
      return m_NumDecimals;
    }
  }

  /**
   * A specialized table for displaying a Report.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Table<R extends ReportProvider<?,?>>
    extends SortableAndSearchableTable {

    /** for serialization. */
    private static final long serialVersionUID = -4065569582552285461L;

    /** an optional spectrum panel that owns the data. */
    protected DataContainerPanel m_DataContainerPanel;

    /** the file chooser for saving the report. */
    protected AbstractReportFileChooser m_FileChooser;

    /** the database connection. */
    protected AbstractDatabaseConnection m_DatabaseConnection;

    /** the structure of the popup menu. */
    protected String[] m_PopupActions;
    
    /**
     * Initializes the table.
     */
    public Table() {
      this((Report) null);
    }

    /**
     * Initializes the table.
     *
     * @param report	the report to base the table on
     */
    public Table(Report report) {
      this(new Model(report));
    }

    /**
     * Initializes the table.
     *
     * @param model	the model to use
     */
    public Table(TableModel model) {
      super(model);
    }

    /**
     * Initializes the table.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      m_DataContainerPanel = null;
      m_FileChooser        = newReportFileChooser();
      m_DatabaseConnection = DatabaseConnection.getSingleton();
      m_PopupActions       = getDefaultPopupActions();

      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      addMouseListener(new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent e) {
	  if (MouseUtils.isRightClick(e)) {
	    final int[] rows;
	    if (getSelectedRowCount() < 1)
	      rows = new int[]{rowAtPoint(e.getPoint())};
	    else
	      rows = getSelectedRows();
	    BasePopupMenu menu = getPopupMenu(rows);
	    if (menu != null) {
	      e.consume();
	      menu.showAbsolute(Table.this, e);
	    }
	  }

	  if (!e.isConsumed())
	    super.mouseClicked(e);
	}
      });
    }
    
    /**
     * Returns the file chooser to use for exporting the reports.
     *
     * @return		the filechooser, null if not available
     */
    protected AbstractReportFileChooser newReportFileChooser() {
      return new DefaultReportFileChooser();
    }

    /**
     * Returns the default actions for the popup menu.
     * 
     * @return		the default actions
     */
    protected String[] getDefaultPopupActions() {
      return new String[]{
	  CopyFieldName.class.getName(),
	  CopyFieldValue.class.getName(),
	  ModifyValue.class.getName(),
	  ViewValue.class.getName(),
	  AbstractTableAction.SEPARATOR,
	  RemoveField.class.getName(),
	  ExcludedFlag.class.getName(),
	  AddField.class.getName(),
	  AbstractTableAction.SEPARATOR,
	  SaveReport.class.getName(),
	  PrintReport.class.getName(),
      };    
    }
    
    /**
     * Sets the actions to use for the popup menu.
     * 
     * @param value	the array of actions
     * @see		AbstractTableAction
     */
    public void setPopupActions(String[] value) {
      m_PopupActions = value;
    }
    
    /**
     * Returns the actions used for the popup menu.
     * 
     * @return		the array of actions
     * @see		AbstractTableAction
     */
    public String[] getPopupActions() {
      return m_PopupActions;
    }
    
    /**
     * Returns the initial setting of whether to set optimal column widths.
     *
     * @return		true
     */
    @Override
    protected boolean initialUseOptimalColumnWidths() {
      return true;
    }

    /**
     * Returns the initial setting of whether to sort new models.
     *
     * @return		true
     */
    @Override
    protected boolean initialSortNewTableModel() {
      return true;
    }

    /**
     * Creates an empty default model.
     *
     * @return		the model
     */
    @Override
    protected TableModel createDefaultDataModel() {
      return new Model(null);
    }

    /**
     * Sets the report to display.
     *
     * @param value	the report to display
     */
    public void setReport(Report value) {
      Model	model;
      
      model = new Model(value);
      model.setNumDecimals(getNumDecimals());
      
      setUnsortedModel(model, true);
    }

    /**
     * Returns the underlying report.
     *
     * @return		the report.
     */
    public Report getReport() {
      return ((Model) getUnsortedModel()).getReport();
    }

    /**
     * Sets the underlying spectrum panel.
     *
     * @param value	the panel
     */
    public void setDataContainerPanel(DataContainerPanel value) {
      m_DataContainerPanel = value;
      if (m_DataContainerPanel != null)
	m_DatabaseConnection = m_DataContainerPanel.getDatabaseConnection();
    }

    /**
     * Returns the currently set spectrum panel.
     *
     * @return		the panel, can be null
     */
    public DataContainerPanel getDataContainerPanel() {
      return m_DataContainerPanel;
    }

    /**
     * Returns the provider for accessing the reports in the database.
     * <br><br>
     * The default implementation returns null.
     *
     * @return		the provider
     */
    public ReportProvider<?,?> getReportProvider() {
      return null;
    }

    /**
     * Returns the file chooser in use.
     * 
     * @return		the file chooser
     */
    public AbstractReportFileChooser getFileChooser() {
      return m_FileChooser;
    }

    /**
     * Returns the field at the given location.
     *
     * @param row	the row to get the field for
     * @return		the generated field
     */
    public AbstractField getFieldAt(int row) {
      AbstractField	result;
      String		field;
      String		type;

      field  = "" + getValueAt(row, 0);
      if (field.indexOf(AbstractField.SEPARATOR_DISPLAY) > -1)
	field = field.replace(AbstractField.SEPARATOR_DISPLAY, AbstractField.SEPARATOR);
      type   = "" + getValueAt(row, 1);
      result = new Field(field, DataType.valueOf((AbstractOption) null, type));

      return result;
    }

    /**
     * Returns a popup menu if appropriate.
     *
     * @param rows	the row that got the click or the currently selected rows
     * @return		the menu if appropriate, otherwise null
     */
    protected BasePopupMenu getPopupMenu(final int[] rows) {
      BasePopupMenu	result;

      result = null;

      if ((rows.length > 0) && (rows[0] > -1))
	result = AbstractTableAction.createPopup(m_PopupActions, this, rows);

      return result;
    }
    
    /**
     * Sets the number of decimals to display for numeric values.
     * 
     * @param value	the number of decimals (use -1 to display all)
     */
    public void setNumDecimals(int value) {
      if (getUnsortedModel() instanceof Model)
	((Model) getUnsortedModel()).setNumDecimals(value);
    }
    
    /**
     * Returns the number of decimals used to display numeric values.
     * 
     * @return		the number of decimals
     */
    public int getNumDecimals() {
      if (getUnsortedModel() instanceof Model)
	return ((Model) getUnsortedModel()).getNumDecimals();
      else
	return -1;
    }
  }

  /**
   * A specialized panel that displays reports.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   * @see Report
   * @param <C> the type of container to use
   * @param <M> the manager in use
   */
  public static class Panel<C extends ReportContainer, M extends ReportContainerManager>
    extends BasePanel
    implements DataChangeListener {

    /** for serialization. */
    private static final long serialVersionUID = -2563183937371175033L;

    /** the manager the tabbed pane listens to. */
    protected M m_ContainerManager;

    /** whether the manager is one handling visibility. */
    protected boolean m_VisibilityManager;

    /** the string that was searched for. */
    protected String m_SearchString;

    /** whether the last search was using regular expressions. */
    protected boolean m_RegExp;

    /** the split pane for table and list. */
    protected BaseSplitPane m_SplitPane;

    /** the panel for the table. */
    protected BasePanel m_PanelTable;

    /** the panel with the containers. */
    protected ReportContainerList m_ReportContainerList;

    /** the associated panel with the data. */
    protected DataContainerPanel m_DataContainerPanel;

    /**
     * Initializes the tabbed pane with not reports.
     */
    public Panel() {
      super();
    }

    /**
     * Performs further initializations.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_SearchString       = null;
      m_RegExp             = false;
      m_ContainerManager   = newContainerManager();
      m_VisibilityManager  = (m_ContainerManager instanceof VisibilityContainerManager);
      m_DataContainerPanel = null;
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      setLayout(new BorderLayout());

      m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
      m_SplitPane.setResizeWeight(1.0);
      m_SplitPane.setOneTouchExpandable(true);
      add(m_SplitPane, BorderLayout.CENTER);

      m_PanelTable = new BasePanel(new BorderLayout());
      m_SplitPane.setLeftComponent(m_PanelTable);

      m_ReportContainerList = new ReportContainerList();
      m_ReportContainerList.setManager(getContainerManager());
      m_ReportContainerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      m_ReportContainerList.addListSelectionListener(e -> showTable(m_ReportContainerList.getTable().getSelectedRow()));
      m_ReportContainerList.addTableModelListener(e ->  {
        if (m_ReportContainerList.getTable().getRowCount() == 0)
          showTable(-1);
      });
      m_SplitPane.setRightComponent(m_ReportContainerList);
    }

    /**
     * Sets the data container panel to use.
     *
     * @param value	the panel to use
     */
    public void setDataContainerPanel(DataContainerPanel value) {
      int	width;
      int	i;

      if (value != null) {
	if (value instanceof DataContainerPanelWithSidePanel) {
	  width = (int) ((DataContainerPanelWithSidePanel) value).getSidePanel().getPreferredSize().getWidth();
	  m_ReportContainerList.setPreferredSize(new Dimension(width, 0));
	}
	value.getContainerManager().addDataChangeListener(this);
	((ContainerModel) m_ReportContainerList.getTable().getModel()).setDisplayVisibility(true);
      }
      else {
	if (m_DataContainerPanel != null)
	  m_DataContainerPanel.getContainerManager().removeDataChangeListener(this);
	((ContainerModel) m_ReportContainerList.getTable().getModel()).setDisplayVisibility(false);
      }

      m_DataContainerPanel = value;
      for (i = 0; i < m_ReportContainerList.getManager().count(); i++) {
	if (m_ReportContainerList.getManager().get(i).hasComponent())
	  ((Table) m_ReportContainerList.getManager().get(i).getComponent()).setDataContainerPanel(value);
      }
    }

    /**
     * Returns the data container panel in use.
     *
     * @return		the panel, can be null if none set
     */
    public DataContainerPanel getDataContainerPanel() {
      return m_DataContainerPanel;
    }

    /**
     * Sets the location of the divider.
     *
     * @param value	the position in pixel
     */
    public void setDividerLocation(int value) {
      m_SplitPane.setDividerLocation(value);
    }

    /**
     * Sets the proportional location of the divider.
     *
     * @param value	the proportional position (0-1)
     */
    public void setDividerLocation(double value) {
      m_SplitPane.setDividerLocation(value);
    }

    /**
     * Returns the current location of the divider.
     *
     * @return		the position in pixel
     */
    public int getDividerLocation() {
      return m_SplitPane.getDividerLocation();
    }

    /**
     * Creates a new container manager.
     *
     * @return		the container manager
     */
    protected M newContainerManager() {
      if (getDataContainerPanel() != null)
	return (M) new ReportContainerManager(getDataContainerPanel().getDatabaseConnection());
      else
	return (M) new ReportContainerManager(DatabaseConnection.getSingleton());
    }

    /**
     * Returns the current manager listening to.
     *
     * @return		the manager, null if not yet set
     */
    public M getContainerManager() {
      return m_ContainerManager;
    }

    /**
     * Sets the manager to listen to.
     *
     * @param value	the manager
     */
    public void setContainerManager(M value) {
      if (m_ContainerManager != null)
	m_ContainerManager.removeDataChangeListener(this);

      m_ContainerManager  = value;
      m_VisibilityManager = false;

      if (m_ContainerManager != null) {
	m_ContainerManager.addDataChangeListener(this);
	m_VisibilityManager = (m_ContainerManager instanceof VisibilityContainerManager);
      }
    }

    /**
     * Returns the container list.
     *
     * @return		the panel with the list
     */
    public ReportContainerList getReportContainerList() {
      return m_ReportContainerList;
    }

    /**
     * Sets the preferred width of the ReportContainerList panel.
     *
     * @param width	the preferred width
     */
    public void setReportContainerListWidth(int width) {
      m_ReportContainerList.setPreferredSize(new Dimension(width, 0));
    }

    /**
     * Sets the data and reports.
     *
     * @param data	the spectrum containers containing the reports
     */
    public synchronized void setData(List<C> data) {
      List<ReportContainer>	conts;

      conts = new ArrayList<ReportContainer>();
      if (data != null) {
	for (C cont: data)
	  conts.add(cont);
      }

      m_ContainerManager.clear();
      m_ContainerManager.addAll(conts);
    }

    /**
     * Sets the reports. Note: there is not underlying data or container
     * panel available.
     *
     * @param data	the reports
     */
    public synchronized void setReports(List<Report> data) {
      List<ReportContainer>	conts;
      ReportContainer		rcont;

      conts = new ArrayList<ReportContainer>();
      if (data != null) {
	for (Report report: data) {
	  rcont = getContainerManager().newContainer(report);
	  rcont.setID("" + report.getDatabaseID());
	  conts.add(rcont);
	}
      }
      m_ContainerManager.clear();
      m_ContainerManager.addAll(conts);
    }

    /**
     * Returns the underlying data.
     *
     * @return		the spectrum containers
     */
    public List<C> getData() {
      List<C>	result;
      int	i;

      result = new ArrayList<C>();
      for (i = 0; i < getContainerManager().count(); i++)
	result.add((C) getContainerManager().get(i));

      return result;
    }

    /**
     * Performs a search for the given string. Limits the display of rows to
     * ones containing the search string.
     *
     * @param searchString	the string to search for
     * @param regexp		whether to perform regular expression matching
     * 				or just plain string comparison
     */
    public void search(String searchString, boolean regexp) {
      SortableAndSearchableTable	table;

      m_SearchString = searchString;
      m_RegExp       = regexp;
      table          = getCurrentTable();

      if (table != null)
	table.search(searchString, regexp);
    }

    /**
     * Returns the current search string.
     *
     * @return		the search string, null if not filtered
     */
    public String getSeachString() {
      return m_SearchString;
    }

    /**
     * Gets called if the data of the spectrum panel has changed.
     *
     * @param e		the event that the spectrum panel sent
     */
    public void dataChanged(DataChangeEvent e) {
      int[]		indices;
      int		i;
      AbstractContainer		cont;
      ReportContainer	rcont;
      Report		report;
      AbstractContainerManager	manager;

      indices = e.getIndices();
      manager = e.getManager();

      switch (e.getType()) {
	case CLEAR:
	  getContainerManager().clear();
	  break;

	case ADDITION:
	  for (i = 0; i < indices.length; i++) {
	    cont   = manager.get(indices[i]);
	    report = ((ReportHandler) cont.getPayload()).getReport();
	    if (report != null) {
	      rcont  = getContainerManager().newContainer(report);
	      rcont.setID(((NamedContainer) cont).getID());
	      if (cont instanceof VisibilityContainer)
		rcont.setVisible(((VisibilityContainer) cont).isVisible());
	      getContainerManager().add(rcont);
	    }
	  }
	  break;

	case REMOVAL:
	  for (i = indices.length - 1; i >= 0; i--)
	    getContainerManager().remove(indices[i]);
	  break;

	case REPLACEMENT:
	  for (i = 0; i < indices.length; i++) {
	    cont   = manager.get(indices[i]);
	    report = ((ReportHandler) cont.getPayload()).getReport();
	    rcont  = getContainerManager().newContainer(report);
	    rcont.setID(((NamedContainer) cont).getID());
	    if (cont instanceof VisibilityContainer)
	      rcont.setVisible(((VisibilityContainer) cont).isVisible());
	    getContainerManager().set(indices[i], rcont);
	  }
	  break;

	case VISIBILITY:
	  for (i = 0; i < indices.length; i++) {
	    cont = manager.get(indices[i]);
	    getContainerManager().setVisible(indices[i], ((VisibilityContainer) cont).isVisible());
	  }
	  break;

	case UPDATE:
	  for (i = 0; i < indices.length; i++) {
	    cont   = manager.get(indices[i]);
	    report = ((ReportHandler) cont.getPayload()).getReport();
	    rcont  = getContainerManager().get(indices[i]);
	    rcont.setPayload(report);
	    rcont.setID(((NamedContainer) cont).getID());
	    if (cont instanceof VisibilityContainer)
	      rcont.setVisible(((VisibilityContainer) cont).isVisible());
	    getContainerManager().set(indices[i], rcont);
	  }
	  break;

	case BULK_UPDATE:
	  getContainerManager().clear();
	  getContainerManager().startUpdate();
	  for (i = 0; i < manager.count(); i++) {
	    cont   = manager.get(i);
	    report = ((ReportHandler) cont.getPayload()).getReport();
	    rcont  = getContainerManager().newContainer(report);
	    rcont.setID(((NamedContainer) cont).getID());
	    getContainerManager().add(rcont);
	  }
	  getContainerManager().finishUpdate();
	  break;

	case SEARCH:
	  getContainerManager().clear();
	  getContainerManager().startUpdate();
	  for (i = 0; i < manager.count(); i++) {
	    cont   = manager.get(i);
	    report = ((ReportHandler) cont.getPayload()).getReport();
	    rcont  = getContainerManager().newContainer(report);
	    rcont.setID(((NamedContainer) cont).getID());
	    getContainerManager().add(rcont);
	  }
	  getContainerManager().finishUpdate();
	  break;
	  
	default:
	  throw new IllegalStateException("Unhandled data change event: " + e.getType());
      }
    }

    /**
     * Creates a new table.
     *
     * @param model	the model to use
     * @return		the new table
     */
    protected Table newTable(Model model) {
      return new Table(model);
    }

    /**
     * Creates a new table model from the report.
     *
     * @param report	the report to base the model on
     * @return		the new table model
     */
    protected Model newModel(Report report) {
      return new Model(report);
    }

    /**
     * Returns the table associated with the specified report.
     *
     * @param index	the index of the report
     * @return		the table
     */
    protected Table getTable(int index) {
      Table		result;
      ReportContainer	cont;

      cont = getContainerManager().get(index);
      if (!cont.hasComponent()) {
	result = newTable(newModel(cont.getReport()));
	result.setDataContainerPanel(getDataContainerPanel());
	cont.setComponent(result);
      }
      result = (Table) cont.getComponent();

      return result;
    }

    /**
     * Returns the title used in the border around the table with the report.
     *
     * @param index	the container/report to generate the title for
     * @return		the title
     */
    protected String getBorderTitle(int index) {
      return getContainerManager().get(index).getDisplayID();
    }

    /**
     * Selects and displays the selected report.
     *
     * @param index	the index of the report table to display, use <0 to
     * 			remove report from display
     */
    protected void selectTable(final int index) {
      Runnable	runnable;

      runnable = new Runnable() {
	public void run() {
	  m_ReportContainerList.getTable().getSelectionModel().addSelectionInterval(index, index);
	}
      };
      SwingUtilities.invokeLater(runnable);
    }

    /**
     * Displays the selected report.
     *
     * @param index	the index of the report table to display, use <0 to
     * 			remove report from display
     */
    protected void showTable(int index) {
      Table	table;

      if (index >= 0) {
	table = getTable(index);
	table.search(m_SearchString, m_RegExp);
      }
      else {
	table = null;
      }

      m_PanelTable.removeAll();

      if (table != null) {
	m_PanelTable.add(new BaseScrollPane(table), BorderLayout.CENTER);
	m_PanelTable.setBorder(BorderFactory.createTitledBorder(getBorderTitle(index)));
      }
      else {
	m_PanelTable.add(new JPanel(), BorderLayout.CENTER);
	m_PanelTable.setBorder(BorderFactory.createEmptyBorder());
      }

      m_PanelTable.doLayout();
    }

    /**
     * Returns the currently selected table.
     *
     * @return		the table, null if none selected
     */
    public Table getCurrentTable() {
      Table	result;

      result = null;

      if (m_ReportContainerList.getTable().getSelectedRow() != -1)
	result = getTable(m_ReportContainerList.getTable().getSelectedRow());

      return result;
    }

    /**
     * Returns the currently selected row.
     *
     * @return		the selected row, can be -1
     */
    public int getSelectedRow() {
      return m_ReportContainerList.getTable().getSelectedRow();
    }

    /**
     * Makes the specified report the current table. Also selects this entry
     * in the panel list.
     *
     * @param index	the index of the report to display
     */
    public void setCurrentTable(int index) {
      BaseTable		table;

      if (index < 0)
	return;

      showTable(index);

      table = m_ReportContainerList.getTable();
      table.getSelectionModel().clearSelection();
      table.getSelectionModel().setSelectionInterval(index, index);
    }
  }

  /**
   * A specialized dialog that displays reports.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   * @param <C> the type of container to use
   * @param <M> the manager in use
   */
  public static class Dialog<C extends ReportContainer, M extends ReportContainerManager>
    extends BaseDialog {

    /** for serialization. */
    private static final long serialVersionUID = 377068894443930941L;

    /** the dialog itself. */
    protected Dialog m_Self;

    /** the panel for displaying the reports. */
    protected Panel<C, M> m_Panel;

    /** the search panel. */
    protected SearchPanel m_SearchPanel;

    /**
     * Initializes the dialog.
     *
     * @param owner	the component that controls the dialog
     * @param modality	the type of modality
     */
    public Dialog(java.awt.Dialog owner, ModalityType modality) {
      super(owner, modality);
    }

    /**
     * Initializes the dialog.
     *
     * @param owner	the component that controls the dialog
     * @param modal	if true then the dialog will be modal
     */
    public Dialog(java.awt.Frame owner, boolean modal) {
      super(owner, modal);
    }

    /**
     * For initializing members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_Self = this;
    }

    /**
     * Returns a new tabbed pane instance.
     *
     * @return		the tabbed pane
     */
    protected Panel<C, M> newPanel() {
      return getPanel((List<ReportContainer>) null);
    }

    /**
     * Initializes the components.
     */
    @Override
    protected void initGUI() {
      JPanel	panel;

      super.initGUI();

      setTitle("Report");
      getContentPane().setLayout(new BorderLayout());

      // tabbed pane
      m_Panel = newPanel();
      getContentPane().add(m_Panel, BorderLayout.CENTER);

      // search
      m_SearchPanel = new SearchPanel(LayoutType.HORIZONTAL, true);
      m_SearchPanel.addSearchListener(e -> {
        m_Panel.search(
          m_SearchPanel.getSearchText(), m_SearchPanel.isRegularExpression());
        m_SearchPanel.grabFocus();
      });
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(m_SearchPanel);
      getContentPane().add(panel, BorderLayout.SOUTH);

      pack();
      setLocationRelativeTo(getOwner());
    }

    /**
     * Sets the underlying manager.
     *
     * @param value	the manager
     */
    public void setContainerManager(M value) {
      m_Panel.setContainerManager(value);
    }

    /**
     * Returns the underlying manager.
     *
     * @return		the manager, can be null if not set
     */
    public M getContainerManager() {
      return m_Panel.getContainerManager();
    }

    /**
     * Sets the data container panel to use.
     *
     * @param value	the panel to use
     */
    public void setDataContainerPanel(DataContainerPanel value) {
      m_Panel.setDataContainerPanel(value);
    }

    /**
     * Returns the data container panel in use.
     *
     * @return		the panel, can be null if none set
     */
    public DataContainerPanel getDataContainerPanel() {
      return m_Panel.getDataContainerPanel();
    }

    /**
     * Sets the divider location in pixel.
     *
     * @param value	the location in pixel
     */
    public void setDividerLocation(int value) {
      m_Panel.setDividerLocation(value);
    }

    /**
     * Sets the divider location as ration (0.0-1.0).
     *
     * @param value	the ratio
     */
    public void setDividerLocation(double value) {
      m_Panel.setDividerLocation(value);
    }

    /**
     * Returns the current divider location.
     *
     * @return		the location in pixel
     */
    public int getDividerLocation() {
      return m_Panel.getDividerLocation();
    }

    /**
     * Sets the preferred width of the ReportContainerList panel.
     *
     * @param width	the preferred width
     */
    public void setReportContainerListWidth(int width) {
      m_Panel.setReportContainerListWidth(width);
    }

    /**
     * Sets the data to display.
     *
     * @param value	the underlying containers
     */
    public synchronized void setData(List<C> value) {
      m_Panel.setData(value);

      if (!isVisible()) {
	pack();
	setLocationRelativeTo(getOwner());
	GUIHelper.setSizeAndLocation(this, this);
      }
    }

    /**
     * Returns the underlying data.
     *
     * @return		the containers
     */
    public List<C> getData() {
      return m_Panel.getData();
    }

    /**
     * Hook method just before the dialog is made visible.
     */
    @Override
    protected void beforeShow() {
      M		manager;
      C		cont;
      int	index;

      super.beforeShow();

      manager = m_Panel.getContainerManager();
      if (manager.countVisible() > 0) {
	cont  = (C) manager.getVisible(0);
	index = manager.indexOf(cont);
	if (index > -1) {
	  m_Panel.showTable(index);
	  pack();
	  m_Panel.selectTable(index);
	}
      }

      m_SearchPanel.grabFocus();
    }
  }

  /**
   * A specialized dialog that lets the user choose the field and minimum
   * value for the standards to load.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class StandardsDialog
    extends BaseDialog {

    /** for serialization. */
    private static final long serialVersionUID = -4658524307287732983L;

    /** the dialog itself. */
    protected StandardsDialog m_Self;

    /** the panel with the values. */
    protected JPanel m_PanelValues;

    /** the label for the compounds. */
    protected JLabel m_LabelCompounds;

    /** for combobox with the compounds. */
    protected JComboBox m_ComboBoxCompounds;

    /** the label for the min value text field. */
    protected JLabel m_LabelMinValue;

    /** the text field for the minimum value. */
    protected JTextField m_TextMinValue;

    /** the label for the max value text field. */
    protected JLabel m_LabelMaxValue;

    /** the text field for the maximum value. */
    protected JTextField m_TextMaxValue;

    /** for Add button. */
    protected JButton m_ButtonAdd;

    /** for Add button. */
    protected JButton m_ButtonRemove;

    /** for OK button. */
    protected JButton m_ButtonOK;

    /** for Cancel button. */
    protected JButton m_ButtonCancel;

    /** the selected compound. */
    protected Field m_SelectedCompound;

    /** the listbox with the selected compound/min/max items. */
    protected JList m_ListCompounds;

    /** the list model for the compounds. */
    protected DefaultListModel m_ModelCompounds;

    /** whether the dialog was canceled. */
    protected boolean m_Canceled;

    /**
     * Initializes the dialog.
     *
     * @param owner	the component that controls the dialog
   * @param modality	the type of modality
     */
    public StandardsDialog(java.awt.Dialog owner, ModalityType modality) {
      super(owner, modality);
    }

    /**
     * Initializes the dialog.
     *
     * @param owner	the component that controls the dialog
     * @param modal	if true then the dialog will be modal
     */
    public StandardsDialog(java.awt.Frame owner, boolean modal) {
      super(owner, modal);
    }

    /**
     * For initializing members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_Self = this;
    }

    /**
     * Initializes the components.
     */
    @Override
    protected void initGUI() {
      JPanel	panel;

      super.initGUI();

      setTitle("Standards");
      getContentPane().setLayout(new BorderLayout());

      m_PanelValues = new JPanel(new GridLayout(4, 1));
      getContentPane().add(m_PanelValues, BorderLayout.WEST);

      // the compounds
      m_ComboBoxCompounds = new JComboBox(new DefaultComboBoxModel());
      m_LabelCompounds = new JLabel("Compound");
      m_LabelCompounds.setDisplayedMnemonic('C');
      m_LabelCompounds.setLabelFor(m_ComboBoxCompounds);
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      m_PanelValues.add(panel);
      panel.add(m_LabelCompounds);
      panel.add(m_ComboBoxCompounds);

      // the min value
      m_TextMinValue = new JTextField(10);
      m_TextMinValue.setToolTipText("Use -1 to ignore - gets automatically ignored if no compound selected");
      m_LabelMinValue = new JLabel("Minimum value");
      m_LabelMinValue.setDisplayedMnemonic('i');
      m_LabelMinValue.setLabelFor(m_TextMinValue);
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      m_PanelValues.add(panel);
      panel.add(m_LabelMinValue);
      panel.add(m_TextMinValue);

      // the max value
      m_TextMaxValue = new JTextField(10);
      m_TextMaxValue.setToolTipText("Use -1 to ignore - gets automatically ignored if no compound selected");
      m_LabelMaxValue = new JLabel("Maximum value");
      m_LabelMaxValue.setDisplayedMnemonic('a');
      m_LabelMaxValue.setLabelFor(m_TextMaxValue);
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      m_PanelValues.add(panel);
      panel.add(m_LabelMaxValue);
      panel.add(m_TextMaxValue);

      // the add button
      m_ButtonAdd = new JButton("Add");
      m_ButtonAdd.setMnemonic('A');
      m_ButtonAdd.addActionListener(e -> {
        if (m_ComboBoxCompounds.getSelectedIndex() == -1)
          return;
        double min = -1;
        double max = -1;
        if (m_TextMinValue.getText().length() > 0)
          min = Utils.toDouble(m_TextMinValue.getText());
        if (m_TextMaxValue.getText().length() > 0)
          max = Utils.toDouble(m_TextMaxValue.getText());
        m_ModelCompounds.addElement(m_ComboBoxCompounds.getSelectedItem() + " " + min + " " + max);
      });
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(m_ButtonAdd);
      m_PanelValues.add(panel);

      // the list
      m_ModelCompounds = new DefaultListModel();
      m_ModelCompounds.addListDataListener(new ListDataListener() {
	public void contentsChanged(ListDataEvent e) {
	  update();
	}
	public void intervalAdded(ListDataEvent e) {
	  update();
	}
	public void intervalRemoved(ListDataEvent e) {
	  update();
	}
	protected void update() {
	  m_ButtonOK.setEnabled(m_ModelCompounds.getSize() > 0);
	}
      });
      m_ListCompounds = new JList(m_ModelCompounds);
      m_ListCompounds.addListSelectionListener(e -> m_ButtonRemove.setEnabled(m_ListCompounds.getSelectedIndices().length > 0));
      panel = new JPanel(new BorderLayout());
      panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      panel.add(new BaseScrollPane(m_ListCompounds), BorderLayout.CENTER);
      getContentPane().add(panel, BorderLayout.CENTER);

      // the remove button
      m_ButtonRemove = new JButton("Remove");
      m_ButtonRemove.setMnemonic('R');
      m_ButtonRemove.setEnabled(false);
      m_ButtonRemove.addActionListener(e -> {
        int[] indices = m_ListCompounds.getSelectedIndices();
        for (int i = indices.length - 1; i >= 0; i--)
          m_ModelCompounds.remove(indices[i]);
      });
      panel = new JPanel(new BorderLayout());
      panel.add(m_ButtonRemove, BorderLayout.NORTH);
      panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      getContentPane().add(panel, BorderLayout.EAST);

      // buttons
      panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(panel, BorderLayout.SOUTH);

      m_ButtonOK = new JButton("OK");
      m_ButtonOK.setMnemonic('O');
      m_ButtonOK.setEnabled(false);
      m_ButtonOK.addActionListener(e -> {
        m_Canceled = false;
        if (m_ComboBoxCompounds.getSelectedIndex() > 0)
          m_SelectedCompound = (Field) m_ComboBoxCompounds.getSelectedItem();
        m_Self.setVisible(false);
      });
      panel.add(m_ButtonOK);

      m_ButtonCancel = new JButton("Cancel", GUIHelper.getIcon("exit.png"));
      m_ButtonCancel.setMnemonic('a');
      m_ButtonCancel.addActionListener(e -> m_Self.setVisible(false));
      panel.add(m_ButtonCancel);

      pack();

      // adjust label sizes
      m_LabelCompounds.setPreferredSize(m_LabelMaxValue.getPreferredSize());
      m_LabelMinValue.setPreferredSize(m_LabelMaxValue.getPreferredSize());

      setLocationRelativeTo(getOwner());
    }

    /**
     * Sets the compounds to display.
     *
     * @param value	the underlying compounds
     */
    public synchronized void setCompounds(List<Field> value) {
      List<Field>	list;

      list = new ArrayList<Field>();
      list.add(new Field());
      list.addAll(value);
      m_ComboBoxCompounds.setModel(new DefaultComboBoxModel(list.toArray(new Field[list.size()])));

      if (!isVisible()) {
	pack();
	setLocationRelativeTo(getOwner());
	GUIHelper.setSizeAndLocation(this, this);
      }
    }

    /**
     * Returns the underlying compounds.
     *
     * @return		the compounds
     */
    public List<Field> getCompounds() {
      List<Field>		result;
      DefaultComboBoxModel	model;
      int			i;

      result = new ArrayList<Field>();
      model  = (DefaultComboBoxModel) m_ComboBoxCompounds.getModel();
      for (i = 1; i < model.getSize(); i++)
	result.add((Field) model.getElementAt(i));

      return result;
    }

    /**
     * Sets the minimum value to display.
     *
     * @param value	the minimum value, use null to no default value
     */
    public void setMinValue(Double value) {
      if (value == null)
	m_TextMinValue.setText("");
      else
	m_TextMinValue.setText(value.toString());
    }

    /**
     * Returns the current minimum value.
     *
     * @return		the minimum value, can be null
     */
    public Double getMinValue() {
      Double	result;

      result = null;

      if (m_TextMinValue.getText().length() != 0) {
	try {
	  result = Double.parseDouble(m_TextMinValue.getText());
	}
	catch (Exception e) {
	  result = null;
	  e.printStackTrace();
	}
      }

      return result;
    }

    /**
     * Sets the maximum value to display.
     *
     * @param value	the maximum value, use null to no default value
     */
    public void setMaxValue(Double value) {
      if (value == null)
	m_TextMaxValue.setText("");
      else
	m_TextMaxValue.setText(value.toString());
    }

    /**
     * Returns the current maximum value.
     *
     * @return		the maximum value, can be null
     */
    public Double getMaxValue() {
      Double	result;

      result = null;

      if (m_TextMaxValue.getText().length() != 0) {
	try {
	  result = Double.parseDouble(m_TextMaxValue.getText());
	}
	catch (Exception e) {
	  result = null;
	  e.printStackTrace();
	}
      }

      return result;
    }

    /**
     * Returns the selected compound (only set if OK clicked).
     *
     * @return		the selected compound, can be null
     */
    public Field getSelectedCompound() {
      return m_SelectedCompound;
    }

    /**
     * Returns the setup, i.e., the compound/min/max items.
     *
     * @return		the setup
     */
    public List<String> getSetup() {
      List<String>	result;
      int		i;

      result = new ArrayList<String>();

      for (i = 0; i < m_ModelCompounds.getSize(); i++)
	result.add((String) m_ModelCompounds.get(i));

      return result;
    }

    /**
     * Returns whether the dialog was canceled or not.
     *
     * @return		true if the dialog was canceled
     */
    public boolean getCanceled() {
      return m_Canceled;
    }

    /**
     * Hook method just before the dialog is made visible.
     */
    @Override
    protected void beforeShow() {
      super.beforeShow();

      m_SelectedCompound = null;
      m_Canceled         = true;
    }
  }

  /**
   * Returns a new model for the given report.
   *
   * @param report	the report to create a model for
   * @return		the model
   */
  public static Model getModel(Report report) {
    return new Model(report);
  }

  /**
   * Returns a new table for the given report.
   *
   * @param report	the report to create a table for
   * @return		the table
   */
  public static Table getTable(Report report) {
    return new Table(report);
  }

  /**
   * Returns a new panel for the given report.
   *
   * @param report	the report to create a table/panel for
   * @param preview	whether to add a preview text field for the cell value
   * @return		the panel
   */
  public static BasePanel getPanel(Report report, boolean preview) {
    final BasePanel			result;
    final Table				table;
    JPanel				panel;
    JPanel				panelTop;
    JPanel				panelBottom;
    BaseSplitPane			split;
    final BaseTextAreaWithButtons	textArea;
    JButton				button;

    result = new BasePanel(new BorderLayout());
    result.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    split = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    split.setOneTouchExpandable(true);
    result.add(split, BorderLayout.CENTER);

    panelTop = new JPanel(new BorderLayout());
    split.setTopComponent(panelTop);

    panelBottom = new JPanel(new BorderLayout());
    split.setBottomComponent(panelBottom);

    // table
    table = new Table(new Model(report));
    panelTop.add(new BaseScrollPane(table), BorderLayout.CENTER);

    // search
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelTop.add(panel, BorderLayout.SOUTH);
    final SearchPanel searchPanel = new SearchPanel(LayoutType.HORIZONTAL, true);
    searchPanel.addSearchListener(e -> {
      table.search(searchPanel.getSearchText(), searchPanel.isRegularExpression());
      searchPanel.grabFocus();
    });
    panel.add(searchPanel);

    // preview
    if (preview) {
      textArea = new BaseTextAreaWithButtons(5, 40);
      textArea.setFont(Fonts.getMonospacedFont());
      panelBottom.add(new BaseScrollPane(textArea), BorderLayout.CENTER);
      table.getSelectionModel().addListSelectionListener(e -> {
        if (table.getSelectedRows().length == 1) {
          Object value = table.getValueAt(table.getSelectedRow(), 2);
          textArea.setText((value == null ? "" : value.toString()));
          textArea.setCaretPosition(0);
	}
      });
      button = new JButton("Copy", GUIHelper.getIcon("copy.gif"));
      button.addActionListener(e -> GUIHelper.copyToClipboard(textArea.getText()));
      textArea.addToButtonsPanel(button);
      button = new JButton("Save as...", GUIHelper.getIcon("save.gif"));
      button.addActionListener(e -> {
        TextFileChooser fileChooser = new TextFileChooser();
        int retVal = fileChooser.showSaveDialog(result);
        if (retVal != TextFileChooser.APPROVE_OPTION)
          return;
        String encoding = fileChooser.getEncoding();
        String filename = fileChooser.getSelectedFile().getAbsolutePath();
        if (!FileUtils.writeToFile(filename, textArea.getText(), false, encoding))
          GUIHelper.showErrorMessage(result, "Failed to save text to file: " + filename);
      });
      textArea.addToButtonsPanel(button);
    }

    return result;
  }

  /**
   * Returns a new table for the given reports.
   *
   * @param reports	the reports to create a tabbed pane for
   * @return		the tabbed pane
   */
  public static Panel getPanel(List<ReportContainer> reports) {
    Panel	result;

    result = new Panel();
    result.setData(reports);

    return result;
  }

  /**
   * Returns a new table for the given reports.
   *
   * @param reports	the reports to create a tabbed pane for
   * @return		the tabbed pane
   */
  public static Panel getPanelForReports(List reports) {
    Panel	result;

    result = new Panel();
    result.setReports(reports);

    return result;
  }

  /**
   * Returns a new dialog for displaying reports.
   *
   * @param owner	the owning component
   * @param modality	the type of modality
   * @return		the dialog
   */
  public static Dialog getDialog(java.awt.Dialog owner, ModalityType modality) {
    return new Dialog(owner, modality);
  }

  /**
   * Returns a new dialog for displaying reports.
   *
   * @param owner	the owning component
   * @param modal	if true then the dialog will be modal
   * @return		the dialog
   */
  public static Dialog getDialog(java.awt.Frame owner, boolean modal) {
    return new Dialog(owner, modal);
  }

  /**
   * Returns a new standards dialog for loading .
   *
   * @param owner	the owning component
   * @param modality	the type of modality
   * @return		the dialog
   */
  public static StandardsDialog getStandardsDialog(java.awt.Dialog owner, ModalityType modality) {
    return new StandardsDialog(owner, modality);
  }

  /**
   * Returns a new dialog for displaying reports.
   *
   * @param owner	the owning component
   * @param modal	whether modal or not
   * @return		the dialog
   */
  public static StandardsDialog getStandardsDialog(java.awt.Frame owner, boolean modal) {
    return new StandardsDialog(owner, modal);
  }
}
