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
 * ScriptingLogPanel.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.scripting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.Document;

import adams.core.Properties;
import adams.core.io.FileUtils;
import adams.core.io.FilenameProposer;
import adams.env.Environment;
import adams.env.ScriptingDialogDefinition;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.event.ScriptingEvent;
import adams.gui.event.ScriptingListener;

/**
 * A panel for outputting scripting commands globally.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScriptingLogPanel
  extends BasePanel
  implements ScriptingListener {

  /** for serialization. */
  private static final long serialVersionUID = 7316507289818697814L;

  /** the text pane for displaying the commands. */
  protected JTextPane m_TextLog;

  /** the button for clearing the log. */
  protected JButton m_ButtonClear;

  /** the button for closing the log. */
  protected JButton m_ButtonClose;

  /** the button for saving the log. */
  protected JButton m_ButtonSave;

  /** the file chooser for saving the log. */
  protected BaseFileChooser m_FileChooser;

  /** for proposing filenames for new flows. */
  protected FilenameProposer m_FilenameProposer;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = new BaseFileChooser();
    m_FileChooser.addChoosableFileFilter(ExtensionFileFilter.getLogFileFilter());
    m_FileChooser.setDefaultExtension(ExtensionFileFilter.getLogFileFilter().getExtensions()[0]);
    m_FilenameProposer = new FilenameProposer("new", m_FileChooser.getDefaultExtension());
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    m_TextLog = new JTextPane() {
      private static final long serialVersionUID = -8867551408542402385L;
      @Override
      public void setSize(Dimension d) {
	if (d.width < getGraphicsConfiguration().getBounds().width)
	  d.width = getGraphicsConfiguration().getBounds().width;
	super.setSize(d);
      }
      @Override
      public boolean getScrollableTracksViewportWidth() {
	return false;
      }
    };
    m_TextLog.setDocument(createDocument());
    add(new BaseScrollPane(m_TextLog), BorderLayout.CENTER);

    // buttons
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    add(panel, BorderLayout.SOUTH);

    m_ButtonClear = new JButton("Clear", GUIHelper.getIcon("new.gif"));
    m_ButtonClear.setMnemonic('e');
    m_ButtonClear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	clear();
      }
    });
    panel.add(m_ButtonClear);

    m_ButtonSave = new JButton("Save...", GUIHelper.getIcon("save.gif"));
    m_ButtonSave.setMnemonic('s');
    m_ButtonSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	save();
      }
    });
    panel.add(m_ButtonSave);

    m_ButtonClose = new JButton("Close", GUIHelper.getIcon("exit.png"));
    m_ButtonClose.setMnemonic('l');
    m_ButtonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	close();
      }
    });
    panel.add(m_ButtonClose);
  }

  /**
   * finishes the initialization, by setting size/location and running
   * startup scripts.
   */
  @Override
  protected void finishInit() {
    ScriptingLogger.getSingleton().addScriptingListener(this);
  }

  /**
   * Creates a new document for the dialog, with syntax highlighting support.
   *
   * @return		the new document
   */
  protected Document createDocument() {
    Document	result;
    Properties	props;

    props  = Environment.getInstance().read(ScriptingDialogDefinition.KEY);
    result = new SyntaxDocument(props);

    return result;
  }

  /**
   * Clears the log.
   */
  protected void clear() {
    synchronized(m_TextLog) {
      m_TextLog.setText("");
    }
  }

  /**
   * Allows the user to save the log to a file.
   */
  protected void save() {
    int		retVal;

    m_FileChooser.setSelectedFile(m_FilenameProposer.propose(null));
    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    if (!FileUtils.writeToFile(m_FileChooser.getSelectedFile().getAbsolutePath(), m_TextLog.getText(), false)) {
      GUIHelper.showErrorMessage(
	  this, "Error saving log to file '" + m_FileChooser.getSelectedFile() + "'!");
    }
  }

  /**
   * Closes the dialog/frame.
   */
  protected void close() {
    ScriptingLogger.getSingleton().removeScriptingListener(ScriptingLogPanel.this);
    if (getParentDialog() != null) {
      getParentDialog().setVisible(false);
    }
    else if (getParentFrame() != null) {
      getParentFrame().setVisible(false);
      getParentFrame().dispose();
    }
  }

  /**
   * Gets called when a scripting command gets executed.
   *
   * @param e		the event
   */
  public void scriptingCommandExecuted(ScriptingEvent e) {
    String	line;
    Document	doc;

    line = "";
    if (e.getCmd().getBasePanel() != null)
      line += e.getCmd().getBasePanel().getClass().getName().replaceAll(".*\\.", "");
    else
      line += "[unknown]";
    line += " (" + (e.getSuccess() ? "successful" : "failed") + ")";
    line += ": " + e.getCmd().getCommand();
    if (e.hasError())
      line += "\n   " + e.getError();
    line += "\n";

    synchronized(m_TextLog) {
      doc = m_TextLog.getDocument();
      synchronized(doc) {
	try {
	  doc.insertString(doc.getLength(), line, null);
	}
	catch (Exception ex) {
	  ex.printStackTrace();
	}
      }
    }
  }
}
