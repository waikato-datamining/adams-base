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
 * MemoryMonitor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone;

import adams.core.ByteFormat;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseText;
import adams.flow.core.RunnableWithLogging;
import adams.flow.sink.sendnotification.AbstractNotification;
import adams.flow.sink.sendnotification.Null;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 <!-- globalinfo-start -->
 * Monitors the memory (used heap vs maximum heap).Every number of seconds ('sampleInterval'), the memory consumption, i.e., 'heap used', is sampled, keeping the specified number of latest samples ('numSamples').<br>
 * Once the specified number of samples have been reached, it is checked whether the specified percentage of samples ('coverage') reaches or exceeds the threshold percentage of the maximum heap has been exceeded ('threshold'). If that should be the case, a notification is sent.<br>
 * After a notification has been sent out, a minimum wait time in seconds is imposed before sending out another one ('notificationWait').<br>
 * Available placeholders for the message template:<br>
 * - {threshold}<br>
 * - {numsamples}<br>
 * - {coverage}<br>
 * - {maxheap}
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
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
 * &nbsp;&nbsp;&nbsp;default: MemoryMonitor
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
 * <pre>-sample-interval &lt;int&gt; (property: sampleInterval)
 * &nbsp;&nbsp;&nbsp;The sample interval in seconds.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-num-samples &lt;int&gt; (property: numSamples)
 * &nbsp;&nbsp;&nbsp;The number of samples to use for making a decision.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-threshold &lt;double&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;The percentage of the maximum heap that the used heap must reach&#47;exceed
 * &nbsp;&nbsp;&nbsp;(0-100).
 * &nbsp;&nbsp;&nbsp;default: 80.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 100.0
 * </pre>
 *
 * <pre>-coverage &lt;double&gt; (property: coverage)
 * &nbsp;&nbsp;&nbsp;The percentage of samples that reach&#47;exceed the threshold (0-100).
 * &nbsp;&nbsp;&nbsp;default: 75.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 100.0
 * </pre>
 *
 * <pre>-notification &lt;adams.flow.sink.sendnotification.AbstractNotification&gt; (property: notification)
 * &nbsp;&nbsp;&nbsp;The notification to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.sendnotification.Null
 * </pre>
 *
 * <pre>-message-template &lt;adams.core.base.BaseText&gt; (property: messageTemplate)
 * &nbsp;&nbsp;&nbsp;The message template to use.
 * &nbsp;&nbsp;&nbsp;default: {coverage}% of {numsamples} samples have exceeded the threshold of {threshold} of the maximum heap of {maxheap}.
 * </pre>
 *
 * <pre>-notification-wait &lt;int&gt; (property: notificationWait)
 * &nbsp;&nbsp;&nbsp;The number of seconds to wait before sending out another notification.
 * &nbsp;&nbsp;&nbsp;default: 86400
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MemoryMonitor
  extends AbstractStandalone {

  private static final long serialVersionUID = -7501219980546039648L;

  public final static String PH_THRESHOLD_PERC = "{threshold_perc}";

  public final static String PH_THRESHOLD_BYTES = "{threshold_bytes}";

  public final static String PH_NUM_SAMPLES = "{num_samples}";

  public final static String PH_COVERAGE_PERC = "{coverage_perc}";

  public final static String PH_COVERAGE_NUM = "{coverage_num}";

  public final static String PH_MAX_HEAP_BYTES = "{max_heap_bytes}";

  /** the sample interval in seconds. */
  protected int m_SampleInterval;

  /** the number of samples to keep. */
  protected int m_NumSamples;

  /** the percentage threshold of max heap that the used heap will have to cross (0-100). */
  protected double m_Threshold;

  /** the coverage percentage of samples reaching/exceeding the threshold (0-100). */
  protected double m_Coverage;

  /** the notification scheme to use. */
  protected AbstractNotification m_Notification;

  /** the notification message template. */
  protected BaseText m_MessageTemplate;

  /** the wait time in seconds after sending out another notification. */
  protected int m_NotificationWait;

  /** the runnable performing the sampling. */
  protected transient RunnableWithLogging m_Sampler;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Monitors the memory (used heap vs maximum heap)."
      + "Every number of seconds ('sampleInterval'), the memory consumption, "
      + "i.e., 'heap used', is sampled, keeping the specified number "
      + "of latest samples ('numSamples').\n"
      + "Once the specified number of samples have been reached, it is checked "
      + "whether the specified percentage of samples ('coverage') reaches or "
      + "exceeds the threshold percentage of the maximum heap has been exceeded "
      + "('threshold'). If that should be the case, a notification is sent.\n"
      + "After a notification has been sent out, a minimum wait time in seconds "
      + "is imposed before sending out another one ('notificationWait').\n"
      + "Available placeholders for the message template:\n"
      + "- " + PH_THRESHOLD_PERC + "\n"
      + "- " + PH_THRESHOLD_BYTES + "\n"
      + "- " + PH_NUM_SAMPLES + "\n"
      + "- " + PH_COVERAGE_PERC + "\n"
      + "- " + PH_COVERAGE_NUM + "\n"
      + "- " + PH_MAX_HEAP_BYTES;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "sample-interval", "sampleInterval",
      10, 1, null);

    m_OptionManager.add(
      "num-samples", "numSamples",
      100, 1, null);

    m_OptionManager.add(
      "threshold", "threshold",
      80.0, 0.0, 100.0);

    m_OptionManager.add(
      "coverage", "coverage",
      75.0, 0.0, 100.0);

    m_OptionManager.add(
      "notification", "notification",
      new Null());

    m_OptionManager.add(
      "message-template", "messageTemplate",
      new BaseText(
        PH_COVERAGE_PERC + "% of " + PH_NUM_SAMPLES + " samples have exceeded the "
	  + "threshold of " + PH_THRESHOLD_PERC + "% (= " + PH_THRESHOLD_BYTES + ") of the maximum heap of "
	  + PH_MAX_HEAP_BYTES + "."));

    m_OptionManager.add(
      "notification-wait", "notificationWait",
      24 * 60 * 60, 1, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "sampleInterval", m_SampleInterval, "interval: ");
    result += QuickInfoHelper.toString(this, "numSamples", m_NumSamples, ", num: ");
    result += QuickInfoHelper.toString(this, "threshold", m_Threshold, ", threshold: ");
    result += QuickInfoHelper.toString(this, "coverage", m_Coverage, ", coverage: ");
    result += QuickInfoHelper.toString(this, "notification", m_Notification, ", notification: ");
    result += QuickInfoHelper.toString(this, "notificationWait", m_NotificationWait, ", wait: ");

    return result;
  }

  /**
   * Sets the sample interval.
   *
   * @param value	the interval in seconds
   */
  public void setSampleInterval(int value) {
    if (getOptionManager().isValid("sampleInterval", value)) {
      m_SampleInterval = value;
      reset();
    }
  }

  /**
   * Returns the sample interval.
   *
   * @return		the interval in seconds
   */
  public int getSampleInterval() {
    return m_SampleInterval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sampleIntervalTipText() {
    return "The sample interval in seconds.";
  }

  /**
   * Sets the number of samples.
   *
   * @param value	the number of samples
   */
  public void setNumSamples(int value) {
    if (getOptionManager().isValid("numSamples", value)) {
      m_NumSamples = value;
      reset();
    }
  }

  /**
   * Returns the number of samples.
   *
   * @return		the number of samples
   */
  public int getNumSamples() {
    return m_NumSamples;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numSamplesTipText() {
    return "The number of samples to use for making a decision.";
  }

  /**
   * Sets the percentage of the maximum heap that the used heap must reach/exceed.
   *
   * @param value	the threshold (0-100)
   */
  public void setThreshold(double value) {
    if (getOptionManager().isValid("threshold", value)) {
      m_Threshold = value;
      reset();
    }
  }

  /**
   * Returns the percentage of the maximum heap that the used heap must reach/exceed.
   *
   * @return		the threshold (0-100)
   */
  public double getThreshold() {
    return m_Threshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String thresholdTipText() {
    return "The percentage of the maximum heap that the used heap must reach/exceed (0-100).";
  }

  /**
   * Sets the percentage of samples that must reach/exceed the threshold.
   *
   * @param value	the coverage (0-1)
   */
  public void setCoverage(double value) {
    if (getOptionManager().isValid("coverage", value)) {
      m_Coverage = value;
      reset();
    }
  }

  /**
   * Returns the percentage of samples that must reach/exceed the threshold.
   *
   * @return		the coverage (0-100)
   */
  public double getCoverage() {
    return m_Coverage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String coverageTipText() {
    return "The percentage of samples that reach/exceed the threshold (0-100).";
  }

  /**
   * Sets the notification scheme to use.
   *
   * @param value	the notification
   */
  public void setNotification(AbstractNotification value) {
    m_Notification = value;
    reset();
  }

  /**
   * Returns the notification scheme in use.
   *
   * @return		the notification
   */
  public AbstractNotification getNotification() {
    return m_Notification;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String notificationTipText() {
    return "The notification to use.";
  }

  /**
   * Sets the message template to use.
   *
   * @param value	the template
   */
  public void setMessageTemplate(BaseText value) {
    m_MessageTemplate = value;
    reset();
  }

  /**
   * Returns the message template to use.
   *
   * @return		the template
   */
  public BaseText getMessageTemplate() {
    return m_MessageTemplate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String messageTemplateTipText() {
    return "The message template to use.";
  }

  /**
   * Sets the number of seconds to wait before sending out another notification.
   *
   * @param value	the number of seconds
   */
  public void setNotificationWait(int value) {
    if (getOptionManager().isValid("notificationWait", value)) {
      m_NotificationWait = value;
      reset();
    }
  }

  /**
   * Returns the number of seconds to wait before sending out another notification.
   *
   * @return		the number of seconds
   */
  public int getNotificationWait() {
    return m_NotificationWait;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String notificationWaitTipText() {
    return "The number of seconds to wait before sending out another notification.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    final MemoryMXBean memory;

    memory = ManagementFactory.getMemoryMXBean();
    m_Sampler = new RunnableWithLogging() {
      @Override
      protected void doRun() {
        long max = memory.getHeapMemoryUsage().getMax();
        double threshold = max / 100.0 * m_Threshold;
        double coverage = m_NumSamples / 100.0 * m_Coverage;
        if (isLoggingEnabled()) {
          getLogger().info("max: " + max);
          getLogger().info("threshold: " + threshold);
          getLogger().info("coverage: " + coverage);
          getLogger().info("#samples: " + m_NumSamples);
          getLogger().info("msg: " + m_MessageTemplate);
        }
	TDoubleList samples = new TDoubleArrayList();
	long nextNotification = -1;

	while (!isStopped()) {
	  // wait for next sample to be taken
	  Utils.wait(this, this, m_SampleInterval * 1000, 100);
	  if (isStopped())
	    continue;

	  // add sample
	  samples.add(memory.getHeapMemoryUsage().getUsed());
	  while (samples.size() > m_NumSamples)
	    samples.removeAt(0);


	  // have we reached the notification wait time?
	  if (nextNotification > 0) {
	    if (nextNotification <= System.currentTimeMillis())
	      nextNotification = -1;
	  }
	  if (nextNotification > 0)
	    continue;

	  // enough samples?
	  if (samples.size() == m_NumSamples) {
	    int hits = 0;
	    for (double sample: samples.toArray()) {
	      if (sample >= threshold)
	        hits++;
	    }
	    if (isLoggingEnabled())
	      getLogger().info("hits: " + hits);

	    // enough coverage?
	    if (hits >= coverage) {
              if (isLoggingEnabled())
                getLogger().info("hits >= coverage: " + true);
	      nextNotification = System.currentTimeMillis() + m_NotificationWait * 1000;
              if (isLoggingEnabled())
                getLogger().info("next notification: " + nextNotification);
	      String msg = m_MessageTemplate.getValue();
	      msg = msg.replace(PH_NUM_SAMPLES, "" + m_NumSamples);
	      msg = msg.replace(PH_COVERAGE_PERC, "" + m_Coverage);
	      msg = msg.replace(PH_COVERAGE_NUM, "" + Math.ceil(coverage));
	      msg = msg.replace(PH_THRESHOLD_PERC, "" + m_Threshold);
	      msg = msg.replace(PH_THRESHOLD_BYTES, "" + ByteFormat.toMegaBytes(threshold, 1));
	      msg = msg.replace(PH_MAX_HEAP_BYTES, ByteFormat.toMegaBytes(max, 1));
              if (isLoggingEnabled())
                getLogger().info("msg: " + msg);
	      m_Notification.setFlowContext(MemoryMonitor.this);
	      String result = m_Notification.sendNotification(msg);
	      if (result != null)
	        getLogger().severe(result);
	    }
	  }
	}
      }
    };
    m_Sampler.setLoggingLevel(getLoggingLevel());
    new Thread(m_Sampler).start();

    return null;
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Sampler != null) {
      m_Sampler.stopExecution();
      m_Sampler = null;
    }
    super.stopExecution();
  }
}
