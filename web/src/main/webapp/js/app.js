
const API_URL = "books";

let currentPage = 0;
let currentQuery = "";
const pageSize = 9;

const bookList = document.getElementById("bookList");
const bookCount = document.getElementById("bookCount");
const searchInput = document.getElementById("searchInput");
const searchButton = document.getElementById("searchButton");
const pagination = document.getElementById("pagination");

async function loadBooks(page = 0) {
    currentPage = page;

    bookList.innerHTML = '<div class="loading">Завантажуємо книги...</div>';

    try {
        const params = new URLSearchParams({
            page: currentPage,
            size: pageSize,
            sort: "title"
        });

        if (currentQuery.trim()) {
            params.set("q", currentQuery.trim());
        }

        const response = await fetch(`${API_URL}?${params}`);

        if (!response.ok) {
            throw new Error(`Помилка сервера: ${response.status}`);
        }

        const data = await response.json();

        renderBooks(data.content || []);
        renderPagination(data.totalPages || 0, data.page || 0);

        bookCount.textContent =
            `Знайдено книг: ${data.totalElements ?? 0}`;

    } catch (error) {
        console.error(error);

        bookList.innerHTML = `
            <div class="empty-message">
                <h3>Не вдалося завантажити каталог</h3>
                <p>Перевір, чи запущений сервер Jetty.</p>
            </div>
        `;

        bookCount.textContent = "Помилка завантаження";
        pagination.innerHTML = "";
    }
}

function renderBooks(books) {
    if (!books.length) {
        bookList.innerHTML = `
            <div class="empty-message">
                <h3>Книг не знайдено</h3>
                <p>Спробуй змінити пошуковий запит.</p>
            </div>
        `;
        return;
    }

    bookList.innerHTML = books.map((book, index) => `
        <article class="book-card">
            <div class="book-cover">
                <div class="cover-art" style="
                    background: ${getCoverColor(index)};
                ">
                    <span>BOOKSPACE / ${book.id}</span>
                    <strong>${escapeHtml(book.title)}</strong>
                    <small>${escapeHtml(book.author)}</small>
                </div>
            </div>

            <div class="book-info">
                <h3>${escapeHtml(book.title)}</h3>
                <p class="author">${escapeHtml(book.author)}</p>
                <p class="description">
                    ${escapeHtml(book.description || "Опис відсутній")}
                </p>

                <a class="details-button"
                   href="book.html?id=${encodeURIComponent(book.id)}">
                    Детальніше <span>→</span>
                </a>
            </div>
        </article>
    `).join("");
}

function renderPagination(totalPages, activePage) {
    pagination.innerHTML = "";

    if (totalPages <= 1) {
        return;
    }

    for (let i = 0; i < totalPages; i++) {
        const button = document.createElement("button");

        button.textContent = i + 1;

        if (i === activePage) {
            button.classList.add("active");
        }

        button.addEventListener("click", () => loadBooks(i));

        pagination.appendChild(button);
    }
}

function getCoverColor(index) {
    const colors = [
        "#315d49",
        "#9b684b",
        "#465b78",
        "#77634d",
        "#526a60",
        "#7b5360"
    ];

    return colors[index % colors.length];
}

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;"
    })[character]);
}

function searchBooks() {
    currentQuery = searchInput.value.trim();
    loadBooks(0);
}

searchButton.addEventListener("click", searchBooks);

searchInput.addEventListener("keydown", event => {
    if (event.key === "Enter") {
        searchBooks();
    }
});

loadBooks();
