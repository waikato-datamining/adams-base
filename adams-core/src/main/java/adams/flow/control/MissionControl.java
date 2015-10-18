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
 * MissionControl.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.event.FlowPauseStateEvent;
import adams.event.FlowPauseStateListener;
import adams.flow.core.AbstractDisplay;
import adams.flow.core.ControlActor;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 <!-- globalinfo-start -->
 * Displays a control panel for pausing&#47;resuming&#47;stopping the flow.<br>
 * Useful when executing flows from command-line with the flow runner, offering a minimal control interface.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: MissionControl
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
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 56
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -3
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
 * <pre>-text &lt;java.lang.String&gt; (property: text)
 * &nbsp;&nbsp;&nbsp;The (optional) text to display in the control panel.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MissionControl
  extends AbstractDisplay
  implements ControlActor, FlowPauseStateListener {

  /** for serialization. */
  private static final long serialVersionUID = -6030616525436078513L;

  /**
   * Panel for displaying a control panel.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ControlPanel
    extends BasePanel {

    /** for serialization. */
    private static final long serialVersionUID = 1725406518546756466L;

    /** the owning actor. */
    protected MissionControl m_Owner;

    /** the panel for the text. */
    protected JPanel m_PanelText;

    /** the text to display. */
    protected JLabel m_LabelText;

    /** the buttom for pausing the flow. */
    protected JButton m_ButtonPause;

    /** the buttom for resuming the flow. */
    protected JButton m_ButtonResume;

    /** the button for stopping the flow. */
    protected JButton m_ButtonStop;

    /**
     * Initializes the panel.
     *
     * @param owner	the owning Stopwatch actor
     */
    public ControlPanel(MissionControl owner) {
      super();
      m_Owner = owner;
      m_LabelText.setText(m_Owner.getText());
      m_PanelText.setVisible(!m_Owner.getText().trim().isEmpty());
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      JPanel		panel;

      super.initGUI();

      setLayout(new BorderLayout());

      // text
      m_PanelText = new JPanel(new FlowLayout(FlowLayout.LEFT));
      m_PanelText.setVisible(false);
      add(m_PanelText, BorderLayout.NORTH);

      m_LabelText = new JLabel("");
      m_PanelText.add(m_LabelText);

      // buttons
      panel = new JPanel(new GridLayout(1, 3));
      add(panel, BorderLayout.CENTER);

      m_ButtonPause = new JButton(GUIHelper.getIcon("pause.gif"));
      m_ButtonPause.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  if (getOwner().getRoot() instanceof Flow) {
	    Flow flow = (Flow) getOwner().getRoot();
	    flow.pauseExecution();
	  }
	}
      });
      panel.add(m_ButtonPause);

      m_ButtonResume = new JButton(GUIHelper.getIcon("resume.gif"));
      m_ButtonResume.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  if (getOwner().getRoot() instanceof Flow) {
	    Flow flow = (Flow) getOwner().getRoot();
	    flow.resumeExecution();
	  }
	}
      });
      panel.add(m_ButtonResume);

      m_ButtonStop = new JButton(GUIHelper.getIcon("stop_blue.gif"));
      m_ButtonStop.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  getOwner().getRoot().stopExecution();
	}
      });
      panel.add(m_ButtonStop);
    }

    /**
     * Returns the owning actor.
     *
     * @return		the actor
     */
    public MissionControl getOwner() {
      return m_Owner;
    }

    /**
     * Updates the state of the buttons.
     */
    public void updateButtons() {
      Flow	flow;

      if (getOwner().getRoot() instanceof Flow) {
	flow = (Flow) getOwner().getRoot();
	m_ButtonPause.setEnabled(!flow.isPaused() && !flow.isStopped());
	m_ButtonResume.setEnabled(flow.isPaused() && !flow.isStopped());
	m_ButtonStop.setEnabled(!flow.isStopped());
      }
    }
  }

  /** the (optional) text to display. */
  protected String m_Text;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Displays a control panel for pausing/resuming/stopping the flow.\n"
      + "Useful when executing flows from command-line with the flow runner, "
      + "offering a minimal control interface.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "text", "text",
	    "");
  }

  /**
   * Sets the text to display.
   *
   * @param value	the text
   */
  public void setText(String value) {
    m_Text = value;
    reset();
  }

  /**
   * Returns the text to display.
   *
   * @return		the text
   */
  public String getText() {
    return m_Text;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String textTipText() {
    return "The (optional) text to display in the control panel.";
  }

  /**
   * Returns the default X position for the dialog.
   *
   * @return		the default X position
   */
  @Override
  protected int getDefaultX() {
    return -3;
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 100;
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 56;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String 	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "text", (m_Text.isEmpty() ? "-none-" : m_Text), ", text: ");

    return result;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    ControlPanel result;

    result = new ControlPanel(this);

    return result;
  }

  /**
   * Returns a runnable that displays frame, etc.
   * Must call notifyAll() on the m_Self object and set m_Updating to false.
   *
   * @return		the runnable
   */
  @Override
  protected Runnable newDisplayRunnable() {
    Runnable	result;

    result = new Runnable() {
      public void run() {
	if (getCreateFrame() && !m_Frame.isVisible())
	  m_Frame.setVisible(true);
	((ControlPanel) m_Panel).updateButtons();
	synchronized(m_Self) {
	  m_Self.notifyAll();
	}
	m_Updating = false;
      }
    };

    return result;
  }

  /**
   * Gets called when the pause state of the flow changes.
   *
   * @param e		the event
   */
  @Override
  public void flowPauseStateChanged(FlowPauseStateEvent e) {
    if (m_Panel != null)
      ((ControlPanel) m_Panel).updateButtons();
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    Flow	flow;

    result = super.setUp();

    if (result == null) {
      if (getRoot() instanceof Flow) {
	flow = (Flow) getRoot();
	flow.getPauseStateManager().addListener(this);
      }
      else {
	result = "Root is not a Flow actor? Cannot register pause state listener!";
      }
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    Flow	flow;

    if (getRoot() instanceof Flow) {
      flow = (Flow) getRoot();
      flow.getPauseStateManager().removeListener(this);
    }

    cleanUpGUI();
    super.wrapUp();
  }
}
