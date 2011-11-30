function [] = mysubplot5(A, B, C, D, E, A_label, B_label, C_label, D_label, E_label, titlename, filename)
figure()

subplot(2,2,1)
plot3(A(:,1),A(:,2),A(:,3), 's');
hold all; grid on;
plot3(B(:,1),B(:,2),B(:,3), 'o');
plot3(C(:,1),C(:,2),C(:,3), 'd');
plot3(D(:,1),D(:,2),D(:,3), 'x');
plot3(E(:,1),E(:,2),E(:,3), 'p');
xlabel('cost')
ylabel('total risk')
zlabel('equity risk')
view(15,30);
xlim([0 15000]);

subplot(2,2,2)
plot(A(:,1),A(:,2), 's');
hold all; grid on;
plot(B(:,1),B(:,2), 'o');
plot(C(:,1),C(:,2), 'd');
plot(D(:,1),D(:,2), 'x');
plot(E(:,1),E(:,2), 'p');
%ylim([4,6]*1e6);
h_legend = legend(A_label, B_label, C_label, D_label, E_label);
set(h_legend,'FontSize',6, 'Location', 'SouthEast');
xlabel('cost')
ylabel('total risk')


subplot(2,2,3)
plot(A(:,1),A(:,3), 's');
hold all; grid on;
plot(B(:,1),B(:,3), 'o');
plot(C(:,1),C(:,3), 'd');
plot(D(:,1),D(:,3), 'x');
plot(E(:,1),E(:,3), 'p');
%ylim([2,5]*1e5);
%h_legend = legend(A_label, B_label, C_label, D_label, E_label);
%set(h_legend,'FontSize',7);
xlabel('cost')
ylabel('equity risk')


subplot(2,2,4)
plot(A(:,2),A(:,3), 's');
hold all; grid on;
plot(B(:,2),B(:,3), 'o');
plot(C(:,2),C(:,3), 'd');
plot(D(:,2),D(:,3), 'x', 'MarkerSize', 12);
plot(E(:,2),E(:,3), 'p');
xlim([0,5]*1e7);
%h_legend = legend(A_label, B_label, C_label, D_label, E_label);
%set(h_legend,'FontSize',7);
xlabel('total risk')
ylabel('equity risk')

% save plot as eps:
exportfig(gcf, filename);

% add title above all for plot on screen:
set(gcf,'NextPlot','add');
axes;
h = title(titlename);
set(gca,'Visible','off');
set(h,'Visible','on'); 


end
