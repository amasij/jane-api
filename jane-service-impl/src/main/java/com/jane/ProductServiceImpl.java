package com.jane;

import com.jane.dto.ProductCreationDto;
import com.jane.entity.*;
import com.jane.enumeration.GenericStatusConstant;
import com.jane.exception.ErrorResponse;
import com.jane.filter.ProductFilter;
import com.jane.pojo.ProductPojo;
import com.jane.pojo.QueryResultsPojo;
import com.jane.qualifier.ProductCodeSequence;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Named
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final ProductStoreRepository productStoreRepository;
    private final ProductImageRepository productImageRepository;
    private final AppRepository appRepository;
    @Autowired
    @ProductCodeSequence
    private SequenceGenerator sequenceGenerator;

    @Transactional
    @Override
    public ProductPojo createProduct(ProductCreationDto dto) {
        Store store = getStore(dto.getStoreCode());
        Product product = new Product();
        product.setCode(sequenceGenerator.getNext());
        product.setDateCreated(LocalDateTime.now());
        product.setStatus(GenericStatusConstant.ACTIVE);
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setPriceInKobo(dto.getPrice().longValue());
        product.setQuantity(dto.getQuantity());
        Product savedProduct = productRepository.save(product);
        createProductStore(savedProduct, store);
        createProductImages(savedProduct, dto.getImageIds());
        return getPojo(savedProduct);
    }


    private Store getStore(String storeCode) {
        return storeRepository.findActiveByCode(storeCode)
                .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, "Cannot find store"));
    }

    @Transactional
    @Override
    public QueryResultsPojo<ProductPojo> search(String storeCode, ProductFilter filter) {
        Store store = getStore(storeCode);
        JPAQuery<ProductStore> jpaQuery = appRepository.startJPAQuery(QProductStore.productStore)
                .innerJoin(QProductStore.productStore.product).fetchJoin()
                .innerJoin(QProductStore.productStore.store).fetchJoin()
                .where(QProductStore.productStore.store.eq(store))
                .where(QProductStore.productStore.product.status.eq(GenericStatusConstant.ACTIVE));
        Optional.ofNullable(filter.getName()).ifPresent(x -> jpaQuery.where(QProductStore.productStore.product.name.containsIgnoreCase(x)));
        Optional.ofNullable(filter.getPriceMin()).ifPresent(x -> jpaQuery.where(QProductStore.productStore.product.price.gt(x)));
        Optional.ofNullable(filter.getPriceMax()).ifPresent(x -> jpaQuery.where(QProductStore.productStore.product.price.loe(x)));
        jpaQuery.limit(Optional.ofNullable(filter.getLimit()).orElse(10L))
                .offset(Optional.ofNullable(filter.getOffset()).orElse(0L));
        QueryResults<ProductStore> results = jpaQuery.fetchResults();
        return new QueryResultsPojo<>(results.getLimit(), results.getOffset(), results.getTotal(), getPojo(results.getResults().stream().map(ProductStore::getProduct).collect(Collectors.toList())));
    }

    private void createProductStore(Product product, Store store) {
        ProductStore productStore = new ProductStore();
        productStore.setStore(store);
        productStore.setProduct(product);
        productStoreRepository.save(productStore);
    }

    private void createProductImages(Product product, List<Long> imageIds) {
        List<Image> images = imageIds.stream().map(x -> appRepository.findById(Image.class, x)
                .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, "Image not found")))
                .collect(Collectors.toList());
        List<ProductImage> productImages = images.stream().map(image -> {
            ProductImage productImage = new ProductImage();
            productImage.setImage(image);
            productImage.setProduct(product);
            productImage.setDateCreated(LocalDateTime.now());
            productImage.setStatus(GenericStatusConstant.ACTIVE);
            return productImage;
        }).collect(Collectors.toList());
        productImageRepository.saveAll(productImages);
    }

    private List<ProductPojo> getPojo(List<Product> products) {
        return products.stream().map(this::getPojo).collect(Collectors.toList());
    }

    private ProductPojo getPojo(Product product) {
        List<ProductImage> productImages = appRepository.startJPAQuery(QProductImage.productImage)
                .where(QProductImage.productImage.product.eq(product))
                .where(QProductImage.productImage.status.eq(GenericStatusConstant.ACTIVE))
                .fetch();
        ProductPojo pojo = new ProductPojo();
        pojo.setName(product.getName());
        pojo.setCode(product.getCode());
        pojo.setDescription(product.getDescription());
        pojo.setPrice(product.getPrice());
        pojo.setImages(productImages.stream().map(x -> x.getImage().getId()).collect(Collectors.toList()));
        return pojo;
    }
}
