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
 * HelpHistoryPanel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.help;

import adams.core.io.FileUtils;
import adams.core.logging.LoggingLevel;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.AbstractNamedHistoryPanel;
import adams.gui.core.AbstractNamedHistoryPanel.FrameDisplaySupporter;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.Fonts;

import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Hashtable;

/**
 * A history panel that keeps track of named help containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HelpHistoryPanel
  extends AbstractNamedHistoryPanel<HelpContainer>
  implements FrameDisplaySupporter<HelpContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 1704390033157269580L;

  /**
   * A specialized frame class for displaying a StringBuilder in a JTextArea.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class SingleHelpFrame
    extends AbstractHistoryEntryFrame<HelpContainer> {

    /** for serialization. */
    private static final long serialVersionUID = 2552148773749071235L;

    /** the text area to display the StringBuilder in. */
    protected JEditorPane m_Text;

    /**
     * Initializes the frame.
     *
     * @param owner	the owning history panel
     * @param name	the name of the history entry
     */
    public SingleHelpFrame(AbstractNamedHistoryPanel<HelpContainer> owner, String name) {
      super(owner, name);
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      getContentPane().setLayout(new BorderLayout());

      m_Text = new JEditorPane();
      m_Text.setFont(Fonts.getMonospacedFont());
      getContentPane().add(new BaseScrollPane(m_Text), BorderLayout.CENTER);
    }

    /**
     * Updates the entry, i.e., re-displays it.
     */
    @Override
    public void updateEntry() {
      HelpContainer	cont;

      cont = getEntryOwner().getEntry(getEntryName());
      m_Text.setText("");
      if (cont != null) {
	m_Text.setContentType(cont.isHtml() ? "text/html" : "text/plain");
	m_Text.setText(cont.getHelp());
      }
    }
  }

  /** the frames that are being displayed. */
  protected Hashtable<String,SingleHelpFrame> m_Frames;

  /** the editor pane to display the help in. */
  protected JEditorPane m_Text;

  /** the file chooser for saving buffers. */
  protected transient BaseFileChooser m_FileChooser;

  /** whether to position the caret at beginning instead of end. */
  protected boolean m_CaretAtStart;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Frames       = new Hashtable<>();
    m_Text         = null;
    m_CaretAtStart = false;
  }

  /**
   * Returns the file chooser and creates it if necessary.
   */
  protected BaseFileChooser getFileChooser() {
    if (m_FileChooser == null) {
      m_FileChooser = new BaseFileChooser();
      m_FileChooser.addChoosableFileFilter(new ExtensionFileFilter("Text file", "txt"));
      m_FileChooser.addChoosableFileFilter(new ExtensionFileFilter("HTML file", "html"));
      m_FileChooser.setAcceptAllFileFilterUsed(true);
      m_FileChooser.setAutoAppendExtension(true);
    }
    
    return m_FileChooser;
  }
  
  /**
   * Sets the text area to display the results in.
   *
   * @param value	the text area to use
   */
  public void setText(JEditorPane value) {
    m_Text = value;
  }

  /**
   * Displays the specified entry.
   *
   * @param name	the name of the entry, can be null to clear display
   */
  @Override
  protected void updateEntry(String name) {
    HelpContainer	cont;

    m_Text.setContentType("text/html");
    m_Text.setText("<b>No help available/selected</b>");

    if (name != null) {
      // update text area
      if (hasEntry(name)) {
        cont = getEntry(name);
	m_Text.setContentType(cont.isHtml() ? "text/html" : "text/plain");
	m_Text.setText(cont.getHelp());
      }

      if (m_CaretAtStart)
	m_Text.setCaretPosition(0);

      // don't create new frame, only show it if it exists already
      if (hasFrame(name))
	showFrame(name);
    }
  }

  /**
   * Checks whether a frame is available fro this entry.
   *
   * @param name	the name of the entry to check
   * @return		true if a frame is already available
   */
  public boolean hasFrame(String name) {
    return m_Frames.containsKey(name);
  }

  /**
   * Creates a new frame for the entry.
   *
   * @param name	the name of the entry to create a frame for
   * @return		the frame
   */
  public SingleHelpFrame newFrame(String name) {
    SingleHelpFrame result;

    result = new SingleHelpFrame(this, name);
    m_Frames.put(name, result);

    return result;
  }

  /**
   * Returns the frame associated with the entry.
   *
   * @param name	the name of the entry to retrieve
   * @return		the frame or null if not found
   */
  public SingleHelpFrame getFrame(String name) {
    return m_Frames.get(name);
  }

  /**
   * Displays the buffer in a separate frame. If a frame already exists for
   * this entry, then it will be displayed
   *
   * @param name	the name of the entry to display
   */
  public void showFrame(String name) {
    SingleHelpFrame frame;

    if (!hasFrame(name)) {
      frame = newFrame(name);
      frame.setLocationRelativeTo(this);
    }
    else {
      frame = getFrame(name);
    }

    frame.setVisible(true);
    frame.toFront();
  }

  /**
   * Removes the frame from the list. This method should be called when
   * the frame gets closed.
   *
   * @param name	the name of the entry this frame is associated with
   */
  public void removeFrame(String name) {
    m_Frames.remove(name);
  }

  /**
   * Removes the specified entry.
   *
   * @param name	the name of the entry
   * @return		the entry that was stored under this name or null if
   * 			no entry was stored with this name
   */
  @Override
  public HelpContainer removeEntry(String name) {
    HelpContainer	result;

    result = super.removeEntry(name);
    if (hasFrame(name))
      getFrame(name).setVisible(false);

    return result;
  }

  /**
   * Generates the right-click menu for the JList.
   *
   * @param e		the event that triggered the popup
   * @return		the generated menu
   */
  @Override
  protected BasePopupMenu createPopup(MouseEvent e) {
    BasePopupMenu	result;
    JMenuItem		menuitem;
    final int[]		indices;

    result  = super.createPopup(e);
    indices = getSelectedIndices();

    result.addSeparator();

    // save buffer
    menuitem = new JMenuItem("Save...");
    menuitem.setEnabled(indices.length == 1);
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	saveEntry(getEntryName(indices[0]));
      }
    });
    result.add(menuitem);

    return result;
  }

  /**
   * Saves the entry to a file.
   *
   * @param name	the entry to save
   */
  protected void saveEntry(String name) {
    int			retVal;
    String		filename;
    String 		msg;

    filename = name;
    filename = FileUtils.createFilename(filename, "");
    getFileChooser().setSelectedFile(new File(filename));
    retVal   = getFileChooser().showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    msg = FileUtils.writeToFileMsg(
	getFileChooser().getSelectedFile().getAbsolutePath(),
	getEntry(name).getHelp(),
	false,
	null);

    if (msg != null)
      ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Error saving help to '" + getFileChooser().getSelectedFile() + "':\n" + msg);
  }

  /**
   * Sets whether to position the caret at the start or at the end (default).
   *
   * @param value	if true then the caret will be positioned at start
   */
  public void setCaretAtStart(boolean value) {
    m_CaretAtStart = value;
    if (getSelectedEntry() != null)
      updateEntry(getSelectedEntry());
  }

  /**
   * Returns whether the caret is positioned at the start instead of the end.
   *
   * @return		true if caret positioned at start
   */
  public boolean isCaretAtStart() {
    return m_CaretAtStart;
  }
}
