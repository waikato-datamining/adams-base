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
 * SpreadSheetPanel.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer;

import adams.core.CleanUpHandler;
import adams.data.io.input.SpreadSheetReader;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.flow.control.Flow;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ConsolePanel.OutputType;
import adams.gui.core.GUIHelper;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SpreadSheetColumnComboBox;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.core.TableRowRange;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.tools.SpreadSheetViewerPanel;
import adams.gui.tools.spreadsheetviewer.chart.AbstractChartGenerator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a panel for the spreadsheet viewer tool.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetPanel
  extends BasePanel
  implements SpreadSheetSupporter, TableModelListener, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4251007424174062651L;

  /** the owning tabbed pane. */
  protected TabbedPane m_Owner;
  
  /** the underlying table. */
  protected SpreadSheetTable m_Table;
  
  /** for listing the column names. */
  protected SpreadSheetColumnComboBox m_ColumnComboBox;
  
  /** the search panel. */
  protected SearchPanel m_PanelSearch;
  
  /** the generated flows (eg charts). */
  protected List<Flow> m_GeneratedFlows;

  /** the associated file name. */
  protected File m_Filename;

  /** the reader used for reading the file. */
  protected SpreadSheetReader m_Reader;
  
  /** the writer used for writing the file. */
  protected SpreadSheetWriter m_Writer;

  /**
   * Initializes the panel.
   * 
   * @param owner	the owning tabbed pane
   */
  public SpreadSheetPanel(TabbedPane owner) {
    super();
    setOwner(owner);
  }
  
  /**
   * initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_GeneratedFlows = new ArrayList<Flow>();
    m_Filename       = null;
    m_Reader         = null;
    m_Writer         = null;
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JLabel	label;

    super.initGUI();

    setLayout(new BorderLayout());
    
    m_Table = new SpreadSheetTable(new SpreadSheetTableModel());
    m_Table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
	if (getViewer() != null)
	  getViewer().getViewerTabs().notifyTabs(SpreadSheetPanel.this, m_Table.getSelectedRows());
      }
    });
    add(new BaseScrollPane(m_Table), BorderLayout.CENTER);
    m_Table.getModel().addTableModelListener(this);

    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_ColumnComboBox = new SpreadSheetColumnComboBox(m_Table);
    label = new JLabel("Jump to");
    label.setLabelFor(m_ColumnComboBox);
    label.setDisplayedMnemonic('J');
    panel.add(label);
    panel.add(m_ColumnComboBox);
    add(panel, BorderLayout.NORTH);

    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, true);
    m_PanelSearch.addSearchListener(new SearchListener() {
      @Override
      public void searchInitiated(SearchEvent e) {
        m_Table.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
      }
    });
    add(m_PanelSearch, BorderLayout.SOUTH);
  }

  /**
   * Sets the owning tabbed pane.
   * 
   * @param value	the owner
   */
  public void setOwner(TabbedPane value) {
    m_Owner = value;
  }
  
  /**
   * Returns the owning tabbed pane.
   * 
   * @return		the owner, null if none set
   */
  public TabbedPane getOwner() {
    return m_Owner;
  }
  
  /**
   * Returns this panel's tab title, if any.
   * 
   * @return		the title, null if not available
   */
  public String getTabTitle() {
    String	result;
    int		index;
    
    result = null;
    
    if (getOwner() != null) {
      index = getOwner().indexOfComponent(this);
      if (index > -1)
	result = getOwner().getTitleAt(index);
    }
    
    return result;
  }
  
  /**
   * Returns the owning viewer.
   * 
   * @return		the viewer, null if none set
   */
  public SpreadSheetViewerPanel getViewer() {
    if (getOwner() != null)
      return getOwner().getOwner();
    else
      return null;
  }
  
  /**
   * Sets the number of decimals to display. Use -1 to display all.
   *
   * @param value	the number of decimals
   */
  public void setNumDecimals(int value) {
    m_Table.setNumDecimals(value);
  }

  /**
   * Returns the currently set number of decimals. -1 if displaying all.
   *
   * @return		the number of decimals
   */
  public int getNumDecimals() {
    return m_Table.getNumDecimals();
  }

  /**
   * Checks whether a custom background color for negative values has been set.
   *
   * @return		true if custom color set
   */
  public boolean hasNegativeBackground() {
    return m_Table.hasNegativeBackground();
  }

  /**
   * Sets the custom background color for negative values.
   *
   * @param value	the color, null to unset it
   */
  public void setNegativeBackground(Color value) {
    m_Table.setNegativeBackground(value);
  }

  /**
   * Returns the custom background color for negative values, if any.
   *
   * @return		the color, null if none set
   */
  public Color getNegativeBackground() {
    return m_Table.getNegativeBackground();
  }

  /**
   * Checks whether a custom background color for positive values has been set.
   *
   * @return		true if custom color set
   */
  public boolean hasPositiveBackground() {
    return m_Table.hasPositiveBackground();
  }

  /**
   * Sets the custom background color for positive values.
   *
   * @param value	the color, null to unset it
   */
  public void setPositiveBackground(Color value) {
    m_Table.setPositiveBackground(value);
  }

  /**
   * Returns the custom background color for positive values, if any.
   *
   * @return		the color, null if none set
   */
  public Color getPositiveBackground() {
    return m_Table.getPositiveBackground();
  }

  /**
   * Sets whether to display the formulas or their calculated values.
   *
   * @param value	true if to display the formulas rather than the calculated values
   */
  public void setShowFormulas(boolean value) {
    m_Table.setShowFormulas(value);
  }

  /**
   * Returns whether to display the formulas or their calculated values.
   *
   * @return		true if to display the formulas rather than the calculated values
   */
  public boolean getShowFormulas() {
    return m_Table.getShowFormulas();
  }

  /**
   * Sets whether the table is read-only.
   *
   * @param value	true if read-only
   */
  public void setReadOnly(boolean value) {
    m_Table.setReadOnly(value);
  }

  /**
   * Returns whether the table is read-only.
   *
   * @return		true if read-only
   */
  public boolean isReadOnly() {
    return m_Table.isReadOnly();
  }

  /**
   * Sets whether the table has been modified.
   *
   * @param value	true if modified
   */
  public void setModified(boolean value) {
    m_Table.setModified(value);
  }

  /**
   * Returns whether the table has been modified.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Table.isModified();
  }

  /**
   * Returns the underlying table.
   * 
   * @return		the table
   */
  public SpreadSheetTable getTable() {
    return m_Table;
  }
  
  /**
   * Sets the spreadsheet to display.
   * 
   * @param value	the sheet
   */
  public void setSheet(SpreadSheet value) {
    m_Table.getModel().removeTableModelListener(this);
    m_Table.setModel(new SpreadSheetTableModel(value));
    m_Table.getModel().addTableModelListener(this);
  }
  
  /**
   * Returns the underlying spreadsheet.
   * 
   * @return		the sheet
   */
  public SpreadSheet getSheet() {
    return toSpreadSheet();
  }
  
  /**
   * Returns the underlying sheet.
   *
   * @return		the spread sheet
   */
  @Override
  public SpreadSheet toSpreadSheet() {
    return m_Table.toSpreadSheet();
  }
  
  /**
   * Returns the underlying sheet.
   *
   * @param range	the range to return
   * @return		the spread sheet
   */
  public SpreadSheet toSpreadSheet(TableRowRange range) {
    return m_Table.toSpreadSheet(range);
  }

  /**
   * Generates and displays a chart using the specified chart generator.
   * 
   * @param generator	the generator to use for creating the chart
   */
  public void generateChart(AbstractChartGenerator generator) {
    SwingWorker	worker;
    final Flow	flow;
    
    flow = generator.generate(getTabTitle(), getSheet());
    flow.setParentComponent(this);
    
    worker = new SwingWorker() {
      String msg = null;
      
      @Override
      protected Object doInBackground() throws Exception {
	msg = flow.setUp();
	if (msg != null)
	  msg = "Failed to setup flow for generating chart:\n" + msg;

	if (msg == null) {
	  msg = flow.execute();
	  if (msg != null)
	    msg = "Failed to execute flow for generating chart:\n" + msg;
	}

	if (msg == null) {
	  flow.wrapUp();
	  if (flow.hasStopMessage())
	    msg = "Flow execution for generating chart was stopped:\n" + flow.getStopMessage();
	}

        return msg;
      }
      
      @Override
      protected void done() {
        super.done();
        if (msg != null) {
          GUIHelper.showErrorMessage(SpreadSheetPanel.this, msg);
          ConsolePanel.getSingleton().append(OutputType.ERROR, msg + "\n");
          ConsolePanel.getSingleton().append(OutputType.ERROR, flow.toCommandLine() + "\n");
          flow.destroy();
        }
        else {
          addGeneratedFlow(flow);
        }
      }
    };
    
    worker.execute();
  }
  
  /**
   * Adds the flow to the list of flows to clean up.
   * 
   * @param flow	the flow to clean up
   */
  public void addGeneratedFlow(Flow flow) {
    m_GeneratedFlows.add(flow);
  }

  /**
   * Sets the associated filename.
   *
   * @param value	the file
   */
  public void setFilename(File value) {
    m_Filename = value;
  }

  /**
   * Returns the associated filename.
   *
   * @return		the file
   */
  public File getFilename() {
    return m_Filename;
  }

  /**
   * Sets the associated reader.
   *
   * @param value	the reader
   */
  public void setReader(SpreadSheetReader value) {
    m_Reader = value;
  }

  /**
   * Returns the associated reader.
   *
   * @return		the reader
   */
  public SpreadSheetReader getReader() {
    return m_Reader;
  }

  /**
   * Sets the associated writer.
   *
   * @param value	the writer
   */
  public void setWriter(SpreadSheetWriter value) {
    m_Writer = value;
  }

  /**
   * Returns the associated writer.
   *
   * @return		the writer
   */
  public SpreadSheetWriter getWriter() {
    return m_Writer;
  }

  /**
   * Gets notified in case of changes to the table model.
   *
   * @param e		the event
   */
  @Override
  public void tableChanged(TableModelEvent e) {
    if (getViewer() != null)
      getViewer().updateMenu();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    for (Flow flow: m_GeneratedFlows)
      flow.destroy();
    m_GeneratedFlows.clear();
  }
}
