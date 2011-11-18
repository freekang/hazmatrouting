% plot resulting populations of rand and cost initialization
% after certain number of generations

%%%%%%%%%%%%%%%%%%%%%%%
generation = 2000;
numOfCommodities = 2;
instance = 2;
%%%%%%%%%%%%%%%%%%%%%%%

filename = ['results/graph_ns' int2str(numOfCommodities) '_' int2str(instance) '_New_costInit.' int2str(generation)];
C = dlmread(filename);
C_nondom = C(find(paretofront(C(:,1:3))),:);
figure(77);
%plot3(C(:,1),C(:,2),C(:,3), 'x');
plot3(C_nondom(:,1),C_nondom(:,2),C_nondom(:,3), 'o');
hold all; grid on;

filename = ['results/graph_ns' int2str(numOfCommodities) '_' int2str(instance) '_New_randInit.' int2str(generation)];
R = dlmread(filename);
R_nondom = R(find(paretofront(R(:,1:3))),:);
%figure()
%plot3(R(:,1),R(:,2),R(:,3), 'x');
%hold all; grid on;
plot3(R_nondom(:,1),R_nondom(:,2),R_nondom(:,3), 'o');
hold off;


%mysubplot(C,C_nondom, 'C', 'C_nondom', 'C only', 'filename');
%mysubplot(R,R_nondom, 'R', 'R_nondom', 'R only', 'filename');

title = ['all solutions after ' int2str(generation) ' generations'];
mysubplot(C,R, 'shortest path initialization', 'random initialization', title, 'allsolutions.eps');
title = ['nondominated solutions after ' int2str(generation) ' generations'];
mysubplot(C_nondom,R_nondom, 'shortest path initialization', 'random initialization', title, 'onlynondominated.eps');

hold off;