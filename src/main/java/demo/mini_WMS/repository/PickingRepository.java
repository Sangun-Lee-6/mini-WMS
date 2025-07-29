package demo.mini_WMS.repository;

import demo.mini_WMS.domain.picking.Picking;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PickingRepository {

    private final EntityManager em;

    // 피킹 세션 저장
    public void save(Picking picking) {
        em.persist(picking);
    }

    // id로 조회
    public Optional<Picking> findById(Long id) {
        return Optional.ofNullable(em.find(Picking.class, id));
    }

    // 모든 피킹 세션 조회
    public java.util.List<Picking> findAll() {
        return em.createQuery("select p from Picking p", Picking.class)
                .getResultList();
    }

    // 특정 알고리즘을 사용한 피킹만 조회 (예시)
    public java.util.List<Picking> findByAlgorithm(String algorithm) {
        return em.createQuery("select p from Picking p where p.algorithm = :alg", Picking.class)
                .setParameter("alg", algorithm)
                .getResultList();
    }
}