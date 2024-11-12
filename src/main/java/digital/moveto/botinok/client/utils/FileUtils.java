package digital.moveto.botinok.client.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {
    public static void saveFile(String fileName, String content) {
        try {
            mkdirs(String.valueOf(Paths.get(fileName).getParent()));
            Files.writeString(Paths.get(fileName), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean mkdirs(String fileName) {
        File f = new File(fileName);
        return f.mkdirs();
    }

    public static String readFile(String fileName) {
        try {
            return Files.readString(Paths.get(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isFileExists(String fileName) {
        return Files.exists(Paths.get(fileName));
    }

    public static List<String> getFilesRecursive(String path) {
        try {
            return Files.walk(Paths.get(path))
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getFolders(String path) {
        return Stream.of(Objects.requireNonNull(new File(path).listFiles()))
                .filter(File::isDirectory)
                .map(File::getName)
                .collect(Collectors.toList());
    }

    public static void deleteFile(String fileName) {
        try {
            Files.delete(Paths.get(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteFolder(String fileName) {
        deleteFolder(new File(fileName));
    }

    public static boolean deleteFolder(File folder) {
        File[] allContents = folder.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteFolder(file);
            }
        }
        return folder.delete();

    }

}
