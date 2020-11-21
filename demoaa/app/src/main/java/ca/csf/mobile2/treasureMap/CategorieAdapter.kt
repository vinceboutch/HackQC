package ca.csf.mobile2.treasureMap

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import ca.csf.mobile2.treasureMap.categorization.Categories
import kotlinx.android.synthetic.main.categorie_activite_layout.view.*

class CategorieAdapter : BaseAdapter {

    private var categories : HashMap<Categories,Int>
    var context: Context? = null

    constructor(context: Context, categories: HashMap<Categories,Int>) : super()
    {
        this.categories = categories
        this.context = context
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        val categorieName = categories.keys.elementAt(position)
        val categorieImage = categories.values.elementAt(position)

        var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var categorieView = inflator.inflate(R.layout.categorie_activite_layout, null)

        categorieView.categorieText.text = categorieName.toString()
        categorieView.categorieImage.setImageResource(categorieImage)

        return categorieView
    }


    override fun getCount(): Int
    {
        return categories.size
    }

    override fun getItem(position: Int): Categories
    {
        return categories.keys.elementAt(position)
    }

    override fun getItemId(position: Int): Long
    {
        return position.toLong()
    }
}