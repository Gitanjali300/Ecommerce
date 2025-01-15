package com.axontic.ecommerce.service.impl;


import com.axontic.ecommerce.exception.InvalidInputException;
import com.axontic.ecommerce.model.ShoppingCartDTO;
import com.axontic.ecommerce.entity.CartItem;
import com.axontic.ecommerce.entity.Product;
import com.axontic.ecommerce.entity.ShoppingCart;
import com.axontic.ecommerce.exception.ResourceNotFoundException;
import com.axontic.ecommerce.repository.CartItemRepository;
import com.axontic.ecommerce.repository.CustomerRepository;
import com.axontic.ecommerce.repository.ProductRepository;
import com.axontic.ecommerce.repository.ShoppingCartRepository;
import com.axontic.ecommerce.response.StatusResponseDTO;
import com.axontic.ecommerce.service.ShoppingCartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartServiceImpl.class);

    private final ShoppingCartRepository shoppingCartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ObjectMapper objectMapper;

    public ShoppingCartServiceImpl(
            ShoppingCartRepository shoppingCartRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            CartItemRepository cartItemRepository, ObjectMapper objectMapper) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<ShoppingCartDTO> getCustomerCarts(Long customerId) {
        logger.info("Fetching all shopping carts for customer with ID: {}", customerId);
        try {
            List<ShoppingCart> carts = shoppingCartRepository.findByCustomerCustomerId(customerId);
            return carts.stream()
                    .map(shoppingCarts -> objectMapper.convertValue(shoppingCarts , ShoppingCartDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            logger.error("Error while fetching shopping carts for customer ID {}: {}", customerId, ex.getMessage(), ex);
            throw new RuntimeException("Unable to fetch shopping carts.", ex);
        }
    }

    @Override
    @Transactional
    public StatusResponseDTO addProductToCart(Long customerId, Long cartId, Long productId, int quantity) {
        logger.info("Adding product with ID: {} to cart with ID: {} for customer with ID: {}", productId, cartId, customerId);
        try {
            final ShoppingCart cart;
            final Product product;
            StatusResponseDTO statusResponseDTO = new StatusResponseDTO();
            validateInput(customerId,cartId,productId,quantity);
            if (cartId != null) {
                cart = shoppingCartRepository.findByIdWithProducts(cartId)
                        .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));
                logger.debug("Fetched cart with ID: {}. Current products: {}", cart.getShoppingCartId(), cart.getCartItems());
            } else {
                cart = new ShoppingCart();
                cart.setCustomer(customerRepository.findById(customerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId)));
                shoppingCartRepository.save(cart);
                logger.info("Created a new cart with ID: {} for customer ID: {}", cart.getShoppingCartId(), customerId);
            }

            product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

            cartItemRepository.findByShoppingCartShoppingCartIdAndProductItemNumber(cart.getShoppingCartId(), productId).ifPresentOrElse(cartItem -> {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
                cartItemRepository.save(cartItem);
                logger.info("Updated quantity for product ID: {} in cart ID: {}", productId, cart.getShoppingCartId());
            }, () -> {
                CartItem newCartItem = new CartItem();
                newCartItem.setShoppingCart(cart);
                newCartItem.setProduct(product);
                newCartItem.setQuantity(quantity);
                cartItemRepository.save(newCartItem);
                logger.info("Added new product ID: {} to cart ID: {}", productId, cart.getShoppingCartId());
            });

            statusResponseDTO.setStatusCode(HttpStatus.CREATED.value());
            statusResponseDTO.setStatusMessage("Product ID " + productId + " successfully added for customer " + customerId);
            return statusResponseDTO;
        } catch (Exception ex) {
            logger.error("Error while adding product to cart: {}", ex.getMessage(), ex);
            throw new InvalidInputException("Unable to add product to cart.", ex);
        }
    }

    private void validateInput(Long customerId, Long cartId, Long productId, int quantity) {
        logger.info("Validating input for customerId: {}, cartId: {}, productId: {}, quantity: {}", customerId, cartId, productId, quantity);

        // Check if quantity is negative or zero
        if (quantity <= 0) {
            logger.error("Invalid quantity: {}. Quantity must be greater than zero.", quantity);
            throw new InvalidInputException("Quantity must be greater than zero.");
        }

        // Check if the product is already added to a different cart for the same customer
        List<ShoppingCart> customerCarts = shoppingCartRepository.findByCustomerCustomerId(customerId);
        boolean productInOtherCart = customerCarts.stream()
                .filter(cart -> !cart.getShoppingCartId().equals(cartId)) // Exclude the current cart if provided
                .flatMap(cart -> cart.getCartItems().stream()) // Flatten cart items into a single stream
                .anyMatch(cartItem -> cartItem.getProduct().getItemNumber().equals(productId));

        if (productInOtherCart) {
            logger.error("Product with ID: {} is already added to a different cart for customer ID: {}", productId, customerId);
            throw new InvalidInputException("Product is already added to a different cart for the customer.");
        }

        logger.info("Input validation successful.");
    }


    @Override
    @Transactional
    public void removeProductFromCart(Long customerId, Long productId, int quantity) {
        logger.info("Removing product with ID: {} from any cart for customer with ID: {}", productId, customerId);
        try {
            // Validate input
            if (quantity <= 0) {
                logger.error("Invalid quantity: {}. Quantity must be greater than zero.", quantity);
                throw new IllegalArgumentException("Quantity must be greater than zero.");
            }

            // Fetch all carts for the customer
            List<ShoppingCart> customerCarts = shoppingCartRepository.findByCustomerCustomerId(customerId);
            if (customerCarts.isEmpty()) {
                logger.warn("No carts found for customer with ID: {}", customerId);
                throw new ResourceNotFoundException("No shopping carts found for the customer.");
            }

            // Iterate through each cart to find the product
            boolean productFound = false;
            for (ShoppingCart cart : customerCarts) {
                CartItem cartItem = cartItemRepository.findByShoppingCartShoppingCartIdAndProductItemNumber(cart.getShoppingCartId(), productId)
                        .orElse(null);

                if (cartItem != null) {
                    productFound = true;
                    if (cartItem.getQuantity() <= quantity) {
                        // Remove the cart item if quantity is less than or equal to the requested quantity
                        cartItemRepository.delete(cartItem);
                        //if cart contains no items, cart should not exist
                        shoppingCartRepository.delete(cart);
                        logger.info("Product completely removed from cart ID: {}", cart.getShoppingCartId());
                    } else {
                        // Reduce the quantity and save the cart item
                        cartItem.setQuantity(cartItem.getQuantity() - quantity);
                        cartItemRepository.save(cartItem);
                        logger.info("Reduced quantity of product ID: {} in cart ID: {}", productId, cart.getShoppingCartId());
                    }
                    break; // Exit loop after processing the product
                }
            }

            if (!productFound) {
                logger.warn("Product with ID: {} not found in any cart for customer ID: {}", productId, customerId);
                throw new ResourceNotFoundException("Product not found in any cart for the customer.");
            }
        } catch (Exception ex) {
            logger.error("Error while removing product with ID: {} from any cart for customer ID: {}: {}", productId, customerId, ex.getMessage(), ex);
            throw new RuntimeException("Unable to remove product from cart.", ex);
        }
    }


    @Override
    @Transactional
    public void deleteCart(Long cartId) {
        logger.info("Deleting shopping cart with ID: {}", cartId);
        try {
            ShoppingCart cart = shoppingCartRepository.findById(cartId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));
            cartItemRepository.deleteAll(cart.getCartItems());
            shoppingCartRepository.delete(cart);
            logger.info("Cart deleted successfully.");
        } catch (Exception ex) {
            logger.error("Error while deleting cart ID {}: {}", cartId, ex.getMessage(), ex);
            throw new RuntimeException("Unable to delete cart.", ex);
        }
    }
}
