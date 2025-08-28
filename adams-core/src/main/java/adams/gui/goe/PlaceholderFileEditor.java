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
 *    Copyright (C) 1999-2025 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.io.PlaceholderFile;
import adams.core.management.User;
import adams.core.option.parsing.PlaceholderFileParsing;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.PopupMenuActions;
import adams.gui.goe.PropertyPanel.PopupMenuCustomizer;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A PropertyEditor for FlowFile objects that lets the user select a file.
 * <br><br>
 * Based on <code>weka.gui.FileEditor</code>.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @see weka.gui.FileEditor
 */
public class PlaceholderFileEditor
  extends AbstractPropertyEditorSupport
  implements PopupMenuCustomizer, CustomStringRepresentationHandler, 
             MultiSelectionEditor, InlineEditorSupport {

  /** The file chooser used for selecting files. */
  protected transient BaseFileChooser m_FileChooser;

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return PlaceholderFileParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return PlaceholderFileParsing.valueOf(null, str);
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
	fileChooser = new BaseFileChooser(new PlaceholderFile(User.getCWD()));
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
    int[] offset;
    PlaceholderFile f = (PlaceholderFile) getValue();
    String val = "No file";
    if (f != null)
      val = f.getPath();
    GUIHelper.configureAntiAliasing(gfx, true);
    offset = GUIHelper.calculateFontOffset(gfx, box);
    gfx.drawString(val, offset[0], offset[1]);
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
    
    chooser = new BaseFileChooser(new PlaceholderFile(User.getCWD()));
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
    final PlaceholderFile	file;

    menu.addSeparator();

    file = (PlaceholderFile) getValue();

    PopupMenuActions.copyAbsolutePath(menu, file);
    PopupMenuActions.openInPreviewBrowser(menu, file);
    PopupMenuActions.openInFileBrowser(menu, file);
    PopupMenuActions.openInTerminal(menu, file);
    PopupMenuActions.copyAbsolutePath(menu, file);
  }
}
