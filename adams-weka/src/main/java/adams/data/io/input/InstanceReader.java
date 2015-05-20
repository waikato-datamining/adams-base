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
 * InstanceReader.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import java.util.logging.Level;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import adams.data.instance.Instance;

/**
 <!-- globalinfo-start -->
 * Reads WEKA datasets in various formats.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The file to read and turn into a container.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-create-dummy-report (property: createDummyReport)
 * &nbsp;&nbsp;&nbsp;If true, then a dummy report is created if none present.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstanceReader
  extends AbstractDataContainerReader<Instance>
  implements IncrementalDataContainerReader {

  /** for serialization. */
  private static final long serialVersionUID = 2653822253818697195L;

  /** the data source for reading. */
  protected DataSource m_Source;

  /** the current data structure. */
  protected Instances m_Structure;

  /** the current index. */
  protected int m_Index;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads WEKA datasets in various formats.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "WEKA datasets";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{
	weka.core.converters.ArffLoader.FILE_EXTENSION,
	weka.core.converters.ArffLoader.FILE_EXTENSION_COMPRESSED,
	weka.core.converters.CSVLoader.FILE_EXTENSION,
	weka.core.converters.CSVLoader.FILE_EXTENSION_COMPRESSED
    };
  }

  /**
   * Resets the filter.
   */
  @Override
  public void reset() {
    super.reset();

    m_Source    = null;
    m_Structure = null;
    m_Index     = 0;
  }

  /**
   * Uses the named setup to read the data.
   */
  @Override
  protected void readData() {
    Instance	cont;

    if (m_Source == null) {
      try {
	m_Index     = 0;
	m_Source    = new DataSource(getInput().getAbsolutePath());
	m_Structure = m_Source.getStructure();
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Error reading '" + getInput() + "':", e);
	m_Source    = null;
	m_Structure = null;
      }
    }

    if (m_Source != null) {
      m_Index++;
      cont = new Instance();
      cont.set(m_Source.nextElement(m_Structure));
      cont.setID(m_Index + "." + m_Structure.relationName());
      m_ReadData.add(cont);
    }
  }

  /**
   * Returns whether there is more data available.
   *
   * @return		true if there is more data available
   */
  public boolean hasMoreData() {
    return (m_Source != null) && m_Source.hasMoreElements(m_Structure);
  }
}
