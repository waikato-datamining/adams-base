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
 * FileSizeChange.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileuse;

import adams.core.Utils;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Checks the file size before and after the specified wait interval. If the sizes differ, then the file is considered to be in use.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-min-size &lt;long&gt; (property: minSize)
 * &nbsp;&nbsp;&nbsp;The minimum size that the file must have; below is considered in use.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-interval &lt;int&gt; (property: interval)
 * &nbsp;&nbsp;&nbsp;The wait interval in msec between before&#47;after file size checks.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileSizeChange
  extends AbstractFileUseCheck {

  private static final long serialVersionUID = -3766862011655514895L;

  /** the minimum file size. */
  protected long m_MinSize;

  /** the interval in msec to wait. */
  protected int m_Interval;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Checks the file size before and after the specified wait interval. "
        + "If the sizes differ, then the file is considered to be in use.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "min-size", "minSize",
      1L, 1L, null);

    m_OptionManager.add(
      "interval", "interval",
      100, 1, null);
  }

  /**
   * Sets the minimum file size in bytes.
   *
   * @param value	the size
   */
  public void setMinSize(long value) {
    m_MinSize = value;
    reset();
  }

  /**
   * Returns the wait interval in msec.
   *
   * @return		the size
   */
  public long getMinSize() {
    return m_MinSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minSizeTipText() {
    return "The minimum size that the file must have; below is considered in use.";
  }

  /**
   * Sets the wait interval in msec.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    m_Interval = value;
    reset();
  }

  /**
   * Returns the wait interval in msec.
   *
   * @return		the interval
   */
  public int getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return "The wait interval in msec between before/after file size checks.";
  }

  /**
   * Checks whether the file is in use.
   *
   * @param file	the file to check
   * @return		true if in use
   */
  @Override
  public boolean isInUse(File file) {
    File 	fileTest;
    long	sizeBefore;
    long	sizeAfter;

    fileTest   = new File(file.getAbsolutePath());
    sizeBefore = fileTest.length();
    Utils.wait(this, m_Interval, 10);
    fileTest   = new File(file.getAbsolutePath());
    sizeAfter  = fileTest.length();

    return (sizeBefore != sizeAfter) || (sizeBefore < m_MinSize) || (sizeAfter < m_MinSize);
  }
}
