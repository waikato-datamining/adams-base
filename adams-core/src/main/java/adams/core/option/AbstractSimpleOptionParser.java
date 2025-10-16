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
 * AbstractSimpleOptionParser.java
 * Copyright (C) 2021-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

import adams.core.logging.LoggingObject;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Ancestor for simple option parser objects.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSimpleOptionParser
  extends LoggingObject
  implements SimpleOptionParser {

  private static final long serialVersionUID = 7095472634480910487L;

  /**
   * Configures and returns the commandline parser.
   *
   * @return		the parser
   */
  @Override
  public ArgumentParser getParser() {
    ArgumentParser 	parser;

    parser = ArgumentParsers.newFor(getClass().getName()).build();
    parser.description(globalInfo());

    return parser;
  }

  /**
   * Sets the parsed options.
   *
   * @param ns		the parsed options
   * @return		if successfully set
   */
  protected boolean setOptions(Namespace ns) {
    return true;
  }

  /**
   * Sets the commandline options.
   *
   * @param options	the options to use
   * @return		true if successful
   * @throws Exception	in case of an invalid option
   */
  @Override
  public boolean setOptions(String[] options) throws Exception {
    ArgumentParser 	parser;
    Namespace 		ns;

    parser = getParser();
    try {
      ns = parser.parseArgs(options);
    }
    catch (ArgumentParserException e) {
      parser.handleError(e);
      return false;
    }

    return setOptions(ns);
  }

  /**
   * Instantiates the option parser from the commandline.
   *
   * @param cmdline	the commandline to use
   * @return		the instantiated object
   * @throws Exception	if instantiation/parsing fails
   */
  public static SimpleOptionParser forCommandline(String cmdline) throws Exception {
    SimpleOptionParser	result;
    String[] 		options;
    String		classname;
    String[]		tmpOptions;

    options = OptionUtils.splitOptions(cmdline);
    classname = options[0];
    tmpOptions = new String[options.length - 1];
    System.arraycopy(options, 0, tmpOptions, 0, tmpOptions.length);
    result = (SimpleOptionParser) Class.forName(classname).getDeclaredConstructor().newInstance();
    result.setOptions(tmpOptions);
    return result;
  }
}
