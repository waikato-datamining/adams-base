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
 * DataContainerToByteArray.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.ObjectCopyHelper;
import adams.data.container.AbstractDataContainer;
import adams.data.container.DataContainer;
import adams.data.container.DataPoint;
import adams.data.container.DataPointComparator;
import adams.data.io.output.AbstractDataContainerWriter;
import adams.data.io.output.StreamableBinaryDataContainerWriter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses the specified writer to generate a byte array from the incoming data container.
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
 * <pre>-writer &lt;adams.data.io.output.StreamableBinaryDataContainerWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for turning the data container into a byte array.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.DataContainerToByteArray$DummyWriter
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DataContainerToByteArray
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
   * Dummy writer.
   */
  public static class DummyWriter
    extends AbstractDataContainerWriter<DummyContainer>
    implements StreamableBinaryDataContainerWriter<DummyContainer> {
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
    public boolean canWriteMultiple() {
      return false;
    }
    @Override
    protected boolean writeData(List<DummyContainer> data) {
      return false;
    }
    @Override
    public boolean write(OutputStream stream, DummyContainer data) {
      return false;
    }
    @Override
    public boolean write(OutputStream stream, List<DummyContainer> data) {
      return false;
    }
  }

  /** the data container writer to use. */
  protected StreamableBinaryDataContainerWriter m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified writer to generate a byte array from the incoming data container.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "writer", "writer",
      new DummyWriter());
  }

  /**
   * Sets the writer to use.
   *
   * @param value	the writer
   */
  public void setWriter(StreamableBinaryDataContainerWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer to use.
   *
   * @return 		the writer
   */
  public StreamableBinaryDataContainerWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for turning the data container into a byte array.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return DataContainer.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return byte[].class;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    StreamableBinaryDataContainerWriter		writer;
    ByteArrayOutputStream 			bos;

    writer = ObjectCopyHelper.copyObject(m_Writer);
    bos    = new ByteArrayOutputStream();
    writer.write(bos, (DataContainer) m_Input);
    return bos.toByteArray();
  }
}
