package com.backendsyndicate.smashclub.payment.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "transaction")

public class RefundRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "RefundStatus", nullable = false)
    private byte refundStatus = 0;

    @Column(name = "RefundReason", nullable = false)
    private String refundReason = "";

    @Column(name = "RefundNotes", nullable = false)
    private String refundNotes = "";

    @Column(name = "CreatedAt", updatable = false, nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt", insertable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TransactionID", foreignKey = @ForeignKey(name = "fk_refundRequest_to_trans"), nullable = false)
    private Transaction transaction;
}
