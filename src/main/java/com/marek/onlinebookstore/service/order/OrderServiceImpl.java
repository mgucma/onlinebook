package com.marek.onlinebookstore.service.order;

import static com.marek.onlinebookstore.model.Status.BOOK_DELIVERED;
import static com.marek.onlinebookstore.model.Status.ORDER_COMPLETED;
import static com.marek.onlinebookstore.model.Status.ORDER_PENDING;

import com.marek.onlinebookstore.dto.order.OrderDto;
import com.marek.onlinebookstore.dto.order.OrderItemDto;
import com.marek.onlinebookstore.dto.order.PlacingOrderRequestDto;
import com.marek.onlinebookstore.exception.EntityNotFoundException;
import com.marek.onlinebookstore.mapper.OrderItemMapper;
import com.marek.onlinebookstore.mapper.OrderMapper;
import com.marek.onlinebookstore.model.CartItem;
import com.marek.onlinebookstore.model.Order;
import com.marek.onlinebookstore.model.OrderItem;
import com.marek.onlinebookstore.model.ShoppingCart;
import com.marek.onlinebookstore.model.User;
import com.marek.onlinebookstore.repository.cart.ShoppingCartRepository;
import com.marek.onlinebookstore.repository.order.OrderRepository;
import com.marek.onlinebookstore.repository.user.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    public static final String MISSING_ORDER_MESSAGE = "Order with your id not found, id: ";
    public static final String MISSING_ORDER_ITEM_MESSAGE
            = "Order item with your id not found, id: ";

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    @Override
    public OrderDto makeOrder(PlacingOrderRequestDto requestDto) {
        List<User> users = userRepository.findByShippingAddress(requestDto.shippingAddress());

        User user = users.stream().findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("No user found for shipping address "
                                + requestDto.shippingAddress())
                );

        Order newOrder = createEmptyOrder(requestDto, user);
        Order orderWithItems = fillingOrderWithItems(user, newOrder);

        return orderMapper.toDto(orderWithItems);
    }

    private Order fillingOrderWithItems(User user, Order newOrder) {
        ShoppingCart cart = shoppingCartRepository
                .findShoppingCartByUser(user.getId());
        Set<OrderItem> orderItemSet = getOrderItemSet(cart, newOrder);

        Set<OrderItem> orderItemsWithPrice = calculatePriceForSet(orderItemSet);
        newOrder.setOrderItems(orderItemsWithPrice);

        BigDecimal total = orderItemsWithPrice.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        newOrder.setTotal(total);
        return orderRepository.save(newOrder);
    }

    private Order createEmptyOrder(PlacingOrderRequestDto requestDto, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(ORDER_PENDING);
        order.setTotal(BigDecimal.ZERO);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(requestDto.shippingAddress());
        order.setOrderItems(new HashSet<>());
        return orderRepository.save(order);
    }

    private Set<OrderItem> calculatePriceForSet(Set<OrderItem> orderItemSet) {
        return orderItemSet.stream()
                .map(this::setPrice)
                .collect(Collectors.toSet());
    }

    private OrderItem setPrice(OrderItem orderItem) {
        orderItem.setPrice(
                orderItem.getBook().getPrice().multiply(
                        BigDecimal.valueOf(orderItem.getQuantity())
                )
        );
        return orderItem;
    }

    private Set<OrderItem> getOrderItemSet(ShoppingCart cart, Order emptyOrder) {
        Set<CartItem> cartItems = cart.getCartItems();
        return cartItems.stream()
                .map(orderItemMapper::fromCarttoOrderItem)
                .map(orderItem -> setOrderToItem(orderItem, emptyOrder))
                .collect(Collectors.toSet());
    }

    private OrderItem setOrderToItem(OrderItem orderItem, Order emptyOrder) {
        orderItem.setOrder(emptyOrder);
        return orderItem;
    }

    @Override
    public List<OrderDto> getHistory(User user, Pageable pageable) {
        return orderRepository.findAllByUser(user, pageable).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderItemDto> getFromOrder(Long orderId) {
        Order order = getOrderFromId(orderId);
        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getFromOrder(Long orderId, Long id) {
        Order order = getOrderFromId(orderId);
        Set<OrderItem> orderItems = order.getOrderItems();
        OrderItem orderItem = orderItems.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException(MISSING_ORDER_ITEM_MESSAGE + id)
                );
        return orderItemMapper.toDto(orderItem);
    }

    private Order getOrderFromId(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new EntityNotFoundException(MISSING_ORDER_MESSAGE + orderId)
                );
    }

    @Override
    public OrderDto updateStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        switch (order.getStatus()) {
            case ORDER_PENDING:
                order.setStatus(BOOK_DELIVERED);
                break;
            case BOOK_DELIVERED:
                order.setStatus(ORDER_COMPLETED);
                break;
            default:
                throw new IllegalStateException("Cannot update status further");
        }

        return orderMapper.toDto(orderRepository.save(order));
    }
}
