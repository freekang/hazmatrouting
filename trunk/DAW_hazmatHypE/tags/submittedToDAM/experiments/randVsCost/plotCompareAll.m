% plots the resulting populations of lazio_2 and lazio_3

%%%%%%%%%%%%%%%%%%%%%%%
generation = 2000;
%%%%%%%%%%%%%%%%%%%%%%%

filename = ['results/graph_ns2_1_OriginalCosts_HypE_popsize50_randInit.' int2str(generation)];
Lazio2 = dlmread(filename);
Lazio2_nondom = Lazio2(find(paretofront(Lazio2(:,1:3))),:);
figure(88);
plot3(Lazio2_nondom(:,1),Lazio2_nondom(:,2),Lazio2_nondom(:,3), 'x');
hold all; grid on;

filename = ['results/graph_ns3_1_OriginalCosts_HypE_popsize50_randInit.' int2str(generation)];
Lazio3 = dlmread(filename);
Lazio3_nondom = Lazio3(find(paretofront(Lazio3(:,1:3))),:);
plot3(Lazio3_nondom(:,1),Lazio3_nondom(:,2),Lazio3_nondom(:,3), 'o');

filename = ['results/graph_ns4_1_OriginalCosts_HypE_popsize50_randInit.' int2str(generation)];
Lazio41 = dlmread(filename);
Lazio41_nondom = Lazio41(find(paretofront(Lazio41(:,1:3))),:);
plot3(Lazio41_nondom(:,1),Lazio41_nondom(:,2),Lazio41_nondom(:,3), 's');

filename = ['results/graph_ns4_2_OriginalCosts_HypE_popsize50_randInit.' int2str(generation)];
Lazio42 = dlmread(filename);
Lazio42_nondom = Lazio42(find(paretofront(Lazio42(:,1:3))),:);
plot3(Lazio42_nondom(:,1),Lazio42_nondom(:,2),Lazio42_nondom(:,3), 'd');

filename = ['results/graph_ns4_3_OriginalCosts_HypE_popsize50_randInit.' int2str(generation)];
Lazio43 = dlmread(filename);
Lazio43_nondom = Lazio43(find(paretofront(Lazio43(:,1:3))),:);
plot3(Lazio43_nondom(:,1),Lazio43_nondom(:,2),Lazio43_nondom(:,3), 'd');
hold off;


title = ['nondominated solutions after ' int2str(generation) ' generations'];
filename = ['compAll_' int2str(generation) 'gens.eps'];
mysubplot5(Lazio2_nondom, Lazio3_nondom, Lazio41_nondom, Lazio42_nondom, Lazio43_nondom, 'lazio\_2', 'lazio\_3', 'lazio\_4\_1', 'lazio\_4\_2', 'lazio\_4\_3', title, filename);

hold off;
