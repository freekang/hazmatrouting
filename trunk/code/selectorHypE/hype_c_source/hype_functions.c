/*========================================================================
  PISA  (http://www.tik.ee.ethz.ch/sop/pisa/)
  ========================================================================
  Computer Engineering (TIK)
  ETH Zurich
  ========================================================================
  HypE - Hypervolume Estimation Algorithm for Multiobjective Optimization

  author: Johannes Bader, johannes.bader@tik.ee.ethz.ch

  last change: 31.10.2008
  ========================================================================
 */


#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <math.h>

#include "hype.h"

/* common parameters */
int alpha;  /* number of individuals in initial population */
int mu;     /* number of individuals selected as parents */
int lambda; /* number of offspring individuals */
int dim;    /* number of objectives */


/* local parameters from paramfile*/
int seed;   /* seed for random number generator */
int tournament;
/**
 * mating type
 * 0 use uniform mating selection
 * 1 use tournament selection with hype fitness
*/
int mating;
double bound;
int nrOfSamples;


/* other variables */
char cfgfile[FILE_NAME_LENGTH];  /* 'cfg' file (common parameters) */
char inifile[FILE_NAME_LENGTH];  /* 'ini' file (initial population) */
char selfile[FILE_NAME_LENGTH];  /* 'sel' file (parents) */
char arcfile[FILE_NAME_LENGTH];  /* 'arc' file (archive) */
char varfile[FILE_NAME_LENGTH];  /* 'var' file (offspring) */

/* population containers */
pop *pp_all = NULL;
pop *pp_new = NULL;
pop *pp_sel = NULL;
/* front partitioning */
fpart front_part;

/*-----------------------| initialization |------------------------------*/
void initialize(char *paramfile, char *filenamebase)
/* Performs the necessary initialization to start in state 0. Reads all
 * parameters
 */
{
	FILE *fp;
	int result;
	char str[CFG_ENTRY_LENGTH];

	/* reading parameter file with parameters for selection */
	fp = fopen(paramfile, "r");
	assert(fp != NULL);

	fscanf(fp, "%s", str);
	assert(strcmp(str, "seed") == 0);
	result = fscanf(fp, "%d", &seed);

	fscanf(fp, "%s", str);
	assert(strcmp(str, "tournament") == 0);
	result = fscanf(fp, "%d", &tournament);

	fscanf(fp, "%s", str);
	assert(strcmp(str, "mating") == 0);
	result = fscanf(fp, "%d", &mating);

	fscanf(fp, "%s", str);
	assert(strcmp(str, "bound") == 0);
	result = fscanf(fp, "%lf", &bound);

	fscanf(fp, "%s", str);
	assert(strcmp(str, "nrOfSamples") == 0);
	result = fscanf(fp, "%d", &nrOfSamples);

	/* fscanf() returns EOF if reading fails. */
	assert(result != EOF); /* no EOF, parameters correctly read */
	fclose(fp);

	srand(seed); /* seeding random number generator */

	sprintf(varfile, "%svar", filenamebase);
	sprintf(selfile, "%ssel", filenamebase);
	sprintf(cfgfile, "%scfg", filenamebase);
	sprintf(inifile, "%sini", filenamebase);
	sprintf(arcfile, "%sarc", filenamebase);

	/* reading cfg file with common configurations for both parts */
	fp = fopen(cfgfile, "r");
	assert(fp != NULL);

	fscanf(fp, "%s", str);
	assert(strcmp(str, "alpha") == 0);
	fscanf(fp, "%d", &alpha);
	assert(alpha > 0);

	fscanf(fp, "%s", str);
	assert(strcmp(str, "mu") == 0);
	fscanf(fp, "%d", &mu);
	assert(mu > 0);

	fscanf(fp, "%s", str);
	assert(strcmp(str, "lambda") == 0);
	fscanf(fp, "%d", &lambda);
	assert(lambda > 0);

	fscanf(fp, "%s", str);
	assert(strcmp(str, "dim") == 0);
	result = fscanf(fp, "%d", &dim);
	assert(result != EOF); /* no EOF, 'dim' correctly read */
	assert(dim > 0);

	fclose(fp);

	/* create individual and archive pop */
	pp_all = create_pop(alpha + lambda, dim);
	pp_sel = create_pop(mu, dim);
	/* create temporary populations (indices only) */
	create_front_part(&front_part, alpha + lambda);
}


