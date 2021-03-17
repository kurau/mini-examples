package io.kurau.example;

import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.activation.DataSource;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class Imap {

    private String host = "host.host";
    private String username;
    private String password;

    private Store store;
    private Folder folder;
    private Message[] messages;

    public Imap(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Imap connect() throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.imap.ssl.enable", "true");

        Session session = Session.getInstance(props);
        store = session.getStore("imap");
        store.connect(host, 993, username, password);
        return this;
    }

    public Imap close() throws MessagingException {
        folder.close(false);
        store.close();
        return this;
    }

    public Imap selectFolder(String folderName) throws MessagingException {
        folder = store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);
        return this;
    }

    public Imap searchMessages(SearchTerm searchTerm) throws MessagingException {
        messages = folder.search(searchTerm);
        System.out.println(messages.length);
        return this;
    }

    public Imap shouldSeeMessageCount(int count) {
        assertThat(String.format("Должны найти %d сообщения(й)", count), messages.length, is(count));
        return this;
    }

    public File getFileFromMessage(String fileExtension) throws Exception {
        Message message = messages[0];
        if (!message.isMimeType("multipart/mixed;")) {
            throw new IllegalStateException("Сообщение без вложения");
        }

        MimeMessageParser mimeParser = new MimeMessageParser((MimeMessage) message).parse();
        List<DataSource> attachmentList = mimeParser.getAttachmentList();
        for (DataSource dataSource: attachmentList) {
            final String fileName = dataSource.getName();
            System.out.println("filename: " + fileName);
            if (fileName.contains(fileExtension)) {
                try (InputStream in = dataSource.getInputStream()) {
                    File attach = Files.createTempFile("mail-", fileExtension).toFile();
                    FileUtils.copyInputStreamToFile(in, attach);
                    return attach;
                }
            }
        }
        throw  new IllegalStateException("Что-то пошло не так");
    }


    public static SubjectTerm bySubject(String subject) {
        return new SubjectTerm(subject);
    }

    public static FromStringTerm byFrom(String from) {
        return new FromStringTerm(from);
    }

    public static BodyTerm byBodyPart(String bodyPart) {
        return new BodyTerm(bodyPart);
    }

    public static AndTerm search(SearchTerm... search) {
        return new AndTerm(search);
    }
}
