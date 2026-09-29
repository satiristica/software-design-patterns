package sdplearningmaterial.adapter;

import org.junit.jupiter.api.Test;
import sdplearningmaterial.exception.ExportException;
import sdplearningmaterial.model.MaterialData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


import externalexporter.ExternalYamlExporter;


class YamlExporterAdapterTest {
    @Test
    void exportsValidData() {
        YamlExporterAdapter adapter = new YamlExporterAdapter();

        assertEquals(
                "title: Bridge\ncontent: |\n  Definition",
                adapter.export(new MaterialData("Bridge", "Definition"))
        );
    }

    @Test
    void translatesExternalFailure() {
        ExternalYamlExporter failing = new ExternalYamlExporter() {
            @Override
            public String generateDocument(String body, String heading) {
                return null;
            }
        };

        YamlExporterAdapter adapter = new YamlExporterAdapter(failing);

        assertThrows(ExportException.class,
                () -> adapter.export(
                        new MaterialData("Bridge", "Definition")));
    }
}