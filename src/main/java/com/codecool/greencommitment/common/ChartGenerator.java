package com.codecool.greencommitment.common;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;

public class ChartGenerator {

    public void LineChart(String title, String categoryLabel, String valueLabel, String sensorName) throws IOException {
        DefaultCategoryDataset line_chart_dataset = new DefaultCategoryDataset();
        line_chart_dataset.addValue( 15 , "schools" , "1970" );
        line_chart_dataset.addValue( 30 , "schools" , "1980" );
        line_chart_dataset.addValue( 60 , "schools" , "1990" );
        line_chart_dataset.addValue( 120 , "schools" , "2000" );
        line_chart_dataset.addValue( 240 , "schools" , "2010" );
        line_chart_dataset.addValue( 300 , "schools" , "2014" );

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                title, categoryLabel, valueLabel, line_chart_dataset, PlotOrientation.VERTICAL,
                true,true,false);

        int width = 640;    /* Width of the image */
        int height = 480;   /* Height of the image */
        String path = new java.io.File(".").getCanonicalPath();
        File lineChart = new File( path + "/" + sensorName + " LineChart.jpeg" );
        ChartUtilities.saveChartAsJPEG(lineChart ,lineChartObject, width ,height);
    }
}
