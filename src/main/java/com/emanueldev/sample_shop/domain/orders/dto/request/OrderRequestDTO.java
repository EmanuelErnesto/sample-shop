package com.emanueldev.sample_shop.domain.orders.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequestDTO {

    @NotEmpty
    @Size(min = 1)
    @Valid
    private List<OrderItemRequestDTO> items;
}
