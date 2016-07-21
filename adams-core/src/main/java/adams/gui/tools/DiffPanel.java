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
 * DiffPanel.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.ParameterPanel;
import adams.gui.core.RecentFilesHandler;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.visualization.debug.SideBySideDiffPanel;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * For comparing two files side-by-side.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DiffPanel
  extends BasePanel 
  implements MenuBarProvider {

  /** for serialization. */
  private static final long serialVersionUID = 1623023054538026169L;

  /**
   * Dialog for selecting the two files for comparison.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class LoadDialog
    extends ApprovalDialog {

    /** for serialization. */
    private static final long serialVersionUID = 4178028433029613223L;

    /** the panel for the files. */
    protected ParameterPanel m_PanelFiles;
    
    /** the first file. */
    protected FileChooserPanel m_PanelFile1;
    
    /** the second file. */
    protected FileChooserPanel m_PanelFile2;
    
    /**
     * Creates a modeless dialog without a title with the specified Dialog as
     * its owner.
     *
     * @param owner	the owning dialog
     */
    public LoadDialog(Dialog owner) {
      super(owner);
    }

    /**
     * Creates a dialog with the specified owner Dialog and modality.
     *
     * @param owner	the owning dialog
     * @param modality	the type of modality
     */
    public LoadDialog(Dialog owner, ModalityType modality) {
      super(owner, modality);
    }

    /**
     * Creates a modeless dialog with the specified title and with the specified
     * owner dialog.
     *
     * @param owner	the owning dialog
     * @param title	the title of the dialog
     */
    public LoadDialog(Dialog owner, String title) {
      super(owner, title);
    }

    /**
     * Creates a modeless dialog without a title with the specified Frame as
     * its owner.
     *
     * @param owner	the owning frame
     */
    public LoadDialog(Frame owner) {
      super(owner);
    }

    /**
     * Creates a dialog with the specified owner Frame, modality and an empty
     * title.
     *
     * @param owner	the owning frame
     * @param modal	whether the dialog is modal or not
     */
    public LoadDialog(Frame owner, boolean modal) {
      super(owner, modal);
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();
     
      setTitle("Load files to compare");
      
      m_PanelFiles = new ParameterPanel();
      getContentPane().add(m_PanelFiles, BorderLayout.CENTER);
      
      m_PanelFile1 = new FileChooserPanel();
      m_PanelFile1.setPrefix("First file");
      m_PanelFile1.setAcceptAllFileFilterUsed(true);
      m_PanelFile1.addChoosableFileFilter(ExtensionFileFilter.getFlowFileFilter());
      m_PanelFile1.addChoosableFileFilter(ExtensionFileFilter.getPropertiesFileFilter());
      m_PanelFile1.addChoosableFileFilter(ExtensionFileFilter.getTextFileFilter());
      m_PanelFiles.addParameter(m_PanelFile1);
      
      m_PanelFile2 = new FileChooserPanel();
      m_PanelFile2.setPrefix("Second file");
      m_PanelFile2.setAcceptAllFileFilterUsed(true);
      m_PanelFile2.addChoosableFileFilter(ExtensionFileFilter.getFlowFileFilter());
      m_PanelFile2.addChoosableFileFilter(ExtensionFileFilter.getPropertiesFileFilter());
      m_PanelFile2.addChoosableFileFilter(ExtensionFileFilter.getTextFileFilter());
      m_PanelFiles.addParameter(m_PanelFile2);

      pack();
    }

    /**
     * Hook method for the "approve" button. 
     * 
     * @return		null if the input is valid, otherwise error mesage
     */
    @Override
    protected String checkInput() {
      if (m_PanelFile1.getCurrent().isDirectory())
	return "First file is a directory!";
      if (!m_PanelFile1.getCurrent().exists())
	return "First file does not exist!";
      if (m_PanelFile2.getCurrent().isDirectory())
	return "Second file is a directory!";
      if (!m_PanelFile2.getCurrent().exists())
	return "Second file does not exist!";
      return null;
    }

    /**
     * Returns the first file.
     * 
     * @return		the first file
     */
    public File getFirstFile() {
      return m_PanelFile1.getCurrent();
    }
    
    /**
     * Returns the second file.
     * 
     * @return		the second file
     */
    public File getSecondFile() {
      return m_PanelFile2.getCurrent();
    }
  }

  /** the file to store the recent files in (left panel). */
  public final static String SESSION_FILE_LEFT = "DiffLeft.props";

  /** the file to store the recent files in (right panel). */
  public final static String SESSION_FILE_RIGHT = "DiffRight.props";
  
  /** the diff panel. */
  protected SideBySideDiffPanel m_PanelDiff;

  /** the left content from the clipboard (null if none). */
  protected ArrayList<String> m_ClipboardLeft;

  /** the right content from the clipboard (null if none). */
  protected ArrayList<String> m_ClipboardRight;
  
  /** the file with the left content. */
  protected File m_FileLeft;
  
  /** the file with the right content. */
  protected File m_FileRight;
  
  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "load recent (left)" submenu. */
  protected JMenu m_MenuFileLoadRecentLeft;

  /** the "load recent (right)" submenu. */
  protected JMenu m_MenuFileLoadRecentRight;

  /** the "Paste (left)" submenu. */
  protected JMenuItem m_MenuItemEditPasteLeft;

  /** the "Paste (right)" submenu. */
  protected JMenuItem m_MenuItemEditPasteRight;
  
  /** the dialog for opening the files. */
  protected LoadDialog m_LoadDialog;
  
  /** the button for pasting the left content. */
  protected JButton m_ButtonPasteLeft;
  
  /** the button for pasting the right content. */
  protected JButton m_ButtonPasteRight;

  /** the recent files handler (left). */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandlerLeft;

  /** the recent files handler (right). */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandlerRight;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_ClipboardLeft           = null;
    m_ClipboardRight          = null;
    m_FileLeft                = new PlaceholderFile(".");
    m_FileRight               = new PlaceholderFile(".");
    m_RecentFilesHandlerLeft  = null;
    m_RecentFilesHandlerRight = null;
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_PanelDiff = new SideBySideDiffPanel();
    add(m_PanelDiff, BorderLayout.CENTER);
    
    // paste left
    m_ButtonPasteLeft = new JButton(GUIHelper.getIcon("paste.gif"));
    m_ButtonPasteLeft.setToolTipText("Paste from clipboard");
    m_ButtonPasteLeft.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	pasteLeft();
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_ButtonPasteLeft);
    m_PanelDiff.getPanel(true).add(panel, BorderLayout.SOUTH);

    // paste right
    m_ButtonPasteRight = new JButton(GUIHelper.getIcon("paste.gif"));
    m_ButtonPasteRight.setToolTipText("Paste from clipboard");
    m_ButtonPasteRight.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	pasteRight();
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(m_ButtonPasteRight);
    m_PanelDiff.getPanel(false).add(panel, BorderLayout.SOUTH);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenu		submenu;
    JMenuItem		menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // File/Open files
      menuitem = new JMenuItem("Open files...");
      menu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.setIcon(GUIHelper.getIcon("open.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  openFiles();
	}
      });

      // File/Open recent (left)
      submenu = new JMenu("Open recent (left)");
      menu.add(submenu);
      m_RecentFilesHandlerLeft = new RecentFilesHandler<JMenu>(SESSION_FILE_LEFT, 5, submenu);
      m_RecentFilesHandlerLeft.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  m_FileLeft      = new PlaceholderFile(e.getItem());
	  m_ClipboardLeft = null;
	  m_RecentFilesHandlerLeft.addRecentItem(m_FileLeft);
	  compare();
	}
      });
      m_MenuFileLoadRecentLeft = submenu;

      // File/Open recent (right)
      submenu = new JMenu("Open recent (right)");
      menu.add(submenu);
      m_RecentFilesHandlerRight = new RecentFilesHandler<JMenu>(SESSION_FILE_RIGHT, 5, submenu);
      m_RecentFilesHandlerRight.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  m_FileRight      = new PlaceholderFile(e.getItem());
	  m_ClipboardRight = null;
	  m_RecentFilesHandlerRight.addRecentItem(m_FileRight);
	  compare();
	}
      });
      m_MenuFileLoadRecentRight = submenu;

      // File/Reload
      menuitem = new JMenuItem("Reload");
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("F5"));
      menuitem.setIcon(GUIHelper.getIcon("refresh.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  reload();
	}
      });

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  closeParent();
	}
      });

      // Edit
      menu = new JMenu("Edit");
      result.add(menu);
      menu.setMnemonic('E');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // Edit/Paste (left)
      menuitem = new JMenuItem("Paste (left)");
      menu.add(menuitem);
      menuitem.setMnemonic('l');
      menuitem.setIcon(GUIHelper.getIcon("paste.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  pasteLeft();
	}
      });
      m_MenuItemEditPasteLeft = menuitem;

      // Edit/Paste (right)
      menuitem = new JMenuItem("Paste (right)");
      menu.add(menuitem);
      menuitem.setMnemonic('r');
      menuitem.setIcon(GUIHelper.getIcon("paste.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  pasteRight();
	}
      });
      m_MenuItemEditPasteRight = menuitem;

      // update menu
      m_MenuBar = result;
      updateMenu();
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;

    m_MenuFileLoadRecentLeft.setEnabled(m_RecentFilesHandlerLeft.size() > 0);
    m_MenuFileLoadRecentRight.setEnabled(m_RecentFilesHandlerRight.size() > 0);
    m_MenuItemEditPasteLeft.setEnabled(ClipboardHelper.canPasteStringFromClipboard());
    m_MenuItemEditPasteRight.setEnabled(ClipboardHelper.canPasteStringFromClipboard());
  }

  /**
   * Pastes the clipboard content into the left panel, if possible.
   */
  protected void pasteLeft() {
    if (!ClipboardHelper.canPasteStringFromClipboard())
      return;
    m_ClipboardLeft = new ArrayList(Arrays.asList(ClipboardHelper.pasteStringFromClipboard().split("\n")));
    compare();
  }

  /**
   * Pastes the clipboard content into the right panel, if possible.
   */
  protected void pasteRight() {
    if (!ClipboardHelper.canPasteStringFromClipboard())
      return;
    m_ClipboardRight = new ArrayList(Arrays.asList(ClipboardHelper.pasteStringFromClipboard().split("\n")));
    compare();
  }
  
  /**
   * Opens dialog for selecting two files and then comparing them.
   */
  protected void openFiles() {
    if (m_LoadDialog == null) {
      if (getParentDialog() != null)
	m_LoadDialog = new LoadDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_LoadDialog = new LoadDialog(getParentFrame(), true);
      m_LoadDialog.setLocationRelativeTo(this);
    }
    
    m_LoadDialog.setVisible(true);
    if (m_LoadDialog.getOption() != LoadDialog.APPROVE_OPTION)
      return;

    m_ClipboardLeft  = null;
    m_ClipboardRight = null;
    m_FileLeft       = m_LoadDialog.getFirstFile();
    m_FileRight      = m_LoadDialog.getSecondFile();
    
    m_RecentFilesHandlerLeft.addRecentItem(m_FileLeft);
    m_RecentFilesHandlerRight.addRecentItem(m_FileRight);

    compare();
  }
  
  /**
   * Compares the two files (if they exist).
   * 
   * @param file1	the first file
   * @param file2	the second file
   */
  public void compareFiles(File file1, File file2) {
    if (!file1.exists() || file1.isDirectory()) {
      System.err.println("File 1 '" + file1 + "' does not exist or is a directory!");
      return;
    }
    if (!file2.exists() || file2.isDirectory()) {
      System.err.println("File 2 '" + file2 + "' does not exist or is a directory!");
      return;
    }
    
    m_FileLeft  = file1;
    m_FileRight = file2;

    m_RecentFilesHandlerLeft.addRecentItem(m_FileLeft);
    m_RecentFilesHandlerRight.addRecentItem(m_FileRight);
    
    compare();
  }
  
  /**
   * Compares the content again.
   */
  protected void reload() {
    compare();
  }
  
  /**
   * Performs the comparison, either using files or clipboard content.
   */
  protected void compare() {
    SwingWorker 	worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	List<String>	left;
	List<String>	right;

	setCursor(new Cursor(Cursor.WAIT_CURSOR));

	if (m_ClipboardLeft != null) {
	  left = m_ClipboardLeft;
	  m_PanelDiff.setLabelText(true,  "Clipboard");
	}
	else {
	  if (m_FileLeft.isDirectory())
	    left = new ArrayList<String>();
	  else
	    left = FileUtils.loadFromFile(m_FileLeft);
	  m_PanelDiff.setLabelText(true,  m_FileLeft.toString());
	}

	if (m_ClipboardRight != null) {
	  right = m_ClipboardRight;
	  m_PanelDiff.setLabelText(false,  "Clipboard");
	}
	else {
	  if (m_FileRight.isDirectory())
	    right = new ArrayList<String>();
	  else
	    right = FileUtils.loadFromFile(m_FileRight);
	  m_PanelDiff.setLabelText(false,  m_FileRight.toString());
	}

	m_PanelDiff.compare(left, right);
	m_PanelDiff.setLastFileLeft(m_FileLeft);
	m_PanelDiff.setLastFileRight(m_FileRight);
	return null;
      }

      @Override
      protected void done() {
	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	super.done();
      }
    };
    worker.execute();
  }
}
