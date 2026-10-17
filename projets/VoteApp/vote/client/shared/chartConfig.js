
const MAX_VALUE = 6;


const allLabels = ['Pour', 'Contre', 'NPPV', 'Abstention'];

/**
 * Creates or updates a voting chart
 * @param {Chart|null} chart - The existing chart to update, or null to create a new one
 * @param {Object} data - The voting data
 * @param {string} canvasId - The ID of the canvas element where to display the chart
 * @returns {Chart} The created or updated chart
 */
export function createOrUpdateChart(chart, data, canvasId) {
    const chartConfig = {
        type: 'bar',
        data: {
            labels: allLabels,
            datasets: [{
                label: 'Nombre de votes',
                data: data.votes,
                backgroundColor: [
                    'rgba(75, 192, 192, 0.6)',
                    'rgba(255, 99, 132, 0.6)',
                    'rgba(255, 206, 86, 0.6)',
                    'rgba(153, 102, 255, 0.6)'
                ]
            }]
        },
        options: {
            indexAxis: 'y',
            animation: false,
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                title: {
                    display: true,
                    text: data.subject ? 'Résultats pour ' + data.subject : 'Résultats',
                    font: {
                        size: 20,
                        weight: 'bold'
                    },
                    padding: {
                        top: 10,
                        bottom: 30
                    }
                }
            },
            scales: {
                x: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    },
                    max: MAX_VALUE
                }
            }
        }
    };

    if (chart) {
        chart.data.datasets[0].data = data.votes;
        chart.options.plugins.title.text = data.subject ? 'Résultats pour ' + data.subject : 'Résultats';
        chart.update('none');
        return chart;
    } else {
        const ctx = document.getElementById(canvasId).getContext('2d');
        return new Chart(ctx, chartConfig);
    }
} 