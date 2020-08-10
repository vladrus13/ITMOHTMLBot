import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class Merger {
    public static final Logger logger = Logger.getLogger(HTMLer.class.getName());
    public static final Properties properties = new Properties();

    public static void main(String[] args) {
        if (HTMLer.readyToWork(properties, logger)) return;
        File git = new File(properties.getProperty("path_to_git"));
        Path previousPath = Paths.get(properties.getProperty("path_to_git")).resolve(properties.getProperty("name") + ".txt");
        File[] merged = git.listFiles((dir, name) -> (name.endsWith(".txt")));
        if (merged == null) {
            logger.severe("No found files!");
            return;
        }
        Map<String, Student> students = new HashMap<>();
        for (File file : merged) {
            try {
                try (BufferedReader bufferedReader = Files.newBufferedReader(file.toPath())) {
                    String reader;
                    while ((reader = bufferedReader.readLine()) != null) {
                        Student student = new Student(reader.split("#"));
                        if (students.containsKey(student.getName())) {
                            if (students.get(student.getName()).getComment() == -1) {
                                students.get(student.getName()).setComment(student.getComment());
                            } else {
                                if (student.getComment() != -1) {
                                    logger.severe("Conflict on student: " + student.toString().replace('#', ' '));
                                }
                            }
                        } else {
                            students.put(student.getName(), student);
                        }
                    }
                }
            } catch (IOException e) {
                logger.severe(Arrays.toString(e.getStackTrace()));
            }
        }
        ArrayList<Student> toWrite = new ArrayList<>();
        try (BufferedReader bufferedReader = Files.newBufferedReader(previousPath)) {
            String reader;
            while ((reader = bufferedReader.readLine()) != null) {
                Student student = new Student(reader.split("#"));
                toWrite.add(students.get(student.getName()));
            }
        } catch (IOException e) {
            logger.severe(Arrays.toString(e.getStackTrace()));
        }
        writeToPrevious(previousPath, toWrite, logger);
    }

    static void writeToPrevious(Path previousPath, ArrayList<Student> toWrite, Logger logger) {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(previousPath)) {
            for (Student student : toWrite) {
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
