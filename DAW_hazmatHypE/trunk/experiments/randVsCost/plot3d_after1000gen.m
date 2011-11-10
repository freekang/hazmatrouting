% plot resulting populations after 1000 generations

C = dlmread('results/graph_ns2_1_New_costInit.1000');
C_nondom = C(find(paretoFront(C(:,1:3))),:);
%plot3(C(:,1),C(:,2),C(:,3), 'x');
%hold all; grid on;
%plot3(C_nondom(:,1),C_nondom(:,2),C_nondom(:,3), 'o');


R = dlmread('results/graph_ns2_1_New_randInit.1000');
R_nondom = R(find(paretoFront(R(:,1:3))),:);
%figure()
%plot3(R(:,1),R(:,2),R(:,3), 'x');
%hold all; grid on;
%plot3(R_nondom(:,1),R_nondom(:,2),R_nondom(:,3), 'o');



%mysubplot(C,C_nondom, 'C', 'C_nondom', 'C only', 'filename');
%mysubplot(R,R_nondom, 'R', 'R_nondom', 'R only', 'filename');

mysubplot(C,R, 'shortest path initialization', 'random initialization', 'all solutions', 'allsolutions.eps')
mysubplot(C_nondom,R_nondom, 'shortest path initialization', 'random initialization', 'nondominated', 'onlynondominated.eps')

hold off;