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
 * BufferHistoryPanel.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.core.io.FileUtils;
import adams.core.logging.LoggingLevel;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.TextFileChooser;
import adams.gui.core.AbstractNamedHistoryPanel.FrameDisplaySupporter;

import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Hashtable;

/**
 * A history panel that keeps track of named StringBuilder objects, e.g.,
 * containing experiments results.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BufferHistoryPanel
  extends AbstractNamedHistoryPanel<StringBuilder>
  implements FrameDisplaySupporter<StringBuilder> {

  /** for serialization. */
  private static final long serialVersionUID = 1704390033157269580L;

  /**
   * A specialized frame class for displaying a StringBuilder in a JTextArea.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class BufferFrame
    extends AbstractHistoryEntryFrame<StringBuilder> {

    /** for serialization. */
    private static final long serialVersionUID = 2552148773749071235L;

    /** the text area to display the StringBuilder in. */
    protected JTextArea m_TextArea;

    /**
     * Initializes the frame.
     *
     * @param owner	the owning history panel
     * @param name	the name of the history entry
     */
    public BufferFrame(AbstractNamedHistoryPanel<StringBuilder> owner, String name) {
      super(owner, name);
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      getContentPane().setLayout(new BorderLayout());

      m_TextArea = new JTextArea();
      m_TextArea.setFont(GUIHelper.getMonospacedFont());
      getContentPane().add(new BaseScrollPane(m_TextArea), BorderLayout.CENTER);
    }

    /**
     * Updates the entry, i.e., re-displays it.
     */
    @Override
    public void updateEntry() {
      StringBuilder	buffer;

      buffer = getEntryOwner().getEntry(getEntryName());
      if (buffer != null)
	m_TextArea.setText(buffer.toString());
      else
	m_TextArea.setText("");
    }
  }

  /** the frames that are being displayed. */
  protected Hashtable<String,BufferFrame> m_Frames;

  /** the text area to display the result in. */
  protected JTextArea m_TextArea;

  /** the file chooser for saving buffers. */
  protected transient TextFileChooser m_FileChooser;

  /** whether to position the caret at beginning instead of end. */
  protected boolean m_CaretAtStart;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Frames      = new Hashtable<String,BufferFrame>();
    m_TextArea    = null;
    m_CaretAtStart = false;
  }

  /**
   * Returns the file chooser and creates it if necessary.
   */
  protected TextFileChooser getFileChooser() {
    if (m_FileChooser == null)
      m_FileChooser = new TextFileChooser();
    
    return m_FileChooser;
  }
  
  /**
   * Sets the text area to display the results in.
   *
   * @param value	the text area to use
   */
  public void setTextArea(JTextArea value) {
    m_TextArea = value;
  }

  /**
   * Displays the specified entry.
   *
   * @param name	the name of the entry, can be null to clear display
   */
  @Override
  protected void updateEntry(String name) {
    m_TextArea.setText("");

    if (name != null) {
      // update text area
      if (hasEntry(name))
	m_TextArea.setText(getEntry(name).toString());

      if (m_CaretAtStart)
	m_TextArea.setCaretPosition(0);

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
  public BufferFrame newFrame(String name) {
    BufferFrame		result;

    result = new BufferFrame(this, name);
    m_Frames.put(name, result);

    return result;
  }

  /**
   * Returns the frame associated with the entry.
   *
   * @param name	the name of the entry to retrieve
   * @return		the frame or null if not found
   */
  public BufferFrame getFrame(String name) {
    return m_Frames.get(name);
  }

  /**
   * Displays the buffer in a separate frame. If a frame already exists for
   * this entry, then it will be displayed
   *
   * @param name	the name of the entry to display
   */
  public void showFrame(String name) {
    BufferFrame		frame;

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
  public StringBuilder removeEntry(String name) {
    StringBuilder	result;

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

    filename = name + "." + getFileChooser().getDefaultExtension();
    filename = FileUtils.createFilename(filename, "");
    getFileChooser().setSelectedFile(new File(filename));
    retVal   = getFileChooser().showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    msg = FileUtils.writeToFileMsg(
	getFileChooser().getSelectedFile().getAbsolutePath(),
	getEntry(name).toString(),
	false,
	getFileChooser().getEncoding());

    if (msg != null)
      ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Error saving text to '" + getFileChooser().getSelectedFile() + "':\n" + msg);
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
