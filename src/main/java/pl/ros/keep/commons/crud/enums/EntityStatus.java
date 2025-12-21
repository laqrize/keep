package pl.ros.keep.commons.crud.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author barbara.rosochacka
 * @since 07.04.2024
 */
@AllArgsConstructor
@Getter
public enum EntityStatus {
    DELETED("D"),
    ARCHIVED("A"),
    CURRENT("C");
    private String code;

    public static EntityStatus fromCode(String code) {
        return Arrays.stream(EntityStatus.values())
                .filter(kgStatsEnum -> kgStatsEnum.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown status code: " + code));
    }
}
