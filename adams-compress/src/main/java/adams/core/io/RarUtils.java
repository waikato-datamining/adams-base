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
 * RarUtils.java
 * Copyright (C) 2017-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import adams.core.MessageCollection;
import adams.core.base.BaseRegExp;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import com.github.junrar.Archive;
import com.github.junrar.UnrarCallback;
import com.github.junrar.rarfile.FileHeader;
import com.github.junrar.volume.Volume;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * A helper class for RAR-file related tasks.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RarUtils {

  /** for logging errors. */
  protected static Logger LOGGER = LoggingHelper.getLogger(RarUtils.class);

  /**
   * Dummy class.
   */
  public static class DummyUnrarCallback
    implements UnrarCallback {
    @Override
    public boolean isNextVolumeReady(Volume nextVolume) {
      return false;
    }
    @Override
    public void volumeProgressChanged(long current, long total) {
    }
  }

  /**
   * Unrars the files in a RAR file. Does not recreate the directory structure
   * stored in the RAR file.
   *
   * @param input	the RAR file to unrar
   * @param outputDir	the directory where to store the extracted files
   * @return		the successfully extracted files
   */
  public static List<File> decompress(File input, File outputDir) {
    return decompress(input, outputDir, false);
  }

  /**
   * Unrars the files in a RAR file.
   *
   * @param input	the RAR file to unrar
   * @param outputDir	the directory where to store the extracted files
   * @param createDirs	whether to re-create the directory structure from the
   * 			RAR file
   * @return		the successfully extracted files
   */
  public static List<File> decompress(File input, File outputDir, boolean createDirs) {
    return decompress(input, outputDir, createDirs, new BaseRegExp(""), false);
  }

  /**
   * Unrars the files in a RAR file. Files can be filtered based on their
   * filename, using a regular expression (the matching sense can be inverted).
   *
   * @param input	the RAR file to unrar
   * @param outputDir	the directory where to store the extracted files
   * @param createDirs	whether to re-create the directory structure from the
   * 			RAR file
   * @param match	the regular expression that the files are matched against
   * @param invertMatch	whether to invert the matching sense
   * @return		the successfully extracted files
   */
  public static List<File> decompress(File input, File outputDir, boolean createDirs, BaseRegExp match, boolean invertMatch) {
    return decompress(input, outputDir, createDirs, match, invertMatch, 1024);
  }

  /**
   * Unrars the files in a RAR file. Files can be filtered based on their
   * filename, using a regular expression (the matching sense can be inverted).
   *
   * @param input	the RAR file to unrar
   * @param outputDir	the directory where to store the extracted files
   * @param createDirs	whether to re-create the directory structure from the
   * 			RAR file
   * @param match	the regular expression that the files are matched against
   * @param invertMatch	whether to invert the matching sense
   * @param bufferSize	the buffer size to use
   * @return		the successfully extracted files
   */
  public static List<File> decompress(File input, File outputDir, boolean createDirs, BaseRegExp match, boolean invertMatch, int bufferSize) {
    return decompress(input, outputDir, createDirs, match, invertMatch, bufferSize, new MessageCollection());
  }

  /**
   * Unrars the files in a RAR file. Files can be filtered based on their
   * filename, using a regular expression (the matching sense can be inverted).
   *
   * @param input	the RAR file to unrar
   * @param outputDir	the directory where to store the extracted files
   * @param createDirs	whether to re-create the directory structure from the
   * 			RAR file
   * @param match	the regular expression that the files are matched against
   * @param invertMatch	whether to invert the matching sense
   * @param bufferSize	the buffer size to use
   * @param errors	for storing potential errors
   * @return		the successfully extracted files
   */
  public static List<File> decompress(File input, File outputDir, boolean createDirs, BaseRegExp match, boolean invertMatch, int bufferSize, MessageCollection errors) {
    List<File>				result;
    Archive 				archive;
    String				entryFilename;
    File				outFile;
    String				outName;
    BufferedInputStream			in;
    BufferedOutputStream		out;
    FileOutputStream			fos;
    String				msg;

    result  = new ArrayList<>();
    archive = null;
    try {
      // unrar archive
      archive = new Archive(input.getAbsoluteFile(), new DummyUnrarCallback());
      if (archive.isEncrypted())
	throw new IllegalStateException("Cannot handle encrypted archives!");
      for (FileHeader entry : archive.getFileHeaders()) {
	entryFilename = entry.getFileName().replace("\\", "/");

	// encrypted?
	if (entry.isEncrypted()) {
	  errors.add("Cannot handle encrypted file: " + entryFilename);
	  continue;
	}

	// dir?
	if (entry.isDirectory() && !createDirs)
	  continue;

	// does name match?
	if (!match.isMatchAll() && !match.isEmpty()) {
	  if (invertMatch && match.isMatch(entryFilename))
	    continue;
	  else if (!invertMatch && !match.isMatch(entryFilename))
	    continue;
	}

	// extract
	if (entry.isDirectory() && createDirs) {
	  outFile = new File(outputDir.getAbsolutePath() + File.separator + entryFilename);
	  if (!outFile.exists()) {
	    if (!outFile.mkdirs()) {
	      msg = "Failed to create directory '" + outFile.getAbsolutePath() + "'!";
	      LOGGER.log(Level.SEVERE, msg);
	      errors.add(msg);
	    }
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
	      outName += entryFilename;
	    else
	      outName += new File(entryFilename).getName();

	    // create directory, if necessary
	    outFile = new File(outName).getParentFile();
	    if (!outFile.exists()) {
	      if (!outFile.mkdirs()) {
		msg =
		    "Failed to create directory '" + outFile.getAbsolutePath() + "', "
		    + "skipping extraction of '" + outName + "'!";
		LOGGER.log(Level.SEVERE, msg);
		errors.add(msg);
		continue;
	      }
	    }

	    // extract data
	    fos  = new FileOutputStream(outName);
	    out  = new BufferedOutputStream(fos, bufferSize);
	    archive.extractFile(entry, out);
	    result.add(new File(outName));
	  }
	  catch (Exception e) {
	    msg = "Error extracting '" + entryFilename + "' to '" + outName + "': ";
	    LOGGER.log(Level.SEVERE, msg, e);
	    errors.add(msg, e);
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
      msg = "Error occurred: ";
      LOGGER.log(Level.SEVERE, msg, e);
      errors.add(msg, e);
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
   * Unrars the specified file from a RAR file. Does not create any directories
   * in case the parent directories of "output" don't exist yet.
   *
   * @param input	the RAR file to unrar
   * @param archiveFile	the file from the archive to extract
   * @param output	the name of the output file
   * @return		whether file was successfully extracted
   */
  public static boolean decompress(File input, String archiveFile, File output) {
    return decompress(input, archiveFile, output, false);
  }

  /**
   * Unrars the specified file from a RAR file.
   *
   * @param input	the RAR file to unrar
   * @param archiveFile	the file from the archive to extract
   * @param output	the name of the output file
   * @param createDirs	whether to create the directory structure represented
   * 			by output file
   * @return		whether file was successfully extracted
   */
  public static boolean decompress(File input, String archiveFile, File output, boolean createDirs) {
    return decompress(input, archiveFile, output, createDirs, 1024, new MessageCollection());
  }

  /**
   * Unrars the specified file from a RAR file.
   *
   * @param input	the RAR file to unrar
   * @param archiveFile	the file from the archive to extract
   * @param output	the name of the output file
   * @param createDirs	whether to create the directory structure represented
   * 			by output file
   * @param bufferSize	the buffer size to use
   * @param errors	for storing potential errors
   * @return		whether file was successfully extracted
   */
  public static boolean decompress(File input, String archiveFile, File output, boolean createDirs, int bufferSize, MessageCollection errors) {
    boolean			result;
    Archive			rarfile;
    File			outFile;
    String			outName;
    BufferedInputStream		in;
    BufferedOutputStream	out;
    FileOutputStream		fos;
    String			msg;
    String			entryFilename;

    result  = false;
    rarfile = null;
    try {
      // unrar archive
      rarfile = new Archive(input.getAbsoluteFile(), new DummyUnrarCallback());
      for (FileHeader entry: rarfile.getFileHeaders()) {
	entryFilename = entry.getFileNameString().replace("\\", "/");
	if (entry.isDirectory())
	  continue;
	if (!entryFilename.equals(archiveFile))
	  continue;
	// encrypted?
	if (entry.isEncrypted()) {
	  errors.add("Cannot handle encrypted file: " + entryFilename);
	  continue;
	}

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
	      msg =
		"Output directory '" + outFile.getAbsolutePath() + " does not exist', "
		  + "skipping extraction of '" + outName + "'!";
	      LOGGER.log(Level.SEVERE, msg);
	      errors.add(msg);
	      break;
	    }
	    else {
	      if (!outFile.mkdirs()) {
		msg =
		  "Failed to create directory '" + outFile.getAbsolutePath() + "', "
		    + "skipping extraction of '" + outName + "'!";
		LOGGER.log(Level.SEVERE, msg);
		errors.add(msg);
		break;
	      }
	    }
	  }

	  // extract data
	  fos  = new FileOutputStream(outName);
	  out  = new BufferedOutputStream(fos, bufferSize);
	  rarfile.extractFile(entry, out);
	  result = true;
	  break;
	}
	catch (Exception e) {
	  result = false;
	  msg    = "Error extracting '" + entryFilename + "' to '" + outName + "': ";
	  LOGGER.log(Level.SEVERE, msg, e);
	  errors.add(msg, e);
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
      msg = "Error occurred: ";
      LOGGER.log(Level.SEVERE, msg, e);
      errors.add(msg, e);
    }
    finally {
      if (rarfile != null) {
	try {
	  rarfile.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }

  /**
   * Lists the files stored in the RAR file. Lists directories automatically.
   *
   * @param input	the RAR file to obtain the file list from
   * @return		the stored files
   */
  public static List<File> listFiles(File input) {
    return listFiles(input, true);
  }

  /**
   * Lists the files stored in the RAR file.
   *
   * @param input	the RAR file to obtain the file list from
   * @param listDirs	whether to include directories in the list
   * @return		the stored files
   */
  public static List<File> listFiles(File input, boolean listDirs) {
    List<File>		result;
    Archive		rarfile;
    String		entryFilename;

    result  = new ArrayList<>();
    rarfile = null;
    try {
      rarfile = new Archive(input.getAbsoluteFile(), new DummyUnrarCallback());
      // encrypted?
      if (rarfile.isEncrypted())
	return result;
      for (FileHeader entry: rarfile.getFileHeaders()) {
	entryFilename = entry.getFileNameString().replace("\\", "/");
	// encrypted?
	if (entry.isEncrypted())
	  continue;
	// extract
	if (entry.isDirectory()) {
          if (listDirs)
            result.add(new File(entryFilename));
	}
	else {
	  result.add(new File(entryFilename));
	}
      }
    }
    catch (Exception e) {
      LoggingHelper.global().log(Level.SEVERE, "Failed to list files in: " + rarfile, e);
    }
    finally {
      if (rarfile != null) {
	try {
	  rarfile.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }

  /**
   * Checks whether the file is rar compressed.
   * See: https://en.wikipedia.org/wiki/RAR_(file_format)
   *
   * @param file	the file to inspect
   * @return		true if gzip
   */
  public static boolean isRarCompressed(File file) {
    byte[]	data;

    data = FileUtils.loadFromBinaryFile(file, 6);
    if (data != null)
      return isRarCompressed(data);
    else
      return false;
  }

  /**
   * Checks whether the array is rar compressed.
   * See: https://en.wikipedia.org/wiki/RAR_(file_format)
   *
   * @param data  	the data to inspect
   * @return		true if gzip
   */
  public static boolean isRarCompressed(byte[] data) {
    if (data.length >= 6)
      return (data[0] == (byte) 'R')
	&& (data[1] == (byte) 'a')
	&& (data[2] == (byte) 'r')
	&& (data[3] == (byte) '!')
	&& (data[4] == (byte) 0x1A)
	&& (data[5] == (byte) 0x07);
    else
      return false;
  }
}
