package com.example.Integrated.RecycleHistory.Entity;

import com.example.Integrated.Item.Entity.RecycleItem;
import com.example.Integrated.login.Entity.User.User;
import com.example.Integrated.point.Entity.Point;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "recycle_history")
public class RecycleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 거점
    @ManyToOne
    @JoinColumn(name = "collection_point_id", nullable = false)
    private Point collectionPoint;

    // 쓰레기 종류
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private RecycleItem item;

    private int quantity; // 쓰레기 개수

    private int earnedAmount; // 반환 포인트

    private LocalDateTime recycledAt; // 기록 시간

}

