import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class HTMLer {
    public static final Logger logger = Logger.getLogger(HTMLer.class.getName());

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }
        ArrayList<Student> students = new ArrayList<>();
        if (Files.exists(Paths.get("resources/previous.txt"))) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get("resources/previous.txt"))) {
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
            if (student.isBVI() || student.isQuota()) {
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
            bufferedWriter.write("<tr><th>№</th><th>На сайте</th><th>Имя абитуриента</th><th>Балл</th><th>Согласие</th><th>Шанс</th></tr>\n");
            int it = 1;
            for (Student student : toWrite) {
                if (student.getComment() == -1) {
                    logger.warning("Student: " + student.toString().replace("#", " ") + ": no comment.");
                }
                bufferedWriter.write("\t" + student.toHTMLString(it));
                if (student.getComment() >= 90) it++;
            }
            bufferedWriter.write("</table>\n</body>\n");
        } catch (IOException e) {
            logger.severe(Arrays.toString(e.getStackTrace()));
        }
    }
}
