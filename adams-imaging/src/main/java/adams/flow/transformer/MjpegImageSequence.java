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
 * MjpegImageSequence.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseURI;
import adams.core.base.BaseURL;
import adams.core.io.PlaceholderFile;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import boofcv.io.image.SimpleImageSequence;
import boofcv.io.video.VideoMjpegCodec;
import boofcv.io.wrapper.images.JpegByteImageSequence;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageFloat32;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Streams the individual frames from the MJPEG video file obtained as input.<br>
 * Images are output as boofcv.struct.image.ImageFloat32 (FRAME) or java.awt.image.BufferedImage (GUIIMAGE).<br>
 * In case of output type BOTH, an array of frame (first) and GUI image (second) is output.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * &nbsp;&nbsp;&nbsp;adams.core.base.BaseURL<br>
 * &nbsp;&nbsp;&nbsp;adams.core.base.BaseURI<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.BufferedImageContainer<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: MjpegImageSequence
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-type &lt;FRAME|GUIIMAGE|BOTH&gt; (property: outputType)
 * &nbsp;&nbsp;&nbsp;The output type: frame, GUI image or both.
 * &nbsp;&nbsp;&nbsp;default: GUIIMAGE
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MjpegImageSequence
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 3690378527551302472L;

  /**
   * Defines the output.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum OutputType {
    FRAME,
    GUIIMAGE,
    BOTH
  }

  /** the video. */
  protected transient SimpleImageSequence m_Video;

  /** the output type. */
  protected OutputType m_OutputType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Streams the individual frames from the MJPEG video file obtained as input.\n"
	+ "Images are output as " + ImageFloat32.class.getName() + " (" + OutputType.FRAME + ") "
	+ "or " + BufferedImage.class.getName() + " (" + OutputType.GUIIMAGE + ").\n"
	+ "In case of output type " + OutputType.BOTH + ", an array of frame (first) "
	+ "and GUI image (second) is output.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-type", "outputType",
      OutputType.GUIIMAGE);
  }

  /**
   * Sets the output type.
   *
   * @param value	the type
   */
  public void setOutputType(OutputType value) {
    m_OutputType = value;
    reset();
  }

  /**
   * Returns the output type.
   *
   * @return		the type
   */
  public OutputType getOutputType() {
    return m_OutputType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTypeTipText() {
    return "The output type: frame, GUI image or both.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "outputType", m_OutputType);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class, BaseURL.class, BaseURI.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    switch (m_OutputType) {
      case FRAME:
	return new Class[]{BoofCVImageContainer.class};
      case GUIIMAGE:
	return new Class[]{BufferedImageContainer.class};
      case BOTH:
	return new Class[]{AbstractImageContainer[].class};
      default:
	throw new IllegalStateException("Unhandled output type: " + m_OutputType);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Object		payload;
    String		filename;
    PlaceholderFile	file;
    VideoMjpegCodec 	codec;
    List<byte[]> 	data;

    result = null;

    payload = m_InputToken.getPayload();
    if ((payload instanceof String) || (payload instanceof File)) {
      if (payload instanceof String)
	filename = (String) payload;
      else
	filename = ((File) payload).getAbsolutePath();
      file = new PlaceholderFile(filename);

      try {
	codec   = new VideoMjpegCodec();
	data    = codec.read(new FileInputStream(file.getAbsolutePath()));
	m_Video = new JpegByteImageSequence<ImageFloat32>(ImageFloat32.class, data, false);
      }
      catch (Exception e) {
	result = handleException("Failed to open video file: " + payload, e);
      }
    }
    else if ((payload instanceof BaseURL) || (payload instanceof BaseURI)) {
      try {
	codec = new VideoMjpegCodec();
	if (payload instanceof BaseURL)
	  data = codec.read(((BaseURL) payload).urlValue().openStream());
	else
	  data = codec.read(((BaseURI) payload).uriValue().toURL().openStream());
	m_Video = new JpegByteImageSequence<ImageFloat32>(ImageFloat32.class, data, false);
      }
      catch (Exception e) {
	result = handleException("Failed to open video stream: " + payload, e);
      }
    }
    else {
      result = "Unhandled input type: " + Utils.classToString(payload.getClass());
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_Video != null) && (m_Video.hasNext());
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token			result;
    BufferedImageContainer 	bicont;
    BoofCVImageContainer	boofcont;
    Field 			field;
    ImageBase 			frame;

    frame    = m_Video.next();
    field    = new Field("Frame", DataType.NUMERIC);
    bicont   = null;
    boofcont = null;
    if ((m_OutputType == OutputType.BOTH) || (m_OutputType == OutputType.FRAME)) {
      boofcont = new BoofCVImageContainer();
      boofcont.setImage(frame);
      boofcont.getReport().addField(field);
      boofcont.getReport().setValue(field, m_Video.getFrameNumber());
    }
    if ((m_OutputType == OutputType.BOTH) || (m_OutputType == OutputType.GUIIMAGE)) {
      bicont = new BufferedImageContainer();
      bicont.setImage((BufferedImage) m_Video.getGuiImage());
      bicont.getReport().addField(field);
      bicont.getReport().setValue(field, m_Video.getFrameNumber());
    }

    if ((boofcont != null) && (bicont != null))
      result = new Token(new AbstractImageContainer[]{boofcont, bicont});
    else if (boofcont != null)
      result = new Token(boofcont);
    else
      result = new Token(bicont);

    updateProvenance(result);

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, this, ((Token) cont).getPayload().getClass()));
  }
}
