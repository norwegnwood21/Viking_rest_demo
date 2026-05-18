package ru.mephi.vikingdemo.service;

import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.EquipmentItem;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Arrays;
@Service
public class LambdaStatisticsService {

    private final VikingService vikingService;
    private final Random random = new Random();

    public LambdaStatisticsService(VikingService vikingService) {
        this.vikingService = vikingService;
    }

    //  Оценка объёма выборки по возрасту
    public long countByAgeGreaterThan(int age) {
        return vikingService.findAll().stream()
                .filter(v -> v.age() > age)
                .count();
    }

    public long countByAgeLessThan(int age) {
        return vikingService.findAll().stream()
                .filter(v -> v.age() < age)
                .count();
    }

    public long countByAgeBetween(int minAge, int maxAge) {
        return vikingService.findAll().stream()
                .filter(v -> v.age() >= minAge && v.age() <= maxAge)
                .count();
    }

    public long countByAgeOutside(int minAge, int maxAge) {
        return vikingService.findAll().stream()
                .filter(v -> v.age() < minAge || v.age() > maxAge)
                .count();
    }

    // Одновременное условие: форма бороды И цвет волос
    public long countByBeardStyleAndHairColor(BeardStyle beard, HairColor hair) {
        return vikingService.findAll().stream()
                .filter(v -> v.beardStyle() == beard && v.hairColor() == hair)
                .count();
    }

    // Подсчёт викингов, имеющих 1 ИЛИ 2 топора
    public long countVikingsWithOneOrTwoAxes() {
        return vikingService.findAll().stream()
                .filter(v -> {
                    long count = v.equipment().stream()
                            .filter(item -> item.name().equalsIgnoreCase("Axe"))
                            .count();
                    return count == 1 || count == 2;
                })
                .count();
    }
    // Случайный викинг ростом выше 180 см
    public Optional<Viking> getRandomTallViking(int minHeight) {
        List<Viking> tall = vikingService.findAll().stream()
                .filter(v -> v.heightCm() > minHeight)
                .collect(Collectors.toList());
        if (tall.isEmpty()) return Optional.empty();
        return Optional.of(tall.get(random.nextInt(tall.size())));
    }

    //  Все викинги с легендарным снаряжением (хотя бы один предмет Legendary)
    public List<Viking> getVikingsWithLegendaryEquipment() {
        return vikingService.findAll().stream()
                .filter(v -> v.equipment().stream().anyMatch(item -> "Legendary".equals(item.quality())))
                .collect(Collectors.toList());
    }

    // Список рыжебородых викингов отсортированных
    public List<Viking> getRedBeardedVikingsSortedByAge() {
        return vikingService.findAll().stream()
                .filter(v -> v.beardStyle() != BeardStyle.CLEAN_SHAVEN)
                .filter(v -> v.hairColor() == HairColor.Red)
                .sorted((v1, v2) -> Integer.compare(v1.age(), v2.age()))
                .collect(Collectors.toList());
    }

    // Максимальный ID
    public Integer getMaxId(Integer[] ids) {
        return Arrays.stream(ids)
                .max(Integer::compareTo)
                .orElse(-1);
    }

    // Все чётные ID
    public List<Integer> getEvenIds(Integer[] ids) {
        return Arrays.stream(ids)
                .filter(id -> id % 2 == 0)
                .collect(Collectors.toList());
    }
}