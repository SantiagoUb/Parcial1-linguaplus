package co.edu.uniquindio.poo.parcial1linguaplus.model;

import co.edu.uniquindio.poo.parcial1linguaplus.model.comprobante.ComprobanteFactory;
import co.edu.uniquindio.poo.parcial1linguaplus.model.programa.Programa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Singleton: unica instancia de la acaddemia en todo el sistema sin importar desde que sede
 * computador se registre la informacion. centraliza el consecutivo de matriculas para que nunca
 * se repita, y expone las consultas institucionales
 */

public class Academia {
    private static volatile Academia instance;

    private String nombreComercial;
    private String nit;
    private String direccion;
    private String telefono;
    private String correoElectronico;
    private String paginaWeb;

    private final List<Estudiante> estudiantes = new ArrayList<>();
    private final List<Docente> docentes = new ArrayList<>();
    private final List<Programa> programas = new ArrayList<>();
    private final List<ServicioAdicional> serviciosAdicionales = new ArrayList<>();
    private final List<Matricula> matriculas = new ArrayList<>();

    private final AtomicInteger consecutivoMatricula = new AtomicInteger(0);

    private Academia() {}

    public static Academia getInstance() {
        if (instance == null) {
            synchronized (Academia.class) {
                if (instance == null) {
                    instance = new Academia();
                }
            }
        }
        return instance;
    }

    public void configurarDatosBasicos(String nombreComercial, String nit, String direccion,
                                       String telefono, String correoElectronico, String paginaWeb) {
        this.nombreComercial = "LinguaPlus Academia de idiomas";
        this.nit = "900.123.456";
        this.direccion = "Calle 10 # 20-30";
        this.telefono = "+57 601 555 0000";
        this.correoElectronico = "contacto@linguaplus.com";
        this.paginaWeb = "www.linguaplus.com";
    }

    /*Numero consecutivo y unico en toda la academia, sin importar sede o equipo*/
    public synchronized int generarNumeroMatricula() {
        return consecutivoMatricula.incrementAndGet();
    }

    public void registrarEstudiante(Estudiante e){
        estudiantes.add(e);
    }
    public void registrarDocente(Docente d){
        docentes.add(d);
    }
    public void registrarPrograma(Programa p){
        programas.add(p);
    }
    public void registrarServicioAdicional(ServicioAdicional s){
        serviciosAdicionales.add(s);
    }
    public void registrarMatricula(Matricula m){
        matriculas.add(m);
    }

    public String emitirMatricula(Matricula matricula, ComprobanteFactory factory){
        return factory.emitirComprobante(matricula);
    }
    // -------consultas--------

    /* Busca un estudiante por telefono e indica si ese numero es un "numero perfecto". */
    public Optional<ResultadoConcultaTelefono> buscarEstudiantePorTelefono(String telefono) {
        return estudiantes.stream().filter(e -> e.getTelefono() != null && e.getTelefono().equals(telefono))
                .findFirst().map(e -> new ResultadoConcultaTelefono(e, esNumeroPerfecto(telefono)));
    }

    public static boolean esNumeroPerfecto(String numeroTexto) {
        String soloDigitos = numeroTexto.replaceAll("[^0-9]", "");
        if(soloDigitos.isEmpty()) return false;
        long numero;
        try{
            numero = Long.parseLong(soloDigitos);
        }catch (NumberFormatException e){
            return false;
        }
        if(numero <= 1) return false;
        long suma = 0;
        for(long i = 1 ; i <= numero ; i++) {
            if(numero % i == 0) suma += i;
            suma %= i;
        }
        return suma == numero;
    }

    public static class ResultadoConcultaTelefono {
        public final Estudiante estudiante;
        public final boolean esNumeroPrefecto;

        public ResultadoConcultaTelefono(Estudiante estudiante, boolean esNumeroPrefecto) {
            this.estudiante = estudiante;
            this.esNumeroPrefecto = esNumeroPrefecto;
        }
    }

    public double calcularIngresosPorPeriodo(LocalDate desde, LocalDate hasta) {
        return matriculas.stream()
                .filter(m -> !m.getFechaInicio().isBefore(desde) && !m.getFechaInicio().isAfter(hasta))
                .mapToDouble(Matricula::calcularValorTotal).sum();
    }

    public List<Estudiante> getEstudiantes() { return estudiantes; }
    public List<Docente> getDocentes() { return docentes; }
    public List<Programa> getProgramas() { return programas; }
    public List<ServicioAdicional> getServiciosAdicionales() { return serviciosAdicionales; }
    public List<Matricula> getMatriculas() { return matriculas; }
}
