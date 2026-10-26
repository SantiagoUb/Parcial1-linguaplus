package cinemauq.reporte;
import java.nio.charset.StandardCharsets;
import java.util.*;
/**
 * ADAPTEE: simula un componente externo de PDF con una interfaz distinta a la de CinemaUQ
 * (nuevaPagina / escribirLinea / renderizar). Escribe un PDF 1.4 mínimo, fuente Courier, A4.
 * Podría reemplazarse por OpenPDF/PDFBox sin tocar el resto del sistema: solo se cambia el adaptador.
 */
public class LibreriaPdfLegada {
    private static final int LINEAS_POR_PAGINA = 80;
    private final List<List<String>> paginas = new ArrayList<>();
    public LibreriaPdfLegada() { nuevaPagina(); }
    public void nuevaPagina() { paginas.add(new ArrayList<>()); }
    public void escribirLinea(String t) { if (paginas.get(paginas.size() - 1).size() >= LINEAS_POR_PAGINA) nuevaPagina(); paginas.get(paginas.size() - 1).add(t); }
    private static String esc(String s) {
        StringBuilder b = new StringBuilder();
        for (char c : s.toCharArray()) { if (c == '(' || c == ')' || c == '\\') b.append('\\').append(c); else b.append(c > 255 ? '?' : c); }
        return b.toString();
    }
    public byte[] renderizar() {
        StringBuilder sb = new StringBuilder("%PDF-1.4\n"); List<Integer> off = new ArrayList<>(); int n = paginas.size();
        StringBuilder kids = new StringBuilder(); for (int i = 0; i < n; i++) kids.append(4 + 2 * i).append(" 0 R ");
        String[] fijos = {"<< /Type /Catalog /Pages 2 0 R >>", "<< /Type /Pages /Kids [" + kids + "] /Count " + n + " >>",
                "<< /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >>"};
        for (int i = 0; i < 3; i++) { off.add(sb.length()); sb.append(i + 1).append(" 0 obj\n").append(fijos[i]).append("\nendobj\n"); }
        for (int i = 0; i < n; i++) {
            StringBuilder c = new StringBuilder("BT /F1 7 Tf 9 TL 36 806 Td\n");
            for (String l : paginas.get(i)) c.append('(').append(esc(l)).append(") Tj T*\n");
            c.append("ET");
            off.add(sb.length()); sb.append(4 + 2 * i).append(" 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 3 0 R >> >> /Contents ").append(5 + 2 * i).append(" 0 R >>\nendobj\n");
            off.add(sb.length()); sb.append(5 + 2 * i).append(" 0 obj\n<< /Length ").append(c.length()).append(" >>\nstream\n").append(c).append("\nendstream\nendobj\n");
        }
        int xref = sb.length(); sb.append("xref\n0 ").append(off.size() + 1).append("\n0000000000 65535 f \n");
        for (int o : off) sb.append(String.format("%010d 00000 n \n", o));
        sb.append("trailer\n<< /Size ").append(off.size() + 1).append(" /Root 1 0 R >>\nstartxref\n").append(xref).append("\n%%EOF\n");
        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }
}