/*-------------------| memory allocation functions |---------------------*/

void* chk_malloc(size_t size)
/* Wrapper function for malloc(). Checks for failed allocations. */
{
	void *return_value = malloc(size);
	if(return_value == NULL)
		PISA_ERROR("Selector: Out of memory.");
	return (return_value);
}

void create_front_part(fpart*  partp, int  max_pop_size)
{
	int i;

	partp->max_fronts = (max_pop_size > 0 ? max_pop_size : 0);
	partp->fronts = 0;
	partp->size = 0;
	partp->front_array = (front*) chk_malloc(max_pop_size * sizeof(front));
	for (i = 0; i < max_pop_size; i++)
	{
		partp->front_array[i].size = 0;
		partp->front_array[i].members = chk_malloc(max_pop_size * sizeof(int));
	}
}

pop* create_pop(int maxsize, int dim)
/* Allocates memory for a population. */
{
	int i;
	pop *pp;

	assert(dim >= 0);
	assert(maxsize >= 0);

	pp = (pop*) chk_malloc(sizeof(pop));
	pp->size = 0;
	pp->maxsize = maxsize;
	pp->ind_array = (ind**) chk_malloc(maxsize * sizeof(ind*));

	for (i = 0; i < maxsize; i++)
		pp->ind_array[i] = NULL;

	return (pp);
}


ind* create_ind(int dim)
/* Allocates memory for one individual. */
{
	ind *p_ind;

	assert(dim >= 0);

	p_ind = (ind*) chk_malloc(sizeof(ind));

	p_ind->index = -1;
	p_ind->fitness = -1;
	p_ind->f = (double*) chk_malloc(dim * sizeof(double));
	return (p_ind);
}

void free_front_part(fpart* partp)
{
	int i;

	if (partp->front_array != NULL)
	{
		for (i = 0; i < partp->max_fronts; i++)
			if (partp->front_array[i].members != NULL)
				chk_free(partp->front_array[i].members);
		chk_free(partp->front_array);
		partp->front_array = NULL;
	}
	partp->fronts = 0;
	partp->max_fronts = 0;
	partp->size = 0;
}

void free_memory()
/* Frees all memory. */
{
	free_pop(pp_sel);
	complete_free_pop(pp_all);
	free_pop(pp_new);
	pp_sel = NULL;
	pp_all = NULL;
	pp_new = NULL;
	free_front_part(&front_part);
}

void free_pop(pop *pp)
/* Frees memory for given population. */
{
	if (pp != NULL)
	{
		free(pp->ind_array);
		free(pp);
	}
}

void complete_free_pop(pop *pp)
/* Frees memory for given population and for all individuals in the
   population. */
{
	int i = 0;
	if (pp != NULL)
	{
		if(pp->ind_array != NULL)
		{
			for (i = 0; i < pp->size; i++)
			{
				if (pp->ind_array[i] != NULL)
				{
					free_ind(pp->ind_array[i]);
					pp->ind_array[i] = NULL;
				}
			}

			free(pp->ind_array);
		}

		free(pp);
	}
}

void free_ind(ind *p_ind)
/* Frees memory for given individual. */
{
	assert(p_ind != NULL);

	free(p_ind->f);
	free(p_ind);
}

/*-----------------------| selection functions|--------------------------*/

void selection()
/**
 *  Do environmental and mating selection
 */
{
	mergeOffspring();
	generateFrontPartition( &front_part );
	environmentalSelection();
	cleanUpArchive(&(front_part));
	matingSelection();

	return;
}

