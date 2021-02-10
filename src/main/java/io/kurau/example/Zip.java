package io.kurau.example;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Zip {

    private static final int BUFFER_SIZE = 4096;

    private File output;
    private File folder;
    private List<File> files = new ArrayList<>();
    private File tempDirectory;

    public Zip tmpFolder(File tmpFolder) {
        tempDirectory = tmpFolder;
        System.out.println("tmp: " + tempDirectory.getAbsolutePath());
        return this;
    }

    public Zip fromFolder(File allFromFolder) {
        folder = allFromFolder;
        return this;
    }

    public Zip andFile(File file) {
        files.add(file);
        return this;
    }

    public void print() throws IOException {
        ZipFile zipFile = new ZipFile(output);
        System.out.println("Zip:");
        zipFile.stream().forEach(e -> System.out.println(" -> " + e.getName()));
    }

    public File toZip(String zipName) {
        output = new File(tempDirectory, zipName);
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output))) {
            zipDirectory(folder, "", zos);
            for (File file : files) {
                if (file.isDirectory()) {
                    zipDirectory(file, file.getName(), zos);
                } else {
                    zipFile(file, zos);
                }
            }
            zos.flush();
            return output;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(" zip error ");
        }
    }

    // from https://www.codejava.net/java-se/file-io/zip-directories
    private void zipDirectory(File folder,
                              String parentFolder,
                              ZipOutputStream zos) throws IOException {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
                continue;
            }
            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));

            write(file, zos);
        }
    }

    private void zipFile(File file, ZipOutputStream zos) throws IOException {
        zos.putNextEntry(new ZipEntry(file.getName()));

        write(file, zos);
    }

    private void write(File file, ZipOutputStream zos) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read;
        while ((read = bis.read(bytesIn)) != -1) {
            zos.write(bytesIn, 0, read);
        }
        zos.closeEntry();
    }
}
