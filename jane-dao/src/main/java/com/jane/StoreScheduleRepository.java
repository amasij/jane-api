package com.jane;

import com.jane.entity.StoreSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreScheduleRepository extends JpaRepository<StoreSchedule,Long> {
}
