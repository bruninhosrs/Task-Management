document.getElementById("login-form")?.addEventListener("submit", async function (e) {
    e.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch("http://localhost:8080/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `username=${username}&password=${password}`,
        });

        if (!response.ok) {
            throw new Error(`Erro: ${await response.text()}`);
        }

        const result = await response.json();
        alert(result.message);

        if (result.status === 200) {
            window.location.href = "../templates/home.html";
        }
    } catch (error) {
        console.error("Erro ao tentar login:", error);
        alert("Falha no login. Verifique suas credenciais.");
    }
});

async function loadTasks() {
    try {
        const response = await fetch("http://localhost:8080/tasks", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        });

        if (!response.ok) {
            throw new Error("Erro ao buscar tarefas. Status: " + response.status);
        }

        const tasks = await response.json();
        const tableBody = document.querySelector("#tasks-table tbody");
        tableBody.innerHTML = "";

        tasks.forEach((task) => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${task.title}</td>
                <td>${task.description}</td>
                <td>${task.dueDate}</td>
                <td>${task.priority}</td>
                <td><button onclick="deleteTask(${task.id})">Excluir</button></td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error("Erro ao carregar tarefas:", error);
    }
}

async function deleteTask(id) {
    try {
        const response = await fetch("http://localhost:8080/tasks", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `id=${id}`,
        });

        if (!response.ok) {
            throw new Error(`Erro ao excluir tarefa. Status: ${response.status}`);
        }

        alert("Tarefa excluída com sucesso!");
        loadTasks();
    } catch (error) {
        console.error("Erro ao excluir tarefa:", error);
    }
}

document.addEventListener("DOMContentLoaded", loadTasks);

