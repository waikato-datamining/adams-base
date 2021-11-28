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
 * ExternalResourceClassPathAugmenter.java
 * Copyright (C) 2013-2021 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.type.FileArgumentType;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows the user to add external jars and directories.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ExternalResourceClassPathAugmenter
    extends AbstractClassPathAugmenter {

  /** for serialization. */
  private static final long serialVersionUID = 3380177345627628995L;

  /** the jars/zips to add to the classpath. */
  protected PlaceholderFile[] m_Files;

  /** the directories with classes to add to the classpath. */
  protected PlaceholderDirectory[] m_Directories;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to add external jars and directories.";
  }

  /**
   * Configures and returns the commandline parser.
   *
   * @return		the parser
   */
  @Override
  public ArgumentParser getParser() {
    ArgumentParser 	parser;

    parser = super.getParser();

    parser.addArgument("--file")
	.setDefault(new ArrayList<String>())
	.dest("files")
	.action(Arguments.append())
	.type(FileArgumentType.class)
	.help(filesTipText());

    parser.addArgument("--dir")
	.setDefault(new ArrayList<String>())
	.dest("dirs")
	.action(Arguments.append())
	.type(FileArgumentType.class)
	.help(directoriesTipText());

    return parser;
  }

  /**
   * Sets the parsed options.
   *
   * @param ns		the parsed options
   * @return		if successfully set
   */
  protected boolean setOptions(Namespace ns) {
    List<PlaceholderFile>	files;
    List<PlaceholderDirectory>	dirs;
    List<File>			list;

    files = new ArrayList<>();
    list = ns.getList("files");
    for (File f: list)
      files.add(new PlaceholderFile(f));
    setFiles(files.toArray(new PlaceholderFile[0]));

    dirs = new ArrayList<>();
    list = ns.getList("dirs");
    for (File f: list)
      dirs.add(new PlaceholderDirectory(f));
    setDirectories(dirs.toArray(new PlaceholderDirectory[0]));

    return true;
  }

  /**
   * Sets the jar/zip files to add to the classpath.
   *
   * @param value	the jar/zip files
   */
  public void setFiles(PlaceholderFile[] value) {
    m_Files = value;
  }

  /**
   * Returns the jar/zip files to add to the classpath.
   *
   * @return		the jar/zip files
   */
  public PlaceholderFile[] getFiles() {
    return m_Files;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filesTipText() {
    return "The jar/zip files to add to the classpath.";
  }

  /**
   * Sets the directories to add to the classpath.
   *
   * @param value	the directories
   */
  public void setDirectories(PlaceholderDirectory[] value) {
    m_Directories = value;
  }

  /**
   * Returns the directories to add to the classpath.
   *
   * @return		the directories
   */
  public PlaceholderDirectory[] getDirectories() {
    return m_Directories;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String directoriesTipText() {
    return "The directories with classes to add to the classpath.";
  }

  /**
   * Returns the classpath parts (jars, directories) to add to the classpath.
   *
   * @return		the additional classpath parts
   */
  public String[] getClassPathAugmentation() {
    List<String>	result;

    result = new ArrayList<>();

    for (PlaceholderFile file: m_Files)
      result.add(file.getAbsolutePath());
    for (PlaceholderDirectory dir: m_Directories)
      result.add(dir.getAbsolutePath());

    return result.toArray(new String[0]);
  }
}
