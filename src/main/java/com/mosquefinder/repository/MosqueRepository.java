package com.mosquefinder.repository;

import com.mosquefinder.model.Mosque;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.Repository;

public interface MosqueRepository extends MongoRepository<Mosque, String> {

}
