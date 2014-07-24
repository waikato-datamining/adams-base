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
 * TextFileChooser.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.chooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.nio.charset.Charset;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import adams.core.management.CharsetHelper;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;

/**
 * Specialized filechooser for text files.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TextFileChooser
  extends BaseFileChooser {

  /** for serialization. */
  private static final long serialVersionUID = -810326731112492794L;

  /** 
   * The accessory panel for setting the encoding.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FileEncodingPanel
    extends BasePanel {
    
    /** for serialization. */
    private static final long serialVersionUID = -2064293687093129577L;

    /** the bookmarks panel. */
    protected FileChooserBookmarksPanel m_PanelBookmarks;

    /** the panel for the encoding. */
    protected JPanel m_PanelEncoding;
    
    /** the label for the text field. */
    protected JLabel m_LabelEncoding;
    
    /** the text field for the encoding. */
    protected JComboBox m_ComboBoxEncoding;

    /** the default background color of the panel. */
    protected Color m_DefaultBackground;

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_DefaultBackground = getBackground();
    }
    
    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      setLayout(new BorderLayout());
      
      m_PanelBookmarks = new FileChooserBookmarksPanel();
      m_PanelBookmarks.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
      add(m_PanelBookmarks, BorderLayout.CENTER);
      
      m_PanelEncoding = new JPanel(new FlowLayout(FlowLayout.LEFT));
      add(m_PanelEncoding, BorderLayout.NORTH);
      m_PanelEncoding.setBorder(BorderFactory.createLineBorder(m_DefaultBackground));

      m_ComboBoxEncoding = new JComboBox(CharsetHelper.getIDs());
      m_ComboBoxEncoding.setSelectedItem(CharsetHelper.getSingleton().getCharset().name());
      
      m_LabelEncoding = new JLabel("Encoding");
      m_LabelEncoding.setLabelFor(m_ComboBoxEncoding);
      
      m_PanelEncoding.add(m_LabelEncoding);
      m_PanelEncoding.add(m_ComboBoxEncoding);
    }

    /**
     * Sets the owner.
     * 
     * @param value	the new owner
     */
    public void setOwner(JFileChooser value) {
      m_PanelBookmarks.setOwner(value);
    }
    
    /**
     * Returns the owner.
     * 
     * @return		the owner, null if none set
     */
    public JFileChooser getOwner() {
      return m_PanelBookmarks.getOwner();
    }
    
    /**
     * Sets the encoding.
     * 
     * @param value	the encoding to use
     */
    public void setEncoding(String value) {
      m_ComboBoxEncoding.setSelectedItem(value);
      if (m_ComboBoxEncoding.getSelectedIndex() == -1)
	m_ComboBoxEncoding.setSelectedItem(0);
    }
    
    /**
     * Returns the currently set encoding.
     * 
     * @return		the encoding
     */
    public String getEncoding() {
      return m_ComboBoxEncoding.getSelectedItem().toString();
    }
    
    /**
     * Returns whether the encoding is valid.
     * 
     * @param value	the encoding to check, e.g., "UTF-8"
     * @return		true if valid
     */
    public static boolean isValidEncoding(String value) {
      try {
        Charset.forName(value);
        return true;
      }
      catch (Exception e) {
	return false;
      }
    }
  }
  
  /** the accessory panel. */
  protected FileEncodingPanel m_PanelEncoding;
  
  /**
   * Constructs a <code>TextFileChooser</code> pointing to the user's
   * default directory. This default depends on the operating system.
   * It is typically the "My Documents" folder on Windows, and the
   * user's home directory on Unix.
   */
  public TextFileChooser() {
    super();
  }

  /**
   * Constructs a <code>TextFileChooser</code> using the given path.
   * Passing in a <code>null</code>
   * string causes the file chooser to point to the user's default directory.
   * This default depends on the operating system. It is
   * typically the "My Documents" folder on Windows, and the user's
   * home directory on Unix.
   *
   * @param currentDirectoryPath  a <code>String</code> giving the path
   *				to a file or directory
   */
  public TextFileChooser(String currentDirectoryPath) {
    super(currentDirectoryPath);
  }

  /**
   * Constructs a <code>TextFileChooser</code> using the given <code>File</code>
   * as the path. Passing in a <code>null</code> file
   * causes the file chooser to point to the user's default directory.
   * This default depends on the operating system. It is
   * typically the "My Documents" folder on Windows, and the user's
   * home directory on Unix.
   *
   * @param currentDirectory  a <code>File</code> object specifying
   *				the path to a file or directory
   */
  public TextFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * For initializing some stuff.
   */
  @Override
  protected void initialize() {
    ExtensionFileFilter	filter;
    Dimension		dim;
    
    super.initialize();
    
    m_PanelEncoding = new FileEncodingPanel();
    m_PanelEncoding.setOwner(this);
    dim = getDefaultAccessoryDimension();
    if (dim != null) {
      m_PanelEncoding.setSize(dim);
      m_PanelEncoding.setMinimumSize(dim);
      m_PanelEncoding.setPreferredSize(dim);
    }
    setAccessory(m_PanelEncoding);

    filter = ExtensionFileFilter.getTextFileFilter();
    addChoosableFileFilter(filter);
    addChoosableFileFilter(ExtensionFileFilter.getLogFileFilter());
    addChoosableFileFilter(ExtensionFileFilter.getCsvFileFilter());
    addChoosableFileFilter(ExtensionFileFilter.getPropertiesFileFilter());
    setFileFilter(filter);
    setDefaultExtension(ExtensionFileFilter.getTextFileFilter().getExtensions()[0]);
    setAutoAppendExtension(true);
  }
  
  /**
   * Sets the encoding.
   * 
   * @param value	the encoding to use
   */
  public void setEncoding(String value) {
    m_PanelEncoding.setEncoding(value);
  }
  
  /**
   * Returns the currently set encoding.
   * 
   * @return		the encoding
   */
  public String getEncoding() {
    return m_PanelEncoding.getEncoding();
  }
}
