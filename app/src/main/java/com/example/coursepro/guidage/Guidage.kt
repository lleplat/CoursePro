package com.example.coursepro.guidage

import com.example.coursepro.lists.ItemToDo
import com.example.coursepro.lists.ListeToDo
import java.lang.IllegalArgumentException
import java.util.ArrayList
import kotlin.math.min

class Guidage(){

// creation du graphe
    var baby = Vertex("Bébé",0)
    var drinks = Vertex("Boissons",1)
    var freshProducts = Vertex("Produits frais",2)
    var frozenFood = Vertex("Surgelés",3)
    var fruitVegetables = Vertex("Fruits et légumes",4)
    var housePastimes = Vertex("Maison & loisirs",5)
    var hygieneBeauty = Vertex("Hygiène & beauté",6)
    var maintenance = Vertex("Entretien",7)
    var market = Vertex("Le Marché",8)
    var petShop = Vertex("Animalerie",9)
    var saltedGrocery = Vertex("Epicerie salée",10)
    var sugaryGrocery = Vertex("Epicerie sucrée",11)

    fun init() {
        mkVoisinsFromList(baby,  listOf(5, 6, 7))
        mkVoisinsFromList(housePastimes, listOf(6, 7))
        mkVoisinsFromList(hygieneBeauty, listOf(7,9, 1))
        mkVoisinsFromList(maintenance, listOf(9, 1))
        mkVoisinsFromList(petShop, listOf(1, 10,11))
        mkVoisinsFromList(drinks, listOf(10, 11))
        mkVoisinsFromList(saltedGrocery, listOf(11, 2,4,8))
        mkVoisinsFromList(sugaryGrocery, listOf( 2,4,8))
        mkVoisinsFromList(freshProducts, listOf( 4,8,3))
        mkVoisinsFromList(fruitVegetables, listOf( 8,3))
        mkVoisinsFromList(market, listOf( 3))
    }

    var categoriesList : List<Vertex> = listOf(baby,drinks,freshProducts,frozenFood,fruitVegetables,housePastimes,hygieneBeauty,maintenance,market,petShop,saltedGrocery,sugaryGrocery)



//rend deux sommets voisins en empêchant les doublons
    fun mkVoisins (vertex1: Vertex, vertex2 : Vertex)  {
        if (!vertex2.adjList.contains(vertex1.id)){
            vertex1.adjList.add(vertex2.id)
            vertex2.adjList.add(vertex1.id)
        }
    }

    //crée les voisins d'un sommet
    fun mkVoisinsFromList(vertex : Vertex,list : List<Int>) {

            for (i in list.indices){
                mkVoisins(vertex,categoriesList[i])

        }

    }
    //retourne la categorie
    fun getCategorieItem (itemToDo: ItemToDo) : Int {
        val categorie = itemToDo.headerName
        for (item in categoriesList){
            if (item.nom==categorie){
                return item.id
            }
        }
        error("L'item n'appartient à aucune catégorie connue")
    }

    //retourne tous les items non faits de la même catégorie que le dernier item récupéré
    fun getMemeCategorie (itemToDo: ItemToDo, listeToDo: ListeToDo) : MutableList<ItemToDo>{
        val res = mutableListOf<ItemToDo>()
        for (i in listeToDo.lesItems){
            if ((i.headerName==itemToDo.headerName)&&(!i.fait)){
                res.add(i)
            }
        }
        return res
    }

    //retourne tous les items pas encore récupérés d'une catégorie donnée dans la liste
    fun findCategorieDansListe (id : Int, listeToDo: ListeToDo) : MutableList<ItemToDo>{
        val res = mutableListOf<ItemToDo>()
        for (i in listeToDo.lesItems.indices){
            if ((listeToDo.lesItems[i].headerName == categoriesList[i].nom)&&(!listeToDo.lesItems[i].fait)){
                res.add(listeToDo.lesItems[i])
            }
        }
        return res
    }

    //retourne la catégorie la plus proche non encore visitée, contenant des items pas encore récupérés
    fun findClosestCategorieNonVisitee (id: Int, listeToDo: ListeToDo, Dejavisites:MutableList<Int> = mutableListOf(),aVisiter:MutableList<Int> = mutableListOf()) : Int {
        val categorie = categoriesList[id]
        val Voisins = categorie.adjList

        for (i in Voisins){
            if (!(i in Dejavisites)&&(!findCategorieDansListe(i,listeToDo).isEmpty())){
                aVisiter.add(i)
            }
        }
        if (aVisiter.isEmpty()){
            return -1

        }
        aVisiter.addAll(Voisins)
        for (i in aVisiter){
            if (!Dejavisites.contains(i)){
                return(i)
            }}
        return (findClosestCategorieNonVisitee(aVisiter[0],listeToDo,Dejavisites,aVisiter.subList(1,aVisiter.size)))


        }


    //retourne le prochain item à récupérer OU null si tous les items ont déjà été récupérés
    //il ne faut pas oublier de changer le statut des items récupérés avec une autre fonction
    fun getNextItem(itemToDo: ItemToDo, listeToDo: ListeToDo) : ItemToDo? {
        val categorie = getCategorieItem(itemToDo)
    //s'il reste des items pas encore récupérés dans la même catégorie
        if (getMemeCategorie(itemToDo,listeToDo).size!=0){
            return getMemeCategorie(itemToDo,listeToDo)[0]
        }
    //sinon on cherche la catégorie la plus proche dans laquelle il reste des items à récupérer
    //si cette catégorie n'existe pas on retourne null : on a fini la liste des courses
        else{
            val i = findClosestCategorieNonVisitee(categorie,listeToDo)
            if (i==-1){
                return null
            }
            else {
                return(findCategorieDansListe(i,listeToDo)[0])
            }
            }
        }
    }


     class Vertex//Vertex Constructor.
         (nom: String, id : Int) {
        var nom //int vertexNum
                = nom
         var id = id
        var adjList //list of adjacent vertices.
                : MutableList<Int> = mutableListOf()


     }

