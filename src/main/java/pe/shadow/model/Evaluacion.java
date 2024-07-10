package pe.shadow.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
public class Evaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idevaluacion")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "idcuestionario")
    private Cuestionario cuestionario;

    @ManyToOne
    @JoinColumn(name = "id_usuario_creacion")
    private Usuario usuarioCreacion;

    private int puntaje;

    private LocalDateTime fechaCreacion;

    @PrePersist
    void prePersistFechaCreacion()
    {
        fechaCreacion = LocalDateTime.now();
    }

    public Evaluacion() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cuestionario getCuestionario() {
        return cuestionario;
    }

    public void setCuestionario(Cuestionario cuestionario) {
        this.cuestionario = cuestionario;
    }

    public Usuario getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(Usuario usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
