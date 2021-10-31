package com.jane;

import com.jane.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    @Query(" SELECT c FROM Customer c JOIN FETCH c.detail WHERE LOWER(c.email) = ?1 AND c.status = 'ACTIVE' ")
    Optional<Customer> findActiveByEmail(String email);

    @Query(" SELECT c FROM Customer c JOIN FETCH c.detail WHERE c.id = ?1 AND c.status = 'ACTIVE' ")
    Optional<Customer> findActiveById(Long id);
}
