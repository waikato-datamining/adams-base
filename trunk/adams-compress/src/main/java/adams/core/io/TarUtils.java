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
 * TarUtils.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 * Copyright (C) 2010 jcscoobyrs
 */
package adams.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.core.base.BaseRegExp;

/**
 * A helper class for Tar-file related tasks.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TarUtils {

  /**
   * The type of compression to use.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Compression {
    /** automatic. */
    AUTO,
    /** no compression. */
    NONE,
    /** gzip. */
    GZIP,
    /** bzip2. */
    BZIP2
  }

  /**
   * Determines the compression based on the the file extension.
   *
   * @param archive	the archive to determine the compression for
   * @return		the compression
   */
  public static Compression determineCompression(File archive) {
    return determineCompression(archive.getAbsolutePath());
  }

  /**
   * Determines the compression based on the the file extension.
   *
   * @param archive	the archive to determine the compression for
   * @return		the compression
   */
  public static Compression determineCompression(String archive) {
    archive = archive.toLowerCase();
    if (archive.endsWith(".tar.gz"))
      return Compression.GZIP;
    else if (archive.endsWith(".tgz"))
      return Compression.GZIP;
    else if (archive.endsWith(".tar.bz2"))
      return Compression.BZIP2;
    else
      return Compression.NONE;
  }

  /**
   * Returns an input stream for the specified tar archive. Automatically
   * determines the compression used for the archive.
   *
   * @param input	the tar archive to create the input stream for
   * @return		the input stream
   * @throws Exception	if file not found or similar problems
   */
  protected static TarArchiveInputStream openArchiveForReading(File input) throws Exception {
    Compression		comp;

    comp = determineCompression(input);
    if (comp == Compression.GZIP)
      return new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(input.getAbsoluteFile()))));
    else if (comp == Compression.BZIP2)
      return new TarArchiveInputStream(new BZip2CompressorInputStream(new BufferedInputStream(new FileInputStream(input.getAbsoluteFile()))));
    else
      return new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(input.getAbsoluteFile())));
  }

  /**
   * Returns an output stream for the specified tar archive. Automatically
   * determines the compression used for the archive. Uses GNU long filename
   * support.
   *
   * @param input	the tar archive to create the output stream for
   * @return		the output stream
   * @throws Exception	if file not found or similar problems
   * @see		TarArchiveOutputStream#LONGFILE_GNU
   */
  protected static TarArchiveOutputStream openArchiveForWriting(File input) throws Exception {
    TarArchiveOutputStream	result;
    Compression			comp;

    comp = determineCompression(input);
    if (comp == Compression.GZIP)
      result = new TarArchiveOutputStream(new GzipCompressorOutputStream(new BufferedOutputStream(new FileOutputStream(input.getAbsoluteFile()))));
    else if (comp == Compression.BZIP2)
      result = new TarArchiveOutputStream(new BZip2CompressorOutputStream(new BufferedOutputStream(new FileOutputStream(input.getAbsoluteFile()))));
    else
      result = new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(input.getAbsoluteFile())));

    result.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

    return result;
  }

  /**
   * Creates a tar file from the specified files.
   *
   * @param output	the output file to generate
   * @param files	the files to store in the tar file
   * @return		null if successful, otherwise error message
   * @see		#compress(File, File[], int)
   */
  public static String compress(File output, File[] files) {
    return compress(output, files, 1024);
  }

  /**
   * Creates a tar file from the specified files.
   *
   * @param output	the output file to generate
   * @param files	the files to store in the tar file
   * @param bufferSize	the buffer size to use
   * @return		null if successful, otherwise error message
   * @see		#compress(File, File[], String, int)
   */
  public static String compress(File output, File[] files, int bufferSize) {
    return compress(output, files, "", bufferSize);
  }

  /**
   * Creates a tar file from the specified files.
   * <p/>
   * See <a href="http://www.thoughtspark.org/node/53" target="_blank">Creating a tar.gz with commons-compress</a>.
   *
   * @param output	the output file to generate
   * @param files	the files to store in the tar file
   * @param stripRegExp	the regular expression used to strip the file names
   * @param bufferSize	the buffer size to use
   * @return		null if successful, otherwise error message
   */
  @MixedCopyright(
      author = "Jeremy Whitlock (jcscoobyrs)",
      copyright = "2010 Jeremy Whitlock",
      license = License.APACHE2,
      url = "http://www.thoughtspark.org/node/53"
  )
  public static String compress(File output, File[] files, String stripRegExp, int bufferSize) {
    String			result;
    int				i;
    byte[] 			buf;
    int 			len;
    TarArchiveOutputStream 	out;
    BufferedInputStream 	in;
    String			filename;
    String			msg;
    TarArchiveEntry		entry;

    in     = null;
    out    = null;
    result = null;
    try {
      // does file already exist?
      if (output.exists())
	System.err.println("WARNING: overwriting '" + output + "'!");

      // create tar file
      buf = new byte[bufferSize];
      out = openArchiveForWriting(output);
      for (i = 0; i < files.length; i++) {
	in = new BufferedInputStream(new FileInputStream(files[i].getAbsolutePath()));

	// Add tar entry to output stream.
	filename = files[i].getParentFile().getAbsolutePath();
	if (stripRegExp.length() > 0)
	  filename = filename.replaceFirst(stripRegExp, "");
	if (filename.length() > 0)
	  filename += File.separator;
	filename += files[i].getName();
	entry = new TarArchiveEntry(filename);
	if (files[i].isFile())
	  entry.setSize(files[i].length());
	out.putArchiveEntry(entry);

	// Transfer bytes from the file to the tar file
	while ((len = in.read(buf)) > 0)
	  out.write(buf, 0, len);

	// Complete the entry
	out.closeArchiveEntry();
	in.close();
	in = null;
      }

      // Complete the tar file
      out.close();
      out = null;
    }
    catch (Exception e) {
      msg = "Failed to generate archive '" + output + "': ";
      System.err.println(msg);
      e.printStackTrace();
      result = msg + e;
    }
    finally {
      if (in != null) {
	try {
	  in.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
      if (out != null) {
	try {
	  out.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }

  /**
   * Decompresses the files in a tar file. Does not recreate the directory structure
   * stored in the tar file.
   *
   * @param input	the tar file to decompress
   * @param outputDir	the directory where to store the extracted files
   * @return		the successfully extracted files
   * @see		#decompress(File, File, boolean)
   */
  public static List<File> decompress(File input, File outputDir) {
    return decompress(input, outputDir, false);
  }

  /**
   * Decompresses the files in a tar file.
   *
   * @param input	the tar file to decompress
   * @param outputDir	the directory where to store the extracted files
   * @param createDirs	whether to re-create the directory structure from the
   * 			tar file
   * @return		the successfully extracted files
   * @see		#decompress(File, File, boolean, String, boolean)
   */
  public static List<File> decompress(File input, File outputDir, boolean createDirs) {
    return decompress(input, outputDir, createDirs, new BaseRegExp(""), false);
  }

  /**
   * Decompresses the files in a tar file. Files can be filtered based on their
   * filename, using a regular expression (the matching sense can be inverted).
   *
   * @param input	the tar file to decompress
   * @param outputDir	the directory where to store the extracted files
   * @param createDirs	whether to re-create the directory structure from the
   * 			tar file
   * @param match	the regular expression that the files are matched against
   * @param invertMatch	whether to invert the matching sense
   * @return		the successfully extracted files
   * @see		#decompress(File, File, boolean, String, boolean, int)
   */
  public static List<File> decompress(File input, File outputDir, boolean createDirs, BaseRegExp match, boolean invertMatch) {
    return decompress(input, outputDir, createDirs, match, invertMatch, 1024);
  }

  /**
   * Decompresses the files in a tar file. Files can be filtered based on their
   * filename, using a regular expression (the matching sense can be inverted).
   *
   * @param input	the tar file to decompress
   * @param outputDir	the directory where to store the extracted files
   * @param createDirs	whether to re-create the directory structure from the
   * 			tar file
   * @param match	the regular expression that the files are matched against
   * @param invertMatch	whether to invert the matching sense
   * @param bufferSize	the buffer size to use
   * @return		the successfully extracted files
   */
  public static List<File> decompress(File input, File outputDir, boolean createDirs, BaseRegExp match, boolean invertMatch, int bufferSize) {
    return decompress(input, outputDir, createDirs, match, invertMatch, bufferSize, new StringBuilder());
  }

  /**
   * Decompresses the files in a tar file. Files can be filtered based on their
   * filename, using a regular expression (the matching sense can be inverted).
   *
   * @param input	the tar file to decompress
   * @param outputDir	the directory where to store the extracted files
   * @param createDirs	whether to re-create the directory structure from the
   * 			tar file
   * @param match	the regular expression that the files are matched against
   * @param invertMatch	whether to invert the matching sense
   * @param bufferSize	the buffer size to use
   * @param errors	for storing potential errors
   * @return		the successfully extracted files
   */
  public static List<File> decompress(File input, File outputDir, boolean createDirs, BaseRegExp match, boolean invertMatch, int bufferSize, StringBuilder errors) {
    List<File>		result;
    TarArchiveInputStream	archive;
    TarArchiveEntry		entry;
    File			outFile;
    String			outName;
    byte[]			buffer;
    BufferedOutputStream	out;
    int				len;
    String			error;
    long			size;
    long			read;

    result  = new ArrayList<File>();
    archive = null;
    try {
      // decompress archive
      buffer  = new byte[bufferSize];
      archive = openArchiveForReading(input.getAbsoluteFile());
      while ((entry = archive.getNextTarEntry()) != null) {
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
	  out     = null;
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
	    out  = new BufferedOutputStream(new FileOutputStream(outName), bufferSize);
	    size = entry.getSize();
	    read = 0;
	    while (read < size) {
	      len = archive.read(buffer);
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
	    if (out != null) {
	      try {
		out.flush();
		out.close();
	      }
	      catch (Exception e) {
		// ignored
	      }
	    }
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
   * Decompresses the specified file from a tar file. Does not create any directories
   * in case the parent directories of "output" don't exist yet.
   *
   * @param input	the tar file to decompress
   * @param archiveFile	the file from the archive to extract
   * @param output	the name of the output file
   * @return		whether file was successfully extracted
   */
  public static boolean decompress(File input, String archiveFile, File output) {
    return decompress(input, archiveFile, output, false);
  }

  /**
   * Decompresses the specified file from a tar file.
   *
   * @param input	the tar file to decompress
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
   * Decompresses the specified file from a tar file.
   *
   * @param input	the tar file to decompress
   * @param archiveFile	the file from the archive to extract
   * @param output	the name of the output file
   * @param createDirs	whether to create the directory structure represented
   * 			by output file
   * @param bufferSize	the buffer size to use
   * @param errors	for storing potential errors
   * @return		whether file was successfully extracted
   */
  public static boolean decompress(File input, String archiveFile, File output, boolean createDirs, int bufferSize, StringBuilder errors) {
    boolean			result;
    TarArchiveInputStream	archive;
    TarArchiveEntry		entry;
    File			outFile;
    String			outName;
    byte[]			buffer;
    BufferedOutputStream	out;
    int				len;
    String			error;
    long			size;
    long			read;

    result  = false;
    archive = null;
    try {
      // decompress archive
      buffer  = new byte[bufferSize];
      archive = openArchiveForReading(input.getAbsoluteFile());
      while ((entry = archive.getNextTarEntry()) != null) {
	if (entry.isDirectory())
	  continue;
	if (!entry.getName().equals(archiveFile))
	  continue;

	out     = null;
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
	  out = new BufferedOutputStream(new FileOutputStream(outName), bufferSize);
	  size = entry.getSize();
	  read = 0;
	  while (read < size) {
	    len = archive.read(buffer);
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
	  if (out != null) {
	    try {
	      out.flush();
	      out.close();
	    }
	    catch (Exception e) {
	      // ignored
	    }
	  }
	}
      }
    }
    catch (Exception e) {
      result = false;
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
   * Lists the files stored in the tar file. Lists directories automatically.
   *
   * @param input	the tar file to obtain the file list from
   * @return		the stored files
   */
  public static List<File> listFiles(File input) {
    return listFiles(input, true);
  }

  /**
   * Lists the files stored in the tar file.
   *
   * @param input	the tar file to obtain the file list from
   * @param listDirs	whether to include directories in the list
   * @return		the stored files
   */
  public static List<File> listFiles(File input, boolean listDirs) {
    List<File>		result;
    TarArchiveInputStream	archive;
    TarArchiveEntry		entry;

    result  = new ArrayList<File>();
    archive = null;
    try {
      archive = openArchiveForReading(input);
      while ((entry = archive.getNextTarEntry()) != null) {
	if (entry.isDirectory() && listDirs) {
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
}
