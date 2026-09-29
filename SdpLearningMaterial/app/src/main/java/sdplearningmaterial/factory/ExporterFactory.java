package sdplearningmaterial.factory;

import java.util.ServiceLoader;
import sdplearningmaterial.implementor.Exporter;

public class ExporterFactory {
    public static Exporter create(String format) {
        for (Exporter exporter : ServiceLoader.load(Exporter.class)) {
            if (exporter.format().equalsIgnoreCase(format)) {
                return exporter;
            }
        }

        throw new IllegalArgumentException("Unknown format: " + format);
    }
}