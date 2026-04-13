package ru.mephi.vikingdemo.model;

import io.swagger.v3.oas.annotations.media.Schema;
//
@Schema(description = "Предмет снаряжения")
public record EquipmentItem(
        @Schema(description = "Название предмета", example = "Iron Axe")
        String name,
        @Schema(description = "Редкость или качество", example = "Rare")
        String quality
) {
}
