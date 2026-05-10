package ru.mephi.vikingdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.LambdaStatisticsService;
import ru.mephi.vikingdemo.service.VikingService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/vikings")
@Tag(name = "Vikings", description = "Операции с викингами")
public class VikingController {

    private final VikingService vikingService;
    private final LambdaStatisticsService statsService;
    private VikingListener vikingListener;

    public VikingController(VikingService vikingService, LambdaStatisticsService statsService, VikingListener vikingListener) {
        this.vikingService = vikingService;
        this.statsService = statsService;
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

    // лямбда-статистика

    @GetMapping("/stats/count/age/gt/{age}")
    @Operation(summary = "Количество викингов старше указанного возраста")
    public long countAgeGreaterThan(@PathVariable int age) {
        return statsService.countByAgeGreaterThan(age);
    }

    @GetMapping("/stats/count/age/lt/{age}")
    @Operation(summary = "Количество викингов младше указанного возраста")
    public long countAgeLessThan(@PathVariable int age) {
        return statsService.countByAgeLessThan(age);
    }

    @GetMapping("/stats/count/age/between")
    @Operation(summary = "Количество викингов в диапазоне возраста")
    public long countAgeBetween(@RequestParam int min, @RequestParam int max) {
        return statsService.countByAgeBetween(min, max);
    }

    @GetMapping("/stats/count/age/outside")
    @Operation(summary = "Количество викингов вне диапазона возраста")
    public long countAgeOutside(@RequestParam int min, @RequestParam int max) {
        return statsService.countByAgeOutside(min, max);
    }

    @GetMapping("/stats/count/beard-hair")
    @Operation(summary = "Количество викингов с заданной бородой и цветом волос")
    public long countByBeardAndHair(@RequestParam BeardStyle beard, @RequestParam HairColor hair) {
        return statsService.countByBeardStyleAndHairColor(beard, hair);
    }

    @GetMapping("/stats/count/axes/{number}")
    @Operation(summary = "Количество викингов, имеющих ровно указанное число топоров")
    public long countByAxes(@PathVariable int number) {
        return statsService.countByNumberOfAxes(number);
    }

    @GetMapping("/stats/random-tall")
    @Operation(summary = "Случайный викинг ростом выше заданного (по умолчанию 180 см)")
    public ResponseEntity<Viking> getRandomTall(@RequestParam(defaultValue = "180") int minHeight) {
        return statsService.getRandomTallViking(minHeight)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats/legendary")
    @Operation(summary = "Список викингов, имеющих хотя бы один предмет легендарного качества")
    public List<Viking> getLegendary() {
        return statsService.getVikingsWithLegendaryEquipment();
    }

    @GetMapping("/stats/red-bearded-sorted")
    @Operation(summary = "Рыжебородые викинги, отсортированные по возрасту")
    public List<Viking> getRedBeardedSorted() {
        return statsService.getRedBeardedVikingsSortedByAge();
    }

    @GetMapping("/stats/id/max")
    @Operation(summary = "Максимальный ID (последний индекс) в списке викингов")
    public int getMaxId() {
        List<Integer> ids = IntStream.range(0, vikingService.findAll().size())
                .boxed()
                .collect(Collectors.toList());
        return statsService.getMaxId(ids);
    }

    @GetMapping("/stats/id/even")
    @Operation(summary = "Список чётных ID")
    public List<Integer> getEvenIds() {
        List<Integer> ids = IntStream.range(0, vikingService.findAll().size())
                .boxed()
                .collect(Collectors.toList());
        return statsService.getEvenIds(ids);
    }

    @PostMapping("/generate-multiple")
    @Operation(summary = "Массовая генерация случайных викингов")
    public void generateMultiple(@RequestParam int count) {
        vikingService.generateMultipleVikings(count);
        if (vikingListener != null && vikingListener.getGui() != null) {
            vikingListener.getGui().refreshTable(vikingService.findAll());
        }
    }
}