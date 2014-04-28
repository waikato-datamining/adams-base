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
 *    PlaceholderFileEditor.java
 *    Copyright (C) 1999-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOption;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.PreviewBrowserDialog;
import adams.gui.goe.PropertyPanel.PopupMenuCustomizer;

/**
 * A PropertyEditor for FlowFile objects that lets the user select a file.
 * <p/>
 * Based on <code>weka.gui.FileEditor</code>.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @version $Revision$
 * @see weka.gui.FileEditor
 */
public class PlaceholderFileEditor
  extends AbstractPropertyEditorSupport
  implements PopupMenuCustomizer, CustomStringRepresentationHandler, 
             MultiSelectionEditor, InlineEditorSupport {

  /** The file chooser used for selecting files. */
  protected transient BaseFileChooser m_FileChooser;

  /**
   * Returns the file as string.
   *
   * @param option	the current option
   * @param object	the file object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((PlaceholderFile) object).getPath();
  }

  /**
   * Returns a file generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a file
   * @return		the generated file
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new PlaceholderFile(str);
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return valueOf(null, str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    PlaceholderFile f = (PlaceholderFile) getValue();
    if (f == null)
      return "null";
    else
      return "new adams.core.io.PlaceholderFile(\"" + f.getName() + "\")";
  }

  /**
   * Returns the file chooser and creates it if necessary.
   */
  protected BaseFileChooser getFileChooser() {
    BaseFileChooser	fileChooser;
    PlaceholderFile 	currentFile;

    if (m_FileChooser == null) {
      currentFile = (PlaceholderFile) getValue();
      if (currentFile != null)
	fileChooser = new BaseFileChooser();
      else
	fileChooser = new BaseFileChooser(new PlaceholderFile(System.getProperty("user.dir")));
      fileChooser.setApproveButtonText("Select");
      fileChooser.setApproveButtonMnemonic('S');
      fileChooser.setMultiSelectionEnabled(false);
      fileChooser.setFileSelectionMode(BaseFileChooser.FILES_ONLY);
      fileChooser.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
  	String cmdString = e.getActionCommand();
  	if (cmdString.equals(BaseFileChooser.APPROVE_SELECTION)) {
  	  PlaceholderFile newVal = getFileChooser().getSelectedPlaceholderFile();
  	  setValue(newVal);
  	  closeDialog(APPROVE_OPTION);
  	}
  	else {
  	  closeDialog(CANCEL_OPTION);
  	}
        }
      });

      m_FileChooser = fileChooser;
    }
    
    return m_FileChooser;
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		a value of type 'Component'
   */
  @Override
  protected JComponent createCustomEditor() {
    return getFileChooser();
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    PlaceholderFile 	currentFile;

    super.initForDisplay();

    currentFile = (PlaceholderFile) getValue();
    if (currentFile != null)
      getFileChooser().setSelectedFile(currentFile);
  }

  /**
   * Paints a representation of the current Object.
   *
   * @param gfx 	the graphics context to use
   * @param box 	the area we are allowed to paint into
   */
  @Override
  public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
    FontMetrics fm = gfx.getFontMetrics();
    int vpad = (box.height - fm.getHeight()) / 2 ;
    PlaceholderFile f = (PlaceholderFile) getValue();
    String val = "No file";
    if (f != null)
      val = f.getPath();
    gfx.drawString(val, 2, fm.getHeight() + vpad);
  }
  
  /**
   * Checks whether inline editing is available.
   * 
   * @return		true if editing available
   */
  public boolean isInlineEditingAvailable() {
    return true;
  }

  /**
   * Sets the value to use.
   * 
   * @param value	the value to use
   */
  public void setInlineValue(String value) {
    if (isInlineValueValid(value))
      setValue(new PlaceholderFile(value));
  }

  /**
   * Returns the current value.
   * 
   * @return		the current value
   */
  public String getInlineValue() {
    return ((PlaceholderFile) getValue()).toString();
  }

  /**
   * Checks whether the value id valid.
   * 
   * @param value	the value to check
   * @return		true if valid
   */
  public boolean isInlineValueValid(String value) {
    return PlaceholderFile.isValid(value);
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    PlaceholderFile[]	result;
    BaseFileChooser	chooser;
    int			retVal;
    
    chooser = new BaseFileChooser(new PlaceholderFile(System.getProperty("user.dir")));
    chooser.setApproveButtonText("Select");
    chooser.setApproveButtonMnemonic('S');
    chooser.setMultiSelectionEnabled(true);
    chooser.setFileSelectionMode(BaseFileChooser.FILES_ONLY);
    retVal = chooser.showOpenDialog(parent);
    if (retVal == BaseFileChooser.APPROVE_OPTION)
      result = chooser.getSelectedPlaceholderFiles();
    else
      result = new PlaceholderFile[0];
    
    return result;
  }

  /**
   * For customizing the popup menu.
   *
   * @param owner	the property panel from where the menu originates
   * @param menu	the menu to customize
   */
  public void customizePopupMenu(final BasePanel owner, JPopupMenu menu) {
    JMenuItem			menuitem;
    final PlaceholderFile	file;

    file     = (PlaceholderFile) getValue();
    menuitem = new JMenuItem("Open in preview browser...");
    menuitem.setIcon(GUIHelper.getIcon("open.gif"));
    menuitem.setEnabled(file.exists() && !file.isDirectory());
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	PreviewBrowserDialog dialog = new PreviewBrowserDialog();
	dialog.open(file);
	dialog.setLocationRelativeTo(dialog.getOwner());
	dialog.setVisible(true);
      }
    });
    menu.addSeparator();
    menu.add(menuitem);

    menuitem = new JMenuItem("Copy (absolute path)");
    menuitem.setIcon(GUIHelper.getIcon("copy.gif"));
    menuitem.setEnabled(true);
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	GUIHelper.copyToClipboard(file.getAbsolutePath());
      }
    });
    menu.add(menuitem);
  }
}
