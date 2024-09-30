package com.example.ead.activities


import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.ead.R
import com.example.ead.fragments.Shopping.CanceledOrderFragment
import com.example.ead.fragments.Shopping.CompletedOrderFragment
import com.example.ead.fragments.Shopping.PendingOrderFragment

class OrdersActivity : AppCompatActivity() {

    private lateinit var buttonBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        buttonBack = findViewById(R.id.buttonBack)
        // Set click listener for the back button
        buttonBack.setOnClickListener { onBackPressed() }

        val linearNavbarItem1 = findViewById<LinearLayout>(R.id.linearNavbarItem1)
        val linearNavbarItem2 = findViewById<LinearLayout>(R.id.linearNavbarItem2)
        val linearNavbarItem3 = findViewById<LinearLayout>(R.id.linearNavbarItem3)

        // Set default fragment
        if (savedInstanceState == null) {
            setFragment(PendingOrderFragment())
            updateUIForActiveFragment(1) // Active fragment is Pending
        }

        // Set up click listeners for the navigation items
        linearNavbarItem1.setOnClickListener {
            setFragment(PendingOrderFragment())
            updateUIForActiveFragment(1)
        }

        linearNavbarItem2.setOnClickListener {
            setFragment(CompletedOrderFragment())
            updateUIForActiveFragment(2)
        }

        linearNavbarItem3.setOnClickListener {
            setFragment(CanceledOrderFragment())
            updateUIForActiveFragment(3)
        }
    }

    private fun setFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.cusTransactionMethodFragmentContainer, fragment)
        transaction.commit()
    }

    private fun updateUIForActiveFragment(activeFragment: Int) {
        // Reset all fragment views
        resetFragmentViews()

        // Update the active fragment's UI
        when (activeFragment) {
            1 -> {
                findViewById<ImageView>(R.id.imageFolder1).setImageResource(R.drawable.pending_on)
                findViewById<TextView>(R.id.txtMyparcels).setTextColor(resources.getColor(R.color.ordersOnTextColor))
            }
            2 -> {
                findViewById<ImageView>(R.id.imageFolder2).setImageResource(R.drawable.completed_on)
                findViewById<TextView>(R.id.txtHome).setTextColor(resources.getColor(R.color.ordersOnTextColor))
            }
            3 -> {
                findViewById<ImageView>(R.id.imageFolder3).setImageResource(R.drawable.cancel_on)
                findViewById<TextView>(R.id.txtMyDeliveries).setTextColor(resources.getColor(R.color.ordersOnTextColor))
            }
        }
    }

    private fun resetFragmentViews() {
        // Reset Pending
        findViewById<ImageView>(R.id.imageFolder1).setImageResource(R.drawable.pending)
        findViewById<TextView>(R.id.txtMyparcels).setTextColor(resources.getColor(R.color.ordersTextColor))

        // Reset Completed
        findViewById<ImageView>(R.id.imageFolder2).setImageResource(R.drawable.completed)
        findViewById<TextView>(R.id.txtHome).setTextColor(resources.getColor(R.color.ordersTextColor))

        // Reset Canceled
        findViewById<ImageView>(R.id.imageFolder3).setImageResource(R.drawable.cancel)
        findViewById<TextView>(R.id.txtMyDeliveries).setTextColor(resources.getColor(R.color.ordersTextColor))
    }

    override fun onBackPressed() {
        Log.d("TAG", "Debug message")
        // Check if the fragment manager has a back stack
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount > 0) {
            Log.d("TAG", "If Debug message")
            supportFragmentManager.popBackStack() // Go back to previous fragment
        } else {
            Log.d("TAG", "Else Debug message")
            finish() // Close activity if no fragments are in the stack
        }
    }
}
