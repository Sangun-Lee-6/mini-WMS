package demo.mini_WMS.repository;

import demo.mini_WMS.domain.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(Product product) {
        em.persist(product);
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(em.find(Product.class, id));
    }

    public Optional<Product> findBySkuCode(String skuCode) {
        List<Product> result = em.createQuery("SELECT p FROM Product p WHERE p.skuCode = :sku", Product.class)
                .setParameter("sku", skuCode)
                .getResultList();
        return result.stream().findFirst();
    }

    public List<Product> findAll() {
        return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }
}
