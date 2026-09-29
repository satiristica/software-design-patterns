package sdplearningmaterial.model;

public class MaterialData {
    private final String title;
    private final String content;

    public MaterialData(String title, String content) {
    if (title == null || title.isBlank()) {
        throw new IllegalArgumentException("title cannot be empty");
    }
    
    if (content == null || content.isBlank()) {
        throw new IllegalArgumentException("content cannot be empty");
    }

    this.title = title;
    this.content = content;
}

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}