void cleanUpArchive(fpart* partp)
/** Removes all individuals from pp_all that are not referenced in partp
 *
 * @param[in] partp partition of the population
 */
{
	pop* new_pop;
	int i, j;

	new_pop = create_pop(alpha + lambda, dim);
	new_pop->size = 0;
	for (i = partp->fronts - 1; i >= 0; i--)
	{
		for (j = partp->front_array[i].size - 1; j >= 0; j--)
		{
			int index = partp->front_array[i].members[j];
			new_pop->ind_array[new_pop->size] = pp_all->ind_array[index];
			pp_all->ind_array[index] = NULL;
			partp->front_array[i].members[j] = new_pop->size;
			new_pop->size++;
		}
	}
	complete_free_pop(pp_all);
	pp_all = new_pop;
}

void mergeOffspring()
/**
 * Merge the offspring individuals (pp_new) with the archive population pp_new
 *
 * @pre pp_all->maxsize >= pp_all->size + pp_new->size
 * @remark the population pp_new is added to pp_all, the population pp_new
 * 	is emptied.
 */
{
	int i;

	assert(pp_all->size + pp_new->size <= pp_all->maxsize);

	for (i = 0; i < pp_new->size; i++)
	{
		pp_all->ind_array[pp_all->size + i] = pp_new->ind_array[i];
	}

	pp_all->size += pp_new->size;

	free_pop(pp_new);
	pp_new = NULL;
}

void generateFrontPartition( fpart* front_part )
/**
 * Partition the population pp_all into fronts
 *
 * @param[out] front_part partition of the front
 * @pre the population pp_all needs to be valid
 */
{
	int i,j;
	int actFront = 0;
	int notDominated;
	int checked[ pp_all->size ];
	int actchecked[ pp_all->size ];
	int added;

	cleanPart( front_part );

	for( i = 0; i < pp_all->size; i++ )
		checked[i] = 0;

	added = 0;
	actFront = 0;
	while( added < pp_all->size )
	{
		for( i = 0; i < pp_all->size; i++ )
		{
			if( checked[i] == 1 )
				continue;
			actchecked[i] = 0;
			notDominated = 1;
			j = 0;
			while( notDominated == 1 && j < pp_all->size )
			{
				if( i != j && checked[j] == 0 && dominates(j, i) )
					notDominated = 0;
				j++;
			}
			if( notDominated )
			{
				actchecked[i] = 1;
				insertInPart(front_part, actFront, i );
				added++;
			}
		}
		for( j = 0; j < pp_all->size; j++ )
			if( actchecked[j] == 1 )
				checked[j] = 1;
		actFront++;
	}
}

void cleanPart(fpart* partp)
{
	int i;

	for (i = 0; i < partp->max_fronts; i++)
		partp->front_array[i].size = 0;
	partp->size = 0;
	partp->fronts = 0;
}

void insertInPart( fpart* partp, int nr, int index )
{
	if( partp->fronts < nr + 1 )
		partp->fronts = nr + 1;
	partp->size++;
	partp->front_array[ nr ].members[ partp->front_array[ nr ].size ] = index;
	partp->front_array[ nr ].size++;
}

void removeIndividual( int sel, fpart* partp, front *fp )
{
	assert( sel >= 0 );
	assert( sel < fp->size );
	fp->size--;
	fp->members[sel] = fp->members[fp->size];
	partp->size--;
}

void environmentalSelection()
/**
 * Selects alpha individuals of the population pp_all
 *
 * @pre pp_all and front_part set
 * @post front_part.size == alpha
 * @remark operates only on the front partition front_part. No individuals
 * 		are actually removed.
 */
{
	int i;
	/** Start with front wise reduction */
	for( i = front_part.fronts - 1; i >= 0; i-- )
	{
		if( front_part.size - front_part.front_array[i].size >= alpha )
		{
			front_part.size -= front_part.front_array[i].size;
			front_part.front_array[i].size = 0;
			front_part.fronts--;
		}
		else
			break;
	}
	/** Then remove from worst front */
	if( front_part.size > alpha )
	{
		hypeReduction( &(front_part),
				&(front_part.front_array[front_part.fronts-1]), alpha, bound,
				nrOfSamples );
	}
	assert( front_part.size == alpha );
}

