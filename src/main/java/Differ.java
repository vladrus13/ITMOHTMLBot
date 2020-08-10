import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Differ {

    public static final Logger logger = Logger.getLogger(Differ.class.getName());

    /**
     * FILE we parse
     */
    public static String CURRENT = "i.html";

    private static Element getTable(String name) throws IOException {
        Document document;
        document = Jsoup.parse(new File("resources/" + name), "UTF-8");
        Elements elements = document.getElementsByTag("table");
        return elements.get(0);
    }

    /**
     * Main for Launcher
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }
        if (args.length != 0) {
            logger.info("Choose input-file");
            if (args.length != 1) {
                logger.severe("Usage: Launcher <current-file.html>");
            }
            CURRENT = args[0];
        } else {
            logger.info("Choose standard files");
        }
        Element current;
        try {
            current = getTable(CURRENT);
        } catch (IOException e) {
            logger.severe(Arrays.toString(e.getStackTrace()));
            return;
        }
        if (current == null) {
            logger.log(Level.SEVERE, "Table can't find");
            return;
        }
        Map<String, Student> students = new HashMap<>();
        if (Files.exists(Paths.get("resources/previous.txt"))) {
            logger.info("Previous file exists");
            try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get("resources/previous.txt"))) {
                String reader;
                while ((reader = bufferedReader.readLine()) != null) {
                    Student student = new Student(reader.split("#"));
                    students.put(student.getName(), student);
                }
            } catch (IOException e) {
                logger.severe(Arrays.toString(e.getStackTrace()));
                return;
            }
        } else {
            logger.info("Previous file doesn't exists");
        }
        boolean isCurrentQuota = false;
        boolean isCurrentBan = false;
        ArrayList<Student> studentsToWrite = new ArrayList<>();
        Elements htmlStudents = current.getElementsByTag("tr");
        for (Element element : htmlStudents) {
            if (element.hasClass("hdr")) {
                continue;
            }
            if (!element.getElementsByTag("td").get(0).attributes().isEmpty()) {
                isCurrentQuota = element.getElementsByTag("td").get(0).ownText().equals("на бюджетное место в пределах особой квоты") ||
                        element.getElementsByTag("td").get(0).ownText().equals("на бюджетное место в пределах целевой квоты");
                isCurrentBan = element.getElementsByTag("td").get(0).ownText().equals("на контрактной основе");
                element.getElementsByTag("td").get(0).remove();
            }
            if (isCurrentBan) {
                continue;
            }
            Elements elements = element.getElementsByTag("td");
            Student student = new Student(new String[]{
                    "",
                    elements.get(0).ownText(),
                    elements.get(2).ownText(),
                    elements.get(7).ownText(),
                    elements.get(10).ownText(),
                    String.valueOf(isCurrentQuota),
                    elements.get(12).ownText()
            });
            studentsToWrite.add(student);
            if (students.containsKey(element.getElementsByTag("td").get(2).html())) {
                student.setComment(students.get(element.getElementsByTag("td").get(2).html()).getComment());
            }
        }
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get("resources/previous.txt"))) {
            for (Student student : studentsToWrite) {
                if (student.getComment() == -1) {
                    logger.warning("Student: " + student.toString().replace("#", " ") + ": no comment.");
                }
                bufferedWriter.write(String.format("%s\n", student.toString()));
            }
        } catch (IOException e) {
            logger.severe(Arrays.toString(e.getStackTrace()));
        }
    }
}
