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
 * SSHPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 * Copyright (C) JCraft (original Shell example)
 */
package adams.gui.tools;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingSupporter;
import adams.core.net.SSHAuthenticationType;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextPaneWithButtons;
import adams.gui.core.ConsolePanel;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Logger;
import com.jcraft.jsch.Session;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * A simple telnet interface. Mainly used for testing.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author Bruno D'Avanzo (original SSHClientExample)
 * @version $Revision$
 */
@MixedCopyright(
  copyright ="JCraft",
  license = License.BSD3,
  url = "http://www.jcraft.com/jsch/examples/Shell.java",
  note = "Code adapted from Shell"
)
public class SSHPanel
  extends BasePanel
  implements LoggingSupporter {

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

  /** the file panel for the known hosts. */
  protected FileChooserPanel m_FileChooserPanelKnownHosts;

  /** the authentication panels. */
  protected HashMap<SSHAuthenticationType,JPanel> m_AuthenticationPanels;

  /** the authentication panel. */
  protected JPanel m_PanelAuthentication;

  /** the combobox for the authentication type. */
  protected JComboBox<SSHAuthenticationType> m_ComboBoxAuthenticationType;

  /** the file panel for the key. */
  protected FileChooserPanel m_FileChooserPanelKey;

  /** the text field for the private key passphrase. */
  protected JPasswordField m_TextKeyPassphrase;

  /** the text field for the user. */
  protected JTextField m_TextUser;

  /** the text field for the password. */
  protected JPasswordField m_TextPassword;

  /** text area for the output. */
  protected BaseTextPaneWithButtons m_TextOutput;
  
  /** the button for clearing the output. */
  protected JButton m_ButtonClear;
  
  /** the button for copying the selected output. */
  protected JButton m_ButtonCopy;
  
  /** the text field for the command to issue. */
  protected JTextField m_TextCommand;
  
  /** the button for executing the command. */
  protected JButton m_ButtonCommand;
  
  /** the ssh session. */
  protected Session m_Session;

  /** the channel. */
  protected Channel m_Channel;
  
  /** the command history. */
  protected List<String> m_CommandHistory;
  
  /** the current command index. */
  protected int m_CommandIndex;

  /** the logger in use. */
  protected java.util.logging.Logger m_Logger;

  /** the attributeset for commands. */
  protected SimpleAttributeSet m_AttributeSetCmd;

  /** the attributeset for remote output. */
  protected SimpleAttributeSet m_AttributeSetRemote;

  /** the attributeset for errors. */
  protected SimpleAttributeSet m_AttributeSetError;

  @Override
  protected void initialize() {
    super.initialize();

    m_CommandIndex   = 0;
    m_CommandHistory = new ArrayList<String>();
    m_CommandHistory.add("");  // empty command to clear command text field

    m_Session = null;

    m_AuthenticationPanels = new HashMap<>();

    m_AttributeSetCmd = new SimpleAttributeSet();
    StyleConstants.setForeground(m_AttributeSetCmd, Color.BLACK);
    StyleConstants.setFontFamily(m_AttributeSetCmd, "monospaced");
    StyleConstants.setBold(m_AttributeSetCmd, true);

    m_AttributeSetRemote = new SimpleAttributeSet();
    StyleConstants.setForeground(m_AttributeSetRemote, Color.BLACK);
    StyleConstants.setFontFamily(m_AttributeSetRemote, "monospaced");
    StyleConstants.setBold(m_AttributeSetRemote, false);

    m_AttributeSetError = new SimpleAttributeSet();
    StyleConstants.setForeground(m_AttributeSetError, Color.RED);
    StyleConstants.setFontFamily(m_AttributeSetError, "monospaced");
    StyleConstants.setBold(m_AttributeSetError, false);
  }
  
  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel		topPanel;
    JPanel		topPanels;
    JPanel		bottomPanel;
    JLabel		label;
    JPanel		authPanel;
    JPanel		panel;

    super.initGUI();
    
    setLayout(new BorderLayout());
    
    // connection
    topPanels = new JPanel(new GridLayout(3, 1));
    add(topPanels, BorderLayout.NORTH);

    topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topPanels.add(topPanel);

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
    m_PortModel.setValue(22);
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

    // known hosts
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topPanels.add(panel);
    m_FileChooserPanelKnownHosts = new FileChooserPanel(
      System.getProperty("user.home")
	+ File.separator
	+ ".ssh"
	+ File.separator
	+ "known_hosts");
    m_FileChooserPanelKnownHosts.setPrefix("Known hosts file");
    panel.add(m_FileChooserPanelKnownHosts);

    // authentications
    m_PanelAuthentication = new JPanel(new BorderLayout());
    topPanels.add(m_PanelAuthentication);
    for (SSHAuthenticationType type: SSHAuthenticationType.values()) {
      authPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      switch (type) {
	case CREDENTIALS:
	  m_TextUser = new JTextField(8);
	  m_TextUser.setText(System.getProperty("user.name"));
	  label = new JLabel("User");
	  label.setLabelFor(m_TextUser);
	  authPanel.add(label);
	  authPanel.add(m_TextUser);
	  m_TextPassword = new JPasswordField(8);
	  label = new JLabel("Password");
	  label.setLabelFor(m_TextPassword);
	  authPanel.add(label);
	  authPanel.add(m_TextPassword);
	  break;
	case PUBLIC_KEY:
	  m_TextUser = new JTextField(8);
	  m_TextUser.setText(System.getProperty("user.name"));
	  label = new JLabel("User");
	  label.setLabelFor(m_TextUser);
	  authPanel.add(label);
	  authPanel.add(m_TextUser);
	  m_FileChooserPanelKey = new FileChooserPanel(
	    System.getProperty("user.home")
	      + File.separator
	      + ".ssh"
	      + File.separator
	      + "id_rsa");
	  m_FileChooserPanelKey.setPrefix("Private key");
	  m_FileChooserPanelKey.setPreferredSize(new Dimension(250, 20));
	  authPanel.add(m_FileChooserPanelKey);
	  m_TextKeyPassphrase = new JPasswordField(8);
	  label = new JLabel("Passphrase");
	  label.setLabelFor(m_TextKeyPassphrase);
	  authPanel.add(label);
	  authPanel.add(m_TextKeyPassphrase);
	  break;
	default:
	  authPanel.add(new JLabel("Unsupported"));
      }
      m_AuthenticationPanels.put(type, authPanel);
    }

    m_ComboBoxAuthenticationType = new JComboBox<>(SSHAuthenticationType.values());
    m_ComboBoxAuthenticationType.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_PanelAuthentication.removeAll();
	JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	panel.add(m_ComboBoxAuthenticationType, BorderLayout.NORTH);
	m_PanelAuthentication.add(panel, BorderLayout.WEST);
	m_PanelAuthentication.add(m_AuthenticationPanels.get(m_ComboBoxAuthenticationType.getSelectedItem()));
	m_PanelAuthentication.getParent().invalidate();
	m_PanelAuthentication.getParent().revalidate();
	m_PanelAuthentication.getParent().repaint();
      }
    });
    m_ComboBoxAuthenticationType.setSelectedIndex(0);

    // output
    m_ButtonClear = new JButton("Clear", GUIHelper.getIcon("new.gif"));
    m_ButtonClear.setMnemonic('l');
    m_ButtonClear.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	clear();
      }
    });
    m_ButtonCopy = new JButton("Copy", GUIHelper.getIcon("copy.gif"));
    m_ButtonCopy.setMnemonic('C');
    m_ButtonCopy.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (m_TextOutput.getSelectedText().length() > 0)
	  ClipboardHelper.copyToClipboard(m_TextOutput.getSelectedText());
	else if (m_TextOutput.getText().length() > 0)
	  ClipboardHelper.copyToClipboard(m_TextOutput.getText());
      }
    });
    m_TextOutput = new BaseTextPaneWithButtons();
    m_TextOutput.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    m_TextOutput.setTextFont(Fonts.getMonospacedFont());
    m_TextOutput.addToButtonsPanel(m_ButtonClear);
    m_TextOutput.addToButtonsPanel(m_ButtonCopy);
    add(m_TextOutput, BorderLayout.CENTER);
    
    // commands
    bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(bottomPanel, BorderLayout.SOUTH);
    
    m_TextCommand = new JTextField(40);
    m_TextCommand.setFont(Fonts.getMonospacedFont());
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
  protected void append(String msg, AttributeSet a) {
    m_TextOutput.getComponent().append(msg + (msg.endsWith("\n") ? "" : "\n"), a);
    m_TextOutput.setCaretPosition(m_TextOutput.getDocument().getLength());
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
    JSch	jsch;
    File	known;

    msg = "??";
    try {
      if (isConnected()) {
	msg = "disconnect";
	m_Session.disconnect();
	updateButtons();
      }
      else if (canConnect()) {
	msg = "connect";
	jsch = new JSch();
        jsch.setLogger(new Logger() {
          @Override
          public boolean isEnabled(int level) {
            return true;
          }
          @Override
          public void log(int level, String message) {
            switch (level) {
              case DEBUG:
                getLogger().fine(message);
                break;
              case INFO:
                getLogger().info(message);
                break;
              case WARN:
                getLogger().warning(message);
                break;
              case ERROR:
              case FATAL:
                getLogger().severe(message);
                break;
            }
          }
        });
	known = m_FileChooserPanelKnownHosts.getCurrent();
	if (known.exists())
	  jsch.setKnownHosts(known.getAbsolutePath());
	SSHAuthenticationType type = (SSHAuthenticationType) m_ComboBoxAuthenticationType.getSelectedItem();
	switch (type) {
	  case CREDENTIALS:
	    m_Session = jsch.getSession(m_TextUser.getText(), m_TextRemote.getText(), ((Number) m_PortModel.getValue()).intValue());
	    m_Session.setPassword(m_TextPassword.getText());
	    m_Session.setConfig("StrictHostKeyChecking", "no");
	    break;
	  case PUBLIC_KEY:
	    if (m_TextKeyPassphrase.getText().isEmpty())
	      jsch.addIdentity(m_FileChooserPanelKey.getCurrent().getAbsolutePath());
	    else
	      jsch.addIdentity(m_FileChooserPanelKey.getCurrent().getAbsolutePath(), m_TextKeyPassphrase.getText());
	    m_Session = jsch.getSession(m_TextUser.getText(), m_TextRemote.getText(), ((Number) m_PortModel.getValue()).intValue());
	    m_Session.setConfig("StrictHostKeyChecking", "no");
	    break;
	  default:
	    throw new IllegalStateException("Unhandled authentication type: " + type);
	}
	m_Session.connect();
	updateButtons();
	
	run = new Runnable() {
	  public void run() {
	    m_Channel = null;
	    InputStream instr;
	    try {
	      m_Channel = m_Session.openChannel("shell");
	      ((ChannelShell) m_Channel).setAgentForwarding(true);
	      ((ChannelShell) m_Channel).setPty(false);
	      m_Channel.connect();
	      instr = m_Channel.getInputStream();
	    }
	    catch (Exception e) {
	      ConsolePanel.getSingleton().append(this, "Exception while opening channel: ", e);
	      return;
	    }
	    try {
	      byte[] buff = new byte[1024];
	      int ret_read;
	      do {
		ret_read = instr.read(buff);
		if (ret_read > 0) {
		  append(new String(buff, 0, ret_read), m_AttributeSetRemote);
		  updateButtons();
		}
	      }
	      while (ret_read >= 0);
	    }
	    catch (IOException e) {
	      ConsolePanel.getSingleton().append(this, "Exception while reading socket: ", e);
	    }
	    updateButtons();
	  }
	};
	new Thread(run).start();
      }
    }
    catch (Exception e) {
      append("Failed to " + msg + ": " + e, m_AttributeSetError);
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
    if (!isConnected())
      return;

    // special command?
    if (cmd.equals("clear")) {
      m_TextCommand.setText("");
      m_CommandHistory.add(cmd);
      clear();
      updateButtons();
      return;
    }

    try {
      m_TextCommand.setText("");
      append(m_TextRemote.getText() + "> " + cmd, m_AttributeSetCmd);
      m_CommandHistory.add(cmd);
      DataOutputStream dos = new DataOutputStream(m_Channel.getOutputStream());
      dos.writeBytes(cmd + "\n");
      dos.flush();
      updateButtons();
    }
    catch (Exception e) {
      append("Failed to execute command " + cmd + ": " + e, m_AttributeSetError);
      System.err.println("Failed to execute command: " + cmd);
      e.printStackTrace();
    }
  }
  
  /**
   * Updates the status/text of the buttons.
   */
  protected void updateButtons() {
    if (isConnected())
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

  /**
   * Returns whether we're currently have a session running.
   *
   * @return		true if connected
   */
  public boolean isConnected() {
    return (m_Session != null) && m_Session.isConnected();
  }

  /**
   * Returns the logger in use.
   *
   * @return		the logger
   */
  public synchronized java.util.logging.Logger getLogger() {
    if (m_Logger == null) {
      m_Logger = LoggingHelper.getLogger(getClass());
      m_Logger.setLevel(Level.FINE);
    }
    return m_Logger;
  }

  /**
   * Returns whether logging is enabled.
   *
   * @return		true if at least {@link Level#INFO}
   */
  public boolean isLoggingEnabled() {
    return true;
  }

  /**
   * Clears the output.
   */
  public void clear() {
    m_TextOutput.setText("");
  }
}
