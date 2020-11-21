package ca.csf.mobile2.treasureMap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.GridView
import ca.csf.mobile2.treasureMap.categorization.InformationProvider
import org.androidannotations.annotations.*
import android.widget.*
import ca.csf.mobile2.treasureMap.loisir.Loisir
import com.google.gson.Gson
import java.io.InputStream
import java.nio.charset.Charset
import ca.csf.mobile2.treasureMap.loisir.LOISIR_LIBRE
import kotlinx.android.synthetic.main.categorie_activite_layout.view.*
import ca.csf.mobile2.treasureMap.categorization.Categories
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.selection_activite.view.*
import android.view.animation.LinearInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import ca.csf.mobile2.treasureMap.Sites.Sites


@EActivity(R.layout.activity_main)
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var map: GoogleMap

    private var latitude :Double?= null

    private var longitude:Double?= null


    private val locationRequestCode = 1000

    val rotate = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)

    var adresseSelected = ""
    var descriptionSelected = ""

    var activitiesWithSelectedCategories : List<LOISIR_LIBRE> = listOf()

    var informationProvider = InformationProvider()

    var adapter: CategorieAdapter = CategorieAdapter(this, informationProvider.GetCategoriesMap())

    lateinit var activityAdapter: ActivityAdapter

    @ViewById(R.id.rootView)
    protected lateinit var rootView: View

    @ViewById(R.id.grid)
    protected lateinit var grid : GridView

    @ViewById(R.id.list)
    protected lateinit var list : ListView

    @ViewById(R.id.mapFrame)
    protected lateinit var gMap : ConstraintLayout

    @ViewById(R.id.backButton)
    protected lateinit var backButton : Button

    @ViewById(R.id.indication)
    protected lateinit var indication : TextView

    @ViewById(R.id.mapAdresse)
    protected lateinit var mapAdresse : TextView

    @ViewById(R.id.mapDescription)
    protected lateinit var mapDescription : TextView

    @ViewById(R.id.loadingImage)
    protected lateinit var loadingImage : CircleImageView

    protected lateinit var loisirObject : Loisir

    protected lateinit var sitesObject : Sites

    @InstanceState
    protected lateinit var selectedCategorieText : String

    var context = this

    @AfterViews
    protected fun afterViews()
    {
        rotate.duration = 4000
        rotate.interpolator = LinearInterpolator()
        rotate.repeatCount = RotateAnimation.INFINITE

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.gmap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        grid.adapter = adapter

        loisirObject = DeserialiseLoisirJSONFile()


        grid.onItemClickListener = object : AdapterView.OnItemClickListener
        {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long)
            {
                selectedCategorieText = view.categorieText.text.toString()

                hideCategories()
                showActivites()
            }
        }

        list.onItemClickListener = object : AdapterView.OnItemClickListener
        {
            @Background
            protected fun setMarkers()
            {
                map.clear()
                for (i in 0..activitiesWithSelectedCategories.size -1)
                {
                    if(activitiesWithSelectedCategories.elementAt(i).ADRESSE != adresseSelected)
                        map.addMarker(MarkerOptions().title(activitiesWithSelectedCategories.elementAt(i).DESCRIPTION).position(getLocationFromAddress(context, activitiesWithSelectedCategories.elementAt(i).ADRESSE!!)!!)!!)
                }

                var locationSelected : LatLng = getLocationFromAddress(context, adresseSelected)!!

                map.addMarker(MarkerOptions().position(locationSelected).title(descriptionSelected).icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))!!)



                var location: CameraUpdate  = CameraUpdateFactory.newLatLngZoom(locationSelected, 13f)
                map.animateCamera(location)

            }

            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long)
            {
                showLoading()
                hideActivites()

                Handler().postDelayed({
                    setMarkers()
                    showMap()
                    hideLoading()
                }, 1000)


                adresseSelected = view.adresse.text.toString()
                descriptionSelected = view.description.text.toString()




            }
        }

        hideMap()
    }

    @UiThread
    protected fun showLoading()
    {

        loadingImage.visibility = View.VISIBLE
        loadingImage.startAnimation(rotate)
        indication.text = ""

    }

    @UiThread
    protected fun hideLoading()
    {
        loadingImage.visibility = View.INVISIBLE
        loadingImage.clearAnimation()
    }

    @UiThread
    protected fun showMap()
    {
        indication.text = getString(R.string.trouvez)
        backButton.visibility = View.VISIBLE
        gMap.visibility = View.VISIBLE

        mapAdresse.text = adresseSelected
        mapDescription.text = descriptionSelected
    }

    @UiThread
    protected fun hideMap()
    {
        gMap.visibility = View.INVISIBLE
    }

    @UiThread
    protected fun showActivites()
    {
        backButton.visibility = View.VISIBLE
        activitiesWithSelectedCategories = GetAllActivitiesWithSelectedCategorie()
        indication.text = getString(R.string.activite)

        activityAdapter = ActivityAdapter(this, activitiesWithSelectedCategories)
        list.adapter = activityAdapter

        list.visibility = View.VISIBLE
    }

    @UiThread
    protected fun hideActivites()
    {
        backButton.visibility = View.INVISIBLE
        list.visibility = View.INVISIBLE
    }

    @UiThread
    protected fun showCategories()
    {
        grid.visibility = GridView.VISIBLE
        indication.text = getString(R.string.categorie)
    }


    @UiThread
    protected fun hideCategories()
    {
        grid.visibility = View.INVISIBLE
    }

    @Click(R.id.backButton)
    protected fun onReturnClicked()
    {
        if(list.visibility == View.VISIBLE)
        {
            hideActivites()
            showCategories()
        }
        else
        {
            showActivites()
            hideMap()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        getCurrentLocation()

        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MainActivity.LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
    }

    private fun getLocationFromAddress(context: Context, strAddress: String): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var convertedAdress: LatLng? = null

        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location = address[0]
            location.getLatitude()
            location.getLongitude()

            convertedAdress = LatLng(location.getLatitude(), location.getLongitude())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return convertedAdress

    }

    private fun getCurrentLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request for permission
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                locationRequestCode
            )

        } else {
            val mLocationRequest = LocationRequest.create()
            mLocationRequest.interval = 60000
            mLocationRequest.fastestInterval = 5000
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val mLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    if (locationResult == null) {
                        return
                    }
                    for (location in locationResult.locations) {
                        if (location != null) {

                            latitude = location.latitude
                            longitude = location.longitude
                            Log.v("location", latitude.toString())
                            Log.v("location", longitude.toString())

                            var myPosition = LatLng(latitude!!, longitude!!)
                            map.addMarker(MarkerOptions().position(myPosition).title("My position"))

                        }
                    }
                }
            }
            LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(mLocationRequest, mLocationCallback, null)

        }
    }

    protected fun ReadJSONFile(id: Int): String
    {
        var ins: InputStream = resources.openRawResource(id)
        var content= ins.readBytes().toString(Charset.defaultCharset())
        return content
    }

    protected fun DeserialiseLoisirJSONFile() : Loisir
    {
        var gson = Gson()
        var content = ReadJSONFile(R.raw.loisir)

        return gson.fromJson(content, Loisir::class.java)
    }

    protected fun DeserialiseSiteJSONFile() : Sites
    {
        var gson = Gson()
        var content = ReadJSONFile(R.raw.sites_loisir)

        return gson.fromJson(content, Sites::class.java)
    }

    private fun GetAllActivitiesWithSelectedCategorie() : List<LOISIR_LIBRE>
    {
        var subCategories: HashMap<String?, Categories> = informationProvider.GetSubCategoriesOf(Categories.valueOf(selectedCategorieText))

        var activites : List<LOISIR_LIBRE> = loisirObject.LOISIRS_LIBRES.LOISIR_LIBRE

        var activitiesFiltre : List<LOISIR_LIBRE> = listOf()

        for(i in 0..subCategories.size -1)
        {
                activitiesFiltre += activites.filter { it.DESCRIPTION_NAT == subCategories.keys.elementAt(i) }
        }

        activitiesFiltre = activitiesFiltre.distinctBy {it.ADRESSE}

        return activitiesFiltre
    }
}
