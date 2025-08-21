package com.example.demo;

import org.junit.jupiter.api.*;                      // Assertions.*, @Test
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 
// ^ Use your real Postgres from application.properties (Docker). 
// If you prefer H2 for this test, remove the annotation above.
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repo;

    @Test
    void saveAndLoadProduct_roundTrips() {
        // TODO: 1) construct a Product (no id / version set by you)
        Product p = new Product("abc", "Demo Product", 1999, 10);

        // TODO: 2) save it using the repository
        Product saved = repo.save(p);

        // TODO: 3) basic assertions after save:
        // - id is not null (JPA generated it)
        // - fields you set are preserved
        Assertions.assertNotNull(saved.getId());
        Assertions.assertEquals("abc", saved.getSku());
        Assertions.assertEquals(10, saved.getStock());

        // TODO: 4) fetch it back from the DB (by id)
        var reloadedOpt = repo.findById(saved.getId());
        Assertions.assertTrue(reloadedOpt.isPresent());
        var reloaded = reloadedOpt.get();

        // TODO: 5) assert equality of the important fields
        Assertions.assertEquals(saved.getSku(), reloaded.getSku());
        Assertions.assertEquals(saved.getName(), reloaded.getName());
        Assertions.assertEquals(saved.getPriceCents(), reloaded.getPriceCents());
        Assertions.assertEquals(saved.getStock(), reloaded.getStock());
    }

    @Test
    void optimisticVersion_incrementsOnUpdate() {
        // Goal: show @Version changes when you update and save.

        // TODO: 1) save a new product
        var saved = repo.save(new Product("abcd", "Versioned", 500, 3));

        // TODO: 2) capture current version
        Long v1 = saved.getVersion();

        // TODO: 3) modify a field and save again
        saved.setStock(2);
        var updated = repo.saveAndFlush(saved);

        // TODO: 4) assert version increased (and value changed)
        Assertions.assertTrue(updated.getVersion() > v1);
        Assertions.assertEquals(2, updated.getStock());
    }
}