void getObjectiveArray( int* A, int sizea, double* pointArray )
/* Returns an array of all objectives referenced by A */
{
	int i,k;
	for( i = 0; i < sizea; i++ )
	{
		for( k = 0; k < dim; k++ )
		{
			pointArray[ i*dim + k ] = pp_all->ind_array[ A[i] ]->f[k];
		}
	}
}

void rearrangeIndicesByColumn(double *mat, int rows, int columns, int col,
		int *ind )
/**
 * Internal function used by hypeExact
 */
{
#define  MAX_LEVELS  300
	int  beg[MAX_LEVELS], end[MAX_LEVELS], i = 0, L, R, swap;
	double pref, pind;
	double ref[rows];
	for( i = 0; i < rows; i++ ) {
		ref[i] = mat[ col + ind[i]*columns ];
	}
	i = 0;

	beg[0] = 0; end[0] = rows;
	while ( i >= 0 ) {
		L = beg[i]; R = end[i]-1;
		if( L < R ) {
			pref = ref[ L ];
			pind = ind[ L ];
			while( L < R ) {
				while( ref[ R ] >= pref && L < R )
					R--;
				if( L < R ) {
					ref[ L ] = ref[ R ];
					ind[ L++] = ind[R];
				}
				while( ref[L] <= pref && L < R )
					L++;
				if( L < R) {
					ref[ R ] = ref[ L ];
					ind[ R--] = ind[L];
				}
			}
			ref[ L ] = pref; ind[L] = pind;
			beg[i+1] = L+1; end[i+1] = end[i];
			end[i++] = L;
			if( end[i] - beg[i] > end[i-1] - beg[i-1] ) {
				swap = beg[i]; beg[i] = beg[i-1]; beg[i-1] = swap;
				swap = end[i]; end[i] = end[i-1]; end[i-1] = swap;
			}
		}
		else {
			i--;
		}
	}
}

void hypeExactRecursive( double* input_p, int pnts, int dim, int nrOfPnts,
		int actDim, double* bounds, int* input_pvec, double* fitness,
		double* rho, int param_k )
/**
 * Internal function used by hypeExact
 */
{
	int i, j;
	double extrusion;
	int pvec[pnts];
	double p[pnts*dim];
	for( i = 0; i < pnts; i++ ) {
		fitness[i] = 0;
		pvec[i] = input_pvec[i];
	}
	for( i = 0; i < pnts*dim; i++ )
		p[i] = input_p[i];

	rearrangeIndicesByColumn( p, nrOfPnts, dim, actDim, pvec );

	for( i = 0; i < nrOfPnts; i++ )
	{
		if( i < nrOfPnts - 1 )
			extrusion = p[ (pvec[i+1])*dim + actDim ] - p[ pvec[i]*dim + actDim ];
		else
			extrusion = bounds[actDim] - p[ pvec[i]*dim + actDim ];

		if( actDim == 0 ) {
			if( i+1 <= param_k )
				for( j = 0; j <= i; j++ ) {
					fitness[ pvec[j] ] = fitness[ pvec[j] ]
					                              + extrusion*rho[ i+1 ];
				}
		}
		else if( extrusion > 0 ) {
			double tmpfit[ pnts ];
			hypeExactRecursive( p, pnts, dim, i+1, actDim-1, bounds, pvec,
					tmpfit, rho, param_k );
			for( j = 0; j < pnts; j++ )
				fitness[j] += extrusion*tmpfit[j];
		}
	}
}

void hypeExact( double* val, int popsize, double lowerbound, double upperbound,
		int param_k, double* points, double* rho  )
/**
 * Calculating the hypeIndicator
 * \f[ \sum_{i=1}^k \left( \prod_{j=1}^{i-1} \frac{k-j}{|P|-j} \right) \frac{ Leb( H_i(a) ) }{ i } \f]
 */
{
	int i;;
	double boundsVec[ dim ];
	int indices[ popsize ];
	for( i = 0; i < dim; i++ )
		boundsVec[i] = bound;
	for( i = 0; i < popsize; i++  )
		indices[i] = i;

	/** Recursively calculate the indicator values */
	hypeExactRecursive( points, popsize, dim, popsize, dim-1, boundsVec,
			indices, val, rho, param_k );
}

