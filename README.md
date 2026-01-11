# Jakarta EE Unit Testing Demo

A minimal Jakarta EE project demonstrating unit testing with dependency injection using Mockito.

## Project Structure

```
src/
├── main/java/com/example/demo/
│   ├── entity/
│   │   └── Product.java          # JPA Entity with PostgreSQL mapping
│   ├── repository/
│   │   └── ProductRepository.java # Data access layer with CDI
│   ├── service/
│   │   └── ProductService.java    # Business logic (unit tested)
│   └── resource/
│       └── ProductResource.java   # JAX-RS REST endpoints
├── main/resources/META-INF/
│   ├── persistence.xml           # JPA configuration
│   ├── beans.xml                 # CDI configuration
│   └── data.sql                  # Sample data
└── test/java/com/example/demo/
    └── service/
        └── ProductServiceTest.java # Unit tests with Mockito
```

## Key Concepts Demonstrated

### 1. Dependency Injection (CDI)
- `@ApplicationScoped` - CDI managed beans
- `@Inject` - Constructor injection for testability
- `@PersistenceContext` - JPA EntityManager injection

### 2. Unit Testing with Mockito
- `@ExtendWith(MockitoExtension.class)` - JUnit 5 + Mockito integration
- `@Mock` - Create mock dependencies
- `@InjectMocks` - Auto-inject mocks into service under test
- `when().thenReturn()` - Stub behavior
- `verify()` - Verify interactions
- `verifyNoInteractions()` - Ensure no calls when validation fails

### 3. Testing Patterns
- **Given-When-Then** pattern
- **Business logic validation** tests

## Running the Project

### Prerequisites
- Java 21+
- Docker (for PostgreSQL)
- Gradle

### Start PostgreSQL
```bash
docker-compose up -d
```

### Run Tests
```bash
./gradlew test
```

### Build WAR
```bash
./gradlew build
```

## Business Logic Under Test

The `ProductService` contains several methods with business logic:

| Method | Description | Business Rules |
|--------|-------------|----------------|
| `getAvailableProductsWithinBudget()` | Find affordable products | Must be active, in stock, within budget |
| `calculateCategoryInventoryValue()` | Sum inventory value | price × quantity for category |
| `getProductsWithDiscount()` | Apply discount pricing | Only active products, valid % |
| `canFulfillOrder()` | Check order feasibility | Active product with sufficient stock |
| `getLowStockProducts()` | Find low inventory | Below threshold, active only |

## Sample Test Cases

```java
void shouldFilterProductsWithinBudgetActiveAndInStock() {
    // given
    when(productRepository.findInStock())
            .thenReturn(Arrays.asList(laptop, mouse, keyboard, inactiveProduct));

    // when
    List<Product> result = productService.getAvailableProductsWithinBudget(new BigDecimal("200.00"));

    // then
    assertEquals(2, result.size());
    assertEquals(keyboard.getName(), result.get(0).getName());
    assertEquals(mouse.getName(), result.get(1).getName());
    assertFalse(result.stream().anyMatch(p -> p.getName().equals(inactiveProduct.getName())));
```

## Database Configuration

The project uses JTA DataSource configured via the application server. 
For local development with PostgreSQL:

- **Database**: demodb
- **User**: demo
- **Password**: demo123
- **Port**: 5432

## Deployment

Deploy the generated WAR file to a Jakarta EE compatible server:
- Payara Server
- WildFly
- Open Liberty
- TomEE

Configure the datasource on your application server to point to PostgreSQL.
