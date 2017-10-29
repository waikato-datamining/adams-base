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
 * RSync.java
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
 * Supports synchronization using rsync.<br>
 * In case of an error, the stderr output is forwarded, otherwise stdout output.
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
 * &nbsp;&nbsp;&nbsp;default: RSync
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
 * <pre>-outputCommandline &lt;boolean&gt; (property: outputCommandline)
 * &nbsp;&nbsp;&nbsp;output the command-line generated for the rsync binary
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-verbose &lt;boolean&gt; (property: verbose)
 * &nbsp;&nbsp;&nbsp;increase verbosity
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-info &lt;java.lang.String&gt; (property: info)
 * &nbsp;&nbsp;&nbsp;fine-grained informational verbosity
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-debug &lt;java.lang.String&gt; (property: debug)
 * &nbsp;&nbsp;&nbsp;fine-grained debug verbosity
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-msgs2stderr &lt;boolean&gt; (property: msgs2stderr)
 * &nbsp;&nbsp;&nbsp;special output handling for debugging
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-quiet &lt;boolean&gt; (property: quiet)
 * &nbsp;&nbsp;&nbsp;suppress non-error messages
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-no_motd &lt;boolean&gt; (property: noMotd)
 * &nbsp;&nbsp;&nbsp;suppress daemon-mode MOTD
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
 * <pre>-no_implied_dirs &lt;boolean&gt; (property: noImpliedDirs)
 * &nbsp;&nbsp;&nbsp;use relative path names
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-backup &lt;boolean&gt; (property: backup)
 * &nbsp;&nbsp;&nbsp;make backups (see --suffix &amp; --backup-dir)
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-backup_dir &lt;java.lang.String&gt; (property: backupDir)
 * &nbsp;&nbsp;&nbsp;make backups into hierarchy based in DIR
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-suffix &lt;java.lang.String&gt; (property: suffix)
 * &nbsp;&nbsp;&nbsp;set backup suffix (default ~ w&#47;o --backup-dir)
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-update &lt;boolean&gt; (property: update)
 * &nbsp;&nbsp;&nbsp;skip files that are newer on the receiver
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-inplace &lt;boolean&gt; (property: inplace)
 * &nbsp;&nbsp;&nbsp;update destination files in-place
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-append &lt;boolean&gt; (property: append)
 * &nbsp;&nbsp;&nbsp;append data onto shorter files
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-append_verify &lt;boolean&gt; (property: appendVerify)
 * &nbsp;&nbsp;&nbsp;like --append, but with old data in file checksum
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-dirs &lt;boolean&gt; (property: dirs)
 * &nbsp;&nbsp;&nbsp;transfer directories without recursing
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-links &lt;boolean&gt; (property: links)
 * &nbsp;&nbsp;&nbsp;copy symlinks as symlinks
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-copy_links &lt;boolean&gt; (property: copyLinks)
 * &nbsp;&nbsp;&nbsp;transform symlink into referent file&#47;dir
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-copy_unsafe_links &lt;boolean&gt; (property: copyUnsafeLinks)
 * &nbsp;&nbsp;&nbsp;only "unsafe" symlinks are transformed
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-safe_links &lt;boolean&gt; (property: safeLinks)
 * &nbsp;&nbsp;&nbsp;ignore symlinks that point outside the source tree
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-munge_links &lt;boolean&gt; (property: mungeLinks)
 * &nbsp;&nbsp;&nbsp;munge symlinks to make them safer (but unusable)
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-copy_dirlinks &lt;boolean&gt; (property: copyDirlinks)
 * &nbsp;&nbsp;&nbsp;transform symlink to a dir into referent dir
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-keep_dirlinks &lt;boolean&gt; (property: keepDirlinks)
 * &nbsp;&nbsp;&nbsp;treat symlinked dir on receiver as dir
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-hard_links &lt;boolean&gt; (property: hardLinks)
 * &nbsp;&nbsp;&nbsp;preserve hard links
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
 * <pre>-chmod &lt;java.lang.String&gt; (property: chmod)
 * &nbsp;&nbsp;&nbsp;affect file and&#47;or directory permissions
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-xattrs &lt;boolean&gt; (property: xattrs)
 * &nbsp;&nbsp;&nbsp;preserve extended attributes
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-owner &lt;boolean&gt; (property: owner)
 * &nbsp;&nbsp;&nbsp;preserve owner (super-user only)
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-group &lt;boolean&gt; (property: group)
 * &nbsp;&nbsp;&nbsp;preserve group
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-devices &lt;boolean&gt; (property: devices)
 * &nbsp;&nbsp;&nbsp;preserve device files (super-user only)
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-specials &lt;boolean&gt; (property: specials)
 * &nbsp;&nbsp;&nbsp;preserve special files
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-times &lt;boolean&gt; (property: times)
 * &nbsp;&nbsp;&nbsp;preserve modification times
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-omit_dir_times &lt;boolean&gt; (property: omitDirTimes)
 * &nbsp;&nbsp;&nbsp;omit directories from --times
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-omit_link_times &lt;boolean&gt; (property: omitLinkTimes)
 * &nbsp;&nbsp;&nbsp;omit symlinks from --times
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-super_ &lt;boolean&gt; (property: super_)
 * &nbsp;&nbsp;&nbsp;receiver attempts super-user activities
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-fake_super &lt;boolean&gt; (property: fakeSuper)
 * &nbsp;&nbsp;&nbsp;store&#47;recover privileged attrs using xattrs
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-sparse &lt;boolean&gt; (property: sparse)
 * &nbsp;&nbsp;&nbsp;handle sparse files efficiently
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-preallocate &lt;boolean&gt; (property: preallocate)
 * &nbsp;&nbsp;&nbsp;allocate dest files before writing them
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-dry_run &lt;boolean&gt; (property: dryRun)
 * &nbsp;&nbsp;&nbsp;perform a trial run with no changes made
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-whole_file &lt;boolean&gt; (property: wholeFile)
 * &nbsp;&nbsp;&nbsp;copy files whole (without delta-xfer algorithm)
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-one_file_system &lt;boolean&gt; (property: oneFileSystem)
 * &nbsp;&nbsp;&nbsp;don't cross filesystem boundaries
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-block_size &lt;java.lang.String&gt; (property: blockSize)
 * &nbsp;&nbsp;&nbsp;force a fixed checksum block-size
 * &nbsp;&nbsp;&nbsp;default: 
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
 * <pre>-existing &lt;boolean&gt; (property: existing)
 * &nbsp;&nbsp;&nbsp;skip creating new files on receiver
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-ignore_existing &lt;boolean&gt; (property: ignoreExisting)
 * &nbsp;&nbsp;&nbsp;skip updating files that already exist on receiver
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-remove_source_files &lt;boolean&gt; (property: removeSourceFiles)
 * &nbsp;&nbsp;&nbsp;sender removes synchronized files (non-dirs)
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-delete &lt;boolean&gt; (property: delete)
 * &nbsp;&nbsp;&nbsp;delete extraneous files from destination dirs
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-delete_before &lt;boolean&gt; (property: deleteBefore)
 * &nbsp;&nbsp;&nbsp;receiver deletes before transfer, not during
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-delete_during &lt;boolean&gt; (property: deleteDuring)
 * &nbsp;&nbsp;&nbsp;receiver deletes during the transfer
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-delete_delay &lt;boolean&gt; (property: deleteDelay)
 * &nbsp;&nbsp;&nbsp;find deletions during, delete after
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-delete_after &lt;boolean&gt; (property: deleteAfter)
 * &nbsp;&nbsp;&nbsp;receiver deletes after transfer, not during
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-delete_excluded &lt;boolean&gt; (property: deleteExcluded)
 * &nbsp;&nbsp;&nbsp;also delete excluded files from destination dirs
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-ignore_missing_args &lt;boolean&gt; (property: ignoreMissingArgs)
 * &nbsp;&nbsp;&nbsp;ignore missing source args without error
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-delete_missing_args &lt;boolean&gt; (property: deleteMissingArgs)
 * &nbsp;&nbsp;&nbsp;delete missing source args from destination
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-ignore_errors &lt;boolean&gt; (property: ignoreErrors)
 * &nbsp;&nbsp;&nbsp;delete even if there are I&#47;O errors
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-force &lt;boolean&gt; (property: force)
 * &nbsp;&nbsp;&nbsp;force deletion of directories even if not empty
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-max_delete &lt;int&gt; (property: maxDelete)
 * &nbsp;&nbsp;&nbsp;don't delete more than NUM files
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 * <pre>-max_size &lt;java.lang.String&gt; (property: maxSize)
 * &nbsp;&nbsp;&nbsp;don't transfer any file larger than SIZE
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-min_size &lt;java.lang.String&gt; (property: minSize)
 * &nbsp;&nbsp;&nbsp;don't transfer any file smaller than SIZE
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-partial &lt;boolean&gt; (property: partial)
 * &nbsp;&nbsp;&nbsp;keep partially transferred files
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-partial_dir &lt;java.lang.String&gt; (property: partialDir)
 * &nbsp;&nbsp;&nbsp;put a partially transferred file into DIR
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-delay_updates &lt;boolean&gt; (property: delayUpdates)
 * &nbsp;&nbsp;&nbsp;put all updated files into place at transfer's end
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-prune_empty_dirs &lt;boolean&gt; (property: pruneEmptyDirs)
 * &nbsp;&nbsp;&nbsp;prune empty directory chains from the file-list
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-numeric_ids &lt;boolean&gt; (property: numericIds)
 * &nbsp;&nbsp;&nbsp;don't map uid&#47;gid values by user&#47;group name
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-usermap &lt;java.lang.String&gt; (property: usermap)
 * &nbsp;&nbsp;&nbsp;custom username mapping
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-groupmap &lt;java.lang.String&gt; (property: groupmap)
 * &nbsp;&nbsp;&nbsp;custom groupname mapping
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-chown &lt;java.lang.String&gt; (property: chown)
 * &nbsp;&nbsp;&nbsp;simple username&#47;groupname mapping
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-timeout &lt;int&gt; (property: timeout)
 * &nbsp;&nbsp;&nbsp;set I&#47;O timeout in seconds
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 * <pre>-contimeout &lt;int&gt; (property: contimeout)
 * &nbsp;&nbsp;&nbsp;set daemon connection timeout in seconds
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 * <pre>-ignore_times &lt;boolean&gt; (property: ignoreTimes)
 * &nbsp;&nbsp;&nbsp;don't skip files that match in size and mod-time
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-remote_option &lt;java.lang.String&gt; (property: remoteOption)
 * &nbsp;&nbsp;&nbsp;send OPTION to the remote side only
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-size_only &lt;boolean&gt; (property: sizeOnly)
 * &nbsp;&nbsp;&nbsp;skip files that match in size
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-modify_window &lt;int&gt; (property: modifyWindow)
 * &nbsp;&nbsp;&nbsp;compare mod-times with reduced accuracy
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 * <pre>-temp_dir &lt;java.lang.String&gt; (property: tempDir)
 * &nbsp;&nbsp;&nbsp;create temporary files in directory DIR
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-fuzzy &lt;boolean&gt; (property: fuzzy)
 * &nbsp;&nbsp;&nbsp;find similar file for basis if no dest file
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-compare_dest &lt;adams.core.base.BaseString&gt; [-compare_dest ...] (property: compareDest)
 * &nbsp;&nbsp;&nbsp;also compare destination files relative to DIR
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-copy_dest &lt;adams.core.base.BaseString&gt; [-copy_dest ...] (property: copyDest)
 * &nbsp;&nbsp;&nbsp;... and include copies of unchanged files
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-link_dest &lt;adams.core.base.BaseString&gt; [-link_dest ...] (property: linkDest)
 * &nbsp;&nbsp;&nbsp;hardlink to files in DIR when unchanged
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-compress &lt;boolean&gt; (property: compress)
 * &nbsp;&nbsp;&nbsp;compress file data during the transfer
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-compress_level &lt;int&gt; (property: compressLevel)
 * &nbsp;&nbsp;&nbsp;explicitly set compression level
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 * <pre>-skip_compress &lt;java.lang.String&gt; (property: skipCompress)
 * &nbsp;&nbsp;&nbsp;skip compressing files with a suffix in LIST
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-cvs_exclude &lt;boolean&gt; (property: cvsExclude)
 * &nbsp;&nbsp;&nbsp;auto-ignore files the same way CVS does
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-filter &lt;adams.core.base.BaseString&gt; [-filter ...] (property: filter)
 * &nbsp;&nbsp;&nbsp;add a file-filtering RULE
 * &nbsp;&nbsp;&nbsp;default: 
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
 * <pre>-from0 &lt;boolean&gt; (property: from0)
 * &nbsp;&nbsp;&nbsp;all *-from&#47;filter files are delimited by 0s
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-protect_args &lt;boolean&gt; (property: protectArgs)
 * &nbsp;&nbsp;&nbsp;no space-splitting; only wildcard special-chars
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-address &lt;java.lang.String&gt; (property: address)
 * &nbsp;&nbsp;&nbsp;bind address for outgoing socket to daemon
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-port &lt;int&gt; (property: port)
 * &nbsp;&nbsp;&nbsp;specify double-colon alternate port number
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 * <pre>-sockopts &lt;java.lang.String&gt; (property: sockopts)
 * &nbsp;&nbsp;&nbsp;specify custom TCP options
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-blocking_io &lt;boolean&gt; (property: blockingIO)
 * &nbsp;&nbsp;&nbsp;use blocking I&#47;O for the remote shell
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stats &lt;boolean&gt; (property: stats)
 * &nbsp;&nbsp;&nbsp;give some file-transfer stats
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-eight_bit_output &lt;boolean&gt; (property: eightBitOutput)
 * &nbsp;&nbsp;&nbsp;leave high-bit chars unescaped in output
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-human_readable &lt;boolean&gt; (property: humanReadable)
 * &nbsp;&nbsp;&nbsp;output numbers in a human-readable format
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-progress &lt;boolean&gt; (property: progress)
 * &nbsp;&nbsp;&nbsp;show progress during transfer
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-itemize_changes &lt;boolean&gt; (property: itemizeChanges)
 * &nbsp;&nbsp;&nbsp;output a change-summary for all updates
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-out_format &lt;java.lang.String&gt; (property: outFormat)
 * &nbsp;&nbsp;&nbsp;output updates using the specified FORMAT
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-log_file &lt;adams.core.io.PlaceholderFile&gt; (property: logFile)
 * &nbsp;&nbsp;&nbsp;log what we're doing to the specified FILE
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-log_file_format &lt;java.lang.String&gt; (property: logFileFormat)
 * &nbsp;&nbsp;&nbsp;log updates using the specified FMT
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-password_file &lt;java.lang.String&gt; (property: passwordFile)
 * &nbsp;&nbsp;&nbsp;read daemon-access password from FILE
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-list_only &lt;boolean&gt; (property: listOnly)
 * &nbsp;&nbsp;&nbsp;list the files instead of copying them
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-bwlimit &lt;java.lang.String&gt; (property: bwlimit)
 * &nbsp;&nbsp;&nbsp;limit socket I&#47;O bandwidth
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-outbuf &lt;java.lang.String&gt; (property: outbuf)
 * &nbsp;&nbsp;&nbsp;set output buffering to None, Line, or Block (N|L|B)
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-write_batch &lt;adams.core.io.PlaceholderFile&gt; (property: writeBatch)
 * &nbsp;&nbsp;&nbsp;write a batched update to FILE
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-only_write_batch &lt;adams.core.io.PlaceholderFile&gt; (property: onlyWriteBatch)
 * &nbsp;&nbsp;&nbsp;like --write-batch but w&#47;o updating destination
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-read_batch &lt;adams.core.io.PlaceholderFile&gt; (property: readBatch)
 * &nbsp;&nbsp;&nbsp;read a batched update from FILE
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-protocol &lt;int&gt; (property: protocol)
 * &nbsp;&nbsp;&nbsp;force an older protocol version to be used
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 * <pre>-iconv &lt;java.lang.String&gt; (property: iconv)
 * &nbsp;&nbsp;&nbsp;request charset conversion of filenames
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-checksum_seed &lt;int&gt; (property: checksumSeed)
 * &nbsp;&nbsp;&nbsp;set block&#47;file checksum seed (advanced)
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 * <pre>-ipv4 &lt;boolean&gt; (property: ipv4)
 * &nbsp;&nbsp;&nbsp;prefer IPv4
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-ipv6 &lt;boolean&gt; (property: ipv6)
 * &nbsp;&nbsp;&nbsp;prefer IPv6
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-version &lt;boolean&gt; (property: version)
 * &nbsp;&nbsp;&nbsp;print version number
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RSync
  extends AbstractSimpleSource {

  private static final long serialVersionUID = 5033321049882638158L;

  /** the source path/url. */
  protected String m_Source;

  /** the destination path/url. */
  protected String m_Destination;

  /** whether to output the commandline. */
  protected boolean m_OutputCommandline;

  protected boolean m_Verbose;

  protected String m_Info;

  protected String m_Debug;

  protected boolean m_Msgs2stderr;

  protected boolean m_Quiet;

  protected boolean m_NoMotd;

  protected boolean m_Checksum;

  protected boolean m_Archive;

  protected boolean m_Recursive;

  protected boolean m_Relative;

  protected boolean m_NoImpliedDirs;

  protected boolean m_Backup;

  protected String m_BackupDir;

  protected String m_Suffix;

  protected boolean m_Update;

  protected boolean m_Inplace;

  protected boolean m_Append;

  protected boolean m_AppendVerify;

  protected boolean m_Dirs;

  protected boolean m_Links;

  protected boolean m_CopyLinks;

  protected boolean m_CopyUnsafeLinks;

  protected boolean m_SafeLinks;

  protected boolean m_MungeLinks;

  protected boolean m_CopyDirlinks;

  protected boolean m_KeepDirlinks;

  protected boolean m_HardLinks;

  protected boolean m_Perms;

  protected boolean m_Executability;

  protected String m_Chmod;

  protected boolean m_Xattrs;

  protected boolean m_Owner;

  protected boolean m_Group;

  protected boolean m_Devices;

  protected boolean m_Specials;

  protected boolean m_Times;

  protected boolean m_OmitDirTimes;

  protected boolean m_OmitLinkTimes;

  protected boolean m_Super_;

  protected boolean m_FakeSuper;

  protected boolean m_Sparse;

  protected boolean m_Preallocate;

  protected boolean m_DryRun;

  protected boolean m_WholeFile;

  protected boolean m_OneFileSystem;

  protected String m_BlockSize;

  protected String m_Rsh;

  protected String m_RsyncPath;

  protected boolean m_Existing;

  protected boolean m_IgnoreExisting;

  protected boolean m_RemoveSourceFiles;

  protected boolean m_Delete;

  protected boolean m_DeleteBefore;

  protected boolean m_DeleteDuring;

  protected boolean m_DeleteDelay;

  protected boolean m_DeleteAfter;

  protected boolean m_DeleteExcluded;

  protected boolean m_IgnoreMissingArgs;

  protected boolean m_DeleteMissingArgs;

  protected boolean m_IgnoreErrors;

  protected boolean m_Force;

  protected int m_MaxDelete;

  protected String m_MaxSize;

  protected String m_MinSize;

  protected boolean m_Partial;

  protected String m_PartialDir;

  protected boolean m_DelayUpdates;

  protected boolean m_PruneEmptyDirs;

  protected boolean m_NumericIds;

  protected String m_Usermap;

  protected String m_Groupmap;

  protected String m_Chown;

  protected int m_Timeout;

  protected int m_Contimeout;

  protected boolean m_IgnoreTimes;

  protected String m_RemoteOption;

  protected boolean m_SizeOnly;

  protected int m_ModifyWindow;

  protected String m_TempDir;

  protected boolean m_Fuzzy;

  protected BaseString[] m_CompareDest;

  protected BaseString[] m_CopyDest;

  protected BaseString[] m_LinkDest;

  protected boolean m_Compress;

  protected int m_CompressLevel;

  protected String m_SkipCompress;

  protected boolean m_CvsExclude;

  protected BaseString[] m_Filter;

  protected BaseString[] m_Exclude;

  protected PlaceholderFile m_ExcludeFrom;

  protected BaseString[] m_Include;

  protected PlaceholderFile m_IncludeFrom;

  protected PlaceholderFile m_FilesFrom;

  protected boolean m_From0;

  protected boolean m_ProtectArgs;

  protected String m_Address;

  protected int m_Port;

  protected String m_Sockopts;

  protected boolean m_BlockingIO;

  protected boolean m_Stats;

  protected boolean m_EightBitOutput;

  protected boolean m_HumanReadable;

  protected boolean m_Progress;

  protected boolean m_ItemizeChanges;

  protected String m_OutFormat;

  protected PlaceholderFile m_LogFile;

  protected String m_LogFileFormat;

  protected String m_PasswordFile;

  protected boolean m_ListOnly;

  protected String m_Bwlimit;

  protected String m_Outbuf;

  protected PlaceholderFile m_WriteBatch;

  protected PlaceholderFile m_OnlyWriteBatch;

  protected PlaceholderFile m_ReadBatch;

  protected int m_Protocol;

  protected String m_Iconv;

  protected int m_ChecksumSeed;

  protected boolean m_Ipv4;

  protected boolean m_Ipv6;

  protected boolean m_Version;

  protected int m_MaxTime;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Supports synchronization using rsync.\n"
      + "In case of an error, the stderr output is forwarded, otherwise stdout output.";
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
      "outputCommandline", "outputCommandline",
      false);

    m_OptionManager.add(
      "verbose", "verbose",
      false);

    m_OptionManager.add(
      "info", "info",
      "");

    m_OptionManager.add(
      "debug", "debug",
      "");

    m_OptionManager.add(
      "msgs2stderr", "msgs2stderr",
      false);

    m_OptionManager.add(
      "quiet", "quiet",
      false);

    m_OptionManager.add(
      "no_motd", "noMotd",
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
      "no_implied_dirs", "noImpliedDirs",
      false);

    m_OptionManager.add(
      "backup", "backup",
      false);

    m_OptionManager.add(
      "backup_dir", "backupDir",
      "");

    m_OptionManager.add(
      "suffix", "suffix",
      "");

    m_OptionManager.add(
      "update", "update",
      false);

    m_OptionManager.add(
      "inplace", "inplace",
      false);

    m_OptionManager.add(
      "append", "append",
      false);

    m_OptionManager.add(
      "append_verify", "appendVerify",
      false);

    m_OptionManager.add(
      "dirs", "dirs",
      false);

    m_OptionManager.add(
      "links", "links",
      false);

    m_OptionManager.add(
      "copy_links", "copyLinks",
      false);

    m_OptionManager.add(
      "copy_unsafe_links", "copyUnsafeLinks",
      false);

    m_OptionManager.add(
      "safe_links", "safeLinks",
      false);

    m_OptionManager.add(
      "munge_links", "mungeLinks",
      false);

    m_OptionManager.add(
      "copy_dirlinks", "copyDirlinks",
      false);

    m_OptionManager.add(
      "keep_dirlinks", "keepDirlinks",
      false);

    m_OptionManager.add(
      "hard_links", "hardLinks",
      false);

    m_OptionManager.add(
      "perms", "perms",
      false);

    m_OptionManager.add(
      "executability", "executability",
      false);

    m_OptionManager.add(
      "chmod", "chmod",
      "");

    m_OptionManager.add(
      "xattrs", "xattrs",
      false);

    m_OptionManager.add(
      "owner", "owner",
      false);

    m_OptionManager.add(
      "group", "group",
      false);

    m_OptionManager.add(
      "devices", "devices",
      false);

    m_OptionManager.add(
      "specials", "specials",
      false);

    m_OptionManager.add(
      "times", "times",
      false);

    m_OptionManager.add(
      "omit_dir_times", "omitDirTimes",
      false);

    m_OptionManager.add(
      "omit_link_times", "omitLinkTimes",
      false);

    m_OptionManager.add(
      "super_", "super_",
      false);

    m_OptionManager.add(
      "fake_super", "fakeSuper",
      false);

    m_OptionManager.add(
      "sparse", "sparse",
      false);

    m_OptionManager.add(
      "preallocate", "preallocate",
      false);

    m_OptionManager.add(
      "dry_run", "dryRun",
      false);

    m_OptionManager.add(
      "whole_file", "wholeFile",
      false);

    m_OptionManager.add(
      "one_file_system", "oneFileSystem",
      false);

    m_OptionManager.add(
      "block_size", "blockSize",
      "");

    m_OptionManager.add(
      "rsh", "rsh",
      "");

    m_OptionManager.add(
      "rsync_path", "rsyncPath",
      "");

    m_OptionManager.add(
      "existing", "existing",
      false);

    m_OptionManager.add(
      "ignore_existing", "ignoreExisting",
      false);

    m_OptionManager.add(
      "remove_source_files", "removeSourceFiles",
      false);

    m_OptionManager.add(
      "delete", "delete",
      false);

    m_OptionManager.add(
      "delete_before", "deleteBefore",
      false);

    m_OptionManager.add(
      "delete_during", "deleteDuring",
      false);

    m_OptionManager.add(
      "delete_delay", "deleteDelay",
      false);

    m_OptionManager.add(
      "delete_after", "deleteAfter",
      false);

    m_OptionManager.add(
      "delete_excluded", "deleteExcluded",
      false);

    m_OptionManager.add(
      "ignore_missing_args", "ignoreMissingArgs",
      false);

    m_OptionManager.add(
      "delete_missing_args", "deleteMissingArgs",
      false);

    m_OptionManager.add(
      "ignore_errors", "ignoreErrors",
      false);

    m_OptionManager.add(
      "force", "force",
      false);

    m_OptionManager.add(
      "max_delete", "maxDelete",
      -1);

    m_OptionManager.add(
      "max_size", "maxSize",
      "");

    m_OptionManager.add(
      "min_size", "minSize",
      "");

    m_OptionManager.add(
      "partial", "partial",
      false);

    m_OptionManager.add(
      "partial_dir", "partialDir",
      "");

    m_OptionManager.add(
      "delay_updates", "delayUpdates",
      false);

    m_OptionManager.add(
      "prune_empty_dirs", "pruneEmptyDirs",
      false);

    m_OptionManager.add(
      "numeric_ids", "numericIds",
      false);

    m_OptionManager.add(
      "usermap", "usermap",
      "");

    m_OptionManager.add(
      "groupmap", "groupmap",
      "");

    m_OptionManager.add(
      "chown", "chown",
      "");

    m_OptionManager.add(
      "timeout", "timeout",
      -1);

    m_OptionManager.add(
      "contimeout", "contimeout",
      -1);

    m_OptionManager.add(
      "ignore_times", "ignoreTimes",
      false);

    m_OptionManager.add(
      "remote_option", "remoteOption",
      "");

    m_OptionManager.add(
      "size_only", "sizeOnly",
      false);

    m_OptionManager.add(
      "modify_window", "modifyWindow",
      -1);

    m_OptionManager.add(
      "temp_dir", "tempDir",
      "");

    m_OptionManager.add(
      "fuzzy", "fuzzy",
      false);

    m_OptionManager.add(
      "compare_dest", "compareDest",
      new BaseString[0]);

    m_OptionManager.add(
      "copy_dest", "copyDest",
      new BaseString[0]);

    m_OptionManager.add(
      "link_dest", "linkDest",
      new BaseString[0]);

    m_OptionManager.add(
      "compress", "compress",
      false);

    m_OptionManager.add(
      "compress_level", "compressLevel",
      -1);

    m_OptionManager.add(
      "skip_compress", "skipCompress",
      "");

    m_OptionManager.add(
      "cvs_exclude", "cvsExclude",
      false);

    m_OptionManager.add(
      "filter", "filter",
      new BaseString[0]);

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
      "from0", "from0",
      false);

    m_OptionManager.add(
      "protect_args", "protectArgs",
      false);

    m_OptionManager.add(
      "address", "address",
      "");

    m_OptionManager.add(
      "port", "port",
      -1);

    m_OptionManager.add(
      "sockopts", "sockopts",
      "");

    m_OptionManager.add(
      "blocking_io", "blockingIO",
      false);

    m_OptionManager.add(
      "stats", "stats",
      false);

    m_OptionManager.add(
      "eight_bit_output", "eightBitOutput",
      false);

    m_OptionManager.add(
      "human_readable", "humanReadable",
      false);

    m_OptionManager.add(
      "progress", "progress",
      false);

    m_OptionManager.add(
      "itemize_changes", "itemizeChanges",
      false);

    m_OptionManager.add(
      "out_format", "outFormat",
      "");

    m_OptionManager.add(
      "log_file", "logFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "log_file_format", "logFileFormat",
      "");

    m_OptionManager.add(
      "password_file", "passwordFile",
      "");

    m_OptionManager.add(
      "list_only", "listOnly",
      false);

    m_OptionManager.add(
      "bwlimit", "bwlimit",
      "");

    m_OptionManager.add(
      "outbuf", "outbuf",
      "");

    m_OptionManager.add(
      "write_batch", "writeBatch",
      new PlaceholderFile());

    m_OptionManager.add(
      "only_write_batch", "onlyWriteBatch",
      new PlaceholderFile());

    m_OptionManager.add(
      "read_batch", "readBatch",
      new PlaceholderFile());

    m_OptionManager.add(
      "protocol", "protocol",
      -1);

    m_OptionManager.add(
      "iconv", "iconv",
      "");

    m_OptionManager.add(
      "checksum_seed", "checksumSeed",
      -1);

    m_OptionManager.add(
      "ipv4", "ipv4",
      false);

    m_OptionManager.add(
      "ipv6", "ipv6",
      false);

    m_OptionManager.add(
      "version", "version",
      false);

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

  /**
   * Sets output commandline flag.
   *
   * @param value	true if to output commandline
   */
  public void setOutputCommandline(boolean value) {
    m_OutputCommandline = value;
    reset();
  }

  /**
   * Returns output commandline flag.
   *
   * @return		true if to output commandline
   */
  public boolean getOutputCommandline() {
    return m_OutputCommandline;
  }

  public String outputCommandlineTipText() {
    return "output the command-line generated for the rsync binary";
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

  public String getInfo() {
    return m_Info;
  }

  public void setInfo(String value) {
    m_Info = value;
    reset();
  }

  public String infoTipText() {
    return "fine-grained informational verbosity";
  }

  public String getDebug() {
    return m_Debug;
  }

  public void setDebug(String value) {
    m_Debug = value;
    reset();
  }

  public String debugTipText() {
    return "fine-grained debug verbosity";
  }

  public boolean isMsgs2stderr() {
    return m_Msgs2stderr;
  }

  public void setMsgs2stderr(boolean value) {
    m_Msgs2stderr = value;
    reset();
  }

  public String msgs2stderrTipText() {
    return "special output handling for debugging";
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

  public boolean isNoMotd() {
    return m_NoMotd;
  }

  public void setNoMotd(boolean value) {
    m_NoMotd = value;
    reset();
  }

  public String noMotdTipText() {
    return "suppress daemon-mode MOTD";
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

  public boolean isNoImpliedDirs() {
    return m_NoImpliedDirs;
  }

  public void setNoImpliedDirs(boolean value) {
    m_NoImpliedDirs = value;
    reset();
  }

  public String noImpliedDirsTipText() {
    return "use relative path names";
  }

  public boolean isBackup() {
    return m_Backup;
  }

  public void setBackup(boolean value) {
    m_Backup = value;
    reset();
  }

  public String backupTipText() {
    return "make backups (see --suffix & --backup-dir)";
  }

  public String getBackupDir() {
    return m_BackupDir;
  }

  public void setBackupDir(String value) {
    m_BackupDir = value;
    reset();
  }

  public String backupDirTipText() {
    return "make backups into hierarchy based in DIR";
  }

  public String getSuffix() {
    return m_Suffix;
  }

  public void setSuffix(String value) {
    m_Suffix = value;
    reset();
  }

  public String suffixTipText() {
    return "set backup suffix (default ~ w/o --backup-dir)";
  }

  public boolean isUpdate() {
    return m_Update;
  }

  public void setUpdate(boolean value) {
    m_Update = value;
    reset();
  }

  public String updateTipText() {
    return "skip files that are newer on the receiver";
  }

  public boolean isInplace() {
    return m_Inplace;
  }

  public void setInplace(boolean value) {
    m_Inplace = value;
    reset();
  }

  public String inplaceTipText() {
    return "update destination files in-place";
  }

  public boolean isAppend() {
    return m_Append;
  }

  public void setAppend(boolean value) {
    m_Append = value;
    reset();
  }

  public String appendTipText() {
    return "append data onto shorter files";
  }

  public boolean isAppendVerify() {
    return m_AppendVerify;
  }

  public void setAppendVerify(boolean value) {
    m_AppendVerify = value;
    reset();
  }

  public String appendVerifyTipText() {
    return "like --append, but with old data in file checksum";
  }

  public boolean isDirs() {
    return m_Dirs;
  }

  public void setDirs(boolean value) {
    m_Dirs = value;
    reset();
  }

  public String dirsTipText() {
    return "transfer directories without recursing";
  }

  public boolean isLinks() {
    return m_Links;
  }

  public void setLinks(boolean value) {
    m_Links = value;
    reset();
  }

  public String linksTipText() {
    return "copy symlinks as symlinks";
  }

  public boolean isCopyLinks() {
    return m_CopyLinks;
  }

  public void setCopyLinks(boolean value) {
    m_CopyLinks = value;
    reset();
  }

  public String copyLinksTipText() {
    return "transform symlink into referent file/dir";
  }

  public boolean isCopyUnsafeLinks() {
    return m_CopyUnsafeLinks;
  }

  public void setCopyUnsafeLinks(boolean value) {
    m_CopyUnsafeLinks = value;
    reset();
  }

  public String copyUnsafeLinksTipText() {
    return "only \"unsafe\" symlinks are transformed";
  }

  public boolean isSafeLinks() {
    return m_SafeLinks;
  }

  public void setSafeLinks(boolean value) {
    m_SafeLinks = value;
    reset();
  }

  public String safeLinksTipText() {
    return "ignore symlinks that point outside the source tree";
  }

  public boolean isMungeLinks() {
    return m_MungeLinks;
  }

  public void setMungeLinks(boolean value) {
    m_MungeLinks = value;
    reset();
  }

  public String mungeLinksTipText() {
    return "munge symlinks to make them safer (but unusable)";
  }

  public boolean isCopyDirlinks() {
    return m_CopyDirlinks;
  }

  public void setCopyDirlinks(boolean value) {
    m_CopyDirlinks = value;
    reset();
  }

  public String copyDirlinksTipText() {
    return "transform symlink to a dir into referent dir";
  }

  public boolean isKeepDirlinks() {
    return m_KeepDirlinks;
  }

  public void setKeepDirlinks(boolean value) {
    m_KeepDirlinks = value;
    reset();
  }

  public String keepDirlinksTipText() {
    return "treat symlinked dir on receiver as dir";
  }

  public boolean isHardLinks() {
    return m_HardLinks;
  }

  public void setHardLinks(boolean value) {
    m_HardLinks = value;
    reset();
  }

  public String hardLinksTipText() {
    return "preserve hard links";
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

  public String getChmod() {
    return m_Chmod;
  }

  public void setChmod(String value) {
    m_Chmod = value;
    reset();
  }

  public String chmodTipText() {
    return "affect file and/or directory permissions";
  }

  public boolean isXattrs() {
    return m_Xattrs;
  }

  public void setXattrs(boolean value) {
    m_Xattrs = value;
    reset();
  }

  public String xattrsTipText() {
    return "preserve extended attributes";
  }

  public boolean isOwner() {
    return m_Owner;
  }

  public void setOwner(boolean value) {
    m_Owner = value;
    reset();
  }

  public String ownerTipText() {
    return "preserve owner (super-user only)";
  }

  public boolean isGroup() {
    return m_Group;
  }

  public void setGroup(boolean value) {
    m_Group = value;
    reset();
  }

  public String groupTipText() {
    return "preserve group";
  }

  public boolean isDevices() {
    return m_Devices;
  }

  public void setDevices(boolean value) {
    m_Devices = value;
    reset();
  }

  public String devicesTipText() {
    return "preserve device files (super-user only)";
  }

  public boolean isSpecials() {
    return m_Specials;
  }

  public void setSpecials(boolean value) {
    m_Specials = value;
    reset();
  }

  public String specialsTipText() {
    return "preserve special files";
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

  public boolean isOmitDirTimes() {
    return m_OmitDirTimes;
  }

  public void setOmitDirTimes(boolean value) {
    m_OmitDirTimes = value;
    reset();
  }

  public String omitDirTimesTipText() {
    return "omit directories from --times";
  }

  public boolean isOmitLinkTimes() {
    return m_OmitLinkTimes;
  }

  public void setOmitLinkTimes(boolean value) {
    m_OmitLinkTimes = value;
    reset();
  }

  public String omitLinkTimesTipText() {
    return "omit symlinks from --times";
  }

  public boolean isSuper_() {
    return m_Super_;
  }

  public void setSuper_(boolean super_) {
    m_Super_ = super_;
    reset();
  }

  public String super_TipText() {
    return "receiver attempts super-user activities";
  }

  public boolean isFakeSuper() {
    return m_FakeSuper;
  }

  public void setFakeSuper(boolean value) {
    m_FakeSuper = value;
    reset();
  }

  public String fakeSuperTipText() {
    return "store/recover privileged attrs using xattrs";
  }

  public boolean isSparse() {
    return m_Sparse;
  }

  public void setSparse(boolean value) {
    m_Sparse = value;
    reset();
  }

  public String sparseTipText() {
    return "handle sparse files efficiently";
  }

  public boolean isPreallocate() {
    return m_Preallocate;
  }

  public void setPreallocate(boolean value) {
    m_Preallocate = value;
    reset();
  }

  public String preallocateTipText() {
    return "allocate dest files before writing them";
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

  public boolean isWholeFile() {
    return m_WholeFile;
  }

  public void setWholeFile(boolean value) {
    m_WholeFile = value;
    reset();
  }

  public String wholeFileTipText() {
    return "copy files whole (without delta-xfer algorithm)";
  }

  public boolean isOneFileSystem() {
    return m_OneFileSystem;
  }

  public void setOneFileSystem(boolean value) {
    m_OneFileSystem = value;
    reset();
  }

  public String oneFileSystemTipText() {
    return "don't cross filesystem boundaries";
  }

  public String getBlockSize() {
    return m_BlockSize;
  }

  public void setBlockSize(String value) {
    m_BlockSize = value;
    reset();
  }

  public String blockSizeTipText() {
    return "force a fixed checksum block-size";
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

  public boolean isExisting() {
    return m_Existing;
  }

  public void setExisting(boolean value) {
    m_Existing = value;
    reset();
  }

  public String existingTipText() {
    return "skip creating new files on receiver";
  }

  public boolean isIgnoreExisting() {
    return m_IgnoreExisting;
  }

  public void setIgnoreExisting(boolean value) {
    m_IgnoreExisting = value;
    reset();
  }

  public String ignoreExistingTipText() {
    return "skip updating files that already exist on receiver";
  }

  public boolean isRemoveSourceFiles() {
    return m_RemoveSourceFiles;
  }

  public void setRemoveSourceFiles(boolean value) {
    m_RemoveSourceFiles = value;
    reset();
  }

  public String removeSourceFilesTipText() {
    return "sender removes synchronized files (non-dirs)";
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

  public boolean isDeleteBefore() {
    return m_DeleteBefore;
  }

  public void setDeleteBefore(boolean value) {
    m_DeleteBefore = value;
    reset();
  }

  public String deleteBeforeTipText() {
    return "receiver deletes before transfer, not during";
  }

  public boolean isDeleteDuring() {
    return m_DeleteDuring;
  }

  public void setDeleteDuring(boolean value) {
    m_DeleteDuring = value;
    reset();
  }

  public String deleteDuringTipText() {
    return "receiver deletes during the transfer";
  }

  public boolean isDeleteDelay() {
    return m_DeleteDelay;
  }

  public void setDeleteDelay(boolean value) {
    m_DeleteDelay = value;
    reset();
  }

  public String deleteDelayTipText() {
    return "find deletions during, delete after";
  }

  public boolean isDeleteAfter() {
    return m_DeleteAfter;
  }

  public void setDeleteAfter(boolean value) {
    m_DeleteAfter = value;
    reset();
  }

  public String deleteAfterTipText() {
    return "receiver deletes after transfer, not during";
  }

  public boolean isDeleteExcluded() {
    return m_DeleteExcluded;
  }

  public void setDeleteExcluded(boolean value) {
    m_DeleteExcluded = value;
    reset();
  }

  public String deleteExcludedTipText() {
    return "also delete excluded files from destination dirs";
  }

  public boolean isIgnoreMissingArgs() {
    return m_IgnoreMissingArgs;
  }

  public void setIgnoreMissingArgs(boolean value) {
    m_IgnoreMissingArgs = value;
    reset();
  }

  public String ignoreMissingArgsTipText() {
    return "ignore missing source args without error";
  }

  public boolean isDeleteMissingArgs() {
    return m_DeleteMissingArgs;
  }

  public void setDeleteMissingArgs(boolean value) {
    m_DeleteMissingArgs = value;
    reset();
  }

  public String deleteMissingArgsTipText() {
    return "delete missing source args from destination";
  }

  public boolean isIgnoreErrors() {
    return m_IgnoreErrors;
  }

  public void setIgnoreErrors(boolean value) {
    m_IgnoreErrors = value;
    reset();
  }

  public String ignoreErrorsTipText() {
    return "delete even if there are I/O errors";
  }

  public boolean isForce() {
    return m_Force;
  }

  public void setForce(boolean value) {
    m_Force = value;
    reset();
  }

  public String forceTipText() {
    return "force deletion of directories even if not empty";
  }

  public int getMaxDelete() {
    return m_MaxDelete;
  }

  public void setMaxDelete(int value) {
    m_MaxDelete = value;
    reset();
  }

  public String maxDeleteTipText() {
    return "don't delete more than NUM files";
  }

  public String getMaxSize() {
    return m_MaxSize;
  }

  public void setMaxSize(String value) {
    m_MaxSize = value;
    reset();
  }

  public String maxSizeTipText() {
    return "don't transfer any file larger than SIZE";
  }

  public String getMinSize() {
    return m_MinSize;
  }

  public void setMinSize(String value) {
    m_MinSize = value;
    reset();
  }

  public String minSizeTipText() {
    return "don't transfer any file smaller than SIZE";
  }

  public boolean isPartial() {
    return m_Partial;
  }

  public void setPartial(boolean value) {
    m_Partial = value;
    reset();
  }

  public String partialTipText() {
    return "keep partially transferred files";
  }

  public String getPartialDir() {
    return m_PartialDir;
  }

  public void setPartialDir(String value) {
    m_PartialDir = value;
    reset();
  }

  public String partialDirTipText() {
    return "put a partially transferred file into DIR";
  }

  public boolean isDelayUpdates() {
    return m_DelayUpdates;
  }

  public void setDelayUpdates(boolean value) {
    m_DelayUpdates = value;
    reset();
  }

  public String delayUpdatesTipText() {
    return "put all updated files into place at transfer's end";
  }

  public boolean isPruneEmptyDirs() {
    return m_PruneEmptyDirs;
  }

  public void setPruneEmptyDirs(boolean value) {
    m_PruneEmptyDirs = value;
    reset();
  }

  public String pruneEmptyDirsTipText() {
    return "prune empty directory chains from the file-list";
  }

  public boolean isNumericIds() {
    return m_NumericIds;
  }

  public void setNumericIds(boolean value) {
    m_NumericIds = value;
    reset();
  }

  public String numericIdsTipText() {
    return "don't map uid/gid values by user/group name";
  }

  public String getUsermap() {
    return m_Usermap;
  }

  public void setUsermap(String value) {
    m_Usermap = value;
    reset();
  }

  public String usermapTipText() {
    return "custom username mapping";
  }

  public String getGroupmap() {
    return m_Groupmap;
  }

  public void setGroupmap(String value) {
    m_Groupmap = value;
    reset();
  }

  public String groupmapTipText() {
    return "custom groupname mapping";
  }

  public String getChown() {
    return m_Chown;
  }

  public void setChown(String value) {
    m_Chown = value;
    reset();
  }

  public String chownTipText() {
    return "simple username/groupname mapping";
  }

  public int getTimeout() {
    return m_Timeout;
  }

  public void setTimeout(int value) {
    m_Timeout = value;
    reset();
  }

  public String timeoutTipText() {
    return "set I/O timeout in seconds";
  }

  public int getContimeout() {
    return m_Contimeout;
  }

  public void setContimeout(int value) {
    m_Contimeout = value;
    reset();
  }

  public String contimeoutTipText() {
    return "set daemon connection timeout in seconds";
  }

  public boolean isIgnoreTimes() {
    return m_IgnoreTimes;
  }

  public void setIgnoreTimes(boolean value) {
    m_IgnoreTimes = value;
    reset();
  }

  public String ignoreTimesTipText() {
    return "don't skip files that match in size and mod-time";
  }

  public String getRemoteOption() {
    return m_RemoteOption;
  }

  public void setRemoteOption(String value) {
    m_RemoteOption = value;
    reset();
  }

  public String remoteOptionTipText() {
    return "send OPTION to the remote side only";
  }

  public boolean isSizeOnly() {
    return m_SizeOnly;
  }

  public void setSizeOnly(boolean value) {
    m_SizeOnly = value;
    reset();
  }

  public String sizeOnlyTipText() {
    return "skip files that match in size";
  }

  public int getModifyWindow() {
    return m_ModifyWindow;
  }

  public void setModifyWindow(int value) {
    m_ModifyWindow = value;
    reset();
  }

  public String modifyWindowTipText() {
    return "compare mod-times with reduced accuracy";
  }

  public String getTempDir() {
    return m_TempDir;
  }

  public void setTempDir(String value) {
    m_TempDir = value;
    reset();
  }

  public String tempDirTipText() {
    return "create temporary files in directory DIR";
  }

  public boolean isFuzzy() {
    return m_Fuzzy;
  }

  public void setFuzzy(boolean value) {
    m_Fuzzy = value;
    reset();
  }

  public String fuzzyTipText() {
    return "find similar file for basis if no dest file";
  }

  public BaseString[] getCompareDest() {
    return m_CompareDest;
  }

  public void setCompareDest(BaseString[] value) {
    m_CompareDest = value;
    reset();
  }

  public String compareDestTipText() {
    return "also compare destination files relative to DIR";
  }

  public BaseString[] getCopyDest() {
    return m_CopyDest;
  }

  public void setCopyDest(BaseString[] value) {
    m_CopyDest = value;
    reset();
  }

  public String copyDestTipText() {
    return "... and include copies of unchanged files";
  }

  public BaseString[] getLinkDest() {
    return m_LinkDest;
  }

  public void setLinkDest(BaseString[] value) {
    m_LinkDest = value;
    reset();
  }

  public String linkDestTipText() {
    return "hardlink to files in DIR when unchanged";
  }

  public boolean isCompress() {
    return m_Compress;
  }

  public void setCompress(boolean value) {
    m_Compress = value;
    reset();
  }

  public String compressTipText() {
    return "compress file data during the transfer";
  }

  public int getCompressLevel() {
    return m_CompressLevel;
  }

  public void setCompressLevel(int value) {
    m_CompressLevel = value;
    reset();
  }

  public String compressLevelTipText() {
    return "explicitly set compression level";
  }

  public String getSkipCompress() {
    return m_SkipCompress;
  }

  public void setSkipCompress(String value) {
    m_SkipCompress = value;
    reset();
  }

  public String skipCompressTipText() {
    return "skip compressing files with a suffix in LIST";
  }

  public boolean isCvsExclude() {
    return m_CvsExclude;
  }

  public void setCvsExclude(boolean value) {
    m_CvsExclude = value;
    reset();
  }

  public String cvsExcludeTipText() {
    return "auto-ignore files the same way CVS does";
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

  public boolean isFrom0() {
    return m_From0;
  }

  public void setFrom0(boolean value) {
    m_From0 = value;
    reset();
  }

  public String from0TipText() {
    return "all *-from/filter files are delimited by 0s";
  }

  public boolean isProtectArgs() {
    return m_ProtectArgs;
  }

  public void setProtectArgs(boolean value) {
    m_ProtectArgs = value;
    reset();
  }

  public String protectArgsTipText() {
    return "no space-splitting; only wildcard special-chars";
  }

  public String getAddress() {
    return m_Address;
  }

  public void setAddress(String value) {
    m_Address = value;
    reset();
  }

  public String addressTipText() {
    return "bind address for outgoing socket to daemon";
  }

  public int getPort() {
    return m_Port;
  }

  public void setPort(int value) {
    m_Port = value;
    reset();
  }

  public String portTipText() {
    return "specify double-colon alternate port number";
  }

  public String getSockopts() {
    return m_Sockopts;
  }

  public void setSockopts(String value) {
    m_Sockopts = value;
    reset();
  }

  public String sockoptsTipText() {
    return "specify custom TCP options";
  }

  public boolean isBlockingIO() {
    return m_BlockingIO;
  }

  public void setBlockingIO(boolean value) {
    m_BlockingIO = value;
    reset();
  }

  public String blockingIOTipText() {
    return "use blocking I/O for the remote shell";
  }

  public boolean isStats() {
    return m_Stats;
  }

  public void setStats(boolean value) {
    m_Stats = value;
    reset();
  }

  public String statsTipText() {
    return "give some file-transfer stats";
  }

  public boolean isEightBitOutput() {
    return m_EightBitOutput;
  }

  public void setEightBitOutput(boolean value) {
    m_EightBitOutput = value;
    reset();
  }

  public String eightBitOutputTipText() {
    return "leave high-bit chars unescaped in output";
  }

  public boolean isHumanReadable() {
    return m_HumanReadable;
  }

  public void setHumanReadable(boolean value) {
    m_HumanReadable = value;
    reset();
  }

  public String humanReadableTipText() {
    return "output numbers in a human-readable format";
  }

  public boolean isProgress() {
    return m_Progress;
  }

  public void setProgress(boolean value) {
    m_Progress = value;
    reset();
  }

  public String progressTipText() {
    return "show progress during transfer";
  }

  public boolean isItemizeChanges() {
    return m_ItemizeChanges;
  }

  public void setItemizeChanges(boolean value) {
    m_ItemizeChanges = value;
    reset();
  }

  public String itemizeChangesTipText() {
    return "output a change-summary for all updates";
  }

  public String getOutFormat() {
    return m_OutFormat;
  }

  public void setOutFormat(String value) {
    m_OutFormat = value;
    reset();
  }

  public String outFormatTipText() {
    return "output updates using the specified FORMAT";
  }

  public PlaceholderFile getLogFile() {
    return m_LogFile;
  }

  public void setLogFile(PlaceholderFile value) {
    m_LogFile = value;
    reset();
  }

  public String logFileTipText() {
    return "log what we're doing to the specified FILE";
  }

  public String getLogFileFormat() {
    return m_LogFileFormat;
  }

  public void setLogFileFormat(String value) {
    m_LogFileFormat = value;
    reset();
  }

  public String logFileFormatTipText() {
    return "log updates using the specified FMT";
  }

  public String getPasswordFile() {
    return m_PasswordFile;
  }

  public void setPasswordFile(String value) {
    m_PasswordFile = value;
    reset();
  }

  public String passwordFileTipText() {
    return "read daemon-access password from FILE";
  }

  public boolean isListOnly() {
    return m_ListOnly;
  }

  public void setListOnly(boolean value) {
    m_ListOnly = value;
    reset();
  }

  public String listOnlyTipText() {
    return "list the files instead of copying them";
  }

  public String getBwlimit() {
    return m_Bwlimit;
  }

  public void setBwlimit(String value) {
    m_Bwlimit = value;
    reset();
  }

  public String bwlimitTipText() {
    return "limit socket I/O bandwidth";
  }

  public String getOutbuf() {
    return m_Outbuf;
  }

  public void setOutbuf(String value) {
    m_Outbuf = value;
    reset();
  }

  public String outbufTipText() {
    return "set output buffering to None, Line, or Block (N|L|B)";
  }

  public PlaceholderFile getWriteBatch() {
    return m_WriteBatch;
  }

  public void setWriteBatch(PlaceholderFile value) {
    m_WriteBatch = value;
    reset();
  }

  public String writeBatchTipText() {
    return "write a batched update to FILE";
  }

  public PlaceholderFile getOnlyWriteBatch() {
    return m_OnlyWriteBatch;
  }

  public void setOnlyWriteBatch(PlaceholderFile value) {
    m_OnlyWriteBatch = value;
    reset();
  }

  public String onlyWriteBatchTipText() {
    return "like --write-batch but w/o updating destination";
  }

  public PlaceholderFile getReadBatch() {
    return m_ReadBatch;
  }

  public void setReadBatch(PlaceholderFile value) {
    m_ReadBatch = value;
    reset();
  }

  public String readBatchTipText() {
    return "read a batched update from FILE";
  }

  public int getProtocol() {
    return m_Protocol;
  }

  public void setProtocol(int value) {
    m_Protocol = value;
    reset();
  }

  public String protocolTipText() {
    return "force an older protocol version to be used";
  }

  public String getIconv() {
    return m_Iconv;
  }

  public void setIconv(String value) {
    m_Iconv = value;
    reset();
  }

  public String iconvTipText() {
    return "request charset conversion of filenames";
  }

  public int getChecksumSeed() {
    return m_ChecksumSeed;
  }

  public void setChecksumSeed(int value) {
    m_ChecksumSeed = value;
    reset();
  }

  public String checksumSeedTipText() {
    return "set block/file checksum seed (advanced)";
  }

  public boolean isIpv4() {
    return m_Ipv4;
  }

  public void setIpv4(boolean value) {
    m_Ipv4 = value;
    reset();
  }

  public String ipv4TipText() {
    return "prefer IPv4";
  }

  public boolean isIpv6() {
    return m_Ipv6;
  }

  public void setIpv6(boolean value) {
    m_Ipv6 = value;
    reset();
  }

  public String ipv6TipText() {
    return "prefer IPv6";
  }

  public boolean isVersion() {
    return m_Version;
  }

  public void setVersion(boolean value) {
    m_Version = value;
    reset();
  }

  public String versionTipText() {
    return "print version number";
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
    CollectingProcessOutput		output;

    result = null;

    try {
      rsync = new com.github.fracpete.rsync4j.RSync();
      rsync.verbose(m_Verbose);
      rsync.info(m_Info);
      rsync.debug(m_Debug);
      rsync.msgs2stderr(m_Msgs2stderr);
      rsync.quiet(m_Quiet);
      rsync.noMotd(m_NoMotd);
      rsync.checksum(m_Checksum);
      rsync.archive(m_Archive);
      rsync.recursive(m_Recursive);
      rsync.relative(m_Relative);
      rsync.noImpliedDirs(m_NoImpliedDirs);
      rsync.backup(m_Backup);
      rsync.backupDir(m_BackupDir);
      rsync.suffix(m_Suffix);
      rsync.update(m_Update);
      rsync.inplace(m_Inplace);
      rsync.append(m_Append);
      rsync.appendVerify(m_AppendVerify);
      rsync.dirs(m_Dirs);
      rsync.links(m_Links);
      rsync.copyLinks(m_CopyLinks);
      rsync.copyUnsafeLinks(m_CopyUnsafeLinks);
      rsync.safeLinks(m_SafeLinks);
      rsync.mungeLinks(m_MungeLinks);
      rsync.copyDirlinks(m_CopyDirlinks);
      rsync.keepDirlinks(m_KeepDirlinks);
      rsync.hardLinks(m_HardLinks);
      rsync.perms(m_Perms);
      rsync.executability(m_Executability);
      rsync.chmod(m_Chmod);
      rsync.xattrs(m_Xattrs);
      rsync.owner(m_Owner);
      rsync.group(m_Group);
      rsync.devices(m_Devices);
      rsync.specials(m_Specials);
      rsync.times(m_Times);
      rsync.omitDirTimes(m_OmitDirTimes);
      rsync.omitLinkTimes(m_OmitLinkTimes);
      rsync.super_(m_Super_);
      rsync.fakeSuper(m_FakeSuper);
      rsync.sparse(m_Sparse);
      rsync.preallocate(m_Preallocate);
      rsync.dryRun(m_DryRun);
      rsync.wholeFile(m_WholeFile);
      rsync.oneFileSystem(m_OneFileSystem);
      rsync.blockSize(m_BlockSize);
      rsync.rsh(m_Rsh);
      rsync.rsyncPath(m_RsyncPath);
      rsync.existing(m_Existing);
      rsync.ignoreExisting(m_IgnoreExisting);
      rsync.removeSourceFiles(m_RemoveSourceFiles);
      rsync.delete(m_Delete);
      rsync.deleteBefore(m_DeleteBefore);
      rsync.deleteDuring(m_DeleteDuring);
      rsync.deleteDelay(m_DeleteDelay);
      rsync.deleteAfter(m_DeleteAfter);
      rsync.deleteExcluded(m_DeleteExcluded);
      rsync.ignoreMissingArgs(m_IgnoreMissingArgs);
      rsync.deleteMissingArgs(m_DeleteMissingArgs);
      rsync.ignoreErrors(m_IgnoreErrors);
      rsync.force(m_Force);
      rsync.maxDelete(m_MaxDelete);
      rsync.maxSize(m_MaxSize);
      rsync.minSize(m_MinSize);
      rsync.partial(m_Partial);
      rsync.delayUpdates(m_DelayUpdates);
      rsync.pruneEmptyDirs(m_PruneEmptyDirs);
      rsync.numericIds(m_NumericIds);
      rsync.usermap(m_Usermap);
      rsync.groupmap(m_Groupmap);
      rsync.chown(m_Chown);
      rsync.timeout(m_Timeout);
      rsync.contimeout(m_Contimeout);
      rsync.ignoreTimes(m_IgnoreTimes);
      rsync.remoteOption(m_RemoteOption);
      rsync.sizeOnly(m_SizeOnly);
      rsync.modifyWindow(m_ModifyWindow);
      rsync.tempDir(m_TempDir);
      rsync.fuzzy(m_Fuzzy);
      rsync.compareDest(BaseObject.toStringArray(m_CompareDest));
      rsync.copyDest(BaseObject.toStringArray(m_CopyDest));
      rsync.linkDest(BaseObject.toStringArray(m_LinkDest));
      rsync.compress(m_Compress);
      rsync.compressLevel(m_CompressLevel);
      rsync.skipCompress(m_SkipCompress);
      rsync.cvsExclude(m_CvsExclude);
      rsync.filter(BaseObject.toStringArray(m_Filter));
      rsync.exclude(BaseObject.toStringArray(m_Exclude));
      if (!m_ExcludeFrom.isDirectory())
	rsync.excludeFrom(m_ExcludeFrom.getAbsolutePath());
      rsync.include(BaseObject.toStringArray(m_Include));
      if (!m_IncludeFrom.isDirectory())
	rsync.includeFrom(m_IncludeFrom.getAbsolutePath());
      if (!m_FilesFrom.isDirectory())
	rsync.filesFrom(m_FilesFrom.getAbsolutePath());
      rsync.from0(m_From0);
      rsync.protectArgs(m_ProtectArgs);
      rsync.address(m_Address);
      rsync.port(m_Port);
      rsync.sockopts(m_Sockopts);
      rsync.blockingIO(m_BlockingIO);
      rsync.stats(m_Stats);
      rsync.eightBitOutput(m_EightBitOutput);
      rsync.humanReadable(m_HumanReadable);
      rsync.progress(m_Progress);
      rsync.itemizeChanges(m_ItemizeChanges);
      rsync.outFormat(m_OutFormat);
      if (!m_LogFile.isDirectory())
	rsync.logFile(m_LogFile.getAbsolutePath());
      rsync.logFileFormat(m_LogFileFormat);
      rsync.passwordFile(m_PasswordFile);
      rsync.listOnly(m_ListOnly);
      rsync.bwlimit(m_Bwlimit);
      if (m_Outbuf.length() == 1)
	rsync.outbuf(m_Outbuf.charAt(0));
      if (!m_WriteBatch.isDirectory())
	rsync.writeBatch(m_WriteBatch.getAbsolutePath());
      if (!m_OnlyWriteBatch.isDirectory())
	rsync.onlyWriteBatch(m_OnlyWriteBatch.getAbsolutePath());
      if (!m_ReadBatch.isDirectory())
	rsync.readBatch(m_ReadBatch.getAbsolutePath());
      rsync.protocol(m_Protocol);
      rsync.iconv(m_Iconv);
      rsync.checksumSeed(m_ChecksumSeed);
      rsync.ipv4(m_Ipv4);
      rsync.ipv6(m_Ipv6);
      rsync.version(m_Version);
      rsync.maxTime(m_MaxTime);

      rsync.outputCommandline(m_OutputCommandline);
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
