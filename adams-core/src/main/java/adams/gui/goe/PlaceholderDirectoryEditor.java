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
 *    PlaceholderDirectoryEditor.java
 *    Copyright (C) 2010-2025 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.management.User;
import adams.core.option.parsing.PlaceholderDirectoryParsing;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.DirectoryChooserFactory;
import adams.gui.chooser.FileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.PopupMenuActions;
import adams.gui.goe.PropertyPanel.PopupMenuCustomizer;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * A PropertyEditor for PlaceholderDirectory objects that lets the user select
 * a directory.
 * <br><br>
 * Based on <code>weka.gui.FileEditor</code>.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 */
public class PlaceholderDirectoryEditor
    extends AbstractPropertyEditorSupport
    implements CustomStringRepresentationHandler, InlineEditorSupport,
    MultiSelectionEditor, PopupMenuCustomizer {

  /** The directory chooser used for selecting dirs. */
  protected FileChooser m_DirChooser;

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return PlaceholderDirectoryParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return PlaceholderDirectoryParsing.valueOf(null, str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    File f = (File) getValue();
    if (f == null)
      return "null";
    else
      return "new adams.core.io.PlaceholderDirectory(\"" + f.getName() + "\")";
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		a value of type 'Component'
   */
  @Override
  protected JComponent createCustomEditor() {
    PlaceholderDirectory 	currentDir;

    currentDir = (PlaceholderDirectory) getValue();
    if (currentDir != null)
      m_DirChooser = DirectoryChooserFactory.createChooser();
    else
      m_DirChooser = DirectoryChooserFactory.createChooser(new File(User.getCWD()));
    m_DirChooser.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String cmdString = e.getActionCommand();
	if (cmdString.equals(DirectoryChooserFactory.APPROVE_SELECTION)) {
	  PlaceholderDirectory newVal = new PlaceholderDirectory(m_DirChooser.getSelectedFile().getAbsolutePath());
	  setValue(newVal);
	  closeDialog(APPROVE_OPTION);
	}
	else {
	  closeDialog(CANCEL_OPTION);
	}
      }
    });

    return (JComponent) m_DirChooser;
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    PlaceholderDirectory 	currentFile;

    super.initForDisplay();

    currentFile = (PlaceholderDirectory) getValue();
    if (currentFile != null)
      m_DirChooser.setSelectedFile(currentFile);
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
    File f = (File) getValue();
    String val = "No directory";
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
      setValue(new PlaceholderDirectory(value));
  }

  /**
   * Returns the current value.
   *
   * @return		the current value
   */
  public String getInlineValue() {
    return getValue().toString();
  }

  /**
   * Checks whether the value id valid.
   *
   * @param value	the value to check
   * @return		true if valid
   */
  public boolean isInlineValueValid(String value) {
    return PlaceholderDirectory.isValid(value);
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  @Override
  public Object[] getSelectedObjects(Container parent) {
    PlaceholderDirectory[]	result;
    File[]			selected;
    FileChooser 		chooser;
    int				retVal;
    int				i;

    chooser = DirectoryChooserFactory.createChooser(new PlaceholderFile(User.getCWD()));
    chooser.setApproveButtonText("Select");
    chooser.setApproveButtonMnemonic('S');
    chooser.setMultiSelectionEnabled(true);
    retVal = chooser.showOpenDialog(parent);
    if (retVal == BaseFileChooser.APPROVE_OPTION) {
      selected = chooser.getSelectedFiles();
      result   = new PlaceholderDirectory[selected.length];
      for (i = 0; i < selected.length; i++)
        result[i] = new PlaceholderDirectory(selected[i]);
    }
    else {
      result = new PlaceholderDirectory[0];
    }

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

    PopupMenuActions.openInPreviewBrowser(menu, file);
    PopupMenuActions.openInFileBrowser(menu, file);
    PopupMenuActions.openInTerminal(menu, file);
  }
}
