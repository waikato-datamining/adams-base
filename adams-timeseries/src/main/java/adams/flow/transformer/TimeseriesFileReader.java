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
 * TimeseriesFileReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.io.input.AbstractDataContainerReader;
import adams.data.io.input.SimpleTimeseriesReader;
import adams.data.timeseries.Timeseries;

/**
 <!-- globalinfo-start -->
 * Loads a file/directory containing spectrums from disk with a specified reader and passes them on.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * <pre>   java.lang.String</pre>
 * - generates:<br>
 * <pre>   knir.data.spectrum.Timeseries</pre>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 *         The name of the actor.
 *         default: TimeseriesFileReader
 * </pre>
 *
 * <pre>-annotation &lt;knir.core.base.BaseString&gt; [-annotation ...] (property: annotations)
 *         The annotations to attach to this actor.
 * </pre>
 *
 * <pre>-skip (property: skip)
 *         If set to true, transformation is skipped and the input token is just forwarded
 *          as it is.
 * </pre>
 *
 * <pre>-reader &lt;knir.data.input.AbstractTimeseriesReader [options]&gt; (property: reader)
 *         The reader to use for importing the spectrums.
 *         default: knir.data.input.SimpleTimeseriesReader -input .
 * </pre>
 *
 * Default options for knir.data.input.SimpleTimeseriesReader (-reader/reader):
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-input &lt;java.io.File&gt; (property: input)
 *         The file to read and turn into a spectrum.
 *         default: .
 * </pre>
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesFileReader
  extends AbstractDataContainerFileReader<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = 1429977151568224156L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Loads a file/directory containing timeseries from disk with a "
      + "specified reader and passes them on.";
  }

  /**
   * Returns the default reader to use.
   *
   * @return		the default reader
   */
  @Override
  protected AbstractDataContainerReader getDefaultReader() {
    return new SimpleTimeseriesReader();
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the data type
   */
  @Override
  public Class[] generates() {
    return new Class[]{Timeseries.class};
  }
}
