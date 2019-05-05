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
 * AbstractFileBasedListingOutput.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.doc.listing;

import adams.core.QuickInfoHelper;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;

import java.util.List;
import java.util.Map;

/**
 * Ancestor for schemes that output the listings to a file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFileBasedListingOutput
  extends AbstractListingOutput
  implements FileWriter {

  private static final long serialVersionUID = -4484277695906980673L;

  /** the output file. */
  protected PlaceholderFile m_OutputFile;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-file", "outputFile",
      getDefaultOutputFile());
  }

  /**
   * Returns the default output file.
   *
   * @return		the default
   */
  protected abstract PlaceholderFile getDefaultOutputFile();

  /**
   * Sets the output file.
   *
   * @param value	the file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output file.
   *
   * @return		the file
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
  public abstract String outputFileTipText();

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "outputFile", m_OutputFile, "output: ");
  }

  /**
   * Hook method for performing checks before generating the output.
   *
   * @param superclass 	the superclass this listing is for
   * @param listing	the listing data to check
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(Class superclass, Map<String, List<String>> listing) {
    String	result;

    result = super.check(superclass, listing);

    if (result == null) {
      if (m_OutputFile.isDirectory())
        result = "Output file points to a directory: " + m_OutputFile;
    }

    return result;
  }
}
