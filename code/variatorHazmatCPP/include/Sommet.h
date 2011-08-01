#ifndef _SOMMET_
#define _SOMMET_

#include<list>


class Graphe;
class Arc;

using namespace::std;

/** Class Sommet */

class Sommet
{
 private:
   // Variables ---------------------------------------
   int num;
   list<Arc*> list_ar_sortants;
   list<Arc*> list_ar_entrants;
   list<Sommet> list_som_sortants;
   
  public:
   // Fonctions --------------------------------------
   Sommet(int);
   ~Sommet();
   int retournerNum();
   void ajouterArcSortant(Arc*);
   void ajouterArcEntrant(Arc*);
//    void ajouterSommetsSucc(Sommet*);
   
   list<Arc*> retournerList_ar_sortants();
   int retournerNombre_ar_sortants();
//    vector<Sommet*> retournerSommetsSucc();
};

#endif
