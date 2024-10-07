package com.example.ead.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.example.ead.R
import com.example.ead.fragments.HomeFragment
import com.google.android.material.navigation.NavigationView

class Main : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var buttonDrawerToggle: ImageButton
    private lateinit var buttonDrawerMenuRight: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var clickcartLogo: ImageView
    private lateinit var buttonUser: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        drawerLayout = findViewById(R.id.navigatorDrawer)
        buttonDrawerToggle = findViewById(R.id.buttonDrawerToggle)
        buttonDrawerMenuRight = findViewById(R.id.buttonDrawerMenuRight)
        navigationView = findViewById(R.id.navigationView)
        clickcartLogo = findViewById(R.id.clickcart_logo)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("customer_name", "User Name") // Default value
        val email = sharedPreferences.getString("customer_email", "User Email") // Default value


        // Load HomeFragment by default when activity is created
        if (savedInstanceState == null) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainContent, HomeFragment())  // Default to HomeFragment
            transaction.commit()
        }

        buttonDrawerToggle.setOnClickListener {
            drawerLayout.open()
        }

        buttonUser = findViewById(R.id.buttonUser)

        buttonUser.setOnClickListener {
            Log.d("CheckoutActivity1", "User icon clicked")
            showUserOptions(buttonUser)
        }

        // Handle clickcart_logo click to load HomeFragment
        clickcartLogo.setOnClickListener {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainContent, HomeFragment())  // Switch to HomeFragment
            transaction.commit()
        }

        val headerView = navigationView.getHeaderView(0)
        val useImage: ImageView = headerView.findViewById(R.id.userImage)
        val textUsername: TextView = headerView.findViewById(R.id.textUserName)
        val textUserEmail: TextView =
            headerView.findViewById(R.id.textUserEmail) // Ensure this ID matches


        textUsername.text = username
        textUserEmail.text = email

        useImage.setOnClickListener {
            Toast.makeText(this, textUsername.text, Toast.LENGTH_SHORT).show()
        }

        // Set OnClickListener for buttonDrawerMenuRight to load CartActivity
        buttonDrawerMenuRight.setOnClickListener {
            // Launch CartActivity when buttonDrawerMenuRight is clicked
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            val itemId = item.itemId

            when (itemId) {
                R.id.navHome -> {
                    // Load HomeFragment
                    val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.mainContent, HomeFragment())
                    transaction.commit()
                }

                R.id.navProfile -> {
                    // Launch ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }

                R.id.navCart -> {
                    // Launch CartActivity
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                }

                R.id.navMyOrders -> {
                    // Launch OrdersActivity
                    val intent = Intent(this, OrdersActivity::class.java)
                    startActivity(intent)
                }

                R.id.navMyComments -> {
                    // Launch CommentsActivity
                    val intent = Intent(this, CommentsActivity::class.java)
                    startActivity(intent)
                }

                R.id.navLogout -> {
                    // Handle logout

                    // Remove user_prefs from SharedPreferences
                    val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()  // Clears all the saved data
                    editor.apply()  // Apply changes

                    // Start the LoginRegisterActivity
                    val intent = Intent(this, LoginRegisterActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

            }

            drawerLayout.close()  // Close the drawer after selection
            true
        }
    }

    private fun showUserOptions(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_user_options, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    // Navigate to HomeFragment
                    val intent = Intent(this, Main::class.java)
                    startActivity(intent)
                    true
                }

                R.id.menu_logout -> {
                    logoutUser()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }


    // Method to clear shared preferences and log out
    private fun logoutUser() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()  // Clears all the saved data
        editor.apply()  // Apply changes

        // Start the LoginRegisterActivity
        val intent = Intent(this, LoginRegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}
