/********************/
/* FICHIER Graph.h */
/********************/

#ifndef _GRAPHE_
#define _GRAPHE_

#include<Sommet.h>
#include<Arc.h>

#include<list>
#include<vector>
#include<fstream>
#include<iostream>
#include<map>

using namespace::std;

/** Class Graph */

class Graph
{
 private:
   
   // Variables ---------------------------------------
   int nbSommets; // nombre de sommets dans le graphe
   int nbArcs; // nombre d'aretes dans le graphe
   int nb_Com; // Nombre de commodites
   int nbReg; // Nombre de regions

   vector <Sommet*> vect_Sommets; // vecteur de pointeurs sur sommets
   list <Arc*> liste_arcs; //vecteur des arcs du graphe   
   list<vector<Sommet*> > list_Com_Orig_Dest; // Pour chaque commodite: une origine et une destination
					    // les commodites sont rangees dans un ordre croissant
					    // list_Com_Orig_Dest.begin(): commodite 0
					    // list_Com_Orig_Dest.begin()+1: commodite 1 ...
  
   list<vector<int> > list_Com_Demand_Cap;  // Pour chaque commodite: la demande et la acpacite
  
  
  public:
  // Fonctions ---------------------------------------
  Graph();
  ~Graph();  
  
  void lecture_instance(const char *name);
  void display();  
  
  int retournerNbSommets();
  int retournerNbArcs();
  int retournerNbCom();
  int retournerNbReg();  
  
  Sommet* retournerSommet(int);
  vector <Sommet*> retournerSommetsGraphe();
  vector<Sommet*> retournerSommetOrigineDestCom(int);
  Arc* retournerArc(Sommet*, Sommet*); 
  list <Arc*> returnlisteArcs();
  void displayGraph();
 
};

#endif
