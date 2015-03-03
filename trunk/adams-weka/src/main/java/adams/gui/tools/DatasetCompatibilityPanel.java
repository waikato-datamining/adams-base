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
 * DatasetCompatibilityPanel.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils;
import weka.gui.ConverterFileChooser;
import adams.core.Utils;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;

/**
 * Compares the headers of a number of datasets and outputs the results.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatasetCompatibilityPanel
  extends BasePanel 
  implements MenuBarProvider, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -5581002075106885237L;

  /** the filechooser for selecting the datasets. */
  protected ConverterFileChooser m_FileChooser;
  
  /** the text area to output the results in. */
  protected BaseTextArea m_TextArea;

  /** the selected files. */
  protected File[] m_CurrentFiles;
  
  /** the current loader. */
  protected AbstractFileLoader m_CurrentLoader;
  
  /** the menu bar. */
  protected JMenuBar m_MenuBar;

  /** the reload menu item. */
  protected JMenuItem m_MenuItemReload;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_FileChooser = new ConverterFileChooser(".");
    m_FileChooser.setMultiSelectionEnabled(true);
    m_FileChooser.setFileMustExist(true);
    
    m_CurrentFiles  = new File[0];
    m_CurrentLoader = null;
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_TextArea = new BaseTextArea();
    m_TextArea.setEditable(false);
    m_TextArea.setFont(GUIHelper.getMonospacedFont());
    add(new BaseScrollPane(m_TextArea), BorderLayout.CENTER);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
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

      // File/Open
      menuitem = new JMenuItem("Open...");
      menu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.setIcon(GUIHelper.getIcon("open.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  open();
	}
      });

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
      m_MenuItemReload = menuitem;

      // File/Send to
      menu.addSeparator();
      if (SendToActionUtils.addSendToSubmenu(this, menu))
	menu.addSeparator();

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  close();
	}
      });

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

    m_MenuItemReload.setEnabled((m_CurrentFiles.length > 0) && (m_CurrentLoader != null));
  }
  
  /**
   * Brings up dialog for selecting files.
   */
  public void open() {
    int		retVal;
    
    retVal = m_FileChooser.showOpenDialog(this);
    
    if (retVal != ConverterFileChooser.APPROVE_OPTION)
      return;

    if (m_FileChooser.getSelectedFiles().length < 2) {
      GUIHelper.showErrorMessage(this, "You must choose at least two files!");
      return;
    }
    
    open(m_FileChooser.getSelectedFiles(), m_FileChooser.getLoader());
  }

  /**
   * Opens the specified files, determines the loader automatically.
   * 
   * @param files	the files to open
   * @return		null if successful, otherwise error message
   */
  public String open(File[] files) {
    AbstractFileLoader	loader;
    
    if (files.length < 2)
      return "You must supply at least two files!";
    
    loader = ConverterUtils.getLoaderForFile(files[0].getAbsoluteFile());
    if (loader == null)
      return "Failed to determine loader for files!";
    
    return open(files, loader);
  }
  
  /**
   * Opens the specified files with the given loader.
   * 
   * @param files	the files to open
   * @param loader	the loader to use
   * @return		null if successful, otherwise error message
   */
  protected String open(File[] files, AbstractFileLoader loader) {
    Instances[]		datasets;
    int			i;
    
    if (files.length < 2)
      return "You must supply at least two files!";
    if (loader == null)
      return "No file loader specified!";
    
    datasets = new Instances[files.length];
    for (i = 0; i < files.length; i++) {
      try {
	loader.setFile(files[i].getAbsoluteFile());
	datasets[i] = loader.getStructure();
	loader.reset();
      }
      catch (Exception e) {
	return "Failed to load dataset from " + files[i] + ":\n" + Utils.throwableToString(e);
      }
    }
    
    compare(files, datasets);
    
    return null;
  }
  
  /**
   * Performs the actual comparison.
   * 
   * @param files	the files the data originated from
   * @param datasets	the datasets to compare
   */
  protected void compare(File[] files, Instances[] datasets) {
    int		i;
    int		n;
    String	msg;
    
    m_TextArea.setText("");
    for (i = 0; i < files.length - 1; i++) {
      for (n = i + 1; n < files.length; n++) {
	msg = datasets[i].equalHeadersMsg(datasets[n]);
	m_TextArea.append(
	    "--> " + files[i].toString() + "\n"
	    + "and " + files[n].toString() + "\n"
	    + Utils.indent((msg == null) ? "match" : msg, 4) + "\n");
      }
    }
  }
  
  /**
   * Re-compares the currently loaded files.
   */
  public void reload() {
    open(m_CurrentFiles, m_CurrentLoader);
  }
  
  /**
   * Closes the dialog.
   */
  public void close() {
    closeParent();
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
           && (m_TextArea.getText().length() > 0);
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
      result = m_TextArea.getText();
      if (((String) result).length() == 0)
	result = null;
    }
    else if (SendToActionUtils.isAvailable(JTextComponent.class, cls)) {
      if (m_TextArea.getText().length() > 0)
	result = m_TextArea;
    }

    return result;
  }
}
