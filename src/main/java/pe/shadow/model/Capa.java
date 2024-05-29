package pe.shadow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
public class Capa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idcapa")
    private Integer id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    @NotBlank
    private String instructor;

    @NotBlank
    public enum Estado{
        BORRADOR,
        PUBLICO,
        CURSANDO,
        COMPLETADO,
        CANCELADO
    }

    @Enumerated(EnumType.STRING)
    private Estado estado;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fecIni;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fecFin;

    @Min(1)
    @NotNull
    private Integer vacante;

    private String rutaImagen;

    @Transient
    private MultipartFile imagen;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @PrePersist
    void prePersistFechaCreacion()
    {
        fechaCreacion = LocalDateTime.now();
    }
    @PreUpdate
    void preUdateFechaActualizacion()
    {
        fechaActualizacion = LocalDateTime.now();
    }

    public Capa(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Date getFecIni() {
        return fecIni;
    }

    public void setFecIni(Date fecIni) {
        this.fecIni = fecIni;
    }

    public Date getFecFin() {
        return fecFin;
    }

    public void setFecFin(Date fecFin) {
        this.fecFin = fecFin;
    }

    public Integer getVacante() {
        return vacante;
    }

    public void setVacante(Integer vacante) {
        this.vacante = vacante;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public MultipartFile getImagen() {
        return imagen;
    }

    public void setImagen(MultipartFile imagen) {
        this.imagen = imagen;
    }


}