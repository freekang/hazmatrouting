/**********************/
/* FICHIER graph.cpp */
/**********************/

#include "Graph.h"

#include<math.h>
#include<cstring>
#include<cstdio>
#include<cstdlib>
#include<string>

Graph::Graph() {
  list_Com_Orig_Dest.clear();
}

Graph::~Graph() {

}

void Graph::lecture_instance(const char *nom)
{
  
  ifstream fic(nom);
  if (!(fic))
    {
      cout<<"Erreur dans la lecture du fichier"<<endl;
      cout<<"le fichier "<<nom<<" n'a pas pu etre ouvert."<<endl;
    }
  else //fichier ouvert avec succes
    {
	cout<<"fichier de donnees ouvert avec succes"<<endl;
	string buffer;
	fic >> buffer;
// 	cout<<buffer<<endl;
	fic>>buffer;	// nombre de sommets
// 	cout<<buffer<<endl;
	nbSommets = atoi(buffer.c_str());
	// Creer les sommets: ils sont numerotes de 0, ...
	for (int i = 0; i<nbSommets; i++) {
	  vect_Sommets.push_back(new Sommet(i));
	}	
	fic>>buffer>>buffer;
	nb_Com = atoi(buffer.c_str());		// nombre de commodites	
	fic>>buffer>>buffer;
	nbReg = atoi(buffer.c_str());		// nombre de regions - zones -	
	// section Arcs
	fic>>buffer>>buffer;
	nbArcs = 0;	// Nombre d'arcs
	while (buffer != "source") {
// 	  cout<<"Arc ";
	  int no = atoi(buffer.c_str()); 
// 	  cout<<no<<"  ";
	  fic>>buffer;
	  int nd = atoi(buffer.c_str());
// 	  cout<<nd<<"\n";
	  Arc* newArc = new Arc(retournerSommet(no), retournerSommet(nd), nbArcs);
	  liste_arcs.push_back(newArc);
	  retournerSommet(no)->ajouterArcSortant(newArc);
	  retournerSommet(nd)->ajouterArcEntrant(newArc);
	  nbArcs++;
	  fic>>buffer;
	}
	
// 	 cout<<"------------------------------\n";
// 	 cout<<"------Affichage-------\n";
// 	 cout<<"les arcs \n";
// 	 list <Arc*>::iterator it = liste_arcs.begin();
// 	   while (it != liste_arcs.end()) {
// 	    cout<<"arc num "<<(*it)->retournerNum()<<" ("<<(*it)->retournerSommetOrigine()->retournerNum()<<","<<(*it)->retournerSommetDest()->retournerNum()<<")"<<endl;
// 	    it++;
// 	   } 
// 	cout<<"nbArcs = "<<nbArcs<<endl;
// 	cout<<buffer<<endl;
	// commodites
	fic >> buffer >> buffer >>  buffer >> buffer;
// 	cout<<buffer<<endl;
	int c = 0; // initialiser le numero de la commodite
// 	cout<<"Affecter les commodites \n";
	while (buffer != "risque") {
	 
// 	  cout<<"ajouter une com \n";
	  vector<Sommet*> vs(2);
	  fic >> buffer;
// 	  cout<<"sommet origine = "<<buffer<<endl;
	  vs[0] = retournerSommet(atoi(buffer.c_str()));	// sommet origine
	  fic >> buffer;
// 	  cout<<"sommet dest = "<<buffer<<endl;
	  vs[1] = retournerSommet(atoi(buffer.c_str()));	// sommet destination
	  list_Com_Orig_Dest.push_back(vs);
	  
	  vector<int> vd(2);
	  fic >> buffer;
	  vd[0] = atoi(buffer.c_str());	// demande
	  fic >> buffer;
	  vd[1] = atoi(buffer.c_str());	// capacite
	  list_Com_Demand_Cap.push_back(vd);
	  // ligne suivante	
	  
	  fic >> buffer; // zapper le num de la commodite
	}
// 	cout<<"------------------------------\n";
// 	 cout<<"------Affichage-------\n";
// 	 cout<<"les commodites \n";
// 	 list<vector<Sommet*> >::iterator itt = list_Com_Orig_Dest.begin();
// 	 int k=0;
// 	 while (itt != list_Com_Orig_Dest.end()) {
// 	  cout<<"commodite numero "<<k<<" d'origine "<<(*itt)[0]->retournerNum() <<" et de destionation "<<(*itt)[1]->retournerNum()<<endl;
// 	  k++;
// 	  itt++;
// 	 }
	
	fic >> buffer>> buffer>> buffer>> buffer>> buffer;
// 	cout<<"IMPORTANT : "<<buffer<<endl;
	// Pour chaque arc, commodite, region, recuperer le risque
	int numCom = 0;
	while (buffer != "end") {
	  // traiter les differentes commodites
// 	  cout<<"traiter une commodite "<<endl;
// 	  cout<<"Affecter les risques \n";
	  do {	      
	      int o = atoi(buffer.c_str());
//  	      cout<<"origine de l'arc = "<<o<<endl;
	      fic >> buffer;
	      int d = atoi(buffer.c_str());
//  	      cout<<"destination de l'arc = "<<d<<endl;
// 	      Sommet* som = retournerSommet(o);
	      Arc* arc = retournerArc(retournerSommet(o), retournerSommet(d)); 
// 	      som->ajouterSommetsSucc(retournerSommet(d));
//  	      cout<<"retournerArc reussie \n";
	      fic >> buffer;
	      int r = atoi(buffer.c_str());
	      fic >> buffer;
	      int risque = atoi(buffer.c_str());
	      arc->affecterRisque(numCom, r, risque);
	      fic >> buffer;
	  } while (buffer != "commodite" && buffer != "end");
	  numCom++;
	  fic >> buffer;
// 	  cout<<"Fin affectation des risques, buffer = "<<buffer<<endl;
	 }
	 // Affichage
// 	 list <Arc*>::iterator ita = liste_arcs.begin();
// 	 while (ita != liste_arcs.end()) {
// 	  cout<<"Arc num "<<(*ita)->retournerNum()<<endl;
// 	  for(int i = 0; i<nb_Com; i++) {
// 	    for(int j = 0; j<nbReg; j++) {
// 	      cout<<"le risque associe a l'arc ("<<(*ita)->retournerSommetOrigine()->retournerNum()<<", "<<(*ita)->retournerSommetDest()->retournerNum()<<") pour la commodite "<<i<<" et la region "<<j<<" est: "<<(*ita)->retournerRisque(i,j)<<endl;
// 	    }
// 	  }
// 	  ita++;
// 	 }
     }
}

