package com.jane;

import com.jane.dto.ProductCreationDto;
import com.jane.entity.*;
import com.jane.enumeration.GenericStatusConstant;
import com.jane.exception.ErrorResponse;
import com.jane.pojo.ProductPojo;
import com.jane.qualifier.ProductCodeSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.List;
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
        Store store = storeRepository.findActiveByCode(dto.getStoreCode())
                .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, "Cannot find store"));
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

    private void createProductStore(Product product, Store store) {
        ProductStore productStore = new ProductStore();
        productStore.setStore(store);
        productStore.setProduct(product);
        productStoreRepository.save(productStore);
    }

    private void createProductImages(Product product, List<Long> imageIds) {
        List<Image> images = appRepository.findByIds(Image.class, imageIds);
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
        pojo.setImages(productImages.stream().map(x-> x.getImage().getId()).collect(Collectors.toList()));
        return pojo;
    }
}
