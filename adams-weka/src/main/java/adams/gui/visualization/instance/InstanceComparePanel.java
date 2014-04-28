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
 * InstanceComparator.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.instance;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.Remove;
import weka.gui.arffviewer.ArffPanel;
import adams.core.CleanUpHandler;
import adams.core.Index;
import adams.core.Properties;
import adams.core.Range;
import adams.core.StatusMessageHandler;
import adams.core.Utils;
import adams.data.instance.Instance;
import adams.data.instance.InstancePoint;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.statistics.StatUtils;
import adams.env.Environment;
import adams.env.InstanceCompareDefinition;
import adams.gui.chooser.DatasetFileChooserPanel;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseList;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.BaseTable;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.report.ReportFactory;

/**
 * A tool for comparing two datasets visually.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstanceComparePanel
  extends BasePanel
  implements StatusMessageHandler, CleanUpHandler, MenuBarProvider {

  /** for serialization. */
  private static final long serialVersionUID = -8521425008936364143L;

  /**
   * Helper class for indexing the rows of a dataset.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DatasetIndexer
    implements Serializable {

    /** for serialization. */
    private static final long serialVersionUID = -632800897294222906L;

    /** the maximum number of decimals after the decimal point to use. */
    public final static int MAX_DECIMAL = 6;

    /** the underlying dataset. */
    protected Instances m_Dataset;

    /** the attribute to index. */
    protected Index m_AttributeIndex;

    /** the range of attributes to use. */
    protected Range m_Range;

    /** the index. */
    protected TreeMap<String,Integer> m_Index;

    /** whether the attribute is numeric or string/nominal. */
    protected Boolean m_IsString;

    /** the remove filter for trimming the range of attributes to return. */
    protected Remove m_Remove;

    /**
     * Initializes the indexer.
     */
    public DatasetIndexer() {
      super();

      m_Dataset        = null;
      m_AttributeIndex = new Index();
      m_Range          = new Range();

      reset();
    }

    /**
     * Invalidates the indexer.
     */
    protected void reset() {
      m_Index    = null;
      m_IsString = null;
      m_Remove   = null;
    }

    /**
     * Sets the dataset to index.
     *
     * @param value	the dataset
     */
    public void setDataset(Instances value) {
      m_Dataset = value;
      reset();
    }

    /**
     * Returns the dataset to index.
     *
     * @return 		the dataset
     */
    public Instances getDataset() {
      return m_Dataset;
    }

    /**
     * Checks whether an attribute index has been set.
     *
     * @return		true if an attribute index has been set
     */
    public boolean hasAttributeIndex() {
      return (m_AttributeIndex.getIndex().length() > 0);
    }

    /**
     * Sets the index of the attribute to use for indexing.
     *
     * @param value	the index
     */
    public void setAttributeIndex(String value) {
      m_AttributeIndex.setIndex(value);
      reset();
    }

    /**
     * Returns the index of the attribute to use for indexing.
     *
     * @return		the index
     */
    public String getAttributeIndex() {
      return m_AttributeIndex.getIndex();
    }

    /**
     * Sets the range of attributes to use.
     *
     * @param value	the range
     */
    public void setRange(String value) {
      m_Range.setRange(value);
      reset();
    }

    /**
     * Returns the range of attributes to use.
     *
     * @return		the range
     */
    public String getRange() {
      return m_Range.getRange();
    }

    /**
     * Initializes the indexer, if necessary.
     */
    protected synchronized void initialize() {
      int	index;
      int	i;
      int	width;
      int[]	indices;

      if (m_Index == null) {
	m_Index = new TreeMap<String,Integer>();

	if (m_Dataset != null) {
	  m_AttributeIndex.setMax(m_Dataset.numAttributes());
	  m_Range.setMax(m_Dataset.numAttributes());
	  index      = -1;
	  m_IsString = false;
	  if (hasAttributeIndex()) {
	    index      = m_AttributeIndex.getIntIndex();
	    m_IsString = m_Dataset.attribute(index).isNominal() || m_Dataset.attribute(index).isString();
	  }
	  if (m_IsString) {
	    width = -1;
	  }
	  else {
	    if (index == -1)
	      width = 10;  // max digits for index
	    else
	      width = 20 + 1 + MAX_DECIMAL;  // max value of attribute + decimal point + 6 decimals
	  }

	  for (i = 0; i < m_Dataset.numInstances(); i++) {
	    if (index == -1) {
	      m_Index.put(Utils.padLeft(Integer.toString(i + 1), '0', width), i);
	    }
	    else {
	      if (m_IsString)
		m_Index.put(m_Dataset.instance(i).stringValue(index), i);
	      else
		m_Index.put(Utils.padLeft(Utils.doubleToString(m_Dataset.instance(i).value(index), MAX_DECIMAL), '0', width), i);
	    }
	  }

	  // setup Remove filter (but only if necessary)
	  indices = m_Range.getIntIndices();
	  if (indices.length == m_Dataset.numAttributes()) {
	    m_Remove = null;
	  }
	  else {
	    m_Remove = new Remove();
	    m_Remove.setAttributeIndicesArray(indices);
	    m_Remove.setInvertSelection(true);
	    try {
	      m_Remove.setInputFormat(m_Dataset);
	    }
	    catch (Exception e) {
	      System.err.println("Error initializing the Remove filter:");
	      e.printStackTrace();
	      m_Remove = null;
	    }
	  }
	}
      }
    }

    /**
     * Returns whether the sort index is nominal/string or numeric.
     *
     * @return		true if string/nominal
     */
    public synchronized boolean isString() {
      initialize();
      if (m_IsString == null)
	return true;
      else
	return m_IsString;
    }

    /**
     * Returns the index, generates it if necessary.
     *
     * @return		the generated index
     */
    public synchronized TreeMap<String,Integer> getIndex() {
      initialize();
      return m_Index;
    }

    /**
     * Returns a list of row IDs.
     *
     * @return		the available IDs
     */
    public synchronized List<String> getRows() {
      List<String>	result;

      result = new ArrayList<String>();

      initialize();
      result.addAll(m_Index.keySet());

      return result;
    }

    /**
     * Returns the Instance for the row.
     *
     * @param row	the row to retrieve
     * @return		the row or null if not found or failed to filter
     */
    public synchronized Instance getRow(String row) {
      Instance			result;
      Integer			index;
      weka.core.Instance	inst;

      result = null;

      initialize();

      index = m_Index.get(row);
      if (index != null) {
	inst = m_Dataset.instance(index);
	if (m_Remove != null) {
	  m_Remove.input(m_Dataset.instance(index));
	  try {
	    m_Remove.batchFinished();
	    inst = m_Remove.output();
	  }
	  catch (Exception e) {
	    System.err.println("Failed to filter instance #" + (index + 1) + ": " + inst);
	    e.printStackTrace();
	    inst = m_Dataset.instance(index);
	  }
	}
	result = new Instance();
	result.set(inst);
      }

      return result;
    }
  }

  /**
   * Specialized panel for loading dataset and setting various parameters.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DatasetPanel
    extends BasePanel {

    /** for serialization. */
    private static final long serialVersionUID = -5445797307951816941L;

    /** the currently loaded dataset. */
    protected DatasetIndexer m_Indexer;

    /** the border title. */
    protected String m_BorderTitle;

    /** the ID to use for the returned instances. */
    protected String m_ID;

    /** the panel for loading the file. */
    protected DatasetFileChooserPanel m_PanelDataset;

    /** the button for displaying the dataset. */
    protected JButton m_ButtonDisplay;

    /** the edit field for the range. */
    protected JTextField m_TextAttributeRange;

    /** the label for the range. */
    protected JLabel m_LabelAttributeRange;

    /** the combobox with the attribute names. */
    protected JComboBox m_ComboBoxRowAttribute;

    /** the underlying model for the comboxbox. */
    protected DefaultComboBoxModel m_ComboBoxRowAttributeModel;

    /** the label for the attribute names. */
    protected JLabel m_LabelRowAttribute;

    /** the change listeners. */
    protected HashSet<ChangeListener> m_ChangeListeners;

    /**
     * Initializes the panel.
     *
     * @param title	the title of the border
     * @param id	the ID for the returned instances
     */
    public DatasetPanel(String title, String id) {
      super();

      setBorderTitle(title);
      setID(id);
    }

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_Indexer         = new DatasetIndexer();
      m_ChangeListeners = new HashSet<ChangeListener>();
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      JPanel	panel;

      super.initGUI();

      setLayout(new GridLayout(2, 1));

      // File
      m_PanelDataset = new DatasetFileChooserPanel();
      m_PanelDataset.setTextColumns(25);
      m_PanelDataset.setPrefix("File");
      m_PanelDataset.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          loadDataset();
          update();
        }
      });
      m_ButtonDisplay = new JButton("Display");
      m_ButtonDisplay.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          displayDataset();
        }
      });
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(m_PanelDataset);
      panel.add(m_ButtonDisplay);
      add(panel);

      // Range
      m_TextAttributeRange = new JTextField(10);
      m_TextAttributeRange.setToolTipText(GUIHelper.processTipText(new Range().getExample(), 40));
      m_TextAttributeRange.getDocument().addDocumentListener(new DocumentListener() {
        public void removeUpdate(DocumentEvent e) {
          update();
        }
        public void insertUpdate(DocumentEvent e) {
          update();
        }
        public void changedUpdate(DocumentEvent e) {
          update();
        }
        protected void update() {
          String oldRange = m_Indexer.getRange();
          m_Indexer.setRange(m_TextAttributeRange.getText());
          if (!oldRange.equals(m_Indexer.getRange()))
            fireSetupChanged();
        }
      });
      m_LabelAttributeRange = new JLabel("Att. range");
      m_LabelAttributeRange.setLabelFor(m_TextAttributeRange);

      // Index attribute
      m_ComboBoxRowAttributeModel = new DefaultComboBoxModel();
      m_ComboBoxRowAttributeModel.addElement("-none-");
      m_ComboBoxRowAttribute = new JComboBox(m_ComboBoxRowAttributeModel);
      m_ComboBoxRowAttribute.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          int index = m_ComboBoxRowAttribute.getSelectedIndex();
          if (index < 1)
            m_Indexer.setAttributeIndex("");
          else
            m_Indexer.setAttributeIndex("" + index);
          fireSetupChanged();
        }
      });
      m_LabelRowAttribute = new JLabel("Row att.");
      m_LabelRowAttribute.setLabelFor(m_ComboBoxRowAttribute);

      panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(m_LabelAttributeRange);
      panel.add(m_TextAttributeRange);
      panel.add(m_LabelRowAttribute);
      panel.add(m_ComboBoxRowAttribute);
      add(panel);

      // adjust heights
      m_ButtonDisplay.setPreferredSize(new Dimension((int) m_ButtonDisplay.getPreferredSize().getWidth(), (int) m_PanelDataset.getPreferredSize().getHeight()));
      m_ComboBoxRowAttribute.setPreferredSize(new Dimension(150, (int) m_ComboBoxRowAttribute.getPreferredSize().getHeight()));

      setBorderTitle("File");

      update();
    }

    /**
     * Sets the title of the border.
     *
     * @param value	the title
     */
    public void setBorderTitle(String value) {
      m_BorderTitle = value;
      setBorder(BorderFactory.createTitledBorder(m_BorderTitle));
    }

    /**
     * Returns the title of the border.
     *
     * @return		the title
     */
    public String getBorderTitle() {
      return m_BorderTitle;
    }

    /**
     * Sets the ID to use for the returned instances.
     *
     * @param value	the ID
     */
    public void setID(String value) {
      m_ID = value;
    }

    /**
     * Returns the ID to use for the returned instances.
     *
     * @return		the ID
     */
    public String getID() {
      return m_ID;
    }

    /**
     * Sets the dataset to use.
     *
     * @param value	the dataset
     */
    public void setDataset(File value) {
      m_PanelDataset.setCurrent(value);
      m_PanelDataset.fireCurrentValueChanged();
    }

    /**
     * Return the currently selected dataset.
     *
     * @return		the dataset
     */
    public File getDataset() {
      return m_PanelDataset.getCurrent();
    }

    /**
     * Checks whether an existing file has been set.
     *
     * @return		true if an existing file has been set
     */
    public boolean hasDataset() {
      return (m_PanelDataset.getCurrent().isFile() && m_PanelDataset.getCurrent().exists());
    }

    /**
     * Sets the range of attributes.
     *
     * @param value	the range
     */
    public void setAttributeRange(String value) {
      m_Indexer.setRange(value);
      m_TextAttributeRange.setText(m_Indexer.getRange());
    }

    /**
     * Returns the current range of attributes.
     *
     * @return		the range
     */
    public String getAttributeRange() {
      return m_Indexer.getRange();
    }

    /**
     * Sets the index of the attribute to use for matching rows
     * (only works if dataset already loaded).
     *
     * @param value	the index (1-based, first and last OK as well), use ""
     * 			to not use a specific attribute.
     */
    public void setRowIndex(String value) {
      int	index;

      index = -1;
      if (value.toLowerCase().equals("first")) {
	index = 0;
      }
      else if (value.toLowerCase().equals("last")) {
	if ((m_Indexer.getDataset() != null))
	  index = m_Indexer.getDataset().numAttributes() - 1;
      }
      else if (value.length() > 0) {
	index = Integer.parseInt(value) - 1;
      }
      m_ComboBoxRowAttribute.setSelectedIndex(index + 1);  // + 1 because of "-none-"
    }

    /**
     * Returns the currently selected.
     *
     * @return		the range
     */
    public String getRowIndex() {
      int	index;

      index = m_ComboBoxRowAttribute.getSelectedIndex();
      if (index == 0)
	return "";
      else if (index == 1)
	return "first";
      else if (index == m_ComboBoxRowAttributeModel.getSize())
	return "last";
      else
	return Integer.toString(index);
    }

    /**
     * Updates buttons, etc.
     */
    protected void update() {
      m_ButtonDisplay.setEnabled(m_Indexer.getDataset() != null);
    }

    /**
     * Reloads the currently loaded dataset.
     */
    public void reload() {
      String	attRange;
      String	rowIndex;

      if (!hasDataset())
	return;

      // backup
      attRange = getAttributeRange();
      rowIndex = getRowIndex();

      loadDataset();

      // restore
      setAttributeRange(attRange);
      setRowIndex(rowIndex);
    }

    /**
     * Loads the dataset, if possible.
     */
    protected void loadDataset() {
      Instances		dataset;
      int		i;

      try {
	dataset = DataSource.read(m_PanelDataset.getCurrent().getAbsolutePath());
	m_ComboBoxRowAttributeModel   = new DefaultComboBoxModel();
	m_ComboBoxRowAttributeModel.addElement("-none-");
	for (i = 0; i < dataset.numAttributes(); i++)
	  m_ComboBoxRowAttributeModel.addElement((i+1) + ": " + dataset.attribute(i).name());
        m_ComboBoxRowAttribute.setModel(m_ComboBoxRowAttributeModel);
        m_ComboBoxRowAttribute.setSelectedIndex(0);
      }
      catch (Exception e) {
	dataset = null;
	e.printStackTrace();
	GUIHelper.showErrorMessage(
	    this,
	    "Error loading dataset from '" + m_PanelDataset.getCurrent().getAbsolutePath() + "':\n" + e);
      }

      m_Indexer.setDataset(dataset);

      update();
      fireSetupChanged();
    }

    /**
     * Displays the dataset in a separate window.
     */
    protected void displayDataset() {
      final BaseDialog	dialog;
      ArffPanel		arffpanel;
      JPanel		panel;
      JButton		buttonClose;

      if (getParentDialog() != null)
	dialog = new BaseDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	dialog = new BaseDialog(getParentFrame(), true);
      dialog.setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);
      dialog.setTitle(m_Indexer.getDataset().relationName());
      dialog.getContentPane().setLayout(new BorderLayout());

      arffpanel = new ArffPanel(m_Indexer.getDataset());
      dialog.getContentPane().add(arffpanel, BorderLayout.CENTER);

      buttonClose = new JButton("Close", GUIHelper.getIcon("exit.png"));
      buttonClose.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          dialog.setVisible(false);
        }
      });
      panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      panel.add(buttonClose);
      dialog.getContentPane().add(panel, BorderLayout.SOUTH);
      dialog.pack();
      dialog.setSize(800, 600);
      if (getParentDialog() != null)
	dialog.setLocationRelativeTo(getParentDialog());
      else
	dialog.setLocationRelativeTo(getParentFrame());
      dialog.setVisible(true);
    }

    /**
     * Returns a list of row IDs.
     *
     * @return		the available IDs
     */
    public List<String> getRows() {
      return m_Indexer.getRows();
    }

    /**
     * Returns the row to display.
     *
     * @param row	the ID of the row to retrieve
     * @return		the Instance, can be null
     */
    public Instance getRow(String row) {
      Instance	result;

      result = m_Indexer.getRow(row);
      if (result != null)
	result.setID(m_ID);

      return result;
    }

    /**
     * Returns whether the index values are nominal/string or numeric.
     *
     * @return		true if index is nominal/string
     */
    public boolean isString() {
      return m_Indexer.isString();
    }

    /**
     * Adds the change listener to the internal list.
     *
     * @param l		the listener to add
     */
    public void addChangeListener(ChangeListener l) {
      m_ChangeListeners.add(l);
    }

    /**
     * Removes the change listener from the internal list.
     *
     * @param l		the listener to remove
     */
    public void removeChangeListener(ChangeListener l) {
      m_ChangeListeners.remove(l);
    }

    /**
     * Sends the event to all change listeners.
     *
     * @param e		the event to send
     */
    protected void notifyChangeListeners(ChangeEvent e) {
      for (ChangeListener l: m_ChangeListeners)
	l.stateChanged(e);
    }

    /**
     * Fires a ChangeEvent that the setup has changed.
     */
    public void fireSetupChanged() {
      notifyChangeListeners(new ChangeEvent(this));
    }
  }

  /** the name of the props file. */
  public final static String FILENAME = "InstanceCompare.props";

  /** the file to store the recent files in (first file). */
  public final static String SESSION_FILE_1 = "InstanceCompareSession1.props";

  /** the file to store the recent files in (second file). */
  public final static String SESSION_FILE_2 = "InstanceCompareSession2.props";

  /** the properties. */
  protected static Properties m_Properties;

  /** the panel for the first dataset. */
  protected DatasetPanel m_PanelDataset1;

  /** the panel for the second dataset. */
  protected DatasetPanel m_PanelDataset2;

  /** the panel for displaying the two instances. */
  protected InstancePanel m_PanelComparison;

  /** the panel with the difference. */
  protected InstancePanel m_PanelDifference;

  /** the label for the combobox. */
  protected JLabel m_LabelRows;

  /** the JLiast with the rows. */
  protected BaseList m_ListRows;

  /** the model for the combobox with the rows. */
  protected DefaultListModel m_ListRowsModel;

  /** the table with the report. */
  protected ReportFactory.Table m_ReportTable;
  //protected JTable m_ReportTable;

  /** for displaying error messages. */
  protected BaseStatusBar m_StatusBar;

  /** the internal index. */
  protected List<String> m_RowIndex;

  /** the menu bar. */
  protected JMenuBar m_MenuBar;

  /** the reload menu item. */
  protected JMenuItem m_MenuItemReload;

  /** the recent file menu item (first file). */
  protected JMenuItem m_MenuItemLoadRecent1;

  /** the recent file menu item (second file). */
  protected JMenuItem m_MenuItemLoadRecent2;

  /** the menu item for view related stuff. */
  protected JMenu m_MenuView;

  /** the toggle anti-aliasing menu item. */
  protected JMenuItem m_MenuItemViewAntiAliasing;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler1;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler2;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_RecentFilesHandler1 = null;
    m_RecentFilesHandler2 = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JPanel	panel2;
    JPanel	panel3;
    JPanel	panel4;

    super.initGUI();

    setLayout(new BorderLayout());

    // files
    m_PanelDataset1 = new DatasetPanel("File 1", "1");
    m_PanelDataset1.setAttributeRange("first-last");
    m_PanelDataset1.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	updateRows();
	if (m_RecentFilesHandler1 != null)
	  m_RecentFilesHandler1.addRecentItem(m_PanelDataset1.getDataset());
      }
    });
    m_PanelDataset2 = new DatasetPanel("File 2", "2");
    m_PanelDataset2.setAttributeRange("first-last");
    m_PanelDataset2.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	updateRows();
	if (m_RecentFilesHandler2 != null)
	  m_RecentFilesHandler2.addRecentItem(m_PanelDataset2.getDataset());
      }
    });
    panel = new JPanel(new GridLayout(1, 2));
    panel.add(m_PanelDataset1);
    panel.add(m_PanelDataset2);
    add(panel, BorderLayout.NORTH);

    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.CENTER);

    // visualization
    panel2 = new JPanel(new GridLayout(2, 1));
    panel.add(panel2, BorderLayout.CENTER);
    m_PanelComparison = new InstancePanel("Compare");
    m_PanelComparison.setSidePanelVisible(false);
    m_PanelComparison.getPlot().getAxis(Axis.BOTTOM).setAxisName("Attribute index (in selected range)");
    m_PanelDifference = new InstancePanel("Difference");
    m_PanelDifference.setSidePanelVisible(false);
    m_PanelDifference.getPlot().getAxis(Axis.BOTTOM).setAxisName("Attribute index (in selected range)");
    panel2.add(m_PanelComparison);
    panel2.add(m_PanelDifference);

    // navigation
    panel2 = new JPanel(new GridLayout(2, 1));
    panel2.setPreferredSize(new Dimension(300, 100));
    panel.add(panel2, BorderLayout.EAST);

    panel4 = new JPanel(new BorderLayout());
    panel2.add(panel4);
    panel3 = new JPanel(new BorderLayout());
    panel4.add(panel3, BorderLayout.CENTER);
    m_ListRowsModel = new DefaultListModel();
    m_ListRowsModel.addElement("-none-");
    m_ListRows = new BaseList(m_ListRowsModel);
    m_ListRows.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_ListRows.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	performComparison();
      }
    });
    m_LabelRows = new JLabel("Row ID");
    m_LabelRows.setDisplayedMnemonic('R');
    m_LabelRows.setLabelFor(m_ListRows);
    panel3.add(m_LabelRows, BorderLayout.NORTH);
    panel3.add(new BaseScrollPane(m_ListRows), BorderLayout.CENTER);

    // report
    m_ReportTable = ReportFactory.getTable(null);
    m_ReportTable.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    panel3 = new JPanel(new BorderLayout());
    panel3.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
    panel3.add(new BaseScrollPane(m_ReportTable), BorderLayout.CENTER);
    panel2.add(panel3);

    // status
    m_StatusBar = new BaseStatusBar();
    panel.add(m_StatusBar, BorderLayout.SOUTH);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenu		submenu;
    JMenuItem		menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // File/Recent files 1
      submenu = new JMenu("Load recent (1st file)");
      menu.add(submenu);
      m_RecentFilesHandler1 = new RecentFilesHandler<JMenu>(
	  SESSION_FILE_1, getProperties().getInteger("MaxRecentFiles", 5), submenu);
      m_RecentFilesHandler1.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  setFirstDataset(e.getItem());
	}
      });
      m_MenuItemLoadRecent1 = submenu;

      // File/Recent files 1
      submenu = new JMenu("Load recent (2nd file)");
      menu.add(submenu);
      m_RecentFilesHandler2 = new RecentFilesHandler<JMenu>(
	  SESSION_FILE_2, getProperties().getInteger("MaxRecentFiles", 5), submenu);
      m_RecentFilesHandler2.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  setSecondDataset(e.getItem());
	}
      });
      m_MenuItemLoadRecent2 = submenu;

      // File/Reload
      menuitem = new JMenuItem("Reload");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("F5"));
      menuitem.setIcon(GUIHelper.getIcon("refresh.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  reload();
	}
      });
      m_MenuItemReload = menuitem;

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  close();
	}
      });

      // View
      menu = new JMenu("View");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });
      m_MenuView = menu;

      // View/Anti-aliasing
      menuitem = new JCheckBoxMenuItem("Anti-aliasing");
      menu.add(menuitem);
      menuitem.setMnemonic('A');
      menuitem.setSelected(m_PanelComparison.isAntiAliasingEnabled());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_PanelComparison.setAntiAliasingEnabled(m_MenuItemViewAntiAliasing.isSelected());
	  m_PanelDifference.setAntiAliasingEnabled(m_MenuItemViewAntiAliasing.isSelected());
	}
      });
      m_MenuItemViewAntiAliasing = menuitem;

      // update menu
      m_MenuBar = result;
      updateMenu();
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    boolean	dataLoaded;

    if (m_MenuBar == null)
      return;

    dataLoaded = m_PanelDataset1.hasDataset() && m_PanelDataset2.hasDataset();

    m_MenuItemLoadRecent1.setEnabled((m_RecentFilesHandler1 != null) && (m_RecentFilesHandler1.size() > 0));
    m_MenuItemLoadRecent2.setEnabled((m_RecentFilesHandler2 != null) && (m_RecentFilesHandler2.size() > 0));
    m_MenuItemReload.setEnabled(dataLoaded);
  }

  /**
   * Reloads the datasets.
   */
  public void reload() {
    m_PanelDataset1.reload();
    m_PanelDataset2.reload();
  }

  /**
   * closes the dialog/frame.
   */
  public void close() {
    cleanUp();
    closeParent();
  }

  /**
   * Removes the leading 0s.
   *
   * @param list	the list to process
   * @return		the processed list
   */
  protected List<String> trimIDs(List<String> list) {
    List<String> 	result;

    result = new ArrayList<String>();
    for (String row: list)
	result.add(row.replaceAll("^[0]*", ""));

    return result;
  }

  /**
   * Updates the combobox with the rows.
   */
  protected void updateRows() {
    List<String> 	rows1;
    List<String> 	rows2;
    List<String>	rowsCommon;
    List<String>	rows;
    DefaultListModel	model;

    showStatus("Updating...");

    rows1      = m_PanelDataset1.getRows();
    rows2      = m_PanelDataset2.getRows();
    rowsCommon = new ArrayList<String>(rows1);
    rowsCommon.retainAll(rows2);

    if (!m_PanelDataset1.isString() && !m_PanelDataset2.isString()) {
      m_RowIndex = rowsCommon;
      rows       = trimIDs(rowsCommon);
      showStatus("");
    }
    else {
      if (rows1.size() > rows2.size())
	m_RowIndex = rows1;
      else
	m_RowIndex = rows2;
      if (!m_PanelDataset1.isString())
	rows = trimIDs(m_RowIndex);
      else
	rows = m_RowIndex;
      showStatus("Attributes differ in type, cannot match rows!");
    }

    model = new DefaultListModel();
    for (String row: rows)
      model.addElement(row);
    m_ListRowsModel = model;
    m_ListRowsModel.insertElementAt("-none-", 0);
    m_ListRows.setModel(m_ListRowsModel);
    m_ListRows.setSelectedIndex(0);
    if (m_ListRowsModel.size() <= 1)
      m_LabelRows.setText("Row ID");
    else
      m_LabelRows.setText("Row ID (" + (m_ListRowsModel.size() - 1) + ")");
  }

  /**
   * Performs the comparison between the rows from the two datasets.
   */
  protected void performComparison() {
    String			row;
    Instance			inst1;
    Instance			inst2;
    Instance			instDiff;
    InstancePoint		point1;
    InstancePoint		point2;
    InstancePoint		pointDiff;
    Iterator<InstancePoint>	iter1;
    Iterator<InstancePoint>	iter2;
    InstanceContainer		cont;
    InstanceContainerManager	managerComp;
    InstanceContainerManager	managerDiff;
    Report			reportDiff;
    Field			field;
    double[]			values1;
    double[]			values2;

    managerComp = m_PanelComparison.getContainerManager();
    managerDiff = m_PanelDifference.getContainerManager();

    managerComp.clear();
    managerDiff.clear();
    m_ReportTable.setModel(ReportFactory.getModel(null));
    m_ReportTable.setOptimalColumnWidth();

    if (m_ListRows.getSelectedIndex() < 1)
      return;
    if (m_RowIndex == null)
      return;

    row   = m_RowIndex.get(m_ListRows.getSelectedIndex() - 1);
    inst1 = m_PanelDataset1.getRow(row);
    inst2 = m_PanelDataset2.getRow(row);

    // comparison
    managerComp.startUpdate();
    if (inst1 != null) {
      cont = managerComp.newContainer(inst1);
      managerComp.add(cont);
    }
    if (inst2 != null) {
      cont = managerComp.newContainer(inst2);
      managerComp.add(cont);
    }
    managerComp.finishUpdate();

    // difference
    if ((inst1 == null) || (inst2 == null)) {
      showStatus("Instance only available in dataset " + (inst1 != null ? "1" : "2"));
    }
    else if (inst1.size() != inst2.size()) {
      showStatus("Instances differ in size: " + inst1.size() + " != " + inst2.size());
    }
    else {
      showStatus("");

      // create instance
      instDiff = new Instance();
      iter1    = inst1.iterator();
      iter2    = inst2.iterator();
      while (iter1.hasNext() && iter2.hasNext()) {
	point1    = iter1.next();
	point2    = iter2.next();
	pointDiff = new InstancePoint(point1.getX(), (double) (point1.getY() - point2.getY()));
	instDiff.add(pointDiff);
      }

      // extend report
      reportDiff = instDiff.getReport();
      values1    = inst1.toInstance().toDoubleArray();
      values2    = inst2.toInstance().toDoubleArray();
      // 1. correlation coefficient
      field = new Field("Correlation coefficient", DataType.NUMERIC);
      reportDiff.addField(field);
      reportDiff.setValue(field, StatUtils.correlationCoefficient(values1, values2));
      // 2. RMSE
      field = new Field("Root mean squared error", DataType.NUMERIC);
      reportDiff.addField(field);
      reportDiff.setValue(field, StatUtils.rmse(values1, values2));
      // 3. RRSE
      field = new Field("Root relative squared error", DataType.NUMERIC);
      reportDiff.addField(field);
      reportDiff.setValue(field, StatUtils.rrse(values1, values2));
      // 4. MAE
      field = new Field("Mean absolute error", DataType.NUMERIC);
      reportDiff.addField(field);
      reportDiff.setValue(field, StatUtils.mae(values1, values2));
      // 5. RAE
      field = new Field("Relative absolute error", DataType.NUMERIC);
      reportDiff.addField(field);
      reportDiff.setValue(field, StatUtils.rae(values1, values2));
      // TODO: further statistics, etc.

      managerDiff.startUpdate();
      cont = managerComp.newContainer(instDiff);
      cont.setID((String) m_ListRows.getSelectedValue());
      managerDiff.add(cont);
      managerDiff.finishUpdate();
      m_ReportTable.setModel(ReportFactory.getModel(reportDiff));
      m_ReportTable.setOptimalColumnWidth();
    }
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    m_StatusBar.showStatus(msg);
  }

  /**
   * Sets the first dataset.
   *
   * @param value	the first dataset
   */
  public void setFirstDataset(File value) {
    m_PanelDataset1.setDataset(value);
  }

  /**
   * Returns the first dataset.
   *
   * @return		the first dataset
   */
  public File getFirstDataset() {
    return m_PanelDataset1.getDataset();
  }

  /**
   * Sets the first attribute range ('first' and 'last' can be used as well).
   *
   * @param value	the attribute range
   */
  public void setFirstAttributeRange(String value) {
    m_PanelDataset1.setAttributeRange(value);
  }

  /**
   * Returns the first attribute range.
   *
   * @return		the attribute range
   */
  public String getFirstAttributeRange() {
    return m_PanelDataset1.getAttributeRange();
  }

  /**
   * Sets the first row index ('first' and 'last' can be used as well).
   *
   * @param value	the index of the row attribute
   */
  public void setFirstRowIndex(String value) {
    m_PanelDataset1.setRowIndex(value);
  }

  /**
   * Returns the first row index.
   *
   * @return		the index of the row attribute (1-based, 'first', 'last')
   */
  public String getFirstRowIndex() {
    return m_PanelDataset1.getRowIndex();
  }

  /**
   * Sets the second dataset.
   *
   * @param value	the second dataset
   */
  public void setSecondDataset(File value) {
    m_PanelDataset2.setDataset(value);
  }

  /**
   * Returns the second dataset.
   *
   * @return		the second dataset
   */
  public File getSecondDataset() {
    return m_PanelDataset2.getDataset();
  }

  /**
   * Sets the second attribute range ('second' and 'last' can be used as well).
   *
   * @param value	the attribute range
   */
  public void setSecondAttributeRange(String value) {
    m_PanelDataset2.setAttributeRange(value);
  }

  /**
   * Returns the second attribute range.
   *
   * @return		the attribute range
   */
  public String getSecondAttributeRange() {
    return m_PanelDataset2.getAttributeRange();
  }

  /**
   * Sets the second row index ('second' and 'last' can be used as well).
   *
   * @param value	the index of the row attribute
   */
  public void setSecondRowIndex(String value) {
    m_PanelDataset2.setRowIndex(value);
  }

  /**
   * Returns the second row index.
   *
   * @return		the index of the row attribute (2-based, 'second', 'last')
   */
  public String getSecondRowIndex() {
    return m_PanelDataset2.getRowIndex();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_PanelDifference.cleanUp();
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(InstanceCompareDefinition.KEY);

    return m_Properties;
  }
}
