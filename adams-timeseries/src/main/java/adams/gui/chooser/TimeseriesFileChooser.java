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
 * TimeseriesFileChooser.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import java.io.File;

import adams.data.io.input.AbstractDataContainerReader;
import adams.data.io.input.AbstractTimeseriesReader;
import adams.data.io.input.SimpleTimeseriesReader;
import adams.data.io.output.AbstractDataContainerWriter;
import adams.data.io.output.AbstractTimeseriesWriter;
import adams.data.io.output.SimpleTimeseriesWriter;
import adams.data.timeseries.Timeseries;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for timeseries.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesFileChooser
  extends AbstractDataContainerFileChooser<Timeseries, AbstractDataContainerReader<Timeseries>, AbstractDataContainerWriter<Timeseries>> {

  /** for serialization. */
  private static final long serialVersionUID = -5373058011025481738L;

  /**
   * Constructs a FileChooser pointing to the user's default directory.
   */
  public TimeseriesFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  public TimeseriesFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  public TimeseriesFileChooser(String currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  @Override
  protected AbstractDataContainerReader<Timeseries> getDefaultReader() {
    return new SimpleTimeseriesReader();
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected AbstractDataContainerWriter<Timeseries> getDefaultWriter() {
    return new SimpleTimeseriesWriter();
  }

  /**
   * Returns the reader superclass for the GOE.
   *
   * @return		the reader class
   */
  @Override
  protected Class getReaderClass() {
    return AbstractTimeseriesReader.class;
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  @Override
  protected Class getWriterClass() {
    return AbstractTimeseriesWriter.class;
  }

  /**
   * Performs the actual initialization of the filters.
   */
  @Override
  protected void doInitializeFilters() {
    initFilters(this, true, AbstractTimeseriesReader.getReaders());
    initFilters(this, false, AbstractTimeseriesWriter.getWriters());
  }
}
