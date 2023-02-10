package pro.sky.socksstorageapp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.socksstorageapp.exceptions.ExceptionsApp;
import pro.sky.socksstorageapp.model.Socks;
import pro.sky.socksstorageapp.model.SocksColor;
import pro.sky.socksstorageapp.model.SocksSize;
import pro.sky.socksstorageapp.services.SocksService;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/socks")
@Tag(name = "Носки", description = "Выполнение действий по добавлению новых позиций носков в список, изменение содержания позиций носков, просмотр списка позиций носков и удаление позиций носков")
public class SocksController {

    private final SocksService socksService;

    public SocksController(SocksService socksService) {
        this.socksService = socksService;
    }

    @PostMapping()
    @Operation(
            summary = "Создание позиции носков",
            description = "Заполняется в формате JSON")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Позиция носков добавлена"),
            @ApiResponse(responseCode = "400",
                    description = "Параметры запроса отсутствуют или имеют некорректный формат"),
            @ApiResponse(responseCode = "500",
                    description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public ResponseEntity<Long> addSocks(@Valid @RequestBody Socks socks) throws ExceptionsApp {
        long id = socksService.addSocks(socks);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{socks}")
    @Operation(
            summary = "Получение позиции носков",
            description = "Для получения позиции носков необходимо указать размер, цвет, содержание хлопка и количество")
       @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Позиция носков выгружена"),
            @ApiResponse(responseCode = "400",
                    description = "Параметры запроса отсутствуют или имеют некорректный формат"),
            @ApiResponse(responseCode = "500",
                    description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public ResponseEntity<Integer> getSocksQuantity(@RequestParam(name = "Размер")SocksSize socksSize,
                                            @RequestParam(name = "Цвет")SocksColor socksColor,
                                            @RequestParam(name = "Содержание хлопка") Integer socksStructure,
                                            @RequestParam(name = "Количество") Integer quantity) throws ExceptionsApp {
        Integer sumSocks = socksService.getSocks(socksSize, socksColor, socksStructure,
                quantity);
        if (quantity != 0) {
            return ResponseEntity.ok().body(sumSocks);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping()
    @Operation(
            summary = "Выдача позиции носков",
            description = "Выдача позиции носков из списка")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Позиция носков отредактирована"),
            @ApiResponse(responseCode = "400",
                    description = "Параметры запроса отсутствуют или имеют некорректный формат"),
            @ApiResponse(responseCode = "500",
                    description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public ResponseEntity<Boolean> extraditeSocks(@Valid @RequestBody Socks socks) throws ExceptionsApp {
        socksService.extraditeSocks(socks);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    @Operation(
            summary = "Удаление позиции носков",
            description = "Удаление позиции носков по порядковому номеру из списка")
       @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Позиция носков удалена"),
            @ApiResponse(responseCode = "400",
                    description = "Параметры запроса отсутствуют или имеют некорректный формат"),
            @ApiResponse(responseCode = "500",
                    description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public ResponseEntity<Boolean> deleteSocks(@RequestParam(name = "Размер")SocksSize socksSize,
                                            @RequestParam(name = "Цвет")SocksColor socksColor,
                                            @RequestParam(name = "Содержание хлопка") Integer socksStructure,
                                            @RequestParam(name = "Количество") Integer quantity) throws ExceptionsApp {
        socksService.deleteSocks(socksSize, socksColor, socksStructure, quantity);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    @Operation(
            summary = "Получение всего списка позиций носков",
            description = "Данных не требуется")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Полный список позиций носков выгружен"),
            @ApiResponse(responseCode = "400",
                    description = "Параметры запроса отсутствуют или имеют некорректный формат"),
            @ApiResponse(responseCode = "500",
                    description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public ResponseEntity<Map<Long, Socks>> getAllSocks() {
        Map<Long, Socks> socksMap = socksService.getAllSocks();
        if (socksMap != null) {
            return ResponseEntity.ok(socksMap);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/export")
    @Operation(
            summary = "Экспорт текстового списка позиций носков",
            description = "Данных не требуется")
    public ResponseEntity<Object> exportTxt() {
        try {
            Path socksInTxt = socksService.createSocksReport();
            if (Files.size(socksInTxt) == 0) {
                return ResponseEntity.noContent().build();
            }
            InputStreamResource resource = new InputStreamResource(
                    new FileInputStream(socksInTxt.toFile()));
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
                    .contentLength(Files.size(socksInTxt))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=" + "socks.txt")
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.toString());
        }
    }
}
