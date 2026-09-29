package sdplearningmaterial.abstraction;

import sdplearningmaterial.implementor.Exporter;
import sdplearningmaterial.model.MaterialData;

public class ComparisonCard extends LearningMaterial {
    private final String firstPattern;
    private final String secondPattern;
    private final String difference;

    public ComparisonCard(Exporter exporter, String firstPattern, String secondPattern, String difference) {
        super(exporter);
        this.firstPattern = firstPattern;
        this.secondPattern = secondPattern;
        this.difference = difference;
    }

    @Override
    protected MaterialData createData() {
        String title = firstPattern + " vs " + secondPattern;
        String content = "difference: " + difference;
        return new MaterialData(title, content);
    }
}