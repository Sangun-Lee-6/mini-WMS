package demo.mini_WMS.init;

import demo.mini_WMS.domain.Product;
import demo.mini_WMS.domain.Warehouse;
import demo.mini_WMS.repository.ProductRepository;
import demo.mini_WMS.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitData implements CommandLineRunner {

    private final WarehouseRepository warehouseRepo;
    private final ProductRepository productRepo;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (warehouseRepo.findAll().isEmpty()) {
            Warehouse wh = new Warehouse("1번 창고", "서울");
            warehouseRepo.save(wh);
        }

        if (productRepo.findAll().isEmpty()) {
            productRepo.save(new Product("SKU001", "사과", "과일"));
            productRepo.save(new Product("SKU002", "바나나", "과일"));
        }
    }
}
