package com.emanueldev.sample_shop.controllers;

import com.emanueldev.sample_shop.domain.payments.dtos.request.PaymentRequestDTO;
import com.emanueldev.sample_shop.domain.payments.dtos.response.PaginatedPaymentResponseDTO;
import com.emanueldev.sample_shop.domain.payments.dtos.response.PaymentResponseDTO;
import com.emanueldev.sample_shop.domain.payments.mappers.PaymentMapper;
import com.emanueldev.sample_shop.exceptions.ApplicationException;
import com.emanueldev.sample_shop.models.Payment;
import com.emanueldev.sample_shop.services.payments.CreatePaymentUseCase;
import com.emanueldev.sample_shop.services.payments.GetAllPaymentsUseCase;
import com.emanueldev.sample_shop.services.payments.GetPaymentByIdUseCase;
import com.emanueldev.sample_shop.services.payments.UpdatePaymentUseCase;
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

@RestController("/payments")
@Tag(name = "Payments", description = "Endpoints for Payment CRUD operations like Create Payment, Get Paginated Payments and Update Payment")
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final GetPaymentByIdUseCase getPaymentByIdUseCase;
    private final GetAllPaymentsUseCase getAllPaymentsUseCase;
    private final UpdatePaymentUseCase updatePaymentUseCase;
    private final PaymentMapper paymentMapper;

    public PaymentController(
            final CreatePaymentUseCase createPaymentUseCase,
            final GetPaymentByIdUseCase getPaymentByIdUseCase,
            final GetAllPaymentsUseCase getAllPaymentsUseCase,
            final UpdatePaymentUseCase updatePaymentUseCase,
            final PaymentMapper paymentMapper
    ) {
        this.createPaymentUseCase = createPaymentUseCase;
        this.getPaymentByIdUseCase = getPaymentByIdUseCase;
        this.getAllPaymentsUseCase = getAllPaymentsUseCase;
        this.updatePaymentUseCase = updatePaymentUseCase;
        this.paymentMapper = paymentMapper;
    }

    @Operation(summary = "Create a new payment", description = "Resource for create a new payment",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Payment created successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Payment.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid field inserted.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "404", description = "Payment Not Found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class)))
            })

    @PostMapping
    public ResponseEntity<Payment> create(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
        Payment payment = createPaymentUseCase.execute(paymentRequestDTO.getOrderId());

        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @Operation(summary = "Show a existent payment", description = "Resource that return a existent payment",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payment returned successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Payment.class))),
                    @ApiResponse(responseCode = "404", description = "Payment not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid id",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))
                    ),
            })
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable("id") UUID paymentId) {
        Payment payment = getPaymentByIdUseCase.execute(paymentId);

        PaymentResponseDTO response = paymentMapper.mappingFromPaymentEntityToPaymentResponseDTO(payment);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Return a list of paginated payments", description = "Resource that return a list of payments",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payments returned successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedPaymentResponseDTO.class))),
            })
    @GetMapping()
    public ResponseEntity<PaginatedPaymentResponseDTO> getAllPayments(
            @RequestParam(defaultValue = "0") @PositiveOrZero final Integer pageNumber,
            @RequestParam(defaultValue = "5") @PositiveOrZero final Integer pageSize
    ) {
        Page<Payment> paymentPage = getAllPaymentsUseCase.execute(pageNumber, pageSize);

        PaginatedPaymentResponseDTO response = paymentMapper.mappingFromPaymentPageToPaginatedPaymentDTO(paymentPage);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update an existent payment", description = "Resource that updates Payment Status of an existent Payment.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Payment updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = void.class))),
                    @ApiResponse(responseCode = "404", description = "Payment not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "400", description = "Payment already made",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
                    @ApiResponse(responseCode = "422", description = "Invalid id inserted",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationException.class))),
            })
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePaymentStatus(@PathVariable("id") UUID paymentId) {
        updatePaymentUseCase.execute(paymentId);

        return ResponseEntity.noContent().build();
    }

}
