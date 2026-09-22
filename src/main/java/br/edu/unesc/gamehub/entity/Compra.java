package br.edu.unesc.gamehub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "compra",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_compra_usuario_jogo", columnNames = {"usuario_id", "jogo_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jogo_id", nullable = false)
    private Jogo jogo;

    @Column(name = "valor_pago", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPago;

    @Column(name = "data_compra", nullable = false)
    private LocalDateTime dataCompra;

    @PrePersist
    public void prePersist() {
        if (dataCompra == null) {
            dataCompra = LocalDateTime.now();
        }
    }
}