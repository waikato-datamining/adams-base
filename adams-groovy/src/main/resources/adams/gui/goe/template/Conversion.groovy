/*
 * Template of a Groovy conversion scheme.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */

import adams.data.conversion.AbstractScript

class TemplateConversion
  extends AbstractScript {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "FIXME."
  }

  /**
   * Returns the class of objects that it accepts.
   *
   * @return		Double.class
   */
  public Class accepts() {
    return Object.class  // FIXME
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		Double.class
   */
  public Class generates() {
    return Object.class  // FIXME
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    // FIXME
    return m_Input
  }
}
