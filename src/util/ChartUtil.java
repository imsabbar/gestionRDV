package util;

import java.awt.Color;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import dao.AppointmentDAO;
import model.Appointment;

public class ChartUtil {

    private static final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public static JPanel createAppointmentsPerDoctorChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> doctorCounts = new HashMap<>();

        for (Appointment appt : appointmentDAO.findAll()) {
            String doctorName = appt.getDoctor().getFullName();
            doctorCounts.put(doctorName, doctorCounts.getOrDefault(doctorName, 0) + 1);
        }

        doctorCounts.forEach((name, count) -> dataset.addValue(count, "Médecins", name));

        JFreeChart chart = ChartFactory.createBarChart(
                "Rendez-vous par Médecin",
                "Médecin",
                "Nombre de Rendez-vous",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        customizeBarChart(chart);
        return new ChartPanel(chart);
    }

    public static JPanel createAppointmentsByStatusChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> statusCounts = new HashMap<>();

        for (Appointment appt : appointmentDAO.findAll()) {
            String status = appt.getStatus();
            statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
        }

        statusCounts.forEach((status, count) -> dataset.addValue(count, "Statut", status));

        JFreeChart chart = ChartFactory.createBarChart(
                "Rendez-vous par Statut",
                "Statut",
                "Nombre",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        customizeBarChart(chart);
        return new ChartPanel(chart);
    }

    public static JPanel createAppointmentsOverTimeChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> dateCounts = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Appointment appt : appointmentDAO.findAll()) {
            String date = appt.getDateTime().toLocalDate().format(formatter);
            dateCounts.put(date, dateCounts.getOrDefault(date, 0) + 1);
        }

        dateCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> dataset.addValue(e.getValue(), "RDV", e.getKey()));

        JFreeChart chart = ChartFactory.createLineChart(
                "Rendez-vous au Fil du Temps",
                "Date",
                "Nombre de RDV",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        customizeLineChart(chart);
        return new ChartPanel(chart);
    }

    public static JPanel createAppointmentsByStatusPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> statusCounts = new HashMap<>();

        for (Appointment appt : appointmentDAO.findAll()) {
            String status = appt.getStatus();
            statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
        }

        statusCounts.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(
                "Distribution des Rendez-vous par Statut",
                dataset,
                true,  // legend
                true,  // tooltips
                false  // URLs
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.setNoDataMessage("Aucune donnée disponible");
        plot.setCircular(true);
        plot.setLabelGap(0.02);
        plot.setBackgroundPaint(Color.WHITE);

        return new ChartPanel(chart);
    }


    public static JPanel createAppointmentsBySpecialtyChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> specialtyCounts = new HashMap<>();

        for (Appointment appt : appointmentDAO.findAll()) {
            String specialty = appt.getDoctor().getSpecialization();
            specialtyCounts.put(specialty, specialtyCounts.getOrDefault(specialty, 0) + 1);
        }

        specialtyCounts.forEach((specialty, count) -> dataset.addValue(count, "Spécialité", specialty));

        JFreeChart chart = ChartFactory.createBarChart(
                "Rendez-vous par Spécialité",
                "Spécialité",
                "Nombre de Rendez-vous",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        customizeBarChart(chart);
        return new ChartPanel(chart);
    }



    private static void customizeLineChart(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 10));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 10));

        if (plot.getRenderer() instanceof LineAndShapeRenderer) {
            LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
            renderer.setSeriesShapesVisible(0, true);
            renderer.setSeriesPaint(0, new Color(41, 128, 185));
        }
    }

    private static void customizeBarChart(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 10));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("Arial", Font.PLAIN, 10));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(255, 99, 132));
    }
}
