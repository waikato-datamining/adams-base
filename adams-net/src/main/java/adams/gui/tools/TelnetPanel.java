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
 * TelnetPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 * Copyright (C) Apache Software Foundation (original TelnetClientExample)
 */
package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.GUIHelper;

/**
 * A simple telnet interface. Mainly used for testing.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author Bruno D'Avanzo (original TelnetClientExample)
 * @version $Revision$
 */
@MixedCopyright(
  copyright ="Apache Software Foundation",
  author = "Bruno D'Avanzo",
  license = License.APACHE2,
  url = "http://commons.apache.org/proper/commons-net/examples/telnet/TelnetClientExample.java",
  note = "Code adapted from TelnetClientExample"
)
public class TelnetPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 6647177121906710884L;

  /** the remote server. */
  protected JTextField m_TextRemote;

  /** the model of the spinner. */
  protected SpinnerNumberModel m_PortModel;
  
  /** the port. */
  protected JSpinner m_SpinnerPort;

  /** for connecting/disconnecting. */
  protected JButton m_ButtonConnection;
  
  /** text area for the output. */
  protected BaseTextAreaWithButtons m_TextOutput;
  
  /** the button for clearing the output. */
  protected JButton m_ButtonClear;
  
  /** the button for copying the selected output. */
  protected JButton m_ButtonCopy;
  
  /** the text field for the command to issue. */
  protected JTextField m_TextCommand;
  
  /** the button for executing the command. */
  protected JButton m_ButtonCommand;
  
  /** the telnet client. */
  protected TelnetClient m_Client;
  
  /** the command history. */
  protected List<String> m_CommandHistory;
  
  /** the current command index. */
  protected int m_CommandIndex;
  
  @Override
  protected void initialize() {
    TerminalTypeOptionHandler	ttopt;
    EchoOptionHandler 		echoopt;
    SuppressGAOptionHandler 	gaopt;

    super.initialize();

    m_Client = new TelnetClient();

    ttopt   = new TerminalTypeOptionHandler("VT100", false, false, true, false);
    echoopt = new EchoOptionHandler(true, false, true, false);
    gaopt   = new SuppressGAOptionHandler(true, true, true, true);

    try {
      m_Client.addOptionHandler(ttopt);
      m_Client.addOptionHandler(echoopt);
      m_Client.addOptionHandler(gaopt);
    } 
    catch (Exception e) {
      System.err.println("Error registering telnet option handlers:");
      e.printStackTrace();
    }
    
    m_CommandIndex   = 0;
    m_CommandHistory = new ArrayList<String>();
    m_CommandHistory.add("");  // empty command to clear command text field
  }
  
  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel		topPanel;
    JPanel		bottomPanel;
    JLabel		label;
    
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    // connection
    topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(topPanel, BorderLayout.NORTH);
    
    m_TextRemote = new JTextField(20);
    label = new JLabel("Remote");
    label.setDisplayedMnemonic('R');
    label.setLabelFor(m_TextRemote);
    topPanel.add(label);
    topPanel.add(m_TextRemote);
    
    m_PortModel = new SpinnerNumberModel();
    m_PortModel.setMinimum(1);
    m_PortModel.setMaximum(65536);
    m_PortModel.setStepSize(1);
    m_PortModel.setValue(23);
    m_SpinnerPort = new JSpinner(m_PortModel);
    label = new JLabel("Port");
    label.setDisplayedMnemonic('P');
    label.setLabelFor(m_SpinnerPort);
    topPanel.add(label);
    topPanel.add(m_SpinnerPort);
    
    m_ButtonConnection = new JButton();
    m_ButtonConnection.setMnemonic('n');
    m_ButtonConnection.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	handleConnectionEvent();
      }
    });
    topPanel.add(m_ButtonConnection);
    
    // output
    m_ButtonClear = new JButton("Clear", GUIHelper.getIcon("new.gif"));
    m_ButtonClear.setMnemonic('l');
    m_ButtonClear.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextOutput.setText("");
      }
    });
    m_ButtonCopy = new JButton("Copy", GUIHelper.getIcon("copy.gif"));
    m_ButtonCopy.setMnemonic('C');
    m_ButtonCopy.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (m_TextOutput.getSelectedText().length() > 0)
	  GUIHelper.copyToClipboard(m_TextOutput.getSelectedText());
	else if (m_TextOutput.getText().length() > 0)
	  GUIHelper.copyToClipboard(m_TextOutput.getText());
      }
    });
    m_TextOutput = new BaseTextAreaWithButtons(10, 40);
    m_TextOutput.setFont(GUIHelper.getMonospacedFont());
    m_TextOutput.addToButtonsPanel(m_ButtonClear);
    m_TextOutput.addToButtonsPanel(m_ButtonCopy);
    add(m_TextOutput, BorderLayout.CENTER);
    
    // commands
    bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(bottomPanel, BorderLayout.SOUTH);
    
    m_TextCommand = new JTextField(40);
    m_TextCommand.setFont(GUIHelper.getMonospacedFont());
    m_TextCommand.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }
      @Override
      public void keyReleased(KeyEvent e) {
      }
      @Override
      public void keyPressed(KeyEvent e) {
	switch (e.getKeyCode()) {
	  case KeyEvent.VK_ENTER:
	    e.consume();
	    execCommand();
	    break;
	  case KeyEvent.VK_UP:
	    e.consume();
	    previousCommand();
	    break;
	  case KeyEvent.VK_DOWN:
	    e.consume();
	    nextCommand();
	    break;
	}
      }
    });
    label = new JLabel("Command");
    label.setDisplayedMnemonic('m');
    label.setLabelFor(m_TextCommand);
    bottomPanel.add(label);
    bottomPanel.add(m_TextCommand);
    
    m_ButtonCommand = new JButton(GUIHelper.getIcon("run.gif"));
    m_ButtonCommand.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	execCommand();
      }
    });
    bottomPanel.add(m_ButtonCommand);
  }

  /**
   * Finalizes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    updateButtons();
  }
  
  /**
   * Places the previous command in the command text field, if available.
   */
  protected void previousCommand() {
    if (m_CommandIndex > 0)
      m_CommandIndex--;
    else
      m_CommandIndex = m_CommandHistory.size() - 1;
    if (m_CommandIndex < 0)
      m_CommandIndex = 0;
    if (m_CommandIndex < m_CommandHistory.size())
      m_TextCommand.setText(m_CommandHistory.get(m_CommandIndex));
  }
  
  /**
   * Places the next command in the command text field, if available.
   */
  protected void nextCommand() {
    if (m_CommandIndex < m_CommandHistory.size() - 1)
      m_CommandIndex++;
    else
      m_CommandIndex = 0;
    if (m_CommandIndex >= m_CommandHistory.size())
      m_CommandIndex = 0;
    if (m_CommandIndex < m_CommandHistory.size())
      m_TextCommand.setText(m_CommandHistory.get(m_CommandIndex));
  }
  
  /**
   * Appends the msg to the output text area.
   * 
   * @param msg		the text to append
   */
  protected void append(String msg) {
    m_TextOutput.getComponent().append(msg + (msg.endsWith("\n") ? "" : "\n"));
      
  }
  
  /**
   * Checks whether we can connect to a server.
   * 
   * @return		true if we can connect
   */
  protected boolean canConnect() {
    return (m_TextRemote.getText().trim().length() > 0);
  }
  
  /**
   * Connects or disconnects the client.
   */
  protected void handleConnectionEvent() {
    String	msg;
    Runnable	run;

    msg = "??";
    try {
      if (m_Client.isConnected()) {
	msg = "disconnect";
	m_Client.disconnect();
	updateButtons();
      }
      else if (canConnect()) {
	msg = "connect";
	m_Client.connect(m_TextRemote.getText(), ((Number) m_PortModel.getValue()).intValue());
	updateButtons();
	
	run = new Runnable() {
	  public void run() {
	    InputStream instr = m_Client.getInputStream();
	    try {
	      byte[] buff = new byte[1024];
	      int ret_read = 0;
	      do {
		ret_read = instr.read(buff);
		if (ret_read > 0) {
		  append(new String(buff, 0, ret_read));
		  updateButtons();
		}
	      }
	      while (ret_read >= 0);
	    }
	    catch (IOException e) {
	      System.err.println("Exception while reading socket: ");
	      e.printStackTrace();
	    }
	    updateButtons();
	  }
	};
	new Thread(run).start();
      }
    }
    catch (Exception e) {
      append("Failed to " + msg + ": " + e);
      System.err.println("Failed to " + msg + ":");
      e.printStackTrace();
    }
  }

  /**
   * Executes, if possible, the currently entered command.
   */
  protected void execCommand() {
    String	cmd;
    
    updateButtons();
    
    cmd = m_TextCommand.getText();

    if (cmd.trim().length() == 0)
      return;
    if (!m_Client.isConnected())
      return;
    
    try {
      m_TextCommand.setText("");
      append(cmd);
      m_CommandHistory.add(cmd);
      m_Client.getOutputStream().write(new String(cmd + "\n").getBytes());
      m_Client.getOutputStream().flush();
      updateButtons();
    }
    catch (Exception e) {
      append("Failed to execute command " + cmd + ": " + e);
      System.err.println("Failed to execute command: " + cmd);
      e.printStackTrace();
    }
  }
  
  /**
   * Updates the status/text of the buttons.
   */
  protected void updateButtons() {
    if (m_Client.isConnected())
      m_ButtonConnection.setText("Disconnect");
    else
      m_ButtonConnection.setText("Connect");
  }
  
  /**
   * Sets the remote server.
   * 
   * @param value	the server
   */
  public void setRemote(String value) {
    m_TextRemote.setText(value);
  }
  
  /**
   * Returns the current remote server.
   * 
   * @return		the server
   */
  public String getRemote() {
    return m_TextRemote.getText();
  }
  
  /**
   * Sets the port to use.
   * 
   * @param value	the port
   */
  public void setPort(int value) {
    if ((value > 0) && (value <= 65536))
      m_PortModel.setValue(value);
    else
      System.err.println("Invalid port number: " + value);
  }
  
  /**
   * Returns the currently set port.
   * 
   * @return		the port
   */
  public int getPort() {
    return ((Number) m_PortModel.getValue()).intValue();
  }
}