void hypeSampling( double* val, int popsize, double lowerbound,
		double upperbound, int nrOfSamples, int param_k, double* points,
		double* rho )
/**
 * Sampling the hypeIndicator
 * \f[ \sum_{i=1}^k \left( \prod_{j=1}^{i-1} \frac{k-j}{|P|-j} \right) \frac{ Leb( H_i(a) ) }{ i } \f]
 *
 * @param[out] val vector of all indicators
 * @param[in] popsize size of the population \f$ |P| \f$
 * @param[in] lowerbound scalar denoting the lower vertex of the sampling box
 * @param[in] upperbound scalar denoting the upper vertex of the sampling box
 * @param[in] nrOfSamples the total number of samples
 * @param[in] param_k the variable \f$ k \f$
 * @param[in] points matrix of all objective values dim*popsize entries
 * @param[in] rho weight coefficients
 * @pre popsize >= 0 && lowerbound <= upperbound && param_k >= 1 &&
 * 		param_k <= popsize
 */
{
	assert(popsize >= 0 );
	assert( lowerbound <= upperbound );
	assert( param_k >= 1 );
	assert( param_k <= popsize );

	int i, s, k;
	int hitstat[ popsize ];
	int domCount;

	double sample[ dim ];
	for( s = 0; s < nrOfSamples; s++ )
	{
		for( k = 0; k < dim; k++ )
			sample[ k ] = drand( lowerbound, upperbound );

		domCount = 0;
		for( i = 0; i < popsize; i++ )
		{
			if( weaklyDominates( points + (i*dim), sample, dim) )
			{
				domCount++;
				if( domCount > param_k )
					break;
				hitstat[i] = 1;
			}
			else
				hitstat[i] = 0;
		}
		if( domCount > 0 && domCount <= param_k )
		{
			for( i = 0; i < popsize; i++ )
				if( hitstat[i] == 1 )
					val[i] += rho[domCount];
		}
	}
	for( i = 0; i < popsize; i++ )
	{
		val[i] = val[i] * pow( (upperbound-lowerbound), dim ) / (double)nrOfSamples;
	}
}

void hypeIndicator( double* val, int popsize, double lowerbound,
		double upperbound, int nrOfSamples, int param_k, double* points )
/**
 * Determine the hypeIndicator
 * \f[ \sum_{i=1}^k \left( \prod_{j=1}^{i-1} \frac{k-j}{|P|-j} \right) \frac{ Leb( H_i(a) ) }{ i } \f]
 *
 * if nrOfSamples < 0, then do exact calculation, else sample the indicator
 *
 * @param[out] val vector of all indicator values
 * @param[in] popsize size of the population \f$ |P| \f$
 * @param[in] lowerbound scalar denoting the lower vertex of the sampling box
 * @param[in] upperbound scalar denoting the upper vertex of the sampling box
 * @param[in] nrOfSamples the total number of samples or, if negative, flag
 * 		that exact calculation should be used.
 * @param[in] param_k the variable \f$ k \f$
 * @param[in] points matrix of all objective values dim*popsize entries
 * @param[in] rho weight coefficients
 */
{
	int i,j;
	double rho[param_k+1];
	/** Set alpha */
	rho[0] = 0;
	for( i = 1; i <= param_k; i++ )
	{
		rho[i] = 1.0 / (double)i;
		for( j = 1; j <= i-1; j++ )
			rho[i] *= (double)(param_k - j ) / (double)( popsize - j );
	}
	for( i = 0; i < popsize; i++ )
		val[i] = 0.0;

	if( nrOfSamples < 0 )
		hypeExact( val, popsize, lowerbound, upperbound, param_k, points,
				rho );
	else
		hypeSampling( val, popsize, lowerbound, upperbound, nrOfSamples,
				param_k, points, rho );
}

void determineIndexAndFront(fpart* partp, int n, int* index, int* front,
		int sort)
