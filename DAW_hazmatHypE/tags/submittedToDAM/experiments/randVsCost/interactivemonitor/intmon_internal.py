# ========================================================================
#  PISA  (www.tik.ee.ethz.ch/pisa/)
# 
#  ========================================================================
#  Computer Engineering Lab (TIK)
#  ETH Zurich
# 
#  ========================================================================
#  Interactive Monitor 
#  
#  internal classes and functions such as IntMonParameters and IntMonState
#  
#  python implementation
#  
#  file: intmon_internal.py
#  author: Dimo Brockhoff  <brockho@lix.polytechnique.fr>
#
#  last change: April 2011
#  
#  ========================================================================

from collections import deque
import sys
import numpy as np
#import matplotlib.pyplot as plt
#import time


class IntMonParameters:
    """ stores all parameters of the interactive PISA monitor and provides\n the corresponding file input/output methods """
    
    def read_common_parameters(self,filename):
        """ reads common parameters alpha, mu, lam, and dimension from 'cfg' file and tests for inconsistencies """

        try:
            f = open(filename, 'r')
        except IOError:
            print 'Problem reading file ' + filename

        for line in f:
            if 'alpha' in line:
                newalpha = int(line[6:len(line)])
            if 'mu' in line:
                newmu = int(line[3:len(line)])
            if 'lambda' in line:
                newlam = int(line[7:len(line)])
            if 'dim' in line:
                newdim = int(line[4:len(line)])

        f.closed

        # test for inconsistency:
        if self.alpha == 0:
            self.alpha = newalpha
            self.mu = newmu
            self.lam = newlam
            self.dimension = newdim
        else:
            assert newalpha == self.alpha
            assert newmu == self.mu
            assert newlam == self.lam
            assert newdim == self.dimension

        
        
    # pre-condition: args contains exactly 7 entries
    def read_selectorvariator_parameters(self,args):
        """ reads parameters from files """
        self.poll = args.pop()
        self.filenamebase_monitor = args.pop()
        self.parameter_file_monitor = args.pop()
        self.filenamebase_selector = args.pop()
        self.parameter_file_selector = args.pop()
        self.filenamebase_variator = args.pop()
        self.parameter_file_variator = args.pop()
        
        
        # generate file names based on 'filenamebase's
        self.var_file_variator = self.filenamebase_variator + 'var'
        self.sel_file_variator = self.filenamebase_variator + 'sel'
        self.cfg_file_variator = self.filenamebase_variator + 'cfg'
        self.ini_file_variator = self.filenamebase_variator + 'ini'
        self.arc_file_variator = self.filenamebase_variator + 'arc'
        self.sta_file_variator = self.filenamebase_variator + 'sta'
        self.var_file_selector = self.filenamebase_selector + 'var'
        self.sel_file_selector = self.filenamebase_selector + 'sel'
        self.cfg_file_selector = self.filenamebase_selector + 'cfg'
        self.ini_file_selector = self.filenamebase_selector + 'ini'
        self.arc_file_selector = self.filenamebase_selector + 'arc'
        self.sta_file_selector = self.filenamebase_selector + 'sta'

        # read and check common parameters (they should be equal)*/
        self.read_common_parameters(self.cfg_file_variator)
        self.read_common_parameters(self.cfg_file_selector)

        

    def read_monitor_parameters(self):

        correctlyReadParams = 0
        self.interaction = 0       # standard is no interaction
        self.plotting = 0          # standard is no plotting
        self.gauDirection = 'none' # standard is no direction for Gaussian
        self.updateVariatorSeed = 'random' # standard is random seed for variator
        try:
            f = open(self.parameter_file_monitor, 'r')
        
            for line in f:
                if 'seed' in line:
                    self.seed = int(line[5:len(line)])
                    correctlyReadParams += 1
                if 'numberOfRuns' in line:
                    self.numberOfRuns = int(line[13:len(line)])
                    correctlyReadParams += 1
                if 'numberOfGenerations' in line:
                    self.numberOfGenerations = int(line[20:len(line)])
                    correctlyReadParams += 1
                if 'outputType' in line:
                    outputType = line[11:len(line)-1]
                    correctlyReadParams += 1
                    if outputType.lower() == 'all':
                        self.outputType = 'ALL'
                    elif outputType.lower() == 'online':
                        self.outputType = 'ONLINE'
                    elif outputType.lower() == 'offline':
                        self.outputType = 'OFFLINE'
                    else:
                        print 'ERROR: outputType not correctly specified'
                        correctlyReadParams -= 1
                if 'outputSet' in line:
                    self.outputSet = int(line[10:len(line)])
                    correctlyReadParams += 1
                if 'debug' in line:
                    debug = line[6:len(line)].rstrip("\n")
                    if (debug == 'on') or (int(debug) == 1):
                        self.debugging = 1
                    else:
                        self.debugging = 0
                    correctlyReadParams += 1
                if 'interactive' in line:
                    interaction = line[12:len(line)].rstrip("\n")
                    if (interaction == 'off'):
                        self.interaction = 0
                    elif (interaction == 'on') or (int(interaction) == 1):
                        self.interaction = 1
                    correctlyReadParams += 1
                if 'plotPeriod' in line:
                    self.plottingPeriod = int(line[11:len(line)])
                    correctlyReadParams += 1
                if 'plotting' in line:
                    plotting = line[9:len(line)].rstrip("\n")
                    if (plotting == 'off'):
                        self.plotting = 0
                    elif (plotting == 'on') or (int(plotting) == 1):
                        self.plotting = 1
                    correctlyReadParams += 1
                if 'interactiontype' in line:
                    inttype = line[16:len(line)].rstrip("\n")
                    if (inttype == 'gau' or inttype == 'gaussian'):
                        self.interactiontype = 'gau'
                    elif (inttype == 'box') or (inttype == 'Box'):
                        self.interactiontype = 'box'
                    elif (inttype == 'prefstatements') or (inttype == 'PrefStatements'):
                        self.interactiontype = 'pref'
                    else:
                        self.interactiontype = 'none'
                    correctlyReadParams += 1
                if 'gauDirection' in line:
                    direction = line[13:len(line)].rstrip("\n")
                    if (direction == 'diag' or direction == 'diagonal'):
                        self.gauDirection = 'diag'
                    elif (direction == 'ideal') or (direction == 'Ideal'):
                        self.gauDirection = 'ideal'
                    elif (direction == 'nadir') or (direction == 'Nadir'):
                        self.gauDirection = 'nadir'
                    correctlyReadParams += 1
                if 'updateVariatorSeed' in line:
                    updateVarSeed = line[19:len(line)].rstrip("\n")
                    if (updateVarSeed == 'random' or updateVarSeed == 'rand'):
                        self.updateVariatorSeed = 'random'
                    elif (updateVarSeed == 'linearWithRuns') or (updateVarSeed == 'linear'):
                        self.updateVariatorSeed = 'linearWithRuns'
                    elif (updateVarSeed == 'constant') or (updateVarSeed == 'const'):
                        self.updateVariatorSeed = 'constant'
                    correctlyReadParams += 1
            f.close()
        except IOError:
            print 'Problem reading file ' + self.parameter_file_monitor
            
        if correctlyReadParams < 7:
            print '\nERROR: too few monitor parameters correctly read'
            exit()
        elif correctlyReadParams > 12:
            print '\nERROR: too many monitor parameters read'
            exit()
        if (self.plotting == 1) and (self.plottingPeriod < 0):
            print '\nERROR: plotting mode is \'on\' but plottingPeriod<0'
            exit()
        if (self.plotting == 0) and (self.interaction == 1):
            print '\nERROR: interaction mode is \'on\' but plotting mode is \'off\''
            exit()
        if (self.interactiontype == 'gau') and (self.gauDirection == 'none'):
            print '\nERROR: interaction type is Gaussian (\'gau\') but the direction (\'gauDirection\')is not set correctly'
            exit()
        
            


    def __init__(self,args):
        # standard values of variables first:
        self.alpha = 0
        self.mu = 0
        self.lam = 0
        self.dimension = 0
        self.debugging = 1  # 0=='off', 1=='on'
        self.filenamebase_monitor = 'logging'
        self.parameter_file_monitor = 'monitorParam.txt'
        self.filenamebase_selector = 'PISAselector_'
        self.parameter_file_selector = 'selParam.txt'
        self.filenamebase_variator = 'PISAvariator_'
        self.parameter_file_variator = 'varParam.txt'
        self.poll = 0.1
        self.seed = 1234
        self.read_selectorvariator_parameters(args)
        self.read_monitor_parameters()


        
    def printInformation(self):
        """ prints information of monitor parameters into file """
        
        self.log('write monitor information file\n')
        
        logfile_monitor = self.filenamebase_monitor + '.txt'

        try:
            f = open(logfile_monitor, 'w')
        except IOError:
            print 'Problem writing file ' + logfile_monitor
            
        # write command line of monitor
        f.write('start commandLine\n')
        f.write(  self.parameter_file_variator + ' ' + self.filenamebase_variator + ' ' + self.parameter_file_selector + ' ' + self.filenamebase_selector + ' ' + self.parameter_file_monitor + ' ' + self.filenamebase_monitor + ' ' + self.poll)
        f.write('\nend commandLine\n\n')

        # write monitor parameters
        f.write('start monParameter\n')
        try:
            f2 = open(self.parameter_file_monitor, 'r')
            for line in f2:
                f.write(line)
            f2.close()
        except IOError:
            print 'Problem reading file ' + self.parameter_file_monitor
            
        f.write('\nend monParameter\n\n')

        # write variator common parameters
        f.write('start varCommonParameter\n')
        try:
            f2 = open(self.cfg_file_variator, 'r')
            for line in f2:
                f.write(line)
            f2.close()
        except IOError:
            print 'Problem reading file ' + self.cfg_file_variator
            
        f.write('\nend varCommonParameter\n\n')
        
        # write variator parameters
        f.write('start varParameter\n')
        try:
            f2 = open(self.parameter_file_variator, 'r')
            for line in f2:
                f.write(line)
            f2.close()
        except IOError:
            print 'Problem reading file ' + self.parameter_file_variator
            
        f.write('\nend varParameter\n\n')

        # write selector common parameters
        f.write('start selCommonParameter\n')
        try:
            f2 = open(self.cfg_file_selector, 'r')
            for line in f2:
                f.write(line)
            f2.close()
        except IOError:
            print 'Problem reading file ' + self.cfg_file_selector
            
        f.write('\nend selCommonParameter\n\n')

        # write selector parameters
        f.write('start selParameter\n')
        try:
            f2 = open(self.parameter_file_selector, 'r')
            for line in f2:
                f.write(line)
            f2.close()
        except IOError:
            print 'Problem reading file ' + self.parameter_file_selector
            
        f.write('\nend selParameter')

        f.close()

    def log(self, message):
        """ prints 'message' on the screen if 'debugging' is on """
        if self.debugging == 1:
            print message
            sys.stdout.flush()


