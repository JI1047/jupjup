package com.example.Integrated.point.Repository;

import com.example.Integrated.point.Entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Integer> {


    List<Point> findTop18ByOrderByIdAsc();
}
