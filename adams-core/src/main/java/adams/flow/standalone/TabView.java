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
 * TabView.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.flow.core.Actor;
import adams.flow.sink.CallableSink;
import adams.flow.sink.ComponentSupplier;
import adams.flow.sink.TextSupplier;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.ExtensionFileFilter;

import javax.swing.JComponent;
import javax.swing.JLabel;
import java.awt.BorderLayout;

/**
 <!-- globalinfo-start -->
 * Displays multiple graphical actors in a tabbed pane. The actors can be referenced in the flow using adams.flow.sink.CallableSink actors.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: TabView
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
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
 * <pre>-actor &lt;adams.flow.core.Actor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The panel-generating actors to display in the tabbed pane.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TabView
  extends AbstractMultiView
  implements ComponentSupplier, TextSupplier, SpreadSheetSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -4454052058077687116L;
  
  /** the tabbed pane. */
  protected BaseTabbedPane m_TabbedPane;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Displays multiple graphical actors in a tabbed pane. The actors "
	+ "can be referenced in the flow using " 
	+ CallableSink.class.getName() + " actors.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String actorsTipText() {
    return "The panel-generating actors to display in the tabbed pane.";
  }

  /**
   * Ensures that the wrapper is visible.
   * 
   * @param wrapper	the wrapper to make visible
   * @return		true if successful
   */
  @Override
  public boolean makeVisible(ViewWrapper wrapper) {
    int		index;
    
    if (m_Wrappers != null) {
      index = m_Wrappers.indexOf(wrapper);
      if (index > -1) {
	m_TabbedPane.setSelectedIndex(index);
	return true;
      }
    }
    
    return false;
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel	result;
    BasePanel	panel;
    JLabel	label;
    int		i;
    
    result = new BasePanel(new BorderLayout());
    
    m_TabbedPane = new BaseTabbedPane();
    result.add(m_TabbedPane, BorderLayout.CENTER);

    // add dummy panels
    for (i = 0; i < m_Actors.size(); i++) {
      panel = new BasePanel(new BorderLayout());
      label = new JLabel(m_Actors.get(i).getName(), JLabel.CENTER);
      panel.add(label, BorderLayout.CENTER);
      m_TabbedPane.addTab(m_Actors.get(i).getName(), panel);
    }
    
    return result;
  }
  
  /**
   * Replaces the current dummy panel with the actual panel.
   * 
   * @param actor	the actor this panel is for
   * @param panel	the panel to replace the dummy one
   */
  @Override
  public void addPanel(Actor actor, BasePanel panel) {
    int		index;
    
    index = indexOf(actor.getName());
    m_TabbedPane.setComponentAt(index, panel);
  }

  /**
   * Returns the current component.
   *
   * @return		the current component, can be null
   */
  @Override
  public JComponent supplyComponent() {
    int		index;
    
    index = m_TabbedPane.getSelectedIndex();
    if (index > -1) {
      if (m_Actors.get(index) instanceof ComponentSupplier)
	return ((ComponentSupplier) m_Actors.get(index)).supplyComponent();
      if (m_TabbedPane.getComponentAt(index) instanceof JComponent)
	return (JComponent) m_TabbedPane.getComponentAt(index);
    }

    return null;
  }

  /**
   * Returns a custom file filter for the file chooser.
   * 
   * @return		the file filter, null if to use default one
   */
  @Override
  public ExtensionFileFilter getCustomTextFileFilter() {
    int		index;
    
    index = m_TabbedPane.getSelectedIndex();
    if (index > -1) {
      if (m_Actors.get(index) instanceof TextSupplier)
	return ((TextSupplier) m_Actors.get(index)).getCustomTextFileFilter();
    }

    return null;
  }

  /**
   * Supplies the text.
   *
   * @return		the text, null if none available
   */
  @Override
  public String supplyText() {
    int		index;
    
    index = m_TabbedPane.getSelectedIndex();
    if (index > -1) {
      if (m_Actors.get(index) instanceof TextSupplier)
	return ((TextSupplier) m_Actors.get(index)).supplyText();
    }

    return null;
  }

  /**
   * Returns the content as spreadsheet.
   * 
   * @return		the content, null if not available
   */
  public SpreadSheet toSpreadSheet() {
    int		index;
    
    index = m_TabbedPane.getSelectedIndex();
    if (index > -1) {
      if (m_Actors.get(index) instanceof SpreadSheetSupporter)
	return ((SpreadSheetSupporter) m_Actors.get(index)).toSpreadSheet();
    }

    return null;
  }
}
