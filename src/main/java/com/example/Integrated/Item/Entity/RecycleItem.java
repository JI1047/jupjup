package com.example.Integrated.Item.Entity;

import com.example.Integrated.login.Entity.User.Gender;
import com.example.Integrated.login.Entity.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recycle_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecycleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int unitPrice;

    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "recycleItem", cascade = CascadeType.ALL)
    private List<PointRecycleItem> points = new ArrayList<>();




}
