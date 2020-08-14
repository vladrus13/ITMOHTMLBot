/**
 * Student class
 */
public class Student implements Comparable<Student> {
    /**
     * Is on quota
     */
    private final boolean quota;
    /**
     * Chance to push
     */
    private int comment;
    /**
     * Number of person
     */
    private final int number;
    /**
     * Name of person
     */
    private final String name;
    /**
     * EGE
     */
    private final int EGE;
    /**
     * Consent to enrollment
     */
    private final boolean accept;
    /**
     * Olympiad
     */
    private final String olympiad;

    /**
     * Constructor for class
     * @param number number of student
     * @param name name of student
     * @param EGE EGE of student
     * @param accept is consent to enrollment
     * @param quota is on quota
     * @param olympiad olympiad of student
     */
    public Student(int number, String name, int EGE, boolean accept, boolean quota, String olympiad) {
        comment = -1;
        this.quota = quota;
        this.number = number;
        this.name = name;
        this.EGE = EGE;
        this.accept = accept;
        this.olympiad = olympiad;
    }

    /**
     * Constructor for class
     * @param strings {comment, number, name, EGE, accept, quota, olympiad}
     */
    public Student(String[] strings) {
        this(Integer.parseInt(strings[1]),
                strings[2],
                strings[3].isEmpty() ? 0 : Integer.parseInt(strings[3]),
                strings[4].equals("true") || strings[4].equals("Да"),
                strings[5].equals("true") || strings[5].equals("Да"),
                strings.length > 6 ? strings[6] : "");
        comment = accept ? 100 : strings[0].isEmpty() ? -1 : Integer.parseInt(strings[0]);
    }

    /**
     * String representation
     * @return string representation
     */
    public String toString() {
        return comment + "#" + number + "#" + name + "#" + EGE + "#" + accept + "#" + quota + "#" + olympiad;
    }

    /**
     * HTML representation
     * @param number number on consent-students
     * @return HTML representation
     */
    public String toHTMLString(int number) {
        String color = "enroll-unknown";
        if (comment >= 0) color = "enroll-no";
        if (comment >= 30) color = "enroll-maybe";
        if (comment >= 80) color = "enroll-likely";
        if (comment == 100) color = "enroll-ok";
        if (comment >= 90 && number > 120) color = "enroll-low";
        return String.format("\t<tr%s><td>%s</td><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n",
                " class=\"" + color + "\"",
                comment >= 80 ? number : "",
                this.number,
                name,
                olympiad.isEmpty() ? (quota ? "Квота" : (EGE == 0 ? "" : EGE)) : "БВИ",
                accept ? "Да" : "Нет",
                comment == -1 ? "" : comment == -2 ? "Нет комментария" : comment + "%");
    }

    /**
     * Setter for comment
     * @param comment comment
     */
    public void setComment(int comment) {
        this.comment = comment;
    }

    /**
     * Getter for name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for comment
     * @return comment
     */
    public int getComment() {
        return comment;
    }

    /**
     * Is student have olympiad
     * @return boolean
     */
    public boolean isBVI() {
        return !olympiad.equals("");
    }

    /**
     * Is student have quota
     * @return boolean
     */
    public boolean isQuota() {
        return quota;
    }

    @Override
    public int compareTo(Student o) {
        return o.EGE - this.EGE == 0 ? name.compareTo(o.name) : o.EGE - this.EGE;
    }
}
