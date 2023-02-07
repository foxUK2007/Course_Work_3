package pro.sky.socksstorageapp.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.socksstorageapp.exceptions.ExceptionsApp;
import pro.sky.socksstorageapp.model.Socks;
import pro.sky.socksstorageapp.services.FileService;
import pro.sky.socksstorageapp.services.SocksService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.TreeMap;

@Service
public class SocksServiceImpl implements SocksService {

    @Value("${name.of.socks.data.file}")
    private String dataFileName;

    private final FileService fileService;

    public static long id = 1;

    private Map<Long, Socks> listSocks = new TreeMap<>();

    public SocksServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @PostConstruct
    private void init() {
        readFromFile();
    }

    @Override
    public Socks getSocks(long id) throws ExceptionsApp {
        if (listSocks.containsKey(id)) {
            return listSocks.get(id);
        } else {
            throw new ExceptionsApp("Такой позиции носков не существует");
        }
    }

    @Override
    public long addSocks(Socks socks) throws ExceptionsApp {
        if (!listSocks.containsValue(socks)) {
            listSocks.put(id, socks);
            saveToFile();
            return id++;
        } else {
            throw new ExceptionsApp("Такая позиция носков есть в списке");
        }
    }

    @Override
    public Socks editSocks(long id, Socks socks) throws ExceptionsApp {
        if (listSocks.containsKey(id)) {
            listSocks.put(id, socks);
            saveToFile();
            return socks;
        } else {
            throw new ExceptionsApp("Такая позиция носков не найдена");
        }
    }

    @Override
    public boolean deleteSocks(long id) throws ExceptionsApp {
        if (listSocks.containsKey(id)) {
            listSocks.remove(id);
            saveToFile();
            return true;
        } else {
            throw new ExceptionsApp("Такая позиция носков не найдена");
        }
    }

    @Override
    public Map<Long, Socks> getAllSocks() {
        return listSocks;
    }

    private void saveToFile() {
        try {
            String json = new ObjectMapper().writeValueAsString(listSocks);
            fileService.saveToFile(json, dataFileName);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сохранения файла");
        }

    }

    private void readFromFile() {
        String json = fileService.readeFromFile(dataFileName);
        try {
            listSocks = new ObjectMapper().readValue(json, new TypeReference<TreeMap<Long, Socks>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка чтения файла");
        }
    }

    @Override
    public Path createSocksReport() throws IOException {
        Path path = fileService.createTempFile("socksReport");
        for (Socks socks : listSocks.values()) {
            try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
                writer.append("Размер носков: " + socks.getSocksSize().getMinSize() + "-" + socks.getSocksSize().getMaxSize() + "\r\n")
                        .append("Цвет носков: " + socks.getSocksColor().getColor() + "\r\n")
                        .append("Содержание хлопка " + socks.getSocksStructure() + " % " + "\r\n")
                        .append("Количество: " + socks.getQuantity() + " шт.");
                writer.append("\r\n");
            }
        }
        return path;
    }
}

