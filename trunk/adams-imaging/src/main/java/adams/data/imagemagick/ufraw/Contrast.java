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
 * Contrast.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagemagick.ufraw;

import org.im4java.core.UFRawOperation;

import adams.core.QuickInfoHelper;

/**
 <!-- globalinfo-start -->
 * Changes the contrast using the specified level.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-contrast &lt;double&gt; (property: contrast)
 * &nbsp;&nbsp;&nbsp;The contrast level to use.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Contrast
  extends AbstractUfrawSimpleOperation {

  /** for serialization. */
  private static final long serialVersionUID = 3529048936510645338L;

  /** the factor to divide the white contrast by. */
  protected double m_Contrast;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Changes the contrast using the specified level.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "contrast", "contrast",
	    1.0);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "contrast", m_Contrast, "contrast: ");
  }

  /**
   * Sets the contrast level.
   *
   * @param value	the contrast
   */
  public void setContrast(double value) {
    m_Contrast = value;
    reset();
  }

  /**
   * Returns the contrast level.
   *
   * @return		the contrast
   */
  public double getContrast() {
    return m_Contrast;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String contrastTipText() {
    return "The contrast level to use.";
  }

  /**
   * Adds the operation.
   * 
   * @param op		the operation object to update
   */
  @Override
  protected void addOperation(UFRawOperation op) {
    op.contrast(m_Contrast);
  }
}
