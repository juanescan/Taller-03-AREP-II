function addTask() {
    let name = document.getElementById("taskName").value;
    let type = document.getElementById("taskType").value;
    fetch(`/tasks?name=${encodeURIComponent(name)}&type=${encodeURIComponent(type)}`, {method: 'POST'})
        .then(r => r.text()).then(t => {
            alert(t);
            listTasks();
        });
}

function listTasks() {
    fetch("/tasks")
        .then(r => r.json())
        .then(data => {
            let list = document.getElementById("tasksList");
            list.innerHTML = "";
            data.forEach(task => {
                let li = document.createElement("li");
                li.textContent = `${task.name} - ${task.type}`;
                li.onclick = () => deleteTask(task.name, task.type);
                list.appendChild(li);
            });
        });
}

function deleteTask(name, type) {
    fetch(`/tasks?name=${encodeURIComponent(name)}&type=${encodeURIComponent(type)}`, {method: 'DELETE'})
        .then(r => r.text()).then(t => {
            alert(t);
            listTasks();
        });
}
