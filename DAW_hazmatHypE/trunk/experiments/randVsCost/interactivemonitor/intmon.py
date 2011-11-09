"""Interactive PISA monitor.

    Calls various selector and variator modules to perform PISA algorithm runs.
    Is going to provide interactive optimization features such as plotting.
    
    Usage: python intmon.py PV CV PS CS PM OM poll
    
    where       PV: parameter file of variator
                CV: communication filename base of variator
                PS: paramameter file of selector
                CS: communication filename base of selector
                PM: parameter file of monitor 
                OM: filename base of monitor output files
                poll: polling interval

    """


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
#  Main file
#  
#  python implementation
#  
#  file: intmon.py
#  author: Dimo Brockhoff brockho@lix.polytechnique.fr
#
#  last change: April 2011
#  
#  ========================================================================



import sys
import getopt
from intmon_internal import *
import random
import time
from collections import deque
import interaction


currentGeneration = 0
currentRun = 0




def wait(timeInSeconds):
    assert(float(timeInSeconds) >= 0.001)
    time.sleep(float(timeInSeconds))
    
# end of wait(timeInSeconds)    
    
    
def writeNewSeed(filename, updateType, reset):
    """ replaces old seed in file 'filename' by a new seed iff a line
        beginning with 'seed' exists within the file 'filename'.
        The seed is chosen either randomly (updateType == 'random'), linearly
        with the number of the current run (updateType == 'linearlyWithRuns'),
        or not changed at all (updateType == 'const').
        If reset==1 and updateType == 'linearlyWithRuns', the seed is reset
        by writing the seed '10001' such that the seed is always '10000+#runs'.
    """
    
    lines = deque() # to store file contents, use a queue
    
    # first read file fully
    try:
        file = open(filename, 'r')
        for line in file:
            lines.append(line)
        file.close()
    except IOError:
        print 'Problem reading file ' + filename

    # now write file back with new seed
    try:
        file = open(filename, 'w')
        for line in lines:
            if 'seed' in line:
                newseed = 0
                oldseed = int(line[5:len(line)])
                # generate new seed (either at random or linearly with run number)
                if updateType == 'linearWithRuns':
                    if reset == 1:
                        newseed = 10001
                    else:
                        newseed = oldseed + 1
                elif updateType == 'constant':
                    newseed = oldseed
                else:
                    newseed = random.randint(1, sys.maxint)
                file.write('seed ' + str(newseed) + '\n')
            else:
                file.write(line)
        file.close()
    except IOError:
        print 'Problem writing file ' + filename
        
# end of writeNewSeed(filename)
    
def resetVariatorSeed(parameters):
    """ writes new seed for variator (either randomly generated or =10001)
        into parameter file parameters.parameter_file_variator
        if a line starting with 'seed' exists in the file
        
    """
    
    parameters.log('    reset variator seed')
    
    writeNewSeed(parameters.parameter_file_variator, parameters.updateVariatorSeed, 1)

# end of resetVariatorSeed()
    
def updateVariatorSeed(parameters):
    """ writes new (randomly generated) seed for variator
        into parameter file parameters.parameter_file_variator
        if a line starting with 'seed' exists in the file
        
    """
    
    parameters.log('    update variator seed')
    
    writeNewSeed(parameters.parameter_file_variator, parameters.updateVariatorSeed, 0)

# end of updateVariatorSeed()
    
    
def updateSelectorSeed(parameters):
    """ writes new (randomly generated) seed for selector
        into parameter file parameters.parameter_file_selector
        if a line starting with 'seed' exists in the file
        
    """
    
    parameters.log('    update selector seed')
    
    writeNewSeed(parameters.parameter_file_selector, 'random', 0)

