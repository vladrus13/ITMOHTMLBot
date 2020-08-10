import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Differ {

    public static final Logger logger = Logger.getLogger(Differ.class.getName());
    public static final Properties properties = new Properties();

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
        if (HTMLer.readyToWork(properties, logger)) return;
        CURRENT = properties.getProperty("html_name");
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
        Path previousPath = Paths.get(properties.getProperty("path_to_git")).resolve(properties.getProperty("name") + ".txt");
        if (Files.exists(previousPath)) {
            logger.info("Previous file exists");
            try (BufferedReader bufferedReader = Files.newBufferedReader(previousPath)) {
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
        Merger.writeToPrevious(previousPath, studentsToWrite, logger);
    }
}
