public class Student {
    private String comment;
    private final int number;
    private final String name;
    private final int EGE;
    private final boolean accept;
    private final String olympiad;

    public Student(int number, String name, int EGE, boolean accept, String olympiad) {
        comment = "";
        this.number = number;
        this.name = name;
        this.EGE = EGE;
        this.accept = accept;
        this.olympiad = olympiad;
    }

    public Student(String[] strings) {
        this(Integer.parseInt(strings[1]), strings[2], strings[3].isEmpty() ? 0 : Integer.parseInt(strings[3]), Boolean.parseBoolean(strings[4]), strings.length > 5 ? strings[5] : "");
        comment = strings[0];
    }

    public String toString() {
        return "#" + number + "#" + name + "#" + EGE + "#" + accept + "#" + olympiad;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }
}
