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
            String clientId,
            String sensorID,
            List<Element> measurements) throws IOException {
        String title = "Measurements";
        String categoryLabel = "Time";
        String valueLabel = "Value";
        int windowWidth = 640;
        int windowHeight = 480;
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
        for (Element measurement : measurements) {
            String value = measurement.getAttribute("value");
            String time = measurement.getAttribute("time");
            line_chart_dataset.addValue(Float.valueOf(value), "measurement", time);
        }
        JFreeChart lineChartObject = ChartFactory.createLineChart(
                title,
                categoryLabel,
                valueLabel,
                line_chart_dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        File lineChart = new File(filePath + "/" + clientId + "_" + sensorID + "LineChart.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, windowWidth, windowHeight);

        return ImageIO.read(lineChart);
    }
}
