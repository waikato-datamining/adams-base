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
 * OpenCVDeviceFrameGrabber.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseClassname;
import adams.data.opencv.OpenCVImageContainer;
import adams.flow.core.Token;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Grabs frames from the specified device and forwards them as image containers.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.opencv.OpenCVImageContainer<br>
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
 * &nbsp;&nbsp;&nbsp;default: OpenCVDeviceFrameGrabber
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-frame-grabber-class &lt;adams.core.base.BaseClassname&gt; (property: frameGrabberClass)
 * &nbsp;&nbsp;&nbsp;The frame grabber class to utilize for obtaining the frames.
 * &nbsp;&nbsp;&nbsp;default: org.bytedeco.javacv.OpenCVFrameGrabber
 * </pre>
 *
 * <pre>-device &lt;int&gt; (property: device)
 * &nbsp;&nbsp;&nbsp;The ID of the device to grab frames from.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 *
 * <pre>-delay &lt;long&gt; (property: delay)
 * &nbsp;&nbsp;&nbsp;The delay in microsecond before grabbing a frame.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class OpenCVDeviceFrameGrabber
    extends AbstractSource {

  private static final long serialVersionUID = 5117434255083739200L;

  /** the frame grabber class. */
  protected BaseClassname m_FrameGrabberClass;

  /** the device ID. */
  protected int m_Device;

  /** the delay between frames in microsecond. */
  protected long m_Delay;

  /** the frame grabber in use. */
  protected transient FrameGrabber m_FrameGrabber;

  /** for converting the frames. */
  protected transient OpenCVFrameConverter.ToMat m_Converter;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Grabs frames from the specified device and forwards them as image containers.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "frame-grabber-class", "frameGrabberClass",
        new BaseClassname(OpenCVFrameGrabber.class));

    m_OptionManager.add(
        "device", "device",
        0);

    m_OptionManager.add(
        "delay", "delay",
        0L, 0L, null);
  }

  /**
   * Sets the class of the frame grabber to utilize.
   *
   * @param value	the class
   */
  public void setFrameGrabberClass(BaseClassname value) {
    m_FrameGrabberClass = value;
    reset();
  }

  /**
   * Returns the class of the frame grabber to utilize.
   *
   * @return		the class
   */
  public BaseClassname getFrameGrabberClass() {
    return m_FrameGrabberClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String frameGrabberClassTipText() {
    return "The frame grabber class to utilize for obtaining the frames.";
  }

  /**
   * Sets the device ID.
   *
   * @param value	the ID
   */
  public void setDevice(int value) {
    m_Device = value;
    reset();
  }

  /**
   * Returns the device ID.
   *
   * @return		the ID
   */
  public int getDevice() {
    return m_Device;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String deviceTipText() {
    return "The ID of the device to grab frames from.";
  }

  /**
   * Sets the delay before grabbing a frame in microsecond.
   *
   * @param value	the delay
   */
  public void setDelay(long value) {
    m_Delay = value;
    reset();
  }

  /**
   * Returns the delay before grabbing a frame in microsecond.
   *
   * @return		the delay
   */
  public long getDelay() {
    return m_Delay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String delayTipText() {
    return "The delay in microsecond before grabbing a frame.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{OpenCVImageContainer.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "frameGrabberClass", m_FrameGrabberClass, "grabber: ");
    result += QuickInfoHelper.toString(this, "device", m_Device, ", device: ");
    result += QuickInfoHelper.toString(this, "delay", m_Delay, ", μsec delay: ");

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Constructor constructor;

    result = null;

    try {
      constructor    = m_FrameGrabberClass.classValue().getConstructor(Integer.TYPE);
      m_FrameGrabber = (FrameGrabber) constructor.newInstance(m_Device);
      m_Converter     = new OpenCVFrameConverter.ToMat();
      m_FrameGrabber.start();
    }
    catch (Exception e) {
      result = handleException("Failed to start frame grabber for device " + m_Device + ":", e);
      closeFrameGrabber();
    }

    return result;
  }

  /**
   * Returns the generated token.
   *
   * @return the generated token
   */
  @Override
  public Token output() {
    Token			result;
    Frame			frame;
    Mat 			img;
    OpenCVImageContainer	cont;

    result = null;

    if ((!isStopped()) && (m_FrameGrabber != null)) {
      synchronized (m_FrameGrabber) {
        try {
          if (m_Delay > 0) {
            m_FrameGrabber.delayedGrab(m_Delay);
            frame = m_FrameGrabber.getDelayedFrame();
          }
          else {
            frame = m_FrameGrabber.grab();
          }
          if (frame != null) {
            img = m_Converter.convert(frame.clone());
            cont = new OpenCVImageContainer();
            cont.setContent(img);
            result = new Token(cont);
          }
        }
        catch (Exception e) {
          getLogger().log(Level.SEVERE, "Failed to grab frame, closing frame grabber!", e);
          closeFrameGrabber();
        }
      }
    }

    return result;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   * <br><br>
   * The method is not allowed allowed to return "true" before the
   * actor has been executed. For actors that return an infinite
   * number of tokens, the m_Executed flag can be returned.
   *
   * @return true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    return (m_FrameGrabber != null);
  }

  /**
   * Closes the frame grabber.
   */
  protected void closeFrameGrabber() {
    if (m_FrameGrabber != null) {
      synchronized (m_FrameGrabber) {
        try {
          m_FrameGrabber.close();
        }
        catch (Exception e) {
          getLogger().log(Level.WARNING, "Error closing frame grabber:", e);
        }
        m_FrameGrabber = null;
      }
    }
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    closeFrameGrabber();
  }

  /**
   * Cleans up after the execution has finished. Graphical output is left
   * untouched.
   */
  @Override
  public void wrapUp() {
    super.wrapUp();
    closeFrameGrabber();
  }
}
