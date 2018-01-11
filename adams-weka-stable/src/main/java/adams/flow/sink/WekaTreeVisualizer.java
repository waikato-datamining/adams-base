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
 * WekaTreeVisualizer.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import adams.flow.container.WekaModelContainer;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import weka.core.Drawable;
import weka.gui.treevisualizer.Node;
import weka.gui.treevisualizer.NodePlace;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeBuild;
import weka.gui.treevisualizer.TreeVisualizer;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

/**
 <!-- globalinfo-start -->
 * Displays trees in dot notation. These trees get generated, for instance, by decision trees such as J48.<br>
 * Either displays the contents of a file or an object that implements weka.core.Drawable and generates trees.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Drawable<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaModelContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaModelContainer: Model, Header, Dataset
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
 * &nbsp;&nbsp;&nbsp;default: WekaTreeVisualizer
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaTreeVisualizer
  extends AbstractGraphicalDisplay 
  implements DisplayPanelProvider {

  /** for serialization. */
  private static final long serialVersionUID = 1970232977500522747L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Displays trees in dot notation. These trees get generated, for "
	+ "instance, by decision trees such as J48.\n"
	+ "Either displays the contents of a file or an object that implements "
	+ Drawable.class.getName() + " and generates trees.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class, Drawable.class, WekaModelContainer.class};
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
   * Creates a tree visualizer from the token.
   * 
   * @param token	the input
   * @return		the tree visualizer or null in case of an error
   */
  protected TreeVisualizer createTreeVisualizer(Token token) {
    TreeVisualizer	result;
    Reader		reader;
    TreeBuild 		builder;
    Node 		top;
    NodePlace 		arrange;
    Object		input;
    
    result = null;

    try {
      input = token.getPayload();
      if (input instanceof WekaModelContainer)
	input = ((WekaModelContainer) input).getValue(WekaModelContainer.VALUE_MODEL);
      
      if (input instanceof String) {
	reader = new StringReader((String) input);
      }
      else if (input instanceof File) {
	reader = new BufferedReader(new FileReader(((File) input).getAbsolutePath()));
      }
      else {
	if (((Drawable) input).graphType() != Drawable.TREE)
	  throw new IllegalArgumentException(
	      token.getPayload().getClass().getName() + " does not generate a tree!");
	reader = new StringReader(((Drawable) input).graph());
      }
    }
    catch (Exception e) {
      handleException("Failed to instantiate reader for input!", e);
      return result;
    }
    
    builder = new TreeBuild();
    top     = null;
    arrange = new PlaceNode2();
    try {
      top    = builder.create(reader);
      result = new TreeVisualizer(null, top, arrange);
    }
    catch (Exception e) {
      handleException("Failed to parse dot notation!", e);
    }
    finally {
      if (reader != null) {
	try {
	  reader.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }
  
  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    TreeVisualizer	visualizer;
    
    clearPanel();

    visualizer = createTreeVisualizer(token);
    if (visualizer != null)
      m_Panel.add(visualizer, BorderLayout.CENTER);
  }
  
  /**
   * Returns the current component.
   *
   * @return		the current component, can be null
   */
  @Override
  public JComponent supplyComponent() {
    return (TreeVisualizer) GUIHelper.findFirstComponent(m_Panel, TreeVisualizer.class, true, true);
  }

  /**
   * Creates a new display panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  @Override
  public AbstractDisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;

    result = new AbstractComponentDisplayPanel(getClass().getSimpleName()) {
      private static final long serialVersionUID = 7384093089760722339L;
      protected TreeVisualizer m_Visualizer;
      @Override
      protected void initGUI() {
	super.initGUI();
	setLayout(new BorderLayout());
      }
      @Override
      public void display(Token token) {
	m_Visualizer = createTreeVisualizer(token);
	add(m_Visualizer, BorderLayout.CENTER);
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
	return m_Visualizer;
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
