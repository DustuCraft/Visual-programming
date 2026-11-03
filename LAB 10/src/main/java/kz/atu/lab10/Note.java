package kz.atu.lab10;

/** Рецепт: title - название, body - ингредиенты и шаги, priority - приоритет. */
public class Note {
    private String title;
    private String body;
    private String priority;

    public Note() {}

    public Note(String title, String body, String priority) {
        this.title = title;
        this.body = body;
        this.priority = priority;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    @Override public String toString() {
        return title + (priority == null ? "" : " [" + priority + "]");
    }
}
