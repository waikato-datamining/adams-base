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
 * Inspect.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.flow.sink.Display;
import adams.flow.sink.DisplayPanel;
import adams.flow.sink.DisplayPanelProvider;
import adams.flow.sink.UpdateableDisplayPanel;
import adams.flow.transformer.AbstractInteractiveTransformerDialog;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;

/**
 <!-- globalinfo-start -->
 * Allows the user to inspect tokens with the specified viewer.<br/>
 * Inspection can be done interactively, allowing the user to drop tokens, or non-interactively, with the viewer updating whenever a token passes through.<br/>
 * Of course, this actor should only be used during development for debugging purposes as can slow down the execution significantly depending on how expensive the view generation is.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br/>
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
 * &nbsp;&nbsp;&nbsp;default: Inspect
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
 * <pre>-stop-if-canceled &lt;boolean&gt; (property: stopFlowIfCanceled)
 * &nbsp;&nbsp;&nbsp;If enabled, the flow gets stopped in case the user cancels the dialog.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-custom-stop-message &lt;java.lang.String&gt; (property: customStopMessage)
 * &nbsp;&nbsp;&nbsp;The custom stop message to use in case a user cancelation stops the flow 
 * &nbsp;&nbsp;&nbsp;(default is the full name of the actor)
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-provider &lt;adams.flow.sink.DisplayPanelProvider&gt; (property: panelProvider)
 * &nbsp;&nbsp;&nbsp;The actor for generating the viewer.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.Display
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Inspect
  extends AbstractInteractiveTransformerDialog {

  /** for serialization. */
  private static final long serialVersionUID = 950028795723993391L;

  /** the interactive text for the button. */
  public final static String TEXT_INTERACTIVE = "Interactive";

  /** the non-interactive text for the button. */
  public final static String TEXT_NONINTERACTIVE = "Non-interactive";
  
  /** the actor to use for generating panels. */
  protected DisplayPanelProvider m_PanelProvider;

  /** the button for turning on/off the interactive state of the viewer. */
  protected JButton m_ButtonToggle;

  /** the button for skipping the token. */
  protected JButton m_ButtonSkip;

  /** the button for accepting the token. */
  protected JButton m_ButtonAccept;

  /** whether the dialog is interactive. */
  protected boolean m_Interactive;
  
  /** whether the token was accepted. */
  protected boolean m_Accepted;
  
  /** whether we're currenlty waiting on the user. */
  protected Boolean m_Waiting;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Allows the user to inspect tokens with the specified viewer.\n"
	+ "Inspection can be done interactively, allowing the user to drop "
	+ "tokens, or non-interactively, with the viewer updating whenever "
	+ "a token passes through.\n"
	+ "Of course, this actor should only be used during development "
	+ "for debugging purposes as can slow down the execution significantly "
	+ "depending on how expensive the view generation is.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "provider", "panelProvider",
	    new Display());
  }

  /**
   * Sets the panel provider to use for generating the panels.
   *
   * @param value	the panel provider to use
   */
  public void setPanelProvider(DisplayPanelProvider value) {
    m_PanelProvider = value;
    reset();
  }

  /**
   * Returns the panel provider in use for generating the panels.
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
    return "The actor for generating the viewer.";
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

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return m_PanelProvider.accepts();
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] generates() {
    return m_PanelProvider.accepts();
  }
  
  /**
   * Updates the Variables instance in use.
   * <p/>
   * Use with caution!
   *
   * @param value	the instance to use
   */
  @Override
  protected void forceVariables(Variables value) {
    super.forceVariables(value);
    m_PanelProvider.setVariables(value);
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Panel != null)
      ((DisplayPanel) m_Panel).clearPanel();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    return (BasePanel) m_PanelProvider.createDisplayPanel(null);
  }
  
  /**
   * Creates the actual dialog.
   *
   * @param panel	the panel to display in the dialog
   * @return		the created dialog
   */
  @Override
  protected BaseDialog doCreateDialog(BasePanel panel) {
    BaseDialog	result;
    JPanel	panelButtons;
    JPanel	panelPart;
    
    result = super.doCreateDialog(panel);
    result.setModalityType(ModalityType.MODELESS);

    panelButtons = new JPanel(new BorderLayout());
    result.getContentPane().add(panelButtons, BorderLayout.SOUTH);
    
    panelPart = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelButtons.add(panelPart, BorderLayout.WEST);
    
    m_ButtonToggle = new JButton("Interactive");
    m_ButtonToggle.setMnemonic('n');
    m_ButtonToggle.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (m_ButtonToggle.getText().equals(TEXT_INTERACTIVE)) {
	  m_ButtonToggle.setText(TEXT_NONINTERACTIVE);
	  m_Interactive = false;
	  m_Accepted    = true;
	  m_Waiting     = false;
	}
	else {
	  m_ButtonToggle.setText(TEXT_INTERACTIVE);
	  m_Interactive = true;
	}
      }
    });
    panelPart.add(m_ButtonToggle);
    
    panelPart = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(panelPart, BorderLayout.EAST);
    
    m_ButtonSkip = new JButton("Skip");
    m_ButtonSkip.setMnemonic('S');
    m_ButtonSkip.setIcon(GUIHelper.getIcon("delete.gif"));
    m_ButtonSkip.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_Accepted = false;
	m_Waiting  = false;
      }
    });
    panelPart.add(m_ButtonSkip);
    
    m_ButtonAccept = new JButton("Accept");
    m_ButtonAccept.setMnemonic('A');
    m_ButtonAccept.setIcon(GUIHelper.getIcon("accept.png"));
    m_ButtonAccept.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_Accepted = true;
	m_Waiting  = false;
      }
    });
    panelPart.add(m_ButtonAccept);
    
    return result;
  }
  
  /**
   * Updates the button state.
   * 
   * @param beforeDisplay	true if token is not yet displayed
   */
  protected void updateButtons(boolean beforeDisplay) {
    if (!m_Interactive) {
      m_ButtonToggle.setEnabled(true);
      m_ButtonSkip.setEnabled(false);
      m_ButtonAccept.setEnabled(false);
    }
    else {
      m_ButtonToggle.setEnabled(true);
      m_ButtonSkip.setEnabled(beforeDisplay);
      m_ButtonAccept.setEnabled(beforeDisplay);
    }
  }
  
  /**
   * Performs the interaction with the user.
   *
   * @return		true if successfully interacted
   */
  @Override
  public boolean doInteract() {
    boolean	result;
    
    result = true;

    if (!m_Dialog.isVisible())
      updateButtons(false);
    m_Dialog.setVisible(true);
    
    updateButtons(true);
    ((DisplayPanel) m_Panel).display(m_InputToken);
    
    if (m_Interactive) {
      m_Waiting = true;
      while (m_Waiting && !m_Stopped) {
	try {
	  synchronized(this) {
	    wait(100);
	  }
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }
    
    if (m_Accepted || !m_Interactive) {
      if (m_Panel instanceof UpdateableDisplayPanel)
	m_OutputToken = ((UpdateableDisplayPanel) m_Panel).getUpdatedToken();
      else
	m_OutputToken = m_InputToken;
    }

    updateButtons(false);
    
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
    
    result = super.setUp();
    
    if (result == null)
      m_Interactive = true;
    
    return result;
  }
  
  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Waiting != null)
      m_Waiting = false;
    
    super.stopExecution();
  }
  
  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    m_Waiting = null;

    if (m_Dialog != null) {
      if (m_Dialog.isVisible())
	m_Dialog.setVisible(false);
    }
    
    super.wrapUp();
  }
}
