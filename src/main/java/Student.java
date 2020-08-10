public class Student {
    private final boolean quota;
    private int comment;
    private final int number;
    private final String name;
    private final int EGE;
    private final boolean accept;
    private final String olympiad;

    public Student(int number, String name, int EGE, boolean accept, boolean quota, String olympiad) {
        comment = -1;
        this.quota = quota;
        this.number = number;
        this.name = name;
        this.EGE = EGE;
        this.accept = accept;
        this.olympiad = olympiad;
    }

    public Student(String[] strings) {
        this(Integer.parseInt(strings[1]),
                strings[2],
                strings[3].isEmpty() ? 0 : Integer.parseInt(strings[3]),
                strings[4].equals("true") || strings[4].equals("Да"),
                strings[5].equals("true") || strings[5].equals("Да"),
                strings.length > 6 ? strings[6] : "");
        comment = accept ? 100 : strings[0].isEmpty() ? -1 : Integer.parseInt(strings[0]);
    }

    public String toString() {
        return comment + "#" + number + "#" + name + "#" + EGE + "#" + accept + "#" + quota + "#" + olympiad;
    }

    public String toHTMLString() {
        return String.format("\t<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n",
                number,
                name,
                olympiad.isEmpty() ? (quota ? "Квота" : (EGE == 0 ? "" : EGE)) : "БВИ",
                accept ? "Да" : "Нет",
                comment == -1 ? "" : comment + "%");
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public int getComment() {
        return comment;
    }

    public int getNumber() {
        return number;
    }

    public int getEGE() {
        return EGE;
    }

    public boolean isAccept() {
        return accept;
    }

    public String getOlympiad() {
        return olympiad;
    }
}
