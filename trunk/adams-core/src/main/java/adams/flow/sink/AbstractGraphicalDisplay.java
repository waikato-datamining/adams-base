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
 * AbstractGraphicalDisplay.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.gui.chooser.TextFileChooser;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.print.JComponentWriter;
import adams.gui.print.JComponentWriterFileChooser;
import adams.gui.print.NullWriter;
import adams.gui.print.PNGWriter;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;

/**
 * Ancestor for actors that display stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGraphicalDisplay
  extends AbstractDisplay
  implements MenuBarProvider, ComponentSupplier, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -4882859296402745911L;

  /** the writer to use. */
  protected JComponentWriter m_Writer;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "clear" menu item. */
  protected JMenuItem m_MenuItemFileClear;

  /** the "save as" menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the "save text as" menu item. */
  protected JMenuItem m_MenuItemFileSaveTextAs;

  /** the "exit" menu item. */
  protected JMenuItem m_MenuItemFileClose;

  /** the filedialog for saving the panel as picture. */
  protected transient JComponentWriterFileChooser m_GraphicFileChooser;

  /** the filedialog for saving the textual data. */
  protected transient TextFileChooser m_TextFileChooser;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "writer", "writer",
	    new NullWriter());
  }

  /**
   * Sets the writer.
   *
   * @param value 	the writer
   */
  public void setWriter(JComponentWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer.
   *
   * @return 		the writer
   */
  public JComponentWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for generating the graphics output.";
  }

  /**
   * Checks whether a panel is available that can be saved.
   *
   * @return 		true if panel is available for saving
   */
  protected boolean hasPanel() {
    return (supplyComponent() != null);
  }

  /**
   * Returns the current component.
   *
   * @return		the current component, can be null
   */
  public JComponent supplyComponent() {
    return m_Panel;
  }
  
  /**
   * Returns (and initializes if necessary) the file chooser for the images.
   * 
   * @return		the file chooser
   */
  protected JComponentWriterFileChooser getGraphicFileChooser() {
    if (m_GraphicFileChooser == null)
      m_GraphicFileChooser = new JComponentWriterFileChooser();
    
    return m_GraphicFileChooser;
  }
  
  /**
   * Returns (and initializes if necessary) the file chooser for the text.
   * 
   * @return		the file chooser
   */
  protected TextFileChooser getTextFileChooser() {
    TextFileChooser	fileChooser;
    ExtensionFileFilter	filter;
    
    if (m_TextFileChooser == null) {
      fileChooser = new TextFileChooser();
      if (this instanceof TextSupplier) {
	filter = ((TextSupplier) this).getCustomTextFileFilter();
	if (filter != null) {
	  fileChooser.resetChoosableFileFilters();
	  fileChooser.addChoosableFileFilter(filter);
	  fileChooser.setFileFilter(filter);
	  fileChooser.setDefaultExtension(filter.getExtensions()[0]);
	}
      }
      m_TextFileChooser = fileChooser;
    }
    
    return m_TextFileChooser;
  }

  /**
   * Assembles the menu bar.
   *
   * @return		the menu bar
   */
  protected JMenuBar createMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenuItem	menuitem;

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

    // File/Clear
    if (supportsClear()) {
      menuitem = new JMenuItem("Clear");
      menu.add(menuitem);
      menuitem.setMnemonic('l');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
      menuitem.setIcon(GUIHelper.getIcon("new.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  clear();
	}
      });
      m_MenuItemFileClear = menuitem;
    }

    // File/Save As
    menuitem = new JMenuItem("Save as...");
    menu.add(menuitem);
    menuitem.setMnemonic('a');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
    menuitem.setIcon(GUIHelper.getIcon("save.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	saveAs();
      }
    });
    m_MenuItemFileSaveAs = menuitem;

    // File/Save Text As
    if (this instanceof TextSupplier) {
      menuitem = new JMenuItem("Save text as...");
      menu.add(menuitem);
      menuitem.setMnemonic('t');
      menuitem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveTextAs();
        }
      });
      m_MenuItemFileSaveTextAs = menuitem;
    }

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
    m_MenuItemFileClose = menuitem;

    return result;
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    if (m_MenuBar == null) {
      m_MenuBar = createMenuBar();
      updateMenu();
    }

    return m_MenuBar;
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;

    m_MenuItemFileSaveAs.setEnabled(hasPanel());
    if (m_MenuItemFileSaveTextAs != null)
      m_MenuItemFileSaveTextAs.setEnabled(hasPanel());
  }

  /**
   * Whether "clear" is supported and shows up in the menu.
   * <p/>
   * Default implementation returns "false".
   *
   * @return		true if supported
   */
  protected boolean supportsClear() {
    return false;
  }

  /**
   * Clears the display.
   * <p/>
   * Default implementation does nothing.
   */
  protected void clear() {
  }

  /**
   * Saves the panel as picture.
   */
  protected void saveAs() {
    int			retVal;
    JComponentWriter	writer;

    retVal = getGraphicFileChooser().showSaveDialog(m_Panel);
    if (retVal != JComponentWriterFileChooser.APPROVE_OPTION)
      return;

    writer = getGraphicFileChooser().getWriter();
    writer.setComponent(supplyComponent());
    try {
      writer.toOutput();
    }
    catch (Exception e) {
      handleException("Error saving panel to '" + writer.getFile() + "': ", e);
    }
  }

  /**
   * Saves the text.
   */
  protected void saveTextAs() {
    int		retVal;
    boolean	ok;

    retVal = getTextFileChooser().showSaveDialog(m_Panel);
    if (retVal != JComponentWriterFileChooser.APPROVE_OPTION)
      return;

    ok = FileUtils.writeToFile(
	getTextFileChooser().getSelectedFile().getAbsolutePath(),
	((TextSupplier) this).supplyText(),
	false,
	getTextFileChooser().getEncoding());

    if (!ok)
      getLogger().severe("Error saving ext to '" + getTextFileChooser().getSelectedFile() + "'!");
  }

  /**
   * Closes the dialog or frame.
   */
  protected void close() {
    m_Panel.closeParent();
  }

  /**
   * Removes all graphical components.
   */
  @Override
  protected void cleanUpGUI() {
    super.cleanUpGUI();

    m_MenuBar                = null;
    m_MenuItemFileClear      = null;
    m_MenuItemFileSaveAs     = null;
    m_MenuItemFileSaveTextAs = null;
    m_MenuItemFileClose      = null;
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  public Class[] getSendToClasses() {
    return new Class[]{PlaceholderFile.class, JComponent.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the requested classes
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    return    (SendToActionUtils.isAvailable(new Class[]{PlaceholderFile.class, JComponent.class}, cls))
           && (supplyComponent() != null);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the requested classes
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    Object		result;
    JComponent		comp;
    PNGWriter		writer;

    result = null;

    comp = supplyComponent();
    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      if (comp != null) {
	result = SendToActionUtils.nextTmpFile("actor-" + getName(), "png");
	writer = new PNGWriter();
	writer.setFile((PlaceholderFile) result);
	writer.setComponent(comp);
	try {
	  writer.generateOutput();
	}
	catch (Exception e) {
	  handleException("Failed to write image to " + result + ":", e);
	  result = null;
	}
      }
    }
    else if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
      result = comp;
    }

    return result;
  }
  
  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    Runnable	run;

    super.wrapUp();

    if (!(m_Writer instanceof NullWriter) && (supplyComponent() != null)) {
      run = new Runnable() {
	@Override
	public void run() {
	  try {
	    m_Writer.setComponent(supplyComponent());
	    m_Writer.toOutput();
	  }
	  catch (Exception e) {
	    handleException("Failed to write graphical output", e);
	  }
	}
      };
      SwingUtilities.invokeLater(run);
    }
  }
}
