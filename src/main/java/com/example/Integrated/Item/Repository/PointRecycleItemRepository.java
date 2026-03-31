package com.example.Integrated.Item.Repository;

import com.example.Integrated.Item.Entity.PointRecycleItem;
import com.example.Integrated.Item.Entity.RecycleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PointRecycleItemRepository extends JpaRepository<PointRecycleItem, Long> {

    @Query("SELECT pri.recycleItem FROM PointRecycleItem pri WHERE pri.point.id = :pointId")
    List<RecycleItem> findRecycleItemsByPointId(@Param("pointId") Long pointId);

}
