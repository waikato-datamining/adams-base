/*
 * A simple Groovy conversion scheme that divides incoming numbers by 100.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */

import adams.data.conversion.AbstractScript

class SimpleConversion
  extends AbstractScript {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Just divides the incoming numbers by 100."
  }

  /**
   * Returns the class of objects that it accepts.
   *
   * @return		Double.class
   */
  public Class accepts() {
    return Double.class
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		Double.class
   */
  public Class generates() {
    return Double.class
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    return m_Input / 100
  }
}
