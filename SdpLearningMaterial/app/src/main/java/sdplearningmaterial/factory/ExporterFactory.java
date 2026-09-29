package sdplearningmaterial.factory;

import sdplearningmaterial.adapter.YamlExporterAdapter;
import sdplearningmaterial.implementor.Exporter;
import sdplearningmaterial.implementor.JsonExporter;
import sdplearningmaterial.implementor.MarkdownExporter;

public class ExporterFactory {
    public static Exporter create(String format) {
        if (format.equalsIgnoreCase("markdown")) {
            return new MarkdownExporter();
        }
        if (format.equalsIgnoreCase("json")) {
            return new JsonExporter();
        }
        if (format.equalsIgnoreCase("yaml")) {
            return new YamlExporterAdapter();
        }
        throw new IllegalArgumentException("Unknown format: " + format);
    }
}