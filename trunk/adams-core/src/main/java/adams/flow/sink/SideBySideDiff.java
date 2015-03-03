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
 * SideBySideDiff.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.awt.BorderLayout;

import javax.swing.JComponent;

import adams.core.DiffUtils;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.visualization.debug.SideBySideDiffPanel;

/**
 <!-- globalinfo-start -->
 * Actor for displaying a side-by-side diff.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.core.DiffUtils$SideBySideDiff<br/>
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
 * &nbsp;&nbsp;&nbsp;default: SideBySideDiff
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
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
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
public class SideBySideDiff
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  /** for serialization. */
  private static final long serialVersionUID = 1523870513962160664L;

  /** the panel with the image. */
  protected SideBySideDiffPanel m_SideBySideDiffPanel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for displaying a side-by-side diff.";
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 800;
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
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_SideBySideDiffPanel != null)
      m_SideBySideDiffPanel.clear();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    m_SideBySideDiffPanel = new SideBySideDiffPanel();
    return m_SideBySideDiffPanel;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.core.DiffUtils$SideBySideDiff.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{DiffUtils.SideBySideDiff.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    m_SideBySideDiffPanel.display((DiffUtils.SideBySideDiff) token.getPayload());
  }

  /**
   * Removes all graphical components.
   */
  @Override
  protected void cleanUpGUI() {
    super.cleanUpGUI();

    if (m_SideBySideDiffPanel != null)
      m_SideBySideDiffPanel.clear();
  }

  /**
   * Creates a new panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  public AbstractDisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;

    result = new AbstractTextAndComponentDisplayPanel(getClass().getSimpleName()) {
      private static final long serialVersionUID = -166614847446985992L;
      protected SideBySideDiffPanel m_SideBySideDiffPanel;
      @Override
      protected void initGUI() {
	super.initGUI();
	setLayout(new BorderLayout());
	m_SideBySideDiffPanel = new SideBySideDiffPanel();
	add(m_SideBySideDiffPanel, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	m_SideBySideDiffPanel.display((DiffUtils.SideBySideDiff) token.getPayload());
      }
      public void cleanUp() {
      }
      @Override
      public void clearPanel() {
	m_SideBySideDiffPanel.clear();
      }
      @Override
      public JComponent supplyComponent() {
	return m_SideBySideDiffPanel;
      }
      @Override
      public ExtensionFileFilter getCustomTextFileFilter() {
	return null;
      }
      @Override
      public String supplyText() {
	return m_SideBySideDiffPanel.getCurrent().toString();
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
    return true;
  }
}
