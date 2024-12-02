// Manipulação do formulário de login
document
  .getElementById("login-form")
  ?.addEventListener("submit", async function (e) {
    e.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    try {
      const response = await fetch("http://localhost:8080/login", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `username=${username}&password=${password}`,
      });

      const result = await response.json();
      if (response.ok) {
        alert(result.message);
        window.location.href = "tasks.html";
      } else {
        alert(result.message);
      }
    } catch (error) {
      console.error(error);
    }
  });

// Carregar tarefas na página tasks.html
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
                <td>
                    <button class="edit-task" onclick="editTask(${task.id})">Editar</button>
                    <button class="delete-task" onclick="deleteTask(${task.id})">Excluir</button>
                </td>
            `;
      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Erro ao carregar tarefas:", error);
    alert("Erro ao carregar tarefas.");
  }
}

// Adicionar uma nova tarefa
async function addTask(event) {
  event.preventDefault();

  const title = document.getElementById("title").value;
  const description = document.getElementById("description").value;
  const dueDate = document.getElementById("dueDate").value;
  const priority = document.getElementById("priority").value;

  try {
    const response = await fetch("http://localhost:8080/tasks", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: `title=${title}&description=${description}&dueDate=${dueDate}&priority=${priority}`,
    });

    if (response.ok) {
      alert("Tarefa cadastrada com sucesso!");
      window.location.href = "tasks.html";
    } else {
      alert(
        "Erro ao cadastrar a tarefa. Verifique os dados e tente novamente."
      );
    }
  } catch (error) {
    console.error("Erro ao adicionar tarefa:", error);
    alert("Erro ao adicionar tarefa.");
  }
}

// Redirecionar para a página de edição
function editTask(taskId) {
  window.location.href = `editTask.html?id=${taskId}`;
}

// Carregar dados da tarefa na página editTask.html
async function loadTaskForEdit() {
  const params = new URLSearchParams(window.location.search);
  const taskId = params.get("id");

  if (!taskId) {
    alert("ID da tarefa não encontrado!");
    window.location.href = "tasks.html";
    return;
  }

  try {
    const response = await fetch(`http://localhost:8080/tasks/${taskId}`, {
      method: "GET",
      headers: { "Content-Type": "application/json" },
    });

    if (!response.ok) throw new Error("Erro ao buscar dados da tarefa.");

    const task = await response.json();
    document.getElementById("title").value = task.title;
    document.getElementById("description").value = task.description;
    document.getElementById("dueDate").value = task.dueDate;
    document.getElementById("priority").value = task.priority;
  } catch (error) {
    console.error("Erro ao carregar tarefa:", error);
    alert("Erro ao carregar tarefa para edição.");
  }
}

// Salvar alterações na tarefa
async function saveEditedTask(event) {
  event.preventDefault();
  const params = new URLSearchParams(window.location.search);
  const taskId = params.get("id");

  if (!taskId) {
    alert("ID da tarefa não encontrado!");
    return;
  }

  const title = document.getElementById("title").value;
  const description = document.getElementById("description").value;
  const dueDate = document.getElementById("dueDate").value;
  const priority = document.getElementById("priority").value;

  try {
    const response = await fetch(`http://localhost:8080/tasks/${taskId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: `title=${title}&description=${description}&dueDate=${dueDate}&priority=${priority}`,
    });

    if (!response.ok) throw new Error("Erro ao salvar alterações da tarefa.");

    alert("Tarefa editada com sucesso!");
    window.location.href = "tasks.html";
  } catch (error) {
    console.error("Erro ao editar tarefa:", error);
    alert("Erro ao salvar alterações.");
  }
}

// Excluir uma tarefa
async function deleteTask(id) {
  try {
    const response = await fetch(`http://localhost:8080/tasks?id=${id}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (response.ok) {
      alert("Tarefa excluída com sucesso!");
      loadTasks(); 
    } else {
      const error = await response.json();
      alert(`Erro: ${error.message || "Não foi possível excluir a tarefa."}`);
    }
  } catch (error) {
    console.error("Erro ao excluir tarefa:", error);
    alert("Erro ao excluir tarefa. Tente novamente.");
  }
}

// Carregar relatórios
async function loadReport(type) {
  try {
    const response = await fetch(`http://localhost:8080/reports?type=${type}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error("Erro ao carregar relatório. Status: " + response.status);
    }

    const reportData = await response.json();
    displayReport(reportData);
  } catch (error) {
    console.error("Erro ao carregar relatório:", error);
    alert("Erro ao carregar relatório.");
  }
}

// Exibir relatório na página
function displayReport(data) {
  const reportContainer = document.getElementById("report-results");
  reportContainer.innerHTML = `<pre>${JSON.stringify(data, null, 2)}</pre>`;
}

// Event listeners para carregamento das páginas
document.addEventListener("DOMContentLoaded", () => {
  if (document.querySelector("#tasks-table")) loadTasks();
  if (document.querySelector("#add-task-form"))
    document
      .querySelector("#add-task-form")
      .addEventListener("submit", addTask);
  if (document.querySelector("#edit-task-form"))
    document
      .querySelector("#edit-task-form")
      .addEventListener("submit", saveEditedTask);
});
