package sdplearningmaterial;

import sdplearningmaterial.abstraction.StudyCard;
import sdplearningmaterial.factory.ExporterFactory;
import sdplearningmaterial.implementor.Exporter;

public class Client {
    public static void main(String[] args) {
        String format;

        if (args.length == 0) {
            format = "markdown";
        } else {
            format = args[0];
        }
        
        Exporter exporter = ExporterFactory.create(format);
        StudyCard card = new StudyCard(exporter, "Bridge",
                "Separates material from export format", "This program");
        System.out.println(card.export());
    }
}