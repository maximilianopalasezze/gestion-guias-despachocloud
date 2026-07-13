package cl.duoc.consumidorguias.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "guias_procesadas_mq")
public class GuiaProcesada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_guia_original", nullable = false)
    private Long idGuiaOriginal;

    @Column(name = "numero_guia", nullable = false, length = 50)
    private String numeroGuia;

    @Column(name = "correlation_id", nullable = false, unique = true, length = 100)
    private String correlationId;

    @Column(nullable = false, length = 150)
    private String transportista;

    @Column(nullable = false, length = 150)
    private String destinatario;

    @Column(name = "direccion_destino", nullable = false, length = 250)
    private String direccionDestino;

    @Column(name = "descripcion_pedido", nullable = false, length = 500)
    private String descripcionPedido;

    @Column(name = "peso_kg", nullable = false)
    private BigDecimal pesoKg;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDate fechaGeneracion;

    @Column(name = "fecha_despacho", nullable = false)
    private LocalDate fechaDespacho;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "ruta_s3")
    private String rutaS3;

    @Column(name = "fecha_mensaje")
    private LocalDateTime fechaMensaje;

    @Column(name = "fecha_procesamiento", nullable = false)
    private LocalDateTime fechaProcesamiento;

    @Column(name = "estado_procesamiento", nullable = false, length = 50)
    private String estadoProcesamiento;

    public GuiaProcesada() {
    }

    public GuiaProcesada(
            Long idGuiaOriginal,
            String numeroGuia,
            String correlationId,
            String transportista,
            String destinatario,
            String direccionDestino,
            String descripcionPedido,
            BigDecimal pesoKg,
            LocalDate fechaGeneracion,
            LocalDate fechaDespacho,
            String nombreArchivo,
            String rutaS3,
            LocalDateTime fechaMensaje,
            String estadoProcesamiento
    ) {
        this.idGuiaOriginal = idGuiaOriginal;
        this.numeroGuia = numeroGuia;
        this.correlationId = correlationId;
        this.transportista = transportista;
        this.destinatario = destinatario;
        this.direccionDestino = direccionDestino;
        this.descripcionPedido = descripcionPedido;
        this.pesoKg = pesoKg;
        this.fechaGeneracion = fechaGeneracion;
        this.fechaDespacho = fechaDespacho;
        this.nombreArchivo = nombreArchivo;
        this.rutaS3 = rutaS3;
        this.fechaMensaje = fechaMensaje;
        this.fechaProcesamiento = LocalDateTime.now();
        this.estadoProcesamiento = estadoProcesamiento;
    }

    public Long getId() {
        return id;
    }

    public Long getIdGuiaOriginal() {
        return idGuiaOriginal;
    }

    public void setIdGuiaOriginal(Long idGuiaOriginal) {
        this.idGuiaOriginal = idGuiaOriginal;
    }

    public String getNumeroGuia() {
        return numeroGuia;
    }

    public void setNumeroGuia(String numeroGuia) {
        this.numeroGuia = numeroGuia;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getTransportista() {
        return transportista;
    }

    public void setTransportista(String transportista) {
        this.transportista = transportista;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public void setDireccionDestino(String direccionDestino) {
        this.direccionDestino = direccionDestino;
    }

    public String getDescripcionPedido() {
        return descripcionPedido;
    }

    public void setDescripcionPedido(String descripcionPedido) {
        this.descripcionPedido = descripcionPedido;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public LocalDate getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(LocalDate fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getRutaS3() {
        return rutaS3;
    }

    public void setRutaS3(String rutaS3) {
        this.rutaS3 = rutaS3;
    }

    public LocalDateTime getFechaMensaje() {
        return fechaMensaje;
    }

    public void setFechaMensaje(LocalDateTime fechaMensaje) {
        this.fechaMensaje = fechaMensaje;
    }

    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }

    public String getEstadoProcesamiento() {
        return estadoProcesamiento;
    }

    public void setEstadoProcesamiento(String estadoProcesamiento) {
        this.estadoProcesamiento = estadoProcesamiento;
    }
}