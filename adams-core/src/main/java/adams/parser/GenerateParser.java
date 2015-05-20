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
 * GenerateParser.java
 * Copyright (C) 2010-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.parser;

import java.io.File;

/**
 * Generates Java code from the Parser.cup and Scanner.jflex files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GenerateParser {

  /**
   * Runs the generator from command-line.
   * <br><br>
   * Expects one argument: directory that that contains the Scanner.jflex and 
   *                       Parser.cup files
   *
   * @param args	the command-line options
   * @throws Exception  if anything goes wrong
   */
  public static void main(String[] args) throws Exception {
    // test arguments
    if (args.length < 1) {
      System.err.println();
      System.err.println("No directory provided!");
      System.err.println();
      System.err.println("Usage: " + GenerateParser.class.getClass().getName() + " <parser-dir> [<parser-dir> ...]");
      System.exit(1);
    }
    for (int i = 0; i < args.length; i++) {
      File dir = new File(args[i]);
      if (!dir.exists()) {
        System.err.println();
        System.err.println("Directory does not exist: " + dir);
        System.err.println();
        System.err.println("Usage: " + GenerateParser.class.getClass().getName() + " <parser-dir>");
        System.exit(2);
      }

      System.out.println("Generating parser: " + dir);

      String[] options;

      // JFlex
      options = new String[]{
        "--jlex",
        "--quiet",
        "--nobak",
        "--outdir",
        dir.getAbsolutePath().replace("resources", "java"),
        dir.getAbsolutePath() + "/Scanner.jflex"
      };
      JFlex.Main.main(options);
      
      // java-cup
      options = new String[]{
        "-parser",
        "Parser",
        "-interface",
        "-nosummary",
        "-destdir",
        dir.getAbsolutePath().replace("resources", "java"),
        dir.getAbsolutePath() + "/Parser.cup"
      };
      java_cup.Main.main(options);
    }
  }
}
