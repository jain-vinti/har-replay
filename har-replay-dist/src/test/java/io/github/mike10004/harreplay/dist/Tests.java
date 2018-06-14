package io.github.mike10004.harreplay.dist;

import com.google.common.collect.Ordering;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class Tests {

    private Tests() {

    }

    public static File getDebFile() throws IOException {
        File buildDir = getBuildDir();
        Collection<File> debs = FileUtils.listFiles(buildDir, new String[]{"deb"}, false);
        List<Pair<File, FileTime>> filesWithTimes = new ArrayList<>();
        for (File debFile : debs) {
            BasicFileAttributeView view = java.nio.file.Files.getFileAttributeView(debFile.toPath(), BasicFileAttributeView.class);
            FileTime lastModified = view.readAttributes().lastModifiedTime();
            filesWithTimes.add(Pair.of(debFile, lastModified));
        }
        File debFile = filesWithTimes.stream().max(Ordering.natural().onResultOf(Pair::getRight)).map(Pair::getLeft).orElseThrow(() -> new FileNotFoundException("no deb files present in " + buildDir));
        return debFile;
    }

    public static File getBuildDir() {
        return new File(getProperties().getProperty("project.build.directory"));
    }

    public static Properties getProperties() {
        Properties p = new Properties();
        try (InputStream in = Tests.class.getResourceAsStream("/har-replay-dist/maven.properties")) {
            p.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return p;
    }
}