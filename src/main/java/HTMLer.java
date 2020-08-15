import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * HTMLer. Convert your .txt file to index.html. BE CAREFUL!
 */
public class HTMLer {
    /**
     * Logger
     */
    public static final Logger logger = Logger.getLogger(HTMLer.class.getName());
    /**
     * Properties
     */
    public static final Properties properties = new Properties();

    /**
     * Main-class for HTMLer
     * @param args ignored
     */
    public static void main(String[] args) {
        /*
        Ready to work. Just ready properties and logger
         */
        if (readyToWork(properties, logger)) return;
        Path previousPath = Paths.get(properties.getProperty("path_to_git")).resolve(properties.getProperty("name") + ".txt");
        ArrayList<Student> students = new ArrayList<>();
        if (Files.exists(previousPath)) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(previousPath)) {
                String reader;
                while ((reader = bufferedReader.readLine()) != null) {
                    Student student = new Student(reader.split("#"));
                    students.add(student);
                }
            } catch (IOException e) {
                logger.severe(Arrays.toString(e.getStackTrace()));
                return;
            }
        } else {
            logger.severe("Previous file doesn't exists");
        }
        ArrayList<Student> toWrite = new ArrayList<>();
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            if (student.isBVI()) {
                toWrite.add(student);
                // O(n)
                students.remove(student);
                i--;
            }
        }
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            if (student.isQuota()) {
                toWrite.add(student);
                // O(n)
                students.remove(student);
                i--;
            }
        }
        students.sort(Student::compareTo);
        toWrite.addAll(students);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get("resources/index.html"))) {
            bufferedWriter.write("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "\t<meta charset=\"UTF-8\">\n" +
                    "\t<title>Приемная кампания КТ 2020</title>\n" +
                    "\t<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">\n" +
                    "\t<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\" />\n" +
                    "\t<link rel=\"shortcut icon\" sizes=\"16x16 32x32 64x64\" href=\"meta/favicon.ico\" />\n" +
                    "\t<link rel=\"apple-touch-icon\" sizes=\"180x180\" href=\"meta/apple-touch-icon.png\" />\n" +
                    "\t<link rel=\"manifest\" href=\"meta/site.webmanifest\" />" +
                    "</head>\n" +
                    "<body>\n");
            bufferedWriter.write("<table>\n");
            bufferedWriter.write("<tr><th>№</th><th>На сайте</th><th>Имя абитуриента</th><th>Балл</th><th>Согласие</th><th>Шанс подачи</th></tr>\n");
            int it = 1;
            for (Student student : toWrite) {
                if (student.getComment() == -1) {
                    logger.warning("Student: " + student.toString().replace("#", " ") + ": no comment.");
                }
                bufferedWriter.write("\t" + student.toHTMLString(it));
                if (student.getComment() >= 80) it++;
            }
            bufferedWriter.write("</table>\n</body>\n");
        } catch (IOException e) {
            logger.severe(Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Ready to work. Just load Logger and Properties
     * @param properties properties
     * @param logger logger
     * @return if we should exit from program
     */
    static boolean readyToWork(Properties properties, Logger logger) {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (IOException exception) {
            exception.printStackTrace();
            return true;
        }
        try {
            properties.load(new FileReader("resources/paths.properties"));
        } catch (IOException e) {
            logger.severe(Arrays.toString(e.getStackTrace()));
            return true;
        }
        return false;
    }
}
