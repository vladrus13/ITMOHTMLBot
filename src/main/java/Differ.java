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
            logger.log(Level.SEVERE, Arrays.toString(exception.getStackTrace()));
            return;
        }
        if (args.length != 0) {
            logger.info("Choose input-file");
            if (args.length != 1) {
                logger.severe("Usage: Launcher <current-file.html>");
            }
            CURRENT = args[1];
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
        ArrayList<Student> studentsToWrite = new ArrayList<>();
        Elements htmlStudents = current.getElementsByTag("tr");
        htmlStudents.stream()
                .filter(element -> !element.hasClass("hdr"))
                .forEach(element -> {
                    if (!element.getElementsByTag("td").get(0).attributes().isEmpty()) {
                        element.getElementsByTag("td").get(0).remove();
                    }
                    if (students.containsKey(element.getElementsByTag("td").get(2).html())) {
                        studentsToWrite.add(students.get(element.getElementsByTag("td").get(2).html()));
                    } else {
                        // new student
                        Elements elements = element.getElementsByTag("td");
                        studentsToWrite.add(new Student(new String[]{
                                "",
                                elements.get(0).ownText(),
                                elements.get(2).ownText(),
                                elements.get(8).ownText(),
                                elements.get(11).ownText(),
                                elements.get(12).ownText()
                        }));
                    }
                });
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get("resources/previous.txt"))) {
            for (Student student : studentsToWrite) {
                if (student.getComment().isEmpty()) {
                    logger.warning("Student: " + student.toString().replace("#", " ") + ": no comment.");
                }
                bufferedWriter.write(String.format("%s\n", student.toString()));
            }
        } catch (IOException e) {
            logger.severe(Arrays.toString(e.getStackTrace()));
        }
    }
}
