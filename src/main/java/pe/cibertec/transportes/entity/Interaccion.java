package pe.cibertec.transportes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "interaccion")
@Getter
@Setter
@NoArgsConstructor
public class Interaccion extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TipoInteraccion tipo;

  @Column(nullable = false, length = 1000)
  private String comentario;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private EstadoInteraccion estado = EstadoInteraccion.PENDIENTE;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "linea_id", nullable = false)
  private LineaTransporte linea;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;
}
