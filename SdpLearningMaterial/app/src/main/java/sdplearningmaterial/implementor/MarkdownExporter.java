package sdplearningmaterial.implementor;

import sdplearningmaterial.model.MaterialData;

public class MarkdownExporter implements Exporter {
    public String export(MaterialData data) {
        return "# " + data.getTitle() + "\n\n" + data.getContent();
    }
}