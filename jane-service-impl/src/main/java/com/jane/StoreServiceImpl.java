package com.jane;

import com.jane.dto.StoreCreationDto;
import com.jane.entity.*;
import com.jane.enumeration.GenericStatusConstant;
import com.jane.exception.ErrorResponse;
import com.jane.filter.StoreFilter;
import com.jane.pojo.QueryResultsPojo;
import com.jane.pojo.StoreCreationPojo;
import com.jane.pojo.StorePojo;
import com.jane.qualifier.StoreCodeSequence;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Named
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final ImageRepository imageRepository;
    private final AddressRepository addressRepository;
    private final StateRepository stateRepository;
    private final GpsRepository gpsRepository;
    private final AppRepository appRepository;
    private final StoreScheduleRepository storeScheduleRepository;
    @Autowired
    @StoreCodeSequence
    private SequenceGenerator sequenceGenerator;


    @Transactional
    @Override
    public List<StorePojo> createStore(StoreCreationDto dto) {
        Store store = new Store();
        store.setCode(sequenceGenerator.getNext());
        store.setName(dto.getName());
        store.setAddress(createAddress(dto));
        store.setDateCreated(LocalDateTime.now());
        store.setStatus(GenericStatusConstant.ACTIVE);
        store.setImage(imageRepository.findById(dto.getLogoId())
                .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, "Logo not found")));
        Store savedStore = storeRepository.save(store);
        createStoreSchedule(savedStore, dto);
        return transform(Collections.singletonList(savedStore));
    }

    private void createStoreSchedule(Store store, StoreCreationDto dto) {
        List<StoreSchedule> storeSchedules = dto.getSchedules().stream().map(x -> {
            StoreSchedule storeSchedule = new StoreSchedule();
            storeSchedule.setStore(store);
            storeSchedule.setStatus(GenericStatusConstant.ACTIVE);
            storeSchedule.setClose(x.getClose());
            storeSchedule.setOpen(x.getOpen());
            storeSchedule.setDay(x.getDay());
            return storeSchedule;
        }).collect(Collectors.toList());
        storeScheduleRepository.saveAll(storeSchedules);
    }

    @Transactional
    @Override
    public QueryResultsPojo<StorePojo> getStores(StoreFilter filter) {
        JPAQuery<Store> jpaQuery = appRepository.startJPAQuery(QStore.store)
                .innerJoin(QStore.store.address).fetchJoin()
                .where(QStore.store.status.eq(GenericStatusConstant.ACTIVE));
        Optional.ofNullable(filter.getName()).ifPresent(x -> jpaQuery.where(QStore.store.name.containsIgnoreCase(x)));
        Optional.ofNullable(filter.getStateCode()).ifPresent(x -> jpaQuery.where(QStore.store.address.state.code.eq(x)));
        jpaQuery.limit(Optional.ofNullable(filter.getLimit()).orElse(10L))
                .offset(Optional.ofNullable(filter.getOffset()).orElse(0L))
                .orderBy(QStore.store.name.asc());
        QueryResults<Store> results = jpaQuery.fetchResults();
        return new QueryResultsPojo<>(results.getLimit(), results.getOffset(), results.getTotal(), transform(results.getResults()));

    }

    private List<StorePojo> transform(List<Store> stores) {
        List<State> states = appRepository.startJPAQuery(QState.state)
                .where(QState.state.id.in(stores.stream().map(x -> x.getAddress().getState().getId())
                        .collect(Collectors.toList()))).fetch();

        List<Gps> gpsList = appRepository.startJPAQuery(QGps.gps)
                .where(QGps.gps.id.in(stores.stream().map(x -> x.getAddress().getGps().getId())
                        .collect(Collectors.toList()))).fetch();

        return stores.stream().map(x -> {
            Gps gps = gpsList.stream().filter(g -> g.getId().equals(x.getAddress().getGps().getId())).findFirst().orElse(null);
            State state = states.stream().filter(s -> s.getId().equals(x.getAddress().getState().getId())).findFirst().orElse(null);
            StorePojo pojo = new StorePojo();
            pojo.setName(x.getName());
            pojo.setCode(x.getCode());
            pojo.setGps(gps);
            pojo.setState(state);
            pojo.setLogoId(x.getImage().getId());
            return pojo;
        }).collect(Collectors.toList());
    }

    private Address createAddress(StoreCreationDto dto) {
        Address address = new Address();
        address.setDateCreated(LocalDateTime.now());
        address.setStatus(GenericStatusConstant.ACTIVE);
        address.setGps(createGps(dto));
        address.setState(stateRepository.findStateByCode(dto.getStateCode())
                .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, "State not found")));
        return addressRepository.save(address);
    }

    private Gps createGps(StoreCreationDto dto) {
        Gps gps = new Gps();
        gps.setLatitude(dto.getLatitude());
        gps.setLongitude(dto.getLongitude());
        return gpsRepository.save(gps);
    }


}
