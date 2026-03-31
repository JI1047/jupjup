package com.example.Integrated.RecycleHistory.Entity;

import com.example.Integrated.Item.Entity.RecycleItem;
import com.example.Integrated.login.Entity.User.User;
import com.example.Integrated.point.Entity.Point;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "recycle_claim", indexes = {
        @Index(name = "idx_recycle_claim_expires_at", columnList = "expiresAt")
})
public class RecycleClaim {

    public enum Status { PENDING, USED, CANCELLED, EXPIRED }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Hibernate 6+
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "collection_point_id", nullable = false)
    private Point collectionPoint;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private RecycleItem item;

    private int quantity;

    // 예상 적립 포인트(확정 시 재검증)
    private int expectedAmount;

    // 특정 사용자로 고정하고 싶을 때 (null이면 스캔한 로그인 사용자로 확정)
    @ManyToOne
    @JoinColumn(name = "intended_user_id")
    private User intendedUser;

    @Enumerated(EnumType.STRING)
    private Status status;

    // QR 만료 시각(예: 생성 후 5분)
    private LocalDateTime expiresAt;

    // 변조 방지용 서명(HMAC 결과)
    @Column(length = 128)
    private String signature;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
