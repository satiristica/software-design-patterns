package sdplearningmaterial.implementor;

import sdplearningmaterial.model.MaterialData;

public class JsonExporter implements Exporter {
    
    @Override
    public String format() {
        return "json";
    }

    @Override
    public String export(MaterialData data) {
        return "{\n"
                + "  \"title\": \"" + data.getTitle() + "\",\n"
                + "  \"content\": \"" + data.getContent().replace("\n", "\\n") + "\"\n"
                + "}";
    }
}