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
 * ZipUtils.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 * Copyright (C) Apache compress commons
 */
package adams.core.io;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.core.base.BaseRegExp;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * A helper class for ZIP-file related tasks.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ZipUtils {

  /**
   * Creates a zip file from the specified files.
   *
   * @param output	the output file to generate
   * @param files	the files to store in the zip file
   * @return		null if successful, otherwise error message
   */
  public static String compress(File output, File[] files) {
    return compress(output, files, 1024);
  }

  /**
   * Creates a zip file from the specified files.
   *
   * @param output	the output file to generate
   * @param files	the files to store in the zip file
   * @param bufferSize	the buffer size to use
   * @return		null if successful, otherwise error message
   */
  public static String compress(File output, File[] files, int bufferSize) {
    return compress(output, files, "", bufferSize);
  }

  /**
   * Creates a zip file from the specified files.
   *
   * @param output	the output file to generate
   * @param files	the files to store in the zip file
   * @param stripRegExp	the regular expression used to strip the file names (only applied to the directory!)
   * @param bufferSize	the buffer size to use
   * @return		null if successful, otherwise error message
   */
  @MixedCopyright(
      copyright = "Apache compress commons",
      license = License.APACHE2,
      url = "http://commons.apache.org/compress/examples.html"
  )
  public static String compress(File output, File[] files, String stripRegExp, int bufferSize) {
    String			result;
    int				i;
    byte[] 			buf;
    int 			len;
    ZipArchiveOutputStream	out;
    BufferedInputStream 	in;
    FileInputStream		fis;
    FileOutputStream		fos;
    String			filename;
    String			msg;
    ZipArchiveEntry		entry;

    in     = null;
    fis    = null;
    out    = null;
    fos    = null;
    result = null;
    try {
      // does file already exist?
      if (output.exists())
	System.err.println("WARNING: overwriting '" + output + "'!");

      // create ZIP file
      buf = new byte[bufferSize];
      fos = new FileOutputStream(output.getAbsolutePath());
      out = new ZipArchiveOutputStream(new BufferedOutputStream(fos));
      for (i = 0; i < files.length; i++) {
	fis = new FileInputStream(files[i].getAbsolutePath());
	in  = new BufferedInputStream(fis);

	// Add ZIP entry to output stream.
	filename = files[i].getParentFile().getAbsolutePath();
	if (stripRegExp.length() > 0)
	  filename = filename.replaceFirst(stripRegExp, "");
	if (filename.length() > 0)
	  filename += File.separator;
	filename += files[i].getName();
	entry = new ZipArchiveEntry(filename);
	entry.setSize(files[i].length());
	out.putArchiveEntry(entry);

	// Transfer bytes from the file to the ZIP file
	while ((len = in.read(buf)) > 0)
	  out.write(buf, 0, len);

	// Complete the entry
	out.closeArchiveEntry();
	FileUtils.closeQuietly(in);
	FileUtils.closeQuietly(fis);
	in  = null;
	fis = null;
      }

      // Complete the ZIP file
      FileUtils.closeQuietly(out);
      FileUtils.closeQuietly(fos);
      out = null;
      fos = null;
    }
    catch (Exception e) {
      msg = "Failed to generate archive '" + output + "': ";
      System.err.println(msg);
      e.printStackTrace();
      result = msg + e;
    }
    finally {
      FileUtils.closeQuietly(in);
      FileUtils.closeQuietly(fis);
      FileUtils.closeQuietly(out);
      FileUtils.closeQuietly(fos);
    }

    return result;
  }

  /**
   * Unzips the files in a ZIP file. Does not recreate the directory structure
   * stored in the ZIP file.
   *
   * @param input	the ZIP file to unzip
   * @param outputDir	the directory where to store the extracted files
   * @return		the successfully extracted files
   */
  public static List<File> decompress(File input, File outputDir) {
    return decompress(input, outputDir, false);
  }

  /**
   * Unzips the files in a ZIP file.
   *
   * @param input	the ZIP file to unzip
   * @param outputDir	the directory where to store the extracted files
   * @param createDirs	whether to re-create the directory structure from the
   * 			ZIP file
   * @return		the successfully extracted files
   */
  public static List<File> decompress(File input, File outputDir, boolean createDirs) {
    return decompress(input, outputDir, createDirs, new BaseRegExp(""), false);
  }

  /**
   * Unzips the files in a ZIP file. Files can be filtered based on their
   * filename, using a regular expression (the matching sense can be inverted).
   *
   * @param input	the ZIP file to unzip
   * @param outputDir	the directory where to store the extracted files
   * @param createDirs	whether to re-create the directory structure from the
   * 			ZIP file
   * @param match	the regular expression that the files are matched against
   * @param invertMatch	whether to invert the matching sense
   * @return		the successfully extracted files
   */
  public static List<File> decompress(File input, File outputDir, boolean createDirs, BaseRegExp match, boolean invertMatch) {
    return decompress(input, outputDir, createDirs, match, invertMatch, 1024);
  }

  /**
   * Unzips the files in a ZIP file. Files can be filtered based on their
   * filename, using a regular expression (the matching sense can be inverted).
   *
   * @param input	the ZIP file to unzip
   * @param outputDir	the directory where to store the extracted files
   * @param createDirs	whether to re-create the directory structure from the
   * 			ZIP file
   * @param match	the regular expression that the files are matched against
   * @param invertMatch	whether to invert the matching sense
   * @param bufferSize	the buffer size to use
   * @return		the successfully extracted files
   */
  public static List<File> decompress(File input, File outputDir, boolean createDirs, BaseRegExp match, boolean invertMatch, int bufferSize) {
    return decompress(input, outputDir, createDirs, match, invertMatch, bufferSize, new StringBuilder());
  }

  /**
   * Unzips the files in a ZIP file. Files can be filtered based on their
   * filename, using a regular expression (the matching sense can be inverted).
   *
   * @param input	the ZIP file to unzip
   * @param outputDir	the directory where to store the extracted files
   * @param createDirs	whether to re-create the directory structure from the
   * 			ZIP file
   * @param match	the regular expression that the files are matched against
   * @param invertMatch	whether to invert the matching sense
   * @param bufferSize	the buffer size to use
   * @param errors	for storing potential errors
   * @return		the successfully extracted files
   */
  @MixedCopyright(
      copyright = "Apache compress commons",
      license = License.APACHE2,
      url = "http://commons.apache.org/compress/examples.html"
  )
  public static List<File> decompress(File input, File outputDir, boolean createDirs, BaseRegExp match, boolean invertMatch, int bufferSize, StringBuilder errors) {
    List<File>			result;
    ZipFile				archive;
    Enumeration<ZipArchiveEntry>	enm;
    ZipArchiveEntry			entry;
    File				outFile;
    String				outName;
    byte[]				buffer;
    BufferedInputStream			in;
    BufferedOutputStream		out;
    FileOutputStream			fos;
    int					len;
    String				error;
    long				read;

    result  = new ArrayList<>();
    archive = null;
    try {
      // unzip archive
      buffer  = new byte[bufferSize];
      archive = new ZipFile(input.getAbsoluteFile());
      enm     = archive.getEntries();
      while (enm.hasMoreElements()) {
	entry = enm.nextElement();

	if (entry.isDirectory() && !createDirs)
	  continue;

	// does name match?
	if (!match.isMatchAll() && !match.isEmpty()) {
	  if (invertMatch && match.isMatch(entry.getName()))
	    continue;
	  else if (!invertMatch && !match.isMatch(entry.getName()))
	    continue;
	}

	// extract
	if (entry.isDirectory() && createDirs) {
	  outFile = new File(outputDir.getAbsolutePath() + File.separator + entry.getName());
	  if (!outFile.mkdirs()) {
	    error = "Failed to create directory '" + outFile.getAbsolutePath() + "'!";
	    System.err.println(error);
	    errors.append(error + "\n");
	  }
	}
	else {
	  in      = null;
	  out     = null;
	  fos     = null;
	  outName = null;
	  try {
	    // assemble output name
	    outName = outputDir.getAbsolutePath() + File.separator;
	    if (createDirs)
	      outName += entry.getName();
	    else
	      outName += new File(entry.getName()).getName();

	    // create directory, if necessary
	    outFile = new File(outName).getParentFile();
	    if (!outFile.exists()) {
	      if (!outFile.mkdirs()) {
		error =
		    "Failed to create directory '" + outFile.getAbsolutePath() + "', "
		    + "skipping extraction of '" + outName + "'!";
		System.err.println(error);
		errors.append(error + "\n");
		continue;
	      }
	    }

	    // extract data
	    in   = new BufferedInputStream(archive.getInputStream(entry));
	    fos  = new FileOutputStream(outName);
	    out  = new BufferedOutputStream(fos, bufferSize);
	    read = 0;
	    while (read < entry.getSize()) {
	      len   = in.read(buffer);
	      read += len;
	      out.write(buffer, 0, len);
	    }
	    result.add(new File(outName));
	  }
	  catch (Exception e) {
	    error = "Error extracting '" + entry.getName() + "' to '" + outName + "': " + e;
	    System.err.println(error);
	    errors.append(error + "\n");
	  }
	  finally {
	    FileUtils.closeQuietly(in);
	    FileUtils.closeQuietly(out);
	    FileUtils.closeQuietly(fos);
	  }
	}
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      errors.append("Error occurred: " + e + "\n");
    }
    finally {
      if (archive != null) {
	try {
	  archive.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }

  /**
   * Unzips the specified file from a ZIP file. Does not create any directories
   * in case the parent directories of "output" don't exist yet.
   *
   * @param input	the ZIP file to unzip
   * @param archiveFile	the file from the archive to extract
   * @param output	the name of the output file
   * @return		whether file was successfully extracted
   */
  public static boolean decompress(File input, String archiveFile, File output) {
    return decompress(input, archiveFile, output, false);
  }

  /**
   * Unzips the specified file from a ZIP file.
   *
   * @param input	the ZIP file to unzip
   * @param archiveFile	the file from the archive to extract
   * @param output	the name of the output file
   * @param createDirs	whether to create the directory structure represented
   * 			by output file
   * @return		whether file was successfully extracted
   */
  public static boolean decompress(File input, String archiveFile, File output, boolean createDirs) {
    return decompress(input, archiveFile, output, createDirs, 1024, new StringBuilder());
  }

  /**
   * Unzips the specified file from a ZIP file.
   *
   * @param input	the ZIP file to unzip
   * @param archiveFile	the file from the archive to extract
   * @param output	the name of the output file
   * @param createDirs	whether to create the directory structure represented
   * 			by output file
   * @param bufferSize	the buffer size to use
   * @param errors	for storing potential errors
   * @return		whether file was successfully extracted
   */
  public static boolean decompress(File input, String archiveFile, File output, boolean createDirs, int bufferSize, StringBuilder errors) {
    boolean				result;
    ZipFile				zipfile;
    Enumeration<ZipArchiveEntry>	enm;
    ZipArchiveEntry			entry;
    File				outFile;
    String				outName;
    byte[]				buffer;
    BufferedInputStream			in;
    BufferedOutputStream		out;
    FileOutputStream			fos;
    int					len;
    String				error;
    long				read;

    result  = false;
    zipfile = null;
    try {
      // unzip archive
      buffer  = new byte[bufferSize];
      zipfile = new ZipFile(input.getAbsoluteFile());
      enm     = zipfile.getEntries();
      while (enm.hasMoreElements()) {
	entry = enm.nextElement();

	if (entry.isDirectory())
	  continue;
	if (!entry.getName().equals(archiveFile))
	  continue;

	in      = null;
	out     = null;
	fos     = null;
	outName = null;
	try {
	  // output name
	  outName = output.getAbsolutePath();

	  // create directory, if necessary
	  outFile = new File(outName).getParentFile();
	  if (!outFile.exists()) {
	    if (!createDirs) {
		error =
		  "Output directory '" + outFile.getAbsolutePath() + " does not exist', "
		  + "skipping extraction of '" + outName + "'!";
		System.err.println(error);
		errors.append(error + "\n");
		break;
	    }
	    else {
	      if (!outFile.mkdirs()) {
		error =
		  "Failed to create directory '" + outFile.getAbsolutePath() + "', "
		  + "skipping extraction of '" + outName + "'!";
		System.err.println(error);
		errors.append(error + "\n");
		break;
	      }
	    }
	  }

	  // extract data
	  in   = new BufferedInputStream(zipfile.getInputStream(entry));
	  fos  = new FileOutputStream(outName);
	  out  = new BufferedOutputStream(fos, bufferSize);
	  read = 0;
	  while (read < entry.getSize()) {
	    len = in.read(buffer);
	    read += len;
	    out.write(buffer, 0, len);
	  }

	  result = true;
	  break;
	}
	catch (Exception e) {
	  result = false;
	  error  = "Error extracting '" + entry.getName() + "' to '" + outName + "': " + e;
	  System.err.println(error);
	  errors.append(error + "\n");
	}
	finally {
	  FileUtils.closeQuietly(in);
	  FileUtils.closeQuietly(out);
	  FileUtils.closeQuietly(fos);
	}
      }
    }
    catch (Exception e) {
      result = false;
      e.printStackTrace();
      errors.append("Error occurred: " + e + "\n");
    }
    finally {
      if (zipfile != null) {
	try {
	  zipfile.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }

  /**
   * Lists the files stored in the ZIP file. Lists directories automatically.
   *
   * @param input	the ZIP file to obtain the file list from
   * @return		the stored files
   */
  public static List<File> listFiles(File input) {
    return listFiles(input, true);
  }

  /**
   * Lists the files stored in the ZIP file.
   *
   * @param input	the ZIP file to obtain the file list from
   * @param listDirs	whether to include directories in the list
   * @return		the stored files
   */
  public static List<File> listFiles(File input, boolean listDirs) {
    List<File>			result;
    ZipFile				zipfile;
    Enumeration<ZipArchiveEntry>	enm;
    ZipArchiveEntry			entry;

    result  = new ArrayList<>();
    zipfile = null;
    try {
      zipfile = new ZipFile(input.getAbsoluteFile());
      enm     = zipfile.getEntries();
      while (enm.hasMoreElements()) {
	entry = enm.nextElement();

	// extract
	if (entry.isDirectory()) {
          if (listDirs)
            result.add(new File(entry.getName()));
	}
	else {
	  result.add(new File(entry.getName()));
	}
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      if (zipfile != null) {
	try {
	  zipfile.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }
}
