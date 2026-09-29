package sdplearningmaterial.implementor;

import sdplearningmaterial.model.MaterialData;

public interface Exporter {
    void export(MaterialData data, String filename);
}