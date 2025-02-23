package com.springboot.ecommerce.service;

import com.springboot.ecommerce.Utils.AuthUtils;
import com.springboot.ecommerce.dto.CartDTO;
import com.springboot.ecommerce.dto.ProductDTO;
import com.springboot.ecommerce.entity.Cart;
import com.springboot.ecommerce.entity.CartItem;
import com.springboot.ecommerce.entity.Product;
import com.springboot.ecommerce.entity.User;
import com.springboot.ecommerce.exception.APIException;
import com.springboot.ecommerce.exception.ResourceNotFoundException;
import com.springboot.ecommerce.repository.CartItemRepository;
import com.springboot.ecommerce.repository.CartRepository;
import com.springboot.ecommerce.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuthUtils authUtils;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Long quantity) {
        /*
        * Find cart or use create cart
        * check if it already in the cart
        * quantity !=0 and < original counts
        * save and return cartDTO
        *
        */
        Cart cart = getUserCart();
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product",productId,"Not Found"));
        CartItem cartItem = cartItemRepository
                .findByCart_CartIdAndProduct_ProductId(cart.getCartId(), productId);

        if(cartItem!=null)
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        if(product.getQuantity()==0)
            throw new APIException("Product " + product.getProductName() + " Stock is not available");
        if(quantity>product.getQuantity())
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");

        cartItem = new CartItem();

        cartItem.setCart(cart);
        cartItem.setQuantity(quantity);
        cartItem.setProduct(product);
        cartItem.setDiscount(product.getDiscount());
        cartItem.setProductPrice(product.getPrice());
        CartItem savedCartItem = cartItemRepository.save(cartItem);

        cart.setTotalPrice(cart.getTotalPrice() + (product.getPrice() * quantity));
        cart.getCartItems().add(savedCartItem);
        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());
        return cartDTO;
    }

    @Override
    public CartDTO getCart() {
        User user = authUtils.loggedinUser();
        Cart cart = cartRepository.findByUser_UserId(user.getUserId());
        if(cart==null)
            throw new APIException("Cart is not found");

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<ProductDTO> productDTO = cart.getCartItems()
                .stream().map(cartItem -> {
                    ProductDTO map = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                    map.setQuantity(cartItem.getQuantity());
                    return map;
                }).toList();

        cartDTO.setProducts(productDTO);
        return cartDTO;
    }

    @Override
    public CartDTO updateProductCart(Long productId, Integer quantity) {
        User user = authUtils.loggedinUser();
        Cart cart = cartRepository.findByUser_UserId(user.getUserId());

        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product",productId,"Not Found"));

        CartItem cartItem = cartItemRepository
                .findByCart_CartIdAndProduct_ProductId(cart.getCartId(), productId);


        if(cartItem==null)
            throw new APIException("Product " + product.getProductName() + " is Not found in the cart");
        if(product.getQuantity()==0)
            throw new APIException("Product " + product.getProductName() + " Stock is not available");

        if (product.getQuantity() < quantity)
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");

        cartItem.setQuantity(cartItem.getQuantity()+quantity);

        if(cartItem.getQuantity()<0)
            throw new APIException("Product " + product.getProductName() + " should not be negative quantity");

        cartItem.setProductPrice(product.getPrice());

        if(cartItem.getQuantity()==0)
            cartItemRepository.delete(cartItem);
        else
            cartItemRepository.save(cartItem);

        cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);

        List<ProductDTO> productDTO = cart.getCartItems()
                .stream().map(item->{
                    ProductDTO prd = modelMapper.map(item,ProductDTO.class);
                    prd.setQuantity(item.getQuantity());
                    return  prd;
                }).toList();
        cartDTO.setProducts(productDTO);

        return cartDTO;
    }

    @Override
    public String deleteProductFromCart(Long productId) {
        User user = authUtils.loggedinUser();
        Cart cart = cartRepository.findByUser_UserId(user.getUserId());

        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product",productId,"Not Found"));

        CartItem cartItem = cartItemRepository
                .findByCart_CartIdAndProduct_ProductId(cart.getCartId(), productId);
        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", productId,"Not Found");
        }

        cartItemRepository.delete(cartItem);

        cart.setTotalPrice(cart.getTotalPrice()-(cartItem.getProductPrice() * cartItem.getQuantity()));
        cartRepository.save(cart);

        return "success";
    }

    private Cart getUserCart() {
        User user = authUtils.loggedinUser();

        Cart cart = cartRepository.findByUser_UserId(user.getUserId());
        if(cart==null){
            cart = new Cart();
            cart.setUser(user);
            cart.setTotalPrice(0.0);
            cart = cartRepository.save(cart);
        }
        return cart;
    }
}
