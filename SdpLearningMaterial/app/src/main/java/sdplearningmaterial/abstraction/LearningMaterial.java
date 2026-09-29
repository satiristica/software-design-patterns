package sdplearningmaterial.abstraction;

import sdplearningmaterial.implementor.Exporter;
import sdplearningmaterial.model.MaterialData;

public abstract class LearningMaterial {
    private final Exporter exporter;

    public LearningMaterial(Exporter exporter) {
        this.exporter = exporter;
    }

    protected abstract MaterialData createData();

    public String export() {
        return exporter.export(createData());
    }
}