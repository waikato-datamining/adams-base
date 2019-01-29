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
 * ApacheCommonsExifTagWrite.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.exiftagoperation;

import adams.core.License;
import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoByte;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoDouble;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoFloat;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoRational;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShort;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Sets the specified tag value in the file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
@MixedCopyright(
  author = "yurko - https://stackoverflow.com/users/418516/yurko",
  url = "https://stackoverflow.com/a/36873897/4698227",
  license = License.CC_BY_SA_3,
  note = "general usage of Apache Commons Imaging for EXIF operations"
)
public class ApacheCommonsExifTagWrite
  extends AbstractApacheCommonsExifTagOperation<Object,Object>
  implements ExifTagWriteOperation<Object,Object> {

  private static final long serialVersionUID = -4257460091938302125L;

  /** the value to write. */
  protected String m_Value;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sets the specified tag value in the file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "value", "value",
      "");
  }

  /**
   * Sets the value to store.
   *
   * @param value	the value
   */
  public void setValue(String value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the value to store.
   *
   * @return		the value
   */
  public String getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The value to store.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "value", m_Value, ", value: ");

    return result;
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
    Object			result;
    File 			inputFile;
    File			tmpFile;
    JpegImageMetadata 		meta;
    TiffImageMetadata 		exif;
    TiffOutputSet outputSet;
    TiffOutputDirectory exifDir;
    FileOutputStream fos;
    BufferedOutputStream bos;

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
	      if (m_Tag.getTagInfo() instanceof TagInfoAscii)
		exifDir.add((TagInfoAscii) m_Tag.getTagInfo(), m_Value);
	      else if (m_Tag.getTagInfo() instanceof TagInfoByte)
		exifDir.add((TagInfoByte) m_Tag.getTagInfo(), Byte.parseByte(m_Value));
	      else if (m_Tag.getTagInfo() instanceof TagInfoShort)
		exifDir.add((TagInfoShort) m_Tag.getTagInfo(), Short.parseShort(m_Value));
	      else if (m_Tag.getTagInfo() instanceof TagInfoDouble)
		exifDir.add((TagInfoDouble) m_Tag.getTagInfo(), Double.parseDouble(m_Value));
	      else if (m_Tag.getTagInfo() instanceof TagInfoFloat)
		exifDir.add((TagInfoFloat) m_Tag.getTagInfo(), Float.parseFloat(m_Value));
	      else if (m_Tag.getTagInfo() instanceof TagInfoRational)
		exifDir.add((TagInfoRational) m_Tag.getTagInfo(), RationalNumber.valueOf(Double.parseDouble(m_Value)));
	      else
	        errors.add("Unhandled tag info type: " + Utils.classToString(m_Tag.getTagInfo()));
	      if (errors.isEmpty()) {
		fos = new FileOutputStream(tmpFile);
		bos = new BufferedOutputStream(fos);
		new ExifRewriter().updateExifMetadataLossless(inputFile, bos, outputSet);
		if (!FileUtils.copy(tmpFile, inputFile))
		  errors.add("Failed to replace " + inputFile + " with updated EXIF from " + tmpFile);
		if (!FileUtils.delete(tmpFile))
		  errors.add("Failed to delete tmp file: " + tmpFile);
	      }
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
