package com.example.ead.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ead.R
import com.example.ead.api.CustomerDeleteResponse
import com.example.ead.api.CustomerUpdateResponse
import com.example.ead.fragments.HomeFragment
import com.example.ead.fragments.LoginRegister.AccountOptionsFragment
import com.example.ead.fragments.LoginRegister.RegisterFragment
import com.example.ead.models.Customer
import com.example.ead.models.LoginRequest
import com.example.ead.util.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var toolbarLayout: RelativeLayout
    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var profileEditImage: ImageView
    private lateinit var cusAccountProfileImage: ImageView
    private lateinit var editTextFullName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPhoneNo: EditText

    private lateinit var cusAccountAddress1: EditText
    private lateinit var btnCusDelete: Button


    private lateinit var cusAccountProfileImageEditFrame: View
    private lateinit var cusAccountButtons: View

    private lateinit var originalDrawable: Drawable


    private var selectedImageUri: Uri? = null
    private var imageData: String? = null

    private lateinit var buttonBack: ImageButton
    private lateinit var buttonCart: ImageButton
    private lateinit var clickcartHome: ImageView

    private lateinit var btnCusUpdate: Button




    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        buttonBack = findViewById(R.id.buttonBack)
        clickcartHome = findViewById(R.id.clickcartHome)
        buttonCart = findViewById(R.id.buttonCart)

        buttonCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        btnCusDelete = findViewById(R.id.btnCusDelete)

        // Handle delete button click
        btnCusDelete.setOnClickListener {
            deleteUserAccount()
        }

        buttonBack.setOnClickListener { onBackPressed() }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        loadingProgressBar = findViewById(R.id.loadingAccManProgressBar)
        toolbarLayout = findViewById(R.id.cusAccManagementHeading)
        constraintLayout = findViewById(R.id.commonHomeConstraint1)

        swipeRefreshLayout.setOnRefreshListener { refreshContent() }

