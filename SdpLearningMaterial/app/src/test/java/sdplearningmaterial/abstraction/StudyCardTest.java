package sdplearningmaterial.abstraction;

import org.junit.jupiter.api.Test;
import sdplearningmaterial.implementor.Exporter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import sdplearningmaterial.model.MaterialData;

class StudyCardTest {
    @Test
    void sendsDataToExporter() {
        Exporter fake = new Exporter() {
            @Override
            public String export(MaterialData data) {
                return data.getTitle() + ": " + data.getContent();
            }

            @Override
            public String format() {
                return "test";
            }
        };
        StudyCard card = new StudyCard(fake, "Bridge", "Definition", "Example");

        assertEquals(
                "Bridge: Definition: Definition\nExample: Example",
                card.export()
        );
    }
}