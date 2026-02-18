package com.backendsyndicate.smashclub.common.constant;

import java.util.Map;
import java.util.Set;

public class OrderStatusConstant {
    public static final byte ORDER_FAILED = 0;
    public static final byte ORDER_CANCELLED = 1;
    public static final byte ORDER_PAYMENT_PENDING = 2;
    public static final byte ORDER_PAID = 3;
    public static final byte ORDER_COMPLETED = 4;

    public static boolean isRefundAllowed(byte status) {
        return status == ORDER_PAID;
    }

    private static final Map<Byte, Set<Object>> allowedTransitions =
            Map.of(
                    OrderStatusConstant.ORDER_PAYMENT_PENDING,
                    Set.of(OrderStatusConstant.ORDER_PAID, OrderStatusConstant.ORDER_CANCELLED, OrderStatusConstant.ORDER_FAILED),

                    OrderStatusConstant.ORDER_PAID,
                    Set.of(OrderStatusConstant.ORDER_COMPLETED, OrderStatusConstant.ORDER_CANCELLED),

                    OrderStatusConstant.ORDER_FAILED,
                    Set.of(),

                    OrderStatusConstant.ORDER_CANCELLED,
                    Set.of(),

                    OrderStatusConstant.ORDER_COMPLETED,
                    Set.of()
            );

    public static boolean isValidTransition(
            byte currentStatus,
            byte target
    ) {
        return allowedTransitions
                .getOrDefault(currentStatus, Set.of())
                .contains(target);
    }
}
