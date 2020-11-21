package ca.csf.mobile2.treasureMap.categorization

import ca.csf.mobile2.treasureMap.R

class InformationProvider() {


    private lateinit var categories: HashMap<String?, Categories>
    private lateinit var imageHash: HashMap<Categories, Int>

    init {
        initializeCategoriesHashMap()
        initializeImageHashMap()
    }


    private fun initializeImageHashMap() {
        imageHash = hashMapOf(
            Pair(Categories.ACTIVITE_PHYSIQUE, R.drawable.activitephysique),
            Pair(Categories.ARTISANAT, R.drawable.artisanat),
            Pair(Categories.ARTISTE, R.drawable.artiste),
            Pair(Categories.CONFERENCE, R.drawable.conference),
            Pair(Categories.EVENEMENT_SPECIAL, R.drawable.evenementspecial),
            Pair(Categories.EXPOSITION, R.drawable.exposition),
            Pair(Categories.LOISIR_JEUNESS, R.drawable.loisirjeunesse),
            Pair(Categories.NATATION, R.drawable.natation),
            Pair(Categories.PATRIMOINE, R.drawable.patrimoine),
            Pair(Categories.PLEIN_AIR, R.drawable.pleinair),
            Pair(Categories.SCIENTIFIQUE, R.drawable.scientifique),
            Pair(Categories.THEATRE, R.drawable.theatre),
            Pair(Categories.MISC, R.drawable.misc)
        )
    }

    private fun initializeCategoriesHashMap() {
        categories = hashMapOf(
            Pair("1R-Activité de reconnaissance", Categories.MISC),
            Pair("1R-Activités-Physiques", Categories.ACTIVITE_PHYSIQUE),
            Pair("1R-Activités aquatiques", Categories.NATATION),
            Pair("1R-Autre activité", Categories.MISC),
            Pair("1R-Conférence", Categories.CONFERENCE),
            Pair("1R-Formation", Categories.MISC),
            Pair("Activités physiques", Categories.ACTIVITE_PHYSIQUE),
            Pair("Activités aquatiques", Categories.NATATION),
            Pair("Artisanat", Categories.ARTISANAT),
            Pair("Arts de la communication", Categories.ARTISTE),
            Pair("Arts de la scène", Categories.THEATRE),
            Pair("Arts visuels", Categories.ARTISTE),
            Pair("Camp spécialisé", Categories.MISC),
            Pair("Clubs de natation", Categories.NATATION),
            Pair("Artianat", Categories.ARTISANAT),
            Pair("Exposition", Categories.EXPOSITION),
            Pair("Formation", Categories.SCIENTIFIQUE),
            Pair("Loisir éducatif", Categories.SCIENTIFIQUE),
            Pair("Loisirs jeunesse", Categories.LOISIR_JEUNESS),
            Pair("Loisirs récréatifs", Categories.MISC),
            Pair("Mieux-être", Categories.MISC),
            Pair("Métiers d'art", Categories.ARTISTE),
            Pair("Patrimoine", Categories.PATRIMOINE),
            Pair("Programme vacance été", Categories.PLEIN_AIR),
            Pair("Relâche", Categories.MISC),
            Pair("Service de garde", Categories.LOISIR_JEUNESS),
            Pair("Sports de combat", Categories.ACTIVITE_PHYSIQUE),
            Pair("Sports de glace", Categories.ACTIVITE_PHYSIQUE),
            Pair("Sports et de plein air", Categories.PLEIN_AIR),
            Pair("Événement spécial", Categories.EVENEMENT_SPECIAL),
            Pair(null, Categories.MISC)
        )
    }

    fun GetCategoriesMap() : HashMap<Categories, Int>
    {
        return imageHash
    }

    fun GetSubCategoriesOf(categorie: Categories) : HashMap<String?, Categories>
    {
        var subCategories: HashMap<String?, Categories>  = HashMap()

        for (i in 0..categories.size -1)
        {
            if(categories.values.elementAt(i) == categorie)
                subCategories.put(categories.keys.elementAt(i), categories.values.elementAt(i))
        }

        return subCategories
    }
}