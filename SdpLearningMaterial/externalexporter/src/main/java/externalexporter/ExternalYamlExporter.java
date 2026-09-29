package externalexporter;

public class ExternalYamlExporter {
    public String generateDocument(String body, String heading) {
        if (body == null || heading == null
                || body.isBlank() || heading.isBlank()) {
            return null;
        }

        return "title: " + heading
                + "\ncontent: |\n  " + body.replace("\n", "\n  ");
    }
}