package com.emanueldev.sample_shop.controllers;

import com.emanueldev.sample_shop.domain.orders.dto.response.PaginatedOrderResponseDTO;
import com.emanueldev.sample_shop.domain.orders.mappers.OrderMapper;
import com.emanueldev.sample_shop.exceptions.ApplicationException;
import com.emanueldev.sample_shop.domain.orders.dto.request.OrderRequestDTO;
import com.emanueldev.sample_shop.models.OrderModel;
import com.emanueldev.sample_shop.services.orders.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Orders", description = "Endpoints for Order CRUD operations like Create Order, Get Paginated Orders, Update Order and Delete Order")
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase;
    private final GetOrdersUseCase getOrdersUseCase;
    private final OrderMapper orderMapper;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    public OrderController(
            final CreateOrderUseCase createOrderUseCase,
            final GetOrderByIdUseCase getOrderByIdUseCase,
            final DeleteOrderUseCase deleteOrderUseCase,
            final GetOrdersUseCase getOrdersUseCase,
            final UpdateOrderStatusUseCase updateOrderStatusUseCase,
            final OrderMapper orderMapper) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.deleteOrderUseCase = deleteOrderUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
        this.getOrdersUseCase = getOrdersUseCase;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    @Operation(summary = "Create a new order", description = "Resource for create a new order",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Order created successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderModel.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid field inserted.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "404", description = "Order Not Found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))
            })
    public ResponseEntity<OrderModel> create(@RequestBody @Valid OrderRequestDTO orderRequestDTO) {
        OrderModel response = createOrderUseCase.execute(orderRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Return a list of paginated orders", description = "Resource that return a list of orders",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders returned successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedOrderResponseDTO.class))),
            })
    @GetMapping
    public ResponseEntity<PaginatedOrderResponseDTO> getAll(
            @RequestParam(defaultValue = "0") @PositiveOrZero final Integer pageNumber,
            @RequestParam(defaultValue = "5") @PositiveOrZero final Integer pageSize
    ) {
        Page<OrderModel> orderPage = getOrdersUseCase.execute(pageNumber, pageSize);

        PaginatedOrderResponseDTO response = orderMapper
                .mappingFromOrderPageToPaginatedOrderDTO(orderPage);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Show an existent order", description = "Resource that return a existent order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order returned successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderModel.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid id",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))
                    ),
            })
    @GetMapping("/{id}")
    public ResponseEntity<OrderModel> getById(@PathVariable("id") UUID id) {
        OrderModel order = getOrderByIdUseCase.execute(id);

        return ResponseEntity.ok(order);
    }


    @Operation(summary = "Delete a existent order", description = "Resource that delete a existent order",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Order deleted successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = void.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid id inserted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") UUID id
    ){
        deleteOrderUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update an existent order", description = "Resource that updates OrderStatus of an existent order",
    responses = {
            @ApiResponse(responseCode = "204", description = "Order updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = void.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
            @ApiResponse(responseCode = "422", description = "Invalid id inserted",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
    })
    @PatchMapping("{id}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable("id") UUID id) {
        updateOrderStatusUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }

}
