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

    @GetMapping("/{id}")
    @Operation(
            summary = "Получение позиции носков",
            description = "Получение позиции носков по порядковому номеру в списке")
       @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Позиция носков выгружена"),
            @ApiResponse(responseCode = "400",
                    description = "Параметры запроса отсутствуют или имеют некорректный формат"),
            @ApiResponse(responseCode = "500",
                    description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public ResponseEntity<Socks> getSocks(@PathVariable @RequestParam(name = "Порядковый номер") long id) throws ExceptionsApp {
        Socks socks = socksService.getSocks(id);
        return ResponseEntity.ok(socks);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Редактирование позиции носков",
            description = "Редактирование позиции носков по порядковому номеру в списке")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Позиция носков отредактирована"),
            @ApiResponse(responseCode = "400",
                    description = "Параметры запроса отсутствуют или имеют некорректный формат"),
            @ApiResponse(responseCode = "500",
                    description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public ResponseEntity<Socks> editRecipe(@PathVariable @RequestParam(name = "Порядковый номер") long id,
                                            @RequestBody Socks socks) throws ExceptionsApp {
        Socks socks1 = socksService.editSocks(id, socks);
        return ResponseEntity.ok(socks1);
    }

    @DeleteMapping("/{id}")
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
    public ResponseEntity<Void> deleteSocks(@PathVariable @RequestParam(name = "Порядковый номер") long id) throws ExceptionsApp {
        socksService.deleteSocks(id);
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
