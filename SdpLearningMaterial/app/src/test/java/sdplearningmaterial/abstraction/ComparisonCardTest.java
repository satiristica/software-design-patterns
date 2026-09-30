package sdplearningmaterial.abstraction;

import org.junit.jupiter.api.Test;
import sdplearningmaterial.implementor.Exporter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import sdplearningmaterial.model.MaterialData;


class ComparisonCardTest {
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
        
        ComparisonCard card = new ComparisonCard(fake,
                "Bridge", "Adapter", "They have different purposes");

        assertEquals(
                "Bridge vs Adapter: difference: They have different purposes",
                card.export()
        );
    }
}