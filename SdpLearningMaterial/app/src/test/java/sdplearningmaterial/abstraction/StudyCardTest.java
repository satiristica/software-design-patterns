package sdplearningmaterial.abstraction;

import org.junit.jupiter.api.Test;
import sdplearningmaterial.implementor.Exporter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StudyCardTest {
    @Test
    void sendsDataToExporter() {
        Exporter fake = data -> data.getTitle() + ": " + data.getContent();
        StudyCard card = new StudyCard(fake, "Bridge", "Definition", "Example");

        assertEquals(
                "Bridge: Definition: Definition\nExample: Example",
                card.export()
        );
    }
}