package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.payment.PaymentCreateResponse;
import org.example.teasystem.dto.payment.PaymentNotifyRequest;
import org.example.teasystem.dto.payment.PaymentVO;
import org.example.teasystem.service.PaymentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 */
@Tag(name = "支付接口")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @Operation(summary = "创建支付单")
    @PostMapping("/{orderId}/create")
    public Result<PaymentCreateResponse> createPayment(Authentication authentication, @PathVariable Long orderId) {
        Long userId = (Long) authentication.getPrincipal();
        PaymentCreateResponse response = paymentService.createPayment(userId, orderId);
        return Result.success(response);
    }
    
    @Operation(summary = "支付回调（Mock）")
    @PostMapping("/{payNo}/notify")
    public Result<Void> notify(@PathVariable String payNo, @RequestBody PaymentNotifyRequest request) {
        paymentService.notify(payNo, request);
        return Result.success();
    }
    
    @Operation(summary = "查询支付信息")
    @GetMapping("/{orderId}")
    public Result<PaymentVO> getPayment(@PathVariable Long orderId) {
        PaymentVO payment = paymentService.getPaymentByOrderId(orderId);
        return Result.success(payment);
    }
}
