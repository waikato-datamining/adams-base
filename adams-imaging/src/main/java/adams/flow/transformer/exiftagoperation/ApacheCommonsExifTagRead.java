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
 * ApacheCommonsExifTagRead.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.exiftagoperation;

import adams.core.License;
import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Unknown;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoByte;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoDouble;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoFloat;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoRational;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShort;

import java.io.File;

/**
 * Reads the specified tag from the file and forwards the information.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@MixedCopyright(
  author = "yurko - https://stackoverflow.com/users/418516/yurko",
  url = "https://stackoverflow.com/a/36873897/4698227",
  license = License.CC_BY_SA_3,
  note = "general usage of Apache Commons Imaging for EXIF operations"
)
public class ApacheCommonsExifTagRead
  extends AbstractApacheCommonsExifTagOperation {

  private static final long serialVersionUID = -4257460091938302125L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads the specified tag from the file and forwards the information.";
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
    if (m_Tag.getTagInfo() instanceof TagInfoAscii)
      return new Class[]{String.class};
    else if (m_Tag.getTagInfo() instanceof TagInfoByte)
      return new Class[]{Byte.class};
    else if (m_Tag.getTagInfo() instanceof TagInfoShort)
      return new Class[]{Short.class};
    else if (m_Tag.getTagInfo() instanceof TagInfoDouble)
      return new Class[]{Double.class};
    else if (m_Tag.getTagInfo() instanceof TagInfoFloat)
      return new Class[]{Float.class};
    else if (m_Tag.getTagInfo() instanceof TagInfoRational)
      return new Class[]{Double.class};
    else
      return new Class[]{Unknown.class};
  }

  /**
   * Hook method for performing checks before processing the data.
   *
   * @param input	the input to process
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(Object input) {
    String	result;

    result = super.check(input);

    if (result == null) {
      if (!(m_Tag.getTagInfo() instanceof TagInfoAscii)
	&& !(m_Tag.getTagInfo() instanceof TagInfoByte)
	&& !(m_Tag.getTagInfo() instanceof TagInfoShort)
	&& !(m_Tag.getTagInfo() instanceof TagInfoDouble)
	&& !(m_Tag.getTagInfo() instanceof TagInfoFloat)
	&& !(m_Tag.getTagInfo() instanceof TagInfoRational))
	result = "Unhandled tag info type: " + Utils.classToString(m_Tag.getTagInfo());
    }

    return result;
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
    Object		result;
    File 		inputFile;
    JpegImageMetadata 	meta;
    TiffImageMetadata 	exif;

    result = null;

    if (input instanceof String)
      inputFile = new PlaceholderFile((String) input).getAbsoluteFile();
    else
      inputFile = ((File) input).getAbsoluteFile();

    try {
      meta = (JpegImageMetadata) Imaging.getMetadata(inputFile);
      if (meta != null) {
	exif = meta.getExif();
	if (exif != null) {
	  result = exif.getFieldValue(m_Tag.getTagInfo());
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

    return result;
  }
}
