package com.example.Integrated.point.Repository;

import com.example.Integrated.point.Entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Integer> {


}
