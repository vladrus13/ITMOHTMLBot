import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class HTMLer {
    public static Logger logger = Logger.getLogger(HTMLer.class.getName());

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
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get("resources/index.html"))) {
            bufferedWriter.write("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>ITMO</title>\n" +
                    "    <link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">\n" +
                    "</head>\n" +
                    "<body>");
            bufferedWriter.write("<table>\n");
            bufferedWriter.write("<tr><td>Id</td><td>Имя</td><td>ЕГЭ/БВИ</td><td>Заявление на согласие</td><td>Шансы подать оригинал</td></tr>\n");
            for (Student student : students) {
                bufferedWriter.write("\t" + student.toHTMLString());
            }
            bufferedWriter.write("</table>\n</body>\n");
        } catch (IOException e) {
            logger.severe(Arrays.toString(e.getStackTrace()));
        }
    }
}
