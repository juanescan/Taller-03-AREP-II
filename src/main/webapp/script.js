// Agregar tarea (POST con JSON)
async function addTask() {
    const name = document.getElementById("taskName").value;
    const type = document.getElementById("taskType").value;

    if (!name.trim() || !type.trim()) {
        alert("Completa todos los campos: nombre y tipo");
        return;
    }

    await fetch("/api/tasks", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, type })
    });

    document.getElementById("taskName").value = "";
    document.getElementById("taskType").value = "";
    listTasks();
}

// Listar todas las tareas (GET)
async function listTasks() {
    const res = await fetch("/api/tasks");
    const tasks = await res.json();

    const list = document.getElementById("taskList");
    list.innerHTML = "";

    tasks.forEach(t => {
        const li = document.createElement("li");
        li.textContent = `${t.name} (${t.type})`;
        list.appendChild(li);
    });
}

// Cargar la lista al inicio
window.onload = listTasks;
