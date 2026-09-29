package sdplearningmaterial;

import sdplearningmaterial.abstraction.ComparisonCard;
import sdplearningmaterial.abstraction.StudyCard;
import sdplearningmaterial.factory.ExporterFactory;
import sdplearningmaterial.implementor.Exporter;

public class Client {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Provide a format: markdown, json, or yaml");
            return;
        }

        Exporter exporter = ExporterFactory.create(args[0]);

        StudyCard card = new StudyCard(exporter, "Bridge",
                "Separates material from export format", "This program");
        System.out.println(card.export());

        ComparisonCard comparison = new ComparisonCard(exporter,
                "Bridge", "Adapter", "Bridge separates, Adapter connects");
        System.out.println(comparison.export());
    }
}