package com.gmail.kenzhang0.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.lifecycle.LiveData
import com.gmail.kenzhang0.R
import com.gmail.kenzhang0.common.CommonFragment
import com.gmail.kenzhang0.databinding.WeatherSearchFragmentBinding
import com.gmail.kenzhang0.repository.CityRepo
import com.gmail.kenzhang0.ui.common.RetryCallback
import com.gmail.kenzhang0.vo.*
import com.google.android.gms.location.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.EasyPermissions


class WeatherSearchFragment : CommonFragment(), EasyPermissions.PermissionCallbacks {
    companion object {
        fun newInstance() = WeatherSearchFragment()
    }

    private lateinit var binding: WeatherSearchFragmentBinding
    private val viewModel by viewModel<WeatherSearchViewModel>()
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = WeatherSearchFragmentBinding.inflate(inflater, container, false)
        binding.retryCallback = object : RetryCallback {
            override fun retry() {
                viewModel.searchWithQuery(viewModel.recentQuery)
            }
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (EasyPermissions.hasPermissions(requireContext(), REQUEST_LOCATION_PERMISSION_COARSE, REQUEST_LOCATION_PERMISSION_FINE)) {
            getLatestLocation()
        }
        binding.btnRecentSearches.setOnClickListener {
            start(RecentSearchFragment.newInstance())
        }
        binding.liveWeatherInfo = viewModel.liveWeatherInfo as LiveData<Resource<WeatherInfo>>
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.searchMostRecent()

        val customAdapter: ArrayAdapter<City> =
            AutoCompleteTextViewAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                viewModel.getCityList()
            )
        binding.currentLocation.setOnClickListener {
            if (EasyPermissions.hasPermissions(requireContext(), REQUEST_LOCATION_PERMISSION_COARSE, REQUEST_LOCATION_PERMISSION_FINE)) {
                if (viewModel.latLon != null) { //gps...
                    viewModel.searchWithQuery(Query.gpsQuery(viewModel.latLon!!))
                } else {
                    Toast.makeText(requireContext(), getString(R.string.msg_trying_location), Toast.LENGTH_LONG).show()
                    getLatestLocation()
                }
            } else {
                EasyPermissions.requestPermissions(
                    this@WeatherSearchFragment, getString(R.string.permission_reason), REQUEST_LOCATION_PERMISSION_CODE,
                    REQUEST_LOCATION_PERMISSION_COARSE, REQUEST_LOCATION_PERMISSION_FINE
                )
            }
        }

        binding.autoCompleteCityZip.apply {
            threshold = 3 //start working from 3 characters
            setAdapter(customAdapter)
            onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                val city = parent.getItemAtPosition(position) as City
                setText(city.toString())
                viewModel.searchWithQuery(Query(QueryType.CITY.type, "${viewModel.cityId}"))
            }

            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    s?.apply {
                        viewModel.cityId = null
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            setOnEditorActionListener { view: View, actionId: Int, _: KeyEvent? ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchAction(view)
                    true
                } else {
                    false
                }
            }
            setOnKeyListener { view: View, keyCode: Int, event: KeyEvent ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    searchAction(view)
                    true
                } else {
                    false
                }

            }
        }
    }

    fun search(query: Query) {
        viewModel.searchWithQuery(query)
    }

    private fun searchAction(v: View) {
        dismissKeyboard(v.windowToken)
        viewModel.input = binding.autoCompleteCityZip.text.toString()
        if (viewModel.isValidZipCode()) {
            viewModel.searchWithQuery(Query(QueryType.ZIP.type, viewModel.input))
        } else {
            binding.autoCompleteCityZip.error = getString(R.string.msg_input_zip_city)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLatestLocation() {
        if (isLocationEnabled()) {
            mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                var location: Location? = task.result
                if (location == null) {
                    requestNewLocationData()
                } else {
                    viewModel.latLon = LatLon(location.latitude, location.longitude)
                    viewModel.latLon?.apply {
                        viewModel.searchWithQuery(Query.gpsQuery(this))
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_enable_gps_network), Toast.LENGTH_LONG).show()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var location: Location = locationResult.lastLocation
            viewModel.latLon = LatLon(location.latitude, location.longitude)
        }
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String?>) {
        Toast.makeText(requireContext(), getString(R.string.msg_location_permission_granted), Toast.LENGTH_LONG).show()
        getLatestLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String?>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Toast.makeText(requireContext(), getString(R.string.msg_location_permission_denied), Toast.LENGTH_LONG).show()
        }
    }
}

const val REQUEST_LOCATION_PERMISSION_COARSE = Manifest.permission.ACCESS_COARSE_LOCATION
const val REQUEST_LOCATION_PERMISSION_FINE = Manifest.permission.ACCESS_FINE_LOCATION
const val REQUEST_LOCATION_PERMISSION_CODE = 1

class AutoCompleteTextViewAdapter(
    private val c: Context,
    @LayoutRes private val layoutResource: Int,
    private val cities: List<City>
) :
    ArrayAdapter<City>(c, layoutResource, cities) {

    var filteredCities: List<City> = listOf()

    override fun getCount(): Int = filteredCities.size

    override fun getItem(position: Int): City = filteredCities[position]

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                @Suppress("UNCHECKED_CAST")
                filteredCities = filterResults.values as List<City>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                val filterResults = FilterResults()
                filterResults.values = if (queryString == null || queryString.isEmpty())
                    cities
                else
                    CityRepo.findCitiesByPrefix(cities, queryString)
                return filterResults
            }
        }
    }
}