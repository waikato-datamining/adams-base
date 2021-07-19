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
 *    AttributeSummaryPanel.java
 *    Copyright (C) 1999-2021 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.tools.wekainvestigator.tab.preprocesstab;

import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTable;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.Utils;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
 * This panel displays summary statistics about an attribute: name, type
 * number/% of missing/unique values, number of distinct values. For numeric
 * attributes gives some other stats (mean/std dev), for nominal attributes
 * gives counts for each attribute value.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AttributeSummaryPanel
  extends JPanel {

  /** for serialization */
  static final long serialVersionUID = -5434987925737735880L;

  /**
   * Panel with labels displaying some basic info.
   */
  public static class AttributeInfoPanel
    extends JPanel {

    private static final long serialVersionUID = -1404314409077539072L;

    /** Message shown when no instances have been loaded and no attribute set */
    public static final String NO_SOURCE = "None";

    /** Displays the name of the relation */
    protected JLabel m_AttributeNameLab = new JLabel(NO_SOURCE);

    /** Displays the type of attribute */
    protected JLabel m_AttributeTypeLab = new JLabel(NO_SOURCE);

    /** Displays the number of missing values */
    protected JLabel m_MissingLab = new JLabel(NO_SOURCE);

    /** Displays the number of unique values */
    protected JLabel m_UniqueLab = new JLabel(NO_SOURCE);

    /** Displays the number of distinct values */
    protected JLabel m_DistinctLab = new JLabel(NO_SOURCE);

    /**
     * Initializes the widget.
     */
    public AttributeInfoPanel() {
      GridBagLayout gbL = new GridBagLayout();
      setLayout(gbL);
      JLabel lab = new JLabel("Name:", SwingConstants.RIGHT);
      lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
      GridBagConstraints gbC = new GridBagConstraints();
      gbC.anchor = GridBagConstraints.EAST;
      gbC.fill = GridBagConstraints.HORIZONTAL;
      gbC.gridy = 0;
      gbC.gridx = 0;
      gbL.setConstraints(lab, gbC);
      add(lab);
      gbC = new GridBagConstraints();
      gbC.anchor = GridBagConstraints.WEST;
      gbC.fill = GridBagConstraints.HORIZONTAL;
      gbC.gridy = 0;
      gbC.gridx = 1;
      gbC.weightx = 100;
      gbC.gridwidth = 3;
      gbL.setConstraints(m_AttributeNameLab, gbC);
      add(m_AttributeNameLab);
      m_AttributeNameLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));

      lab = new JLabel("Type:", SwingConstants.RIGHT);
      lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
      gbC = new GridBagConstraints();
      gbC.anchor = GridBagConstraints.EAST;
      gbC.fill = GridBagConstraints.HORIZONTAL;
      gbC.gridy = 0;
      gbC.gridx = 4;
      gbL.setConstraints(lab, gbC);
      add(lab);
      gbC = new GridBagConstraints();
      gbC.anchor = GridBagConstraints.WEST;
      gbC.fill = GridBagConstraints.HORIZONTAL;
      gbC.gridy = 0;
      gbC.gridx = 5;
      gbC.weightx = 100;
      gbL.setConstraints(m_AttributeTypeLab, gbC);
      add(m_AttributeTypeLab);
      m_AttributeTypeLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));

      // Put into a separate panel?
      lab = new JLabel("Missing:", SwingConstants.RIGHT);
      lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
      gbC = new GridBagConstraints();
      gbC.anchor = GridBagConstraints.EAST;
      gbC.fill = GridBagConstraints.HORIZONTAL;
      gbC.gridy = 1;
      gbC.gridx = 0;
      gbL.setConstraints(lab, gbC);
      add(lab);
      gbC = new GridBagConstraints();
      gbC.anchor = GridBagConstraints.WEST;
      gbC.fill = GridBagConstraints.HORIZONTAL;
      gbC.gridy = 1;
      gbC.gridx = 1;
      gbC.weightx = 100;
      gbL.setConstraints(m_MissingLab, gbC);
      add(m_MissingLab);
      m_MissingLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));

      lab = new JLabel("Distinct:", SwingConstants.RIGHT);
      lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
      gbC = new GridBagConstraints();
      gbC.anchor = GridBagConstraints.EAST;
      gbC.fill = GridBagConstraints.HORIZONTAL;
      gbC.gridy = 1;
      gbC.gridx = 2;
      gbL.setConstraints(lab, gbC);
      add(lab);
      gbC = new GridBagConstraints();
      gbC.anchor = GridBagConstraints.WEST;
      gbC.fill = GridBagConstraints.HORIZONTAL;
      gbC.gridy = 1;
      gbC.gridx = 3;
      gbC.weightx = 100;
      gbL.setConstraints(m_DistinctLab, gbC);
      add(m_DistinctLab);
      m_DistinctLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));

      lab = new JLabel("Unique:", SwingConstants.RIGHT);
      lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
      gbC = new GridBagConstraints();
      gbC.anchor = GridBagConstraints.EAST;
      gbC.fill = GridBagConstraints.HORIZONTAL;
      gbC.gridy = 1;
      gbC.gridx = 4;
      gbL.setConstraints(lab, gbC);
      add(lab);
      gbC = new GridBagConstraints();
      gbC.anchor = GridBagConstraints.WEST;
      gbC.fill = GridBagConstraints.HORIZONTAL;
      gbC.gridy = 1;
      gbC.gridx = 5;
      gbC.weightx = 100;
      gbL.setConstraints(m_UniqueLab, gbC);
      add(m_UniqueLab);
      m_UniqueLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));
    }

    /**
     * Clears the display.
     */
    public void clear() {
      m_AttributeNameLab.setText(NO_SOURCE);
      m_AttributeTypeLab.setText(NO_SOURCE);
      m_MissingLab.setText(NO_SOURCE);
      m_UniqueLab.setText(NO_SOURCE);
      m_DistinctLab.setText(NO_SOURCE);
    }

    /**
     * Sets the gui elements for fields that are stored in the AttributeStats
     * structure.
     *
     * @param as the attribute statistics
     */
    public void updateStatistics(AttributeStats as) {
      long percent = Math.round(100.0 * as.missingCount / as.totalCount);
      m_MissingLab.setText("" + as.missingCount + " (" + percent + "%)");
      percent = Math.round(100.0 * as.uniqueCount / as.totalCount);
      m_UniqueLab.setText("" + as.uniqueCount + " (" + percent + "%)");
      m_DistinctLab.setText("" + as.distinctCount);
    }

    /**
     * Sets the labels for fields we can determine just from the instance header.
     *
     * @param att the selected attribute
     */
    public void updateLabels(Attribute att) {
      m_AttributeNameLab.setText(att.name());
      switch (att.type()) {
	case Attribute.NOMINAL:
	  m_AttributeTypeLab.setText("Nominal");
	  break;
	case Attribute.NUMERIC:
	  m_AttributeTypeLab.setText("Numeric");
	  break;
	case Attribute.STRING:
	  m_AttributeTypeLab.setText("String");
	  break;
	case Attribute.DATE:
	  m_AttributeTypeLab.setText("Date");
	  break;
	case Attribute.RELATIONAL:
	  m_AttributeTypeLab.setText("Relational");
	  break;
	default:
	  m_AttributeTypeLab.setText("Unknown");
	  break;
      }
      m_MissingLab.setText("...");
      m_UniqueLab.setText("...");
      m_DistinctLab.setText("...");
    }
  }

  /**
   * Displays other stats in a table.
   */
  public static class StatisticsTable
    extends BaseTable {

    /** for serialization */
    private static final long serialVersionUID = 7165142874670048578L;

    /**
     * Initializes the table.
     */
    @Override
    protected void initGUI() {
      super.initGUI();
      setShowSimpleHeaderPopupMenu(false);
      setShowSimpleCellPopupMenu(true);
      getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * returns always false, since it's just information for the user
     *
     * @param row the row
     * @param column the column
     * @return always false, i.e., the whole table is not editable
     */
    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }

    /**
     * Clears the table.
     */
    public void clear() {
      setModel(new DefaultTableModel());
    }

    /**
     * Creates a tablemodel for the attribute being displayed
     *
     * @param as the attribute statistics
     * @param att the current attribute
     * @param allEqualWeights whether all instances have the same weight
     */
    public void updateStatistics(AttributeStats as, Attribute att, boolean allEqualWeights) {
      if (as.nominalCounts != null) {
	Object[] colNames = { "No.", "Label", "Count", "Weight" };
	Object[][] data = new Object[as.nominalCounts.length][4];
	for (int i = 0; i < as.nominalCounts.length; i++) {
	  data[i][0] = i + 1;
	  data[i][1] = att.value(i);
	  data[i][2] = as.nominalCounts[i];
	  data[i][3] = Utils.doubleToString(as.nominalWeights[i], 3);
	}
	setModel(new DefaultTableModel(data, colNames));
	getColumnModel().getColumn(0).setMaxWidth(60);
	DefaultTableCellRenderer tempR = new DefaultTableCellRenderer();
	tempR.setHorizontalAlignment(JLabel.RIGHT);
	getColumnModel().getColumn(0).setCellRenderer(tempR);
      }
      else if (as.numericStats != null) {
	Object[] colNames = { "Statistic", "Value" };
	Object[][] data = new Object[4][2];
	data[0][0] = "Minimum";
	data[1][0] = "Maximum";
	data[2][0] = "Mean" + ((!allEqualWeights) ? " (weighted)" : "");
	data[3][0] = "StdDev" + ((!allEqualWeights) ? " (weighted)" : "");
	if (att.isDate()) {
	  data[0][1] = att.formatDate(as.numericStats.min);
	  data[1][1] = att.formatDate(as.numericStats.max);
	  data[2][1] = att.formatDate(as.numericStats.mean);
	  data[3][1] = att.formatDate(as.numericStats.stdDev);
	}
	else {
	  data[0][1] = Utils.doubleToString(as.numericStats.min, 3);
	  data[1][1] = Utils.doubleToString(as.numericStats.max, 3);
	  data[2][1] = Utils.doubleToString(as.numericStats.mean, 3);
	  data[3][1] = Utils.doubleToString(as.numericStats.stdDev, 3);
	}
	setModel(new DefaultTableModel(data, colNames));
      }
      else {
	setModel(new DefaultTableModel());
      }
      getColumnModel().setColumnMargin(4);
    }
  }

  /** The instances we're playing with */
  protected Instances m_Instances = null;

  /** the current attribute index. */
  protected int m_Index = -1;

  /** Cached stats on the attributes we've summarized so far */
  protected AttributeStats[] m_AttributeStats;

  /** Do all instances have the same weight */
  protected boolean m_allEqualWeights = true;

  /** the info panel. */
  protected AttributeInfoPanel m_InfoPanel;

  /** the statistics table. */
  protected StatisticsTable m_StatsTable;

  /**
   * Creates the instances panel with no initial instances.
   */
  public AttributeSummaryPanel() {
    setLayout(new BorderLayout());

    m_InfoPanel = new AttributeInfoPanel();
    add(m_InfoPanel, BorderLayout.NORTH);

    m_StatsTable = new StatisticsTable();
    add(new BaseScrollPane(m_StatsTable), BorderLayout.CENTER);
  }

  /**
   * Tells the panel to use a new set of instances.
   *
   * @param inst a set of Instances, null to unset
   */
  public void setInstances(Instances inst) {
    m_Instances      = inst;
    m_Index          = -1;
    m_AttributeStats = new AttributeStats[(inst != null) ? inst.numAttributes() : 0];
    m_InfoPanel.clear();
    m_StatsTable.clear();

    if (m_Instances == null)
      return;

    m_allEqualWeights = true;
    if (m_Instances.numInstances() == 0)
      return;

    double w = m_Instances.instance(0).weight();
    for (int i = 1; i < m_Instances.numInstances(); i++) {
      if (m_Instances.instance(i).weight() != w) {
	m_allEqualWeights = false;
	break;
      }
    }
  }

  /**
   * Returns the currently set data.
   *
   * @return		the data, null if none set
   */
  public Instances getInstances() {
    return m_Instances;
  }

  /**
   * Sets the attribute that statistics will be displayed for.
   *
   * @param index the index of the attribute to display
   */
  public void setAttribute(final int index) {
    if (m_Instances == null) {
      m_Index = -1;
      return;
    }

    m_Index = index;
    m_InfoPanel.updateLabels(m_Instances.attribute(index));
    if (m_AttributeStats[index] == null) {
      Thread t = new Thread(() -> {
	m_AttributeStats[index] = m_Instances.attributeStats(index);
	SwingUtilities.invokeLater(() -> {
	  m_InfoPanel.updateStatistics(m_AttributeStats[index]);
	  m_StatsTable.updateStatistics(m_AttributeStats[index], m_Instances.attribute(index), m_allEqualWeights);
	  m_StatsTable.sizeColumnsToFit(-1);
	  m_StatsTable.revalidate();
	  m_StatsTable.repaint();
	});
      });
      t.setPriority(Thread.MIN_PRIORITY);
      t.start();
    }
    else {
      m_InfoPanel.updateStatistics(m_AttributeStats[index]);
      m_StatsTable.updateStatistics(m_AttributeStats[index], m_Instances.attribute(index), m_allEqualWeights);
    }
  }

  /**
   * Returns the currently set index.
   *
   * @return		the index, -1 if none set
   */
  public int getAttribute() {
    return m_Index;
  }

  /**
   * Tests out the attribute summary panel from the command line.
   *
   * @param args optional name of dataset to load
   */
  public static void main(String[] args) {
    try {
      final javax.swing.JFrame jf = new javax.swing.JFrame("Attribute Panel");
      jf.getContentPane().setLayout(new BorderLayout());
      final weka.gui.AttributeSummaryPanel p = new weka.gui.AttributeSummaryPanel();
      p.setBorder(BorderFactory.createTitledBorder("Attribute"));
      jf.getContentPane().add(p, BorderLayout.CENTER);
      final adams.gui.core.BaseComboBox j = new adams.gui.core.BaseComboBox();
      j.setEnabled(false);
      j.addActionListener(new java.awt.event.ActionListener() {
	@Override
	public void actionPerformed(java.awt.event.ActionEvent e) {
	  p.setAttribute(j.getSelectedIndex());
	}
      });
      jf.getContentPane().add(j, BorderLayout.NORTH);
      jf.addWindowListener(new java.awt.event.WindowAdapter() {
	@Override
	public void windowClosing(java.awt.event.WindowEvent e) {
	  jf.dispose();
	  System.exit(0);
	}
      });
      jf.pack();
      jf.setVisible(true);
      if (args.length == 1) {
	java.io.Reader r = new java.io.BufferedReader(new java.io.FileReader(
	  args[0]));
	Instances inst = new Instances(r);
	p.setInstances(inst);
	p.setAttribute(0);
	String[] names = new String[inst.numAttributes()];
	for (int i = 0; i < names.length; i++) {
	  names[i] = inst.attribute(i).name();
	}
	j.setModel(new javax.swing.DefaultComboBoxModel(names));
	j.setEnabled(true);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      System.err.println(ex.getMessage());
    }
  }
}