/** Determine the front and the referencing index of the
 * nth individual
 *
 * @param[in] partp front paritioning
 * @param[in] n index of the individual to be retrieved
 * @param[out] index index the individuals is referencing
 * @param[out] front front the individual is in.
 * @param[out] sort if sorting = 0, don't use front partition
 * @pre n >= 0 && n < partp->size - 1
 * @post front >= 0 && front < partp->fronts, index >= 0 &&
 * 	index <= partp->size
 */
{
	assert( n >= 0 );
	assert( n <  partp->size );
	if( sort == 0 )
		*index = n;

	int i;

	i = partp->fronts - 1;
	while (i >= 0 && (n - partp->front_array[i].size) >= 0)
	{
		n -= partp->front_array[i].size;
		i--;
	}
	assert(i >= 0 );
	assert(n >= 0 );
	assert(n < partp->front_array[i].size);
	*index = partp->front_array[i].members[n];
	*front = i;
}

void hypeReduction( fpart* partp, front* fp, int alpha, double bound,
		int nrOfSamples )
/**
 * Iteratively remove individuals from front based on sampled hypeIndicator
 * value
 *
 * @param[in] partp partition of the population pp_all
 * @param[in] fp the current front to operate on
 * @param[in] alpha the number of individuals in partp after removal
 * @param[in] bound scaler denoting the upper bound
 * @param[in] nrOfSamples the number of Samples per iteration. If negative, use
 * 		exact hypeIndicator calculation
 * @pre fp->size > 0
 * @pre partp->size >= alpha
 * @post partp->size == alpha
 */
{
	double val[ fp->size];
	double points[ fp->size*dim ];
	double checkRate;
	double minRate = -1;
	int sel = -1;
	int i;

	assert( fp->size > 0 );
	getObjectiveArray( fp->members, fp->size, points );

	while( partp->size > alpha )
	{
		hypeIndicator( val, fp->size, 0, bound, nrOfSamples,
					partp->size - alpha, points );

		sel = -1;
		for( i = 0; i < fp->size; i++ )
		{
			checkRate = val[i];
			if( sel == -1  || checkRate < minRate ) {
				minRate = checkRate;
				sel = i;
			}
		}
		assert( sel >= 0 );
		assert( sel < fp->size );
		memcpy( points + dim*sel, points + dim*(fp->size - 1),
					sizeof(double)*dim );
		/** Removing Individual <sel> */
		fp->size--;
		fp->members[sel] = fp->members[fp->size];
		partp->size--;
	}
}

void hypeFitnessMating( double bound, int nrOfSamples )
/**
 * Calculates the fitness of all individuals in pp_all based on the hypE
 * indicator
 *
 * @param[in] bound used by hypeIndicator
 * @param[in] nrOfSamples used by hypeIndicator
 */
{
	int i;
	double val[ pp_all->size ];
	double points[ pp_all->size*dim ];
	int indices[ pp_all->size ];
	for( i = 0; i < pp_all->size; i++ )
		indices[i] = i;
	getObjectiveArray( indices, pp_all->size, points );
	hypeIndicator( val, pp_all->size, 0, bound, nrOfSamples, pp_all->size,
			points );
	for( i = 0; i < pp_all->size; i++ )
		pp_all->ind_array[ i ]->fitness = val[i];
}

void matingSelection()
/**
 * Select parents individuals from pp_all and add them to pp_sel
 */
{
	int winner, winnerFront;
	int opponent, opponentFront;
	int i,j;

	if( mating == 1  )
		hypeFitnessMating( bound, nrOfSamples );
	else
		for( i = 0; i < pp_all->size; i++ )
			pp_all->ind_array[i]->fitness = 0.0;

	pp_sel->size = mu;
	for (i = 0; i < mu; i++)
	{
		determineIndexAndFront(&front_part, irand( pp_all->size ),
				&winner, &winnerFront, !( mating == 2) );
		assert( winner < pp_all->size );
		assert( winner >= 0 );
		for (j = 1; j < tournament; j++)
		{
			determineIndexAndFront(&front_part, irand( front_part.size ),
					&opponent, &opponentFront, !( mating == 2) );
			assert( opponent < pp_all->size );
			assert( opponent >= 0 );
			if (opponentFront < winnerFront || (opponentFront == winnerFront &&
					pp_all->ind_array[opponent]->fitness >
			pp_all->ind_array[winner]->fitness ))
			{
				winner = opponent;
				winnerFront = opponentFront;
			}
		}
		addToSelection( i, winner );
	}
}

