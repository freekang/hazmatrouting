% plot resulting populations of rand and cost initialization
% after certain number of generations

%%%%%%%%%%%%%%%%%%%%%%%
generation = 2000;
%%%%%%%%%%%%%%%%%%%%%%%

% first ns4_1_New:
%%%%%%%%%%%%%%%%%%
filename = ['results/graph_ns4_1_New_HypE_popsize50_costInit.' int2str(generation)];
C = dlmread(filename);
C_nondom = C(find(paretofront(C(:,1:3))),:);
figure(77);
plot3(C_nondom(:,1),C_nondom(:,2),C_nondom(:,3), 'o');
hold all; grid on;

filename = ['results/graph_ns4_1_New_HypE_popsize50_randInit.' int2str(generation)];
R = dlmread(filename);
R_nondom = R(find(paretofront(R(:,1:3))),:);
plot3(R_nondom(:,1),R_nondom(:,2),R_nondom(:,3), 'o');
hold off;

title = ['nondominated solutions after ' int2str(generation) ' generations'];
filename = ['randVsCost_ns4_1_New_' int2str(generation) 'gens.eps'];
mysubplotRandVsCost(R_nondom, C_nondom, 'random', 'shortest path', title, filename);

hold off;


% now ns4_1_OriginalCost:
%%%%%%%%%%%%%%%%%%%%%%%%%%
filename = ['results/graph_ns4_1_OriginalCosts_HypE_popsize50_costInit.' int2str(generation)];
C = dlmread(filename);
C_nondom = C(find(paretofront(C(:,1:3))),:);
figure(78);
plot3(C_nondom(:,1),C_nondom(:,2),C_nondom(:,3), 'o');
hold all; grid on;

filename = ['results/graph_ns4_1_OriginalCosts_HypE_popsize50_randInit.' int2str(generation)];
R = dlmread(filename);
R_nondom = R(find(paretofront(R(:,1:3))),:);
plot3(R_nondom(:,1),R_nondom(:,2),R_nondom(:,3), 'o');
hold off;

title = ['nondominated solutions after ' int2str(generation) ' generations'];
filename = ['randVsCost_ns4_1_OriginalCost_' int2str(generation) 'gens.eps'];
mysubplotRandVsCost(R_nondom, C_nondom, 'random', 'shortest path', title, filename);

hold off;
