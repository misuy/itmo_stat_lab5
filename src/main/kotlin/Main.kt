import org.jfree.chart.*
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import javax.swing.JFrame


val DEFAULT_FRAME_SIZE: Dimension = Dimension(1000, 1000);


fun main(args: Array<String>) {
    val elements: List<Double> = listOf(-0.76, -1.14, -0.55, 1.07, -0.62, -0.14, 0.21, -1.45, -1.31, 1.45, 0.64, 0.24, -0.21, 1.46, -1.07, 1.04, 0.21, -0.31, 1.16, -1.12);
    val sample: Sample = Sample(elements);

    print("Выборка: ");
    println(sample.toString());

    print("Вариационный ряд: ");
    println(Sample(sample.elements.sorted()).toString());

    print("Минимальное значение: ");
    println(sample.elements.min());

    print("Максимальное значение: ");
    println(sample.elements.max());

    print("Размах: ");
    println(scale(sample));

    print("Оценка математического ожидания: ");
    println(mathExpectation(sample));

    print("Оценка среднеквадратического отклонения: ");
    println(meanSquareDeviation(sample));

    val distributionFunction: EmpiricalDistributionFunction = EmpiricalDistributionFunction(sample);
    println("Эмпирическая функция распределения: ");
    println(distributionFunction.toString());

    val intervalStatisticsSeries: IntervalStatisticsSeries = IntervalStatisticsSeries(sample);
    val polygon: DistributionPolygon = DistributionPolygon(intervalStatisticsSeries);
    val histogram: DistributionHistogram = DistributionHistogram(intervalStatisticsSeries);

    val functionPanel: ChartPanel = distributionFunction.getChartPanel();
    val polygonPanel: ChartPanel = polygon.getChartPanel();
    val histogramPanel: ChartPanel = histogram.getChartPanel();

    val functionFrame: JFrame = JFrame();
    functionFrame.add(functionPanel);
    functionFrame.size = DEFAULT_FRAME_SIZE;
    val polygonFrame: JFrame = JFrame();
    polygonFrame.add(polygonPanel);
    polygonFrame.size = DEFAULT_FRAME_SIZE;
    val histogramFrame: JFrame = JFrame();
    histogramFrame.add(histogramPanel);
    histogramFrame.size = DEFAULT_FRAME_SIZE;

    functionFrame.isVisible = true;
    polygonFrame.isVisible = true;
    histogramFrame.isVisible = true;
}