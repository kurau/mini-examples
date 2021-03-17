package io.kurau.example;

import org.junit.Test;

import java.io.File;

import static io.kurau.example.Imap.byBodyPart;
import static io.kurau.example.Imap.byFrom;
import static io.kurau.example.Imap.bySubject;
import static io.kurau.example.Imap.search;

public class ImapTest {

    @Test
    public void test() throws Exception {
        Imap imap = new Imap("login", "password");
        imap.connect()
                .selectFolder("INBOX")
                .searchMessages(search(byFrom("umu"), bySubject("123"), byBodyPart("sdfdf")))
                .shouldSeeMessageCount(1);
        File a = imap.getFileFromMessage(".json");

        System.out.println(a.getAbsolutePath());
        imap.close();
    }
}
