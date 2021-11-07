package com.jane.starter;

import com.google.gson.Gson;
import com.jane.CountryRepository;
import com.jane.StateRepository;
import com.jane.dto.CountryRecordDto;
import com.jane.dto.StateRecordDto;
import com.jane.entity.Country;
import com.jane.entity.State;
import com.jane.enumeration.GenericStatusConstant;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MasterRecordLoader {

    private final StateRepository stateRepository;
    private final TransactionTemplate transactionTemplate;
    private final CountryRepository countryRepository;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Gson gson;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        transactionTemplate.execute(tx -> {
            try {
                loadData();
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
            return null;
        });
    }

    private void loadData() throws IOException {
        if (countryRepository.count() == 0) {
            loadCountries();
        }

        if (stateRepository.count() == 0) {
            loadStates();
        }
    }

    private void loadStates() throws IOException {
        try (InputStreamReader reader = new InputStreamReader(new ClassPathResource("records/state.json").getInputStream())) {
            StateRecordDto[] dtoList = gson.fromJson(gson.newJsonReader(reader), StateRecordDto[].class);

            List<State> states = new ArrayList<>();
            for (StateRecordDto stateRecordDto : dtoList) {
                if (!stateRepository.findByCode(stateRecordDto.getCode()).isPresent()) {
                    State state = new State();
                    state.setId(null);
                    state.setStatus(GenericStatusConstant.ACTIVE);
                    state.setCode(stateRecordDto.getCode());
                    state.setName(stateRecordDto.getName());
                    state.setCountryAlpha2(stateRecordDto.getCountryAlpha2());
                    states.add(state);
                }
            }
            stateRepository.saveAll(states);
        }
    }

    private void loadCountries() throws IOException {
        try (InputStreamReader reader = new InputStreamReader(new ClassPathResource("records/country.json").getInputStream())) {
            CountryRecordDto[] dtoList = gson.fromJson(gson.newJsonReader(reader), CountryRecordDto[].class);

            List<Country> countries = new ArrayList<>();
            for (CountryRecordDto countryRecordDto : dtoList) {
                if (!countryRepository.findByAlpha2(countryRecordDto.getAlpha2()).isPresent()) {
                    Country country = new Country();
                    country.setAlpha2(countryRecordDto.getAlpha2());
                    country.setName(countryRecordDto.getName());
                    country.setIsSupported(countryRecordDto.isSupported());
                    country.setAlpha3(countryRecordDto.getAlpha3());
                    countries.add(country);
                }
            }
            countryRepository.saveAll(countries);
        }
    }

}
