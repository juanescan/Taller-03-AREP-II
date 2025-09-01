async function addTask() {
    const name = document.getElementById("taskName").value;
    const type = document.getElementById("taskType").value;
    await fetch(`/tasks?name=${encodeURIComponent(name)}&type=${encodeURIComponent(type)}`, {
        method: "POST"
    });
    listTasks();
}

async function listTasks() {
    const res = await fetch("/tasks");
    const tasks = await res.json();
    const list = document.getElementById("taskList");
    list.innerHTML = "";
    tasks.forEach(t => {
        const li = document.createElement("li");
        li.textContent = `${t.name} (${t.type})`;
        list.appendChild(li);
    });
}

async function deleteTask(name, type) {
    const res = await fetch(`/tasks?name=${encodeURIComponent(name)}&type=${encodeURIComponent(type)}`, {
        method: "DELETE"
    });
    const msg = await res.text();
    alert(msg);
    listTasks();
}
