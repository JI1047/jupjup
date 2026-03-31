package com.example.Integrated.Item.Repository;

import com.example.Integrated.Item.Entity.RecycleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecycleItemRepository extends JpaRepository<RecycleItem, Long> {

    RecycleItem findByName(String name);
}
