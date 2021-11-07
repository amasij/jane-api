package com.jane;

import com.jane.entity.PaymentInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentInvoiceRepository extends JpaRepository<PaymentInvoice,Long> {
}
