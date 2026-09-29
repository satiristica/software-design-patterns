package sdplearningmaterial.abstraction;

import org.junit.jupiter.api.Test;
import sdplearningmaterial.implementor.Exporter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComparisonCardTest {
    @Test
    void sendsDataToExporter() {
        Exporter fake = data -> data.getTitle() + ": " + data.getContent();
        ComparisonCard card = new ComparisonCard(fake,
                "Bridge", "Adapter", "They have different purposes");

        assertEquals(
                "Bridge vs Adapter: difference => They have different purposes",
                card.export()
        );
    }
}