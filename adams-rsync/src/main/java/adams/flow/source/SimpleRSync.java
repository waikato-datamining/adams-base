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

/**
 * SimpleRSync.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;
import com.github.fracpete.rsync4j.Binaries;

/**
 <!-- globalinfo-start -->
 * An rsync wrapper with a reduced set of options.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: SimpleRSync
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
 * <pre>-source &lt;java.lang.String&gt; (property: source)
 * &nbsp;&nbsp;&nbsp;The local or remote source path (path or [user&#64;]host:path)
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-destination &lt;java.lang.String&gt; (property: destination)
 * &nbsp;&nbsp;&nbsp;The local or remote destination path (path or [user&#64;]host:path)
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-verbose &lt;boolean&gt; (property: verbose)
 * &nbsp;&nbsp;&nbsp;increase verbosity
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-quiet &lt;boolean&gt; (property: quiet)
 * &nbsp;&nbsp;&nbsp;suppress non-error messages
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-checksum &lt;boolean&gt; (property: checksum)
 * &nbsp;&nbsp;&nbsp;skip based on checksum, not mod-time &amp; size
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-archive &lt;boolean&gt; (property: archive)
 * &nbsp;&nbsp;&nbsp;archive mode; equals -rlptgoD (no -H,-A,-X)
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-recursive &lt;boolean&gt; (property: recursive)
 * &nbsp;&nbsp;&nbsp;recurse into directories
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-relative &lt;boolean&gt; (property: relative)
 * &nbsp;&nbsp;&nbsp;use relative path names
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-perms &lt;boolean&gt; (property: perms)
 * &nbsp;&nbsp;&nbsp;preserve permissions
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-executability &lt;boolean&gt; (property: executability)
 * &nbsp;&nbsp;&nbsp;preserve the file's executability
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-times &lt;boolean&gt; (property: times)
 * &nbsp;&nbsp;&nbsp;preserve modification times
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-dry_run &lt;boolean&gt; (property: dryRun)
 * &nbsp;&nbsp;&nbsp;perform a trial run with no changes made
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-rsh &lt;java.lang.String&gt; (property: rsh)
 * &nbsp;&nbsp;&nbsp;specify the remote shell to use
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-rsync_path &lt;java.lang.String&gt; (property: rsyncPath)
 * &nbsp;&nbsp;&nbsp;specify the rsync to run on the remote machine
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-delete &lt;boolean&gt; (property: delete)
 * &nbsp;&nbsp;&nbsp;delete extraneous files from destination dirs
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-exclude &lt;adams.core.base.BaseString&gt; [-exclude ...] (property: exclude)
 * &nbsp;&nbsp;&nbsp;exclude files matching PATTERN
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-exclude_from &lt;adams.core.io.PlaceholderFile&gt; (property: excludeFrom)
 * &nbsp;&nbsp;&nbsp;read exclude patterns from FILE
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-include &lt;adams.core.base.BaseString&gt; [-include ...] (property: include)
 * &nbsp;&nbsp;&nbsp;include files matching PATTERN
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-include_from &lt;adams.core.io.PlaceholderFile&gt; (property: includeFrom)
 * &nbsp;&nbsp;&nbsp;read include patterns from FILE
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-files_from &lt;adams.core.io.PlaceholderFile&gt; (property: filesFrom)
 * &nbsp;&nbsp;&nbsp;read list of source-file names from FILE
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-filter &lt;adams.core.base.BaseString&gt; [-filter ...] (property: filter)
 * &nbsp;&nbsp;&nbsp;add a file-filtering RULE
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleRSync
  extends AbstractSimpleSource {

  private static final long serialVersionUID = -2922477251211046117L;

  /** the source path/url. */
  protected String m_Source;

  /** the destination path/url. */
  protected String m_Destination;

  protected boolean m_Verbose;

  protected boolean m_Quiet;

  protected boolean m_Checksum;

  protected boolean m_Archive;

  protected boolean m_Recursive;

  protected boolean m_Relative;

  protected boolean m_Perms;

  protected boolean m_Executability;

  protected boolean m_Times;

  protected boolean m_DryRun;

  protected String m_Rsh;

  protected String m_RsyncPath;

  protected boolean m_Delete;

  protected BaseString[] m_Exclude;

  protected PlaceholderFile m_ExcludeFrom;

  protected BaseString[] m_Include;

  protected PlaceholderFile m_IncludeFrom;

  protected PlaceholderFile m_FilesFrom;

  protected BaseString[] m_Filter;

  protected int m_MaxTime;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "An rsync wrapper with a reduced set of options.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "source", "source",
      "");

    m_OptionManager.add(
      "destination", "destination",
      "");

    m_OptionManager.add(
      "verbose", "verbose",
      false);

    m_OptionManager.add(
      "quiet", "quiet",
      false);

    m_OptionManager.add(
      "checksum", "checksum",
      false);

    m_OptionManager.add(
      "archive", "archive",
      false);

    m_OptionManager.add(
      "recursive", "recursive",
      false);

    m_OptionManager.add(
      "relative", "relative",
      false);

    m_OptionManager.add(
      "perms", "perms",
      false);

    m_OptionManager.add(
      "executability", "executability",
      false);

    m_OptionManager.add(
      "times", "times",
      false);

    m_OptionManager.add(
      "dry_run", "dryRun",
      false);

    m_OptionManager.add(
      "rsh", "rsh",
      "");

    m_OptionManager.add(
      "rsync_path", "rsyncPath",
      "");

    m_OptionManager.add(
      "delete", "delete",
      false);

    m_OptionManager.add(
      "exclude", "exclude",
      new BaseString[0]);

    m_OptionManager.add(
      "exclude_from", "excludeFrom",
      new PlaceholderFile());

    m_OptionManager.add(
      "include", "include",
      new BaseString[0]);

    m_OptionManager.add(
      "include_from", "includeFrom",
      new PlaceholderFile());

    m_OptionManager.add(
      "files_from", "filesFrom",
      new PlaceholderFile());

    m_OptionManager.add(
      "filter", "filter",
      new BaseString[0]);

    m_OptionManager.add(
      "max_time", "maxTime",
      -1);
  }

  /**
   * Sets the source path/url.
   *
   * @param value	the source
   */
  public void setSource(String value) {
    m_Source = Binaries.convertPath(value);
    reset();
  }

  /**
   * Returns the current source path/url.
   *
   * @return		the source, null if not set
   */
  public String getSource() {
    return m_Source;
  }

  public String sourceTipText() {
    return "The local or remote source path (path or [user@]host:path)";
  }

  /**
   * Sets the destination path/url.
   *
   * @param value	the destination
   */
  public void setDestination(String value) {
    m_Destination = Binaries.convertPath(value);
    reset();
  }

  /**
   * Returns the current destination path/url.
   *
   * @return		the destination, null if not set
   */
  public String getDestination() {
    return m_Destination;
  }

  public String destinationTipText() {
    return "The local or remote destination path (path or [user@]host:path)";
  }

  public boolean isVerbose() {
    return m_Verbose;
  }

  public void setVerbose(boolean value) {
    m_Verbose = value;
    reset();
  }

  public String verboseTipText() {
    return "increase verbosity";
  }

  public boolean isQuiet() {
    return m_Quiet;
  }

  public void setQuiet(boolean value) {
    m_Quiet = value;
    reset();
  }

  public String quietTipText() {
    return "suppress non-error messages";
  }

  public boolean isChecksum() {
    return m_Checksum;
  }

  public void setChecksum(boolean value) {
    m_Checksum = value;
    reset();
  }

  public String checksumTipText() {
    return "skip based on checksum, not mod-time & size";
  }

  public boolean isArchive() {
    return m_Archive;
  }

  public void setArchive(boolean value) {
    m_Archive = value;
    reset();
  }

  public String archiveTipText() {
    return "archive mode; equals -rlptgoD (no -H,-A,-X)";
  }

  public boolean isRecursive() {
    return m_Recursive;
  }

  public void setRecursive(boolean value) {
    m_Recursive = value;
    reset();
  }

  public String recursiveTipText() {
    return "recurse into directories";
  }

  public boolean isRelative() {
    return m_Relative;
  }

  public void setRelative(boolean value) {
    m_Relative = value;
    reset();
  }

  public String relativeTipText() {
    return "use relative path names";
  }

  public boolean isPerms() {
    return m_Perms;
  }

  public void setPerms(boolean value) {
    m_Perms = value;
    reset();
  }

  public String permsTipText() {
    return "preserve permissions";
  }

  public boolean isExecutability() {
    return m_Executability;
  }

  public void setExecutability(boolean value) {
    m_Executability = value;
    reset();
  }

  public String executabilityTipText() {
    return "preserve the file's executability";
  }

  public boolean isTimes() {
    return m_Times;
  }

  public void setTimes(boolean value) {
    m_Times = value;
    reset();
  }

  public String timesTipText() {
    return "preserve modification times";
  }

  public boolean isDryRun() {
    return m_DryRun;
  }

  public void setDryRun(boolean value) {
    m_DryRun = value;
    reset();
  }

  public String dryRunTipText() {
    return "perform a trial run with no changes made";
  }

  public String getRsh() {
    return m_Rsh;
  }

  public void setRsh(String value) {
    m_Rsh = value;
    reset();
  }

  public String rshTipText() {
    return "specify the remote shell to use";
  }

  public String getRsyncPath() {
    return m_RsyncPath;
  }

  public void setRsyncPath(String value) {
    m_RsyncPath = value;
    reset();
  }

  public String rsyncPathTipText() {
    return "specify the rsync to run on the remote machine";
  }

  public boolean isDelete() {
    return m_Delete;
  }

  public void setDelete(boolean value) {
    m_Delete = value;
    reset();
  }

  public String deleteTipText() {
    return "delete extraneous files from destination dirs";
  }

  public BaseString[] getExclude() {
    return m_Exclude;
  }

  public void setExclude(BaseString[] value) {
    m_Exclude = value;
    reset();
  }

  public String excludeTipText() {
    return "exclude files matching PATTERN";
  }

  public PlaceholderFile getExcludeFrom() {
    return m_ExcludeFrom;
  }

  public void setExcludeFrom(PlaceholderFile value) {
    m_ExcludeFrom = value;
    reset();
  }

  public String excludeFromTipText() {
    return "read exclude patterns from FILE";
  }

  public BaseString[] getInclude() {
    return m_Include;
  }

  public void setInclude(BaseString[] value) {
    m_Include = value;
    reset();
  }

  public String includeTipText() {
    return "include files matching PATTERN";
  }

  public PlaceholderFile getIncludeFrom() {
    return m_IncludeFrom;
  }

  public void setIncludeFrom(PlaceholderFile value) {
    m_IncludeFrom = value;
    reset();
  }

  public String includeFromTipText() {
    return "read include patterns from FILE";
  }

  public PlaceholderFile getFilesFrom() {
    return m_FilesFrom;
  }

  public void setFilesFrom(PlaceholderFile value) {
    m_FilesFrom = value;
    reset();
  }

  public String filesFromTipText() {
    return "read list of source-file names from FILE";
  }

  public BaseString[] getFilter() {
    return m_Filter;
  }

  public void setFilter(BaseString[] value) {
    m_Filter = value;
    reset();
  }

  public String filterTipText() {
    return "add a file-filtering RULE";
  }

  public int getMaxTime() {
    return m_MaxTime;
  }

  public void setMaxTime(int value) {
    m_MaxTime = value;
    reset();
  }

  public String maxTimeTipText() {
    return "time out in seconds, stopping rsync process once exceeded, ignored if less than 1";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;

    result  = QuickInfoHelper.toString(this, "source", m_Source.isEmpty() ? "-none-" : m_Source, "src: ");
    result += QuickInfoHelper.toString(this, "destination", m_Destination.isEmpty() ? "-none-" : m_Destination, ", dst: ");
    result += QuickInfoHelper.toString(this, "recursive", m_Recursive, "recursive", ", ");
    result += QuickInfoHelper.toString(this, "dryRun", m_DryRun, "dry-run", ", ");

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    com.github.fracpete.rsync4j.RSync	rsync;
    CollectingProcessOutput output;

    result = null;

    try {
      rsync = new com.github.fracpete.rsync4j.RSync();
      rsync.verbose(m_Verbose);
      rsync.quiet(m_Quiet);
      rsync.checksum(m_Checksum);
      rsync.archive(m_Archive);
      rsync.recursive(m_Recursive);
      rsync.relative(m_Relative);
      rsync.perms(m_Perms);
      rsync.executability(m_Executability);
      rsync.times(m_Times);
      rsync.dryRun(m_DryRun);
      rsync.rsh(m_Rsh);
      rsync.rsyncPath(m_RsyncPath);
      rsync.delete(m_Delete);
      rsync.exclude(BaseObject.toStringArray(m_Exclude));
      if (!m_ExcludeFrom.isDirectory())
        rsync.excludeFrom(m_ExcludeFrom.getAbsolutePath());
      rsync.include(BaseObject.toStringArray(m_Include));
      if (!m_IncludeFrom.isDirectory())
        rsync.includeFrom(m_IncludeFrom.getAbsolutePath());
      if (!m_FilesFrom.isDirectory())
        rsync.filesFrom(m_FilesFrom.getAbsolutePath());
      rsync.include(BaseObject.toStringArray(m_Filter));
      rsync.maxTime(m_MaxTime);

      rsync.source(m_Source);
      rsync.destination(m_Destination);

      if (isLoggingEnabled())
	getLogger().info("Rsync:\n" + Utils.flatten(rsync.commandLineArgs(), " "));

      output = rsync.execute();
      if (output.getExitCode() > 0)
	m_OutputToken = new Token(output.getStdErr());
      else
	m_OutputToken = new Token(output.getStdOut());
    }
    catch (Exception e) {
      result = handleException("Failed to execute rsync!", e);
    }

    return result;
  }
}
