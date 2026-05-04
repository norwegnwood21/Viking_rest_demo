//practicum3
package ru.mephi.vikingdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingService;

import java.util.List;

@RestController
@RequestMapping("/api/vikings")
@Tag(name = "Vikings", description = "Операции с викингами")
public class VikingController {

    private final VikingService vikingService;
    private VikingListener vikingListener;

    public VikingController(VikingService vikingService, VikingListener vikingListener) {
        this.vikingService = vikingService;
        this.vikingListener = vikingListener;
    }

    @GetMapping
    @Operation(summary = "Получить список созданных викингов")
    public List<Viking> getAllVikings() {
        return vikingService.findAll();
    }

    @PostMapping
    @Operation(summary = "Добавить нового викинга")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Викинг успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public ResponseEntity<Viking> createViking(@RequestBody Viking viking) {
        Viking created = vikingService.addViking(viking);
        if (vikingListener != null && vikingListener.getGui() != null) {
            vikingListener.getGui().refreshTable(vikingService.findAll());
        }
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{index}")
    @Operation(summary = "Удалить викинга по индексу")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Викинг успешно удален"),
            @ApiResponse(responseCode = "404", description = "Викинг не найден")
    })
    public ResponseEntity<Void> deleteViking(@PathVariable int index) {
        boolean deleted = vikingService.deleteViking(index);
        if (deleted && vikingListener != null && vikingListener.getGui() != null) {
            vikingListener.getGui().refreshTable(vikingService.findAll());
        }
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{index}")
    @Operation(summary = "Полностью обновить викинга по индексу")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Викинг успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Викинг не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public ResponseEntity<Viking> updateViking(
            @PathVariable int index,
            @RequestBody Viking viking) {
        Viking updated = vikingService.updateViking(index, viking);
        if (updated != null && vikingListener != null && vikingListener.getGui() != null) {
            vikingListener.getGui().refreshTable(vikingService.findAll());
        }
        return updated != null ? new ResponseEntity<>(updated, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}