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
 * TarUtils.java
 * Copyright (C) 2011-2024 University of Waikato, Hamilton, New Zealand
 * Copyright (C) 2010 jcscoobyrs
 */
package adams.core.io;

import adams.core.License;
import adams.core.MessageCollection;
import adams.core.annotation.MixedCopyright;
import adams.core.base.BaseRegExp;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import com.ning.compress.lzf.LZFInputStream;
import com.ning.compress.lzf.LZFOutputStream;
import lzma.sdk.lzma.Decoder;
import lzma.streams.LzmaInputStream;
import lzma.streams.LzmaOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * A helper class for Tar-file related tasks.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class TarUtils {

  /** for logging errors. */
  protected static Logger LOGGER = LoggingHelper.getLogger(TarUtils.class);

  /**
   * The type of compression to use.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum Compression {
    /** automatic. */
    AUTO,
    /** no compression. */
    NONE,
    /** gzip. */
    GZIP,
    /** bzip2. */
    BZIP2,
    /** lzf. */
    LZF,
    /** lzma. */
    LZMA,
    /** XZ. */
    XZ,
    /** Zstd. */
    ZSTD,
  }

  /**
   * Returns the list of supported compression extensions.
   *
   * @return		the list of extensions
   */
  public static List<String> getSupportedCompressionExtensions() {
    List<String>	result;

    result = new ArrayList<>();
    for (Compression c: Compression.values()) {
      switch (c) {
	case AUTO:
	case NONE:
	  // ignored
	  continue;
	case GZIP:
	  result.add(".gz");
	  break;
	case BZIP2:
	  result.add(".bz2");
	  break;
	case XZ:
	  result.add(".xz");
	  break;
	case ZSTD:
	  result.add(".zst");
	  break;
	case LZMA:
	  result.add(".7z");
	  break;
	case LZF:
	  result.add(".lzf");
	  break;
	default:
	  throw new IllegalStateException("Unhandled compression: " + c);
      }
    }

    Collections.sort(result);

    return result;
  }

  /**
   * Determines the compression based on the file extension.
   * Falls back on "no" compression if unknown extension.
   *
   * @param archive	the archive to determine the compression for
   * @return		the compression
   * @throws Exception	if unsupported extension
   */
  public static Compression determineCompression(File archive) throws Exception {
    return determineCompression(archive.getAbsolutePath(), false);
  }

  /**
   * Determines the compression based on the file extension.
   *
   * @param archive	the archive to determine the compression for
   * @param strict 	fail if unsupported extension
   * @return		the compression
   * @throws Exception	if unsupported extension
   */
  public static Compression determineCompression(File archive, boolean strict) throws Exception {
    return determineCompression(archive.getAbsolutePath(), strict);
  }

  /**
   * Determines the compression based on the file extension.
   * Falls back on "no" compression if unknown extension.
   *
   * @param archive	the archive to determine the compression for
   * @return		the compression
   * @throws Exception	if unsupported extension
   */
  public static Compression determineCompression(String archive) throws Exception {
    return determineCompression(archive, false);
  }

  /**
   * Determines the compression based on the file extension.
   *
   * @param archive	the archive to determine the compression for
   * @param strict 	fail if unsupported extension
   * @return		the compression
   * @throws Exception	if unsupported extension
   */
  public static Compression determineCompression(String archive, boolean strict) throws Exception {
    archive = archive.toLowerCase();

    if (archive.endsWith(".tar.gz"))
      return Compression.GZIP;
    else if (archive.endsWith(".tgz"))
      return Compression.GZIP;
    else if (archive.endsWith(".tar.bz2"))
      return Compression.BZIP2;
    else if (archive.endsWith(".tar.7z"))
      return Compression.LZMA;
    else if (archive.endsWith(".tar.lzf"))
      return Compression.LZF;
    else if (archive.endsWith(".tar.xz"))
      return Compression.XZ;
    else if (archive.endsWith(".tar.zst"))
      return Compression.ZSTD;

    if (strict) {
      if (archive.endsWith(".tar"))
	return Compression.NONE;
      else
	throw new IllegalArgumentException("Unsupported extension: " + FileUtils.getExtension(archive));
    }
    else {
      return Compression.NONE;
    }
  }

  /**
   * Returns an input stream for the specified tar archive. Automatically
   * determines the compression used for the archive.
   *
   * @param file	the tar archive to create the input stream for
   * @param stream	the stream to wrap
   * @return		the input stream
   * @throws Exception	if file not found or similar problems
   */
  public static TarArchiveInputStream openArchiveForReading(File file, FileInputStream stream) throws Exception {
    Compression		comp;

    comp = determineCompression(file, true);
    if (comp == Compression.GZIP)
      return new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(stream)));
    else if (comp == Compression.BZIP2)
      return new TarArchiveInputStream(new BZip2CompressorInputStream(new BufferedInputStream(stream)));
    else if (comp == Compression.LZF)
      return new TarArchiveInputStream(new LZFInputStream(new BufferedInputStream(stream)));
    else if (comp == Compression.LZMA)
      return new TarArchiveInputStream(new LzmaInputStream(new BufferedInputStream(stream), new Decoder()));
    else if (comp == Compression.XZ)
      return new TarArchiveInputStream(new XZInputStream(new BufferedInputStream(stream)));
    else if (comp == Compression.ZSTD)
      return new TarArchiveInputStream(new ZstdInputStream(new BufferedInputStream(stream)));
    else
      return new TarArchiveInputStream(new BufferedInputStream(stream));
  }

  /**
   * Returns an output stream for the specified tar archive. Automatically
   * determines the compression used for the archive. Uses GNU long filename
   * support.
   *
   * @param input	the tar archive to create the output stream for
   * @param stream	the output stream to wrap
   * @return		the output stream
   * @throws Exception	if file not found or similar problems
   * @see		TarArchiveOutputStream#LONGFILE_GNU
   */
  public static TarArchiveOutputStream openArchiveForWriting(File input, FileOutputStream stream) throws Exception {
    TarArchiveOutputStream	result;
    Compression			comp;

    comp = determineCompression(input, true);
    if (comp == Compression.GZIP)
      result = new TarArchiveOutputStream(new GzipCompressorOutputStream(new BufferedOutputStream(stream)));
    else if (comp == Compression.BZIP2)
      result = new TarArchiveOutputStream(new BZip2CompressorOutputStream(new BufferedOutputStream(stream)));
    else if (comp == Compression.LZF)
      result = new TarArchiveOutputStream(new LZFOutputStream(new BufferedOutputStream(stream)));
    else if (comp == Compression.LZMA)
      result = new TarArchiveOutputStream(new LzmaOutputStream.Builder(new BufferedOutputStream(stream)).build());
    else if (comp == Compression.XZ)
      result = new TarArchiveOutputStream(new XZOutputStream(new BufferedOutputStream(stream), new LZMA2Options()));
    else if (comp == Compression.ZSTD)
      result = new TarArchiveOutputStream(new ZstdOutputStream(new BufferedOutputStream(stream)));
    else
      result = new TarArchiveOutputStream(new BufferedOutputStream(stream));

    result.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

    return result;
  }

  /**
   * Creates a tar file from the specified files.
   *
   * @param output	the output file to generate
   * @param files	the files to store in the tar file
   * @return		null if successful, otherwise error message
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
   */
  public static String compress(File output, File[] files, int bufferSize) {
    return compress(output, files, "", bufferSize);
  }

  /**
   * Creates a tar file from the specified files.
   * <br><br>
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
    FileInputStream             fis;
    FileOutputStream		fos;
    String			filename;
    String			msg;
    TarArchiveEntry		entry;

    in     = null;
    fis    = null;
    out    = null;
    fos    = null;
    result = null;
    try {
      // does file already exist?
      if (output.exists())
	LOGGER.warning("overwriting '" + output + "'!");

      // create tar file
      buf = new byte[bufferSize];
      fos = new FileOutputStream(output.getAbsolutePath());
      out = openArchiveForWriting(output, fos);
      for (i = 0; i < files.length; i++) {
	fis = new FileInputStream(files[i].getAbsolutePath());
	in  = new BufferedInputStream(fis);

	// Add tar entry to output stream.
	filename = files[i].getParentFile().getAbsolutePath();
	if (!stripRegExp.isEmpty())
	  filename = filename.replaceFirst(stripRegExp, "");
	if (!filename.isEmpty())
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
	FileUtils.closeQuietly(in);
	FileUtils.closeQuietly(fis);
	in  = null;
	fis = null;
      }

      // Complete the tar file
      FileUtils.closeQuietly(out);
      FileUtils.closeQuietly(fos);
      out = null;
      fos = null;
    }
    catch (Exception e) {
      msg = "Failed to generate archive '" + output + "': ";
      LOGGER.log(Level.SEVERE, msg, e);
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
   * Decompresses the files in a tar file. Does not recreate the directory structure
   * stored in the tar file.
   *
   * @param input	the tar file to decompress
   * @param outputDir	the directory where to store the extracted files
   * @return		the successfully extracted files
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
    return decompress(input, outputDir, createDirs, match, invertMatch, bufferSize, new MessageCollection());
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
  public static List<File> decompress(File input, File outputDir, boolean createDirs, BaseRegExp match, boolean invertMatch, int bufferSize, MessageCollection errors) {
    List<File>			result;
    FileInputStream		fis;
    TarArchiveInputStream	archive;
    TarArchiveEntry		entry;
    File			outFile;
    String			outName;
    byte[]			buffer;
    BufferedOutputStream	out;
    FileOutputStream		fos;
    int				len;
    String			msg;
    long			size;
    long			read;

    result  = new ArrayList<>();
    archive = null;
    fis     = null;
    fos     = null;
    try {
      // decompress archive
      buffer  = new byte[bufferSize];
      fis     = new FileInputStream(input.getAbsoluteFile());
      archive = openArchiveForReading(input, fis);
      while ((entry = archive.getNextEntry()) != null) {
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
	    msg = "Failed to create directory '" + outFile.getAbsolutePath() + "'!";
	    LOGGER.log(Level.SEVERE, msg);
	    errors.add(msg);
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
	    msg = "Error extracting '" + entry.getName() + "' to '" + outName + "': ";
	    LOGGER.log(Level.SEVERE, msg, e);
	    errors.add(msg, e);
	  }
	  finally {
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
      FileUtils.closeQuietly(fis);
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
    return decompress(input, archiveFile, output, createDirs, 1024, new MessageCollection());
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
  public static boolean decompress(File input, String archiveFile, File output, boolean createDirs, int bufferSize, MessageCollection errors) {
    boolean			result;
    FileInputStream		fis;
    TarArchiveInputStream	archive;
    TarArchiveEntry		entry;
    File			outFile;
    String			outName;
    byte[]			buffer;
    FileOutputStream		fos;
    BufferedOutputStream	out;
    int				len;
    String			msg;
    long			size;
    long			read;

    result  = false;
    archive = null;
    fis     = null;
    fos     = null;
    try {
      // decompress archive
      buffer  = new byte[bufferSize];
      fis     = new FileInputStream(input.getAbsoluteFile());
      archive = openArchiveForReading(input, fis);
      while ((entry = archive.getNextEntry()) != null) {
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
	  fos = new FileOutputStream(outName);
	  out = new BufferedOutputStream(fos, bufferSize);
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
	  msg = "Error extracting '" + entry.getName() + "' to '" + outName + "': ";
	  LOGGER.log(Level.SEVERE, msg, e);
	  errors.add(msg, e);
	}
	finally {
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
      FileUtils.closeQuietly(fis);
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
    List<File>			result;
    FileInputStream		fis;
    TarArchiveInputStream	archive;
    TarArchiveEntry		entry;

    result  = new ArrayList<>();
    archive = null;
    fis     = null;
    try {
      fis     = new FileInputStream(input.getAbsolutePath());
      archive = openArchiveForReading(input, fis);
      while ((entry = archive.getNextEntry()) != null) {
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
      LOGGER.log(Level.SEVERE, "Error occurred: ", e);
    }
    finally {
      FileUtils.closeQuietly(fis);
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
