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
 * InformativeStatisticFactory.java
 * Copyright (C) 2008-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.statistics;


import adams.data.statistics.InformativeStatistic;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseMultiPagePane;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTable;
import adams.gui.core.GUIHelper;
import adams.gui.core.SortableAndSearchableTable;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A factory for GUI components for InformativeStatistic objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InformativeStatisticFactory {

  /**
   * A specialized model for an object implementing the InformativeStatistic
   * interface.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Model
    extends AbstractBaseTableModel {

    /** for serialization. */
    private static final long serialVersionUID = -248671609209397527L;

    /** the underlying object. */
    protected InformativeStatistic m_Statistic;

    /** the data retrieved from the statistics object. */
    protected Object[][] m_Data;

    /**
     * Initializes the model.
     *
     * @param stat	the statistics object
     */
    public Model(InformativeStatistic stat) {
      super();

      m_Statistic = stat;

      initialize();
    }

    /**
     * Retrieves the data from the statistic object.
     */
    protected void initialize() {
      List<String>	names;
      Iterator<String>	enm;
      int		i;

      names = new ArrayList<String>();
      enm   = m_Statistic.statisticNames();
      while (enm.hasNext())
        names.add(enm.next());

      m_Data = new Object[names.size()][2];
      for (i = 0; i < names.size(); i++) {
        m_Data[i][0] = names.get(i);
        m_Data[i][1] = m_Statistic.getStatistic(names.get(i));
      }
    }

    /**
     * Returns the underlying chromatogram.
     *
     * @return		the data
     */
    public InformativeStatistic getStatistic() {
      return m_Statistic;
    }

    /**
     * Returns the number of rows/statistics.
     *
     * @return		the number of rows
     */
    public int getRowCount() {
      return m_Data.length;
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return		the number of columns
     */
    public int getColumnCount() {
      // 0. name
      // 1. value
      // ---------
      // = 2
      return 2;
    }

    /**
     * Returns the name of the column.
     *
     * @param column	the column to retrieve the name for
     * @return		the name of the column
     */
    @Override
    public String getColumnName(int column) {
      String	result;

      result = null;

      if (column == 0)
        result = "Statistic";
      else if (column == 1)
        result = "Value";

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
      return m_Data[row][column];
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

      result = String.class;

      if (column == 0)
        result = String.class;
      else if (column == 1)
        result = Double.class;

      return result;
    }
  }

  /**
   * A specialized table for displaying an object that implements the
   * InformativeStatistic interface.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   * @see     InformativeStatistic
   */
  public static class Table
    extends SortableAndSearchableTable {

    /** for serialization. */
    private static final long serialVersionUID = 344016476619814279L;

    /**
     * Initializes the table.
     *
       * @param stat	the statistics object
     */
    public Table(InformativeStatistic stat) {
      super(new Model(stat));

      setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      setOptimalColumnWidth();
      setShowSimpleCellPopupMenu(true);
    }

    /**
     * Sets the model to display - only QuantitationReportModel.
     *
     * @param model	the model to display
     */
    @Override
    public void setModel(TableModel model) {
      if (!(model instanceof Model))
        throw new IllegalArgumentException(
  	  "Only models of type " + Model.class.getName()
  	  + " are allowed!");

      super.setModel(model);

      setOptimalColumnWidth();
    }

    /**
     * Sets the statistics object to display.
     *
     * @param stat	the statistics object
     */
    public void setStatistic(InformativeStatistic stat) {
      setModel(new Model(stat));
    }

    /**
     * Returns the underlying statistics object.
     *
     * @return		the statistics object
     */
    public InformativeStatistic getStatistic() {
      return ((Model) getModel()).getStatistic();
    }
  }

  /**
   * A specialized multi-page pane that displays statistics.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class MultiPagePane
    extends BaseMultiPagePane {

    /** for serialization. */
    private static final long serialVersionUID = 3805619139057394474L;

    /** the underlying data. */
    protected List<InformativeStatistic> m_Statistics;

    /**
     * Initializes the tabbed pane with not statistics.
     */
    public MultiPagePane() {
      this(null);
    }

    /**
     * Initializes the tabbed pane with the statistics.
     *
     * @param stats	the underlying statistics
     */
    public MultiPagePane(List<InformativeStatistic> stats) {
      super();

      setStatistics(stats);
    }

    /**
     * Sets the statistics.
     *
     * @param value	the underlying statistics
     */
    public synchronized void setStatistics(List<InformativeStatistic> value) {
      m_Statistics = new ArrayList<InformativeStatistic>();

      if (value != null)
        m_Statistics.addAll(value);

      update();
    }

    /**
     * Returns the underlying statistics.
     *
     * @return		the statistics
     */
    public List<InformativeStatistic> getStatistics() {
      return m_Statistics;
    }

    /**
     * updates the tabbed pane.
     */
    protected void update() {
      int	i;
      BaseTable	table;

      removeAll();

      for (i = 0; i < m_Statistics.size(); i++) {
        table = getTable(m_Statistics.get(i));
        addPage(m_Statistics.get(i).getStatisticDescription(), new BaseScrollPane(table));
      }
    }
  }

  /**
   * A specialized dialog that displays informative statistics.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Dialog
    extends BaseDialog {

    /** for serialization. */
    private static final long serialVersionUID = 377068894443930941L;

    /** the dialog itself. */
    protected Dialog m_Self;

    /** the multi-page pane for displaying the statistics. */
    protected MultiPagePane m_MultiPagePane;

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
     * Initializes the components.
     */
    @Override
    protected void initGUI() {
      JPanel	panel;
      JButton	buttonOK;

      super.initGUI();

      setTitle("Statistics");
      getContentPane().setLayout(new BorderLayout());

      m_MultiPagePane = getMultiPagePane(null);
      getContentPane().add(m_MultiPagePane, BorderLayout.CENTER);

      panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(panel, BorderLayout.SOUTH);

      buttonOK = new JButton("OK");
      buttonOK.setMnemonic('O');
      buttonOK.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          m_Self.setVisible(false);
        }
      });
      panel.add(buttonOK);

      pack();
      setLocationRelativeTo(getOwner());
    }

    /**
     * Sets the statistics to display.
     *
     * @param value	the underlying statistics
     */
    public synchronized void setStatistics(List<InformativeStatistic> value) {
      m_MultiPagePane.setStatistics(value);

      if (!isVisible()) {
	pack();
	setLocationRelativeTo(getOwner());
	GUIHelper.setSizeAndLocation(this, this);
      }
    }

    /**
     * Returns the underlying regions.
     *
     * @return		the data
     */
    public List<InformativeStatistic> getStatistics() {
      return m_MultiPagePane.getStatistics();
    }
  }

  /**
   * Returns a new model for an informative statistic object.
   *
   * @param stat	the object to create the model for
   * @return		the model
   */
  public static Model getModel(InformativeStatistic stat) {
    return new Model(stat);
  }

  /**
   * Returns a new table for an informative statistic object.
   *
   * @param stat	the object to create the table for
   * @return		the table
   */
  public static Table getTable(InformativeStatistic stat) {
    return new Table(stat);
  }

  /**
   * Returns a multi-page pane for the informative statistic objects. Each
   * object will be placed in a table on a separate page.
   *
   * @param stats	the objects to create the multi-page pane for
   * @return		the multi-page pane
   */
  public static MultiPagePane getMultiPagePane(List<InformativeStatistic> stats) {
    return new MultiPagePane(stats);
  }

  /**
   * Returns a new dialog for displaying informative statistics.
   *
   * @param owner	the owning component
   * @param modality	the type of modality
   * @return		the dialog
   */
  public static Dialog getDialog(java.awt.Dialog owner, ModalityType modality) {
    return new Dialog(owner, modality);
  }

  /**
   * Returns a new dialog for displaying informative statistics.
   *
   * @param owner	the owning component
   * @param modal	if true then the dialog will be modal
   * @return		the dialog
   */
  public static Dialog getDialog(java.awt.Frame owner, boolean modal) {
    return new Dialog(owner, modal);
  }
}
