package com.jane.sequence;

import com.jane.SequenceGeneratorImpl;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.util.Locale;

@com.jane.qualifier.StoreCodeSequence
@Named
public class StoreCodeSequence extends SequenceGeneratorImpl {

    @Inject
    public StoreCodeSequence(EntityManager entityManager, TransactionTemplate transactionTemplate) {
        super(entityManager, transactionTemplate, "store_code");
    }

    @Override
    public String getNext() {
        return String.format(Locale.ENGLISH, "ST%09d", getNextLong());
    }
}
