# Makefile for hype

# Compiler
CC = gcc

# Compiler options
CFLAGS = -g -Wall -O3

# all object files
SEL_OBJECTS = hype_functions.o hype_io.o hype.o

hype : $(SEL_OBJECTS)
	$(CC) $(CFLAGS) -lm $(SEL_OBJECTS) -o hype

hype_functions.o : hype_functions.c hype.h
	$(CC) $(CFLAGS) -c hype_functions.c 

hype_io.o : hype_io.c hype.h
	$(CC) $(CFLAGS) -c hype_io.c

hype.o : hype.c hype.h
	$(CC) $(CFLAGS) -c hype.c

clean:
	rm -f *~ *.o

