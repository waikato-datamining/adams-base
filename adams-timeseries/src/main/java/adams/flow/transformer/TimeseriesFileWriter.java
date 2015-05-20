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
 * TimeseriesFileWriter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.io.output.AbstractDataContainerWriter;
import adams.data.io.output.SimpleTimeseriesWriter;
import adams.data.timeseries.Timeseries;

/**
 <!-- globalinfo-start -->
 * Saves a spectrum to disk with the specified writer and passes the absolute filename on.<br>
 * As filename/directory name (depending on the writer) the database ID of the spectrum is used (below the specified output directory).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * <pre>   knir.data.spectrum.Timeseries</pre>
 * - generates:<br>
 * <pre>   java.lang.String</pre>
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
 *         default: TimeseriesFileWriter
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
 * <pre>-writer &lt;knir.data.output.AbstractTimeseriesWriter [options]&gt; (property: writer)
 *         The writer to use for saving the spectrums.
 *         default: knir.data.output.SimpleTimeseriesWriter -output /tmp/out.spec
 * </pre>
 *
 * <pre>-dir &lt;java.io.File&gt; (property: outputDir)
 *         The output directory for the spectrums.
 *         default: .
 * </pre>
 *
 * Default options for knir.data.output.SimpleTimeseriesWriter (-writer/writer):
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-output &lt;java.io.File&gt; (property: output)
 *         The file to write the spectrum to.
 *         default: /tmp/out.spec
 * </pre>
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesFileWriter
  extends AbstractDataContainerFileWriter<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = -7990944411836957831L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Saves a timeseries to disk with the "
      + "specified writer and passes the absolute filename on.\n"
      + "As filename/directory name (depending on the writer) the "
      + "database ID of the spectrum is used (below the specified output "
      + "directory).";
  }

  /**
   * Returns the default writer to use.
   *
   * @return		the default writer
   */
  @Override
  protected AbstractDataContainerWriter<Timeseries> getDefaultWriter() {
    return new SimpleTimeseriesWriter();
  }

  /**
   * Returns the data container class in use.
   *
   * @return		the container class
   */
  @Override
  protected Class getDataContainerClass() {
    return Timeseries.class;
  }
}
