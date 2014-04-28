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
 * WekaInstancesDisplay.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JTable;

import weka.core.Attribute;
import weka.core.Instances;
import weka.gui.arffviewer.ArffPanel;
import adams.core.DateUtils;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.sendto.SendToActionUtils;

/**
 <!-- globalinfo-start -->
 * Actor for displaying a weka.core.Instances object in table format.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
 * &nbsp;&nbsp;&nbsp;default: InstancesDisplay
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
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 640
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 480
 * &nbsp;&nbsp;&nbsp;minimum: 1
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
 * <pre>-writer &lt;adams.gui.print.JComponentWriter [options]&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstancesDisplay
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider, TextSupplier {

  /** for serialization. */
  private static final long serialVersionUID = 6791882185628220997L;

  /** the panel with the instances. */
  protected ArffPanel m_ArffPanel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for displaying a weka.core.Instances object in table format.";
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 640;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 480;
  }

  /**
   * Creates an empty dummy dataset.
   *
   * @return		the dummy dataset
   */
  protected Instances createDummyDataset() {
    return new Instances("Empty", new ArrayList<Attribute>(), 0);
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_ArffPanel != null)
      m_ArffPanel.setInstances(createDummyDataset());
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel	result;

    result      = new BasePanel(new BorderLayout());
    m_ArffPanel = new ArffPanel(createDummyDataset());
    result.add(m_ArffPanel, BorderLayout.CENTER);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    m_ArffPanel.setInstances((Instances) token.getPayload());
  }

  /**
   * Executes the flow item. 
   * <p/>
   * Outputs the token on the command-line in headless mode.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    
    result = null;
    
    if (m_Headless) {
      System.out.println("\n--> " + DateUtils.getTimestampFormatterMsecs().format(new Date()) + "\n");
      System.out.println(m_InputToken.getPayload());
    }
    else {
      result = super.doExecute();
    }
    
    return result;
  }

  /**
   * Removes all graphical components.
   */
  @Override
  protected void cleanUpGUI() {
    super.cleanUpGUI();

    if (m_ArffPanel != null)
      m_ArffPanel.setInstances(createDummyDataset());
  }

  /**
   * Returns a custom file filter for the file chooser.
   * 
   * @return		the file filter, null if to use default one
   */
  public ExtensionFileFilter getCustomTextFileFilter() {
    return new ExtensionFileFilter("ARFF file", "arff");
  }

  /**
   * Supplies the text.
   *
   * @return		the text, null if none available
   */
  public String supplyText() {
    if (m_ArffPanel != null)
      return m_ArffPanel.getInstances().toString();
    else
      return null;
  }

  /**
   * Creates a new panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  public AbstractDisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;

    result = new AbstractTextDisplayPanel(getClass().getSimpleName()) {
      private static final long serialVersionUID = 7384093089760722339L;
      protected ArffPanel m_ArffPanel;
      @Override
      protected void initGUI() {
	super.initGUI();
	setLayout(new BorderLayout());
	m_ArffPanel = new ArffPanel(new Instances("Dummy", new ArrayList<Attribute>(), 0));
	add(m_ArffPanel, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	m_ArffPanel.setInstances((Instances) token.getPayload());
      }
      @Override
      public ExtensionFileFilter getCustomTextFileFilter() {
        return new ExtensionFileFilter("ARFF file", "arff");
      }
      @Override
      public String supplyText() {
	return m_ArffPanel.getInstances().toString();
      }
      @Override
      public void clearPanel() {
      }
      public void cleanUp() {
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
  public boolean displayPanelRequiresScrollPane() {
    return false;
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  @Override
  public Class[] getSendToClasses() {
    List<Class> 	result;
    
    result = new ArrayList<Class>(Arrays.asList(super.getSendToClasses()));
    if (!result.contains(JTable.class))
      result.add(JTable.class);
    
    return result.toArray(new Class[result.size()]);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the requested classes
   * @return		the item to send
   */
  @Override
  public Object getSendToItem(Class[] cls) {
    Object		result;

    result = null;

    if (SendToActionUtils.isAvailable(JTable.class, cls)) {
      result = m_ArffPanel.getTable();
    }
    else {
      result = super.getSendToItem(cls);
    }

    return result;
  }
}
