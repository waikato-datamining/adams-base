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
 * ObjectExporterFileChooser.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.chooser;

import adams.gui.visualization.debug.objectexport.AbstractObjectExporter;
import adams.gui.visualization.debug.objectexport.PlainTextExporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * File chooser for object exporters..
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ObjectExporterFileChooser
  extends AbstractConfigurableExtensionFileFilterFileChooser<Object, AbstractObjectExporter>{

  private static final long serialVersionUID = -8442453722350177493L;

  /** the file filters for the writers. */
  protected static List<ExtensionFileFilterWithClass> m_WriterFileFilters;

  /** the current class to export (null for all). */
  protected Class m_CurrentClass;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CurrentClass = null;
  }

  /**
   * Returns whether the filters have already been initialized.
   *
   * @return		true if the filters have been initialized
   */
  @Override
  protected boolean getFiltersInitialized() {
    return (m_WriterFileFilters != null);
  }

  /**
   * Performs the actual initialization of the filters.
   */
  @Override
  protected void doInitializeFilters() {
    initFilters(AbstractObjectExporter.getExporters());
  }

  /**
   * initializes the Filters.
   *
   * @param classnames	the classnames to use
   */
  protected static void initFilters(String[] classnames) {
    Class 				cls;
    String[] 				ext;
    String 				desc;
    Object		 		converter;
    ExtensionFileFilterWithClass 	filter;

    m_WriterFileFilters = new ArrayList<ExtensionFileFilterWithClass>();

    for (String classname: classnames) {
      // get data from converter
      try {
	cls       = Class.forName(classname);
	converter = cls.newInstance();
	desc      = ((AbstractObjectExporter) converter).getFormatDescription();
	ext       = ((AbstractObjectExporter) converter).getFormatExtensions();
      }
      catch (Exception e) {
	handleException("Failed to set up '" + classname + "':", e);
	cls       = null;
	converter = null;
	ext       = new String[0];
	desc      = "";
      }

      if (converter == null)
	continue;

      filter = new ExtensionFileFilterWithClass(classname, desc, ext);
      m_WriterFileFilters.add(filter);
    }

    Collections.sort(m_WriterFileFilters);
  }

  /**
   * Returns the file filters for opening files.
   *
   * @return		always null
   */
  @Override
  protected List<ExtensionFileFilterWithClass> getOpenFileFilters() {
    return null;
  }

  /**
   * Returns the file filters for writing files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilterWithClass> getSaveFileFilters() {
    List<ExtensionFileFilterWithClass>	result;
    AbstractObjectExporter		exporter;
    Class				cls;

    if (m_CurrentClass == null)
      return m_WriterFileFilters;

    result = new ArrayList<>();
    for (ExtensionFileFilterWithClass filter: m_WriterFileFilters) {
      try {
	cls      = Class.forName(filter.getClassname());
	exporter = (AbstractObjectExporter) cls.newInstance();
	if (exporter.handles(m_CurrentClass))
	  result.add(filter);
      }
      catch (Exception e) {
	handleException("Failed to check filter: " + filter.getClassname(), e);
      }
    }

    return result;
  }

  /**
   * Returns the default reader.
   *
   * @return		always null
   */
  @Override
  protected Object getDefaultReader() {
    return null;
  }

  /**
   * Returns the reader superclass for the GOE.
   *
   * @return		always null
   */
  @Override
  protected Class getReaderClass() {
    return null;
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected AbstractObjectExporter getDefaultWriter() {
    return new PlainTextExporter();
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  @Override
  protected Class getWriterClass() {
    return AbstractObjectExporter.class;
  }

  /**
   * Sets the class to initialize the file chooser for.
   *
   * @param value	the class, null to list all exporters
   */
  public void setCurrentClass(Class value) {
    m_CurrentClass = value;
  }

  /**
   * Returns the class used to initialize the file chooser with.
   *
   * @return		the class, null if listing all exporters
   */
  public Class getCurrentClass() {
    return m_CurrentClass;
  }

  /**
   * initializes the GUI.
   *
   * @param dialogType		the type of dialog to setup the GUI for
   */
  @Override
  protected void initGUI(int dialogType) {
    if (dialogType == OPEN_DIALOG)
      throw new IllegalStateException("Open dialog is not supported!");
    super.initGUI(dialogType);
  }
}
