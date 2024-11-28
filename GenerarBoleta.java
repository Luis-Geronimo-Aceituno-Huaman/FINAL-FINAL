/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PROYECTO_GRUPO_3;
  
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenerarBoleta {
    public void crearBoleta(Contenedores y, Usuario usuario, GestorDeUsuarios gestor, int llenados, Producto_Reciclable[] detallesEnvases) {
        try {
            // Tamaño de ticket reducido (ancho y alto)
            Rectangle ticketSize = new Rectangle(215, 480); // Reducción del tamaño
            Document document = new Document(ticketSize, 10, 10, 10, 10);
            
            PdfWriter.getInstance(document, new FileOutputStream("boleta_ticket.pdf"));
            document.open();

            // Fuente básica
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD); // Reducido aún más el tamaño
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 7); // Reducido
            Font smallFont = new Font(Font.FontFamily.HELVETICA, 5); // Fuente más pequeña para TOTAL y SALDO

            // Títulos principales
            Paragraph title = new Paragraph("RECICLA RIQUEZA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("BANCO DE RECICLAJE CON RECOMPENSA MONETARIA", subTitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            // Logo - Aumentado el tamaño
            Image logo = Image.getInstance("C:\\Users\\jeana\\Downloads\\RECICLA-removebg-preview (1) (1).png");
            logo.scaleToFit(150, 100); // Tamaño adecuado para el logo
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
            
            // Línea encima de "BOLETA ELECTRÓNICA N°"
            LineSeparator line = new LineSeparator();
            line.setLineWidth(0.5f);  // Ajusta el grosor de la línea
            line.setLineColor(BaseColor.BLACK); // Color de la línea
            document.add(line);
            
            // Número de boleta
            Paragraph boletaNum = new Paragraph("BOLETA ELECTRÓNICA N° " + llenados, subTitleFont);
            boletaNum.setAlignment(Element.ALIGN_CENTER);
            document.add(boletaNum);

            // Distrito
            Paragraph distrito = new Paragraph("DISTRITO DE " + y.getUbicacion().toUpperCase(), subTitleFont);
            distrito.setAlignment(Element.ALIGN_CENTER);
            distrito.setSpacingAfter(5);
            // Agregar espacio después del distrito
            document.add(distrito);
            document.add(line); // Usamos la misma línea para debajo del distrito
            
            document.add(new Paragraph("\n")); // Espaciado

            // Nombre del usuario centrado
            Paragraph nombreUsuario = new Paragraph(usuario.getNombres().toUpperCase() + " " + usuario.getApellidos().toUpperCase(), normalFont);
            nombreUsuario.setAlignment(Element.ALIGN_CENTER);
            document.add(nombreUsuario);

            // Correo del usuario (más pequeño, debajo del nombre)
            Paragraph correoUsuario = new Paragraph(usuario.getEmail(), smallFont);
            correoUsuario.setAlignment(Element.ALIGN_CENTER);
            document.add(correoUsuario);
            
            // Fecha y hora separada
            String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());

            // Subtítulos en negrita y valores en fuente normal
            Paragraph fechaHora = new Paragraph();
            fechaHora.add(new Phrase("Fecha: ", subTitleFont)); 
            fechaHora.add(new Phrase(" " + fecha, normalFont)); 
            fechaHora.add(new Phrase("                     "));
            fechaHora.add(new Phrase("Hora: ", subTitleFont));
            fechaHora.add(new Phrase(" " + hora, normalFont));

            // Alinear el párrafo al centro
            fechaHora.setAlignment(Element.ALIGN_CENTER);

            // Añadir al documento
            document.add(fechaHora);
            document.add(new Paragraph("\n")); // Espaciado

            // Tabla de envases
            PdfPTable table = new PdfPTable(4); // Cantidad, Descripción, Precio Unitario, Monto Total
            table.setWidthPercentage(100);
            table.setWidths(new int[]{24, 45, 22, 30}); // Ajuste de ancho de columnas

            // Encabezados con tamaño de fuente más pequeño
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 5, Font.BOLD); // Fuente más pequeña para los encabezados
            addTableCell(table, "CANTIDAD", headerFont, Element.ALIGN_CENTER);
            addTableCell(table, "DESCRIPCIÓN", headerFont, Element.ALIGN_CENTER);
            addTableCell(table, "P. UNIT", headerFont, Element.ALIGN_CENTER);
            addTableCell(table, "MONTO TOTAL", headerFont, Element.ALIGN_CENTER);

            // Sumar los montos
            Font tableFont = new Font(Font.FontFamily.HELVETICA, 5);
            double totalMonto = 0;
            String[] tipos = {"Vidrio", "Plástico", "Lata"};
            for (int i = 0; i < detallesEnvases.length; i++) {
                Producto_Reciclable producto = detallesEnvases[i];
                if (producto != null) {
                    addTableCell(table, String.valueOf(producto.getCantidad()), tableFont, Element.ALIGN_CENTER); // Cantidad
                    addTableCell(table, "Envases de " + tipos[i], tableFont, Element.ALIGN_CENTER); // Descripción
                    addTableCell(table,"S/. "+String.format("%.2f", producto.establecerValor()), tableFont, Element.ALIGN_CENTER); // Precio unitario
                    double monto = producto.calcularValor();
                    totalMonto += monto; // Sumamos el monto total
                    addTableCell(table,"S/. "+String.format("%.2f", monto), tableFont, Element.ALIGN_CENTER); // Monto total
                }
            }

            document.add(table);

            document.add(new Paragraph("\n")); // Espaciado

            // Creamos una tabla con dos columnas para TOTAL y SALDO
            PdfPTable bottomTable = new PdfPTable(2); // 2 columnas
            bottomTable.setWidthPercentage(100);
            bottomTable.setWidths(new int[]{50, 50}); // Cada columna tiene un 50% de ancho
            
            // Columna 1: Total
            PdfPCell cell1 = new PdfPCell(new Phrase("TOTAL", headerFont));
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell1.setFixedHeight(10); // Ajustar altura de la fila
            cell1.setBorderWidth(0.5f);
            bottomTable.addCell(cell1);
            PdfPCell cell2 = new PdfPCell(new Phrase("S/. "+String.format("%.2f", totalMonto), smallFont));
            cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell2.setFixedHeight(10); // Ajustar altura de la fila
            cell2.setBorderWidth(0.5f);
            bottomTable.addCell(cell2);

            // Columna 2: Saldo
            PdfPCell cell3 = new PdfPCell(new Phrase("SALDO", headerFont));
            cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell3.setFixedHeight(10); // Ajustar altura de la fila
            cell3.setBorderWidth(0.5f);
            bottomTable.addCell(cell3);
            PdfPCell cell4 = new PdfPCell(new Phrase("S/. "+String.format("%.2f", usuario.getSaldo()), smallFont));
            cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell4.setFixedHeight(10); // Ajustar altura de la fila
            cell4.setBorderWidth(0.5f);
            bottomTable.addCell(cell4);

            document.add(bottomTable);

            document.add(new Paragraph("\n")); // Espaciado

            // Mensaje en negritas
            Paragraph mensajeGestor = new Paragraph(
                "Usted ha sido atendido por el siguiente gestor, comuníquese ante cualquier consulta:", 
                subTitleFont // Usamos la fuente de subtítulo para resaltar en negritas
            );
            mensajeGestor.setAlignment(Element.ALIGN_CENTER);
            mensajeGestor.setSpacingAfter(5);
            document.add(mensajeGestor);

            // Datos del gestor organizados
            Paragraph datosGestor = new Paragraph(gestor.getNombres().toUpperCase() + " " + gestor.getApellidos().toUpperCase(),normalFont);
            datosGestor.setAlignment(Element.ALIGN_CENTER); // Alineación a la izquierda
            datosGestor.setSpacingAfter(5);
            document.add(datosGestor);
            
            Paragraph TelefonoCorreo = new Paragraph(gestor.getTelefono() + "  -  " + gestor.getEmail(),smallFont);
            TelefonoCorreo.setAlignment(Element.ALIGN_CENTER);
            document.add(TelefonoCorreo);
            document.close();
            System.out.println("Boleta generada exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTableCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBorderWidth(0.5f);
        cell.setPadding(5);
        table.addCell(cell);
    }
}