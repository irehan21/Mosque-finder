package com.mosquefinder.repository;

import com.mosquefinder.model.Mosque;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface MosqueRepository extends MongoRepository<Mosque, String> {
    List<Mosque> findByLocationNear(Point location, Distance distance);
}
