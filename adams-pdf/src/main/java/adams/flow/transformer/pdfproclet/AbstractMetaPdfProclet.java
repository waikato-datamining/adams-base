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
 * AbstractMetaPdfProclet.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.pdfproclet;

/**
 * Ancestor for proclets that wrap another base-proclet.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMetaPdfProclet
  extends AbstractPdfProclet {

  private static final long serialVersionUID = 4506139429272953505L;
  
  /** the base proclet to use. */
  protected PdfProclet m_Proclet;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "proclet", "proclet",
      new PlainText());
  }

  /**
   * Sets the proclet to use.
   *
   * @param value	the proclet
   */
  public void setProclet(PdfProclet value) {
    m_Proclet = value;
    reset();
  }

  /**
   * Returns the proclet to use.
   *
   * @return 		the proclet
   */
  public PdfProclet getProclet() {
    return m_Proclet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String procletTipText() {
    return "The base PDF proclet to use.";
  }
}
