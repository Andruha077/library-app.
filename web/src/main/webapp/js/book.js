
const bookDetails = document.getElementById("bookDetails");
const commentsList = document.getElementById("commentsList");
const commentForm = document.getElementById("commentForm");

const params = new URLSearchParams(window.location.search);
const bookId = params.get("id");

const BOOKS_URL = "books";
const COMMENTS_URL = "comments";

async function loadBook() {
    if (!bookId) {
        bookDetails.innerHTML = "<p>Книгу не знайдено.</p>";
        return;
    }

    try {
        const response = await fetch(`${BOOKS_URL}/${bookId}`);

        if (!response.ok) {
            throw new Error("Не вдалося завантажити книгу");
        }

        const book = await response.json();

        const title = book.title ?? book["назва"] ?? "Без назви";
        const author = book.author ?? book["автор"] ?? "Невідомий автор";
        const description = book.description ?? book["опис"] ?? "Опис відсутній";

        bookDetails.innerHTML = `
            <a href="index.html" class="back-link">← Назад до каталогу</a>

            <div class="book-page">
                <div class="book-cover">
                    <div class="cover-art">
                        <span>BOOKSPACE</span>
                        <h2>${escapeHtml(title)}</h2>
                    </div>
                </div>

                <div class="book-info book-details">
                    <span class="book-label">КНИГА</span>
                    <h1>${escapeHtml(title)}</h1>
                    <p class="book-author">${escapeHtml(author)}</p>
                    <div class="book-description">
                        ${escapeHtml(description)}
                    </div>
                </div>
            </div>
        `;

    } catch (error) {
        bookDetails.innerHTML = `
            <p class="empty-message">${escapeHtml(error.message)}</p>
        `;
    }
}

async function loadComments() {
    try {
        const response = await fetch(`${COMMENTS_URL}/book/${bookId}`);

        if (!response.ok) {
            throw new Error("Не вдалося завантажити коментарі");
        }

        const comments = await response.json();

        renderComments(Array.isArray(comments) ? comments : []);
    } catch (error) {
        commentsList.innerHTML = `
            <p class="empty-message">${escapeHtml(error.message)}</p>
        `;
    }
}

function renderComments(comments) {
    if (comments.length === 0) {
        commentsList.innerHTML = `
            <p class="empty-message">Поки що немає коментарів. Будьте першим!</p>
        `;
        return;
    }

    commentsList.innerHTML = comments.map(comment => {
        const id = comment.id ?? comment["ідентифікатор"];
        const author = comment.author ?? comment["автор"] ?? "Анонім";
        const content = comment.text ?? comment["текст"] ?? "";
        const createdAt = comment.createdAt ?? comment["датаСтворення"] ?? "";

        return `
            <article class="comment-card">
                <div class="comment-header">
                    <strong>${escapeHtml(author)}</strong>
                    <span>${formatDate(createdAt)}</span>
                </div>

                <p>${escapeHtml(content)}</p>

                <button
                    class="delete-comment"
                    data-id="${Number(id)}"
                    type="button">
                    Видалити
                </button>
            </article>
        `;
    }).join("");

    document.querySelectorAll(".delete-comment").forEach(button => {
        button.addEventListener("click", () => {
            deleteComment(button.dataset.id);
        });
    });
}

commentForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const authorInput = document.getElementById("commentAuthor");
    const textInput = document.getElementById("commentText");

    const author = authorInput.value.trim();
    const text = textInput.value.trim();

    if (!author || !text) {
        alert("Заповніть усі поля");
        return;
    }

    try {
        const response = await fetch(`${COMMENTS_URL}/book/${bookId}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                author: author,
                text: text
            })
        });

        if (!response.ok) {
            throw new Error("Не вдалося додати коментар");
        }

        commentForm.reset();
        await loadComments();

    } catch (error) {
        alert(error.message);
    }
});

async function deleteComment(commentId) {
    if (!confirm("Видалити цей коментар?")) {
        return;
    }

    try {
        const response = await fetch(`${COMMENTS_URL}/${commentId}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            throw new Error("Не вдалося видалити коментар");
        }

        await loadComments();

    } catch (error) {
        alert(error.message);
    }
}

function formatDate(value) {
    if (!value) return "";

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return "";
    }

    return date.toLocaleString("uk-UA");
}

function escapeHtml(value) {
    return String(value).replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;"
    })[character]);
}

loadBook();
loadComments();