//        profileEditImage = findViewById(R.id.profileEditImage)
        cusAccountProfileImage = findViewById(R.id.cusAccountProfileImage)

        editTextFullName = findViewById(R.id.editTextFullName2)
        editTextEmail = findViewById(R.id.viewInputEmail2)
        editTextPhoneNo = findViewById(R.id.viewInputPhoneNo2)
        cusAccountAddress1 = findViewById(R.id.cusAccountAddress2)

        cusAccountProfileImage.setOnClickListener {
            val fullScreenIntent = Intent(this, FullScreenImageActivity::class.java)
            fullScreenIntent.putExtra("productImage", selectedImageUri)
            startActivity(fullScreenIntent)
        }

        btnCusUpdate = findViewById(R.id.btnCusUpdate)


        btnCusUpdate.setOnClickListener {
            updateUserDetails()
        }

        // In your Activity or Fragment
        val editText = findViewById<EditText>(R.id.viewInputEmail2)
        editText.setOnClickListener {
            // Show dialog with the full text
            val fullText = editText.text.toString()
            AlertDialog.Builder(this)
                .setTitle("Email")
                .setMessage(fullText)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }





        // Retrieve user details from SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val customerName = sharedPreferences.getString("customer_name", "")
        val customerEmail = sharedPreferences.getString("customer_email", "")
        val customerMobileNumber = sharedPreferences.getString("customer_mobileNumber", "")
        val customerAddress = sharedPreferences.getString("customer_address", "")

        // Populate the EditText fields with retrieved data
        editTextFullName.setText(customerName)
        editTextEmail.setText(customerEmail)
        editTextPhoneNo.setText(customerMobileNumber)
        cusAccountAddress1.setText(customerAddress)
    }

    private fun deleteUserAccount() {
        // Get user email from shared preferences
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val customerEmail = sharedPreferences.getString("customer_email", "")

        if (customerEmail.isNullOrEmpty()) {
            Toast.makeText(this, "Error: No email found", Toast.LENGTH_SHORT).show()
            return
        }


        // Call the API using Retrofit
        val call = RetrofitInstance.Customerapi.deactivateCustomer(customerEmail)

        call.enqueue(object : Callback<CustomerDeleteResponse> {
            override fun onResponse(call: Call<CustomerDeleteResponse>, response: Response<CustomerDeleteResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null && responseBody.status == "Success") {
                        // Show success message and redirect or update UI
                        Toast.makeText(this@ProfileActivity, "Account deleted successfully.", Toast.LENGTH_SHORT).show()

                        // Optionally, you can log the user out or navigate them to the login screen
                        val intent = Intent(this@ProfileActivity, LoginRegisterActivity::class.java)
                        startActivity(intent)
                        finish() // Close the ProfileActivity
                    } else {
                        Toast.makeText(this@ProfileActivity, "Failed to delete account.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }



            override fun onFailure(call: Call<CustomerDeleteResponse>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun updateUserDetails() {
        // Get values from EditTexts
        val fullName = editTextFullName.text.toString()
        val email = editTextEmail.text.toString()
        val phoneNoString = editTextPhoneNo.text.toString()
        val phoneNo: Int? = phoneNoString.toIntOrNull()
        val address = cusAccountAddress1.text.toString()
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val password = sharedPreferences.getString("customer_password", "")
        val userId = sharedPreferences.getString("customer_id","")

        // Check for validation if required (e.g. non-empty fields)
        val updateRequest = Customer(fullName,email, password, phoneNo,address)
        Log.d("update request",updateRequest.toString())

        val call = RetrofitInstance.Customerapi.updateCustomer(userId,updateRequest)
        Log.d("updated call",call.toString())



        if (call != null) {
            call.enqueue(object : Callback<CustomerUpdateResponse> {
                override fun onResponse(
                    call: Call<CustomerUpdateResponse>,
                    response: Response<CustomerUpdateResponse>
                ) {
                    if (response.isSuccessful) {
                        // Handle successful response
                        val updatedCustomer = response.body()

                        if (updatedCustomer != null) {
                            // Show success message

                            val sharedPreferences =
                                getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("customer_name", updatedCustomer.fullName)
                            editor.putString("customer_email", updatedCustomer.email)
                            editor.putString(
                                "customer_mobileNumber",
                                updatedCustomer.mobileNumber.toString()
                            )
                            editor.putString("customer_address", updatedCustomer.address)

                            // Apply changes to SharedPreferences
                            editor.apply()

                            Log.d("ProfileUpdate", "Updated data saved to local storage.")
                            Toast.makeText(
                                this@ProfileActivity,
                                "Update successful: ${updatedCustomer.fullName}",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Log success
                            Log.d("ProfileUpdate", "Update Successful: $updatedCustomer")

                            // Save updated details to SharedPreferences

                        }
                    } else {
                        // Handle failure
                        Toast.makeText(
                            this@ProfileActivity,
                            "Failed to update. Code: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(
                            "ProfileUpdate",
                            "Update failed with response code: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<CustomerUpdateResponse>, t: Throwable) {
                    // Handle error
                    Toast.makeText(
                        this@ProfileActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("ProfileUpdate", "Error: ${t.message}")
                }
            })


        }
    }

    private fun refreshContent() {
        loadingProgressBar.visibility = View.VISIBLE
        swipeRefreshLayout.isRefreshing = false
    }

    private fun toggleEditMode() {
        toggleViewVisibility(profileEditImage)
        toggleViewVisibility(cusAccountButtons)
        toggleViewVisibility(cusAccountProfileImageEditFrame)

        toggleViewPair(editTextFullName, findViewById<TextView>(R.id.editTextFullName2))
        toggleViewPair(editTextEmail, findViewById<TextView>(R.id.viewInputEmail2))
        toggleViewPair(editTextPhoneNo, findViewById<TextView>(R.id.viewInputPhoneNo2))



        toggleViewPair(cusAccountAddress1, findViewById<TextView>(R.id.cusAccountAddress2))

    }

    private fun toggleViewVisibility(view: View) {
        view.visibility = if (view.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    private fun toggleViewPair(editView: View, displayView: View) {
        if (editView.visibility == View.GONE) {
            editView.visibility = View.VISIBLE
            displayView.visibility = View.GONE
        } else {
            editView.visibility = View.GONE
            displayView.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        Log.d("TAG", "Debug message")
        if (supportFragmentManager.backStackEntryCount > 0) {
            Log.d("TAG", "If Debug message")
            supportFragmentManager.popBackStack() // Go back to previous fragment
        } else {
            Log.d("TAG", "Else Debug message")
            super.onBackPressed() // Close activity if no fragments are in the stack
        }
    }


}