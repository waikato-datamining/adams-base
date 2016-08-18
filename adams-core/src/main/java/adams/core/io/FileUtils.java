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
 * FileUtils.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io;

import adams.core.License;
import adams.core.Placeholders;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import adams.core.management.OS;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for I/O related actions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileUtils {

  /** the properties file. */
  public final static String FILENAME = "adams/core/io/FileUtils.props";

  /** the properties. */
  protected static Properties m_Properties;

  /** valid characters for filenames. */
  protected static String FILENAME_CHARS;

  /** the maximum length for an extension. */
  protected static Integer MAX_EXTENSION_LENGTH;
  
  /** the ignored extension suffixes. */
  protected static String[] IGNORED_EXTENSION_SUFFIXES;
  
  /** the length of the buffer for binary checks. */
  public final static int BINARY_CHECK_BUFFER_SIZE = 1024;

  /**
   * Returns the properties.
   *
   * @return		the properties
   */
  protected static synchronized Properties getProperties() {
    Properties	result;

    if (m_Properties == null) {
      try {
	result = Properties.read(FILENAME);
      }
      catch (Exception e) {
	result = new Properties();
      }
      m_Properties = result;
    }

    return m_Properties;
  }

  /**
   * Returns the characters that can be used in filenames.
   * 
   * @return		the characters (in single string)
   */
  public static synchronized String getFileNameChars() {
    if (FILENAME_CHARS == null)
      FILENAME_CHARS = getProperties().getProperty("FileNameChars", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-.,;()#@");
    return FILENAME_CHARS;
  }

  /**
   * Returns the maximum length of extensions.
   * 
   * @return		the max length
   */
  public static synchronized int getMaxExtensionLength() {
    if (MAX_EXTENSION_LENGTH == null)
      MAX_EXTENSION_LENGTH = getProperties().getInteger("MaxExtensionLength", 6);
    return MAX_EXTENSION_LENGTH;
  }

  /**
   * Returns the extension suffixes that get ignored in determining a file's 
   * extension. Examples are "gz" or "bz2".
   * 
   * @return		the suffixes
   */
  public static synchronized String[] getIgnoredExtensionSuffixes() {
    if (IGNORED_EXTENSION_SUFFIXES == null)
      IGNORED_EXTENSION_SUFFIXES = getProperties().getProperty("IgnoredExtensionSuffixes", "7z,bz2,gz").replaceAll(" ", "").split(",");
    return IGNORED_EXTENSION_SUFFIXES;
  }

  /**
   * Removes byte order marks (BOMs) from the start of the string (if present).
   * For UTF-16 and UTF-32.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  public static String removeByteOrderMarks(String s) {
    if (s.length() >= 2) {
      // UTF-16 little endian
      if (s.startsWith("\uFFFE"))
	return s.substring(1);
      // UTF-16 big endian
      if (s.startsWith("\uFEFF"))
	return s.substring(1);
    }
    if (s.length() >= 4) {
      // UTF-32 little endian
      if (s.startsWith("\uFFFE\u0000"))
	return s.substring(2);
      // UTF-32 big endian
      if (s.startsWith("\u0000\uFEFF"))
	return s.substring(2);
    }
    return s;
  }

  /**
   * Returns the content of the given file, null in case of an error.
   *
   * @param file	the file to load
   * @return		the content/lines of the file
   */
  public static List<String> loadFromFile(File file) {
    return loadFromFile(file, null);
  }

  /**
   * Returns the content of the given file, null in case of an error.
   *
   * @param file	the file to load
   * @param encoding	the encoding to use, null to use default
   * @return		the content/lines of the file, null in case of an error
   */
  public static List<String> loadFromFile(File file, String encoding) {
    List<String>	result;

    try {
      if (encoding == null)
	result = Files.readAllLines(file.toPath());
      else
	result = Files.readAllLines(file.toPath(), Charset.forName(encoding));
      // remove byte order marks
      if (result.size() > 0)
	result.set(0, removeByteOrderMarks(result.get(0)));
      return result;
    }
    catch (Exception e) {
      System.err.println("Failed to read lines from '" + file + "':");
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Loads the binary file.
   *
   * @param file	the file to load
   * @return		the binary content, null in case of an error
   */
  public static byte[] loadFromBinaryFile(File file) {
    try {
      return Files.readAllBytes(file.toPath());
    }
    catch (Exception e) {
      System.err.println("Failed to read bytes from '" + file + "':");
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Loads the binary file and returns the hexadecimal representation.
   * Uses 16 columns.
   *
   * @param file	the file to load
   * @return		the binary content as hex, null in case of an error
   */
  public static String loadHexFromBinaryFile(File file) {
    return loadHexFromBinaryFile(file, 16);
  }

  /**
   * Loads the binary file and returns the hexadecimal representation.
   *
   * @param file	the file to load
   * @param columns	the number of columns to generate
   * @return		the binary content as hex, null in case of an error
   */
  public static String loadHexFromBinaryFile(File file, int columns) {
    byte[]		binary;
    StringBuilder	hex;
    StringBuilder	human;
    int			width;
    int			i;

    binary = loadFromBinaryFile(file);
    if (binary == null)
      return null;

    width  = ("" + binary.length).length();
    hex    = new StringBuilder(binary.length * 5);
    human  = new StringBuilder();

    for (i = 0; i < binary.length; i++) {
      if (i % columns == 0) {
	if (i > 0) {
	  hex.append(" | ");
	  hex.append(human.toString());
	  hex.append("\n");
	  human.delete(0, human.length());
	}
	hex.append(Utils.padLeft("" + (i+1), '0', width));
	hex.append("-");
	hex.append(Utils.padLeft("" + (i+columns), '0', width));
	hex.append(" |");
      }

      hex.append(" ");
      hex.append(Utils.toHex(binary[i]));
      if (binary[i] > 31)
	human.append((char) binary[i]);
      else
	human.append(".");
    }

    return hex.toString();
  }

  /**
   * Saves the content to the given file.
   *
   * @param content	the content to save
   * @param file	the file to save the content to
   * @return		true if successfully saved
   */
  public static boolean saveToFile(String[] content, File file) {
    List<String>	lines;
    int			i;

    lines = new ArrayList<String>();
    for (i = 0; i < content.length; i++)
      lines.add(content[i]);

    return FileUtils.saveToFile(lines, file);
  }

  /**
   * Saves the content to the given file.
   *
   * @param content	the content to save
   * @param file	the file to save the content to
   * @return		true if successfully saved
   */
  public static boolean saveToFile(List<String> content, File file) {
    return saveToFile(content, file, null);
  }

  /**
   * Saves the content to the given file.
   *
   * @param content	the content to save
   * @param file	the file to save the content to
   * @param encoding	the encoding to use, null for default
   * @return		true if successfully saved
   */
  public static boolean saveToFile(List<String> content, File file, String encoding) {
    return (saveToFileMsg(content, file, encoding) == null);
  }

  /**
   * Saves the content to the given file.
   *
   * @param content	the content to save
   * @param file	the file to save the content to
   * @param encoding	the encoding to use, null for default
   * @return		true if successfully saved
   */
  public static String saveToFileMsg(List<String> content, File file, String encoding) {
    String		result;

    result = null;

    try {
      if (encoding == null)
	Files.write(file.toPath(), content);
      else
	Files.write(file.toPath(), content, Charset.forName(encoding));
    }
    catch (Exception e) {
      result = "Failed to save to '" + file + "':\n" + Utils.throwableToString(e);
    }

    return result;
  }

  /**
   * Writes the given object to the specified file. The object is always
   * appended.
   *
   * @param filename	the file to write to
   * @param obj		the object to write
   * @return		true if writing was successful
   */
  public static boolean writeToFile(String filename, Object obj) {
    return writeToFile(filename, obj, null);
  }

  /**
   * Writes the given object to the specified file. The object is always
   * appended.
   *
   * @param filename	the file to write to
   * @param obj		the object to write
   * @param encoding	the encoding to use, null for default
   * @return		true if writing was successful
   */
  public static boolean writeToFile(String filename, Object obj, String encoding) {
    return writeToFile(filename, obj, true, encoding);
  }

  /**
   * Writes the given object to the specified file. The message is either
   * appended or replaces the current content of the file.
   *
   * @param filename	the file to write to
   * @param obj		the object to write
   * @param append	whether to append the message or not
   * @return		true if writing was successful
   */
  public static boolean writeToFile(String filename, Object obj, boolean append) {
    return writeToFile(filename, obj, append, null);
  }

  /**
   * Writes the given object to the specified file. The message is either
   * appended or replaces the current content of the file.
   *
   * @param filename	the file to write to
   * @param obj		the object to write
   * @param append	whether to append the message or not
   * @param encoding	the encoding to use, null for default
   * @return		true if writing was successful
   */
  public static boolean writeToFile(String filename, Object obj, boolean append, String encoding) {
    return (writeToFileMsg(filename, obj, append, encoding) == null);
  }

  /**
   * Writes the given object to the specified file. The message is either
   * appended or replaces the current content of the file.
   *
   * @param filename	the file to write to
   * @param obj		the object to write
   * @param append	whether to append the message or not
   * @param encoding	the encoding to use, null for default
   * @return		true if writing was successful
   */
  public static String writeToFileMsg(String filename, Object obj, boolean append, String encoding) {
    String			result;
    List<String>		lines;
    StandardOpenOption[]	options;

    result = null;
    lines = new ArrayList<>();
    lines.add("" + obj);
    if (append)
      options = new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE};
    else
      options = new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE};
    try {
      if (encoding == null)
	Files.write(new File(filename).toPath(), lines, options);
      else
	Files.write(new File(filename).toPath(), lines, Charset.forName(encoding), options);
    }
    catch (Exception e) {
      result = "Failed to write to '" + filename + "'\n" + Utils.throwableToString(e);
    }

    return result;
  }

  /**
   * Copies or moves files and directories (recursively).
   * If targetLocation does not exist, it will be created.
   * <br><br>
   * Original code from <a href="http://www.java-tips.org/java-se-tips/java.io/how-to-copy-a-directory-from-one-location-to-another-loc.html" target="_blank">Java-Tips.org</a>.
   *
   * @param sourceLocation	the source file/dir
   * @param targetLocation	the target file/dir
   * @param move		if true then the source files/dirs get deleted
   * 				as soon as copying finished
   * @param atomic		whether to perform an atomic move operation
   * @return			false if failed to delete when moving or failed to create target directory
   * @throws IOException	if copying/moving fails
   */
  public static boolean copyOrMove(File sourceLocation, File targetLocation, boolean move, boolean atomic) throws IOException {
    String[] 		children;
    int 		i;
    Path		source;
    Path 		target;

    if (sourceLocation.isDirectory()) {
      if (!targetLocation.exists()) {
	if (!targetLocation.mkdir())
	  return false;
      }

      children = sourceLocation.list();
      for (i = 0; i < children.length; i++) {
        if (!copyOrMove(
            new File(sourceLocation.getAbsoluteFile(), children[i]),
            new File(targetLocation.getAbsoluteFile(), children[i]),
            move, atomic))
          return false;
      }

      if (move)
        return sourceLocation.delete();
      else
	return true;
    }
    else {
      source = FileSystems.getDefault().getPath(sourceLocation.getAbsolutePath());
      if (targetLocation.isDirectory())
        target = FileSystems.getDefault().getPath(targetLocation.getAbsolutePath() + File.separator + sourceLocation.getName());
      else
        target = FileSystems.getDefault().getPath(targetLocation.getAbsolutePath());
      if (move) {
	if (atomic)
	  Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
	else
	  Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
      }
      else {
	Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
      }
      return true;
    }
  }

  /**
   * Copies the file/directory (recursively).
   *
   * @param sourceLocation	the source file/dir
   * @param targetLocation	the target file/dir
   * @return			if successfully copied
   * @throws IOException	if copying fails
   */
  public static boolean copy(File sourceLocation, File targetLocation) throws IOException {
    return copyOrMove(sourceLocation, targetLocation, false, false);
  }

  /**
   * Moves the file/directory (recursively).
   *
   * @param sourceLocation	the source file/dir
   * @param targetLocation	the target file/dir
   * @return			if successfully moved
   * @throws IOException	if moving fails
   */
  public static boolean move(File sourceLocation, File targetLocation) throws IOException {
    return move(sourceLocation, targetLocation, false);
  }

  /**
   * Moves the file/directory (recursively).
   *
   * @param sourceLocation	the source file/dir
   * @param targetLocation	the target file/dir
   * @param atomic		whether to perform an atomic move operation
   * @return			if successfully moved
   * @throws IOException	if moving fails
   */
  public static boolean move(File sourceLocation, File targetLocation, boolean atomic) throws IOException {
    return copyOrMove(sourceLocation, targetLocation, true, atomic);
  }

  /**
   * Deletes the specified file. If the file represents a directory, then this
   * will get deleted recursively.
   *
   * @param file	the file/dir to delete
   * @return		true if successfully deleted
   */
  public static boolean delete(String file) {
    return delete(new PlaceholderFile(file));
  }

  /**
   * Deletes the specified file. If the file represents a directory, then this
   * will get deleted recursively.
   *
   * @param file	the file/dir to delete
   * @return		true if successfully deleted
   */
  public static boolean delete(File file) {
    boolean	result;
    File[]	files;

    result = true;

    if (file.isDirectory()) {
      files = file.listFiles();
      if (files != null) {
	for (File f : files) {
	  if (f.getName().equals(".") || f.getName().equals(".."))
	    continue;
	  result = delete(f);
	  if (!result)
	    return false;
	}
      }
    }

    result = file.delete();

    return result;
  }

  /**
   * Replaces all characters that would create problems on a filesystem.
   * The string is to be expected a filename without a path.
   *
   * @param s		the string to process
   * @param replace	the character to replace "invalid" characters with,
   * 			use empty string to strip "invalid" characters instead
   * 			of replacing them.
   * @return		the processed string
   */
  public static String createFilename(String s, String replace) {
    StringBuilder	result;
    int			i;
    String		chars;

    result = new StringBuilder();
    chars  = getFileNameChars();

    for (i = 0; i < s.length(); i++) {
      if (chars.indexOf(s.charAt(i)) == -1) {
	result.append(replace);
      }
      else {
	result.append(s.charAt(i));
      }
    }

    return result.toString();
  }

  /**
   * Returns the number of directories that this file object contains.
   * E.g.: /home/blah/some/where.txt will return 3. /blah.txt returns 0.
   * 
   * @param file		the file
   */
  public static int getDirectoryDepth(File file) {
    int		result;

    result = 0;
    
    if (!file.isDirectory())
      file = file.getParentFile();
    
    while (file.getParentFile() != null) {
      result++;
      file = file.getParentFile();
    }
    
    return result;
  }
  
  /**
   * Creates a partial filename for the given file, based on how many parent
   * directories should be included. Examples:
   * <pre>
   * createPartialFilename(new File("/home/some/where/file.txt"), -1)
   *   = /home/some/where/file.txt
   * createPartialFilename(new File("/home/some/where/file.txt"), 0)
   *   = file.txt
   * createPartialFilename(new File("/home/some/where/file.txt"), 1)
   *   = where/file.txt
   * createPartialFilename(new File("/home/some/where/file.txt"), 2)
   *   = some/where/file.txt
   * </pre>
   *
   * @param file		the file to create the partial filename for
   * @param numParentDirs	the number of parent directories to include in
   * 				the partial name, -1 returns the absolute
   * 				filename
   * @return			the generated filename
   */
  public static String createPartialFilename(File file, int numParentDirs) {
    String	result;
    File	parent;
    int		i;

    if (numParentDirs == -1) {
      result = file.getAbsolutePath();
    }
    else {
      result = file.getName();
      parent = file;
      for (i = 0; (i < numParentDirs) && (parent.getParentFile() != null); i++) {
        parent = parent.getParentFile();
        result = parent.getName() + File.separator + result;
      }
    }

    return result;
  }

  /**
   * Checks whether the directory is empty.
   *
   * @param dir		the directory to check
   * @return		true if empty
   */
  public static boolean isDirEmpty(File dir) {
    return isDirEmpty(dir, null);
  }

  /**
   * Checks whether the directory is empty.
   *
   * @param dir		the directory to check
   * @return		true if empty
   */
  public static boolean isDirEmpty(String dir) {
    return isDirEmpty(dir, null);
  }

  /**
   * Checks whether the directory is empty.
   *
   * @param dir		the directory to check
   * @param regExp	a regular expression to look for, use null to ignore
   * @return		true if empty
   */
  public static boolean isDirEmpty(File dir, String regExp) {
    return isDirEmpty(dir.getAbsolutePath(), regExp);
  }

  /**
   * Checks whether the directory is empty.
   *
   * @param dir		the directory to check
   * @param regExp	a regular expression to look for, use null to ignore
   * @return		true if empty or directory does not exist
   */
  public static boolean isDirEmpty(String dir, String regExp) {
    boolean	result;
    File	file;
    String[]	files;
    int		i;

    result = true;

    file = new File(dir);
    // directory does not exist?
    if (!file.exists() || !file.isDirectory())
      return result;
    
    files = file.list();
    for (i = 0; i < files.length; i++) {
      // skip . and ..
      if (files[i].equals(".") || files[i].equals(".."))
	continue;

      // only look for matching filenames only
      if ((regExp != null) && (!files[i].matches(regExp)))
	continue;

      // found at least 1 file!
      result = false;
      break;
    }

    return result;
  }

  /**
   * Adjusts the extension according to the platform. For Windows it
   * automatically adds ".exe" it neither ".com" nor ".exe" extension present.
   * For other platforms it removes ".exe" and ".com".
   *
   * @param executable	the executable (full path or just filename) to process
   * @return		the processed executable
   */
  public static String fixExecutable(String executable)  {
    String	result;

    result = executable;

    if (OS.isWindows()) {
      if (!result.endsWith(".exe") || !result.endsWith(".com"))
	result += ".exe";
    }
    else {
      if (result.endsWith(".exe") || result.endsWith(".com"))
	result = result.substring(0, result.length() - 4);
    }

    return result;
  }

  /**
   * Surrounds the executable with double quotes if a blank is in the path.
   *
   * @param executable	the executable (full path, no parameters)
   * @return		the processed executable
   */
  public static String quoteExecutable(String executable) {
    String	result;

    result = executable;
    if (result.indexOf(' ') > -1)
      result = "\"" + result + "\"";

    return result;
  }
  
  /**
   * Removes any ignored extension suffixes from the filename.
   * 
   * @param filename	the filename to process
   * @return		the processed filename
   */
  public static String removeIgnoredExtensionSuffixes(String filename) {
    String	result;
    String[]	ignored;

    result  = filename;
    
    // remove ignored suffixes
    ignored = getIgnoredExtensionSuffixes();
    for (String suffix: ignored) {
      if (result.endsWith("." + suffix))
	result = result.substring(0, result.length() - suffix.length() - 1);
    }
    
    return result;
  }

  /**
   * Returns the extension of the file, if any.
   *
   * @param file	the file to get the extension from
   * @return		the extension (no dot), null if none available
   */
  public static String getExtension(File file) {
    return getExtension(file.getAbsolutePath());
  }

  /**
   * Returns the extension of the file, if any.
   *
   * @param filename	the file to get the extension from
   * @return		the extension (no dot), null if none available
   */
  public static String getExtension(String filename) {
    String[]	result;

    result = getExtensions(filename);

    if (result != null)
      return result[0];
    else
      return null;
  }

  /**
   * Returns the extensions of the file, if any.
   * Returns "txt.gz" and "gz", for instance, for file "hello_world.txt.gz".
   * The longer extension always comes first.
   *
   * @param file	the file to get the extensions from
   * @return		the extensions (no dot), null if none available
   */
  public static String[] getExtensions(File file) {
    return getExtensions(file.getAbsolutePath());
  }

  /**
   * Returns the extensions of the file, if any.
   * Removes ignored file extension suffixes like "gz" first.
   *
   * @param filename	the file to get the extensions from
   * @return		the extensions (no dot), null if none available
   * @see		#getIgnoredExtensionSuffixes()
   */
  public static String[] getExtensions(String filename) {
    List<String>	result;
    int			max;
    int			pos;
    int			posNext;
    String		shortened;

    if (filename.indexOf('.') == -1)
      return null;

    result = new ArrayList<String>();

    shortened = removeIgnoredExtensionSuffixes(filename);
    max       = getMaxExtensionLength();
    pos       = filename.lastIndexOf('.', shortened.length() - 1);
    result.add(filename.substring(pos + 1));

    posNext = filename.lastIndexOf('.', pos - 1);
    if ((posNext > -1) && (pos - posNext <= max))
      result.add(filename.substring(posNext + 1));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Replaces the extension of the given file with the new one. Leave the
   * new extension empty if you want to remove the extension.
   * Always removes ignored extension suffixes first from the filename.
   * 
   * @param file	the file to replace the extension for
   * @param newExt	the new extension (incl dot), empty string to remove extension
   * @return		the updated file
   * @see		#getIgnoredExtensionSuffixes()
   */
  public static File replaceExtension(File file, String newExt) {
    return new File(replaceExtension(file.getAbsolutePath(), newExt));
  }

  /**
   * Replaces the extension of the given file with the new one. Leave the
   * new extension empty if you want to remove the extension.
   * Always removes ignored extension suffixes first from the filename.
   * 
   * @param file	the file to replace the extension for
   * @param newExt	the new extension (incl dot), empty string to remove extension
   * @return		the updated file
   * @see		#getIgnoredExtensionSuffixes()
   */
  public static PlaceholderFile replaceExtension(PlaceholderFile file, String newExt) {
    return new PlaceholderFile(replaceExtension(file.getAbsolutePath(), newExt));
  }

  /**
   * Replaces the extension of the given file with the new one. Leave the
   * new extension empty if you want to remove the extension.
   * Always removes ignored extension suffixes first from the filename.
   * 
   * @param file	the file to replace the extension for
   * @param newExt	the new extension (incl dot), empty string to remove extension
   * @return		the updated file
   * @see		#getIgnoredExtensionSuffixes()
   */
  public static String replaceExtension(String file, String newExt) {
    String	result;
    int		index;
    
    result = file;
    
    file  = removeIgnoredExtensionSuffixes(file);
    index = file.lastIndexOf('.');
    if (index > -1) {
      if (newExt.length() > 0)
	result = file.substring(0, index) + newExt;
      else
	result = file.substring(0, index);
    }
    
    return result;
  }

  /**
   * Returns whether the file is (most likely) a binary one.
   * 
   * @param filename	the file to check
   * @return		true if a binary file
   */
  public static boolean isBinary(String filename) {
    return isBinary(new PlaceholderFile(filename));
  }
  
  /**
   * Returns whether the file is (most likely) a binary one.
   * Reads the first kb and analyzes the bytes (skips tab, cr, lf).
   * 
   * @param file	the file to check
   * @return		true if a binary file
   */
  public static boolean isBinary(File file) {
    boolean		result;
    FileInputStream	fis;
    BufferedInputStream	stream;
    int			i;
    int			read;
    byte[]		buffer;
    
    result = false;

    fis    = null;
    stream = null;
    buffer = new byte[BINARY_CHECK_BUFFER_SIZE];
    try {
      fis    = new FileInputStream(file.getAbsoluteFile());
      stream = new BufferedInputStream(fis);
      read   = stream.read(buffer);
    }
    catch (Exception e) {
      System.err.println("Problem reading binary file '" + file + "':");
      e.printStackTrace();
      read = -1;
    }
    finally {
      closeQuietly(stream);
      closeQuietly(fis);
    }
    
    if (read > -1) {
      for (i = 0; (i < read) && !result; i++) {
	if (buffer[i] < 32) {
	  switch(buffer[i]) {
	    case 9:  // tab
	    case 10: // cr
	    case 13: // lf
	      continue;
	    default:
	      result = true;
	  }
	}
      }
    }
    
    return result;
  }
  
  /**
   * Ensures that the file string no longer contains a placeholder.
   * 
   * @param file	the file name to process
   * @return		the "purged" file name
   */
  public static String convertPlaceholder(String file) {
    if (file.startsWith(Placeholders.PLACEHOLDER_START))
      return new PlaceholderFile(file).getAbsolutePath();
    else
      return file;
  }

  /**
   * Turns String, String[], File, File[] into a {@link PlaceholderFile} array.
   * 
   * @param input	the input
   * @return		the {@link PlaceholderFile} array
   * @throws IllegalArgumentException	if unsupported input class
   */
  public static PlaceholderFile[] toPlaceholderFileArray(Object input) {
    PlaceholderFile[]	result;
    String[]		str;
    int			i;

    if (input instanceof String) {
      result = new PlaceholderFile[]{new PlaceholderFile((String) input)};
    }
    else if (input instanceof String[]) {
      str   = (String[]) input;
      result = new PlaceholderFile[str.length];
      for (i = 0; i < str.length; i++)
	result[i] = new PlaceholderFile(str[i]);
    }
    else if (input instanceof File) {
      result = new PlaceholderFile[]{new PlaceholderFile((File) input)};
    }
    else if (input instanceof File[]) {
      result = (PlaceholderFile[]) input;
    }
    else {
      throw new IllegalArgumentException("Unhandled class: " + Utils.classToString(input.getClass()));
    }
    
    return result;
  }
  
  /**
   * Turns String, String[], File, File[] into a {@link File} array.
   * Ensures that strings don't contain placeholders and {@link File} objects
   * aren't {@link PlaceholderFile} objects.
   * 
   * @param input	the input
   * @return		the {@link File} array
   * @throws IllegalArgumentException	if unsupported input class
   */
  public static File[] toFileArray(Object input) {
    File[]	result;
    File[]	files;
    String[]	str;
    int		i;

    if (input instanceof String) {
      result = new File[]{new File(convertPlaceholder((String) input))};
    }
    else if (input instanceof String[]) {
      str   = (String[]) input;
      result = new File[str.length];
      for (i = 0; i < str.length; i++)
	result[i] = new File(convertPlaceholder(str[i]));
    }
    else if (input instanceof File) {
      result = new File[]{((File) input).getAbsoluteFile()};
    }
    else if (input instanceof File[]) {
      files  = (File[]) input;
      result = new File[files.length];
      for (i = 0; i < files.length; i++)
	result[i] = files[i].getAbsoluteFile();
    }
    else {
      throw new IllegalArgumentException("Unhandled class: " + Utils.classToString(input.getClass()));
    }
    
    return result;
  }

  /**
   * Turns String, String[], File, File[] into a {@link String} array.
   * Ensures that strings don't contain placeholders.
   * 
   * @param input	the input
   * @return		the {@link String} array
   * @throws IllegalArgumentException	if unsupported input class
   */
  public static String[] toStringArray(Object input) {
    String[]	result;
    String[]	str;
    File[]	files;
    int		i;

    if (input instanceof String) {
      result = new String[]{convertPlaceholder((String) input)};
    }
    else if (input instanceof String[]) {
      str    = (String[]) input;
      result = new String[str.length];
      for (i = 0; i < str.length; i++)
	result[i] = convertPlaceholder(str[i]);
    }
    else if (input instanceof File) {
      result = new String[]{((File) input).getAbsolutePath()};
    }
    else if (input instanceof File[]) {
      files  = (File[]) input;
      result = new String[files.length];
      for (i = 0; i < files.length; i++)
	result[i] = files[i].getAbsolutePath();
    }
    else {
      throw new IllegalArgumentException("Unhandled class: " + Utils.classToString(input.getClass()));
    }
    
    return result;
  }

  /**
   * Converts backslashes in a path to forwards slashes. However, leaves
   * UNC path prefix "\\" intact.
   *
   * @param path      the path/filename to convert
   * @return          the path/filename with forward slashes
   */
  public static String useForwardSlashes(String path) {
    String      result;

    if (path.startsWith("\\\\"))
      result = "\\\\" + path.substring(2).replace("\\", "/");
    else
      result = path.replace("\\", "/");

    return result;
  }

  /**
   * Closes the stream, if possible, suppressing any exception.
   *
   * @param is		the stream to close
   */
  public static void closeQuietly(InputStream is) {
    if (is != null) {
      try {
	is.close();
      }
      catch (Exception e) {
	// ignored
      }
    }
  }

  /**
   * Closes the stream, if possible, suppressing any exception.
   *
   * @param os		the stream to close
   */
  public static void closeQuietly(OutputStream os) {
    if (os != null) {
      try {
	os.flush();
      }
      catch (Exception e) {
	// ignored
      }
      try {
	os.close();
      }
      catch (Exception e) {
	// ignored
      }
    }
  }

  /**
   * Closes the reader, if possible, suppressing any exception.
   *
   * @param reader	the reader to close
   */
  public static void closeQuietly(Reader reader) {
    if (reader != null) {
      try {
	reader.close();
      }
      catch (Exception e) {
	// ignored
      }
    }
  }

  /**
   * Closes the writer, if possible, suppressing any exception.
   *
   * @param writer	the writer to close
   */
  public static void closeQuietly(Writer writer) {
    if (writer != null) {
      try {
	writer.flush();
      }
      catch (Exception e) {
	// ignored
      }
      try {
	writer.close();
      }
      catch (Exception e) {
	// ignored
      }
    }
  }

  /**
   * Closes the closeable object, if possible, suppressing any exception.
   *
   * @param closeable	the closeable object to close
   */
  public static void closeQuietly(Closeable closeable) {
    if (closeable != null) {
      try {
	closeable.close();
      }
      catch (Exception e) {
	// ignored
      }
    }
  }

  /**
   * Checks whether the file is open/accessed by another process (Windows/*nix).
   *
   * @param file	the file to check
   * @return		true if still open/accessed by another process
   */
  @MixedCopyright(
    copyright = "Hans Frankenstein - http://stackoverflow.com/users/2406808/hans-frankenstein",
    license = License.CC_BY_SA_3,
    url = "http://stackoverflow.com/a/16686031/4698227"
  )
  public static boolean isOpen(File file) {
    boolean		result;
    FileOutputStream 	fos;
    Process 		plsof;
    BufferedReader 	reader;
    String 		line;

    result = false;

    if (OS.isWindows()) {
      fos = null;
      try {
	fos = new FileOutputStream(file.getAbsolutePath(), true);
      }
      catch (Exception e) {
	result = true;
      }
      finally {
	FileUtils.closeQuietly(fos);
      }
    }
    else {
      plsof     = null;
      reader    = null;
      try {
	plsof  = new ProcessBuilder(new String[]{"lsof", "|", "grep", file.getAbsolutePath()}).start();
	reader = new BufferedReader(new InputStreamReader(plsof.getInputStream()));
	while ((line = reader.readLine()) != null) {
	  if (line.contains(file.getAbsolutePath())) {
	    result = true;
	    break;
	  }
	}
      }
      catch (Exception ex) {
	System.err.println("Failed to check file open status of: " + file);
	ex.printStackTrace();
      }
      finally {
	FileUtils.closeQuietly(reader);
	if (plsof != null)
	  plsof.destroy();
      }
    }

    return result;
  }

  /**
   * Checks whether the file (not directory!) exists.
   *
   * @param filename	the filename to check
   * @return		true if file exists
   */
  public static boolean fileExists(String filename) {
    return fileExists(new PlaceholderFile(filename));
  }

  /**
   * Checks whether the file (not directory!) exists.
   *
   * @param file	the file to check
   * @return		true if file exists
   */
  public static boolean fileExists(File file) {
    return file.exists() && !file.isDirectory();
  }

  /**
   * Checks whether the directory exists. If this file object represents a
   * file, then the parent directory is checked.
   *
   * @param filename	the filename to check
   * @return		true if directory exists
   */
  public static boolean directoryExists(String filename) {
    return directoryExists(new PlaceholderFile(filename));
  }

  /**
   * Checks whether the directory exists. If this file object represents a
   * file, then the parent directory is checked.
   *
   * @param file	the file object to check
   * @return		true if directory exists
   */
  public static boolean directoryExists(File file) {
    if (file.isDirectory())
      return file.exists();
    else if (file.isFile())
      return file.getParentFile().exists();
    else
      return (!file.exists() && (file.getParentFile() != null) && file.getParentFile().exists() && file.getParentFile().isDirectory());
  }
}
