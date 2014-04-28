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
 * ThirdPartyCopyrightProcessor.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.core.annotation;

import javax.annotation.processing.SupportedOptions;

/**
 * A processor that lists all classes/methods with 3rd party copyright.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see MixedCopyright
 */
@SupportedOptions(
    {"module", "output", "printheader"}
)
public class ThirdPartyCopyrightProcessor 
  extends AbstractCopyrightProcessor<ThirdPartyCopyright> {
  
  /**
   * Returns the annotation class to process.
   * 
   * @return		the class
   */
  protected Class getAnnotationClass() {
    return ThirdPartyCopyright.class;
  }
  
  /**
   * Returns the output file to write the information to.
   * 
   * @param prefix	the path/filename prefix
   * @return		the full path/filename
   */
  protected String getOutputFile(String prefix) {
    return prefix + "-copyright-3rdparty.txt";
  }

  /**
   * Returns the header row for the tab-separated output file.
   * 
   * @return		the header row
   */
  protected String getHeaderRow() {
    return
	  "Module"
	  + "\t"
	  + "Class"
	  + "\t"
	  + "Method"
	  + "\t"
	  + "Copyright"
	  + "\t"
	  + "Author"
	  + "\t"
	  + "License"
	  + "\t"
	  + "URL"
	  + "\t"
	  + "Note";
  }
  
  /**
   * Returns a data row for a specific annotation.
   * 
   * @param copyright	the copyright annotation to use
   * @param module	the module to use
   * @param cls		the class the annotation is located in
   * @param method	the method the annotation is located in, empty string if not applicable
   */
  protected String getDataRow(ThirdPartyCopyright copyright, String module, String cls, String method) {
    return
	  module
	  + "\t"
	  + cls
	  + "\t"
	  + method
	  + "\t"
	  + copyright.copyright()
	  + "\t"
	  + copyright.author()
	  + "\t"
	  + copyright.license()
	  + "\t"
	  + copyright.url()
	  + "\t"
	  + copyright.note();
  }
}