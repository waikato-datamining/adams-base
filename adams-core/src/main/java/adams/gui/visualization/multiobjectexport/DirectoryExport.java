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
 * DirectoryExport.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.multiobjectexport;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.gui.visualization.debug.objectexport.AbstractObjectExporter;

import java.io.File;

/**
 * Exports the objects to a directory.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DirectoryExport
  extends AbstractMultiObjectExportWithPreferredExtensions {

  private static final long serialVersionUID = -8530263811736177464L;

  /** the directory to export the objects to. */
  protected PlaceholderDirectory m_OutputDir;

  /** the prefix for the files. */
  protected String m_Prefix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Exports the objects to the specified directory.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-dir", "outputDir",
      new PlaceholderDirectory());

    m_OptionManager.add(
      "prefix", "prefix",
      "");
  }

  /**
   * Sets the output directory for the files generated from the objects.
   *
   * @param value	the directory
   */
  public void setOutputDir(PlaceholderDirectory value) {
    m_OutputDir = value;
    reset();
  }

  /**
   * Returns the output directory for the files generated from the objects.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getOutputDir() {
    return m_OutputDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDirTipText() {
    return "The directory to store the files in that get generated from the objects.";
  }

  /**
   * Sets the optional prefix for the file names.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the optional prefix for the file names.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The optional prefix for the files (<outputdir>/<prefix><name>.<ext>).";
  }

  /**
   * Performs the actual export of the objects using the given names.
   *
   * @param names	the names for the objects
   * @param objects	the objects
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExport(String[] names, Object[] objects) {
    MessageCollection		errors;
    AbstractObjectExporter 	exporter;
    int				i;
    String			ext;
    PlaceholderFile		file;
    String			msg;

    for (i = 0; i < names.length; i++)
      names[i] = FileUtils.createFilename(m_Prefix + names[i], "");
    names  = disambiguateNames(names);
    errors = new MessageCollection();

    for (i = 0; i < names.length; i++) {
      exporter = determineExporter(names[i], objects[i], errors);
      ext      = determineExtension(exporter);
      file     = new PlaceholderFile(m_OutputDir.getAbsolutePath() + File.separator + names[i] + "." + ext);
      msg      = exporter.export(objects[i], file);
      if (msg != null)
	errors.add("Failed to find export '" + names[i] + "'/" + Utils.classToString(objects[i].getClass()) + "\n" + msg);
    }

    if (errors.isEmpty())
      return null;
    else
      return errors.toString();
  }
}
