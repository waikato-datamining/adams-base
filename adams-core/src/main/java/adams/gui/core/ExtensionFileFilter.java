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
 * ExtensionFileFilter.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import java.io.File;
import java.io.Serializable;

import javax.swing.filechooser.FileFilter;

import adams.core.Utils;
import adams.flow.core.AbstractActor;

/**
 * A FileFilter for filtering files base on their extension. Directories are
 * always accepted.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExtensionFileFilter
  extends FileFilter
  implements Comparable<ExtensionFileFilter>, Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -2053536869521888824L;

  /** the "all files" extensions. */
  public final static String EXT_ALL_FILES = "*";

  /** the description of the filter. */
  protected String m_Description;

  /** the extensions. */
  protected String[] m_Extensions;

  /** whether case-sensitive or not. */
  protected boolean m_CaseSensitive;

  /**
   * Constructs a filter that matches all files.
   */
  public ExtensionFileFilter() {
    this("All files", EXT_ALL_FILES);
  }

  /**
   * Constructs a filter that matches files with the given extension, not
   * case-sensitive.
   *
   * @param description	the display string
   * @param extension	the extensions of the files (no dot!)
   */
  public ExtensionFileFilter(String description, String extension) {
    this(description, extension, false);
  }

  /**
   * Constructs a filter that matches files with the given extension, not
   * case-sensitive.
   *
   * @param description	the display string
   * @param extensions	the extensions of the files (no dot!)
   */
  public ExtensionFileFilter(String description, String[] extensions) {
    this(description, extensions, false);
  }

  /**
   * Constructs a filter that matches files with the given extension, not
   * case-sensitive.
   *
   * @param description		the display string
   * @param extension		the extensions of the files (no dot!)
   * @param caseSensitive	if true then the filter is case-sensitive
   */
  public ExtensionFileFilter(String description, String extension, boolean caseSensitive) {
    this(description, new String[]{extension}, caseSensitive);
  }

  /**
   * Constructs a filter that matches files with the given extension, not
   * case-sensitive.
   *
   * @param description		the display string
   * @param extensions		the extensions of the files (no dot!)
   * @param caseSensitive	if true then the filter is case-sensitive
   */
  public ExtensionFileFilter(String description, String[] extensions, boolean caseSensitive) {
    super();

    m_Description   = description;
    m_CaseSensitive = caseSensitive;
    m_Extensions    = new String[extensions.length];
    for (int i = 0; i < extensions.length; i++)
      m_Extensions[i] = extensions[i].replaceAll("^\\.", "");
  }

  /**
   * Whether the given file is accepted by this filter.
   *
   * @param f		the file to check
   * @return		true if accepted
   */
  @Override
  public boolean accept(File f) {
    int		i;
    boolean	result;

    result = false;

    if (f.isDirectory())
      result = true;

    if (!result) {
      for (i = 0; i < m_Extensions.length; i++) {
	if (m_Extensions[i].equals(EXT_ALL_FILES))
	  result = true;
	else {
	  if (m_CaseSensitive)
	    result = f.getName().endsWith("." + m_Extensions[i]);
	  else
	    result = f.getName().toLowerCase().endsWith("." + m_Extensions[i].toLowerCase());
	}
	if (result)
	  break;
      }
    }

    return result;
  }

  /**
   * The description of this filter.
   *
   * @return		the description
   */
  @Override
  public String getDescription() {
    String	result;
    int		i;

    result = m_Description + " (";
    for (i = 0; i < m_Extensions.length; i++) {
      if (i > 0)
	result += ", ";
      if (i == 9) {
	result += "...";
	break;
      }
      result += "." + m_Extensions[i];
    }
    result += ")";

    return result;
  }

  /**
   * Returns the extensions.
   *
   * @return		the extensions
   */
  public String[] getExtensions() {
    return m_Extensions;
  }

  /**
   * Returns whether the filter is case-sensitive or not.
   *
   * @return		true if case-sensitive
   */
  public boolean isCaseSensitive() {
    return m_CaseSensitive;
  }

  /**
   * Compares this image format with the specified filter for order. Returns a
   * negative integer, zero, or a positive integer as this filter is less
   * than, equal to, or greater than the specified filter.
   *
   * @param   o the filter to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   */
  public int compareTo(ExtensionFileFilter o) {
    int		result;
    int		i;

    result = m_Description.compareTo(o.m_Description);

    if (result == 0)
      result = new Integer(m_Extensions.length).compareTo(new Integer(o.m_Extensions.length));

    if (result == 0) {
      for (i = 0; i < m_Extensions.length; i++) {
	result = m_Extensions[i].compareTo(o.m_Extensions[i]);
	if (result != 0)
	  break;
      }
    }

    return result;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * <br><br>
   * For some reason, we cannot override this method, using the result returned
   * by the compareTo(ExtensionFileFilter) method. This results in the user
   * no longer being able to change the file filter in the FileChooser dialog.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  /**
   * Returns a short description of the filter.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    return "desc=" + m_Description + ", ext=" + Utils.arrayToString(m_Extensions) + ", case=" + m_CaseSensitive;
  }

  /**
   * Returns a filter for text files.
   *
   * @return		the text filter
   */
  public static ExtensionFileFilter getTextFileFilter() {
    return new ExtensionFileFilter("Text files", "txt");
  }

  /**
   * Returns a filter for flow files.
   *
   * @return		the flow filter
   */
  public static ExtensionFileFilter getFlowFileFilter() {
    return new ExtensionFileFilter("Flow files", new String[]{AbstractActor.FILE_EXTENSION, AbstractActor.FILE_EXTENSION_GZ});
  }

  /**
   * Returns a filter for log files.
   *
   * @return		the log filter
   */
  public static ExtensionFileFilter getLogFileFilter() {
    return new ExtensionFileFilter("Log files", "log");
  }

  /**
   * Returns a filter for JSON files.
   *
   * @return		the JSON filter
   */
  public static ExtensionFileFilter getJSONFileFilter() {
    return new ExtensionFileFilter("JSON files", "json");
  }

  /**
   * Returns a filter for props files.
   *
   * @return		the props filter
   */
  public static ExtensionFileFilter getPropertiesFileFilter() {
    return new ExtensionFileFilter("Properties files", new String[]{"props", "properties"});
  }

  /**
   * Returns a filter for model files.
   *
   * @return		the model filter
   */
  public static ExtensionFileFilter getModelFileFilter() {
    return new ExtensionFileFilter("Model files", new String[]{"model", "model.gz"});
  }

  /**
   * Returns a filter for CSV files.
   *
   * @return		the CSV filter
   */
  public static ExtensionFileFilter getCsvFileFilter() {
    return new ExtensionFileFilter("Comma-separated values files", "csv");
  }

  /**
   * Returns a filter for PDF files.
   *
   * @return		the PDF filter
   */
  public static ExtensionFileFilter getPdfFileFilter() {
    return new ExtensionFileFilter("PDF files", "pdf");
  }

  /**
   * Returns a filter for ZIP files.
   *
   * @return		the ZIP filter
   */
  public static ExtensionFileFilter getZipFileFilter() {
    return new ExtensionFileFilter("ZIP files", "zip");
  }

  /**
   * Returns a filter for serialized model files.
   *
   * @return		the model filter
   */
  public static ExtensionFileFilter getSerialiedModelFileFilter() {
    return new ExtensionFileFilter("Serialized objects", "model");
  }
}
