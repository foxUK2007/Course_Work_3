package pro.sky.socksstorageapp.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pro.sky.socksstorageapp.exceptions.ExceptionsApp;
import pro.sky.socksstorageapp.model.Socks;
import pro.sky.socksstorageapp.model.SocksColor;
import pro.sky.socksstorageapp.model.SocksSize;
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

    private final FileService fileService;

    public static long id = 1;

    private Map<Long, Socks> mapSocks = new TreeMap<>();

    public SocksServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @PostConstruct
    private void init() {
        readFromFile();
    }

    @Override
    public int getSocks(SocksSize socksSize, SocksColor socksColor, Integer socksStructure, Integer quantity) throws ExceptionsApp {
        int count = 0;
        for (Map.Entry<Long, Socks> entry : mapSocks.entrySet()) {
            if (socksSize != null && !entry.getValue().getSocksSize().equals(socksSize)) {
                continue;
            }
            if (socksColor != null && !entry.getValue().getSocksColor().equals(socksColor)) {
                continue;
            }
            if (socksStructure != 0) {
                continue;
            }
            if (quantity != 0) {
                continue;
            }
            count += entry.getValue().getQuantity();
        }
        return count;
    }

    @Override
    public long addSocks(Socks socks) throws ExceptionsApp {
        if (!mapSocks.isEmpty() && mapSocks.containsValue(socks)) {
            for (Map.Entry<Long, Socks> entry : mapSocks.entrySet()) {
                if (entry.getValue().equals(socks)) {
                    long key = entry.getKey();
                    int oldQuantity = entry.getValue().getQuantity();
                    int newQuantity = oldQuantity + socks.getQuantity();
                    Socks socksNew = new Socks(socks.getSocksSize(), socks.getSocksColor(), socks.getSocksStructure(),
                            newQuantity);
                    mapSocks.put(key, socksNew);
                    saveToFile();
                    return key;
                }
            }
        } else {
            mapSocks.put(id, socks);
            saveToFile();
        }
        return id++;
    }


    @Override
    public boolean extraditeSocks(Socks socks) throws ExceptionsApp {
        if (isValueMapSocks(socks)) {
            saveToFile();
            return true;
        } else {
            throw new ExceptionsApp("Такая позиция носков не найдена");
        }
    }

    private boolean isValueMapSocks(Socks socks) {
        if (!mapSocks.isEmpty() && mapSocks.containsValue(socks)) {
            for (Map.Entry<Long, Socks> entry : mapSocks.entrySet()) {
                if (entry.getValue().equals(socks)) {
                    long key = entry.getKey();
                    int oldQuantity = entry.getValue().getQuantity();
                    int newQuantity = socks.getQuantity();
                    if (oldQuantity >= newQuantity) {
                        int quantity = oldQuantity - newQuantity;
                        Socks socksNew = new Socks(socks.getSocksSize(), socks.getSocksColor(), socks.getSocksStructure(),
                                quantity);
                        mapSocks.put(key, socksNew);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean deleteSocks(SocksSize socksSize, SocksColor socksColor, Integer socksStructure, Integer quantity) throws ExceptionsApp {
        Socks socks = new Socks(socksSize, socksColor, socksStructure, quantity);
        if (mapSocks.containsKey(socks)) {
            mapSocks.remove(socks);
            saveToFile();
            return true;
        } else {
            throw new ExceptionsApp("Позиция носков удалена");
        }
    }

    private void saveToFile() {
        try {
            String json = new ObjectMapper().writeValueAsString(mapSocks);
            fileService.saveToFile(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сохранения файла");
        }

    }

    private void readFromFile() {
        String json = fileService.readeFromFile();
        try {
            mapSocks = new ObjectMapper().readValue(json, new TypeReference<TreeMap<Long, Socks>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка чтения файла");
        }
    }

    @Override
    public Map<Long, Socks> getMapSocks() {
        return Map.copyOf(mapSocks);
    }

    @Override
    public Path createSocksReport() throws IOException {
        Path path = fileService.createTempFile("socksReport");
        for (Socks socks : mapSocks.values()) {
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

