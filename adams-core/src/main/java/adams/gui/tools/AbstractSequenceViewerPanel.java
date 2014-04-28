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
 * SequenceViewerPanel.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.Document;

import adams.core.Properties;
import adams.data.sequence.XYSequence;
import adams.db.SequenceProvider;
import adams.env.Environment;
import adams.env.SequenceViewerDefinition;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTable;
import adams.gui.scripting.SyntaxDocument;
import adams.gui.visualization.sequence.XYSequencePanel;
import adams.gui.visualization.sequence.XYSequenceTable;

/**
 * A panel for viewing sequences obtained from SQL statements.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSequenceViewerPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -2118371024397646256L;

  /** the name of the props file. */
  public final static String FILENAME = "SequenceViewerPanel.props";

  /** the panel for the SQL statement. */
  protected JPanel m_PanelSQL;

  /** the text area for the SQL statement. */
  protected JTextPane m_TextSQL;

  /** the combobox with the templates. */
  protected JComboBox m_ComboboxTemplates;

  /** the button for executing the SQL. */
  protected JButton m_ButtonExecute;

  /** the panel for the results. */
  protected JPanel m_PanelResults;

  /** the tabbed pane for the results. */
  protected BaseTabbedPane m_TabbedPaneResults;

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    JPanel	panel;
    JPanel	panel2;

    super.initGUI();

    setLayout(new BorderLayout());

    // SQL
    m_PanelSQL = new JPanel(new BorderLayout());
    add(m_PanelSQL, BorderLayout.NORTH);

    m_TextSQL = new JTextPane();
    m_TextSQL.setDocument(createDocument());
    m_TextSQL.setPreferredSize(new Dimension(400, 300));
    m_PanelSQL.add(new BaseScrollPane(m_TextSQL), BorderLayout.CENTER);

    panel2 = new JPanel(new BorderLayout());
    m_PanelSQL.add(panel2, BorderLayout.EAST);
    panel = new JPanel(new GridLayout(2, 1));
    panel2.add(panel, BorderLayout.NORTH);

    m_ComboboxTemplates = new JComboBox(new Vector(getSequenceProvider().getTemplates().keySet()));
    m_ComboboxTemplates.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loadTemplate();
      }
    });
    panel.add(m_ComboboxTemplates);

    m_ButtonExecute = new JButton("Execute");
    m_ButtonExecute.setMnemonic('E');
    m_ButtonExecute.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        execute();
      }
    });
    panel.add(m_ButtonExecute);

    // results
    m_PanelResults = new JPanel(new BorderLayout());
    add(m_PanelResults, BorderLayout.CENTER);

    m_TabbedPaneResults = new BaseTabbedPane();
    m_PanelResults.add(m_TabbedPaneResults, BorderLayout.CENTER);
  }

  /**
   * Returns the sequence provider used for generating the data.
   *
   * @return		the sequence provider in use
   */
  protected abstract SequenceProvider getSequenceProvider();

  /**
   * Creates a new document for the dialog, with syntax highlighting support.
   *
   * @return		the new document
   */
  public static Document createDocument() {
    Document	result;
    Properties	props;

    props  = Environment.getInstance().read(SequenceViewerDefinition.KEY);
    result = new SyntaxDocument(props);

    return result;
  }

  /**
   * Loads the currently selected template into the text area.
   */
  protected void loadTemplate() {
    if (m_ComboboxTemplates.getSelectedIndex() == -1)
      return;

    m_TextSQL.setText(getSequenceProvider().getTemplates().get(m_ComboboxTemplates.getSelectedItem()));
  }

  /**
   * Executes the current SQL and displays the results.
   */
  protected synchronized void execute() {
    Vector<XYSequence>	sequences;
    int			i;
    XYSequenceTable	table;
    XYSequencePanel	panel;

    if (m_TextSQL.getText().trim().length() == 0)
      return;

    sequences = getSequenceProvider().retrieve(m_TextSQL.getText());

    // remove all tabs
    m_TabbedPaneResults.removeAll();

    // add tables
    for (i = 0; i < sequences.size(); i++) {
      table = new XYSequenceTable();
      table.setModel(new XYSequenceTable.Model(sequences.get(i)));
      table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
      m_TabbedPaneResults.addTab(sequences.get(i).getID(), new BaseScrollPane(table));
    }

    // add graph
    panel = new XYSequencePanel();
    m_TabbedPaneResults.addTab("Graph", panel);
    panel.getContainerManager().startUpdate();
    for (i = 0; i < sequences.size(); i++) {
      panel.getContainerManager().add(
	  panel.getContainerManager().newContainer(sequences.get(i)));
    }
    panel.getContainerManager().finishUpdate();
  }
}
