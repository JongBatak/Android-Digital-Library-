import sqlite3, pathlib

PROJECT = pathlib.Path(r"C:\Users\Pongo\AndroidStudioProjects\PerpustakaanOffline")
books_dir = PROJECT / "app" / "src" / "main" / "assets" / "books"
db_path = PROJECT / "app" / "src" / "main" / "assets" / "databases" / "perpustakaan_preload.db"
db_path.parent.mkdir(parents=True, exist_ok=True)

conn = sqlite3.connect(db_path)
cur = conn.cursor()

schema = """
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS reading_stats;

CREATE TABLE accounts(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    full_name TEXT NOT NULL,
    class_name TEXT,
    gender TEXT,
    is_admin INTEGER NOT NULL
);

CREATE TABLE books(
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    category TEXT,
    format TEXT NOT NULL,
    file_path TEXT,
    cover_url TEXT,
    is_preinstalled INTEGER NOT NULL,
    description TEXT
);

CREATE TABLE reading_stats(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    book_id INTEGER NOT NULL,
    account_id INTEGER NOT NULL,
    minutes_read INTEGER NOT NULL,
    last_read_at INTEGER NOT NULL
);
"""
cur.executescript(schema)

cur.execute(
    "INSERT INTO accounts(username,password,full_name,is_admin) VALUES(?,?,?,1)",
    ("adminPerpusDig","admin123","Administrator")
)

for idx, epub in enumerate(sorted(books_dir.glob("*.epub")), start=1):
    title = epub.stem.replace("_", " ").strip()
    cur.execute(
        """
        INSERT INTO books(title,author,category,format,file_path,cover_url,is_preinstalled,description)
        VALUES(?,?,?,?,?,?,?,?)
        """,
        (
            title or f"Buku {idx}",
            "Unknown Author",
            "General",
            "EPUB",
            f"books/{epub.name}",
            None,
            1,
            "Buku pre-install"
        )
     )

conn.commit()
conn.close()
