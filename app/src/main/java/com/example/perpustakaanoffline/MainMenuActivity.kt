package com.example.perpustakaanoffline

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainMenuActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var adapter: BookAdapter
    private val books = mutableListOf<Book>()
    private val repo by lazy {
        val app = application as PerpusApp
        app.container.bookRepository
    }
    private val accountRepo by lazy {
        val app = application as PerpusApp
        app.container.accountRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view)
        val btnToggleDrawer = findViewById<ImageView>(R.id.btn_toggle_drawer)
        val btnProfile = findViewById<ImageButton>(R.id.btn_profile)
        val btnLogout = findViewById<ImageButton>(R.id.btn_logout_toolbar)
        val tvWelcome = findViewById<TextView>(R.id.tv_welcome)
        val searchField = findViewById<EditText>(R.id.et_search)
        val recycler = findViewById<RecyclerView>(R.id.rv_books)

        recycler.layoutManager = GridLayoutManager(this, 2)
        adapter = BookAdapter(mutableListOf()) { book ->
            openBookDetails(book)
        }
        recycler.adapter = adapter

        lifecycleScope.launch {
            repo.observeBooks().collectLatest { entities ->
                val mapped = entities.map {
                    Book(
                        id = it.id,
                        title = it.title,
                        author = it.author,
                        category = it.category ?: "",
                        coverResId = R.drawable.book_cover_placeholder,
                        description = it.description,
                        format = it.format.name,
                        filePath = it.filePath
                    )
                }
                books.clear()
                books.addAll(mapped)
                adapter.submitList(books.toList())
            }
        }

        val prefs = getSharedPreferences("perpus_prefs", MODE_PRIVATE)
        val currentUser = prefs.getString(LoginActivity.KEY_SESSION_USER, null)
        val isAdmin = prefs.getBoolean(LoginActivity.KEY_IS_ADMIN, false)
        if (currentUser == null) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
            return
        }
        if (isAdmin) {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
            return
        }
        val headerView = navView.getHeaderView(0)
        val tvNavName = headerView.findViewById<TextView>(R.id.tv_nav_name)
        val tvNavEmail = headerView.findViewById<TextView>(R.id.tv_nav_email)
        tvWelcome.text = resources.getString(R.string.welcome_message, currentUser)
        tvNavName?.text = currentUser
        tvNavEmail?.text = "$currentUser@perpus.local"

        lifecycleScope.launch {
            runCatching { accountRepo.findByUsername(currentUser) }
                .onSuccess { account ->
                    account?.let {
                        tvWelcome.text = resources.getString(R.string.welcome_message, it.fullName)
                        tvNavName?.text = it.fullName
                        tvNavEmail?.text = it.className?.takeIf { name -> name.isNotBlank() }
                            ?: "$currentUser@perpus.local"
                    }
                }
        }

        btnToggleDrawer.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        btnProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }

        btnLogout.setOnClickListener { showLogoutDialog() }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> Unit
                R.id.nav_dashboard -> {
                    val intent = Intent(this, UserDashboardActivity::class.java).apply {
                        putExtra(LoginActivity.KEY_SESSION_USER, currentUser)
                    }
                    startActivity(intent)
                }
                R.id.nav_logout -> showLogoutDialog()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val keyword = s?.toString().orEmpty()
                val filtered = if (keyword.isBlank()) books else books.filter { it.title.contains(keyword, true) }
                adapter.submitList(filtered)
            }
        })
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Logout") { _, _ ->
                val prefs = getSharedPreferences("perpus_prefs", MODE_PRIVATE)
                prefs.edit()
                    .remove(LoginActivity.KEY_SESSION_USER)
                    .remove(LoginActivity.KEY_IS_ADMIN)
                    .apply()
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun openBookDetails(book: Book) {
        val intent = Intent(this, BookDetailsActivity::class.java).apply {
            putExtra(BookDetailsActivity.EXTRA_BOOK_ID, book.id)
            putExtra(BookDetailsActivity.EXTRA_TITLE, book.title)
            putExtra(BookDetailsActivity.EXTRA_AUTHOR, book.author)
            putExtra(BookDetailsActivity.EXTRA_DESCRIPTION, book.description)
            putExtra(BookDetailsActivity.EXTRA_FORMAT, book.format)
            putExtra(BookDetailsActivity.EXTRA_COVER_RES, book.coverResId)
        }
        startActivity(intent)
    }
}
