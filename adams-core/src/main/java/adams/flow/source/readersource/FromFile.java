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
 * FromFile.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.readersource;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;

import java.io.FileReader;
import java.io.Reader;

/**
 * Generates a reader from the specified file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FromFile
  extends AbstractReaderGenerator {

  private static final long serialVersionUID = -4372049990967649532L;

  /** the file to load. */
  protected PlaceholderFile m_Source;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a reader from the specified file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "source", "source",
      new PlaceholderFile());
  }

  /**
   * Sets the source file.
   *
   * @param value	the file
   */
  public void setSource(PlaceholderFile value) {
    m_Source = value;
    reset();
  }

  /**
   * Returns the source file.
   *
   * @return		the file
   */
  public PlaceholderFile getSource() {
    return m_Source;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "The file to use as source.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "source", m_Source, "source: ");
  }

  /**
   * Hook method for checks.
   *
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String check() {
    String	result;

    result = super.check();

    if (result == null) {
      if (!m_Source.exists())
        result = "Source file does not exist: " + m_Source;
      else if (m_Source.isDirectory())
        result = "Source file points to a directory: " + m_Source;
    }

    return result;
  }

  /**
   * Generates the InputStream instance.
   *
   * @return		the stream
   * @throws Exception	if generation fails
   */
  @Override
  protected Reader doGenerate() throws Exception {
    return new FileReader(m_Source.getAbsolutePath());
  }
}
