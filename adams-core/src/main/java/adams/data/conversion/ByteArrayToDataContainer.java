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
 * ByteArrayToDataContainer.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.ObjectCopyHelper;
import adams.data.container.AbstractDataContainer;
import adams.data.container.DataPoint;
import adams.data.container.DataPointComparator;
import adams.data.io.input.AbstractDataContainerReader;
import adams.data.io.input.StreamableBinaryDataContainerReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses the specified reader to generate data container(s) from the incoming byte array.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-reader &lt;adams.data.io.input.StreamableBinaryDataContainerReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader to use for parsing the byte array and generating data container
 * &nbsp;&nbsp;&nbsp;(s).
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.ByteArrayToDataContainer$DummyReader
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ByteArrayToDataContainer
  extends AbstractConversion {

  private static final long serialVersionUID = -6065755000892576919L;

  /**
   * Dummy container.
   */
  public static class DummyContainer extends AbstractDataContainer {
    private static final long serialVersionUID = 8045373760287481482L;
    @Override
    public void mergeWith(Object other) {
    }
    @Override
    public DataPointComparator newComparator() {
      return null;
    }
    @Override
    public DataPointComparator getComparator() {
      return null;
    }
    @Override
    public DataPoint newPoint() {
      return null;
    }
    @Override
    public boolean add(Object o) {
      return false;
    }
  }

  /**
   * Dummy reader.
   */
  public static class DummyReader
    extends AbstractDataContainerReader<DummyContainer>
    implements StreamableBinaryDataContainerReader<DummyContainer> {
    private static final long serialVersionUID = -8253173114045248043L;
    @Override
    public String globalInfo() {
      return "";
    }
    @Override
    public String getFormatDescription() {
      return "";
    }
    @Override
    public String[] getFormatExtensions() {
      return new String[]{"*"};
    }
    @Override
    protected void readData() {
    }
    @Override
    public List<DummyContainer> read(InputStream input) {
      return null;
    }
  }

  /** the data container reader to use. */
  protected StreamableBinaryDataContainerReader m_Reader;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified reader to generate data container(s) from the incoming byte array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "reader", "reader",
      new DummyReader());
  }

  /**
   * Sets the reader to use.
   *
   * @param value	the reader
   */
  public void setReader(StreamableBinaryDataContainerReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader to use.
   *
   * @return 		the reader
   */
  public StreamableBinaryDataContainerReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader to use for parsing the byte array and generating data container(s).";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return byte[].class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return List.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    StreamableBinaryDataContainerReader	reader;
    ByteArrayInputStream			bis;

    reader = ObjectCopyHelper.copyObject(m_Reader);
    bis    = new ByteArrayInputStream((byte[]) m_Input);
    return reader.read(bis);
  }
}
