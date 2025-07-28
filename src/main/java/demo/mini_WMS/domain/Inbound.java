package demo.mini_WMS.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inbound {

    @Id
    @GeneratedValue
    private Long id;

    private String supplier;

    private LocalDateTime inboundDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Warehouse warehouse;

    @OneToMany(mappedBy = "inbound", cascade = CascadeType.ALL)
    @Builder.Default
    private List<InboundItem> items = new ArrayList<>();

    public void addItem(InboundItem item) {
        item.setInbound(this);
        this.items.add(item);
    }
}
