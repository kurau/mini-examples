package io.kurau.example;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MiniTest {

    private Map<String, Object> freemarkerModel = new HashMap<>();
    private File tmp;
    private File folderToZip;

    @Before
    public void setUp() throws IOException, URISyntaxException {
        tmp = Files.createTempDirectory(UUID.randomUUID().toString()).toFile();
        folderToZip = getFileFromSrc("zipfolder");

        freemarkerModel.put("node", "name");
        freemarkerModel.put("user", "u r ETC");
    }

    @Test
    public void freemarker() throws IOException {
        Freemarker freemarker = new Freemarker();
        freemarker.tmpDir(tmp)
                .onData(freemarkerModel)
                .addParam("user2", "M")
                .print();
    }

    @Test
    public void zip() throws IOException {
        Zip zip = new Zip();
        File zipped = zip.tmpFolder(tmp)
                .fromFolder(folderToZip)
                .toZip("123.zip");
    }

    @Test
    public void pack() throws IOException {
        Freemarker freemarker = new Freemarker();
        File xml = freemarker.tmpDir(tmp)
                .onData(freemarkerModel)
                .addParam("user2", "M2")
                .create("template.xml");

        Zip zip = new Zip();
        zip.tmpFolder(tmp)
                .fromFolder(folderToZip)
                .andFile(xml)
                .toZip("123.zip");
        zip.print();
    }

    private File getFileFromSrc(String name) throws URISyntaxException {
        return new File(Objects.requireNonNull(getClass().getClassLoader().getResource(name)).toURI());
    }
}
