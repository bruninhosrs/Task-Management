async function loadReport(type) {
    try {
        const response = await fetch(`http://localhost:8080/reports?type=${type}`);
        if (!response.ok) {
            throw new Error(`Erro ao carregar relatório. Status: ${response.status}`);
        }

        const data = await response.json();
        const contentDiv = document.getElementById("report-content");
        contentDiv.innerHTML = `<pre>${JSON.stringify(data, null, 2)}</pre>`;
    } catch (error) {
        console.error("Erro ao carregar relatório:", error);
    }
}
