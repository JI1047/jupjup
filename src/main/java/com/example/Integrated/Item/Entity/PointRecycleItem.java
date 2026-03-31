package com.example.Integrated.Item.Entity;

import com.example.Integrated.point.Entity.Point;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Table(name = "pointrecycle_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PointRecycleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 거점 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "point_id", nullable = false)
    private Point point;

    // ✅ 수거 품목 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "recycle_item_id", nullable = false)
    private RecycleItem recycleItem;
}
