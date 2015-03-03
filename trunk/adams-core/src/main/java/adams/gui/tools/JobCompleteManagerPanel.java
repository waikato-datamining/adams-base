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
 * JobCompleteManagerPanel.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import adams.core.Performance;
import adams.event.JobCompleteManagerChangeEvent;
import adams.event.JobCompleteManagerChangeListener;
import adams.event.JobCompleteManagerChangeEvent.Type;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTable;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.multiprocess.JobCompleteManager;
import adams.multiprocess.JobCompleteManager.JobCompleteInformation;

/**
 * A panel for displaying the JobComplete events.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JobCompleteManagerPanel
  extends BasePanel
  implements JobCompleteManagerChangeListener {

  /**
   * A table model for displaying informations.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class JobCompleteTableModel
    extends AbstractBaseTableModel {

    /** for serialization. */
    private static final long serialVersionUID = 3369969274010850739L;

    /** the underlying informations. */
    protected Vector<JobCompleteInformation> m_Informations;

    /**
     * Initializes the model.
     *
     * @param informations	the informations to use
     */
    public JobCompleteTableModel(Vector<JobCompleteInformation> informations) {
      super();
      set(informations);
    }

    /**
     * Returns the number of rows/informations.
     *
     * @return		the number of rows
     */
    public int getRowCount() {
      return m_Informations.size();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return		the number of columns
     */
    public int getColumnCount() {
      // 0. job class
      // 1. success
      // 2. job details
      // ---------
      // = 3
      return 3;
    }

    /**
     * Returns the name of the column.
     *
     * @param column	the column to retrieve the name for
     * @return		the name of the column
     */
    public String getColumnName(int column) {
      String	result;

      result = null;

      if (column == 0)
        result = "Job class";
      else if (column == 1)
        result = "Success";
      else if (column == 2)
        result = "Details";

      return result;
    }

    /**
     * Returns the value at the given position.
     *
     * @param row	the row in the table
     * @param column	the column in the table
     * @return		the value
     */
    public Object getValueAt(int row, int column) {
      Object	result;
      String	s;

      result = null;

      if (column == 0) {
	s      = m_Informations.get(row).getJobClass();
	s      = s.replaceAll(".*\\.", "");
	s      = s.replaceAll("\\$", "/");
	s      = s.replaceAll("Abstract", "");
	result = s;
      }
      else if (column == 1) {
	result = m_Informations.get(row).getJobResult().getSuccess();
      }
      else if (column == 2) {
	result = m_Informations.get(row).getJobDetails();
      }

      return result;
    }

    /**
     * Returns the class for the column.
     *
     * @param column	the column to retrieve the class for
     * @return		the class
     */
    public Class getColumnClass(int column) {
      Class	result;

      result = String.class;

      if (column == 0)
        result = String.class;
      else if (column == 1)
        result = Boolean.class;
      else if (column == 2)
        result = String.class;

      return result;
    }

    /**
     * Returns the underlying informations.
     *
     * @return		the informations
     */
    public Vector<JobCompleteInformation> getInformations() {
      return m_Informations;
    }

    /**
     * Sets the given informations.
     *
     * @param informations	the informations to use
     */
    public synchronized void set(Vector<JobCompleteInformation> informations) {
      m_Informations = new Vector<JobCompleteInformation>();
      synchronized(m_Informations) {
	if (informations != null)
	  m_Informations.addAll(informations);
      }
      fireTableDataChanged();
    }

    /**
     * Appends the given informations.
     *
     * @param informations	the informations to append
     */
    public synchronized void append(Vector<JobCompleteInformation> informations) {
      int	fromRow;
      int	toRow;

      synchronized(m_Informations) {
	fromRow = m_Informations.size();
	m_Informations.addAll(informations);
	toRow   = m_Informations.size() - 1;
      }
      fireTableRowsInserted(fromRow, toRow);
    }
  }

  /**
   * A table for displaying job complete informations.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class JobCompleteTable
    extends BaseTable {

    /** for serialization. */
    private static final long serialVersionUID = 1829018507165434360L;

    /**
     * default constructor.
     */
    public JobCompleteTable() {
      super(new JobCompleteTableModel(null));
    }

    /**
     * Sets the model and re-calculates the column widths.
     *
     * @param value	the model to use
     */
    public synchronized void setModel(TableModel value) {
      if (!(value instanceof JobCompleteTableModel))
	throw new IllegalArgumentException(
	    "Model must be of type " + JobCompleteTableModel.class.getName());

      super.setModel(value);
    }

    /**
     * Returns the underlying model.
     *
     * @return		the model
     */
    public JobCompleteTableModel getJobCompleteModel() {
      return (JobCompleteTableModel) getModel();
    }
  }

  /** for serialization. */
  private static final long serialVersionUID = -5632965108439630903L;

  /** the informations being displayed. */
  protected Vector<JobCompleteInformation> m_Informations;

  /** the manager. */
  protected JobCompleteManager m_Manager;

  /** the table holding the data. */
  protected JobCompleteTable m_TableInformations;

  /** the text area for displaying the job details. */
  protected JTextArea m_TextDetails;

  /** the clear button. */
  protected JButton m_ButtonClear;

  /** the close button. */
  protected JButton m_ButtonClose;

  /**
   * default constructor.
   */
  public JobCompleteManagerPanel() {
    super();

    m_Manager.addChangeListener(this);
    stateChanged(new JobCompleteManagerChangeEvent(m_Manager, Type.RESET));
  }

  /**
   * For initializing members.
   */
  protected void initialize() {
    super.initialize();

    m_Informations  = new Vector<JobCompleteInformation>();
    m_Manager = JobCompleteManager.getSingleton();
  }

  /**
   * For initializing the GUI.
   */
  protected void initGUI() {
    JPanel	panel;
    JPanel	panel2;

    super.initGUI();

    setLayout(new BorderLayout());

    // table + details
    panel2 = new JPanel(new GridLayout(2, 1, 0, 5));
    panel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    add(panel2, BorderLayout.CENTER);
    m_TableInformations = new JobCompleteTable();
    m_TableInformations.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_TableInformations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_TableInformations.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
	int index = m_TableInformations.getSelectedRow();

	if (MouseUtils.isDoubleClick(e)) {
	  if (index > -1)
	    showDetails(m_TableInformations.getJobCompleteModel().getInformations().get(index));
	}
      }
    });
    m_TableInformations.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	updateDetails(m_TableInformations.getJobCompleteModel().getInformations().get(
	    m_TableInformations.getSelectedRow()));
      }
    });
    panel2.add(new BaseScrollPane(m_TableInformations));

    m_TextDetails = new JTextArea();
    m_TextDetails.setFont(new Font("monospace", Font.PLAIN, 12));
    m_TextDetails.setEditable(false);
    panel2.add(new BaseScrollPane(m_TextDetails));

    // buttons
    panel2 = new JPanel(new BorderLayout());
    panel2.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    add(panel2, BorderLayout.SOUTH);
    panel = new JPanel(new BorderLayout());
    panel2.add(panel, BorderLayout.WEST);
    if (Performance.getKeepOnlyFailedJobComplete())
      panel.add(new JLabel("Only failed jobs listed"), BorderLayout.WEST);
    else
      panel.add(new JLabel("All finished jobs listed"), BorderLayout.WEST);
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel2.add(panel, BorderLayout.EAST);
    m_ButtonClear = new JButton("Clear", GUIHelper.getIcon("new.gif"));
    m_ButtonClear.setMnemonic('C');
    m_ButtonClear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        m_Manager.clear();
      }
    });

    m_ButtonClose = new JButton("Close", GUIHelper.getIcon("exit.png"));
    m_ButtonClose.setMnemonic('l');
    m_ButtonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        close();
      }
    });

    panel.add(m_ButtonClear);
    panel.add(m_ButtonClose);
  }

  /**
   * Closes the dialog/frame this panel belongs to.
   */
  public void close() {
    if (getParentFrame() != null) {
      getParentFrame().setVisible(false);
      getParentFrame().dispose();
    }
    else if (getParentDialog() != null) {
      getParentDialog().setVisible(false);
      getParentDialog().dispose();
    }
  }

  /**
   * Displays a dialog with the details of this event.
   *
   * @param information	the information to display
   */
  protected void showDetails(JobCompleteInformation information) {
    GUIHelper.showInformationMessage(
	this,
	information.getJobResult().toString(),
	"Job result details");
  }

  /**
   * Updates the text area with the details of this event. Use null to
   * clear the output.
   *
   * @param information	the information to display
   */
  protected void updateDetails(JobCompleteInformation information) {
    if (information == null)
      m_TextDetails.setText("");
    else
      m_TextDetails.setText(information.getJobResult().toString());
  }

  /**
   * Whenever a new JobComplete happened.
   *
   * @param e		the event
   */
  public synchronized void stateChanged(JobCompleteManagerChangeEvent e) {
    Vector<JobCompleteInformation>	informations;

    informations = e.getManager().getInformations();

    if (e.getType() == Type.RESET) {
      m_Informations = new Vector<JobCompleteInformation>();
      synchronized(m_Informations) {
        m_Informations.addAll(informations);
        synchronized(m_TableInformations) {
          m_TableInformations.getJobCompleteModel().set(m_Informations);
        }
      }
    }
    else {
      informations.removeAll(m_Informations);
      synchronized(m_Informations) {
	m_Informations.addAll(informations);
	synchronized(m_TableInformations) {
	  m_TableInformations.getJobCompleteModel().append(informations);
	}
      }
    }

    m_TableInformations.setOptimalColumnWidth();
  }
}
