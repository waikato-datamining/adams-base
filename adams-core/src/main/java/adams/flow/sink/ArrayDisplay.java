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
 * ArrayDisplay.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.lang.reflect.Array;

/**
 <!-- globalinfo-start -->
 * Displays the array elements in a single panel.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br>
 * &nbsp;&nbsp;&nbsp;java.awt.image.BufferedImage[]<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer[]<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.BufferedImageSupporter[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: ArrayDisplay
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
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
 * <pre>-display-type &lt;adams.flow.core.displaytype.AbstractDisplayType&gt; (property: displayType)
 * &nbsp;&nbsp;&nbsp;Determines how to show the display, eg as standalone frame (default) or
 * &nbsp;&nbsp;&nbsp;in the Flow editor window.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.core.displaytype.Default
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
 * <pre>-panel-provider &lt;adams.flow.sink.DisplayPanelProvider&gt; (property: panelProvider)
 * &nbsp;&nbsp;&nbsp;The actor for generating the display sub-panels.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.ImageViewer -display-type adams.flow.core.displaytype.Default -writer adams.gui.print.NullWriter -selection-processor adams.gui.visualization.image.selection.NullProcessor -left-click-processor adams.gui.visualization.image.leftclick.NullProcessor -selection-shape-painter adams.gui.visualization.image.selectionshape.RectanglePainter -image-overlay adams.gui.visualization.image.NullOverlay
 * </pre>
 *
 * <pre>-arrangement &lt;TABBED|HORIZONTAL|VERTICAL|GRID&gt; (property: arrangement)
 * &nbsp;&nbsp;&nbsp;How to arrange the elements of the array.
 * &nbsp;&nbsp;&nbsp;default: TABBED
 * </pre>
 *
 * <pre>-num-cols &lt;int&gt; (property: numCols)
 * &nbsp;&nbsp;&nbsp;The number of columns to use in case of arrangement type GRID.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ArrayDisplay
    extends AbstractGraphicalDisplay {

  private static final long serialVersionUID = -6836725942835326741L;

  /**
   * How to display the array elements.
   */
  public enum Arrangement {
    TABBED,
    HORIZONTAL,
    VERTICAL,
    GRID,
  }

  /** the actor to use for generating panels. */
  protected DisplayPanelProvider m_PanelProvider;

  /** how to display the array elements. */
  protected Arrangement m_Arrangement;

  /** the number of columns to use (in case of GRID). */
  protected int m_NumCols;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the array elements in a single panel.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"panel-provider", "panelProvider",
	new ImageViewer());

    m_OptionManager.add(
	"arrangement", "arrangement",
	Arrangement.TABBED);

    m_OptionManager.add(
	"num-cols", "numCols",
	1, 1, null);
  }

  /**
   * Sets the panel provider to use for generating the sub-panels.
   *
   * @param value	the panel provider to use
   */
  public void setPanelProvider(DisplayPanelProvider value) {
    m_PanelProvider = value;
    reset();
  }

  /**
   * Returns the panel provider in use for generating the sub-panels.
   *
   * @return		the panel provider in use
   */
  public DisplayPanelProvider getPanelProvider() {
    return m_PanelProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String panelProviderTipText() {
    return "The actor for generating the display sub-panels.";
  }

  /**
   * Sets how to arrange the elements of the array.
   *
   * @param value	the arrangement
   */
  public void setArrangement(Arrangement value) {
    m_Arrangement = value;
    reset();
  }

  /**
   * Returns how to arrange the elements of the array.
   *
   * @return		the arrangement
   */
  public Arrangement getArrangement() {
    return m_Arrangement;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String arrangementTipText() {
    return "How to arrange the elements of the array.";
  }

  /**
   * Sets the number of cols to use in case of {@link Arrangement#GRID}.
   *
   * @param value	the number of cols
   */
  public void setNumCols(int value) {
    m_NumCols = value;
    reset();
  }

  /**
   * Returns the number of cols to use in case of {@link Arrangement#GRID}.
   *
   * @return		the number of cols
   */
  public int getNumCols() {
    return m_NumCols;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numColsTipText() {
    return "The number of columns to use in case of arrangement type " + Arrangement.GRID + ".";
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
    result += QuickInfoHelper.toString(this, "panelProvider", m_PanelProvider, ", provider: ");
    result += QuickInfoHelper.toString(this, "arrangement", m_Arrangement, ", arrangement: ");
    if (m_Arrangement == Arrangement.GRID)
      result += QuickInfoHelper.toString(this, "numCols", m_NumCols, ", #cols: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    Class[] 	result;
    Class[] 	base;
    int		i;
    Class	cls;

    base   = m_PanelProvider.accepts();
    result = new Class[base.length];
    for (i = 0; i < base.length; i++)
      result[i] = Array.newInstance(base[i], 0).getClass();

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
   * Creates the panel to display in the dialog.
   *
   * @return the panel
   */
  @Override
  protected BasePanel newPanel() {
    return new BasePanel();
  }

  /**
   * Displays the array in tabs.
   *
   * @param elements	the array to display
   */
  protected void displayTabbed(Object[] elements) {
    BaseTabbedPane	pane;
    int			i;
    DisplayPanel	panel;

    pane = new BaseTabbedPane();
    for (i = 0; i < elements.length; i++) {
      panel = m_PanelProvider.createDisplayPanel(new Token(elements[i]));
      pane.addTab("" + (i+1), (JComponent) panel);
    }
    m_Panel.setLayout(new BorderLayout());
    m_Panel.add(pane, BorderLayout.CENTER);
  }

  /**
   * Displays the array in a grid.
   *
   * @param elements	the array to display
   * @param numCols	the number of columns
   * @param numRows 	the number of rows
   */
  protected void displayGridded(Object[] elements, int numCols, int numRows) {
    int			i;
    DisplayPanel	panel;

    m_Panel.setLayout(new GridLayout(numRows, numCols));
    for (i = 0; i < elements.length; i++) {
      panel = m_PanelProvider.createDisplayPanel(new Token(elements[i]));
      m_Panel.add((JComponent) panel);
    }
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token the token to display
   */
  @Override
  protected void display(Token token) {
    int		arrayLen;
    Object[]	elements;
    int		i;

    arrayLen = Array.getLength(token.getPayload());
    elements = new Object[arrayLen];
    for (i = 0; i < arrayLen; i++)
      elements[i] = Array.get(token.getPayload(), i);

    switch (m_Arrangement) {
      case TABBED:
	displayTabbed(elements);
	break;
      case HORIZONTAL:
	displayGridded(elements, arrayLen, 1);
	break;
      case VERTICAL:
	displayGridded(elements, 1, arrayLen);
	break;
      case GRID:
	displayGridded(elements, m_NumCols, (int) Math.ceil((double) arrayLen / m_NumCols));
	break;
      default:
	throw new IllegalStateException("Unhandled arrangement type: " + m_Arrangement);
    }
  }
}
