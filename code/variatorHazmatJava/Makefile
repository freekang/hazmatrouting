# 
# Makefile for ATSP and ABACUS 3.0
#

#######################################################################
#
# Please adapt the following settings, if necessary.
#
#######################################################################

# Define the C++ compiler you are using:
#
#Variable globale
CXX = g++

#Variable CPLEX
# CPLEX_LD_FLAGS = -lcplex -lpthread
# CPLEX_INCLUDE  = -I /opt/lipn/software/cplex102/include/ilcplex
# CPLEX_LIB      = -L /opt/lipn/software/cplex102/lib/x86_rhel4.0_3.4/static_pic/

##############

PROGRAM 			:= hazmat
TARGET_DIR			:= ./bin
SOURCE_DIR			:= ./src
OUTPUT_DIR			:= ./obj
INCL_DIR			:= ./include

CXXFLAGS =	-O2 -g -Wall -fmessage-length=0

TARGET				:= $(TARGET_DIR)/$(PROGRAM)
SOURCES				:= $(wildcard $(SOURCE_DIR)/*.cpp)
OBJECTS				:= $(patsubst $(SOURCE_DIR)/%.cpp,$(OUTPUT_DIR)/%.o,$(SOURCES))

$(TARGET): $(OBJECTS) 
	$(CXX) -o $(TARGET) $(OBJECTS) 

clean:
	-rm -f $(TARGET) $(OBJECTS)

$(OUTPUT_DIR)/%.o: $(SOURCE_DIR)/%.cpp 
	@ echo output for $<
	$(CXX) -I$(INCL_DIR) -c $< -o $@

all:	$(TARGET)