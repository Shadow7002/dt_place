package pe.shadow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Usuario {

    public interface Registro {}
    public interface EdicionAdmin {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idusuario")
    private Integer id;

    @NotBlank(groups = {Registro.class, EdicionAdmin.class})
    private String nombres;

    @NotBlank(groups = {Registro.class, EdicionAdmin.class})
    private String apellidos;

    @Column(name = "nom_completo")
    private String nombreCompleto;

    @NotBlank(groups = {Registro.class, EdicionAdmin.class})
    @Email(groups = {Registro.class, EdicionAdmin.class})
    private String email;

    private String password;

    @Transient
    @NotBlank(groups = Registro.class)
    private String password1;

    @Transient
    @NotBlank(groups = Registro.class)
    private String password2;

    public enum Rol{
        ADMIN,
        USUARIO
    }

    @Enumerated(EnumType.STRING)
    private Rol rol;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @PrePersist
    void prePersist(){
        asignarNombreCompleto();
        fechaCreacion = LocalDateTime.now();
    }
    @PreUpdate
    void preUdate()
    {
        asignarNombreCompleto();
        fechaActualizacion = LocalDateTime.now();
    }


    void asignarNombreCompleto() {
        nombreCompleto = nombres + ' ' + apellidos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
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

    public Usuario() {
    }
}
