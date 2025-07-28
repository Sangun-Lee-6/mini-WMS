package demo.mini_WMS.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InboundItem {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Inbound inbound;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private Long quantity;

}
