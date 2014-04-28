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
 * PixelSelector.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import adams.data.image.AbstractImage;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.transformer.pixelselector.AbstractPixelSelectorAction;
import adams.flow.transformer.pixelselector.AbstractPixelSelectorOverlay;
import adams.flow.transformer.pixelselector.PixelSelectorPanel;
import adams.gui.core.BasePanel;
import adams.gui.visualization.image.ImageOverlay;

/**
 <!-- globalinfo-start -->
 * Allows the user to select pixels. How the pixels are interpreted depends on the actions selected.<br/>
 * In addition, overlays that visualize the information stored in the image's report can be selected as well. The overlays offer information on what actions generate the data that is required for proper visualization.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImage<br/>
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
 * &nbsp;&nbsp;&nbsp;default: PixelSelector
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
 * <pre>-stop-if-canceled (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * </pre>
 * 
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow 
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-action &lt;adams.flow.transformer.pixelselector.AbstractPixelSelectorAction&gt; [-action ...] (property: actions)
 * &nbsp;&nbsp;&nbsp;The overlays available to the user from the popup menu of the viewer.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-overlay &lt;adams.flow.transformer.pixelselector.AbstractPixelSelectorOverlay&gt; [-overlay ...] (property: overlays)
 * &nbsp;&nbsp;&nbsp;The image overlays to use in the viewer.
 * &nbsp;&nbsp;&nbsp;default: 
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
public class PixelSelector
  extends AbstractInteractiveTransformerDialog {

  /** for serialization. */
  private static final long serialVersionUID = 2400845362270344769L;

  /** the actions that are available from the popup menu. */
  protected AbstractPixelSelectorAction[] m_Actions;

  /** the overlays to use. */
  protected AbstractPixelSelectorOverlay[] m_Overlays;
  
  /** the position of the splitter. */
  protected int m_SplitterPosition;
  
  /** whether the user approved (or canceled) the dialog. */
  protected Boolean m_Approved;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Allows the user to select pixels. How the pixels are interpreted "
	+ "depends on the actions selected.\n"
        + "In addition, overlays that visualize the information stored in "
	+ "the image's report can be selected as well. The overlays offer "
        + "information on what actions generate the data that is required "
	+ "for proper visualization.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "action", "actions",
	    new AbstractPixelSelectorAction[0]);

    m_OptionManager.add(
	    "overlay", "overlays",
	    new AbstractPixelSelectorOverlay[0]);

    m_OptionManager.add(
	    "splitter-pos", "splitterPosition",
	    getDefaultSplitterPosition(), 1, null);
  }

  /**
   * Sets the available popup actions.
   *
   * @param value	the actions
   */
  public void setActions(AbstractPixelSelectorAction[] value) {
    m_Actions = value;
    reset();
  }

  /**
   * Returns the available popup actions.
   *
   * @return		the actions
   */
  public AbstractPixelSelectorAction[] getActions() {
    return m_Actions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actionsTipText() {
    return "The overlays available to the user from the popup menu of the viewer.";
  }

  /**
   * Sets the available image overlays.
   *
   * @param value	the overlays
   */
  public void setOverlays(AbstractPixelSelectorOverlay[] value) {
    m_Overlays = value;
    reset();
  }

  /**
   * Returns the available image overlays.
   *
   * @return		the overlays
   */
  public AbstractPixelSelectorOverlay[] getOverlays() {
    return m_Overlays;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlaysTipText() {
    return "The image overlays to use in the viewer.";
  }

  /**
   * Returns the default position for the splitter.
   */
  protected int getDefaultSplitterPosition() {
    return 500;
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
  public Class[] accepts() {
    return new Class[]{AbstractImage.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.data.image.AbstractImage.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{AbstractImage.class};
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    ((PixelSelectorPanel) m_Panel).setImage(null);
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    PixelSelectorPanel	result;
    
    result = new PixelSelectorPanel();
    result.setSplitterPosition(m_SplitterPosition);
    result.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_Approved = (((PixelSelectorPanel) m_Panel).getResult() == PixelSelectorPanel.APPROVE_OPTION);
      }
    });
    
    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    int		i;
    String	msg;
    
    result = super.setUp();
    
    if (result == null) {
      for (i = 0; i < m_Actions.length; i++) {
	msg = m_Actions[i].check();
	if (msg != null) {
	  result = "Action #" + (i+1) + " (" + m_Actions[i].getClass().getName() + ") failed check: " + msg;
	  break;
	}
      }
    }
    
    return result;
  }

  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    boolean		result;
    PixelSelectorPanel	panel;
    
    result = false;
    
    panel = (PixelSelectorPanel) m_Panel;
    panel.setImage((AbstractImage) m_InputToken.getPayload());
    panel.setActions(m_Actions);
    panel.clearImageOverlays();
    for (ImageOverlay overlay: m_Overlays)
      panel.addImageOverlay(overlay);
    
    m_Dialog.setVisible(true);
    
    if ((m_Approved != null) && m_Approved) {
      result = true;
      m_OutputToken = new Token(panel.getImage());
    }
    
    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    m_Approved = null;
    result     = super.doExecute();
    
    if (m_Approved == null) {
      if ((m_StopMessage != null) && (m_StopMessage.length() > 0))
	result = m_StopMessage;
      else
	result = "User cancelled dialog!";
    }
    
    if (result == null) {
      if (m_OutputToken != null)
	updateProvenance(m_OutputToken);
    }
    
    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.PREPROCESSOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
