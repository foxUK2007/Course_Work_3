package pro.sky.socksstorageapp.services;


import java.io.File;
import java.nio.file.Path;

public interface FileService {

    boolean saveToFile(String json, String dataFileName);

    String readeFromFile(String dataFileName);

    boolean cleanFile(String dataFileName);

    File getDataFile(String dataNameFile);

    Path createTempFile(String suffix);
}

