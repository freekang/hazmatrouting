% plot resulting populations after 1000 generations

C = dlmread('results/graph_ns2_1_New_costInit.1000');
R = dlmread('results/graph_ns2_1_New_randInit.1000');

subplot(2,2,1)
plot3(C(:,1),C(:,2),C(:,3), 'x');
hold all; grid on;
plot3(R(:,1),R(:,2),R(:,3), 'o');
xlabel('cost')
ylabel('total risk')
zlabel('equity risk')


subplot(2,2,2)
plot(C(:,1),C(:,2), 'x');
hold all; grid on;
plot(R(:,1),R(:,2), 'o');
legend('shortest path initialization', 'random initialization');
xlabel('cost')
ylabel('total risk')


subplot(2,2,3)
plot(C(:,1),C(:,3), 'x');
hold all; grid on;
plot(R(:,1),R(:,3), 'o');
legend('shortest path initialization', 'random initialization');
xlabel('cost')
ylabel('equity risk')


subplot(2,2,4)
plot(C(:,2),C(:,3), 'x');
hold all; grid on;
plot(R(:,2),R(:,3), 'o');
legend('shortest path initialization', 'random initialization');
xlabel('total risk')
ylabel('equity risk')