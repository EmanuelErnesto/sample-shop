package com.emanueldev.sample_shop.domain.products.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

    @NotEmpty
    @Size(min = 3, max = 100)
    private String name;

    @NotEmpty
    @Size(min = 10, max = 255)
    private String description;

    @NotNull
    @DecimalMin(value = "0.99")
    @DecimalMax(value = "5000.00")
    private BigDecimal price;

    @NotNull
    @PositiveOrZero
    @Max(900)
    private Long stockQuantity;
}
