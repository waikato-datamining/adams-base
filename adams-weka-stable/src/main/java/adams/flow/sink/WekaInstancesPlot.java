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
 * WekaInstancesPlot.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.data.weka.WekaAttributeIndex;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import weka.core.Instances;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.VisualizePanel;

import javax.swing.JComponent;
import java.awt.BorderLayout;

/**
 <!-- globalinfo-start -->
 * Actor for plotting one attribute vs another.
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
 * &nbsp;&nbsp;&nbsp;default: WekaInstancesPlot
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
 * &nbsp;&nbsp;&nbsp;default: 640
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 480
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
 * <pre>-attribute-x &lt;adams.data.weka.WekaAttributeIndex&gt; (property: attributeX)
 * &nbsp;&nbsp;&nbsp;The attribute to show on the X axis.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-attribute-y &lt;adams.data.weka.WekaAttributeIndex&gt; (property: attributeY)
 * &nbsp;&nbsp;&nbsp;The attribute to show on the Y axis.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstancesPlot
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  /** for serialization. */
  private static final long serialVersionUID = 3247255046513744115L;

  /** the text area. */
  protected VisualizePanel m_VisualizePanel;

  /** the attribute on the X axis. */
  protected WekaAttributeIndex m_AttributeX;
  
  /** the attribute on the Y axis. */
  protected WekaAttributeIndex m_AttributeY;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Actor for plotting one attribute vs another.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "attribute-x", "attributeX",
      new WekaAttributeIndex("1"));

    m_OptionManager.add(
      "attribute-y", "attributeY",
      new WekaAttributeIndex("2"));
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
   * Sets the attribute to show on the X axis.
   *
   * @param value 	the attribute
   */
  public void setAttributeX(WekaAttributeIndex value) {
    m_AttributeX = value;
    reset();
  }

  /**
   * Returns the attribute to show on the X axis.
   *
   * @return 		the attribute
   */
  public WekaAttributeIndex getAttributeX() {
    return m_AttributeX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeXTipText() {
    return "The attribute to show on the X axis.";
  }

  /**
   * Sets the attribute to show on the Y axis.
   *
   * @param value 	the attribute
   */
  public void setAttributeY(WekaAttributeIndex value) {
    m_AttributeY = value;
    reset();
  }

  /**
   * Returns the attribute to show on the Y axis.
   *
   * @return 		the attribute
   */
  public WekaAttributeIndex getAttributeY() {
    return m_AttributeY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeYTipText() {
    return "The attribute to show on the Y axis.";
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
    result += QuickInfoHelper.toString(this, "attributeX", m_AttributeX, ", x-axis: ");
    result += QuickInfoHelper.toString(this, "attributeY", m_AttributeY, ", y-axis: ");

    return result;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_VisualizePanel != null)
      m_VisualizePanel.removeAllPlots();
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
    m_VisualizePanel = new VisualizePanel();
    result.add(m_VisualizePanel, BorderLayout.CENTER);

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
   * Plots the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    PlotData2D		plot;
    Instances 		data;

    try {
      data = (Instances) token.getPayload();
      m_AttributeX.setData(data);
      m_AttributeY.setData(data);
      plot = new PlotData2D(data);
      if ((m_AttributeX.getIntIndex() != -1) && (m_AttributeY.getIntIndex() != -1))
        plot.setPlotName(data.attribute(m_AttributeX.getIntIndex()).name() + " vs " + data.attribute(m_AttributeX.getIntIndex()).name());
      plot.m_displayAllPoints = true;
      m_VisualizePanel.addPlot(plot);
      if (m_AttributeX.getIntIndex() != -1)
        m_VisualizePanel.setXIndex(m_AttributeX.getIntIndex());
      if (m_AttributeY.getIntIndex() != -1)
        m_VisualizePanel.setYIndex(m_AttributeY.getIntIndex());
    }
    catch (Exception e) {
      handleException("Failed to display token: " + token, e);
    }
  }

  /**
   * Removes all graphical components.
   */
  @Override
  protected void cleanUpGUI() {
    super.cleanUpGUI();

    if (m_VisualizePanel != null) {
      m_VisualizePanel.removeAllPlots();
      m_VisualizePanel = null;
    }
  }

  /**
   * Creates a new panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  public AbstractDisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;
    String			name;

    name = "Instances plot";

    result = new AbstractComponentDisplayPanel(name) {
      private static final long serialVersionUID = -7362768698548152899L;
      protected VisualizePanel m_VisualizePanel;
      @Override
      protected void initGUI() {
	super.initGUI();
	setLayout(new BorderLayout());
	m_VisualizePanel = new VisualizePanel();
	add(m_VisualizePanel, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	PlotData2D plot;
	Instances data;

	try {
	  data = (Instances) token.getPayload();
	  m_AttributeX.setData(data);
	  m_AttributeY.setData(data);
	  plot = new PlotData2D(data);
	  if ((m_AttributeX.getIntIndex() != -1) && (m_AttributeY.getIntIndex() != -1))
	    plot.setPlotName(data.attribute(m_AttributeX.getIntIndex()).name() + " vs " + data.attribute(m_AttributeX.getIntIndex()).name());
	  plot.m_displayAllPoints = true;
	  m_VisualizePanel.addPlot(plot);
	  if (m_AttributeX.getIntIndex() != -1)
	    m_VisualizePanel.setXIndex(m_AttributeX.getIntIndex());
	  if (m_AttributeY.getIntIndex() != -1)
	    m_VisualizePanel.setYIndex(m_AttributeY.getIntIndex());
	}
	catch (Exception e) {
	  handleException("Failed to display token: " + token, e);
	}
      }
      @Override
      public JComponent supplyComponent() {
	return m_VisualizePanel;
      }
      @Override
      public void clearPanel() {
	m_VisualizePanel.removeAllPlots();
      }
      public void cleanUp() {
	m_VisualizePanel.removeAllPlots();
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
