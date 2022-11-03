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
 * OpenCVImageWriter.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.core.Utils;
import adams.core.base.BaseInteger;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.OpenCVImageReader;
import adams.data.opencv.ImwriteFlag;
import adams.data.opencv.OpenCVImageContainer;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

/**
 <!-- globalinfo-start -->
 * Writes images using OpenCV. Writing can be influenced via the write flags and their corresponding values.<br>
 * For more information see:<br>
 * https:&#47;&#47;docs.opencv.org&#47;4.6.0&#47;d8&#47;d6a&#47;group__imgcodecs__flags.html#ga292d81be8d76901bff7988d18d2b42ac
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-flag &lt;IMWRITE_JPEG_QUALITY|IMWRITE_JPEG_PROGRESSIVE|IMWRITE_JPEG_OPTIMIZE|IMWRITE_JPEG_RST_INTERVAL|IMWRITE_JPEG_LUMA_QUALITY|IMWRITE_JPEG_CHROMA_QUALITY|IMWRITE_PNG_COMPRESSION|IMWRITE_PNG_STRATEGY|IMWRITE_PNG_BILEVEL|IMWRITE_PXM_BINARY|IMWRITE_EXR_TYPE|IMWRITE_EXR_COMPRESSION|IMWRITE_WEBP_QUALITY|IMWRITE_PAM_TUPLETYPE|IMWRITE_TIFF_RESUNIT|IMWRITE_TIFF_XDPI|IMWRITE_TIFF_YDPI|IMWRITE_TIFF_COMPRESSION|IMWRITE_JPEG2000_COMPRESSION_X1000&gt; [-flag ...] (property: flags)
 * &nbsp;&nbsp;&nbsp;The flags influencing the writing.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-value &lt;adams.core.base.BaseInteger&gt; [-value ...] (property: values)
 * &nbsp;&nbsp;&nbsp;The values corresponding with the flags.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class OpenCVImageWriter
    extends AbstractImageWriter<OpenCVImageContainer> {

  private static final long serialVersionUID = 7557585219819025299L;

  /** the write flags. */
  protected ImwriteFlag[] m_Flags;

  /** the flag values. */
  protected BaseInteger[] m_Values;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes images using OpenCV. Writing can be influenced via the write flags and their corresponding values.\n"
	+ "For more information see:\n"
	+ "https://docs.opencv.org/4.6.0/d8/d6a/group__imgcodecs__flags.html#ga292d81be8d76901bff7988d18d2b42ac";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"flag", "flags",
	new ImwriteFlag[0]);

    m_OptionManager.add(
	"value", "values",
	new BaseInteger[0]);
  }

  /**
   * Sets the write flags.
   *
   * @param value	the flags
   */
  public void setFlags(ImwriteFlag[] value) {
    m_Flags  = value;
    m_Values = (BaseInteger[]) Utils.adjustArray(m_Values, m_Flags.length, new BaseInteger());
    reset();
  }

  /**
   * Returns the write flags.
   *
   * @return		the flags
   */
  public ImwriteFlag[] getFlags() {
    return m_Flags;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String flagsTipText() {
    return "The flags influencing the writing.";
  }

  /**
   * Sets the values corresponding with the flags.
   *
   * @param value	the values
   */
  public void setValues(BaseInteger[] value) {
    m_Values = value;
    m_Flags  = (ImwriteFlag[]) Utils.adjustArray(m_Flags, m_Values.length, ImwriteFlag.IMWRITE_JPEG_QUALITY);
    reset();
  }

  /**
   * Returns the values corresponding with the flags.
   *
   * @return		the values
   */
  public BaseInteger[] getValues() {
    return m_Values;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String valuesTipText() {
    return "The values corresponding with the flags.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return new OpenCVImageReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new OpenCVImageReader().getFormatExtensions();
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return the reader, null if none available
   */
  @Override
  public AbstractImageReader getCorrespondingReader() {
    return new OpenCVImageReader();
  }

  /**
   * Performs the actual writing of the image file.
   *
   * @param file the file to write to
   * @param cont the image container to write
   * @return null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, OpenCVImageContainer cont) {
    TIntList	flags;
    int		i;
    boolean	retVal;

    flags = new TIntArrayList();
    for (i = 0; i < m_Flags.length; i++) {
      flags.add(m_Flags[i].getFlag());
      flags.add(m_Values[i].intValue());
    }

    if (flags.size() == 0)
      retVal = imwrite(file.getAbsolutePath(), cont.getContent());
    else
      retVal = imwrite(file.getAbsolutePath(), cont.getContent(), flags.toArray());

    if (retVal)
      return null;
    else
      return "Failed to write image to (flags: " + Utils.arrayToString(flags.toArray()) + "): " + file;
  }
}
