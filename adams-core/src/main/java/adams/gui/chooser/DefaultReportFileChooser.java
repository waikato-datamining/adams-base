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
 * DefaultReportFileChooser.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.ClassLister;
import adams.data.io.input.AbstractReportReader;
import adams.data.io.input.DefaultSimpleCSVReportReader;
import adams.data.io.output.AbstractReportWriter;
import adams.data.io.output.DefaultSimpleCSVReportWriter;
import adams.data.report.Report;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for sampledata reports.
 * <br><br>
 * Based on <code>weka.gui.ConverterFileChooser</code>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see	    weka.gui.ConverterFileChooser
 */
public class DefaultReportFileChooser
  extends AbstractReportFileChooser<Report, AbstractReportReader, AbstractReportWriter> {

  /** for serialization. */
  private static final long serialVersionUID = -53374407938356183L;

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  @Override
  protected AbstractReportReader<Report> getDefaultReader() {
    return new DefaultSimpleCSVReportReader();
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected AbstractReportWriter<Report> getDefaultWriter() {
    return new DefaultSimpleCSVReportWriter();
  }

  /**
   * Performs the actual initialization of the filters.
   */
  @Override
  protected void doInitializeFilters() {
    initFilters(this, true, ClassLister.getSingleton().getClassnames(AbstractReportReader.class));
    initFilters(this, false, ClassLister.getSingleton().getClassnames(AbstractReportWriter.class));
  }
}
