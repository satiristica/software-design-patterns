package sdplearningmaterial.implementor;

import sdplearningmaterial.model.MaterialData;

public interface Exporter {
    String export(MaterialData data);

    default String format() {
        return "";
    }
}