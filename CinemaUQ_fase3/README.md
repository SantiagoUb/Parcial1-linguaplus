# CinemaUQ – Fase 1: núcleo del dominio (sin UI)
Compilar: `javac -d out $(find src -name '*.java')` · Ejecutar demo: `java -cp out cinemauq.Main`

## Decisiones de diseño del grupo (NO vienen del enunciado)
- Dinero en pesos enteros (long, COP).
- Puntos: 1 por cada $1.000 pagados en dinero; 1 punto = $20 al redimir. Compras pagadas con puntos NO acumulan.
- Pago con puntos o con dinero, nunca mixto. Promoción: una por compra, solo con dinero, porcentaje sobre subtotal.
- Asiento pasa a ocupado en el momento de confirmar el pago (reserva atómica todo-o-nada); sin reservas temporales.
- Pago fallido: la compra queda PENDIENTE, sin asientos reservados ni saldo descontado.
- Reembolso: se anulan los puntos ganados por esa compra; si se pagó con puntos, se restauran a sus lotes originales.
- Cancelación con varias funciones: aplica el tiempo de la función más próxima.
- Compras solo de confitería: no cancelables.
- Función cancelada por el cine: se cancela y reembolsa 100 % TODA compra que la incluya.

## Patrones ya implementados (7 de 10)
Singleton (ConfiguracionCine) · Builder (Compra.Builder) · Composite (ItemMenu/Producto/Combo) ·
Facade (ServicioCompra) · Strategy (PoliticaReembolso) · State (EstadoCompra/Estados) · Observer (ObservadorCompra)

# Fase 2: patrones restantes, reportes, persistencia y datos demo
Ejecutar: `java -cp out cinemauq.MainFase2` (genera TXT/PDF de los 7 reportes en una carpeta temporal).

## Patrones nuevos (ahora 10)
- Factory (Simple Factory estática): `FabricaReportes.crear(TipoReporte)`.
- Adapter: `AdaptadorPDF` adapta `LibreriaPdfLegada` (componente con otra interfaz) a `GeneradorReporte`.
- Decorator: `DecoradorItem` -> `TamanoGrande`, `ConExtra`. Los combos NO admiten decoradores (precio/composición cerrados).

## Decisiones adicionales
- Persistencia: serialización Java del grafo completo (`DatosCine`), escritura atómica (tmp + move). Guardado automático tras cada compra/reembolso (Observer) y tras cada recarga.
- Reportes y estadísticas: solo administrador (validado en el servicio). Los reportes de ventas cuentan en dinero las compras PAGADAS; las pagadas con puntos se listan pero no suman dinero.
- Datos demo: reloj simulado (`ConfiguracionCine.fijarReloj`) para generar historia pasada; Random(42) => reproducible. Usuarios: admin@cinemauq.co/admin123, cliente1..5@cinemauq.co/clave123.

# Fase 3: gestión del administrador + interfaz JavaFX (MVC)
Ejecutar la app: `mvn javafx:run` (Java 21 + Maven; descarga JavaFX 21). Los datos se guardan en `~/.cinemauq/cinemauq.dat`
(si venías de la Fase 2, borra esa carpeta: el formato de `Sala` cambió). Prueba de reglas sin UI: `cinemauq.MainFase3`.

## Arquitectura MVC
- Modelo: `modelo/`, `servicio/`, `reporte/`, `persistencia/` (sin dependencias de JavaFX).
- Vista: `vista/` (App, Ui, Sesion, Pantalla). Controlador: `controlador/` (un controlador por pantalla; construye su vista con código, sin FXML).
- Los controladores solo leen campos, llaman al servicio y muestran el resultado/error (`Ui.accion`). Ninguna regla de negocio está en ellos.

## Reglas de gestión (decisiones del grupo, en ServicioCatalogo)
- Película: no se modifica si alguna de sus funciones tiene compras; no se elimina si tiene funciones. Título único.
- Sala: puede renombrarse siempre; sus asientos (filas x columnas) solo cambian si no tiene funciones; no se elimina con funciones.
- Función: debe ser a futuro; no puede solaparse en la misma sala (duración de la película + 15 min de aseo); no se modifica ni elimina con compras (se cancela, lo que reembolsa).
- Producto: no se modifica/elimina si pertenece a un combo; el historial conserva el precio con que se vendió. Combo: mínimo 2 productos; los complementos (Decorator) no aplican a combos.
- Promoción: 1-100 %, vigencia válida, código único.
- Registro: correo único. Todas las operaciones de catálogo, recargas, reportes y estadísticas validan rol ADMINISTRADOR en el servicio.
