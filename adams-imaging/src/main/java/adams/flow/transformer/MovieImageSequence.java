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
 * MovieImageSequence.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.License;
import adams.core.QuickInfoHelper;
import adams.core.annotation.MixedCopyright;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IError;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Streams the individual frames from a video file obtained as input.<br>
 * Images are output as java.awt.image.BufferedImage.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
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
 * &nbsp;&nbsp;&nbsp;default: MovieImageSequence
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
 * <pre>-interval &lt;int&gt; (property: interval)
 * &nbsp;&nbsp;&nbsp;The interval in milli-seconds to wait before continuing with the execution.
 * &nbsp;&nbsp;&nbsp;default: 1000
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
  author = "Xuggle-Xuggler-Main",
  license = License.LGPL3,
  url = "http://xuggle.googlecode.com/svn/trunk/java/xuggle-xuggler/src/com/xuggle/mediatool/demos/DecodeAndCaptureFrames.java"
)
public class MovieImageSequence
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 3690378527551302472L;

  /** the interval in milli-seconds. */
  protected int m_Interval;

  /** the reader to use. */
  protected transient IMediaReader m_Reader;

  /** the listener to use. */
  protected transient MediaListenerAdapter m_Listener;

  /** the last frame write. */
  protected long m_LastPtsWrite;

  /** the interval for the frames. */
  protected long m_MicroSecondsBetweenFrames;

  /** The video stream index, used to ensure we display frames from one
   * and only one video stream from the media container. */
  protected int m_VideoStreamIndex;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Streams the individual frames from a video file obtained as input.\n"
	+ "Images are output as " + BufferedImage.class.getName() + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "interval", "interval",
      1000);
  }

  /**
   * Sets the interval in milli-seconds to wait.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    m_Interval = value;
    reset();
  }

  /**
   * Returns the interval to wait in milli-seconds.
   *
   * @return		the interval
   */
  public int getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return "The interval in milli-seconds to wait before continuing with the execution.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "interval", m_Interval) + "ms";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{BufferedImageContainer.class};
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
    IError		error;

    result = null;

    // reset
    m_LastPtsWrite              = Global.NO_PTS;
    m_VideoStreamIndex          = -1;
    m_MicroSecondsBetweenFrames = Global.DEFAULT_PTS_PER_SECOND * m_Interval / 1000;

    payload = m_InputToken.getPayload();
    if (m_InputToken.getPayload() instanceof String)
      filename = (String) m_InputToken.getPayload();
    else
      filename = ((File) m_InputToken.getPayload()).getAbsolutePath();
    file = new PlaceholderFile(filename);

    try {
      m_Reader = ToolFactory.makeReader(file.getAbsolutePath());
      m_Reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
      m_Listener = new MediaListenerAdapter() {
	@Override
	public void onVideoPicture(IVideoPictureEvent event) {
	  try {
	    // if the stream index does not match the selected stream index,
	    // then have a closer look
	    if (event.getStreamIndex() != m_VideoStreamIndex) {
	      // if the selected video stream id is not yet set, go ahead an
	      // select this lucky video stream
	      if (m_VideoStreamIndex == -1)
		m_VideoStreamIndex = event.getStreamIndex();
	      else
		return;
	    }
	    // if uninitialized, backdate mLastPtsWrite so we get the very first frame
	    if (m_LastPtsWrite == Global.NO_PTS)
	      m_LastPtsWrite = event.getTimeStamp() - m_MicroSecondsBetweenFrames;
	    // if it's time to write the next frame
	    if (event.getTimeStamp() - m_LastPtsWrite >= m_MicroSecondsBetweenFrames) {
	      BufferedImageContainer cont = new BufferedImageContainer();
	      cont.setImage(event.getImage());
	      Field field = new Field("Frame", DataType.NUMERIC);
	      cont.getReport().addField(field);
	      cont.getReport().setValue(field, event.getStreamIndex());
	      field = new Field("Timestamp", DataType.STRING);
	      DateFormat dformat = DateUtils.getTimestampFormatterMsecs();
	      cont.getReport().addField(field);
	      cont.getReport().setValue(field, dformat.format(new Date(event.getTimeStamp())));
	      m_OutputToken = new Token(cont);
	      // update last write time
	      m_LastPtsWrite += m_MicroSecondsBetweenFrames;
	    }
	  }
	  catch (Exception e) {
	    handleException("Failed to process video event!", e);
	  }
	}
      };
      m_Reader.addListener(m_Listener);
      error = m_Reader.readPacket();
      if (error != null)
	result = "Failed to start reading: " + error.toString();
    }
    catch (Exception e) {
      result = handleException("Failed to open video file: " + payload, e);
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
    return (m_Reader != null) && (m_Reader.isOpen());
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    result = null;

    while ((m_Reader.readPacket() == null) && (m_OutputToken == null)) {
    }

    if (m_OutputToken != null) {
      result        = m_OutputToken;
      m_OutputToken = null;
    }
    else {
      m_Reader.removeListener(m_Listener);
      m_Reader   = null;
      m_Listener = null;
    }

    if (result != null)
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

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    if (m_Reader != null) {
      m_Reader.removeListener(m_Listener);
      if (m_Reader.isOpen())
	m_Reader.close();
      m_Reader = null;
    }
    super.wrapUp();
  }
}
