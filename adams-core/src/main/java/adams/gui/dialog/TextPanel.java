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
 * TextPanel.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import adams.gui.chooser.FontChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.PopupMenuCustomizer;
import adams.gui.core.RecentFilesWithEncodingHandler;
import adams.gui.core.TextEditorPanel;
import adams.gui.core.TitleGenerator;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;

/**
 * A simple text editor panel. By default, files cannot be loaded. This has
 * to be allowed explicitly via <code>setCanOpenFiles(boolean)</code>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see #setCanOpenFiles(boolean)
 */
public class TextPanel
  extends BasePanel
  implements MenuBarProvider, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -5335911070392516986L;

  /** the file to store the recent directories. */
  public final static String SESSION_FILE = "TextPanelSession.props";

  /** the menu bar. */
  protected JMenuBar m_MenuBar;

  /** for displaying the text. */
  protected TextEditorPanel m_TextPanel;

  /** the panel with optional info text. */
  protected JPanel m_PanelInfo;
  
  /** the label with the option info text. */
  protected JLabel m_LabelInfo;
  
  /** the new menu item. */
  protected JMenuItem m_MenuItemFileNew;
  
  /** the open menu item. */
  protected JMenuItem m_MenuItemFileOpen;

  /** the "load recent" submenu. */
  protected JMenu m_MenuFileLoadRecent;

  /** the save menu item. */
  protected JMenuItem m_MenuItemFileSave;

  /** the save as menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the undo menu item. */
  protected JMenuItem m_MenuItemEditUndo;

  /** the redo menu item. */
  protected JMenuItem m_MenuItemEditRedo;

  /** the cut menu item. */
  protected JMenuItem m_MenuItemEditCut;

  /** the copy menu item. */
  protected JMenuItem m_MenuItemEditCopy;

  /** the paste menu item. */
  protected JMenuItem m_MenuItemEditPaste;

  /** the select all menu item. */
  protected JMenuItem m_MenuItemEditSelectAll;

  /** the find menu item. */
  protected JMenuItem m_MenuItemEditFind;

  /** the find next menu item. */
  protected JMenuItem m_MenuItemEditFindNext;

  /** the tab size menu item. */
  protected JMenuItem m_MenuItemViewTabSize;

  /** the select font menu item. */
  protected JMenuItem m_MenuItemViewFont;

  /** the line wrap menu item. */
  protected JCheckBoxMenuItem m_MenuItemViewLineWrap;

  /** the recent files handler. */
  protected RecentFilesWithEncodingHandler<JMenu> m_RecentFilesHandler;

  /** whether the editor is allowed to open files as well. */
  protected boolean m_CanOpenFiles;

  /** for generating the title. */
  protected TitleGenerator m_TitleGenerator;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_TitleGenerator     = new TitleGenerator("Text editor", true);
    m_RecentFilesHandler = null;
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelInfo.setBackground(Color.WHITE);
    m_LabelInfo = new JLabel();
    m_PanelInfo.add(m_LabelInfo);
    add(m_PanelInfo, BorderLayout.NORTH);
    
    m_TextPanel = new TextEditorPanel();
    m_TextPanel.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	update();
      }
    });
    add(m_TextPanel, BorderLayout.CENTER);

    setSize(600, 800);
    setInfoText(null);
  }

  /**
   * Sets whether the editor can open files.
   *
   * @param value 	if true then the editor can open files as well
   */
  public void setCanOpenFiles(boolean value) {
    m_CanOpenFiles = value;
  }

  /**
   * Returns whether the editor can open files.
   *
   * @return		true if the editor can open files
   */
  public boolean getCanOpenFiles() {
    return m_CanOpenFiles;
  }

  /**
   * Sets the base title to use for the title generator.
   * 
   * @param value	the title to use
   * @see		#m_TitleGenerator
   */
  public void setTitle(String value) {
    m_TitleGenerator.setTitle(value);
    update();
  }
  
  /**
   * Returns the base title in use by the title generator.
   * 
   * @return		the title in use
   * @see		#m_TitleGenerator
   */
  public String getTitle() {
    return m_TitleGenerator.getTitle();
  }
  
  /**
   * Returns the title generator in use.
   * 
   * @return		the generator
   */
  public TitleGenerator getTitleGenerator() {
    return m_TitleGenerator;
  }

  /**
   * Sets the customizer to use.
   * 
   * @param value	the customizer, null to unset
   */
  public void setPopupMenuCustomizer(PopupMenuCustomizer<TextEditorPanel> value) {
    m_TextPanel.setPopupMenuCustomizer(value);
  }
  
  /**
   * Returns the customizer in use.
   * 
   * @return		the customizer, null if none set
   */
  public PopupMenuCustomizer<TextEditorPanel> getPopupMenuCustomizer() {
    return m_TextPanel.getPopupMenuCustomizer();
  }
  
  /**
   * Sets the modified state.
   *
   * @param value 	if true then the content is flagged as modified
   */
  public void setModified(boolean value) {
    m_TextPanel.setModified(value);
  }

  /**
   * Returns whether the content has been modified.
   *
   * @return		true if the content was modified
   */
  public boolean isModified() {
    return m_TextPanel.isModified();
  }

  /**
   * Sets the content to display. Resets the modified state.
   *
   * @param value	the text
   */
  public void setContent(String value) {
    m_TextPanel.setContent(value);
  }

  /**
   * Returns the content to display.
   *
   * @return		the text
   */
  public String getContent() {
    return m_TextPanel.getContent();
  }

  /**
   * Sets the info text to display.
   * 
   * @param value	the text, null or empty string to hide
   */
  public void setInfoText(String value) {
    if (value == null)
      value = "";
    else
      value = value.trim();
    m_LabelInfo.setText(value);
    m_PanelInfo.setVisible(value.length() > 0);
  }
  
  /**
   * Returns the current info text.
   * 
   * @return		the text
   */
  public String getInfoText() {
    return m_LabelInfo.getText();
  }
  
  /**
   * Sets whether the text area is editable or not.
   *
   * @param value	if true then the text will be editable
   */
  public void setEditable(boolean value) {
    m_TextPanel.setEditable(value);
  }

  /**
   * Returns whether the text area is editable or not.
   *
   * @return		true if the text is editable
   */
  public boolean isEditable() {
    return m_TextPanel.isEditable();
  }

  /**
   * Sets the font of the text area.
   *
   * @param value	the font to use
   */
  public void setTextFont(Font value) {
    m_TextPanel.setTextFont(value);
  }

  /**
   * Returns the font currently in use by the text area.
   *
   * @return		the font in use
   */
  public Font getTextFont() {
    return m_TextPanel.getTextFont();
  }

  /**
   * Sets the tab size, i.e., the number of maximum width characters.
   *
   * @param value	the number of maximum width chars
   */
  public void setTabSize(int value) {
    m_TextPanel.setTabSize(value);
  }

  /**
   * Returns the tab size, i.e., the number of maximum width characters.
   *
   * @return		the number of maximum width chars
   */
  public int getTabSize() {
    return m_TextPanel.getTabSize();
  }

  /**
   * Enables/disables line wrap.
   *
   * @param value	if true line wrap gets enabled
   */
  public void setLineWrap(boolean value) {
    m_TextPanel.setLineWrap(value);
  }

  /**
   * Returns whether line wrap is enabled.
   *
   * @return		true if line wrap enabled
   */
  public boolean getLineWrap() {
    return m_TextPanel.getLineWrap();
  }

  /**
   * Sets the position of the cursor.
   *
   * @param value	the position
   */
  public void setCaretPosition(int value) {
    m_TextPanel.setCaretPosition(value);
  }

  /**
   * Returns the current position of the cursor.
   *
   * @return		the cursor position
   */
  public int getCaretPosition() {
    return m_TextPanel.getCaretPosition();
  }

  /**
   * Adds the current file/encoding as recent item.
   */
  protected void addRecentItem() {
    if (m_RecentFilesHandler != null)
      m_RecentFilesHandler.addRecentItem(
	  m_TextPanel.getCurrentFile().getAbsolutePath() + "\t" + m_TextPanel.getCurrentEncoding());
  }
  
  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenu	submenu;
    JMenuItem	menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      menu.setMnemonic('F');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });
      result.add(menu);
      
      // File/New
      menuitem = new JMenuItem("New", GUIHelper.getIcon("new.gif"));
      menuitem.setMnemonic('N');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_TextPanel.setContent("");
	}
      });
      menu.add(menuitem);
      m_MenuItemFileNew = menuitem;

      if (m_CanOpenFiles) {
	// File/Open
	menuitem = new JMenuItem("Open...", GUIHelper.getIcon("open.gif"));
	menuitem.setMnemonic('O');
	menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
	menuitem.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    if (m_TextPanel.open())
	      addRecentItem();
	  }
	});
	menu.add(menuitem);
	m_MenuItemFileOpen = menuitem;

	// File/Recent files
	submenu = new JMenu("Open recent");
	menu.add(submenu);
	m_RecentFilesHandler = new RecentFilesWithEncodingHandler<JMenu>(
	    SESSION_FILE, 5, submenu);
	m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,String>() {
	  @Override
	  public void recentItemAdded(RecentItemEvent<JMenu,String> e) {
	    // ignored
	  }
	  @Override
	  public void recentItemSelected(RecentItemEvent<JMenu,String> e) {
	    open(RecentFilesWithEncodingHandler.getFile(e.getItem()), RecentFilesWithEncodingHandler.getEncoding(e.getItem()));
	  }
	});
	m_MenuFileLoadRecent = submenu;

	// File/Save
	menuitem = new JMenuItem("Save", GUIHelper.getIcon("save.gif"));
	menuitem.setMnemonic('a');
	menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
	menuitem.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    m_TextPanel.save();
	  }
	});
	menu.add(menuitem);
	m_MenuItemFileSave = menuitem;
      }

      // File/Save as
      menuitem = new JMenuItem("Save as...");
      if (!m_CanOpenFiles)
	menuitem.setIcon(GUIHelper.getIcon("save.gif"));
      menuitem.setMnemonic('a');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed S"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_TextPanel.saveAs();
	}
      });
      menu.add(menuitem);
      m_MenuItemFileSaveAs = menuitem;

      // File/Send to
      menu.addSeparator();
      if (SendToActionUtils.addSendToSubmenu(this, menu))
	menu.addSeparator();

      // File/Close
      menuitem = new JMenuItem("Close", GUIHelper.getIcon("exit.png"));
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  close();
	}
      });
      menu.add(menuitem);

      // Edit
      menu = new JMenu("Edit");
      menu.setMnemonic('E');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });
      result.add(menu);

      // Edit/Undo
      menuitem = new JMenuItem("Undo");
      menuitem.setMnemonic('U');
      menuitem.setEnabled(m_TextPanel.canUndo());
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Z"));
      menuitem.setIcon(GUIHelper.getIcon("undo.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_TextPanel.undo();
	}
      });
      menu.add(menuitem);
      m_MenuItemEditUndo = menuitem;

      menuitem = new JMenuItem("Redo");
      menuitem.setMnemonic('R');
      menuitem.setEnabled(m_TextPanel.canUndo());
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Y"));
      menuitem.setIcon(GUIHelper.getIcon("redo.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_TextPanel.redo();
	}
      });
      menu.add(menuitem);
      m_MenuItemEditRedo = menuitem;

      // Edit/Cut
      menuitem = new JMenuItem("Cut", GUIHelper.getIcon("cut.gif"));
      menuitem.setMnemonic('u');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed X"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_TextPanel.cut();
	}
      });
      menu.addSeparator();
      menu.add(menuitem);
      m_MenuItemEditCut = menuitem;

      // Edit/Copy
      menuitem = new JMenuItem("Copy", GUIHelper.getIcon("copy.gif"));
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed C"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_TextPanel.copy();
	}
      });
      menu.add(menuitem);
      m_MenuItemEditCopy = menuitem;

      // Edit/Paste
      menuitem = new JMenuItem("Paste", GUIHelper.getIcon("paste.gif"));
      menuitem.setMnemonic('P');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed V"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_TextPanel.paste();
	}
      });
      menu.add(menuitem);
      m_MenuItemEditPaste = menuitem;

      // Edit/Select all
      menuitem = new JMenuItem("Select all", GUIHelper.getEmptyIcon());
      menuitem.setMnemonic('S');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed A"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_TextPanel.selectAll();
	}
      });
      menu.addSeparator();
      menu.add(menuitem);
      m_MenuItemEditSelectAll = menuitem;

      // Edit/Find
      menuitem = new JMenuItem("Find", GUIHelper.getIcon("find.gif"));
      menuitem.setMnemonic('F');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed F"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_TextPanel.find();
	}
      });
      menu.addSeparator();
      menu.add(menuitem);
      m_MenuItemEditFind = menuitem;

      // Edit/Find next
      menuitem = new JMenuItem("Find next", GUIHelper.getEmptyIcon());
      menuitem.setMnemonic('n');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed F"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_TextPanel.findNext();
	}
      });
      menu.add(menuitem);
      m_MenuItemEditFindNext = menuitem;

      // View
      menu = new JMenu("View");
      menu.setMnemonic('V');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });
      result.add(menu);

      // View/Tab size
      menuitem = new JMenuItem("Tab size...");
      menuitem.setMnemonic('T');
      menuitem.setIcon(GUIHelper.getIcon("text_indent.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  String size = GUIHelper.showInputDialog(
	      TextPanel.this, "Please enter new tab size (> 0)", "" + m_TextPanel.getTabSize());
	  if (size == null)
	    return;
	  try {
	    int value = Integer.parseInt(size);
	    if (value <= 0)
	      return;
	  }
	  catch (Exception ex) {
	    // ignored
	  }
	  m_TextPanel.setTabSize(Integer.parseInt(size));
	}
      });
      menu.add(menuitem);
      m_MenuItemViewTabSize = menuitem;

      // View/Select font
      menuitem = new JMenuItem("Font...");
      menuitem.setMnemonic('f');
      menuitem.setIcon(GUIHelper.getIcon("font.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  FontChooser dialog;
	  if (getParentDialog() != null)
	    dialog = new FontChooser(getParentDialog());
	  else
	    dialog = new FontChooser(getParentFrame());
	  dialog.setCurrent(m_TextPanel.getTextFont());
	  dialog.setLocationRelativeTo(TextPanel.this);
	  dialog.setVisible(true);
	  if (!dialog.getCurrent().equals(m_TextPanel.getTextFont()))
	    m_TextPanel.setTextFont(dialog.getCurrent());
	}
      });
      menu.add(menuitem);
      m_MenuItemViewFont = menuitem;

      // View/Line wrap
      menuitem = new JCheckBoxMenuItem("Line wrap");
      menuitem.setMnemonic('L');
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.setSelected(getLineWrap());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_TextPanel.setLineWrap(m_MenuItemViewLineWrap.isSelected());
	}
      });
      menu.addSeparator();
      menu.add(menuitem);
      m_MenuItemViewLineWrap = (JCheckBoxMenuItem) menuitem;
      
      m_MenuBar = result;
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Updates title and menu items.
   */
  protected void update() {
    updateTitle();
    updateMenu();
  }

  /**
   * Updates the title of the dialog.
   *
   * @see		#m_UpdateParentTitle
   */
  protected void updateTitle() {
    Runnable	run;

    if (!m_TitleGenerator.isEnabled())
      return;

    run = new Runnable() {
      @Override
      public void run() {
	String title = m_TitleGenerator.generate(m_TextPanel.getCurrentFile(), m_TextPanel.isModified());
	setParentTitle(title);
      }
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Updates the state of the menu items.
   */
  protected void updateMenu() {
    Runnable	run;

    if (m_MenuBar == null)
      return;
    
    run = new Runnable() {
      @Override
      public void run() {
	boolean contentAvailable = (m_TextPanel.getContent().length() > 0);
	m_MenuItemFileNew.setEnabled(isEditable() && contentAvailable);
	// File
	if (m_CanOpenFiles) {
	  m_MenuItemFileOpen.setEnabled(isEditable() && true);
	  m_MenuItemFileSave.setEnabled(contentAvailable && isModified());
	}
	m_MenuItemFileSaveAs.setEnabled(contentAvailable);
	// Edit
	m_MenuItemEditUndo.setEnabled(isEditable() && m_TextPanel.canUndo());
	m_MenuItemEditRedo.setEnabled(isEditable() && m_TextPanel.canRedo());
	m_MenuItemEditCut.setEnabled(isEditable() && m_TextPanel.canCut());
	m_MenuItemEditCopy.setEnabled(m_TextPanel.canCopy());
	m_MenuItemEditPaste.setEnabled(isEditable() && m_TextPanel.canPaste());
	m_MenuItemEditFind.setEnabled(contentAvailable);
	m_MenuItemEditFindNext.setEnabled(contentAvailable && (m_TextPanel.getLastFind() != null));
	// View
	m_MenuItemViewLineWrap.setSelected(getLineWrap());
      }
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Closes the dialog, if possible.
   */
  protected void close() {
    if (m_TextPanel.checkForModified()) {
      if (getParentDialog() != null)
	getParentDialog().setVisible(false);
      else if (getParentFrame() != null)
	getParentFrame().setVisible(false);
    }

    closeParent();
  }

  /**
   * Opens the specified file using UTF-8 and loads/displays the content.
   *
   * @param file	the file to load
   * @see		#getCanOpenFiles()
   */
  public void open(File file) {
    open(file, null);
  }

  /**
   * Opens the specified file and loads/displays the content.
   *
   * @param file	the file to load
   * @param encoding	the encoding to use, null for default UTF-8
   * @see		#getCanOpenFiles()
   */
  public void open(File file, String encoding) {
    if (getCanOpenFiles()) {
      if (m_TextPanel.open(file, encoding))
	addRecentItem();
    }
    else {
      throw new IllegalAccessError("Cannot load files!");
    }
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  public Class[] getSendToClasses() {
    return new Class[]{String.class, JTextComponent.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the requested classes
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    return    (SendToActionUtils.isAvailable(new Class[]{String.class, JTextComponent.class}, cls))
           && (m_TextPanel.getContent().length() > 0);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the requested classes
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    Object	result;

    result = null;

    if ((SendToActionUtils.isAvailable(String.class, cls))) {
      result = m_TextPanel.getContent();
      if (((String) result).length() == 0)
	result = null;
    }
    else if (SendToActionUtils.isAvailable(JTextComponent.class, cls)) {
      if (m_TextPanel.getContent().length() > 0)
	result = m_TextPanel;
    }

    return result;
  }

  /**
   * Sets whether to update the parent's title.
   *
   * @param value	if true the parent's title will get updated
   */
  public void setUpdateParentTitle(boolean value) {
    m_TitleGenerator.setEnabled(value);
    if (m_TitleGenerator.isEnabled())
      updateTitle();
  }

  /**
   * Returns whether to update the parent's title.
   *
   * @return		true if to update the parent's title
   */
  public boolean getUpdateParentTitle() {
    return m_TitleGenerator.isEnabled();
  }
}
