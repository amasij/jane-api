package com.jane;

import com.jane.entity.DeductibleHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeductibleHashRepository extends JpaRepository<DeductibleHash, Long> {

    Optional<DeductibleHash> findByToken(String token);
}
