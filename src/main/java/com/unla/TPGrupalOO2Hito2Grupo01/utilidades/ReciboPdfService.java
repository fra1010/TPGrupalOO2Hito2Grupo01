package com.unla.TPGrupalOO2Hito2Grupo01.utilidades;
import com.unla.TPGrupalOO2Hito2Grupo01.dtos.ReciboDetalladoEmpleadoDTO;
import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

// import com.example.festival.dto.ReciboDetalladoEmpleadoDTO;  <- ajustar

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
@Service
public class ReciboPdfService {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Font TITULO    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
    private static final Font EMPLEADO  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
    private static final Font NEGRITA   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font NORMAL    = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font APAGADO   = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, Color.GRAY);

    private static final Color FONDO_ENCABEZADO = new Color(240, 240, 240);
    private static final Color FONDO_TOTAL      = new Color(225, 240, 225);
    private static final Color COLOR_BORDE      = new Color(210, 210, 210);

    /**
     * Genera el PDF del recibo y lo devuelve como bytes.
     * Replica la estructura de mi-sueldo.html.
     */
    public byte[] generarRecibo(ReciboDetalladoEmpleadoDTO r) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, out);
            doc.open();

            // Encabezado: título + unidad de venta
            doc.add(parrafo("Recibo de sueldo", TITULO, 2));
            doc.add(parrafo(r.getUnidadVenta(), APAGADO, 16));

            // Empleado, rol y categoría (la categoría es null en cajeros)
            doc.add(parrafo(r.getApellido() + ", " + r.getNombre(), EMPLEADO, 2));
            String rol = r.getCategoria() != null ? r.getRol() + " - " + r.getCategoria() : r.getRol();
            doc.add(parrafo(rol, APAGADO, 12));

            // Duración del festival
            Paragraph duracion = new Paragraph();
            duracion.add(new Chunk("Duración del festival: ", NEGRITA));
            duracion.add(new Chunk(r.getFechaDesde().format(FECHA) + " al "
                    + r.getFechaHasta().format(FECHA)
                    + " (" + r.getDias() + (r.getDias() == 1 ? " día)" : " días)"), NORMAL));
            duracion.setSpacingAfter(6);
            doc.add(duracion);

            // Aviso de días restantes (igual que la alerta de la vista)
            doc.add(parrafo(avisoDiasRestantes(r), APAGADO, 14));

            doc.add(tablaRecibo(r));

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el PDF del recibo", e);
        }
    }

    private PdfPTable tablaRecibo(ReciboDetalladoEmpleadoDTO r) {
        PdfPTable t = new PdfPTable(new float[]{7f, 3f});
        t.setWidthPercentage(100);

        // Encabezado de la tabla
        t.addCell(celda("Concepto", NEGRITA, Element.ALIGN_LEFT, 1, FONDO_ENCABEZADO));
        t.addCell(celda("Importe", NEGRITA, Element.ALIGN_RIGHT, 1, FONDO_ENCABEZADO));

        // Sueldo base (se paga una sola vez)
        t.addCell(izq("Sueldo base"));
        t.addCell(der(importe(r.getSueldoBase())));

        // Plus (de categoría en cocineros, por antigüedad en cajeros)
        t.addCell(izq(conceptoPlus(r)));
        t.addCell(der(importe(r.getTotalPlus())));

        // Valor de referencia (bruto)
        t.addCell(celda("Valor de referencia", NEGRITA, Element.ALIGN_LEFT, 1, null));
        t.addCell(celda(importe(r.getBruto()), NEGRITA, Element.ALIGN_RIGHT, 1, null));

        // Factor de liquidación
        String quincenas = r.getQuincenas() + (r.getQuincenas() == 1 ? " quincena" : " quincenas");
        t.addCell(celda("Factor de liquidación (" + quincenas + ")", APAGADO, Element.ALIGN_LEFT, 1, null));
        t.addCell(der(numero(r.getMeses())));

        // Total a pagar
        t.addCell(celda("Total a pagar (valor de referencia x factor)", NEGRITA, Element.ALIGN_LEFT, 1, FONDO_TOTAL));
        t.addCell(celda(importe(r.getTotal()), NEGRITA, Element.ALIGN_RIGHT, 1, FONDO_TOTAL));

        return t;
    }

    // Cajero: "Plus por antigüedad (3 años x $ 5.000,00)". Cocinero: solo la descripción.
    private String conceptoPlus(ReciboDetalladoEmpleadoDTO r) {
        if ("CAJERO".equals(r.getRol()) && r.getAntiguedad() != null) {
            return r.getDescripcionPlus() + " (" + r.getAntiguedad()
                    + (r.getAntiguedad() == 1 ? " año" : " años")
                    + " x " + importe(r.getValorPlus()) + ")";
        }
        return r.getDescripcionPlus();
    }

    private String avisoDiasRestantes(ReciboDetalladoEmpleadoDTO r) {
        long dias = r.getDiasRestantes();
        if (dias > 0) {
            return (dias == 1 ? "Falta 1 día" : "Faltan " + dias + " días")
                    + " para cobrar tu sueldo (el festival termina el "
                    + r.getFechaHasta().format(FECHA) + ").";
        }
        return "El festival ya terminó: ya podés cobrar tu sueldo.";
    }

    // ---------- helpers de armado ----------

    private static Paragraph parrafo(String texto, Font font, float espacioDespues) {
        Paragraph p = new Paragraph(texto, font);
        p.setSpacingAfter(espacioDespues);
        return p;
    }

    private static PdfPCell izq(String texto) {
        return celda(texto, NORMAL, Element.ALIGN_LEFT, 1, null);
    }

    private static PdfPCell der(String texto) {
        return celda(texto, NORMAL, Element.ALIGN_RIGHT, 1, null);
    }

    private static PdfPCell celda(String texto, Font font, int alineacion, int colspan, Color fondo) {
        PdfPCell c = new PdfPCell(new Phrase(texto != null ? texto : "", font));
        c.setHorizontalAlignment(alineacion);
        c.setColspan(colspan);
        c.setPadding(6);
        c.setBorder(Rectangle.BOTTOM);
        c.setBorderColor(COLOR_BORDE);
        if (fondo != null) {
            c.setBackgroundColor(fondo);
        }
        return c;
    }

    // ---------- formato de números: punto para miles, coma para decimales ----------
    // (DecimalFormat no es thread-safe, por eso se crea uno nuevo en cada llamada)

    private static String numero(BigDecimal valor) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.ROOT);
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');
        return new DecimalFormat("#,##0.00", simbolos)
                .format(valor != null ? valor : BigDecimal.ZERO);
    }

    private static String importe(BigDecimal valor) {
        return "$ " + numero(valor);
    }
}