void addToSelection(int i, int c )
/**
 * adds the cth individual of pp_all to the ith place of pp_sel
 *
 * @param[in] c index of pp_all to be added
 * @param[in] i index of pp_sel to be replaced
 * @pre 0 <= i < pp_sel->size
 * @pre 0 <= c <= pp_all->size
 */
{
	assert( 0 <= i );
	assert( i < pp_sel->size );
	assert( 0 <= c );
	assert( c < pp_all->size );
	pp_sel->ind_array[i] = pp_all->ind_array[c];
}


void select_initial()
/* Performs initial selection. */
{
	selection();
}


void select_normal()
/* Performs normal selection.*/
{
	selection();
}

int irand(int range)
/* Generate a random integer. */
{
	int j;
	j=(int) ((double)range * (double) rand() / (RAND_MAX+1.0));
	return (j);
}

double drand( double from, double to )
{
	double j;
	j = from + (double)( (to-from)*rand() / ( RAND_MAX + 1.0) );
	return (j);
}

int dominates(int a, int b)
/* Determines if one individual dominates another.
   Minimizing fitness values. */
{
	int i;
	int a_is_worse = 0;
	int equal = 1;

	for (i = 0; i < dim && !a_is_worse; i++)
	{
		a_is_worse = pp_all->ind_array[a]->f[i] > pp_all->ind_array[b]->f[i];
		equal = (pp_all->ind_array[a]->f[i] == pp_all->ind_array[b]->f[i]) && equal;
	}

	return (!equal && !a_is_worse);
}

int weaklyDominates( double *point1, double *point2, int no_objectives )
{
	int better;
	int i = 0;
	better = 1;


	while( i < no_objectives && better )
	{
		better = point1[i] <= point2[i];
		i++;
	}
	return better;
}


comp dominanceCheckInd(int a, int b)
/* objectives are to be minimized */
{
	int i;
	int aWorse = 0;
	int bWorse = 0;

	for (i = 0; i < dim && !(aWorse && bWorse); i++)
	{
		aWorse = (aWorse || (pp_all->ind_array[a]->f[i] >
		pp_all->ind_array[b]->f[i] ) );
		bWorse = (bWorse || (pp_all->ind_array[a]->f[i] <
				pp_all->ind_array[b]->f[i] ) );
	}
	if (aWorse && bWorse)
		return incomparable;
	else if (aWorse)
		return b_better_a;
	else if (bWorse)
		return a_better_b;
	else
		return indifferent;
}

/*--------------------| data exchange functions |------------------------*/

int read_ini()
{
	int i;
	pp_new = create_pop(alpha, dim);

	for (i = 0; i < alpha; i++)
		pp_new->ind_array[i] = create_ind(dim);
	pp_new->size = alpha;

	return (read_pop(inifile, pp_new, alpha, dim));
}


int read_var()
{
	int i;
	pp_new = create_pop(lambda, dim);

	for (i = 0; i < lambda; i++)
		pp_new->ind_array[i] = create_ind(dim);

	pp_new->size = lambda;
	return (read_pop(varfile, pp_new, lambda, dim));
}


void write_sel()
{
	assert( pp_sel->size == mu );
	write_pop(selfile, pp_sel, mu);
}


void write_arc()
{
	assert( pp_all->size == alpha );
	write_pop(arcfile, pp_all, pp_all->size);
}


int check_sel()
{
	return (check_file(selfile));
}


int check_arc()
{
	return (check_file(arcfile));
}

void chk_free( void* handle )
{
	if( handle != NULL ) {
		free( handle );
	}
}
