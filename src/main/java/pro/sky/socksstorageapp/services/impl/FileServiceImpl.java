package pro.sky.socksstorageapp.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.socksstorageapp.services.FileService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileServiceImpl implements FileService {

    @Value("${path.to.data.file}")
    private String dataFilePath;

    @Value("${name.of.socks.data.file}")
    private String dataFileName;

    @Override
    public boolean saveToFile(String json) {
        Path path = Path.of(dataFilePath, dataFileName);
        try {
            cleanFile();
            Files.writeString(path, json);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String readeFromFile() {
        Path path = Path.of(dataFilePath, dataFileName);
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean cleanFile() {
        try {
            Files.deleteIfExists(Path.of(dataFilePath, dataFileName));
            Files.createFile(Path.of(dataFilePath, dataFileName));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public File getDataFile() {
        return new File(dataFilePath + "/" + dataFileName);

    }

    @Override
    public Path createTempFile(String suffix) {
        try {
            return Files.createTempFile(Path.of(dataFilePath), "temp", suffix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}