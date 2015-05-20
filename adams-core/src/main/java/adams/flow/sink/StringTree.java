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
 * StringTree.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.dotnotationtree.DotNotationNode;
import adams.gui.core.dotnotationtree.DotNotationTree;

/**
 <!-- globalinfo-start -->
 * Generates a tree from the strings arriving at the input, splitting them into parts using the specified separator. This can be used to display files from various directories in a hierarchical view.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: StringTree
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
 * <pre>-separator &lt;java.lang.String&gt; (property: separator)
 * &nbsp;&nbsp;&nbsp;The separator to use for splitting the strings into parts.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 * 
 * <pre>-sorted &lt;boolean&gt; (property: sorted)
 * &nbsp;&nbsp;&nbsp;Whether to sort the entries in the tree.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringTree
  extends AbstractGraphicalDisplay {

  /** for serialization. */
  private static final long serialVersionUID = 3452547680157056093L;
  
  /**
   * The custom node for the {@link HierarchicalStringTree}.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class HierarchicalStringNode
    extends DotNotationNode {

    /** for serialization. */
    private static final long serialVersionUID = -3163677863942389729L;
    
    /** the separator to use. */
    protected String m_Separator;
    
    /**
     * Initializes the node with the specified label.
     *
     * @param label	the label for this node
     * @param separator	the separator to use
     */
    public HierarchicalStringNode(String label, String separator) {
      super(label);
      m_Separator = separator;
    }
    
    /**
     * Returns the separator in use.
     * 
     * @return		the separator
     */
    @Override
    public String getSeparator() {
      return m_Separator;
    }
  }
  
  /**
   * Custom tree for displaying the strings.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class HierarchicalStringTree
    extends DotNotationTree<HierarchicalStringNode> {
    
    /** for serialization. */
    private static final long serialVersionUID = -646089462979164896L;
    
    /** the separator to use. */
    protected String m_Separator;
    
    /**
     * Initializes the tree.
     * 
     * @param separator		the separator to use
     */
    public HierarchicalStringTree(String separator) {
      super();
      m_Separator = separator;
    }
    
    /**
     * Splits the item into its sub-parts.
     * 
     * @param item	the full item string to split
     * @return		the generated parts, without the separator
     */
    @Override
    protected String[] splitItem(String item) {
      List<String>	result;
      int		pos;
      String		tmp;
      
      result = new ArrayList<String>();
      tmp    = item;
      while ((pos = tmp.indexOf(m_Separator)) > -1) {
	if (pos > 0)
	  result.add(tmp.substring(0, pos));
	tmp = tmp.substring(pos + m_Separator.length());
      }
      
      if (!tmp.isEmpty())
	result.add(tmp);
      
      return result.toArray(new String[result.size()]);
    }
    
    /**
     * Returns the separator in use.
     * 
     * @return		the separator
     */
    @Override
    public String getSeparator() {
      return m_Separator;
    }
    
    /**
     * Creates a new node with the specified label.
     *
     * @param label	the label to use for the node
     * @return		the new node
     */
    @Override
    protected HierarchicalStringNode newNode(String label) {
      return new HierarchicalStringNode(label, m_Separator);
    }
  }
  
  /** the separator to use. */
  protected String m_Separator;
  
  /** whether to sort the items in the tree. */
  protected boolean m_Sorted;
  
  /** the current tree. */
  protected HierarchicalStringTree m_Tree;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a tree from the strings arriving at the input, splitting "
	+ "them into parts using the specified separator. This can be used "
	+ "to display files from various directories in a hierarchical view.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "separator", "separator",
	    ".");

    m_OptionManager.add(
	    "sorted", "sorted",
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
    
    result  = QuickInfoHelper.toString(this, "separator", m_Separator, "separator: ");
    result += QuickInfoHelper.toString(this, "sorted", m_Sorted, "sorted", ", ");
    
    return result;
  }

  /**
   * Sets name of the content.
   *
   * @param value 	the content name
   */
  public void setSeparator(String value) {
    m_Separator = value;
    reset();
  }

  /**
   * Returns the name of the content.
   *
   * @return 		the content name
   */
  public String getSeparator() {
    return m_Separator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String separatorTipText() {
    return "The separator to use for splitting the strings into parts.";
  }

  /**
   * Sets whether to sort the entries in the tree.
   *
   * @param value 	true if sorted
   */
  public void setSorted(boolean value) {
    m_Sorted = value;
    reset();
  }

  /**
   * Returns whether to store the entries in the tree.
   *
   * @return 		true if sorted
   */
  public boolean getSorted() {
    return m_Sorted;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortedTipText() {
    return "Whether to sort the entries in the tree.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, String[].class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    String[]	items;
    
    if (m_Tree == null)
      return;

    items = null;
    if (token.getPayload() instanceof String)
      items = new String[]{(String) token.getPayload()};
    else if (token.getPayload() instanceof String[])
      items = (String[]) token.getPayload();

    if (items != null)
      m_Tree.addItems(items);
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Tree != null)
      m_Tree.setItems(new String[0]);
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel	result;
    
    result = new BasePanel(new BorderLayout());
    m_Tree = new HierarchicalStringTree(m_Separator);
    m_Tree.setSorted(m_Sorted);
    result.add(new BaseScrollPane(m_Tree), BorderLayout.CENTER);
    
    return result;
  }
}
