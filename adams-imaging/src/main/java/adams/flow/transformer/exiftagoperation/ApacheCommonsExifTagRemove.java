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
 * ApacheCommonsExifTagRemove.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.exiftagoperation;

import adams.core.License;
import adams.core.MessageCollection;
import adams.core.annotation.MixedCopyright;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Removes the specified tag from the file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@MixedCopyright(
  author = "yurko - https://stackoverflow.com/users/418516/yurko",
  url = "https://stackoverflow.com/a/36873897/4698227",
  license = License.CC_BY_SA_3,
  note = "general usage of Apache Commons Imaging for EXIF operations"
)
public class ApacheCommonsExifTagRemove
  extends AbstractApacheCommonsExifTagOperation {

  private static final long serialVersionUID = -4257460091938302125L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes the specified tag from the file.";
  }

  /**
   * Returns the type of data that we can process.
   *
   * @return		the type of data
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the type of data that we generate.
   *
   * @return		the type of data
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Processes the incoming data.
   *
   * @param input	the input to process
   * @param errors	for storing errors
   * @return		the generated output
   */
  @Override
  protected Object doProcess(Object input, MessageCollection errors) {
    Object			result;
    File 			inputFile;
    File			tmpFile;
    JpegImageMetadata 		meta;
    TiffImageMetadata 		exif;
    TiffOutputSet 		outputSet;
    TiffOutputDirectory 	exifDir;
    FileOutputStream		fos;
    BufferedOutputStream	bos;

    result = null;

    if (input instanceof String)
      inputFile = new PlaceholderFile((String) input).getAbsoluteFile();
    else
      inputFile = ((File) input).getAbsoluteFile();
    tmpFile = TempUtils.createTempFile(getClass().getSimpleName().toLowerCase() + "-", ".jpg");

    fos = null;
    bos = null;
    try {
      meta = (JpegImageMetadata) Imaging.getMetadata(inputFile);
      if (meta != null) {
	exif = meta.getExif();
	if (exif != null) {
	  outputSet = exif.getOutputSet();
	  if (outputSet != null) {
	    exifDir = outputSet.getOrCreateExifDirectory();
	    if (exifDir != null) {
	      exifDir.removeField(m_Tag.getTagInfo());
	      fos = new FileOutputStream(tmpFile);
	      bos = new BufferedOutputStream(fos);
	      new ExifRewriter().updateExifMetadataLossless(inputFile, bos, outputSet);
	      if (!FileUtils.copy(tmpFile, inputFile))
	        errors.add("Failed to replace " + inputFile + " with updated EXIF from " + tmpFile);
	      if (!FileUtils.delete(tmpFile))
	        errors.add("Failed to delete tmp file: " + tmpFile);
	    }
	    else {
	      errors.add("Failed to obtain EXIF directory: " + input);
	    }
	  }
	  else {
	    errors.add("Failed to obtain output set: " + input);
	  }
	}
	else {
	  errors.add("No EXIF meta-data available: " + input);
	}
      }
      else {
        errors.add("No meta-data available: " + input);
      }
    }
    catch (Exception e) {
      errors.add("Failed to read EXIF tag " + m_Tag + " from: " + input, e);
    }
    finally {
      FileUtils.closeQuietly(bos);
      FileUtils.closeQuietly(fos);
    }

    if (errors.isEmpty())
      result = input;

    return result;
  }
}
