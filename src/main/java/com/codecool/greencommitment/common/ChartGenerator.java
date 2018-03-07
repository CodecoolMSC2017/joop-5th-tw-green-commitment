package com.codecool.greencommitment.common;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.CategoryDataset;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChartGenerator {

    private final String filePath = System.getProperty("user.home");

    public BufferedImage lineChart(
            String sensorID,
            List<Element> measurements) throws IOException {
        String title = "Measurements";
        String categoryLabel = "Time";
        String valueLabel = "Test";
        int windowWidth = 640;
        int windowHeight = 480;
        System.out.println(1);
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
        System.out.println(2);
        for (Element measurement : measurements) {
            String value = measurement.getAttribute("value");
            String time = measurement.getAttribute("time");
            line_chart_dataset.addValue(Float.valueOf(value), "measurement", time);
        }
        System.out.println(3);
        JFreeChart lineChartObject = ChartFactory.createLineChart(
                title,
                categoryLabel,
                valueLabel,
                line_chart_dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        System.out.println(4);
        File lineChart = new File(filePath + "/" + sensorID + "LineChart.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, windowWidth, windowHeight);
        System.out.println("file generated");

        return ImageIO.read(lineChart);
    }
}
