package com.backendsyndicate.smashclub.common.constant;

import java.util.Map;
import java.util.Set;

public class OrderStatusConstant {
    public static final byte ORDER_CANCELLED = 0;
    public static final byte ORDER_PAYMENT_PENDING = 1;
    public static final byte ORDER_PROCESSING = 2;
    public static final byte ORDER_READY_FOR_PICKUP = 3;
    public static final byte ORDER_COMPLETED = 4;

    public static boolean isRefundAllowed(byte status) {
        return status > ORDER_PAYMENT_PENDING;
    }

    private static final Map<Byte, Set<Object>> allowedTransitions =
            Map.of(
                    OrderStatusConstant.ORDER_PAYMENT_PENDING,
                    Set.of(OrderStatusConstant.ORDER_PROCESSING),

                    OrderStatusConstant.ORDER_PROCESSING, Set.of(OrderStatusConstant.ORDER_READY_FOR_PICKUP, OrderStatusConstant.ORDER_CANCELLED),

                    OrderStatusConstant.ORDER_READY_FOR_PICKUP,
                    Set.of(OrderStatusConstant.ORDER_COMPLETED, OrderStatusConstant.ORDER_CANCELLED),

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
