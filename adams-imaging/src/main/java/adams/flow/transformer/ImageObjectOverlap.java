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
 * ImageObjectOverlap.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.image.AbstractImageContainer;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.report.AbstractField;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.control.StorageName;
import adams.flow.core.Token;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.HashSet;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Computes the overlap of objects with the specified report from storage.<br>
 * It stores the overlap percentage of the highest overlap found (overlap_highest) and the total number of overlaps greater than the specified minimum (overlap_count).<br>
 * If a label key (located object meta-data) has been supplied, then the label of the object with the highest overlap gets stored as well (overlap_label_highest) and whether the labels match (overlap_label_highest_match)
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.image.AbstractImageContainer<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
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
 * &nbsp;&nbsp;&nbsp;default: ImageObjectOverlap
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
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the storage item to merge with (Report or ReportHandler).
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder for locating the objects of interest.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 * <pre>-min-overlap-ratio &lt;double&gt; (property: minOverlapRatio)
 * &nbsp;&nbsp;&nbsp;The minimum ratio that an overlap must have before being considered an actual
 * &nbsp;&nbsp;&nbsp;overlap.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 * <pre>-label-key &lt;java.lang.String&gt; (property: labelKey)
 * &nbsp;&nbsp;&nbsp;The (optional) key for a string label in the meta-data; if supplied the
 * &nbsp;&nbsp;&nbsp;value of the object with the highest overlap gets stored in the report using
 * &nbsp;&nbsp;&nbsp;overlap_label_highest, overlap_label_highest_match stores whether the labels
 * &nbsp;&nbsp;&nbsp;match.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-use-other-object &lt;boolean&gt; (property: useOtherObject)
 * &nbsp;&nbsp;&nbsp;If enabled, the object data from the other report is used&#47;forwarded in case
 * &nbsp;&nbsp;&nbsp;of an overlap.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-additional-object &lt;boolean&gt; (property: additionalObject)
 * &nbsp;&nbsp;&nbsp;If enabled, the additional predicted objects not present in actual objects
 * &nbsp;&nbsp;&nbsp;will be checked.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageObjectOverlap
  extends AbstractTransformer {

  private static final long serialVersionUID = 8175397929496972306L;

  /** the highest overlap percentage. */
  public final static String OVERLAP_PERCENTAGE_HIGHEST = "overlap_highest";

  /** the label of the highest overlap. */
  public final static String OVERLAP_LABEL_HIGHEST = "overlap_label_highest";

  /** whether the labels of the highest overlap match. */
  public final static String OVERLAP_LABEL_HIGHEST_MATCH = "overlap_label_highest_match";

  /** the overlap count. */
  public final static String OVERLAP_COUNT = "overlap_count";

  /** the additional objects boolean. */
  public final static String ADDITIONAL_OBJ = "additional_object";

  /** the placeholder for unknown label. */
  public static final String UNKNOWN_LABEL = "???";

  /** the storage item. */
  protected StorageName m_StorageName;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the minimum overlap ratio to use. */
  protected double m_MinOverlapRatio;

  /** the label meta-data key - ignored if empty. */
  protected String m_LabelKey;

  /** whether to use the other object in the output in case of an overlap. */
  protected boolean m_UseOtherObject;

  /** whether to check for additional predicted objects not present in actual. */
  protected boolean m_AdditionalObject;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Computes the overlap of objects with the specified report from storage.\n"
        + "It stores the overlap percentage of the highest overlap found (" + OVERLAP_PERCENTAGE_HIGHEST + ") and the "
        + "total number of overlaps greater than the specified minimum (" + OVERLAP_COUNT + ").\n"
        + "If a label key (located object meta-data) has been supplied, then the label of the object with "
        + "the highest overlap gets stored as well (" + OVERLAP_LABEL_HIGHEST + ") and whether the "
        + "labels match (" + OVERLAP_LABEL_HIGHEST_MATCH + ")";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName());

    m_OptionManager.add(
      "finder", "finder",
      new AllFinder());

    m_OptionManager.add(
      "min-overlap-ratio", "minOverlapRatio",
      0.0, 0.0, 1.0);

    m_OptionManager.add(
      "label-key", "labelKey",
      "");

    m_OptionManager.add(
      "use-other-object", "useOtherObject",
      false);

    m_OptionManager.add(
      "additional-object", "additionalObject",
      false);
  }

  /**
   * Sets the name of the storage item to merge with (Report or ReportHandler).
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the storage item to merge with (Report or ReportHandler).
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the storage item to merge with (Report or ReportHandler).";
  }

  /**
   * Sets the object finder for locating the objects.
   *
   * @param value 	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns object finder for locating the objects.
   *
   * @return 		the finder
   */
  public ObjectFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finderTipText() {
    return "The object finder for locating the objects of interest.";
  }

  /**
   * Sets the minimum overlap ratio to use.
   *
   * @param value 	the minimum ratio
   */
  public void setMinOverlapRatio(double value) {
    if (getOptionManager().isValid("minOverlapRatio", value)) {
      m_MinOverlapRatio = value;
      reset();
    }
  }

  /**
   * Returns the minimum overlap ratio to use.
   *
   * @return 		the minimum ratio
   */
  public double getMinOverlapRatio() {
    return m_MinOverlapRatio;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minOverlapRatioTipText() {
    return "The minimum ratio that an overlap must have before being considered an actual overlap.";
  }

  /**
   * Sets the (optional) key for a string label in the meta-data; if supplied
   * the value of the object with the highest overlap gets stored in the
   * report using {@link #OVERLAP_LABEL_HIGHEST}, {@link #OVERLAP_LABEL_HIGHEST_MATCH}
   * stores whether the labels match.
   *
   * @param value	the key, ignored if empty
   */
  public void setLabelKey(String value) {
    m_LabelKey = value;
    reset();
  }

  /**
   * Returns the (optional) key for a string label in the meta-data; if supplied
   * the value of the object with the highest overlap gets stored in the
   * report using {@link #OVERLAP_LABEL_HIGHEST}, {@link #OVERLAP_LABEL_HIGHEST_MATCH}
   * stores whether the labels match.
   *
   * @return		the key, ignored if empty
   */
  public String getLabelKey() {
    return m_LabelKey;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelKeyTipText() {
    return "The (optional) key for a string label in the meta-data; if supplied "
      + "the value of the object with the highest overlap gets stored in the "
      + "report using " + OVERLAP_LABEL_HIGHEST + ", "
      + OVERLAP_LABEL_HIGHEST_MATCH + " stores whether the labels match.";
  }

  /**
   * Sets whether to use/forward other object data.
   *
   * @param value	true if to use other object
   */
  public void setUseOtherObject(boolean value) {
    m_UseOtherObject = value;
    reset();
  }

  /**
   * Returns whether to use/forward other object data.
   *
   * @return		true if to use other object
   */
  public boolean getUseOtherObject() {
    return m_UseOtherObject;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useOtherObjectTipText() {
    return "If enabled, the object data from the other report is used/forwarded in case of an overlap.";
  }

  /**
   * Sets whether to count additional predicted objects.
   *
   * @param value	true if to count additional predicted objects
   */
  public void setAdditionalObject(boolean value) {
    m_AdditionalObject = value;
    reset();
  }

  /**
   * Returns whether to count additional predicted objects.
   *
   * @return		true if to count additional predicted objects
   */
  public boolean getAdditionalObject() {
    return m_AdditionalObject;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalObjectTipText() {
    return "If enabled, the additional predicted objects not present in actual objects will be checked.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String    result;

    result  = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    result += QuickInfoHelper.toString(this, "finder", m_Finder, ", finder: ");
    result += QuickInfoHelper.toString(this, "minOverlapRatio", m_MinOverlapRatio, ", overlap ratio: ");
    result += QuickInfoHelper.toString(this, "labelKey", (m_LabelKey.isEmpty() ? "-none-" : m_LabelKey), ", label key: ");
    result += QuickInfoHelper.toString(this, "useOtherObject", m_UseOtherObject, "use other obj", ", ");
    result += QuickInfoHelper.toString(this, "additionalObject", m_AdditionalObject, "additional obj", ", ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{AbstractImageContainer.class, Report.class, ReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{AbstractImageContainer.class, Report.class, ReportHandler.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Object			obj;
    Report			newReport;
    Report			thisReport;
    Report 			otherReport;
    LocatedObject		actObj;
    LocatedObjects		thisObjs;
    LocatedObjects		otherObjs;
    LocatedObjects 		newObjs;
    int 			count;
    double 			overlapHighest;
    String			thisLabel;
    String			labelHighest;
    double			ratio;
    Object			output;
    boolean                     additionalObj;

    result = null;

    output      = null;
    thisReport  = null;
    otherReport = null;

    additionalObj = false;

    if (m_InputToken.getPayload() instanceof AbstractImageContainer)
      thisReport = ((AbstractImageContainer) m_InputToken.getPayload()).getReport();
    else if (m_InputToken.getPayload() instanceof Report)
      thisReport = (Report) m_InputToken.getPayload();
    else if (m_InputToken.getPayload() instanceof ReportHandler)
      thisReport = ((ReportHandler) m_InputToken.getPayload()).getReport();
    else
      result = "Unsupported input class: " + Utils.classToString(m_InputToken.getPayload());

    if (thisReport != null) {
      obj = getStorageHandler().getStorage().get(m_StorageName);
      if (obj == null)
        result = "Failed to retrieve storage item: " + m_StorageName;
      else {
        if (obj instanceof Report)
          otherReport = (Report) obj;
        else if (obj instanceof ReportHandler)
          otherReport = ((ReportHandler) obj).getReport();
        else
          result = "Unhandled type of storage item '" + m_StorageName + "': " + Utils.classToString(obj);
      }
    }

    if (otherReport != null) {
      thisObjs  = m_Finder.findObjects(LocatedObjects.fromReport(thisReport,  m_Finder.getPrefix()));
      otherObjs = m_Finder.findObjects(LocatedObjects.fromReport(otherReport, m_Finder.getPrefix()));
      newObjs   = new LocatedObjects();
      if (thisObjs.size() == 0) {
        newObjs = otherObjs;
      }
      else {
        Set<LocatedObject> matchingObjects = new HashSet<>();
        for (LocatedObject thisObj : thisObjs) {
          count          = 0;
          overlapHighest = 0.0;
          labelHighest   = UNKNOWN_LABEL;
          thisLabel      = UNKNOWN_LABEL;
          if (!m_LabelKey.isEmpty() && thisObj.getMetaData().containsKey(m_LabelKey))
            thisLabel = "" + thisObj.getMetaData().get(m_LabelKey);
          actObj = thisObj;
          for (LocatedObject otherObj : otherObjs) {
            ratio = thisObj.overlapRatio(otherObj);
            if (ratio >= m_MinOverlapRatio) {
              count++;
              if (ratio > overlapHighest) {
                if (m_UseOtherObject)
                  actObj = otherObj;
                overlapHighest = ratio;
                if (!m_LabelKey.isEmpty()) {
                  if (otherObj.getMetaData().containsKey(m_LabelKey)) {
                    labelHighest = "" + otherObj.getMetaData().get(m_LabelKey);
                    matchingObjects.add(otherObj);
                  }
                  else
                    labelHighest = UNKNOWN_LABEL;
                }
                else {
		  matchingObjects.add(otherObj);
		}
              }
            }
          }
          actObj = actObj.getClone();
          actObj.getMetaData().put(OVERLAP_COUNT, count);
          actObj.getMetaData().put(OVERLAP_PERCENTAGE_HIGHEST, overlapHighest);
          if (!m_LabelKey.isEmpty()) {
            actObj.getMetaData().put(OVERLAP_LABEL_HIGHEST, labelHighest);
            actObj.getMetaData().put(OVERLAP_LABEL_HIGHEST_MATCH, thisLabel.equals(labelHighest));
          }
          if (m_AdditionalObject)
            actObj.getMetaData().put(ADDITIONAL_OBJ, additionalObj);
          newObjs.add(actObj);
        }
        if (m_AdditionalObject) {
          additionalObj = true;
          for (LocatedObject otherObj : otherObjs) {
            if (!matchingObjects.contains(otherObj)) {
              otherObj = otherObj.getClone();
              otherObj.getMetaData().put(ADDITIONAL_OBJ, additionalObj);
              newObjs.add(otherObj);
            }
          }
        }
      }

      // assemble new report
      try {
        newReport = thisReport.getClass().newInstance();
        // transfer non-object fields
        for (AbstractField field: thisReport.getFields()) {
          if (!field.getName().startsWith(m_Finder.getPrefix())) {
            newReport.addField(field);
            newReport.setValue(field, thisReport.getValue(field));
          }
        }
        // store objects
        newReport.mergeWith(newObjs.toReport(m_Finder.getPrefix()));
        // update report
        if (m_InputToken.getPayload() instanceof AbstractImageContainer) {
          output = m_InputToken.getPayload();
          ((AbstractImageContainer) output).setReport(newReport);
        }
        else if (m_InputToken.getPayload() instanceof MutableReportHandler) {
          output = m_InputToken.getPayload();
          ((MutableReportHandler) output).setReport(newReport);
        }
        else {
          output = newReport;
        }
      }
      catch (Exception e) {
        result = handleException("Failed to create new report with updated objects!", e);
        output = null;
      }
    }

    if (output != null)
      m_OutputToken = new Token(output);

    return result;
  }
}
