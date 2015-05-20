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
 * DOMDisplay.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import java.awt.BorderLayout;
import java.util.logging.Level;

import org.w3c.dom.Node;

import adams.core.QuickInfoHelper;
import adams.data.conversion.DOMNodeToString;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.dom.DOMTreeWithPreview;

/**
 <!-- globalinfo-start -->
 * Displays a DOM node object as tree structure.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;org.w3c.dom.Node<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: XMLDisplay
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
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 * 
 * <pre>-preview &lt;boolean&gt; (property: preview)
 * &nbsp;&nbsp;&nbsp;If enabled, a table is shown that displays the attributes of the Node object 
 * &nbsp;&nbsp;&nbsp;associated with the currently selected node in the tree.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-expand &lt;boolean&gt; (property: expand)
 * &nbsp;&nbsp;&nbsp;If enabled, the tree gets fully expanded.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-splitter-pos &lt;int&gt; (property: splitterPosition)
 * &nbsp;&nbsp;&nbsp;The initial position of the splitter in the viewer panel.
 * &nbsp;&nbsp;&nbsp;default: 500
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DOMDisplay
  extends AbstractGraphicalDisplay 
  implements DisplayPanelProvider, TextSupplier {

  /** for serialization. */
  private static final long serialVersionUID = 680299970232233254L;
  
  /** the tree displaying the DOM object. */
  protected DOMTreeWithPreview m_Tree;

  /** whether to offer preview. */
  protected boolean m_Preview;

  /** whether to fully expand the tree. */
  protected boolean m_Expand;
  
  /** the position of the splitter. */
  protected int m_SplitterPosition;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays a DOM node object as tree structure.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "preview", "preview",
	    false);

    m_OptionManager.add(
	    "expand", "expand",
	    false);

    m_OptionManager.add(
	    "splitter-pos", "splitterPosition",
	    getDefaultSplitterPosition(), 1, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();

    result += QuickInfoHelper.toString(this, "preview", m_Preview, "preview", ", ");
    result += QuickInfoHelper.toString(this, "expand", m_Preview, "expand", ", ");

    return result;
  }

  /**
   * Sets whether to show a preview table for the attributes.
   *
   * @param value	if true then preview table is displayed
   */
  public void setPreview(boolean value) {
    m_Preview = value;
    reset();
  }

  /**
   * Returns whether to show a preview table for the attributes.
   *
   * @return		true if preview table is displayed
   */
  public boolean getPreview() {
    return m_Preview;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String previewTipText() {
    return
        "If enabled, a table is shown that displays the attributes of the Node "
	+ "object associated with the currently selected node in the tree.";
  }

  /**
   * Sets whether to fully expand the tree or not.
   *
   * @param value	if true then the tree gets fully expanded
   */
  public void setExpand(boolean value) {
    m_Expand = value;
    reset();
  }

  /**
   * Returns whether to fully expand the tree or not.
   *
   * @return		true if text area is displayed
   */
  public boolean getExpand() {
    return m_Expand;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expandTipText() {
    return "If enabled, the tree gets fully expanded.";
  }

  /**
   * Returns the default position for the splitter.
   */
  protected int getDefaultSplitterPosition() {
    return 400;
  }
  
  /**
   * Sets the initial position of the splitter in the viewer panel.
   *
   * @param value	the position
   */
  public void setSplitterPosition(int value) {
    m_SplitterPosition = value;
    reset();
  }

  /**
   * Returns the initial position of the splitter in the viewer panel.
   *
   * @return		the position
   */
  public int getSplitterPosition() {
    return m_SplitterPosition;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String splitterPositionTipText() {
    return "The initial position of the splitter in the viewer panel.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Node.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    m_Tree.setDOM((Node) token.getPayload());
    if (m_Expand)
      m_Tree.getTree().expandAll();
  }
  
  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Tree != null)
      m_Tree.setDOM(null);
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    m_Tree = new DOMTreeWithPreview();
    m_Tree.setPreviewVisible(m_Preview);
    m_Tree.setSplitterPosition(m_SplitterPosition);
    return m_Tree;
  }

  /**
   * Returns a custom file filter for the file chooser.
   * 
   * @return		the file filter, null if to use default one
   */
  @Override
  public ExtensionFileFilter getCustomTextFileFilter() {
    return new ExtensionFileFilter("XML", "xml");
  }

  /**
   * Supplies the text.
   *
   * @return		the text, null if none available
   */
  @Override
  public String supplyText() {
    String		result;
    DOMNodeToString	conv;
    String		msg;
    
    result = null;
    
    if (m_Tree != null) {
      try {
	conv = new DOMNodeToString();
	conv.setInput(m_Tree.getDOM());
	msg = conv.convert();
	if (msg == null)
	  result = (String) conv.getOutput();
	else
	  getLogger().severe("Failed to convert value to string: " + msg);
	conv.cleanUp();
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to convert value to string:", e);
	result = null;
      }
    }
    
    return result;
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
    
    result = new AbstractTextDisplayPanel(getClass().getSimpleName()) {
      private static final long serialVersionUID = 4356468458332186521L;
      protected DOMTreeWithPreview m_Tree;
      @Override
      protected void initGUI() {
        super.initGUI();
        m_Tree = new DOMTreeWithPreview();
        m_Tree.setPreviewVisible(m_Preview);
        add(m_Tree, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	m_Tree.setDOM((Node) token.getPayload());
        m_Tree.setSplitterPosition(m_SplitterPosition);
	if (m_Expand)
	  m_Tree.getTree().expandAll();
      }
      @Override
      public void clearPanel() {
	m_Tree.setDOM(null);
      }
      @Override
      public void cleanUp() {
	m_Tree.setDOM(null);
      }
      @Override
      public ExtensionFileFilter getCustomTextFileFilter() {
        return new ExtensionFileFilter("XML", "xml");
      }
      @Override
      public String supplyText() {
	if (m_Tree != null) {
	  try {
	    DOMNodeToString conv = new DOMNodeToString();
	    conv.setInput(m_Tree.getDOM());
	    String msg = conv.convert();
	    conv.cleanUp();
	    if (msg == null)
	      return (String) conv.getOutput();
	    else
	      getLogger().severe("Failed to convert value to string: " + msg);
	  }
	  catch (Exception e) {
	    getLogger().log(Level.SEVERE, "Failed to convert value to string:", e);
	  }
	}
	return null;
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
