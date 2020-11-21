package ca.csf.mobile2.treasureMap

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import ca.csf.mobile2.treasureMap.categorization.Categories
import ca.csf.mobile2.treasureMap.loisir.LOISIR_LIBRE
import kotlinx.android.synthetic.main.categorie_activite_layout.view.*
import kotlinx.android.synthetic.main.selection_activite.view.*

class ActivityAdapter : BaseAdapter {

    private var activites : List<LOISIR_LIBRE>
    var context: Context? = null

    constructor(context: Context, activites: List<LOISIR_LIBRE>) : super()
    {
        this.activites = activites
        this.context = context
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        val description = activites.elementAt(position).DESCRIPTION
        val adresse = activites.elementAt(position).ADRESSE

        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var activityView = inflator.inflate(R.layout.selection_activite, null)

        activityView.description.text = description
        activityView.adresse.text = adresse

        return activityView
    }


    override fun getCount(): Int
    {
        return activites.size
    }

    override fun getItem(position: Int): LOISIR_LIBRE
    {
        return activites.elementAt(position)
    }

    override fun getItemId(position: Int): Long
    {
        return position.toLong()
    }
}