# end of class IntMonParameters


class IntMonState:
    """ class for reading and writing state from/to files """

    def write(self, filename, newstate):
        """ writes new state 'newstate' into file named 'filename' """
        
        assert (newstate >= 0) and (newstate <= 11)
        
        try:
            f = open(filename, 'w')            
            f.write(str(newstate))
            f.close()
        except IOError:
            print 'Problem writing file ' + filename
        
    
    def read(self, filename):
        """ reads state from file 'filename' and returns it """
        
        newstate = -1 # error state in case reading is not possible
        while (newstate < 0) or (newstate > 11):
            try:
                f = open(filename, 'r')
                line = f.read()
                # check whether file was not empty by accident because some
                # other process was currently writing it when we read it
                if not ( (line == '') or (line == ' ') ):
                    newstate = int(line)
                
                f.close()
            except IOError:
                print 'Problem reading file ' + filename
            
        assert (newstate >= 0) and (newstate <= 11)
                
        return newstate
    
    # end of read(self, filename)
    
    
# end of class IntMonState


class IntMonPopulation:
    """ provides storing of population as dictionary of <id, objArray> pairs """
    
    def __init__(self):
        self.currentPop = {}             # dictionary for current population
        self.currentPopNonDominated = {} # dictionary for nondominated solutions in current population
        self.all = {}                    # dictionary for all solutions ever seen
        self.allNonDominated = {}        # dictionary for all non-dominated solutions ever seen

    
    def addWithReplacement(self, id, solution, population):
        population[id] = solution
            
    def add(self, id, solution, population):
        # always generate new id as size of population + 1:
        newid = len(population) + 1
        population[newid] = solution
        
             
    def addNonDominated(self, id, solution, population):
        toDelete = {}
        for p_id, p in population.iteritems():
            if self.dominates(p, solution) == 1:
                return
            elif self.dominates(solution, p) == 1:
                toDelete[p_id] = population[p_id]
        # finally, delete all dominated solutions and add new solution
        for s_id, s in toDelete.iteritems():
            del population[s_id]
        self.addWithReplacement(id, solution, population)

    def addToCurrentPop(self, population):
        """ adds solutions, given in dictionary 'population' to current population.
        
            At the same time, also 'all' and 'allNonDominated' are updated.
            'population' itself should contain the objective values as lists stored
            with the solutions' id's as their keys.
        """
        for id, solution in population.iteritems():
            self.addWithReplacement(id, solution, self.currentPop)
            self.add(id, solution, self.all)
            self.addNonDominated(id, solution, self.allNonDominated)
            self.addNonDominated(id, solution, self.currentPopNonDominated)
        
    def getCurrentPop(self):
        return self.currentPop    
    
    def getCurrentPopNonDominated(self):
        return self.currentPopNonDominated
    
    def getAll(self):
        return self.all
    
    def getAllNonDominated(self):
        return self.allNonDominated

    def dominates(self, x, y):
        """ returns 1 if solution x dominates solution y and 0 otherwise.
        
            x and y are thereby lists of objective values and minimization is assumed
        """
        dom = 0
        for k in range(len(x)):
            if x[k] > y[k]:
                return 0
            elif x[k] < y[k]:
                dom = 1
        return dom
    
    def getObjectiveValuesOfCurrentPop(self):
        """ returns objective values of all 'self.currentPop' members as an
            array (solutions as rows with objective values as columns)
        """
        retPop = np.array(self.currentPop.values(), 'double')

        return retPop
        
    def getSpreadOfCurrentPop(self):
        """ returns the differences between maximum and minimum value in all
            objectives over the current population as a list of double values.
        """
        ov = np.array(self.currentPop.values(), 'double')
        
        return (np.amax(ov ,axis=0) - np.amin(ov ,axis=0))

    def moveIds(self, fromFile, toFile):
        """ copies ids from 'fromFile' to 'toFile' (including the first and
            last lines of 'fromFile' and returns the ids (i.e. without first
            and last lines of the file
        """
        ids = []
        # read ids from 'fromFile'
        try:
            file = open(fromFile, 'r')
            for line in file:
                ids.append(line)
            file.close()
        except IOError:
            print 'Problem reading file ' + fromFile
        
        # write all ids to 'toFile'
        try:
            file = open(toFile, 'w')
            for id in ids:
                # write line directly only if it does not contain 'END':
                if 'END' not in id:
                    file.write(id)
                else: # if EOF is contained, simply write 'END' back:
                    file.write('END')
            file.close()
        except IOError:
            print 'Problem writing file ' + toFile
        
        # delete first and last row before to return only the solution ids
        ids.pop()
        ids.reverse()
        ids.pop()
        
        return ids
    # end of moveIds(self, fromFile, tofile)


    def copyArchiveSelected(self, params):
        """ reads the ids of all individuals selected by the selector from
            params.sel_file_selector, writes them to params.sel_file_variator,
            reads the ids from params.arc_file_selector, updates the current
            population self.currentPop accordingly (i.e. deletes all unwanted
            solutions), and writes the ids from the arc file into
            params.sel_file_variator
            
        """
        
        self.moveIds(params.sel_file_selector, params.sel_file_variator)
        arcids = map(int, self.moveIds(params.arc_file_selector, params.arc_file_variator)) # make sure that read ids are integers
        assert(len(arcids) > 0) # we need to keep at least one individual
        
        # update current population and the set of stored nondominated
        # solutions as well
        toDelete = []
        for oldid in self.currentPop:
            if oldid not in arcids:
                toDelete.append(oldid)
        for id in toDelete:
            del self.currentPop[id]
        toDelete = []
        for oldid in self.currentPopNonDominated:
            if oldid not in arcids:
                toDelete.append(oldid)
        for id in toDelete:
            del self.currentPopNonDominated[id]
        
    # end of copyArchiveSelected(self, params)
            
    
    def copyAndGetPopulation(self, fromFile, toFile, params):
        """ copies individuals from 'fromFile' to 'toFile' (including first and
            last line of the file) and returns a dictionary 'newIndividuals'
            with the read solutions in the form of <id, objValues> pairs
            (excluding the first and last line of 'fromFile').

        """
        
        lines = deque() # to store file contents, use a queue
        
        # read fromFile and store in 'lines' for the moment
        try:
            file = open(fromFile, 'r')
            for line in file:
                lines.append(line)
            file.close()
        except IOError:
            print 'Problem reading file ' + fromFile
        
        # check whether file content complies with PISA specifications
        assert int(lines[0]) == ((params.dimension + 1) * params.alpha)
        assert 'END' in lines[len(lines)-1]
        
        # write everything to params.var_file_selector
        try:
            file = open(toFile, 'w')
            for line in lines:
                file.write(line)
            file.close()
        except IOError:
            print 'Problem writing file ' + toFile
        
        # add read individuals (except first and last line) to 'newIndividuals'
        newIndividuals = {}
        # delete lines that are not solutions
        lines.pop()
        lines.popleft()
        for line in lines:
            # split line correctly and use first value as id and the others as
            # objective values
            line = line.split()
            line = [float(x) for x in line]
            line.reverse()
            id = int(line.pop())
            line.reverse()
            objValues = line
            newIndividuals[id] = objValues
        
        return newIndividuals

    # end of copyAndGetPopulation(self, params):

    def outputAll(self, file):
        """ appends the selector's archive of the current generation to
            'file', i.e., the objective vectors of all individuals are written
        """
        for id, solution in self.currentPop.iteritems():
            for objValue in solution:
                file.write("%e " % objValue)
            file.write('\n')
        file.write('\n')
        
    def outputOnline(self, file):
        """ appends the non-dominated solutions within the selector's archive
            of the current generation to 'file'
        """
        for id, solution in self.currentPopNonDominated.iteritems():
            for objValue in solution:
                file.write("%e " % objValue)
            file.write('\n')
        file.write('\n')

    def appendOutput(self, params, generation, run):
        """ writes population to output file depending on params.outputType
            every params.outputSet generations
        """
        if ((params.outputSet == 0) and (generation == params.numberOfGenerations)) or ((params.outputSet !=0) and ((generation % params.outputSet) == 0)):
            outputfile = params.filenamebase_monitor + '.' + str(generation)
            
            try:
                if run == 1:
                    file = open(outputfile, 'w')
                else:
                    file = open(outputfile, 'a')
                
                if params.outputType == 'ALL':
                    params.log('    append ALL generation ' + str(generation) + '\n')
                    self.outputAll(file)
                elif params.outputType == 'ONLINE':
                    params.log('    append ONLINE generation ' + str(generation) + '\n')
                    self.outputOnline(file)
                elif params.outputType == 'OFFLINE':
                    params.log('    append OFFLINE generation ' + str(generation) + '\n')
                    self.outputOffline(file)

                file.close()
            except IOError:
                print 'Problem writing file ' + outputfile

    # end of appendOutput(self, params, generation)
    

    def do_state1(self, params):
        # get solutions from ini file as dictionary of <id, objArray> pairs
        # and copy everything to selector side
        newIndividuals = self.copyAndGetPopulation(params.ini_file_variator, params.ini_file_selector, params)
        # update population
        self.addToCurrentPop(newIndividuals)
        
    # end of do_state1(self, params)
        
    def do_state2(self, params, generation, run):    
        # read ids of individuals selected by the selector, update current
        # population accordingly and report selected solutions back to variator
        self.copyArchiveSelected(params)
        
        # output current population depending on params.outputType
        self.appendOutput(params, generation, run)


    # end of do_state2(self, params)

    def do_state3(self, params):
        # get solutions from variator's var file as dictionary of <id, objArray>
        # pairs and copy everything to selector side
        newIndividuals = self.copyAndGetPopulation(params.var_file_variator, params.var_file_selector, params)
        # update population
        self.addToCurrentPop(newIndividuals)
        
    # end of do_state3(self, params)
        
    # computes the local nadir point of the population the objective vectors
    # of which are given in the array f_current
    def computeLocalNadir(self, f_current):
        return np.amax(f_current, axis=0)
        
    
    # computes the local ideal point of the population the objective vectors
    # of which are given in the array f_current
    def computeLocalIdeal(self, f_current):
        return np.amin(f_current, axis=0)
    

# end of class IntMonPopulation


class intMonIO:
    """ class containing some useful input/output methods """
    
    @staticmethod
    def printToFile(stringList, filename):
        try:
            file = open(filename, 'w')
            for str in stringList:
                if str.endswith('\n'):
                    file.write(str)
                else:
                    file.write(str + '\n')
            file.close()
        except IOError:
            print 'Problem writing file ' + filename