package org.gradle.util;

import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * General purpose file filter class.
 *
 * @author Tom Eyckmans
 */
public class GFileFilterUtil {

    public List<File> globFilter(final List<File> directories, final List<String> includes, final List<String> excludes, final boolean includeDirectories, final boolean recurse, final boolean trace) {
        final ListFileFilterMatchReceiver matchReceiver = new ListFileFilterMatchReceiver();
        final List<FileFilterMatchReceiver> matchReceivers = new ArrayList<FileFilterMatchReceiver>();
        if ( trace ) {
            matchReceivers.add(new TraceFileFilterMatchReceiver());
        }
        matchReceivers.add(matchReceiver);

        globFilter(directories, includes, excludes, includeDirectories, recurse, matchReceivers);

        return matchReceiver.getFilterMatches();
    }

    public void globFilter(final List<File> directories, final List<String> includes, final List<String> excludes, final boolean includeDirectories, final boolean recurse, final boolean trace, final BlockingQueueItemProducer<File> blockingQueueItemProducer) {
        final FileFilterMatchReceiver matchReceiver = new BlockingQueueFileFilterMatchReceiver(blockingQueueItemProducer);
        final List<FileFilterMatchReceiver> matchReceivers = new ArrayList<FileFilterMatchReceiver>();
        if ( trace ) {
            matchReceivers.add(new TraceFileFilterMatchReceiver());
        }
        matchReceivers.add(matchReceiver);

        globFilter(directories, includes, excludes, includeDirectories, recurse, matchReceivers);
    }

    public void globFilter(final List<File> directories, final List<String> includes, final List<String> excludes, final boolean includeDirectories, final boolean recurse, final List<FileFilterMatchReceiver> matchReceivers) {
        if ( directories != null && directories.size() > 0 &&
             includes != null && includes.size() > 0 ) {
            // compile all includes/excludes to patterns
            final GlobCompiler globCompiler = new GlobCompiler();

            final List<Pattern> includePatterns = new ArrayList<Pattern>();
            final List<Pattern> excludePatterns = new ArrayList<Pattern>();
            String currentIncludeOrExclude = null;
            try {
                for ( final String include : includes ) {
                    currentIncludeOrExclude = include;
                    includePatterns.add(globCompiler.compile(currentIncludeOrExclude));
                }
                if ( excludes != null && excludes.size() > 0 ) {
                    for ( final String exclude : excludes ) {
                        currentIncludeOrExclude = exclude;
                        excludePatterns.add(globCompiler.compile(currentIncludeOrExclude));
                    }
                }
            }
            catch ( MalformedPatternException e){
                throw new IllegalArgumentException("pattern " + currentIncludeOrExclude + " is not a valid GLOB pattern", e);
            }

            final PatternMatcher matcher = new Perl5Matcher();

            for ( final File directory : directories ) {
                processDirectory(directory, directory, includePatterns, excludePatterns, includeDirectories, recurse, matchReceivers, matcher);
            }
        }
    }

    private void processDirectory(File rootDirectory, File directory, final List<Pattern> includes, final List<Pattern> excludes, final boolean includeDirectories, final boolean recurse, final List<FileFilterMatchReceiver> matchReceivers, final PatternMatcher matcher) {
        final int subFilePathStart = rootDirectory.getAbsolutePath().length()+1;
        final File[] subFiles = directory.listFiles();
        if ( subFiles != null && subFiles.length > 0 ) {
            for ( final File subFile : Arrays.asList(subFiles) ) {
                final String subFilePath = subFile.getAbsolutePath().substring(subFilePathStart).replaceAll("/", ".");

                boolean exclude = false;
                if ( excludes != null ) {
                    final Iterator<Pattern> excludesIt = excludes.iterator();
                    while ( !exclude && excludesIt.hasNext() ) {
                        exclude = matcher.matches(subFilePath, excludesIt.next());
                    }
                }
                boolean include = false;
                if ( !exclude ) {
                    final Iterator<Pattern> includesIt = includes.iterator();
                    while ( !include && includesIt.hasNext() ) {
                        include = matcher.matches(subFilePath, includesIt.next());
                    }
                }
                if ( !exclude && include ) {
                    if ( subFile.isFile() || (subFile.isDirectory() && includeDirectories) )
                        filterMatched(matchReceivers, subFile, subFilePath);
                }
                else {
                    if ( subFile.isDirectory() && recurse )
                        processDirectory(rootDirectory, subFile, includes, excludes, includeDirectories, recurse, matchReceivers, matcher);
                }
            }
        }
    }

    private void filterMatched(List<FileFilterMatchReceiver> matchReceivers, File matchedFile, String matchedFilePath) {
        for ( final FileFilterMatchReceiver matchReceiver : matchReceivers ) {
            try {
                matchReceiver.receive(matchedFile, matchedFilePath);
            }
            catch ( Throwable t ) {

            }
        }
    }

    public static void main(String[] args) {

        GFileFilterUtil fileFilterUtil = new GFileFilterUtil();

        fileFilterUtil.globFilter(
                Arrays.asList(new File("/home/teyckmans/lib/org/gradle/git/gradle/src/test/groovy")),
                Arrays.asList("org.gradle.*"), // includes
                 null, // excludes
                true, true, true);

    }
}
