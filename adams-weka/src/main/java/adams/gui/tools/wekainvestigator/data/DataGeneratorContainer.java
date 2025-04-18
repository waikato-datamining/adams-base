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
 * DataGeneratorContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.data;

import adams.core.option.OptionUtils;
import weka.core.Instances;
import weka.datagenerators.DataGenerator;

import java.io.Serializable;
import java.util.logging.Level;

/**
 * Dataset generated by datagenerator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataGeneratorContainer
  extends AbstractDataContainer {

  private static final long serialVersionUID = 6267905940957451551L;

  /** the generator used to load the data. */
  protected DataGenerator m_Generator;

  /**
   * Loads the data using the specified loader.
   *
   * @param generator	the generator to use
   */
  public DataGeneratorContainer(DataGenerator generator) {
    super();
    try {
      generator.defineDataFormat();
      m_Data      = generator.generateExamples();
      m_Generator = generator;
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Failed to generate dataset: " + OptionUtils.getCommandLine(generator), e);
    }
  }

  /**
   * Returns the source of the data item.
   *
   * @return		the source
   */
  @Override
  public String getSource() {
    if (m_Generator == null)
      return "<unknown>";
    else
      return OptionUtils.getCommandLine(m_Generator);
  }

  /**
   * Whether it is possible to reload this item.
   *
   * @return		true if reloadable
   */
  @Override
  public boolean canReload() {
    return (m_Generator != null);
  }

  /**
   * Reloads the data.
   *
   * @return		null if successfully reloaded, otherwise error message
   */
  @Override
  protected String doReload() {
    try {
      m_Generator.defineDataFormat();
      m_Data = m_Generator.generateExamples();
      return null;
    }
    catch (Exception e) {
      return handleException("Failed to regenerate data: " + OptionUtils.getCommandLine(m_Generator), e);
    }
  }

  /**
   * Returns the data to store in the undo.
   *
   * @return		the undo point
   */
  protected Serializable[] getUndoData() {
    return new Serializable[]{
      m_Data,
      m_Modified,
      m_Generator
    };
  }

  /**
   * Restores the data from the undo point.
   *
   * @param data	the undo point
   */
  protected void applyUndoData(Serializable[] data) {
    m_Data      = (Instances) data[0];
    m_Modified  = (Boolean) data[1];
    m_Generator = (DataGenerator) data[2];
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    super.cleanUp();
    m_Generator = null;
  }
}
