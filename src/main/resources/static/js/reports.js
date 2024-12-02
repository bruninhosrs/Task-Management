async function loadReport(type) {
    try {
        const response = await fetch(`http://localhost:8080/reports?type=${type}`, {
            method: "GET",
            headers: { "Content-Type": "application/json" }
        });

        if (!response.ok) {
            throw new Error(`Erro ao carregar relatório. Tipo: ${type}, Status: ${response.status}`);
        }

        const data = await response.json();
        const reportResults = document.getElementById("report-results");

        if (!reportResults) {
            console.error("Elemento #report-results não encontrado.");
            return;
        }

        reportResults.innerHTML = `<pre>${JSON.stringify(data, null, 2)}</pre>`;
    } catch (error) {
        console.error("Erro ao carregar relatório. Tipo:", type, "Erro:", error.message || error);
        alert("Erro ao carregar relatório.");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".buttons button").forEach(button => {
        button.addEventListener("click", () => {
            const type = button.getAttribute("data-type");
            loadReport(type);
        });
    });
});

