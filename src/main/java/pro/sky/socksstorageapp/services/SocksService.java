package pro.sky.socksstorageapp.services;

import pro.sky.socksstorageapp.exceptions.ExceptionsApp;
import pro.sky.socksstorageapp.model.Socks;
import pro.sky.socksstorageapp.model.SocksColor;
import pro.sky.socksstorageapp.model.SocksSize;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface SocksService {

    int getSocks(SocksSize socksSize, SocksColor socksColor, Integer socksStructure, Integer quantity) throws ExceptionsApp;

    long addSocks(Socks socks) throws ExceptionsApp;

    boolean extraditeSocks(Socks socks) throws ExceptionsApp;

    boolean deleteSocks(SocksSize socksSize, SocksColor socksColor, Integer socksStructure, Integer quantity) throws ExceptionsApp;

    Map<Long, Socks> getAllSocks();

    Path createSocksReport() throws IOException;

}
