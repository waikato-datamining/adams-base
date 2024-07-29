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
 * TarExport.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.multiobjectexport;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;
import adams.core.io.TarUtils;
import adams.core.io.TempUtils;
import adams.gui.visualization.debug.objectexport.AbstractObjectExporter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Exports the objects to a tar file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TarExport
  extends AbstractMultiObjectExportWithPreferredExtensions
  implements FileWriter {

  private static final long serialVersionUID = -8530263811736177464L;

  /** the tar file to export the objects to. */
  protected PlaceholderFile m_OutputFile;

  /** the prefix for the files. */
  protected String m_Prefix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Exports the objects to the specified TAR file. " 
	     + "Supported compression extensions: " + Utils.flatten(TarUtils.getSupportedCompressionExtensions(), ", ");
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-file", "outputFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "prefix", "prefix",
      "");
  }

  /**
   * Sets the output tar file for the files generated from the objects.
   *
   * @param value	the directory
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output tar file for the files generated from the objects.
   *
   * @return		the directory
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFileTipText() {
    return "The TAR file to store the files in that get generated from the objects; "
	     + "supported compression extensions: " + Utils.flatten(TarUtils.getSupportedCompressionExtensions(), ", ");
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
    return "The optional prefix for the files (<prefix><name>.<ext>";
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
    MessageCollection			errors;
    List<AbstractObjectExporter> 	exporters;
    int					i;
    String				ext;
    PlaceholderFile			file;
    List<File>				files;
    String				msg;
    File				outputDir;

    for (i = 0; i < names.length; i++)
      names[i] = FileUtils.createFilename(m_Prefix + names[i], "");
    names  = disambiguateNames(names);
    errors = new MessageCollection();

    // temp output dir
    outputDir = TempUtils.createTempFile("tarexport", "");
    if (!outputDir.mkdirs())
      errors.add("Failed to create temporary output directory: " + outputDir);

    files = new ArrayList<>();
    if (errors.isEmpty()) {
      for (i = 0; i < names.length; i++) {
	exporters = determineExporters(names[i], objects[i], errors);
	for (AbstractObjectExporter exporter: exporters) {
	  ext  = determineExtension(exporter);
	  file = new PlaceholderFile(outputDir.getAbsolutePath() + File.separator + names[i] + "." + ext);
	  msg  = exporter.export(objects[i], file);
	  if (msg != null)
	    errors.add("Failed to find export '" + names[i] + "'/" + Utils.classToString(objects[i]) + "\n" + msg);
	  else
	    files.add(file);
	}
      }
    }

    // create tar file
    if (errors.isEmpty()) {
      msg = TarUtils.compress(m_OutputFile, files.toArray(new File[0]), ".*", 1024);
      if (msg != null)
	errors.add("Failed to generated TAR archive: " + m_OutputFile + "\n" + msg);
    }

    if (outputDir.exists())
      FileUtils.delete(outputDir);

    if (errors.isEmpty())
      return null;
    else
      return errors.toString();
  }
}
