package sdplearningmaterial.abstraction;

import sdplearningmaterial.implementor.Exporter;
import sdplearningmaterial.model.MaterialData;

public class StudyCard extends LearningMaterial {
    private final String patternName;
    private final String definition;
    private final String example;

    public StudyCard(Exporter exporter, String patternName, String definition, String example) {
        super(exporter);
        this.patternName = patternName;
        this.definition = definition;
        this.example = example;
    }

    @Override
    protected MaterialData createData() {
        String content = "Definition: " + definition + "\nExample: " + example;
        return new MaterialData(patternName, content);
    }
}