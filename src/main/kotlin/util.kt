import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.BasicStroke
import java.awt.Color
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sqrt

class Sample(val elements: List<Double>) {
    override fun toString(): String {
        return buildString {
            this.append("{")
            elements.forEach {
                this.append(it);
                this.append(", ");
            }
            this.delete(this.length - 2, this.length);
            this.append("}");
        }
    }
}

fun scale(sample: Sample): Double { return sample.elements.max() - sample.elements.min(); }

fun mathExpectation(sample: Sample): Double { return sample.elements.sum() / sample.elements.size; }

fun meanSquareDeviation(sample: Sample): Double {
    val mathExpectation: Double = mathExpectation(sample);
    return sqrt(sample.elements.sumOf { (mathExpectation - it).pow(2) } / sample.elements.size);
}

class EmpiricalDistributionFunction(private val sample: Sample) {
    private var surges: List<Pair<Double, Double>>;

    init {
        val sortedUniqueElements: Set<Double> = sample.elements.toSortedSet() + Double.POSITIVE_INFINITY;
        surges = sortedUniqueElements.map { element: Double ->
            Pair(element, sample.elements.count { it < element }.toDouble() / sample.elements.count());
        }.toList();
    }

    override fun toString(): String {
        var prev: Double? = null;
        return buildString {
            this.append("F(x) = {\n");
            surges.forEach {
                this.append("   ");
                this.append(it.second);
                this.append(", ");
                if (prev == null) {
                    this.append("x < ");
                    this.append(it.first);
                }
                else if (it.first == Double.POSITIVE_INFINITY) {
                    this.append("x >= ");
                    this.append(prev);
                }
                else {
                    this.append(prev);
                    this.append(" <= x < ");
                    this.append(it.first);
                }
                this.append(";\n");
                prev = it.first;
            }
            this.append("}");
        }
    }

    fun getChartPanel(): ChartPanel {
        val series: XYSeries = XYSeries(0);

        var prev: Double = sample.elements.min();
        surges.forEach {
            series.add(prev, it.second);
            if (it.first == Double.POSITIVE_INFINITY) series.add(sample.elements.max(), it.second);
            else series.add(it.first, it.second);
            prev = it.first;
        }

        val dataset = XYSeriesCollection();
        dataset.addSeries(series);

        val chart: JFreeChart = ChartFactory.createXYLineChart(
            "Эмпирическая функция распределения",
            "x",
            "F(x)",
            dataset,
        )

        val renderer: XYLineAndShapeRenderer = XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, BasicStroke(3f));
        renderer.setSeriesShapesVisible(0, false);
        chart.xyPlot.renderer = renderer;

        return ChartPanel(chart);
    }
}

class IntervalStatisticsSeries(sample: Sample) {
    var series: List<Pair<Pair<Double, Double>, Int>>;
    var h: Double;
    init {
        h = (sample.elements.max() - sample.elements.min()) / (1 + log2(sample.elements.count().toDouble()));
        var leftBorder: Double = sample.elements.min() - h / 2;
        val seriesTmp: MutableList<Pair<Pair<Double, Double>, Int>> = mutableListOf();
        while (leftBorder < sample.elements.max()) {
            seriesTmp.add(Pair(Pair(leftBorder, leftBorder + h), sample.elements.count { (it > leftBorder) && (it <= (leftBorder + h)) }));
            leftBorder += h;
        }
        series = seriesTmp;
    }
}

class DistributionPolygon(private val intervalStatisticsSeries: IntervalStatisticsSeries) {
    fun getChartPanel(): ChartPanel {
        val series: XYSeries = XYSeries(0);
        intervalStatisticsSeries.series.forEach { series.add((it.first.first + it.first.second) / 2, it.second); }

        val dataset = XYSeriesCollection();
        dataset.addSeries(series);

        val chart: JFreeChart = ChartFactory.createXYLineChart(
            "Полигон частот",
            "x",
            "n",
            dataset,
        )

        val renderer: XYLineAndShapeRenderer = XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, BasicStroke(3f));
        renderer.setSeriesShapesVisible(0, false);
        chart.xyPlot.renderer = renderer;

        return ChartPanel(chart);
    }
}

class DistributionHistogram(private val intervalStatisticsSeries: IntervalStatisticsSeries) {
    fun getChartPanel(): ChartPanel {
        val series: XYSeries = XYSeries(0);
        intervalStatisticsSeries.series.forEach {
            series.add(it.first.first, 0);
            series.add(it.first.first, it.second / intervalStatisticsSeries.h)
            series.add(it.first.second, it.second / intervalStatisticsSeries.h)
            series.add(it.first.second, 0);
        }

        val dataset = XYSeriesCollection();
        dataset.addSeries(series);

        val chart: JFreeChart = ChartFactory.createXYLineChart(
            "Гистограмма частот",
            "x",
            "n_i / h",
            dataset,
        )

        val renderer: XYLineAndShapeRenderer = XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesStroke(0, BasicStroke(3f));
        renderer.setSeriesShapesVisible(0, false);
        chart.xyPlot.renderer = renderer;

        return ChartPanel(chart);
    }
}