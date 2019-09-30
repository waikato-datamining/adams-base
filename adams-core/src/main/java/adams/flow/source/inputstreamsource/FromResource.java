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
 * FromResource.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.source.inputstreamsource;

import adams.core.QuickInfoHelper;

import java.io.InputStream;

/**
 * Generates an input stream from the specified resource (eg from jar).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FromResource
  extends AbstractInputStreamGenerator {

  private static final long serialVersionUID = -4372049990967649532L;

  /** the resource to load. */
  protected String m_Resource;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates an input stream from the specified resource (eg from jar).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "resource", "resource",
      "");
  }

  /**
   * Sets the resource to load.
   *
   * @param value	the resource
   */
  public void setResource(String value) {
    m_Resource = value;
    reset();
  }

  /**
   * Returns the resource to load.
   *
   * @return		the resource
   */
  public String getResource() {
    return m_Resource;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String resourceTipText() {
    return "The resource to load.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "resource", m_Resource, "resource: ");
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
      if (m_Resource.isEmpty())
        result = "Resource is empty!";
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
  protected InputStream doGenerate() throws Exception {
    return ClassLoader.getSystemResourceAsStream(m_Resource);
  }
}
