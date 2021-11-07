package com.jane;

import com.jane.entity.ProductStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductStoreRepository extends JpaRepository<ProductStore,Long> {
}
