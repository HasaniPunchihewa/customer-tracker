package com.hasanipunchihewa.customer_tracker;

import com.hasanipunchihewa.customer_tracker.dto.AuthRequest;
import com.hasanipunchihewa.customer_tracker.dto.AuthResponse;
import com.hasanipunchihewa.customer_tracker.dto.OrderRequest;
import com.hasanipunchihewa.customer_tracker.dto.OrderItemRequest;
import com.hasanipunchihewa.customer_tracker.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CustomerTrackerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String token;

    @BeforeEach
    void setup() {
        AuthRequest auth = new AuthRequest();
        auth.setEmail("test@example.com");
        auth.setPassword("password123");

        restTemplate.postForEntity("/api/auth/register", auth, AuthResponse.class);

        ResponseEntity<AuthResponse> login = restTemplate.postForEntity(
                "/api/auth/login", auth, AuthResponse.class);

        token = login.getBody().getToken();
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // ── Auth Tests ──────────────────────────────────────────────

    @Test
    void register_shouldReturn200() {
        AuthRequest auth = new AuthRequest();
        auth.setEmail("newuser@example.com");
        auth.setPassword("password123");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/auth/register", auth, AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getToken()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("newuser@example.com");
    }

    @Test
    void login_withInvalidCredentials_shouldReturnError() {
        AuthRequest auth = new AuthRequest();
        auth.setEmail("nobody@example.com");
        auth.setPassword("wrongpassword");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/login", auth, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void protectedEndpoint_withoutToken_shouldReturn403() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/customers", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // ── Customer Tests ──────────────────────────────────────────

    @Test
    void createCustomer_shouldReturn201() {
        Map<String, String> customer = Map.of(
                "name", "Anika Silva",
                "email", "anika@example.com",
                "phone", "0771234567",
                "address", "42 Galle Road, Colombo"
        );

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/customers",
                HttpMethod.POST,
                new HttpEntity<>(customer, authHeaders()),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("name")).isEqualTo("Anika Silva");
    }

    @Test
    void getAllCustomers_shouldReturn200() {
        ResponseEntity<List> response = restTemplate.exchange(
                "/api/customers",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(List.class);
    }

    @Test
    void updateCustomer_shouldReturn200() {
        // Create customer
        Map<String, String> customer = Map.of(
                "name", "Anika Silva",
                "email", "anika@example.com"
        );
        ResponseEntity<Map> created = restTemplate.exchange(
                "/api/customers",
                HttpMethod.POST,
                new HttpEntity<>(customer, authHeaders()),
                Map.class);
        String id = (String) created.getBody().get("id");

        // Update customer
        Map<String, String> updated = Map.of(
                "name", "Anika Fernando",
                "email", "anika@example.com"
        );
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/customers/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(updated, authHeaders()),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("name")).isEqualTo("Anika Fernando");
    }

    @Test
    void deleteCustomer_shouldReturn204() {
        Map<String, String> customer = Map.of(
                "name", "Temp Customer",
                "email", "temp@example.com"
        );
        ResponseEntity<Map> created = restTemplate.exchange(
                "/api/customers",
                HttpMethod.POST,
                new HttpEntity<>(customer, authHeaders()),
                Map.class);
        String id = (String) created.getBody().get("id");

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/customers/" + id,
                HttpMethod.DELETE,
                new HttpEntity<>(authHeaders()),
                Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    // ── Product Tests ───────────────────────────────────────────

    @Test
    void createProduct_shouldReturn201() {
        Map<String, Object> product = Map.of(
                "name", "Siren Dress",
                "description", "A dark elegant dress",
                "price", 89.99
        );

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/products",
                HttpMethod.POST,
                new HttpEntity<>(product, authHeaders()),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("name")).isEqualTo("Siren Dress");
    }

    @Test
    void getAllProducts_shouldReturn200() {
        ResponseEntity<List> response = restTemplate.exchange(
                "/api/products",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders()),
                List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(List.class);
    }

    // ── Order Tests ─────────────────────────────────────────────

    @Test
    void createOrder_shouldReturn201() {
        // Create customer
        Map<String, String> customer = Map.of("name", "Anika Silva", "email", "anika2@example.com");
        ResponseEntity<Map> customerResult = restTemplate.exchange(
                "/api/customers", HttpMethod.POST,
                new HttpEntity<>(customer, authHeaders()), Map.class);
        String customerId = (String) customerResult.getBody().get("id");

        // Create product
        Map<String, Object> product = Map.of("name", "Siren Dress", "price", 89.99);
        ResponseEntity<Map> productResult = restTemplate.exchange(
                "/api/products", HttpMethod.POST,
                new HttpEntity<>(product, authHeaders()), Map.class);
        String productId = (String) productResult.getBody().get("id");

        // Create order
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(UUID.fromString(productId));
        item.setQuantity(1);

        OrderRequest order = new OrderRequest();
        order.setCustomerId(UUID.fromString(customerId));
        order.setStatus(Order.Status.PENDING);
        order.setSource(Order.Source.INSTAGRAM);
        order.setNotes("Gift wrap requested");
        order.setItems(List.of(item));

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/orders", HttpMethod.POST,
                new HttpEntity<>(order, authHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("PENDING");
        assertThat(response.getBody().get("source")).isEqualTo("INSTAGRAM");
    }

    @Test
    void updateOrderStatus_shouldReturn200() {
        // Create customer
        Map<String, String> customer = Map.of("name", "Test Customer", "email", "status@example.com");
        ResponseEntity<Map> customerResult = restTemplate.exchange(
                "/api/customers", HttpMethod.POST,
                new HttpEntity<>(customer, authHeaders()), Map.class);
        String customerId = (String) customerResult.getBody().get("id");

        // Create product
        Map<String, Object> product = Map.of("name", "Moon Skirt", "price", 59.99);
        ResponseEntity<Map> productResult = restTemplate.exchange(
                "/api/products", HttpMethod.POST,
                new HttpEntity<>(product, authHeaders()), Map.class);
        String productId = (String) productResult.getBody().get("id");

        // Create order
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(UUID.fromString(productId));
        item.setQuantity(1);

        OrderRequest order = new OrderRequest();
        order.setCustomerId(UUID.fromString(customerId));
        order.setStatus(Order.Status.PENDING);
        order.setSource(Order.Source.WHATSAPP);
        order.setItems(List.of(item));

        ResponseEntity<Map> orderResult = restTemplate.exchange(
                "/api/orders", HttpMethod.POST,
                new HttpEntity<>(order, authHeaders()), Map.class);
        String orderId = (String) orderResult.getBody().get("id");

        // Update status
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/orders/" + orderId + "/status?status=DELIVERED",
                HttpMethod.PATCH,
                new HttpEntity<>(authHeaders()),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("status")).isEqualTo("DELIVERED");
    }
}