# end of updateSelectorSeed()
    
    
def resetAll(state, params, run):
    """ resets both variator and selector and update random seeds """
    params.log('starting reset\n')
    currentGeneration = 0
    state.write(params.sta_file_selector, 10)
    state.write(params.sta_file_variator, 8)
    params.log('    variator and selector in states 8 and 10 respectively\n')
    pop = IntMonPopulation() # create empty population

    while 1:
        varState = state.read(params.sta_file_variator)
        selState = state.read(params.sta_file_selector)
        
        if varState != 9:
            state.write(params.sta_file_variator, 8)
        if selState != 11:
            state.write(params.sta_file_selector, 10)
        if (varState == 9) & (selState == 11):
             # allow for linear update of variator seed with a fixed start seed:
            if run == 1:
                resetVariatorSeed(params)
            else:
                updateVariatorSeed(params)
            updateSelectorSeed(params)
            state.write(params.ini_file_variator, 0) # make ini file ready for writing
            state.write(params.sta_file_variator, 0) # start initialization of variator
            params.log('    variator in state 0\n')
            params.log('  currentGeneration = ' + str(currentGeneration) + '\n')
            break
        wait(params.poll);

    while 1:
        if state.read(params.sta_file_variator) == 1:
            params.log('    variator in state 1\n')
            pop.do_state1(params)
            state.write(params.arc_file_selector, 0) # make arc file ready for write
            state.write(params.sel_file_selector, 0) # make sel file ready for write
            state.write(params.sta_file_selector, 1)
            params.log('    copying of ini_pop done; selector in state 1\n')
            break
        wait(params.poll);

    while 1:
        if state.read(params.sta_file_selector) == 2:
            params.log('    selector in state 2\n')
            pop.do_state2(params, 0, run)
            state.write(params.var_file_variator, 0) # make var file ready for write
            state.write(params.sta_file_variator, 2)
            params.log('    selection (copying of arc_pop and sel_pop) done; variator in state 2\n')
            break
        wait(params.poll);
        
    return pop

# end of resetAll()

def main():
    
    # parse command line options
    try:
        opts, args = getopt.getopt(sys.argv[1:], "h", ["help"])
    except getopt.error, msg:
        print msg
        print "for help use --help"
        sys.exit(2)
    # process options
    for o, a in opts:
        if o in ("-h", "--help"):
            print __doc__
            sys.exit(0)
    # process arguments
    if len(args) != 7:
        sys.exit('wrong usage, use --help option to see details')
    else:
        params = IntMonParameters(args)

    state = IntMonState()

    random.seed(params.seed)        # seed random generator
    params.printInformation()  # print information file of monitor
    
        
    # start loop
    
    for currentRun in range(1,params.numberOfRuns+1):
        params.log('currentRun = ' + str(currentRun) + '\n')
        
        pop = resetAll(state, params, currentRun)
                
        for currentGeneration in range(1,params.numberOfGenerations+1):
            params.log('  currentGeneration = ' + str(currentGeneration) + '\n')

            while 1:
                if state.read(params.sta_file_variator) == 3:
                    pop.do_state3(params)
                    
                    # if everything went fine, make arc and sel files ready for
                    # writing again before to go to next state
                    state.write(params.arc_file_selector, 0)
                    state.write(params.sel_file_selector, 0)
        
                    state.write(params.sta_file_selector, 3)
                    params.log('    variation done; selector in state 3\n')
                    break
                wait(params.poll)
            
            while 1:
                if state.read(params.sta_file_selector) == 2:
                    pop.do_state2(params, currentGeneration, currentRun)
                    
                    # plotting (incl. possible interaction every 'params.plottingPeriod' iterations
                    if (params.plotting == 1) and (currentGeneration > 0) and ((currentGeneration % params.plottingPeriod) == 0):
                        interaction.do_interactionStep(pop, params, state, currentGeneration, currentRun)

                    # if everything went fine make var file ready for writing
                    # again before to go to next state
                    state.write(params.var_file_variator, 0)
                    
                    state.write(params.sta_file_variator, 2)
                    params.log('    selection done; variator in state 2\n')
                    break
                wait(params.poll)

        # current run correcly finished
    # all runs finished
        
    params.log('  selector in state 6 (kill)\n')
    state.write(params.sta_file_selector, 6);
    while 1:
        if state.read(params.sta_file_selector) == 7:
            params.log('  selector killed\n')
            break
        wait(params.poll)

    while 1:
        if state.read(params.sta_file_variator) == 3:
            params.log('  variator in state 4 (kill)\n')
            state.write(params.sta_file_variator, 4);
            break;
        wait(params.poll)

    while 1:
        if state.read(params.sta_file_variator) == 5:
            params.log('  variator killed\n')
            break;
        wait(params.poll)

    interaction.finalPlot()

    params.log('killed myself\n')
    
# end of main()


if __name__ == "__main__":
    main()





    


    
    
 