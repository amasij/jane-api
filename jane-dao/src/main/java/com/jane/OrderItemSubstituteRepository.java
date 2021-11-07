package com.jane;

import com.jane.entity.OrderItemSubstitute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemSubstituteRepository extends JpaRepository<OrderItemSubstitute,Long> {
}
