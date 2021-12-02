package com.jane;

import com.jane.dto.DeliveryCostEstimateDto;
import com.jane.dto.OrderCreationDto;
import com.jane.dto.ProductQuantityDto;
import com.jane.entity.*;
import com.jane.enumeration.GenericStatusConstant;
import com.jane.enumeration.OrderStatusConstant;
import com.jane.enumeration.PaymentStatusConstant;
import com.jane.exception.ErrorResponse;
import com.jane.pojo.DeliveryCostEstimatePojo;
import com.jane.pojo.OrderCreationPojo;
import com.jane.pojo.ProductPojo;
import com.jane.pojo.ProductSubPojo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Named
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CustomerRepository customerRepository;
    private final DeductibleHashRepository deductibleHashRepository;
    private final AppRepository appRepository;
    private final PaymentInvoiceRepository paymentInvoiceRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final StateRepository stateRepository;
    private final AddressRepository addressRepository;
    private final GpsRepository gpsRepository;
    private final PhoneNumberService phoneNumberService;
    private final OrderItemSubstituteRepository orderItemSubstituteRepository;

    @Transactional
    @Override
    public DeliveryCostEstimatePojo getDeliveryCost(Long customerId, DeliveryCostEstimateDto dto) {
        Customer customer = getCustomer(customerId);
        DeductibleHash deductibleHash = createDeductibleHash(customer, dto);
        DeliveryCostEstimatePojo pojo = new DeliveryCostEstimatePojo();
        pojo.setAmountInKobo(deductibleHash.getDeductible());
        pojo.setDeductibleHashToken(deductibleHash.getToken());
        return pojo;
    }

    private Long getDeliveryCost(Customer customer, DeliveryCostEstimateDto dto) {
        return 1000000L;
    }

    private DeductibleHash createDeductibleHash(Customer customer, DeliveryCostEstimateDto dto) {
        DeductibleHash deductibleHash = new DeductibleHash();
        deductibleHash.setDeductible(getDeliveryCost(customer, dto));
        deductibleHash.setDateCreated(LocalDateTime.now());
        deductibleHash.setToken(UUID.randomUUID().toString());
        return deductibleHashRepository.save(deductibleHash);
    }

    private DeductibleHash getDeductibleHash(String token) {
        return deductibleHashRepository.findByToken(token).orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, "Hash does not exist"));
    }

    @Transactional
    @Override
    public OrderCreationPojo createOrder(Long customerId, OrderCreationDto dto) {
        Customer customer = getCustomer(customerId);
        DeductibleHash deductibleHash = getDeductibleHash(dto.getDeductibleHashToken());
        validateDeductibleHash(customer, deductibleHash, dto);
        List<Product> products = getProducts(dto.getProductsQuantities());
        List<ProductSubPojo> substitutes = getSubProducts(products, dto.getProductsQuantities());
        PaymentInvoice paymentInvoice = createPaymentInvoice(customer, deductibleHash, products, substitutes, dto.getProductsQuantities());
        Orders order = createOrder(customer, paymentInvoice, dto);
        List<OrderItem> orderItems = createOrderItems(order, products, dto.getProductsQuantities());
        createOrderItemSubstitutes(substitutes, orderItems);
        OrderCreationPojo pojo = new OrderCreationPojo();
        pojo.setOrderId(order.getId());
        pojo.setAmountDue(paymentInvoice.getAmountInKobo());
        return pojo;
    }

    private void validateDeductibleHash(Customer customer, DeductibleHash deductibleHash, OrderCreationDto dto) {
        List<String> productCodes = dto.getProductsQuantities().stream().map(ProductQuantityDto::getProductCode).collect(Collectors.toList());
        productCodes.addAll(dto.getProductsQuantities().stream().filter(x -> (!Objects.isNull(x.getSubstitute()))).map(x -> x.getSubstitute().getProductCode()).collect(Collectors.toList()));
        DeliveryCostEstimateDto deliveryCostEstimateDto = new DeliveryCostEstimateDto();
        deliveryCostEstimateDto.setLatitude(dto.getLatitude());
        deliveryCostEstimateDto.setLongitude(dto.getLongitude());
        deliveryCostEstimateDto.setStoreCode(dto.getStoreCode());
        deliveryCostEstimateDto.setProductCodes(productCodes);
        Long deliveryCost = getDeliveryCost(customer, deliveryCostEstimateDto);
        if (!deductibleHash.getDeductible().equals(deliveryCost)) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Deductible hash");
        }
    }

    private void createOrderItemSubstitutes(List<ProductSubPojo> substitutes, List<OrderItem> orderItems) {
        List<OrderItemSubstitute> orderItemSubstitutes = substitutes.stream().map(x -> {
            OrderItem orderItem = orderItems.stream().filter(item -> item.getProduct().equals(x.getProduct())).findFirst()
                    .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, "Cannot find order item"));
            OrderItemSubstitute orderItemSubstitute = new OrderItemSubstitute();
            orderItemSubstitute.setOrderItem(orderItem);
            orderItemSubstitute.setProduct(x.getSubstitute());
            orderItemSubstitute.setQuantity(x.getQuantity());
            return orderItemSubstitute;
        }).collect(Collectors.toList());
        orderItemSubstituteRepository.saveAll(orderItemSubstitutes);
    }

    private List<OrderItem> createOrderItems(Orders order, List<Product> products, List<ProductQuantityDto> productsQuantities) {
        List<OrderItem> orderItems = products.stream().map(product -> {
            ProductQuantityDto dto = productsQuantities.stream().filter(x -> x.getProductCode().equals(product.getCode())).findFirst()
                    .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, "Cannot find product"));
            OrderItem orderItem = new OrderItem();
            orderItem.setOrders(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(dto.getQuantity());
            return orderItem;
        }).collect(Collectors.toList());
        return orderItemRepository.saveAll(orderItems);
    }

    private Orders createOrder(Customer customer, PaymentInvoice paymentInvoice, OrderCreationDto dto) {
        Orders order = new Orders();
        order.setCustomer(customer);
        order.setDateCreated(LocalDateTime.now());
        order.setOrderStatus(OrderStatusConstant.NOT_STARTED);
        order.setPaymentInvoice(paymentInvoice);
        order.setEta(LocalDateTime.now().plusMinutes(45));
        order.setDetail(createOrderDetail(dto));
//        Optional.ofNullable(dto.getInstructions()).ifPresent(x->order.set);
        return orderRepository.save(order);
    }

    private OrderDetail createOrderDetail(OrderCreationDto dto) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setIsCustomer(true);
        orderDetail.setAddress(createAddress(dto));
        orderDetail.setRecepientName(dto.getRecipientName());
        orderDetail.setRecepientPhoneNumber(phoneNumberService.formatPhoneNumber(dto.getRecipientPhoneNumber()));
        return orderDetailRepository.save(orderDetail);
    }

    private PaymentInvoice createPaymentInvoice(Customer customer, DeductibleHash deductibleHash, List<Product> products, List<ProductSubPojo> substitutes, List<ProductQuantityDto> productsQuantities) {
        PaymentInvoice paymentInvoice = new PaymentInvoice();
        paymentInvoice.setDateCreated(LocalDateTime.now());
        paymentInvoice.setAmountInKobo(totalAmountDue(products, substitutes, deductibleHash, productsQuantities));
        paymentInvoice.setCustomer(customer);
        paymentInvoice.setPaymentStatus(PaymentStatusConstant.NOT_PAID);
        paymentInvoice.setStatus(GenericStatusConstant.ACTIVE);
        paymentInvoice.setDeductableInKobo(deductibleHash.getDeductible());
        return paymentInvoiceRepository.save(paymentInvoice);
    }

    private Long totalAmountDue(List<Product> products, List<ProductSubPojo> substitutes, DeductibleHash deductibleHash, List<ProductQuantityDto> productsQuantities) {
        Long productTotal = products.stream().mapToLong(product -> {
            ProductQuantityDto pqd = productsQuantities.stream().filter(x -> x.getProductCode().equals(product.getCode())).findFirst()
                    .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, "Cannot find product quantity"));
            return product.getPriceInKobo() * pqd.getQuantity();
        }).sum();
        Long subProductTotal = substitutes.stream().mapToLong(x -> x.getSubstitute().getPriceInKobo() * x.getQuantity()).sum();
        return (deductibleHash.getDeductible()) + subProductTotal + productTotal;
    }

    private Address createAddress(OrderCreationDto dto) {
        State state = stateRepository.findByCode(dto.getStateCode())
                .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, "Cannot fine state"));
        Address address = new Address();
        address.setStatus(GenericStatusConstant.ACTIVE);
        address.setDateCreated(LocalDateTime.now());
        address.setGps(createGps(dto));
        address.setState(state);
        return addressRepository.save(address);
    }

    private Gps createGps(OrderCreationDto dto) {
        Gps gps = new Gps();
        gps.setLongitude(dto.getLongitude());
        gps.setLatitude(dto.getLongitude());
        return gpsRepository.save(gps);
    }

    private List<ProductSubPojo> getSubProducts(List<Product> products, List<ProductQuantityDto> productsQuantities) {
        List<String> productSubCodes = productsQuantities.stream().filter(x -> !Objects.isNull(x.getSubstitute()))
                .map(x -> x.getSubstitute().getProductCode()).collect(Collectors.toList());
        List<Product> subProducts = getProductsFromCodes(productSubCodes);
        return subProducts.stream().map(sub -> {
            ProductQuantityDto pqd = productsQuantities.stream()
                    .filter(x -> (!Objects.isNull(x.getSubstitute())) && x.getSubstitute().getProductCode().equals(sub.getCode())).findFirst()
                    .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, "Substitute has no main product"));
            Product product = products.stream().filter(x -> x.getCode().equals(pqd.getProductCode())).findFirst()
                    .orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, "Cannot find product from code"));
            ProductSubPojo pojo = new ProductSubPojo();
            pojo.setProduct(product);
            pojo.setSubstitute(sub);
            pojo.setQuantity(pqd.getSubstitute().getQuantity());
            return pojo;
        }).collect(Collectors.toList());
    }

    private List<Product> getProducts(List<ProductQuantityDto> productsQuantities) {
        List<String> productCodes = productsQuantities.stream().map(ProductQuantityDto::getProductCode).collect(Collectors.toList());
        return getProductsFromCodes(productCodes);
    }

    private List<Product> getProductsFromCodes(List<String> productCodes) {
        return appRepository.startJPAQuery(QProduct.product)
                .where(QProduct.product.status.eq(GenericStatusConstant.ACTIVE))
                .where(QProduct.product.code.in(productCodes))
                .fetch();
    }

    private Customer getCustomer(Long id) {
        return customerRepository.findActiveById(id).orElseThrow(() -> new ErrorResponse(HttpStatus.BAD_REQUEST, "Customer does not exist"));
    }
}
