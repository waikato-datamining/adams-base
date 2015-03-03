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
 * ConsolePanel.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import adams.gui.event.ConsolePanelEvent;
import adams.gui.event.ConsolePanelListener;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;

/**
 * Global panel for capturing output via PrintObject instances.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConsolePanel
  extends BasePanel
  implements MenuBarProvider, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -2339480199106797838L;

  /**
   * The type of output to handle.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum OutputType {
    /** informational messages. */
    INFO,
    /** errors and warnings. */
    ERROR,
    /** debugging information. */
    DEBUG
  }

  /**
   * Represents a single panel for a specific type of output.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class OutputPanel
    extends BasePanel {

    /** for serialization. */
    private static final long serialVersionUID = 8547336176163250862L;

    /** the title of the panel. */
    protected String m_Title;

    /** whether output is enabled. */
    protected boolean m_OutputEnabled;

    /** the text area for the output. */
    protected TextEditorPanel m_TextArea;

    /** the button for enabling/disabling the output. */
    protected JButton m_ButtonEnabledDisable;

    /** the spinner for the maximum number of lines. */
    protected JSpinner m_SpinnerMaxLines;

    /** the button for clearing the output. */
    protected JButton m_ButtonClear;

    /**
     * Initializes the panel.
     *
     * @param title	the title of the panel
     */
    public OutputPanel(String title) {
      this(title, Color.BLACK);
    }

    /**
     * Initializes the panel.
     *
     * @param title	the title of the panel
     * @param color	the font color
     */
    public OutputPanel(String title, Color color) {
      super();

      m_Title         = title;
      m_OutputEnabled = true;
      m_TextArea.getTextArea().setForeground(color);
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      JPanel			panel;
      SpinnerNumberModel	model;

      super.initGUI();

      setLayout(new BorderLayout());

      m_TextArea = new TextEditorPanel();
      add(m_TextArea, BorderLayout.CENTER);

      panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
      panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
      add(panel, BorderLayout.SOUTH);

      m_ButtonEnabledDisable = new JButton("Disable");
      m_ButtonEnabledDisable.setMnemonic('a');
      m_ButtonEnabledDisable.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          m_OutputEnabled = !m_OutputEnabled;
          if (m_OutputEnabled)
            m_ButtonEnabledDisable.setText("Disable");
          else
            m_ButtonEnabledDisable.setText("Enable");
        }
      });
      panel.add(m_ButtonEnabledDisable);

      m_SpinnerMaxLines = new JSpinner();
      m_SpinnerMaxLines.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          trimOutput();
        }
      });
      model = (SpinnerNumberModel) m_SpinnerMaxLines.getModel();
      model.setMinimum(1);
      model.setMaximum(10000000);
      model.setStepSize(10000);
      model.setValue(100000);
      panel.add(m_SpinnerMaxLines);

      m_ButtonClear = new JButton("Clear", GUIHelper.getIcon("new.gif"));
      m_ButtonClear.setMnemonic('l');
      m_ButtonClear.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          m_TextArea.clear();
        }
      });
      panel.add(m_ButtonClear);
    }

    /**
     * Returns the title of the panel.
     *
     * @return		the title of the panel
     */
    public String getTitle() {
      return m_Title;
    }

    /**
     * Sets whether the output is enabled.
     *
     * @param value	if true the output will get enabled
     */
    public void setOutputEnabled(boolean value) {
      m_OutputEnabled = value;
    }

    /**
     * Clears the text.
     */
    public void clear() {
      m_TextArea.clear();
    }

    /**
     * Copies the text to the clipboard.
     */
    public void copy() {
      m_TextArea.copy();
    }

    /**
     * Saves the current content to a file.
     */
    public void saveAs() {
      m_TextArea.saveAs();
    }

    /**
     * For finding a string.
     */
    public void find() {
      m_TextArea.find();
    }

    /**
     * Finds the next occurrence.
     */
    public void findNext() {
      m_TextArea.findNext();
    }

    /**
     * Sets the line wrap flag.
     *
     * @param value	if true line wrap is enabled
     */
    public void setLineWrap(boolean value) {
      m_TextArea.setLineWrap(value);
    }

    /**
     * Returns the current line wrap setting.
     *
     * @return		true if line wrap is enabled
     */
    public boolean getLineWrap() {
      return m_TextArea.getLineWrap();
    }

    /**
     * Trims the output of the text area if necessary.
     */
    protected void trimOutput() {
      StringBuilder	buf;
      int		index;

      synchronized(m_TextArea) {
	if (m_TextArea.getTextArea().getLineCount() > ((Number) m_SpinnerMaxLines.getValue()).intValue()) {
	  buf   = new StringBuilder(m_TextArea.getContent());
	  index = buf.indexOf("\n", (int) (buf.length() * 0.2));
	  if (index == -1)
	    buf = new StringBuilder();
	  else
	    buf.delete(0, index);
	  m_TextArea.setContent(buf.toString());
	}
      }
    }

    /**
     * Appends the given string.
     *
     * @param msg	the message to append
     */
    public void append(final String msg) {
      if (!m_OutputEnabled)
	return;

      synchronized(m_TextArea) {
	m_TextArea.append(msg);
	trimOutput();
	m_TextArea.setCaretPosition(m_TextArea.getContent().length());
      }
    }
    
    /**
     * Returns the content of the text area.
     * 
     * @return		the currently displayed text
     */
    public String getContent() {
      return m_TextArea.getContent();
    }
  }

  /**
   * The type of panel.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum PanelType {
    /** contains all the output. */
    ALL,
    /** only the debug output. */
    DEBUG,
    /** only output on stdout. */
    STDOUT,
    /** only output on stderr. */
    STDERR
  }
  
  /**
   * For letting {@link PrintStream} objects print to the {@link ConsolePanel}.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ConsolePanelOutputStream
    extends OutputStream {
    
    /** the output type to use. */
    protected OutputType m_OutputType;
    
    /** the current buffer. */
    protected StringBuilder m_Buffer;
    
    /**
     * Initializes the output stream.
     * 
     * @param outputType	the output type to use
     */
    public ConsolePanelOutputStream(OutputType outputType) {
      super();
      
      m_OutputType = outputType;
      m_Buffer     = new StringBuilder();
    }
    
    /**
     * Writes the specified byte to this output stream. The general
     * contract for <code>write</code> is that one byte is written
     * to the output stream. The byte to be written is the eight
     * low-order bits of the argument <code>b</code>. The 24
     * high-order bits of <code>b</code> are ignored.
     * <p>
     * Subclasses of <code>OutputStream</code> must provide an
     * implementation for this method.
     *
     * @param      b   the <code>byte</code>.
     */
    @Override
    public void write(int b) throws IOException {
      char	c;
      
      c = (char) b;
      m_Buffer.append(c);
      
      if (c == '\n') {
	getSingleton().append(m_OutputType, m_Buffer.toString());
	m_Buffer.delete(0, m_Buffer.length());
      }
    }
  }
  
  /** the singleton. */
  protected static ConsolePanel m_Singleton;

  /** the tabbed pane for the various outputs. */
  protected BaseTabbedPane m_TabbedPane;

  /** the ALL panel. */
  protected OutputPanel m_PanelAll;

  /** the info panel. */
  protected OutputPanel m_PanelInfo;

  /** the error panel. */
  protected OutputPanel m_PanelError;

  /** the debug panel. */
  protected OutputPanel m_PanelDebug;

  /** the menu bar. */
  protected JMenuBar m_MenuBar;

  /** the save as item. */
  protected JMenuItem m_MenuItemSaveAs;

  /** the close item. */
  protected JMenuItem m_MenuItemExit;

  /** the copy item. */
  protected JMenuItem m_MenuItemCopy;

  /** the clear item. */
  protected JMenuItem m_MenuItemClear;

  /** the clear all item. */
  protected JMenuItem m_MenuItemClearAll;

  /** the line wrap item. */
  protected JMenuItem m_MenuItemLineWrap;

  /** the find item. */
  protected JMenuItem m_MenuItemFind;

  /** the find next item. */
  protected JMenuItem m_MenuItemFindNext;

  /** the listeners. */
  protected HashSet<ConsolePanelListener> m_Listeners;
  
  /**
   * Initializes the panel.
   */
  protected ConsolePanel() {
    super();
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Listeners = new HashSet<ConsolePanelListener>();
  }

  /**
   * Initializes the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new BaseTabbedPane();
    add(m_TabbedPane, BorderLayout.CENTER);

    m_PanelAll = new OutputPanel("All");
    m_TabbedPane.addTab(m_PanelAll.getTitle(), m_PanelAll);

    m_PanelInfo = new OutputPanel("Info");
    m_TabbedPane.addTab(m_PanelInfo.getTitle(), m_PanelInfo);

    m_PanelError = new OutputPanel("Error", Color.RED.darker());
    m_TabbedPane.addTab(m_PanelError.getTitle(), m_PanelError);

    m_PanelDebug = new OutputPanel("Debug", Color.BLUE.darker());
    m_TabbedPane.addTab(m_PanelDebug.getTitle(), m_PanelDebug);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenuItem	menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');

      // File/Clear
      menuitem = new JMenuItem("Clear");
      menu.add(menuitem);
      menuitem.setMnemonic('l');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
      menuitem.setIcon(GUIHelper.getIcon("new.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  if (getCurrentPanel() != null)
	    getCurrentPanel().clear();
	}
      });
      m_MenuItemClear = menuitem;

      // File/Clear all
      menuitem = new JMenuItem("Clear all");
      menu.add(menuitem);
      menuitem.setMnemonic('a');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed N"));
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  for (int i = 0; i < m_TabbedPane.getTabCount(); i++)
	    ((OutputPanel) m_TabbedPane.getComponent(i)).clear();
	}
      });
      m_MenuItemClearAll = menuitem;

      // File/Save as...
      menuitem = new JMenuItem("Save as...");
      menu.add(menuitem);
      menuitem.setMnemonic('S');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed S"));
      menuitem.setIcon(GUIHelper.getIcon("save.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  if (getCurrentPanel() != null)
	    getCurrentPanel().saveAs();
	}
      });
      m_MenuItemSaveAs = menuitem;

      // File/Send to
      menu.addSeparator();
      if (SendToActionUtils.addSendToSubmenu(this, menu))
	menu.addSeparator();

      // File/Exit
      menuitem = new JMenuItem("Close");
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  closeParent();
	}
      });
      m_MenuItemExit = menuitem;

      // Edit
      menu = new JMenu("Edit");
      result.add(menu);
      menu.setMnemonic('E');

      // Edit/Copy
      menuitem = new JMenuItem("Copy");
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed C"));
      menuitem.setIcon(GUIHelper.getIcon("copy.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  if (getCurrentPanel() != null)
	    getCurrentPanel().copy();
	}
      });
      m_MenuItemCopy = menuitem;

      // Edit/Line wrap
      menuitem = new JMenuItem("Line wrap");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('L');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed L"));
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  if (getCurrentPanel() != null)
	    getCurrentPanel().setLineWrap(!getCurrentPanel().getLineWrap());
	}
      });
      m_MenuItemFind = menuitem;

      // Edit/Find
      menuitem = new JMenuItem("Find");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('F');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed F"));
      menuitem.setIcon(GUIHelper.getIcon("find.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  if (getCurrentPanel() != null)
	    getCurrentPanel().find();
	}
      });
      m_MenuItemFind = menuitem;

      // Edit/Find
      menuitem = new JMenuItem("Find next");
      menu.add(menuitem);
      menuitem.setMnemonic('N');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed F"));
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  if (getCurrentPanel() != null)
	    getCurrentPanel().findNext();
	}
      });
      m_MenuItemFindNext = menuitem;

      m_MenuBar = result;
    }

    return m_MenuBar;
  }

  /**
   * Returns the current output panel.
   *
   * @return		the panel, null if none available
   */
  public OutputPanel getCurrentPanel() {
    OutputPanel	result;

    result = null;
    if (m_TabbedPane.getSelectedComponent() instanceof OutputPanel)
      result = (OutputPanel) m_TabbedPane.getSelectedComponent();

    return result;
  }

  /**
   * Returns the specified output panel.
   *
   * @param type	the panel to retrieve
   * @return		the panel
   */
  public OutputPanel getPanel(PanelType type) {
    switch (type) {
      case ALL:
	return m_PanelAll;
      case DEBUG:
	return m_PanelDebug;
      case STDERR:
	return m_PanelError;
      case STDOUT:
	return m_PanelInfo;
      default:
	throw new IllegalArgumentException("Unhandled panel type: " + type);
    }
  }

  /**
   * Adds the listener.
   * 
   * @param l		the listener to add
   */
  public void addListener(ConsolePanelListener l) {
    synchronized(m_Listeners) {
      m_Listeners.add(l);
    }
  }

  /**
   * Removes the listener.
   * 
   * @param l		the listener to remove
   */
  public void removeListener(ConsolePanelListener l) {
    synchronized(m_Listeners) {
      m_Listeners.remove(l);
    }
  }
  
  /**
   * Notifies the listeners.
   * 
   * @param outputType	the type of output the string represents
   * @param msg		the message to append
   */
  protected void notifyListeners(OutputType outputType, String msg) {
    ConsolePanelEvent	e;
    
    if (m_Listeners.size() == 0)
      return;
    
    e = new ConsolePanelEvent(this, outputType, msg);
    try {
      synchronized(m_Listeners) {
	for (ConsolePanelListener l: m_Listeners)
	  l.consolePanelMessageReceived(e);
      }
    }
    catch (Throwable t) {
      // ignored
    }
  }
  
  /**
   * Appends the given string to the according panels.
   *
   * @param outputType	the type of output the string represents
   * @param msg		the message to append
   */
  public void append(OutputType outputType, String msg) {
    switch (outputType) {
      case INFO:
	m_PanelAll.append(msg);
	m_PanelInfo.append(msg);
	break;
      case ERROR:
	m_PanelAll.append(msg);
	m_PanelError.append(msg);
	break;
      case DEBUG:
	m_PanelAll.append(msg);
	m_PanelDebug.append(msg);
	break;
      default:
	throw new IllegalArgumentException("Unhandled output type: " + outputType);
    }
    
    notifyListeners(outputType, msg);
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  @Override
  public Class[] getSendToClasses() {
    return new Class[]{String.class, JTextComponent.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the requested classes
   * @return		true if an object is available for sending
   */
  @Override
  public boolean hasSendToItem(Class[] cls) {
    return    (SendToActionUtils.isAvailable(new Class[]{String.class, JTextComponent.class}, cls))
           && (getCurrentPanel().getContent().length() > 0);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the requested classes
   * @return		the item to send
   */
  @Override
  public Object getSendToItem(Class[] cls) {
    Object	result;

    result = null;

    if ((SendToActionUtils.isAvailable(String.class, cls))) {
      result = getCurrentPanel().getContent();
      if (((String) result).length() == 0)
	result = null;
    }
    else if (SendToActionUtils.isAvailable(JTextComponent.class, cls)) {
      if (getCurrentPanel().getContent().length() > 0)
	result = getCurrentPanel();
    }

    return result;
  }

  /**
   * Returns the singleton instance.
   *
   * @return		the singleton
   */
  public static synchronized ConsolePanel getSingleton() {
    if (m_Singleton == null)
      m_Singleton = new ConsolePanel();

    return m_Singleton;
  }
}
