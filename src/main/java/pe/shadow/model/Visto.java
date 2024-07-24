package pe.shadow.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "archivo_visto")
public class Visto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idvisto;

    @ManyToOne
    @JoinColumn(name = "idfile", nullable = false)
    private Archivo archivo;

    @ManyToOne
    @JoinColumn(name = "id_usuario_creacion", nullable = false)
    private Usuario usuarioCreacion;

    private LocalDateTime fechaCreacion;

    @PrePersist
    void prePersistFechaCreacion()
    {
        fechaCreacion = LocalDateTime.now();
    }

    public Visto() {
    }

    public Integer getIdvisto() {
        return idvisto;
    }

    public void setIdvisto(Integer idvisto) {
        this.idvisto = idvisto;
    }

    public Archivo getArchivo() {
        return archivo;
    }

    public void setArchivo(Archivo archivo) {
        this.archivo = archivo;
    }

    public Usuario getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(Usuario usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
