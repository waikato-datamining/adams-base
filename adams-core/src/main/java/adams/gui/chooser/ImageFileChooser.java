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
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.JAIImageReader;
import adams.data.io.output.AbstractImageWriter;
import adams.data.io.output.JAIImageWriter;
import adams.gui.core.GUIHelper;

/**
 * A file chooser for images.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageFileChooser
  extends AbstractConfigurableExtensionFileFilterFileChooser<AbstractImageReader,AbstractImageWriter> {

  /** for serialization. */
  private static final long serialVersionUID = -4519042048473978377L;

  /** the checkbox for bringing up the GenericObjectEditor. */
  protected JCheckBox m_CheckBoxPreview;

  /** the image preview. */
  protected ImagePreview m_ImagePreview;

  /** the file filters for the readers. */
  protected static List<ExtensionFileFilterWithClass> m_ReaderFileFilters;

  /** the file filters for the writers. */
  protected static List<ExtensionFileFilterWithClass> m_WriterFileFilters;

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
    JPanel	config;
    JPanel	panel;
    Dimension	dim;

    config = (JPanel) super.createAccessoryPanel();

    m_CheckBoxPreview = new JCheckBox("Preview");
    m_CheckBoxPreview.setMnemonic('P');
    m_CheckBoxPreview.setSelected(false);
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
    m_ImagePreview.setVisible(false);
    panel.add(config, BorderLayout.CENTER);
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
  protected ExtensionFileFilterWithClass getDefaultFileFilter(int dialogType) {
    ExtensionFileFilterWithClass	result;
    boolean				found;
    String				preferred;
    
    result = null;
    found  = false;
    
    if (dialogType == OPEN_DIALOG) {
      preferred = GUIHelper.getString("PreferredImageReader", "png");
      for (ExtensionFileFilterWithClass reader: m_ReaderFileFilters) {
	for (String ext: reader.getExtensions()) {
	  if (ext.equalsIgnoreCase(preferred)) {
	    found  = true;
	    result = reader;
	    break;
	  }
	}
	if (found)
	  break;
      }
    }
    else if (dialogType == SAVE_DIALOG) {
      preferred = GUIHelper.getString("PreferredImageWriter", "png");
      for (ExtensionFileFilterWithClass writer: m_WriterFileFilters) {
	for (String ext: writer.getExtensions()) {
	  if (ext.equalsIgnoreCase(preferred)) {
	    found  = true;
	    result = writer;
	    break;
	  }
	}
	if (found)
	  break;
      }
    }
    
    if (!found)
      result = super.getDefaultFileFilter(dialogType);
    
    return result;
  }

  /**
   * Returns the file filters for opening files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilterWithClass> getOpenFileFilters() {
    return m_ReaderFileFilters;
  }

  /**
   * Returns the file filters for writing files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilterWithClass> getSaveFileFilters() {
    return m_WriterFileFilters;
  }

  /**
   * Returns the current image reader.
   *
   * @return		the image reader, null if not applicable
   */
  public AbstractImageReader getImageReader() {
    configureCurrentHandlerHook(OPEN_DIALOG);

    if (m_CurrentHandler instanceof AbstractImageReader)
      return (AbstractImageReader) m_CurrentHandler;
    else
      return null;
  }

  /**
   * Returns the current image writer.
   *
   * @return		the image writer, null if not applicable
   */
  public AbstractImageWriter getImageWriter() {
    configureCurrentHandlerHook(SAVE_DIALOG);

    if (m_CurrentHandler instanceof AbstractImageWriter)
      return (AbstractImageWriter) m_CurrentHandler;
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
    return (m_ReaderFileFilters != null);
  }

  /**
   * Performs the actual initialization of the filters.
   */
  @Override
  protected void doInitializeFilters() {
    initFilters(true, AbstractImageReader.getReaders());
    initFilters(false, AbstractImageWriter.getWriters());
  }

  /**
   * initializes the Filters.
   *
   * @param reader	if true then the reader filters are initialized
   * @param classnames	the classnames of the converters
   */
  protected static void initFilters(boolean reader, String[] classnames) {
    int					i;
    String 				classname;
    Class 				cls;
    String[] 				ext;
    String 				desc;
    Object		 		converter;
    ExtensionFileFilterWithClass 	filter;

    if (reader)
      m_ReaderFileFilters = new ArrayList<ExtensionFileFilterWithClass>();
    else
      m_WriterFileFilters  = new ArrayList<ExtensionFileFilterWithClass>();

    for (i = 0; i < classnames.length; i++) {
      classname = (String) classnames[i];

      // get data from converter
      try {
	cls       = Class.forName(classname);
	converter = cls.newInstance();
	if (reader) {
	  desc = ((AbstractImageReader) converter).getFormatDescription();
	  ext  = ((AbstractImageReader) converter).getFormatExtensions();
	}
	else {
	  desc = ((AbstractImageWriter) converter).getFormatDescription();
	  ext  = ((AbstractImageWriter) converter).getFormatExtensions();
	}
      }
      catch (Exception e) {
	System.err.println("Failed to set up '" + classname + "':");
	e.printStackTrace();
	cls       = null;
	converter = null;
	ext       = new String[0];
	desc      = "";
      }

      if (converter == null)
	continue;

      // reader?
      if (reader) {
	filter = new ExtensionFileFilterWithClass(classname, desc, ext);
	m_ReaderFileFilters.add(filter);
      }
      else {
	filter = new ExtensionFileFilterWithClass(classname, desc, ext);
	m_WriterFileFilters.add(filter);
      }
    }

    if (reader)
      Collections.sort(m_ReaderFileFilters);
    else
      Collections.sort(m_WriterFileFilters);
  }

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  @Override
  protected AbstractImageReader getDefaultReader() {
    return new JAIImageReader();
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected AbstractImageWriter getDefaultWriter() {
    return new JAIImageWriter();
  }

  /**
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public AbstractImageReader getReaderForFile(File file) {
    AbstractImageReader	result;

    result = null;

    for (ExtensionFileFilterWithClass filter: m_ReaderFileFilters) {
      if (filter.accept(file)) {
	try {
	  result = (AbstractImageReader) Class.forName(filter.getClassname()).newInstance();
	}
	catch (Exception e) {
	  System.err.println("Failed to instantiate reader '" + filter.getClassname() + "':");
	  e.printStackTrace();
	}
      }
    }

    return result;
  }

  /**
   * Returns the writer for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the writer, null if none found
   */
  public AbstractImageWriter getWriterForFile(File file) {
    AbstractImageWriter	result;

    result = null;

    for (ExtensionFileFilterWithClass filter: m_WriterFileFilters) {
      if (filter.accept(file)) {
	try {
	  result = (AbstractImageWriter) Class.forName(filter.getClassname()).newInstance();
	}
	catch (Exception e) {
	  System.err.println("Failed to instantiate writer '" + filter.getClassname() + "':");
	  e.printStackTrace();
	}
      }
    }

    return result;
  }

  /**
   * Returns the reader superclass for the GOE.
   *
   * @return		the reader class
   */
  @Override
  protected Class getReaderClass() {
    return AbstractImageReader.class;
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  @Override
  protected Class getWriterClass() {
    return AbstractImageWriter.class;
  }
}
