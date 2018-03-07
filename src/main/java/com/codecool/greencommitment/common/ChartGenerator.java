package com.codecool.greencommitment.common;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChartGenerator {

    private String filePath = System.getProperty("user.home");

    public void LineChart(String title, String categoryLabel, String valueLabel, String sensorID, List<Element> measurements) throws IOException {
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();

        for (Element measurement : measurements) {
            String value = measurement.getAttribute("value");
            String time = measurement.getAttribute("time");
            line_chart_dataset.addValue(Integer.parseInt(value), "measurement", time);
        }


        JFreeChart lineChartObject = ChartFactory.createLineChart(
                title, categoryLabel, valueLabel, line_chart_dataset, PlotOrientation.VERTICAL,
                true, true, false);

        int width = 640;    /* Width of the image */
        int height = 480;   /* Height of the image */
        File lineChart = new File(filePath + "/" + sensorID + "LineChart.jpeg");
        ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
    }
}
