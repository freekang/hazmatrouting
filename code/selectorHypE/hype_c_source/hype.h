/*========================================================================
  PISA  (http://www.tik.ee.ethz.ch/sop/pisa/)
  ========================================================================
  Computer Engineering (TIK)
  ETH Zurich
  ========================================================================
  HypE - Hypervolume Estimation Algorithm for Multiobjective Optimization

  author: Johannes Bader, johannes.bader@tik.ee.ethz.ch

  last change: 14.05.2008
  ========================================================================
 */

#ifndef HYPE_H
#define HYPE_H

/*-----------------------| specify Operating System |------------------*/
/* necessary for wait() */

/* #define PISA_WIN */
#define PISA_UNIX

/*----------------------------| macro |----------------------------------*/

#define PISA_ERROR(x) fprintf(stderr, "\nError: " x "\n"), fflush(stderr), exit(EXIT_FAILURE)

/*---------------------------| constants |-------------------------------*/
#define FILE_NAME_LENGTH 128 /* maximal length of filenames */
#define CFG_ENTRY_LENGTH 128 /* maximal length of entries in cfg file */
#define PISA_MAXDOUBLE 1E99  /* Internal maximal value for double */
#define PISA_MINDOUBLE 1E-99  /* Internal minimal value for double */

/*----------------------------| structs |--------------------------------*/

typedef struct ind_st  /* an individual */
{
	int index;
	double *f; /* objective vector */
	double fitness;
} ind;

typedef struct pop_st  /* a population */
{
	int size;
	int maxsize;
	ind **ind_array;
} pop;

typedef struct front_st /* a single nondominated set */
{
	int     size;
	int*    members;
} front;

typedef struct fp_st  /* a hierarchy of multiple nondominated sets */
{
	int     max_fronts;
	int     fronts;
	int     size;
	front*  front_array;
} fpart;

typedef enum
{
	a_better_b, b_better_a, incomparable, indifferent
} comp;

/*-------------| functions for control flow (in spea2.c) |------------*/

void write_flag(char *filename, int flag);
int read_flag(char *filename);
void wait(double sec);

/*---------| initialization function (in spea2_functions.c) |---------*/

void initialize(char *paramfile, char *filenamebase);

/*--------| memory allocation functions (in spea2_functions.c) |------*/

void* chk_malloc(size_t size);
pop* create_pop(int size, int dim);
ind* create_ind(int dim);
void create_front_part(fpart* partp, int max_pop_size);
void generateFrontPartition( fpart* front_part );
void insertInPart( fpart* partp, int nr, int id );
void cleanPart(fpart* partp);
void removeIndividual( int sel, fpart* partp, front *fp );

void free_memory(void);
void free_pop(pop *pp);
void complete_free_pop(pop *pp);
void free_ind(ind *p_ind);
void chk_free( void* handle );

/*-----| functions implementing the selection (spea2_functions.c) |---*/

void selection();
void mergeOffspring();
void environmentalSelection();
void matingSelection();
void select_initial();
void select_normal();

int dominates( int a, int b );
int irand(int range);

void hypeReduction( fpart* partp, front* fp, int alpha, double bound,
		int nrOfSamples );
void hypeFitnessFrontwise( front* fp, double bound, int nrOfSamples, int type );
int weaklyDominates( double *point1, double *point2, int no_objectives );
double drand( double from, double to );
void cleanUpArchive(fpart* partp);
void addToSelection(int i, int c );

/*--------------------| data exchange functions |------------------------*/

/* in spea2_functions.c */

int read_ini(void);
int read_var(void);
void write_sel(void);
void write_arc(void);
int check_sel(void);
int check_arc(void);

/* in hype_io.c */

int read_pop(char *filename, pop *pp, int size, int dim);
void write_pop(char *filename, pop *pp, int size);
int check_file(char *filename);

#endif /* HYPE_H */
