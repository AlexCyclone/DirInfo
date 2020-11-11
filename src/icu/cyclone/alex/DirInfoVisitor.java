package icu.cyclone.alex;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * @author Aleksey Babanin
 * @since 2020/07/04
 */
public class DirInfoVisitor extends SimpleFileVisitor<Path> {
    private static final String[] UNITS = {"B", "Kb", "Mb", "Gb", "Tb"};
    private static final String SUMMARY = "Summary:";
    private static final String SUMMARY_FORMAT = "Files: %d Folders: %d Size: %s (%d)%n";
    private static final String DETAILS = "Details:";
    private static final String DETAILS_FORMAT = "Files: %d Size: %s (%d B)%n";

    private final DirInfo summaryInfo = new DirInfo();
    private final NavigableMap<String, DirInfo> detailDirInfo = new TreeMap<>();

    public static class DirInfo {
        private int filesCount;
        private long summarySize;

        public DirInfo() {
        }

        public DirInfo(int filesCount, long summarySize) {
            this.filesCount = filesCount;
            this.summarySize = summarySize;
        }

        public int getFilesCount() {
            return filesCount;
        }

        public long getSummarySize() {
            return summarySize;
        }

        public void addFileInfo(DirInfo dirInfo) {
            this.summarySize += dirInfo.getSummarySize();
            this.filesCount += dirInfo.getFilesCount();
        }
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        detailDirInfo.put(dir.toString(), new DirInfo());
        return super.preVisitDirectory(dir, attrs);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        DirInfo currentFileInfo = new DirInfo(1, Files.size(file));
        summaryInfo.addFileInfo(currentFileInfo);
        detailDirInfo.get(file.getParent().toString()).addFileInfo(currentFileInfo);
        return super.visitFile(file, attrs);
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        return FileVisitResult.SKIP_SUBTREE;
    }

    public void printSummary(PrintStream printStream) {
        printStream.println(SUMMARY);
        printStream.println(detailDirInfo.firstEntry().getKey());
        printStream.printf((SUMMARY_FORMAT),
                summaryInfo.getFilesCount(),
                detailDirInfo.size() - 1,
                formatSize(summaryInfo.getSummarySize()),
                summaryInfo.getSummarySize());
    }

    public void printDetails(PrintStream printStream) {
        printStream.println(DETAILS);
        detailDirInfo.forEach((k, v) -> {
            printStream.println(k);
            printStream.printf((DETAILS_FORMAT),
                    v.getFilesCount(),
                    formatSize(v.getSummarySize()),
                    v.getSummarySize());
        });
    }

    private String formatSize(long size) {
        int i = 0;
        double unitSize = (double) size;
        while (i < UNITS.length - 1 && unitSize >= 1024) {
            unitSize /= 1024;
            i++;
        }
        return i == 0 ? size + " " + UNITS[0] : String.format("%.2f %s", unitSize, UNITS[i]);
    }
}
