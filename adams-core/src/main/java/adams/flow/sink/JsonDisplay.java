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
 * JsonDisplay.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import java.awt.BorderLayout;

import javax.swing.JComponent;

import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import adams.core.JsonSupporter;
import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.json.JsonTree;
import adams.gui.core.json.JsonTreeWithPreview;

/**
 <!-- globalinfo-start -->
 * Displays a JSON object as tree structure.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;org.json.simple.JSONAware<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: JsonDisplay
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-short-title (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
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
 * <pre>-preview (property: preview)
 * &nbsp;&nbsp;&nbsp;If enabled, a text area is shown that displays the JSON object associated 
 * &nbsp;&nbsp;&nbsp;with the currently selected node in the tree.
 * </pre>
 * 
 * <pre>-expand (property: expand)
 * &nbsp;&nbsp;&nbsp;If enabled, the tree gets fully expanded.
 * </pre>
 * 
 * <pre>-sort-keys (property: sortKeys)
 * &nbsp;&nbsp;&nbsp;If enabled, the keys of JSONObject objects will get sorted for display.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonDisplay
  extends AbstractGraphicalDisplay 
  implements DisplayPanelProvider, TextSupplier {

  /** for serialization. */
  private static final long serialVersionUID = 680299970232233254L;
  
  /** the tree displaying the JSON object. */
  protected JsonSupporter m_Tree;

  /** whether to offer preview. */
  protected boolean m_Preview;

  /** whether to fully expand the tree. */
  protected boolean m_Expand;

  /** whether to sort the keys of {@link JSONObject} objects. */
  protected boolean m_SortKeys;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays a JSON object as tree structure.";
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
	    "sort-keys", "sortKeys",
	    false);
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
    result += QuickInfoHelper.toString(this, "expand", m_Expand, "expand", ", ");
    result += QuickInfoHelper.toString(this, "sortKeys", m_SortKeys, "sort keys", ", ");

    return result;
  }

  /**
   * Sets whether to show a preview text area.
   *
   * @param value	if true then text area is displayed
   */
  public void setPreview(boolean value) {
    m_Preview = value;
    reset();
  }

  /**
   * Returns whether to show a preview text area.
   *
   * @return		true if text area is displayed
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
        "If enabled, a text area is shown that displays the JSON object "
	+ "associated with the currently selected node in the tree.";
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
   * Sets whether to sort the keys of {@link JSONObject} objects.
   *
   * @param value	if true then keys get sorted
   */
  public void setSortKeys(boolean value) {
    m_SortKeys = value;
    reset();
  }

  /**
   * Returns whether to sort the keys of {@link JSONObject} objects.
   *
   * @return		true if keys get sorted
   */
  public boolean getSortKeys() {
    return m_SortKeys;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortKeysTipText() {
    return "If enabled, the keys of JSONObject objects will get sorted for display.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{JSONAware.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    m_Tree.setJSON((JSONAware) token.getPayload());
    if (m_Expand) {
      if (m_Tree instanceof adams.gui.core.json.JsonTreeWithPreview)
	((adams.gui.core.json.JsonTreeWithPreview) m_Tree).getTree().expandAll();
      else if (m_Tree instanceof adams.gui.core.json.JsonTree)
	((adams.gui.core.json.JsonTree) m_Tree).expandAll();
    }
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Tree != null)
      m_Tree.setJSON(null);
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel	result;
    
    if (m_Preview) {
      m_Tree = new JsonTreeWithPreview();
      ((JsonTreeWithPreview) m_Tree).setSortKeys(m_SortKeys);
      result = (BasePanel) m_Tree;
    }
    else {
      m_Tree = new adams.gui.core.json.JsonTree();
      ((JsonTree) m_Tree).setSortKeys(m_SortKeys);
      result = new BasePanel(new BorderLayout());
      result.add(new BaseScrollPane((JComponent) m_Tree), BorderLayout.CENTER);
    }
    
    return result;
  }

  /**
   * Returns a custom file filter for the file chooser.
   * 
   * @return		the file filter, null if to use default one
   */
  @Override
  public ExtensionFileFilter getCustomTextFileFilter() {
    return new ExtensionFileFilter("JSON", "json");
  }

  /**
   * Supplies the text.
   *
   * @return		the text, null if none available
   */
  @Override
  public String supplyText() {
    if (m_Tree != null)
      return JSONValue.toJSONString(m_Tree.getJSON());
    else
      return null;
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
      protected JsonSupporter m_Tree;
      @Override
      protected void initGUI() {
        super.initGUI();
        if (m_Preview) {
          m_Tree = new adams.gui.core.json.JsonTreeWithPreview();
          ((JsonTreeWithPreview) m_Tree).setSortKeys(m_SortKeys);
          add((JComponent) m_Tree, BorderLayout.CENTER);
        }
        else {
          m_Tree = new adams.gui.core.json.JsonTree();
          ((JsonTree) m_Tree).setSortKeys(m_SortKeys);
          add(new BaseScrollPane((JComponent) m_Tree), BorderLayout.CENTER);
        }
      }
      @Override
      public void display(Token token) {
	m_Tree.setJSON((JSONAware) token.getPayload());
	if (m_Expand) {
	  if (m_Tree instanceof adams.gui.core.json.JsonTreeWithPreview)
	    ((adams.gui.core.json.JsonTreeWithPreview) m_Tree).getTree().expandAll();
	  else if (m_Tree instanceof adams.gui.core.json.JsonTree)
	    ((adams.gui.core.json.JsonTree) m_Tree).expandAll();
	}
      }
      @Override
      public void clearPanel() {
	m_Tree.setJSON(null);
      }
      @Override
      public void cleanUp() {
	m_Tree.setJSON(null);
      }
      @Override
      public ExtensionFileFilter getCustomTextFileFilter() {
        return new ExtensionFileFilter("JSON", "json");
      }
      @Override
      public String supplyText() {
        if (m_Tree != null)
          return JSONValue.toJSONString(m_Tree.getJSON());
        else
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
