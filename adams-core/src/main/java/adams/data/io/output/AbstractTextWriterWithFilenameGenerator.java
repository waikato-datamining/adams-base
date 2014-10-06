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
 * AbstractTextWriterWithFilenameGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.io.AbstractFilenameGenerator;
import adams.core.io.SimpleFilenameGenerator;

/**
 * Ancestor for text writers that use an {@link AbstractFilenameGenerator}
 * to generate their filename.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTextWriterWithFilenameGenerator
  extends AbstractTextWriterWithEncoding {

  /** for serialization. */
  private static final long serialVersionUID = -6137430023471487081L;

  /** the filename generator to use. */
  protected AbstractFilenameGenerator m_FilenameGenerator;

  /** whether to discard the provided name object. */
  protected boolean m_IgnoreName;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filename-generator", "filenameGenerator",
	    new SimpleFilenameGenerator());

    m_OptionManager.add(
	    "ignore-name", "ignoreName",
	    false);
  }

  /**
   * Sets the filename generator.
   *
   * @param value	the generator
   */
  public void setFilenameGenerator(AbstractFilenameGenerator value) {
    m_FilenameGenerator = value;
    reset();
  }

  /**
   * Returns the filename generator.
   *
   * @return		the generator
   */
  public AbstractFilenameGenerator getFilenameGenerator() {
    return m_FilenameGenerator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filenameGeneratorTipText() {
    return "The filename generator to use.";
  }

  /**
   * Sets whether to ignore the name and just rely on prefix/suffix to
   * generate the filename.
   *
   * @param value 	if true then the name of the content gets ignored
   */
  public void setIgnoreName(boolean value) {
    m_IgnoreName = value;
    reset();
  }

  /**
   * Returns whether the name of the content is ignored and the filename is
   * only generated based on prefix/suffix.
   *
   * @return 		true if the name of the content gets ignored
   */
  public boolean getIgnoreName() {
    return m_IgnoreName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String ignoreNameTipText() {
    return
        "If set to true, then the name of the content is ignored for "
      + "generating the filename (useful when prefix or suffix is based on "
      + "variables).";
  }

  /**
   * Creates the filename.
   *
   * @param name	the name of the content
   * @return		the generated filename
   */
  protected String createFilename(String name) {
    String	result;

    if (m_IgnoreName || (name == null))
      result = m_FilenameGenerator.generate("");
    else
      result = m_FilenameGenerator.generate(name);

    return result;
  }
}
