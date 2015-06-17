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
 * TimeseriesImportDatabaseDialog.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.timeseries;

import adams.core.Constants;
import adams.core.Properties;
import adams.db.DatabaseConnection;
import adams.db.SQL;
import adams.gui.core.BaseDialog;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.wizard.AbstractDatabaseConnectionPage.DatabaseConnectionPageCheck;
import adams.gui.wizard.AbstractWizardPage;
import adams.gui.wizard.DatabaseConnectionPage;
import adams.gui.wizard.ListPage;
import adams.gui.wizard.PageCheck;
import adams.gui.wizard.ParameterPanelPage;
import adams.gui.wizard.ProceedAction;
import adams.gui.wizard.StartPage;
import adams.gui.wizard.WizardPane;

import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Dialog for import timeseries from a database.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesImportDatabaseDialog
  extends BaseDialog {

  /** for serialization. */
  private static final long serialVersionUID = 8033226799557584214L;

  /** the approve option. */
  public final static int APPROVE_OPTION = JOptionPane.YES_OPTION;

  /** the cancel option. */
  public final static int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;
  
  /** key for query listing the IDs. */
  public static final String QUERY_IDS = "queryIDs";

  /** key for data query. */
  public static final String QUERY_DATA = "queryData";

  /** key for the meta-data query (key/value). */
  public static final String QUERY_METADATA_KEYVALUE = "queryMetaDataKeyValue";

  /** key for the meta-data query (row). */
  public static final String QUERY_METADATA_ROW = "queryMetaDataRow";
  
  /** the wizard. */
  protected WizardPane m_PaneWizard;

  /** the connection page. */
  protected DatabaseConnectionPage m_PageConnection;

  /** the queries page. */
  protected ParameterPanelPage m_PageQueries;

  /** the ID list page. */
  protected ListPage m_PageIDs;

  /** the option selected by the user (CANCEL_OPTION, APPROVE_OPTION). */
  protected int m_Option;

  /**
   * Creates a modeless dialog without a title and without a specified Frame
   * owner.
   */
  public TimeseriesImportDatabaseDialog() {
    super();
  }

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public TimeseriesImportDatabaseDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public TimeseriesImportDatabaseDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public TimeseriesImportDatabaseDialog(Dialog owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified title, modality and the specified
   * owner Dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   * @param modality	the type of modality
   */
  public TimeseriesImportDatabaseDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public TimeseriesImportDatabaseDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public TimeseriesImportDatabaseDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public TimeseriesImportDatabaseDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public TimeseriesImportDatabaseDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Option = CANCEL_OPTION;
  }
  
  /**
   * Initializes the widgets.
   */
  @SuppressWarnings("serial")
  @Override
  protected void initGUI() {
    StartPage		start;
    ParameterPanelPage	param;
    Properties 		props;

    super.initGUI();
    
    setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);
    
    m_PaneWizard = new WizardPane();
    m_PaneWizard.setCustomFinishText("Load");
    
    start = new StartPage();
    start.setLogo(null);
    start.setPageName("Start");
    start.setDescription(
	"<html>\n"
	+ "This wizard will guide you through the process of loading "
	+ "timeseries data from a database using SQL queries. Optional "
	+ "meta-data can be added to the data using further queries.<br>\n"
	+ "<br>\n"
	+ "You can save/load setups which will allow you to keep track "
	+ "of the various SQL statements for different databases. Just use "
	+ "the load/save buttons at the bottom of a page.<br>\n"
	+ "<br>\n"
	+ "Please click on <b>Next</b> to continue.</html>");
    m_PaneWizard.addPage(start);
    
    m_PageConnection = new DatabaseConnectionPage();
    m_PageConnection.setPageCheck(new DatabaseConnectionPageCheck());
    m_PaneWizard.addPage(m_PageConnection);
    
    param = new ParameterPanelPage();
    param.setPageName("Queries");
    param.setDescription(
	"Please enter the queries to retrieve data from the database. You can "
	+ "use the placeholder '" + Constants.PLACEHOLDER_ID + "' in your SQL statements for reading the "
	+ "timeseries data and meta-data.\n"
	+ "If no meta-data available, leave the meta-data statements empty. "
	+ "Otherwise, either use the statement that returns mulitple rows of "
	+ "key-value pairs or the one that returns a single row with all the "
	+ "meta-data columns (uses the column name as key).");
    param.getParameterPanel().addPropertyType(QUERY_IDS, PropertyType.SQL);
    param.getParameterPanel().setLabel(QUERY_IDS, "Listing the IDs");
    param.getParameterPanel().addPropertyType(QUERY_DATA, PropertyType.SQL);
    param.getParameterPanel().setLabel(QUERY_DATA, "Timeseries data");
    param.getParameterPanel().addPropertyType(QUERY_METADATA_KEYVALUE, PropertyType.SQL);
    param.getParameterPanel().setLabel(QUERY_METADATA_KEYVALUE, "Meta-data (key-value)");
    param.getParameterPanel().addPropertyType(QUERY_METADATA_ROW, PropertyType.SQL);
    param.getParameterPanel().setLabel(QUERY_METADATA_ROW, "Meta-data (row)");
    param.getParameterPanel().setPropertyOrder(new String[]{
	QUERY_IDS,
	QUERY_DATA,
	QUERY_METADATA_KEYVALUE,
	QUERY_METADATA_ROW,
    });
    props = new Properties();
    props.setProperty(QUERY_IDS, "select ID from table1 order by ID");
    props.setProperty(QUERY_DATA, "select timestamp,value from table1 where ID = " + Constants.PLACEHOLDER_ID);
    props.setProperty(QUERY_METADATA_KEYVALUE, "select key,value from table2 where ID = " + Constants.PLACEHOLDER_ID);
    props.setProperty(QUERY_METADATA_ROW, "select * from table2 where ID = " + Constants.PLACEHOLDER_ID);
    param.setProperties(props);
    param.getParameterPanel().setDefaultSQLDimension(new Dimension(200, 80));
    param.setProceedAction(new ProceedAction() {
      @Override
      public void onProceed(AbstractWizardPage currPage, AbstractWizardPage nextPage) {
	Properties props = m_PageConnection.getProperties();
	DatabaseConnection conn = new DatabaseConnection(
	    props.getProperty(DatabaseConnectionPage.CONNECTION_URL), 
	    props.getProperty(DatabaseConnectionPage.CONNECTION_USER), 
	    props.getPassword(DatabaseConnectionPage.CONNECTION_PASSWORD));
	try {
	  conn.connect();
	  props = m_PageQueries.getProperties();
	  SQL sql = new SQL(conn);
	  ResultSet rs = sql.getResultSet(props.getProperty(QUERY_IDS));
	  List<String> ids = new ArrayList<String>();
	  while (rs.next())
	    ids.add("" + rs.getObject(1));
	  SQL.closeAll(rs);
	  m_PageIDs.setValues(ids);
	}
	catch (Exception e) {
          currPage.getLogger().log(Level.SEVERE, "Failed to retrieve IDs!", e);
	}
      }
    });
    m_PaneWizard.addPage(param);
    m_PageQueries = param;

    m_PageIDs = new ListPage();
    m_PageIDs.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    m_PageIDs.setPageName("Select IDs");
    m_PageIDs.setDescription("Please select the IDs of the timeseries to load.");
    m_PageIDs.setPageCheck(new PageCheck<ListPage>() {
      @Override
      public boolean checkPage(ListPage page) {
        return (page.getList().getSelectedIndices().length > 0);
      }
    });
    m_PaneWizard.addPage(m_PageIDs);
    
    getContentPane().add(m_PaneWizard, BorderLayout.CENTER);
    
    m_PaneWizard.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand().equals(WizardPane.ACTION_FINISH))
	  m_Option = APPROVE_OPTION;
	setVisible(false);
      }
    });
  }

  /**
   * Returns whether the user approved or canceled the dialog.
   *
   * @return		the result
   * @see		#APPROVE_OPTION
   * @see		#CANCEL_OPTION
   */
  public int getOption() {
    return m_Option;
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  @Override
  protected void beforeShow() {
    super.beforeShow();

    m_Option = CANCEL_OPTION;
  }

  /**
   * Returns the wizard pane.
   * 
   * @return		the wizard
   */
  public WizardPane getWizard() {
    return m_PaneWizard;
  }
  
  /**
   * Returns the wizard page for the connection data.
   * 
   * @return		the page
   */
  public DatabaseConnectionPage getConnectionPage() {
    return m_PageConnection;
  }
  
  /**
   * Returns the wizard page for the queries.
   * 
   * @return		the page
   */
  public ParameterPanelPage getQueriesPage() {
    return m_PageQueries;
  }
  
  /**
   * Returns the wizard page for the IDs.
   * 
   * @return		the page
   */
  public ListPage getIDsPage() {
    return m_PageIDs;
  }

  /**
   * Returns the properties from all the pages.
   * 
   * @param usePrefix	whether to use the page name as prefix
   * @return		the combined properties
   */
  public Properties getProperties(boolean usePrefix) {
    return m_PaneWizard.getProperties(usePrefix);
  }
}
