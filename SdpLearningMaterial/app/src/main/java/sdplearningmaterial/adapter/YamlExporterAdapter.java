package sdplearningmaterial.adapter;

import externalexporter.ExternalYamlExporter;
import sdplearningmaterial.exception.ExportException;
import sdplearningmaterial.implementor.Exporter;
import sdplearningmaterial.model.MaterialData;

public class YamlExporterAdapter implements Exporter {

    @Override
    public String format() {
        return "yaml";
    }

    private final ExternalYamlExporter external;

    public YamlExporterAdapter() {
        this(new ExternalYamlExporter());
    }

    YamlExporterAdapter(ExternalYamlExporter external) {
        this.external = external;
    }


    @Override
    public String export(MaterialData data) {
        String result = external.generateDocument(
                data.getContent(), data.getTitle());

        if (result == null) {
            throw new ExportException("YAML needs a title and content");
        }

        return result;
    }
}