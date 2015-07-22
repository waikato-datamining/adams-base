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
 * ReportDisplay.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.VariableNameNoUpdate;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.visualization.report.ReportContainer;
import adams.gui.visualization.report.ReportFactory;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

/**
 <!-- globalinfo-start -->
 * Displays reports.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ReportDisplay
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-display-in-editor &lt;boolean&gt; (property: displayInEditor)
 * &nbsp;&nbsp;&nbsp;If enabled displays the panel in a tab in the flow editor rather than in 
 * &nbsp;&nbsp;&nbsp;a separate frame.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-font &lt;java.awt.Font&gt; (property: font)
 * &nbsp;&nbsp;&nbsp;The font of the dialog.
 * &nbsp;&nbsp;&nbsp;default: Monospaced-PLAIN-12
 * </pre>
 * 
 * <pre>-always-clear &lt;boolean&gt; (property: alwaysClear)
 * &nbsp;&nbsp;&nbsp;If enabled, the display is always cleared before processing a token.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-entry-name-variable &lt;adams.core.VariableNameNoUpdate&gt; (property: entryNameVariable)
 * &nbsp;&nbsp;&nbsp;The variable to use for naming the entries; gets ignored if variable not 
 * &nbsp;&nbsp;&nbsp;available; an existing history entry gets replaced if a new one with the 
 * &nbsp;&nbsp;&nbsp;same name gets added.
 * &nbsp;&nbsp;&nbsp;default: entryNameVariable
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportDisplay
  extends AbstractTextualDisplay {

  /** for serialization. */
  private static final long serialVersionUID = 934663436062863370L;

  /** the panel. */
  protected ReportFactory.Panel m_Panel;

  /** the search panel. */
  protected SearchPanel m_SearchPanel;

  /** the variable to use for naming the entries. */
  protected VariableNameNoUpdate m_EntryNameVariable;

  /** whether a report has been selected. */
  protected boolean m_ReportSelected;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays reports.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "entry-name-variable", "entryNameVariable",
      new VariableNameNoUpdate("entryNameVariable"));
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 600;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 600;
  }

  /**
   * Sets the variable name which value gets used to name the entries. Gets
   * ignored if variable does not exist.
   *
   * @param value	the variable name
   */
  public void setEntryNameVariable(VariableNameNoUpdate value) {
    m_EntryNameVariable = value;
    reset();
  }

  /**
   * Returns the variable name which value gets used to name the entries.
   * Gets ignored if variable does not exist.
   *
   * @return		the variable name
   */
  public VariableNameNoUpdate getEntryNameVariable() {
    return m_EntryNameVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String entryNameVariableTipText() {
    return "The variable to use for naming the entries; gets ignored if variable not available; an existing history entry gets replaced if a new one with the same name gets added.";
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Panel != null)
      m_Panel.getContainerManager().clear();
    m_ReportSelected = false;
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel	result;
    JPanel	panel;

    m_Panel = ReportFactory.getPanel(new ArrayList<ReportContainer>());
    m_Panel.setDividerLocation((int) (getWidth() * 0.67));

    result = new BasePanel(new BorderLayout());
    result.add(m_Panel, BorderLayout.CENTER);

    // search
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_SearchPanel = new SearchPanel(LayoutType.HORIZONTAL, true);
    m_SearchPanel.addSearchListener(new SearchListener() {
      public void searchInitiated(SearchEvent e) {
	m_Panel.search(
	    m_SearchPanel.getSearchText(), m_SearchPanel.isRegularExpression());
	m_SearchPanel.grabFocus();
      }
    });
    panel.add(m_SearchPanel);
    result.add(panel, BorderLayout.SOUTH);

    m_ReportSelected = false;

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.report.ReportHandler.class, adams.data.report.Report.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{ReportHandler.class, Report.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    Report		report;
    ReportContainer	cont;
    String		var;
    String		id;

    if (token.getPayload() instanceof ReportHandler)
      report = ((ReportHandler) token.getPayload()).getReport();
    else
      report = (Report) token.getPayload();

    if (report != null) {
      cont = m_Panel.getContainerManager().newContainer(report);
      var = getEntryNameVariable().getValue();
      if (getVariables().has(var)) {
	id = getVariables().get(var);
	cont.setID(id);
      }
      m_Panel.getContainerManager().add(cont);
    }
  }

  /**
   * After the token has been displayed.
   *
   * @param token	the token to display
   */
  @Override
  protected void postDisplay(Token token) {
    Runnable 	runnable;

    super.postDisplay(token);

    if (!m_ReportSelected) {
      runnable = new Runnable() {
	public void run() {
	  if (m_Panel.getContainerManager().count() > 0)
	    m_Panel.setCurrentTable(0);
	}
      };
      SwingUtilities.invokeLater(runnable);
    }
  }

  /**
   * Whether "clear" is supported and shows up in the menu.
   *
   * @return		always true
   */
  @Override
  protected boolean supportsClear() {
    return true;
  }

  /**
   * Clears the display.
   */
  @Override
  protected void clear() {
    m_Panel.getContainerManager().clear();
  }

  /**
   * Returns a custom file filter for the file chooser.
   * 
   * @return		the file filter, null if to use default one
   */
  @Override
  public ExtensionFileFilter getCustomTextFileFilter() {
    return new ExtensionFileFilter("Report file", "props");
  }

  /**
   * Supplies the text.
   *
   * @return		the text, null if none available
   */
  @Override
  public String supplyText() {
    String	result;
    int		index;
    Report	report;

    result = null;

    if (m_Panel != null) {
      index = m_Panel.getSelectedRow();
      if (index != -1) {
	report = ((ReportContainer) m_Panel.getData().get(index)).getReport();
	if (report != null)
	  result = report.toProperties().toString();
      }
    }

    return result;
  }
}
