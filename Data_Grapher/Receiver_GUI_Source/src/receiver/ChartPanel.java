package receiver;

import javafx.scene.chart.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ScrollPane;
import javax.swing.*;
import java.awt.GridLayout;
import javafx.scene.Scene;

/**
 * Contains charts that display temperature, altitude, and acceleration,
 * and provides methods to update them.
 */
@SuppressWarnings("serial")
public class ChartPanel extends JPanel{
	
	LineChart.Series<Number, Number> tempSeries;
	LineChart.Series<Number, Number> altiSeries;
	LineChart.Series<Number, Number> xSeries;
	LineChart.Series<Number, Number> ySeries;
	LineChart.Series<Number, Number> zSeries;
	
	LineChart<Number, Number> tempChart;
	LineChart<Number, Number> altiChart;
	LineChart<Number, Number> accelChart;
	
	/**
	 * Constructs and arranges the charts and their series. All chart 
	 * manipulations after construction are handled in the JavaFX thread. 
	 */
	public ChartPanel(){
		super(new GridLayout(3, 1));	
		
		// create containers for the charts
		JFXPanel tempPanel = new JFXPanel();
		JFXPanel altiPanel = new JFXPanel();
		JFXPanel accelPanel = new JFXPanel();
		
		// create charts
		tempChart = createTempChart();
		altiChart = createAltiChart();
		accelChart = createAccelChart();
		
		// create series
		tempSeries = new LineChart.Series<Number, Number>();
		altiSeries = new LineChart.Series<Number, Number>();
		xSeries = new LineChart.Series<Number, Number>();
		ySeries = new LineChart.Series<Number, Number>();
		zSeries = new LineChart.Series<Number, Number>();
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {	
				// make the panel containing the temperature chart
				ScrollPane tempPane = new ScrollPane();
				tempPane.setContent(tempChart);
				tempPane.setPrefSize(750, 265);
				tempPane.setMaxHeight(50);
				tempPanel.setScene(new Scene(tempPane));
				
				// make the panel containing the altitude chart
				ScrollPane altiPane = new ScrollPane();
				altiPane.setContent(altiChart);
				altiPane.setPrefSize(750, 265);
				altiPanel.setScene(new Scene(altiPane));
				
				// make the panel containing the acceleration chart
				ScrollPane accelPane = new ScrollPane();
				accelPane.setContent(accelChart);
				accelPane.setPrefSize(750, 265);
				accelPanel.setScene(new Scene(accelPane));
				
				// name series
				tempSeries.setName("Temperature");
				altiSeries.setName("Altitude");
				xSeries.setName("X");
				ySeries.setName("Y");
				zSeries.setName("Z");
				
				// add data series to charts
				altiChart.getData().add(altiSeries);
				tempChart.getData().add(tempSeries);
				accelChart.getData().add(xSeries);
				accelChart.getData().add(ySeries);
				accelChart.getData().add(zSeries);
				
			}
		});
		
		// add individual charts to chart panel
		add(tempPanel);
		add(altiPanel);
		add(accelPanel);
	}
	
	/**
	 * Updates the temperature chart with the given temperature and time.
	 * @param time x-axis
	 * @param temp y-axis
	 */
	public void updateTemp(Number time, Number temp){
		Platform.runLater(new Runnable(){
			public void run(){
				tempSeries.getData().add(new XYChart.Data<Number, Number>(time, temp));
			}
		});
	}

	/**
	 * Updates the altitude chart with the given altitude and time.
	 * @param time x-axis
	 * @param altitude y-axis
	 */
	public void updateAlti(Number time, Number altitude){
		Platform.runLater(new Runnable(){
			public void run(){
				altiSeries.getData().add(new XYChart.Data<Number, Number>(time, altitude));
			}
		});
	}

	/**
	 * Updates the acceleration chart with the given acceleration and time
	 * @param time x-axis
	 * @param xAcc y-axis
	 */
	public void updateX(Number time, Number xAcc){
		Platform.runLater(new Runnable(){
			public void run(){
				xSeries.getData().add(new XYChart.Data<Number, Number>(time, xAcc));
			}
		});
	}

	/**
	 * Updates the acceleration chart with the given acceleration and time
	 * @param time x-axis
	 * @param yAcc y-axis
	 */
	public void updateY(Number time, Number yAcc){
		Platform.runLater(new Runnable(){
			public void run(){
				ySeries.getData().add(new XYChart.Data<Number, Number>(time, yAcc));
			}
		});
	}
	
	/**
	 * Updates the acceleration chart with the given acceleration and time
	 * @param time x-axis
	 * @param zAcc y-axis
	 */
	public void updateZ(Number time, Number zAcc){
		Platform.runLater(new Runnable(){
			public void run(){
				zSeries.getData().add(new XYChart.Data<Number, Number>(time, zAcc));
			}
		});
	}
	
	private LineChart<Number, Number> createTempChart() {
		NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel("Time");
		xAxis.setAutoRanging(true);
		
		NumberAxis yAxis = new NumberAxis(50, 100, 10);
		yAxis.setLabel("Temperature");
		yAxis.setAutoRanging(true);
		
		LineChart<Number, Number> chart = new LineChart<Number, Number>(xAxis, yAxis);
		chart.setAnimated(true);
		chart.setMaxHeight(250);
		chart.setMinWidth(800);
		return chart;
		
	}
	
	private LineChart<Number, Number> createAltiChart() {
		NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel("Time");
		xAxis.setAutoRanging(true);
		
		NumberAxis yAxis = new NumberAxis(0, 200, 20);
		yAxis.setLabel("Altitude");
		yAxis.setAutoRanging(true);
		
		LineChart<Number, Number> chart = new LineChart<Number, Number>(xAxis, yAxis);
		chart.setMaxHeight(250);
		chart.setMinWidth(800);
		return chart;
		
	}
	
	private LineChart<Number, Number> createAccelChart() {
		NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel("Time");
		xAxis.setAutoRanging(true);
		
		NumberAxis yAxis = new NumberAxis(-1, 20, 2);
		yAxis.setLabel("Acceleration");
		yAxis.setAutoRanging(true);
		
		LineChart<Number, Number> chart = new LineChart<Number, Number>(xAxis, yAxis);
		chart.setMaxHeight(250);
		chart.setMinWidth(800);
		return chart;
		
	}
}
