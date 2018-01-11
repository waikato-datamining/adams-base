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
 * WekaAttributeSummary.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.data.weka.WekaAttributeRange;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import weka.core.Instances;
import weka.gui.AttributeVisualizationPanel;

import javax.swing.JComponent;
import java.awt.BorderLayout;

/**
 <!-- globalinfo-start -->
 * Displays an attribute summary.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaAttributeSummary
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
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
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
 * &nbsp;&nbsp;&nbsp;default: 400
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
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 * 
 * <pre>-range &lt;adams.data.weka.WekaAttributeRange&gt; (property: range)
 * &nbsp;&nbsp;&nbsp;The range of attributes to visualize.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); attribute names can be surrounded by double quotes.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaAttributeSummary
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  /** for serialization. */
  private static final long serialVersionUID = 1970232977500522747L;

  /** the attribute to visualize. */
  protected WekaAttributeRange m_Range;

  /** the tabbed pane with the attribute visualizations (if more than one in range). */
  protected BaseTabbedPane m_TabbedPane;

  /** the visualization panel (if only one in range). */
  protected AttributeVisualizationPanel m_PanelAtt;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays an attribute summary.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "range", "range",
      new WekaAttributeRange(WekaAttributeRange.ALL));
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
    return 400;
  }

  /**
   * Sets the ranges of attributes to visualize.
   *
   * @param value	the range
   */
  public void setRange(WekaAttributeRange value) {
    m_Range = value;
    reset();
  }

  /**
   * Returns the range of attributes to visualize.
   *
   * @return		the range
   */
  public WekaAttributeRange getRange() {
    return m_Range;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangeTipText() {
    return "The range of attributes to visualize.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "range", m_Range, ", atts: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel	result;

    result = new BasePanel();
    result.setLayout(new BorderLayout());

    return result;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    m_Panel.removeAll();
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    Instances			inst;
    int[]			indices;
    AttributeVisualizationPanel	panel;

    inst = (Instances) token.getPayload();
    m_Range.setData(inst);
    indices = m_Range.getIntIndices();

    clearPanel();

    if (indices.length == 1) {
      m_PanelAtt = new AttributeVisualizationPanel();
      m_PanelAtt.setInstances(inst);
      m_PanelAtt.setAttribute(indices[0]);
      m_Panel.add(m_PanelAtt, BorderLayout.CENTER);
    }
    else if (indices.length > 1) {
      m_TabbedPane = new BaseTabbedPane();
      m_Panel.add(m_TabbedPane, BorderLayout.CENTER);
      for (int index: indices) {
	panel = new AttributeVisualizationPanel();
	panel.setInstances(inst);
	panel.setAttribute(index);
	m_TabbedPane.addTab(inst.attribute(index).name(), panel);
      }
    }
  }

  /**
   * Returns the current component.
   *
   * @return		the current component, can be null
   */
  @Override
  public JComponent supplyComponent() {
    if (m_TabbedPane != null)
      return m_TabbedPane;
    else
      return m_PanelAtt;
  }

  /**
   * Creates a new display panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  @Override
  public DisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;

    result = new AbstractComponentDisplayPanel(getClass().getSimpleName()) {
      private static final long serialVersionUID = 7384093089760722339L;
      protected BaseTabbedPane m_TabbedPane;
      protected AttributeVisualizationPanel m_PanelAtt;
      @Override
      protected void initGUI() {
	super.initGUI();
	setLayout(new BorderLayout());
	m_PanelAtt = new AttributeVisualizationPanel();
	add(m_PanelAtt, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	Instances			inst;
	int[]				indices;
	AttributeVisualizationPanel	panel;

	inst = (Instances) token.getPayload();
	m_Range.setData(inst);
	indices = m_Range.getIntIndices();

	clearPanel();

	if (indices.length == 1) {
	  m_PanelAtt = new AttributeVisualizationPanel();
	  m_PanelAtt.setInstances(inst);
	  m_PanelAtt.setAttribute(indices[0]);
	  m_Panel.add(m_PanelAtt, BorderLayout.CENTER);
	}
	else if (indices.length > 1) {
	  m_TabbedPane = new BaseTabbedPane();
	  m_Panel.add(m_TabbedPane, BorderLayout.CENTER);
	  for (int index: indices) {
	    panel = new AttributeVisualizationPanel();
	    panel.setInstances(inst);
	    panel.setAttribute(index);
	    m_TabbedPane.addTab(inst.attribute(index).name(), panel);
	  }
	}
      }
      @Override
      public void cleanUp() {
      }
      @Override
      public void clearPanel() {
	removeAll();
      }
      @Override
      public JComponent supplyComponent() {
	if (m_TabbedPane != null)
	  return m_TabbedPane;
	else
	  return m_PanelAtt;
      }
    };

    if (token != null)
      result.display(token);

    return result;
  }

  /**
   * Returns whether the created display panel requires a scroll pane or not.
   *
   * @return		true if the display panel requires a scroll pane
   */
  @Override
  public boolean displayPanelRequiresScrollPane() {
    return false;
  }
}
