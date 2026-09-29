package sdplearningmaterial.implementor;

import sdplearningmaterial.model.MaterialData;

public class MarkdownExporter implements Exporter {
    
    @Override
    public String format() {
        return "markdown";
    }

    public String export(MaterialData data) {
        return "# " + data.getTitle() + "\n\n" + data.getContent();
    }
}