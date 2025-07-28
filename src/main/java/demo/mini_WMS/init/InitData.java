package demo.mini_WMS.init;
import demo.mini_WMS.domain.Product;
import demo.mini_WMS.domain.Warehouse;
import demo.mini_WMS.domain.WarehouseLocation;
import demo.mini_WMS.repository.ProductRepository;
import demo.mini_WMS.repository.WarehouseLocationRepository;
import demo.mini_WMS.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitData implements CommandLineRunner {

    private final WarehouseRepository warehouseRepo;
    private final ProductRepository productRepo;
    private final WarehouseLocationRepository locationRepo;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 창고 없으면 생성
        if (warehouseRepo.findAll().isEmpty()) {
            Warehouse wh = new Warehouse("1번 창고", "서울");
            warehouseRepo.save(wh);

            // ✅ 위치도 함께 초기화
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 10; col++) {
                    WarehouseLocation loc = WarehouseLocation.builder()
                            .warehouse(wh)
                            .rowIdx(row)
                            .colIdx(col)
                            .build();
                    locationRepo.save(loc);
                }
            }
        }

        // 상품이 없으면 생성
        if (productRepo.findAll().isEmpty()) {
            productRepo.save(new Product("SKU001", "🍎사과", "과일"));
            productRepo.save(new Product("SKU002", "🍌바나나", "과일"));
            productRepo.save(new Product("SKU003", "🍊오렌지", "과일"));
            productRepo.save(new Product("SKU004", "🥦브로콜리", "채소"));
            productRepo.save(new Product("SKU005", "🥚계란", "유제품"));
        }
    }
}