int Graph::retournerNbSommets() {
  return nbSommets;
}

int Graph::retournerNbArcs(){
  return nbArcs;
}

int Graph::retournerNbCom() {
  return nb_Com;
}

int Graph::retournerNbReg() {
  return nbReg;
}

Sommet*Graph::retournerSommet(int n) {
  return vect_Sommets[n];
}

// void Graph::affecterComOrigDest(int c, Sommet* o, Sommet* d) {
//   vector<Sommet*> l;
//   l.push_back(o);
//   l.push_back(d);  
//   list_Com_Orig_Dest.push_back(l);
// }

vector<Sommet*> Graph::retournerSommetOrigineDestCom(int c) {
  list<vector<Sommet*> >::iterator it = list_Com_Orig_Dest.begin();
  for (int i = 0; i<c; i++) 
    it++;    
  return (*it);
}

// Sommet* Graph::retournerSommetDestCom(int c) {
//   return map_Com_Orig_Dest[c][1];
// }

Arc* Graph::retournerArc(Sommet* o, Sommet* d) {
  // retourner les arcs adjacents au sommet o
  int num = d->retournerNum();
  list<Arc*> arcs = o->retournerList_ar_sortants();
//   cout<<"nombre d'arcs sortants du sommet "<<o->retournerNum()<<" = "<<arcs.size()<<endl;
  list<Arc*>::iterator itarcs = arcs.begin();
  
//    cout<<"Cherche l'arc ("<<o->retournerNum()<<","<<d->retournerNum()<<")\n";
  while (itarcs != arcs.end()) {
    Sommet* s = (*itarcs)->retournerSommetDest();
    if (s->retournerNum() == num)
      return (*itarcs);
    else
      itarcs++;
  }
  cout<<"Pbm, Arc non trouvé: toutes les origines destinations doivent etre connecte par un arc \n";
  return (*itarcs);
}

vector <Sommet*> Graph::retournerSommetsGraphe(){
  return vect_Sommets;
}

void Graph::displayGraph() {
  
  cout<<"Nodes:\n";
  vector <Sommet*> nodes = retournerSommetsGraphe();
  for (int i = 0; i<nodes.size(); i++) {
    cout<<nodes[i]->retournerNum() << "  -  ";
  }
  cout<<endl<<"Commodities: \n";
  for (int i = 0; i<retournerNbCom(); i++) {
    cout<<"Commodity "<<i+1<<" -- Source "<<((retournerSommetOrigineDestCom(i))[0])->retournerNum()<<" -- Dest "<<((retournerSommetOrigineDestCom(i))[1])->retournerNum()<<endl;
  }
  
  cout<<"NbRegions = "<<retournerNbReg()<<endl;
  
  cout<<"Arcs:\n";
  list<Arc*> arcs = returnlisteArcs();
  list<Arc*>::iterator it;
  
  for (int i = 0; i<retournerNbCom(); i++) {
    for (int j = 0; j<retournerNbReg(); j++) {  
      for (it = arcs.begin(); it != arcs.end(); it++) {
	cout<<"OrigNode: "<<(*it)->retournerSommetOrigine()->retournerNum()<<" -- DestNode: "
	<<(*it)->retournerSommetDest()->retournerNum()<<" -- "<<i<<" -- "<<j<<" -- "<<(*it)->retournerRisque(i, j)<<endl;	
      }
    }
  } 
}

list <Arc*> Graph::returnlisteArcs() {
  return liste_arcs;
}