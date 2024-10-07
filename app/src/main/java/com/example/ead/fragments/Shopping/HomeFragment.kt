package com.example.ead.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ead.R
import com.example.ead.adapters.ProductAdapter
import com.example.ead.api.ProductApi
import com.example.ead.models.Product
import com.example.ead.util.RetrofitInstance
import retrofit2.*
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var productList: MutableList<Product>
    private lateinit var filteredProductList: MutableList<Product>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var categorySpinner: Spinner
    private lateinit var sortingSpinner: Spinner
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts)
        recyclerViewProducts.layoutManager = GridLayoutManager(context, 2)

        // Initialize the product list and adapter
        productList = mutableListOf()
        filteredProductList = mutableListOf()
        productAdapter = ProductAdapter(requireContext(), filteredProductList)
        recyclerViewProducts.adapter = productAdapter

        // Initialize the category, sorting spinners, and search view
        categorySpinner = view.findViewById(R.id.spinnerCategory)
        sortingSpinner = view.findViewById(R.id.spinnerSort)
        searchView = view.findViewById(R.id.searchViewProducts)

        setupCategorySpinner()
        setupSortingSpinner()
        setupSearchView()

        // Load the products from the API
        loadProducts()

        // Swipe-to-refresh functionality
        swipeRefreshLayout.setOnRefreshListener {
            loadProducts()
        }

        return view
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.product_categories,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                Log.d("HomeFragment", "Selected Category: $selectedCategory")
                filterProductsByCategory(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun setupSortingSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sorting_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortingSpinner.adapter = adapter

        sortingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedSortOption = parent.getItemAtPosition(position).toString()
                Log.d("HomeFragment", "Selected Sort Option: $selectedSortOption")
                sortProducts(selectedSortOption)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProductsBySearch(newText)
                return true
            }
        })
    }

    private fun filterProductsBySearch(query: String?) {
        Log.d("HomeFragment", "Filtering products by search query: $query")
        filteredProductList.clear()

        if (query.isNullOrEmpty()) {
            filteredProductList.addAll(productList)
        } else {
            for (product in productList) {
                val productRating = getProductRatingText(product)

                // Check if the query matches product name, vendor, or rating
                if (product.name.contains(query, ignoreCase = true) ||
                    product.vendorId.contains(query, ignoreCase = true) ||
                    productRating.contains(query, ignoreCase = true)
                ) {
                    filteredProductList.add(product)
                }
            }
        }
        productAdapter.notifyDataSetChanged()
        Log.d("HomeFragment", "Filtered product count: ${filteredProductList.size}")
    }

    private fun getProductRatingText(product: Product): String {

        val productView =
            recyclerViewProducts.findViewHolderForAdapterPosition(productList.indexOf(product)) as? ProductAdapter.ProductViewHolder
        return productView?.textViewRating?.text?.toString()?.replace("Rating: ", "") ?: ""
    }

    private fun filterProductsByCategory(category: String) {
        Log.d("HomeFragment", "Filtering products by category: $category")
        filteredProductList.clear()

        if (category == "All") {
            filteredProductList.addAll(productList)
        } else {
            for (product in productList) {
                if (product.name.contains(category, ignoreCase = true)) {
                    filteredProductList.add(product)
                }
            }
        }
        productAdapter.notifyDataSetChanged()
        Log.d("HomeFragment", "Filtered product count: ${filteredProductList.size}")
    }

    private fun sortProducts(sortOption: String) {
        Log.d("HomeFragment", "Sorting products by: $sortOption")
        when (sortOption) {
            "A-Z" -> {
                filteredProductList.sortBy { it.name }
            }

            "Z-A" -> {
                filteredProductList.sortByDescending { it.name }
            }

            "Price: Low to High" -> {
                filteredProductList.sortBy { it.price }
            }

            "Price: High to Low" -> {
                filteredProductList.sortByDescending { it.price }
            }

            "Rating: Low to High" -> {
                filteredProductList.sortBy { it.rating }  // Assuming 'rating' is a field in your Product class
            }

            "Rating: High to Low" -> {
                filteredProductList.sortByDescending { it.rating }
            }

            "Default" -> {
                filteredProductList.clear()
                filteredProductList.addAll(productList)
            }
        }
        productAdapter.notifyDataSetChanged()
        Log.d("HomeFragment", "Sorted product count: ${filteredProductList.size}")
    }


    private fun loadProducts() {
        Log.d("API_CALL", "Starting API call to load products")

        val response = RetrofitInstance.Productapi.getProducts()
        Log.d("prod response", response.toString())

        // Make API request
        response.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    // Update the product list with the response data
                    productList.clear()
                    productList.addAll(response.body() ?: emptyList())
                    filteredProductList.clear()
                    filteredProductList.addAll(productList)
                    productAdapter.notifyDataSetChanged()
                    Log.d("HomeFragment", "Loaded ${productList.size} products successfully")
                } else {
                    Log.e("HomeFragment", "Error loading products: ${response.message()}")
                }
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("HomeFragment", "API call failed: ${t.message}")
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }
}

