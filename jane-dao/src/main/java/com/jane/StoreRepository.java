package com.jane;

import com.jane.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query(" SELECT s FROM Store s WHERE s.code = ?1 AND s.status = 'ACTIVE' ")
    Optional<Store> findActiveByCode(String code);
}
