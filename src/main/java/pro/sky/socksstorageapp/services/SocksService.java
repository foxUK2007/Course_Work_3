package pro.sky.socksstorageapp.services;

import pro.sky.socksstorageapp.exceptions.ExceptionsApp;
import pro.sky.socksstorageapp.model.Socks;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface SocksService {

    Socks getSocks(long id) throws ExceptionsApp;

    long addSocks(Socks socks) throws ExceptionsApp;

    Socks editSocks(long id, Socks socks) throws ExceptionsApp;

    boolean deleteSocks(long id) throws ExceptionsApp;

    Map<Long, Socks> getAllSocks();

    Path createSocksReport() throws IOException;

}
