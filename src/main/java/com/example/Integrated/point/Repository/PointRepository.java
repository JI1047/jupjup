package com.example.Integrated.point.Repository;

import com.example.Integrated.point.Entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {


    @Query("""
    SELECT DISTINCT p
    FROM Point p
    JOIN FETCH p.pointAddress
    JOIN FETCH p.recycleItems pri
    JOIN FETCH pri.recycleItem
    ORDER BY p.id ASC
    """)
    List<Point> findTop165WithAllFetch();

    Point findByName(String name);
    @Query(value = """
        SELECT p.id
        FROM point p
        JOIN pointrecycle_item pri ON p.id = pri.point_id
        WHERE pri.recycle_item_id IN (:itemIds)
        GROUP BY p.id
        HAVING COUNT(DISTINCT pri.recycle_item_id) = :size
    """, nativeQuery = true)
    List<Long> findPointIdsThatCollectAllItems(
            @Param("itemIds") List<Long> itemIds,
            @Param("size") int size
    );
}
