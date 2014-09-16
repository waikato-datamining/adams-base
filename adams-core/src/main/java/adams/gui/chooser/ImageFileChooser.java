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
 * ImageFileChooser.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.chooser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import adams.data.image.BufferedImageHelper;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;

/**
 * A file chooser for images.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageFileChooser
  extends AbstractExtensionFileFilterFileChooser<ExtensionFileFilter> {

  /** for serialization. */
  private static final long serialVersionUID = -4519042048473978377L;

  /** the checkbox for bringing up the GenericObjectEditor. */
  protected JCheckBox m_CheckBoxPreview;

  /** the image preview. */
  protected ImagePreview m_ImagePreview;
  
  /**
   * A simple container for image formats.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ImageFormat
    implements Comparable<ImageFormat> {

    /** the format name. */
    protected String m_FormatName;

    /** the display name. */
    protected String m_DisplayName;

    /** the extensions. */
    protected List<String> m_Extensions;

    /**
     * Initializes the image format container.
     *
     * @param format	the format of this
     */
    public ImageFormat(String format) {
      this(format, format.toUpperCase() + " image");
    }

    /**
     * Initializes the image format container.
     *
     * @param format	the format of this
     * @param display	the display string to be used in the dialog
     */
    public ImageFormat(String format, String display) {
      super();

      m_FormatName  = format;
      m_DisplayName = display;
      m_Extensions  = new ArrayList<String>();
    }

    /**
     * Returns the format name.
     *
     * @return		the format name
     */
    public String getFormatName() {
      return m_FormatName;
    }

    /**
     * Returns the display name of the format.
     *
     * @return		the display name
     */
    public String getDisplayName() {
      return m_DisplayName;
    }

    /**
     * Adds an extension to the internal list.
     *
     * @param value	the extension to add
     */
    public void addExtension(String value) {
      if (!m_Extensions.contains(value)) {
	m_Extensions.add(value);
	Collections.sort(m_Extensions);
      }
    }

    /**
     * Returns the list of extensions currently stored.
     *
     * @return		the extensions
     */
    public List<String> getExtensions() {
      return m_Extensions;
    }

    /**
     * Compares this image format with the specified image format for order. Returns a
     * negative integer, zero, or a positive integer as this image format is less
     * than, equal to, or greater than the specified image format.
     *
     * @param   o the image format to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     */
    public int compareTo(ImageFormat o) {
      return m_FormatName.toLowerCase().compareTo(o.getFormatName().toLowerCase());
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj	the reference object with which to compare.
     * @return		true if this object is the same as the obj argument;
     * 			false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof ImageFormat))
	return false;
      else
	return (compareTo((ImageFormat) obj) == 0);
    }

    /**
     * Hashcode so can be used as hashtable key. Returns the hashcode of the
     * commandline string.
     *
     * @return		the hashcode
     */
    @Override
    public int hashCode() {
      return m_FormatName.hashCode();
    }

    /**
     * Returns a string representation of the format.
     *
     * @return		the string representation
     */
    @Override
    public String toString() {
      return
            "format=" + getFormatName() + ", "
          + "display=" + getDisplayName() + ", "
          + "ext=" + getExtensions();
    }
  }

  /** the image readers (format name &lt-gt; format container). */
  protected static Hashtable<String,ImageFormat> m_ImageReaders;

  /** the image writers (format name &lt-gt; format container). */
  protected static Hashtable<String,ImageFormat> m_ImageWriters;

  /**
   * Constructs a <code>ImageFileChooser</code> pointing to the user's
   * default directory. This default depends on the operating system.
   * It is typically the "My Documents" folder on Windows, and the
   * user's home directory on Unix.
   */
  public ImageFileChooser() {
    super();
  }

  /**
   * Constructs a <code>ImageFileChooser</code> using the given path.
   * Passing in a <code>null</code>
   * string causes the file chooser to point to the user's default directory.
   * This default depends on the operating system. It is
   * typically the "My Documents" folder on Windows, and the user's
   * home directory on Unix.
   *
   * @param currentDirectoryPath  a <code>String</code> giving the path
   *				to a file or directory
   */
  public ImageFileChooser(String currentDirectoryPath) {
    super(currentDirectoryPath);
  }

  /**
   * Constructs a <code>ImageFileChooser</code> using the given <code>File</code>
   * as the path. Passing in a <code>null</code> file
   * causes the file chooser to point to the user's default directory.
   * This default depends on the operating system. It is
   * typically the "My Documents" folder on Windows, and the user's
   * home directory on Unix.
   *
   * @param currentDirectory  a <code>File</code> object specifying
   *				the path to a file or directory
   */
  public ImageFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Creates an accessory panel displayed next to the files.
   * 
   * @return		the panel or null if none available
   */
  @Override
  protected JComponent createAccessoryPanel() {
    JPanel	result;
    JPanel	panel;
    Dimension	dim;

    super.createAccessoryPanel();

    m_CheckBoxPreview = new JCheckBox("Preview");
    m_CheckBoxPreview.setMnemonic('P');
    m_CheckBoxPreview.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_ImagePreview.setVisible(m_CheckBoxPreview.isSelected());
      }
    });
    result = new JPanel(new BorderLayout());
    result.add(m_CheckBoxPreview, BorderLayout.NORTH);
    
    panel = new JPanel(new BorderLayout());
    result.add(panel, BorderLayout.CENTER);
    
    m_ImagePreview = new ImagePreview(this);
    panel.add(m_PanelBookmarks, BorderLayout.CENTER);
    panel.add(m_ImagePreview, BorderLayout.SOUTH);
    
    dim = getDefaultAccessoryDimension();
    if (dim != null) {
      result.setSize(dim);
      result.setMinimumSize(dim);
      result.setPreferredSize(dim);
    }
    
    return result;
  }

  /**
   * Returns the default file filter to use.
   * 
   * @param dialogType	the dialog type: open/save
   * @return		the default file filter, null if unable find default one
   */
  @Override
  protected ExtensionFileFilter getDefaultFileFilter(int dialogType) {
    ExtensionFileFilter	result;
    boolean		found;
    String		preferred;
    
    result = null;
    found  = false;
    
    if (dialogType == OPEN_DIALOG) {
      preferred = GUIHelper.getString("PreferredImageReader", "png");
      for (ImageFormat format: m_ImageReaders.values()) {
	for (String ext: format.getExtensions()) {
	  if (ext.equalsIgnoreCase(preferred)) {
	    found = true;
	    result = new ExtensionFileFilter(
		format.getDisplayName(),
		format.getExtensions().toArray(new String[0]));
	    break;
	  }
	}
	if (found)
	  break;
      }
    }
    else if (dialogType == SAVE_DIALOG) {
      preferred = GUIHelper.getString("PreferredImageWriter", "png");
      for (ImageFormat format: m_ImageWriters.values()) {
	for (String ext: format.getExtensions()) {
	  if (ext.equalsIgnoreCase(preferred)) {
	    found = true;
	    result = new ExtensionFileFilter(
		format.getDisplayName(),
		format.getExtensions().toArray(new String[0]));
	    break;
	  }
	}
	if (found)
	  break;
      }
    }
    else {
      result = super.getDefaultFileFilter(dialogType);
    }
    
    return result;
  }

  /**
   * Returns the file filters for opening files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilter> getOpenFileFilters() {
    List<ExtensionFileFilter>	result;
    List<String>		keys;
    ImageFormat			format;

    result = new ArrayList<ExtensionFileFilter>();

    keys   = new ArrayList<String>(m_ImageReaders.keySet());
    for (String key: keys) {
      format = m_ImageReaders.get(key);
      result.add(
	  new ExtensionFileFilter(
	      format.getDisplayName(),
	      format.getExtensions().toArray(new String[0])));
    }
    Collections.sort(result);

    return result;
  }

  /**
   * Returns the file filters for writing files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilter> getSaveFileFilters() {
    List<ExtensionFileFilter>	result;
    List<String>		keys;
    ImageFormat			format;

    result = new ArrayList<ExtensionFileFilter>();

    keys   = new ArrayList<String>(m_ImageWriters.keySet());
    for (String key: keys) {
      format = m_ImageWriters.get(key);
      result.add(
	  new ExtensionFileFilter(
	      format.getDisplayName(),
	      format.getExtensions().toArray(new String[0])));
    }
    Collections.sort(result);

    return result;
  }

  /**
   * sets the current converter according to the current filefilter.
   */
  @Override
  protected void updateCurrentHandlerHook() {
    String	suffix;
    Object	newHandler;

    try {
      suffix = ((ExtensionFileFilter) getFileFilter()).getExtensions()[0];
      if (m_DialogType == OPEN_DIALOG)
	newHandler = BufferedImageHelper.getReaderForExtension(suffix);
      else
	newHandler = BufferedImageHelper.getWriterForExtension(suffix);
      if (newHandler == null)
	return;

      if (m_CurrentHandler == null) {
	m_CurrentHandler = newHandler;
      }
      else {
	if (!m_CurrentHandler.getClass().equals(newHandler.getClass()))
	  m_CurrentHandler = newHandler;
      }
    }
    catch (Exception e) {
      m_CurrentHandler = null;
      e.printStackTrace();
    }
  }

  /**
   * Returns the current image reader.
   *
   * @return		the image reader, null if not applicable
   */
  public ImageReader getImageReader() {
    configureCurrentHandlerHook(OPEN_DIALOG);

    if (m_CurrentHandler instanceof ImageReader)
      return (ImageReader) m_CurrentHandler;
    else
      return null;
  }

  /**
   * Returns the current image writer.
   *
   * @return		the image writer, null if not applicable
   */
  public ImageWriter getImageWriter() {
    configureCurrentHandlerHook(SAVE_DIALOG);

    if (m_CurrentHandler instanceof ImageWriter)
      return (ImageWriter) m_CurrentHandler;
    else
      return null;
  }

  /**
   * Returns whether the filters have already been initialized.
   *
   * @return		true if the filters have been initialized
   */
  @Override
  protected boolean getFiltersInitialized() {
    return (m_ImageReaders != null);
  }

  /**
   * Performs the actual initialization of the filters.
   */
  @Override
  protected void doInitializeFilters() {
    m_ImageReaders = new Hashtable<String,ImageFormat>();
    m_ImageWriters = new Hashtable<String,ImageFormat>();

    // readers
    String[] suffixes = ImageIO.getReaderFileSuffixes();
    for (int i = 0; i < suffixes.length; i++) {
      Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix(suffixes[i]);
      while (readers.hasNext()) {
	ImageReader reader = readers.next();
	try {
	  String formatName = reader.getFormatName();
	  if (!m_ImageReaders.containsKey(formatName))
	    m_ImageReaders.put(formatName, new ImageFormat(formatName));
	  m_ImageReaders.get(formatName).addExtension(suffixes[i]);
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    // writers
    suffixes = ImageIO.getWriterFileSuffixes();
    for (int i = 0; i < suffixes.length; i++) {
      // ImageWriters don't support "getFormatName()" so we'll just try and
      // find the suffix in the readers and use the first occurence for the
      // format name.
      String formatName = getReaderFormatName(suffixes[i]);
      if (formatName != null) {
	if (!m_ImageWriters.containsKey(formatName))
	  m_ImageWriters.put(formatName, new ImageFormat(formatName));
	m_ImageWriters.get(formatName).addExtension(suffixes[i]);
      }
    }
  }

  /**
   * Returns the reader format name for the file.
   *
   * @param file	the file to determine the format name for
   * @return		the reader format name, null if not found
   */
  public static String getReaderFormatName(File file) {
    return getReaderFormatName(file.getName().replaceAll(".*\\.", ""));
  }

  /**
   * Returns the reader format name for the suffix.
   *
   * @param suffix	the suffix to determine the format name for
   * @return		the reader format name, null if not found
   */
  public static String getReaderFormatName(String suffix) {
    String	result;

    result = null;

    for (String format: m_ImageReaders.keySet()) {
      List<String> exts = m_ImageReaders.get(format).getExtensions();
      for (String ext: exts) {
	if (ext.equals(suffix)) {
	  result = format;
	  break;
	}
      }
      if (result != null)
	break;
    }

    return result;
  }

  /**
   * Returns the writer format name for the file.
   *
   * @param file	the file to determine the format name for
   * @return		the writer format name, null if not found
   */
  public static String getWriterFormatName(File file) {
    return getWriterFormatName(file.getName().replaceAll(".*\\.", ""));
  }

  /**
   * Returns the writer format name for the suffix.
   *
   * @param suffix	the suffix to determine the format name for
   * @return		the writer format name, null if not found
   */
  public static String getWriterFormatName(String suffix) {
    String	result;

    result = null;

    for (String format: m_ImageWriters.keySet()) {
      List<String> exts = m_ImageWriters.get(format).getExtensions();
      for (String ext: exts) {
	if (ext.equals(suffix)) {
	  result = format;
	  break;
	}
      }
      if (result != null)
	break;
    }

    return result;
  }
}
