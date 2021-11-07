package com.jane.sequence;

import com.jane.SequenceGeneratorImpl;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.util.Locale;

@com.jane.qualifier.ProductCodeSequence
@Named
public class ProductCodeSequence extends SequenceGeneratorImpl {

    @Inject
    public ProductCodeSequence(EntityManager entityManager, TransactionTemplate transactionTemplate) {
        super(entityManager, transactionTemplate, "product_code");
    }

    @Override
    public String getNext() {
        return String.format(Locale.ENGLISH, "PR%09d", getNextLong());
    }
}
