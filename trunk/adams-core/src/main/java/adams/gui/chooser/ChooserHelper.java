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
 * ChooserHelper.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.chooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adams.core.ClassLister;
import adams.data.io.input.AbstractDataContainerReader;
import adams.data.io.input.AbstractReportReader;
import adams.data.io.output.AbstractDataContainerWriter;
import adams.data.io.output.AbstractReportWriter;

/**
 * Helper class for file choosers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ChooserHelper {

  /**
   * Returns an instance of the specified subclass. Checks whether it is a
   * subclass of the specified superclass.
   *
   * @param superclass	the super class to check
   * @param subclass	the class name of the sub class
   * @return		the instance or null in case of an instantiation or
   * 			sub-class error
   */
  protected static Object getInstance(Class superclass, String subclass) {
    Object	result;

    try {
      result = Class.forName(subclass).newInstance();
      result.getClass().asSubclass(superclass);
    }
    catch (Exception e) {
      result = null;
    }

    return result;
  }

  /**
   * Returns all the classnames of data container readers that could handle
   * the given file, based on the file extensions that they handle.
   *
   * @param cls		the superclass of readers to use
   * @param file	the file to retrieve reader(s) for
   * @return		the classnames of potential readers
   */
  public static String[] getDataContainerReadersForFile(Class cls, File file) {
    List<String>		result;
    String[]			classes;
    AbstractDataContainerReader	reader;
    String[]			exts;
    String			fileExt;

    result  = new ArrayList<String>();
    classes = ClassLister.getSingleton().getClassnames(cls);
    fileExt = file.getName().toLowerCase().replaceAll(".*\\.", "");

    for (String clazz: classes) {
      reader = (AbstractDataContainerReader) getInstance(AbstractDataContainerReader.class, clazz);
      if (reader == null)
	continue;
      // check extensions
      exts = reader.getFormatExtensions();
      for (String ext: exts) {
	if (ext.toLowerCase().equals(fileExt)) {
	  result.add(clazz);
	  break;
	}
      }
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns all the classnames of data container writers that could handle
   * the given file, based on the file extensions that they handle.
   *
   * @param cls		the superclass of writers to use
   * @param file	the file to retrieve writer(s) for
   * @return		the classnames of potential writers
   */
  public static String[] getDataContainerWritersForFile(Class cls, File file) {
    List<String>		result;
    String[]			classes;
    AbstractDataContainerWriter	writer;
    String[]			exts;
    String			fileExt;

    result  = new ArrayList<String>();
    classes = ClassLister.getSingleton().getClassnames(cls);
    fileExt = file.getName().toLowerCase().replaceAll(".*\\.", "");

    for (String clazz: classes) {
      writer = (AbstractDataContainerWriter) getInstance(AbstractDataContainerWriter.class, clazz);
      if (writer == null)
	continue;
      // check extensions
      exts = writer.getFormatExtensions();
      for (String ext: exts) {
	if (ext.toLowerCase().equals(fileExt)) {
	  result.add(clazz);
	  break;
	}
      }
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns all the classnames of report readers that could handle
   * the given file, based on the file extensions that they handle.
   *
   * @param cls		the superclass of readers to use
   * @param file	the file to retrieve reader(s) for
   * @return		the classnames of potential readers
   */
  public static String[] getReportReadersForFile(Class cls, File file) {
    List<String>		result;
    String[]			classes;
    AbstractReportReader	reader;
    String[]			exts;
    String			fileExt;

    result  = new ArrayList<String>();
    classes = ClassLister.getSingleton().getClassnames(cls);
    fileExt = file.getName().toLowerCase().replaceAll(".*\\.", "");

    for (String clazz: classes) {
      reader = (AbstractReportReader) getInstance(AbstractReportReader.class, clazz);
      if (reader == null)
	continue;
      // check extensions
      exts = reader.getFormatExtensions();
      for (String ext: exts) {
	if (ext.toLowerCase().equals(fileExt)) {
	  result.add(clazz);
	  break;
	}
      }
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns all the classnames of report writers that could handle
   * the given file, based on the file extensions that they handle.
   *
   * @param cls		the superclass of writers to use
   * @param file	the file to retrieve writer(s) for
   * @return		the classnames of potential writers
   */
  public static String[] getReportWritersForFile(Class cls, File file) {
    List<String>		result;
    String[]			classes;
    AbstractReportWriter	writer;
    String[]			exts;
    String			fileExt;

    result  = new ArrayList<String>();
    classes = ClassLister.getSingleton().getClassnames(cls);
    fileExt = file.getName().toLowerCase().replaceAll(".*\\.", "");

    for (String clazz: classes) {
      writer = (AbstractReportWriter) getInstance(AbstractReportWriter.class, clazz);
      if (writer == null)
	continue;
      // check extensions
      exts = writer.getFormatExtensions();
      for (String ext: exts) {
	if (ext.toLowerCase().equals(fileExt)) {
	  result.add(clazz);
	  break;
	}
      }
    }

    return result.toArray(new String[result.size()]);
  }
}
