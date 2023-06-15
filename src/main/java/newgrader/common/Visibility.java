package newgrader.common;

public enum Visibility {
    VISIBLE("visible"),
    HIDDEN("hidden"),
    AFTER_DUE_DATE("after_due_date"),
    AFTER_PUBLISHED("after_published");

    private final String text;

    Visibility(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
