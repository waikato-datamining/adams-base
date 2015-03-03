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
 * WekaInstanceViewer.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.awt.BorderLayout;

import adams.data.report.DataType;
import adams.data.report.Field;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.visualization.instance.InstanceContainer;
import adams.gui.visualization.instance.InstanceContainerManager;
import adams.gui.visualization.instance.InstancePanel;

/**
 <!-- globalinfo-start -->
 * Actor for displaying adams.data.instance.Instance objects in a graphical way (using the internal format), like the 'Instance Explorer' tool.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.instance.Instance<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: WekaInstanceViewer
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
 * &nbsp;&nbsp;&nbsp;default: 500
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
 * <pre>-zoom-overview &lt;boolean&gt; (property: zoomOverview)
 * &nbsp;&nbsp;&nbsp;If enabled, a zoom overview panel gets displayed as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-id &lt;java.lang.String&gt; (property: ID)
 * &nbsp;&nbsp;&nbsp;The name of the attribute&#47;field to use as the ID in the display.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstanceViewer
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider, TextSupplier {

  /** for serialization. */
  private static final long serialVersionUID = 1283926389472133810L;

  /** the panel with the instances. */
  protected InstancePanel m_InstancePanel;

  /** whether to display the zoom overview. */
  protected boolean m_ZoomOverview;
  
  /** the name of the attribute/field to use as ID. */
  protected String m_ID;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Actor for displaying adams.data.instance.Instance objects in a "
      + "graphical way (using the internal format), like the 'Instance Explorer' tool.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "zoom-overview", "zoomOverview",
	    false);

    m_OptionManager.add(
	    "id", "ID",
	    "");
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
    return 500;
  }

  /**
   * Sets whether to display the zoom overview.
   *
   * @param value 	if true then the zoom overview will get displayed
   */
  public void setZoomOverview(boolean value) {
    m_ZoomOverview = value;
    reset();
  }

  /**
   * Returns whether the zoom overview gets displayed.
   *
   * @return 		true if the zoom overview gets displayed
   */
  public boolean getZoomOverview() {
    return m_ZoomOverview;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String zoomOverviewTipText() {
    return "If enabled, a zoom overview panel gets displayed as well.";
  }

  /**
   * Sets the name of the attribute/field to use as ID in the display.
   *
   * @param value 	the attribute/field name
   */
  public void setID(String value) {
    m_ID = value;
    reset();
  }

  /**
   * Returns the name of the attribute/field to use as ID in the display.
   *
   * @return 		the attribute/field name
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String IDTipText() {
    return "The name of the attribute/field to use as the ID in the display.";
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_InstancePanel != null)
      m_InstancePanel.getContainerManager().clear();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel	result;

    result          = new BasePanel(new BorderLayout());
    m_InstancePanel = new InstancePanel();
    m_InstancePanel.setZoomOverviewPanelVisible(m_ZoomOverview);
    result.add(m_InstancePanel, BorderLayout.CENTER);

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instance.class, adams.data.instance.Instance.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{weka.core.Instance.class, adams.data.instance.Instance.class};
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    InstanceContainerManager		manager;
    InstanceContainer			cont;
    weka.core.Instance			winst;
    weka.core.Attribute			att;
    String				id;
    adams.data.instance.Instance	inst;

    if (token.getPayload() instanceof weka.core.Instance) {
      winst = (weka.core.Instance) token.getPayload();
      inst  = new adams.data.instance.Instance();
      inst.set(winst);
      if (!m_ID.isEmpty()) {
	att = winst.dataset().attribute(m_ID);
	if (att != null) {
	  if (att.isNominal() || att.isString())
	    id = winst.stringValue(att.index());
	  else
	    id = "" + winst.value(att.index());
	  inst.setID(id);
	}
      }
    }
    else {
      inst = (adams.data.instance.Instance) token.getPayload();
      if (inst.hasReport() && inst.getReport().hasValue(m_ID))
	inst.setID("" + inst.getReport().getValue(new Field(m_ID, DataType.UNKNOWN)));
    }

    manager = m_InstancePanel.getContainerManager();
    cont    = manager.newContainer(inst);
    manager.add(cont);
  }

  /**
   * Removes all graphical components.
   */
  @Override
  protected void cleanUpGUI() {
    super.cleanUpGUI();

    if (m_InstancePanel != null)
      m_InstancePanel.getContainerManager().clear();
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
      private static final long serialVersionUID = -5618543590920864397L;
      protected InstancePanel m_InstancePanel;
      @Override
      protected void initGUI() {
	super.initGUI();
	setLayout(new BorderLayout());
	m_InstancePanel = new InstancePanel();
	add(m_InstancePanel, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	adams.data.instance.Instance inst = null;
	if (token.getPayload() instanceof weka.core.Instance) {
	  inst = new adams.data.instance.Instance();
	  inst.set((weka.core.Instance) token.getPayload());
	}
	else {
	  inst = (adams.data.instance.Instance) token.getPayload();
	}
	InstanceContainerManager manager = m_InstancePanel.getContainerManager();
	InstanceContainer cont = manager.newContainer(inst);
	manager.add(cont);
      }
      @Override
      public ExtensionFileFilter getCustomTextFileFilter() {
        return new ExtensionFileFilter("ARFF file", "arff");
      }
      @Override
      public String supplyText() {
	return WekaInstanceViewer.supplyText(m_InstancePanel);
      }
      @Override
      public void clearPanel() {
	m_InstancePanel.getContainerManager().clear();
      }
      public void cleanUp() {
	m_InstancePanel.getContainerManager().clear();
      }
    };
    
    if (token != null)
      result.display(token);

    return result;
  }

  /**
   * Returns a custom file filter for the file chooser.
   * 
   * @return		the file filter, null if to use default one
   */
  @Override
  public ExtensionFileFilter getCustomTextFileFilter() {
    return new ExtensionFileFilter("ARFF file", "arff");
  }

  /**
   * Supplies the text.
   *
   * @return		the text, null if none available
   */
  public String supplyText() {
    return supplyText(m_InstancePanel);
  }

  /**
   * Returns the displayed instances as ARFF.
   *
   * @param panel	the panel to obtain the data form
   * @return		the generated ARFF content or null if no data available
   */
  protected static String supplyText(InstancePanel panel) {
    InstanceContainerManager 	manager;
    weka.core.Instances 	data;
    int				i;

    if (panel == null)
      return null;
    
    manager = panel.getContainerManager();
    if (manager.countVisible() == 0)
      return null;

    data = new weka.core.Instances(manager.getVisible(0).getData().getDatasetHeader());
    for (i = 0; i < manager.countVisible(); i++)
      data.add(manager.getVisible(i).getData().toInstance());

    return data.toString();
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
